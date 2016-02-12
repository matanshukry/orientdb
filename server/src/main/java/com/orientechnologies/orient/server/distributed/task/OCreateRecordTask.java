/*
 *
 *  *  Copyright 2014 Orient Technologies LTD (info(at)orientechnologies.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientechnologies.com
 *
 */
package com.orientechnologies.orient.server.distributed.task;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.command.OCommandDistributedReplicateRequest;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OPlaceholder;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ODocumentInternal;
import com.orientechnologies.orient.core.storage.ORawBuffer;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.distributed.ODistributedConfiguration;
import com.orientechnologies.orient.server.distributed.ODistributedRequest;
import com.orientechnologies.orient.server.distributed.ODistributedServerLog;
import com.orientechnologies.orient.server.distributed.ODistributedServerLog.DIRECTION;
import com.orientechnologies.orient.server.distributed.ODistributedServerManager;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Distributed create record task used for synchronization.
 *
 * @author Luca Garulli (l.garulli--at--orientechnologies.com)
 *
 */
public class OCreateRecordTask extends OAbstractRecordReplicatedTask {
  public static final String SUFFIX_QUEUE_NAME = ".insert";
  private static final long  serialVersionUID  = 1L;
  protected byte[]           content;
  protected byte             recordType;
  protected int              clusterId         = -1;
  private transient ORecord  record;

  public OCreateRecordTask() {
  }

  public OCreateRecordTask(final ORecordId iRid, final byte[] iContent, final int iVersion, final byte iRecordType) {
    super(iRid, iVersion);
    content = iContent;
    recordType = iRecordType;
  }

  public OCreateRecordTask(final ORecord record) {
    this((ORecordId) record.getIdentity(), record.toStream(), record.getVersion(), ORecordInternal.getRecordType(record));

    if (rid.getClusterId() == ORID.CLUSTER_ID_INVALID) {
      final OClass clazz;
      if (record instanceof ODocument && (clazz = ODocumentInternal.getImmutableSchemaClass((ODocument) record)) != null) {
        // PRE-ASSIGN THE CLUSTER ID ON CALLER NODE
        clusterId = clazz.getClusterSelection().getCluster(clazz, (ODocument) record);
      } else {
        ODatabaseDocumentInternal db = ODatabaseRecordThreadLocal.INSTANCE.get();
        clusterId = db.getDefaultClusterId();
      }
    }
  }

  @Override
  public ORecord getRecord() {
    if (record == null) {
      record = Orient.instance().getRecordFactoryManager().newInstance(recordType);
      ORecordInternal.fill(record, rid, version, content, true);
    }
    return record;
  }

  public void resetRecord() {
    record = null;
  }

  @Override
  void setLockRecord(boolean lockRecord) {
  }

  @Override
  public Object execute(final OServer iServer, final ODistributedServerManager iManager, final ODatabaseDocumentTx database)
      throws Exception {
    ODistributedServerLog.debug(this, iManager.getLocalNodeName(), getNodeSource(), DIRECTION.IN, "creating record %s/%s v.%d...",
        database.getName(), rid.toString(), version);

    getRecord();

    final long clusterPosToReach = rid.getClusterPosition();
    if (ORecordId.isPersistent(clusterPosToReach)) {
      // THIS IS A FIX TO FILL THE MISSING HOLES
      ORecordInternal.setIdentity(record, rid.getClusterId(), ORID.CLUSTER_POS_INVALID);
    }

    while (true) {
      if (clusterId > -1)
        record.save(database.getClusterNameById(clusterId), true);
      else if (rid.getClusterId() != -1)
        record.save(database.getClusterNameById(rid.getClusterId()), true);
      else
        record.save();

      rid = (ORecordId) record.getIdentity();

      if (clusterPosToReach > 0 && rid.clusterPosition < clusterPosToReach) {
        // DELETE THE HOLE
        rid.getRecord().delete();
      } else
        break;
    }

    ODistributedServerLog.debug(this, iManager.getLocalNodeName(), getNodeSource(), DIRECTION.IN, "+-> assigned new rid %s/%s v.%d",
        database.getName(), rid.toString(), record.getVersion());

    // TODO: IMPROVE TRANSPORT BY AVOIDING THE RECORD CONTENT, BUT JUST RID + VERSION
    return new OPlaceholder(record);
  }

  @Override
  public OCommandDistributedReplicateRequest.QUORUM_TYPE getQuorumType() {
    return OCommandDistributedReplicateRequest.QUORUM_TYPE.WRITE;
  }

  @Override
  public List<OAbstractRemoteTask> getFixTask(final ODistributedRequest iRequest, OAbstractRemoteTask iOriginalTask,
      final Object iBadResponse, final Object iGoodResponse, final String executorNode, final ODistributedServerManager dManager) {
    if (iBadResponse instanceof Throwable)
      return null;

    final OPlaceholder badResult = (OPlaceholder) iBadResponse;
    final OPlaceholder goodResult = (OPlaceholder) iGoodResponse;

    final List<OAbstractRemoteTask> result = new ArrayList<OAbstractRemoteTask>(2);

    if (!badResult.equals(goodResult)) {
      // CREATE RECORD FAILED TO HAVE THE SAME RIDS. FORCE REALIGNING OF DATA CLUSTERS
      if (badResult.getIdentity().getClusterId() == goodResult.getIdentity().getClusterId()
          && badResult.getIdentity().getClusterPosition() < goodResult.getIdentity().getClusterPosition()) {

        final long minPos = Math.max(badResult.getIdentity().getClusterPosition() - 1, 0);
        for (long pos = minPos; pos < goodResult.getIdentity().getClusterPosition(); ++pos) {
          // UPDATE INTERMEDIATE RECORDS
          final ORecordId toUpdateRid = new ORecordId(goodResult.getIdentity().getClusterId(), pos);

          final ORecord toUpdateRecord;
          if (dManager.getLocalNodeName().equals(executorNode)) {
            // SAME SERVER: LOAD THE RECORD FROM ANOTHER NODE
            final ODistributedConfiguration dCfg = dManager.getDatabaseConfiguration(iRequest.getDatabaseName());
            final List<String> nodes = dCfg.getServers(ODatabaseRecordThreadLocal.INSTANCE.get().getClusterNameById(clusterId),
                dManager.getLocalNodeName());

            final ORawBuffer remoteReadRecord = (ORawBuffer) dManager.sendRequest(iRequest.getDatabaseName(), null, nodes,
                new OReadRecordTask(toUpdateRid), ODistributedRequest.EXECUTION_MODE.RESPONSE);

            if (remoteReadRecord != null) {
              toUpdateRecord = Orient.instance().getRecordFactoryManager().newInstance(recordType);
              ORecordInternal.fill(toUpdateRecord, toUpdateRid, remoteReadRecord.version, remoteReadRecord.buffer, false);
            } else
              toUpdateRecord = null;
          } else
            // LOAD IT LOCALLY
            toUpdateRecord = toUpdateRid.getRecord();

          if (toUpdateRecord != null)
            result.add(new OUpdateRecordTask(toUpdateRid, toUpdateRecord.toStream(), toUpdateRecord.getVersion(),
                toUpdateRecord.toStream(), toUpdateRecord.getVersion(), ORecordInternal.getRecordType(toUpdateRecord)));
        }

        // CREATE LAST RECORD
        result.add(new OCreateRecordTask((ORecordId) goodResult.getIdentity(), content, version, recordType));

      } else if (badResult.getIdentity().getClusterId() == goodResult.getIdentity().getClusterId()
          && badResult.getIdentity().getClusterPosition() > goodResult.getIdentity().getClusterPosition()) {

      } else
        // ANY OTHER CASE JUST DELETE IT
        result.add(new ODeleteRecordTask(new ORecordId(badResult.getIdentity()), badResult.getVersion()).setDelayed(false));
    }

    return result;
  }

  @Override
  public ODeleteRecordTask getUndoTask(final ODistributedRequest iRequest, final Object iBadResponse) {
    if (iBadResponse instanceof Throwable)
      return null;

    final OPlaceholder badResult = (OPlaceholder) iBadResponse;
    return new ODeleteRecordTask(new ORecordId(badResult.getIdentity()), badResult.getVersion()).setDelayed(false);
  }

  @Override
  public void writeExternal(final ObjectOutput out) throws IOException {
    super.writeExternal(out);
    if (content == null)
      out.writeInt(0);
    else {
      out.writeInt(content.length);
      out.write(content);
    }
    out.write(recordType);
    out.writeInt(clusterId);
  }

  @Override
  public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    final int contentSize = in.readInt();
    if (contentSize == 0)
      content = null;
    else {
      content = new byte[contentSize];
      in.readFully(content);
    }
    recordType = in.readByte();
    clusterId = in.readInt();
  }

  // @Override
  // public String getQueueName(final String iOriginalQueueName) {
  // return iOriginalQueueName + SUFFIX_QUEUE_NAME;
  // }

  @Override
  public String getName() {
    return "record_create";
  }
}

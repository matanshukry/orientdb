/* Generated By:JJTree: Do not edit this line. OOptimizeDatabaseStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OInternalResultSet;
import com.orientechnologies.orient.core.sql.executor.OResultInternal;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OOptimizeDatabaseStatement extends OSimpleExecStatement {

  protected List<OCommandLineOption> options = new ArrayList<OCommandLineOption>();
  private int batch = 1000;

  public OOptimizeDatabaseStatement(int id) {
    super(id);
  }

  public OOptimizeDatabaseStatement(OrientSql p, int id) {
    super(p, id);
  }

  @Override
  public OResultSet executeSimple(OCommandContext ctx) {
    OResultInternal result = new OResultInternal();
    result.setProperty("operation", "optimize databae");

    if (isOptimizeEdges()) {
      String edges = optimizeEdges();
      result.setProperty("optimizeEdges", edges);
    }

    OInternalResultSet rs = new OInternalResultSet();
    rs.add(result);
    return rs;
  }

  @Override
  public void toString(Map<Object, Object> params, StringBuilder builder) {
    builder.append("OPTIMIZE DATABASE");
    for (OCommandLineOption option : options) {
      builder.append(" ");
      option.toString(params, builder);
    }
  }

  @Override
  public OOptimizeDatabaseStatement copy() {
    OOptimizeDatabaseStatement result = new OOptimizeDatabaseStatement(-1);
    result.options =
        options == null
            ? null
            : options.stream().map(OCommandLineOption::copy).collect(Collectors.toList());
    return result;
  }

  private String optimizeEdges() {
    final ODatabaseDocumentInternal db = getDatabase();

    db.declareIntent(new OIntentMassiveInsert());
    try {
      long transformed = 0;
      if (db.getTransaction().isActive()) db.commit();

      db.begin();

      try {

        final long totalEdges = db.countClass("E");
        long browsedEdges = 0;
        long lastLapBrowsed = 0;
        long lastLapTime = System.currentTimeMillis();

        for (ODocument doc : db.browseClass("E")) {
          if (Thread.currentThread().isInterrupted()) break;

          browsedEdges++;

          if (doc != null) {
            if (doc.fields() == 2) {
              final ORID edgeIdentity = doc.getIdentity();

              final ODocument outV = doc.field("out");
              final ODocument inV = doc.field("in");

              // OUTGOING
              final Object outField = outV.field("out_" + doc.getClassName());
              if (outField instanceof ORidBag) {
                final Iterator<OIdentifiable> it = ((ORidBag) outField).iterator();
                while (it.hasNext()) {
                  OIdentifiable v = it.next();
                  if (edgeIdentity.equals(v)) {
                    // REPLACE EDGE RID WITH IN-VERTEX RID
                    it.remove();
                    ((ORidBag) outField).add(inV.getIdentity());
                    break;
                  }
                }
              }

              outV.save();

              // INCOMING
              final Object inField = inV.field("in_" + doc.getClassName());
              if (outField instanceof ORidBag) {
                final Iterator<OIdentifiable> it = ((ORidBag) inField).iterator();
                while (it.hasNext()) {
                  OIdentifiable v = it.next();
                  if (edgeIdentity.equals(v)) {
                    // REPLACE EDGE RID WITH IN-VERTEX RID
                    it.remove();
                    ((ORidBag) inField).add(outV.getIdentity());
                    break;
                  }
                }
              }

              inV.save();

              doc.delete();

              if (++transformed % batch == 0) {
                db.commit();
                db.begin();
              }

              final long now = System.currentTimeMillis();

              if (verbose() && (now - lastLapTime > 2000)) {
                final long elapsed = now - lastLapTime;

                OLogManager.instance()
                    .info(
                        this,
                        "Browsed %,d of %,d edges, transformed %,d so far (%,d edges/sec)",
                        browsedEdges,
                        totalEdges,
                        transformed,
                        (((browsedEdges - lastLapBrowsed) * 1000 / elapsed)));

                lastLapTime = System.currentTimeMillis();
                lastLapBrowsed = browsedEdges;
              }
            }
          }
        }

        // LAST COMMIT
        db.commit();

      } finally {
        if (db.getTransaction().isActive()) db.rollback();
      }
      return "Transformed " + transformed + " regular edges in lightweight edges";

    } finally {
      db.declareIntent(null);
    }
  }

  private boolean isOptimizeEdges() {
    for (OCommandLineOption option : options) {
      if (option.name.getStringValue().equalsIgnoreCase("LWEDGES")) {
        return true;
      }
    }
    return false;
  }

  private boolean verbose() {
    for (OCommandLineOption option : options) {
      if (option.name.getStringValue().equalsIgnoreCase("NOVERBOSE")) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OOptimizeDatabaseStatement that = (OOptimizeDatabaseStatement) o;

    if (options != null ? !options.equals(that.options) : that.options != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return options != null ? options.hashCode() : 0;
  }
}
/* JavaCC - OriginalChecksum=b85d66f84bbae92224565361df9d0c91 (do not edit this line) */

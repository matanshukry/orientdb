/* Generated By:JJTree: Do not edit this line. OMoveVertexStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.sql.executor.OMoveVertexExecutionPlanner;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.sql.executor.OUpdateExecutionPlan;
import java.util.HashMap;
import java.util.Map;

public class OMoveVertexStatement extends OStatement {
  protected OFromItem source;
  protected OCluster targetCluster;
  protected OIdentifier targetClass;
  protected OUpdateOperations updateOperations;
  protected OBatch batch;

  public OMoveVertexStatement(int id) {
    super(id);
  }

  public OMoveVertexStatement(OrientSql p, int id) {
    super(p, id);
  }

  @Override
  public OResultSet execute(
      ODatabase db, Object[] args, OCommandContext parentCtx, boolean usePlanCache) {
    Map<Object, Object> params = new HashMap<>();
    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        params.put(i, args[i]);
      }
    }
    return execute(db, params, parentCtx, usePlanCache);
  }

  @Override
  public OResultSet execute(
      ODatabase db, Map params, OCommandContext parentCtx, boolean usePlanCache) {
    OBasicCommandContext ctx = new OBasicCommandContext();
    if (parentCtx != null) {
      ctx.setParentWithoutOverridingChild(parentCtx);
    }
    ctx.setDatabase(db);
    ctx.setInputParameters(params);
    OUpdateExecutionPlan executionPlan;
    if (usePlanCache) {
      executionPlan = createExecutionPlan(ctx, false);
    } else {
      executionPlan = (OUpdateExecutionPlan) createExecutionPlanNoCache(ctx, false);
    }
    executionPlan.executeInternal();
    return new OLocalResultSet(executionPlan);
  }

  public OUpdateExecutionPlan createExecutionPlan(OCommandContext ctx, boolean enableProfiling) {
    OMoveVertexExecutionPlanner planner = new OMoveVertexExecutionPlanner(this);
    return planner.createExecutionPlan(ctx, enableProfiling);
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {
    builder.append("MOVE VERTEX ");
    source.toString(params, builder);
    builder.append(" TO ");
    if (targetCluster != null) {
      targetCluster.toString(params, builder);
    } else {
      builder.append("CLASS:");
      targetClass.toString(params, builder);
    }

    if (updateOperations != null) {
      builder.append(" ");
      updateOperations.toString(params, builder);
    }

    if (batch != null) {
      builder.append(" ");
      batch.toString(params, builder);
    }
  }

  @Override
  public OMoveVertexStatement copy() {
    OMoveVertexStatement result = new OMoveVertexStatement(-1);
    result.source = source.copy();
    result.targetClass = targetClass == null ? null : targetClass.copy();
    result.targetCluster = targetCluster == null ? null : targetCluster.copy();
    result.updateOperations = updateOperations == null ? null : updateOperations.copy();
    result.batch = batch == null ? null : batch.copy();
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OMoveVertexStatement that = (OMoveVertexStatement) o;

    if (!source.equals(that.source)) return false;
    if (targetCluster != null
        ? !targetCluster.equals(that.targetCluster)
        : that.targetCluster != null) return false;
    if (targetClass != null ? !targetClass.equals(that.targetClass) : that.targetClass != null)
      return false;
    if (updateOperations != null
        ? !updateOperations.equals(that.updateOperations)
        : that.updateOperations != null) return false;
    return batch != null ? batch.equals(that.batch) : that.batch == null;
  }

  @Override
  public int hashCode() {
    int result = source.hashCode();
    result = 31 * result + (targetCluster != null ? targetCluster.hashCode() : 0);
    result = 31 * result + (targetClass != null ? targetClass.hashCode() : 0);
    result = 31 * result + (updateOperations != null ? updateOperations.hashCode() : 0);
    result = 31 * result + (batch != null ? batch.hashCode() : 0);
    return result;
  }

  public OFromItem getSource() {
    return source;
  }

  public void setSource(OFromItem source) {
    this.source = source;
  }

  public OCluster getTargetCluster() {
    return targetCluster;
  }

  public void setTargetCluster(OCluster targetCluster) {
    this.targetCluster = targetCluster;
  }

  public OIdentifier getTargetClass() {
    return targetClass;
  }

  public void setTargetClass(OIdentifier targetClass) {
    this.targetClass = targetClass;
  }

  public OUpdateOperations getUpdateOperations() {
    return updateOperations;
  }

  public void setUpdateOperations(OUpdateOperations updateOperations) {
    this.updateOperations = updateOperations;
  }

  public OBatch getBatch() {
    return batch;
  }

  public void setBatch(OBatch batch) {
    this.batch = batch;
  }
}
/* JavaCC - OriginalChecksum=5cb0b9d3644fd28813ff615fe59d577d (do not edit this line) */

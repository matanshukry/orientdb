/* Generated By:JJTree: Do not edit this line. OCreateVertexStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.sql.executor.OCreateVertexExecutionPlanner;
import com.orientechnologies.orient.core.sql.executor.OInsertExecutionPlan;
import com.orientechnologies.orient.core.sql.executor.OInternalExecutionPlan;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.HashMap;
import java.util.Map;

public class OCreateVertexStatement extends OStatement {

  OIdentifier targetClass;
  OIdentifier targetClusterName;
  OCluster targetCluster;
  OProjection returnStatement;
  OInsertBody insertBody;

  public OCreateVertexStatement(int id) {
    super(id);
  }

  public OCreateVertexStatement(OrientSql p, int id) {
    super(p, id);
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
    OInsertExecutionPlan executionPlan;
    if (usePlanCache) {
      executionPlan = (OInsertExecutionPlan) createExecutionPlan(ctx, false);
    } else {
      executionPlan = (OInsertExecutionPlan) createExecutionPlanNoCache(ctx, false);
    }
    executionPlan.executeInternal();
    return new OLocalResultSet(executionPlan);
  }

  @Override
  public OResultSet execute(
      ODatabase db, Object[] args, OCommandContext parentCtx, boolean usePlanCache) {
    OBasicCommandContext ctx = new OBasicCommandContext();
    if (parentCtx != null) {
      ctx.setParentWithoutOverridingChild(parentCtx);
    }
    ctx.setDatabase(db);
    Map<Object, Object> params = new HashMap<>();
    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        params.put(i, args[i]);
      }
    }
    ctx.setInputParameters(params);
    OInsertExecutionPlan executionPlan;
    if (usePlanCache) {
      executionPlan = (OInsertExecutionPlan) createExecutionPlan(ctx, false);
    } else {
      executionPlan = (OInsertExecutionPlan) createExecutionPlanNoCache(ctx, false);
    }
    executionPlan.executeInternal();
    return new OLocalResultSet(executionPlan);
  }

  @Override
  public OInternalExecutionPlan createExecutionPlan(OCommandContext ctx, boolean enableProfiling) {
    OCreateVertexExecutionPlanner planner = new OCreateVertexExecutionPlanner(this);
    OInternalExecutionPlan result = planner.createExecutionPlan(ctx, enableProfiling);
    result.setStatement(this.originalStatement);
    return result;
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {

    builder.append("CREATE VERTEX ");
    if (targetClass != null) {
      targetClass.toString(params, builder);
      if (targetClusterName != null) {
        builder.append(" CLUSTER ");
        targetClusterName.toString(params, builder);
      }
    }
    if (targetCluster != null) {
      targetCluster.toString(params, builder);
    }
    if (returnStatement != null) {
      builder.append(" RETURN ");
      returnStatement.toString(params, builder);
    }
    if (insertBody != null) {
      if (targetClass != null || targetCluster != null || returnStatement != null) {
        builder.append(" ");
      }
      insertBody.toString(params, builder);
    }
  }

  @Override
  public OCreateVertexStatement copy() {
    OCreateVertexStatement result = null;
    try {
      result = getClass().getConstructor(Integer.TYPE).newInstance(-1);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    result.targetClass = targetClass == null ? null : targetClass.copy();
    result.targetClusterName = targetClusterName == null ? null : targetClusterName.copy();
    result.targetCluster = targetCluster == null ? null : targetCluster.copy();
    result.returnStatement = returnStatement == null ? null : returnStatement.copy();
    result.insertBody = insertBody == null ? null : insertBody.copy();
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OCreateVertexStatement that = (OCreateVertexStatement) o;

    if (targetClass != null ? !targetClass.equals(that.targetClass) : that.targetClass != null)
      return false;
    if (targetClusterName != null
        ? !targetClusterName.equals(that.targetClusterName)
        : that.targetClusterName != null) return false;
    if (targetCluster != null
        ? !targetCluster.equals(that.targetCluster)
        : that.targetCluster != null) return false;
    if (returnStatement != null
        ? !returnStatement.equals(that.returnStatement)
        : that.returnStatement != null) return false;
    if (insertBody != null ? !insertBody.equals(that.insertBody) : that.insertBody != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = targetClass != null ? targetClass.hashCode() : 0;
    result = 31 * result + (targetClusterName != null ? targetClusterName.hashCode() : 0);
    result = 31 * result + (targetCluster != null ? targetCluster.hashCode() : 0);
    result = 31 * result + (returnStatement != null ? returnStatement.hashCode() : 0);
    result = 31 * result + (insertBody != null ? insertBody.hashCode() : 0);
    return result;
  }

  public OIdentifier getTargetClass() {
    return targetClass;
  }

  public void setTargetClass(OIdentifier targetClass) {
    this.targetClass = targetClass;
  }

  public OIdentifier getTargetClusterName() {
    return targetClusterName;
  }

  public void setTargetClusterName(OIdentifier targetClusterName) {
    this.targetClusterName = targetClusterName;
  }

  public OCluster getTargetCluster() {
    return targetCluster;
  }

  public void setTargetCluster(OCluster targetCluster) {
    this.targetCluster = targetCluster;
  }

  public OProjection getReturnStatement() {
    return returnStatement;
  }

  public void setReturnStatement(OProjection returnStatement) {
    this.returnStatement = returnStatement;
  }

  public OInsertBody getInsertBody() {
    return insertBody;
  }

  public void setInsertBody(OInsertBody insertBody) {
    this.insertBody = insertBody;
  }
}
/* JavaCC - OriginalChecksum=0ac3d3f09a76b9924a17fd05bc293863 (do not edit this line) */

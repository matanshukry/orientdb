/* Generated By:JJTree: Do not edit this line. ODeleteVertexStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.sql.executor.ODeleteExecutionPlan;
import com.orientechnologies.orient.core.sql.executor.ODeleteVertexExecutionPlanner;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.HashMap;
import java.util.Map;

public class ODeleteVertexStatement extends OStatement {

  protected boolean from = false;
  protected OFromClause fromClause;
  protected OWhereClause whereClause;
  protected boolean returnBefore = false;
  protected OLimit limit = null;
  protected OBatch batch = null;

  public ODeleteVertexStatement(int id) {
    super(id);
  }

  public ODeleteVertexStatement(OrientSql p, int id) {
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
    ODeleteExecutionPlan executionPlan;
    if (usePlanCache) {
      executionPlan = createExecutionPlan(ctx, false);
    } else {
      executionPlan = (ODeleteExecutionPlan) createExecutionPlanNoCache(ctx, false);
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
    ODeleteExecutionPlan executionPlan;
    if (usePlanCache) {
      executionPlan = createExecutionPlan(ctx, false);
    } else {
      executionPlan = (ODeleteExecutionPlan) createExecutionPlanNoCache(ctx, false);
    }
    executionPlan.executeInternal();
    return new OLocalResultSet(executionPlan);
  }

  public ODeleteExecutionPlan createExecutionPlan(OCommandContext ctx, boolean enableProfiling) {
    ODeleteVertexExecutionPlanner planner = new ODeleteVertexExecutionPlanner(this);
    ODeleteExecutionPlan result = planner.createExecutionPlan(ctx, enableProfiling);
    result.setStatement(this.originalStatement);
    return result;
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {
    builder.append("DELETE VERTEX ");
    if (from) {
      builder.append("FROM ");
    }
    fromClause.toString(params, builder);
    if (returnBefore) {
      builder.append(" RETURN BEFORE");
    }
    if (whereClause != null) {
      builder.append(" WHERE ");
      whereClause.toString(params, builder);
    }
    if (limit != null) {
      limit.toString(params, builder);
    }
    if (batch != null) {
      batch.toString(params, builder);
    }
  }

  @Override
  public ODeleteVertexStatement copy() {
    ODeleteVertexStatement result = new ODeleteVertexStatement(-1);
    result.from = from;
    result.fromClause = fromClause == null ? null : fromClause.copy();
    result.whereClause = whereClause == null ? null : whereClause.copy();
    result.returnBefore = returnBefore;
    result.limit = limit == null ? null : limit.copy();
    result.batch = batch == null ? null : batch.copy();
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ODeleteVertexStatement that = (ODeleteVertexStatement) o;

    if (from != that.from) return false;
    if (returnBefore != that.returnBefore) return false;
    if (fromClause != null ? !fromClause.equals(that.fromClause) : that.fromClause != null)
      return false;
    if (whereClause != null ? !whereClause.equals(that.whereClause) : that.whereClause != null)
      return false;
    if (limit != null ? !limit.equals(that.limit) : that.limit != null) return false;
    if (batch != null ? !batch.equals(that.batch) : that.batch != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (from ? 1 : 0);
    result = 31 * result + (fromClause != null ? fromClause.hashCode() : 0);
    result = 31 * result + (whereClause != null ? whereClause.hashCode() : 0);
    result = 31 * result + (returnBefore ? 1 : 0);
    result = 31 * result + (limit != null ? limit.hashCode() : 0);
    result = 31 * result + (batch != null ? batch.hashCode() : 0);
    return result;
  }

  public boolean isFrom() {
    return from;
  }

  public void setFrom(boolean from) {
    this.from = from;
  }

  public OFromClause getFromClause() {
    return fromClause;
  }

  public void setFromClause(OFromClause fromClause) {
    this.fromClause = fromClause;
  }

  public OWhereClause getWhereClause() {
    return whereClause;
  }

  public void setWhereClause(OWhereClause whereClause) {
    this.whereClause = whereClause;
  }

  public boolean isReturnBefore() {
    return returnBefore;
  }

  public void setReturnBefore(boolean returnBefore) {
    this.returnBefore = returnBefore;
  }

  public OLimit getLimit() {
    return limit;
  }

  public void setLimit(OLimit limit) {
    this.limit = limit;
  }

  public OBatch getBatch() {
    return batch;
  }

  public void setBatch(OBatch batch) {
    this.batch = batch;
  }
}
/* JavaCC - OriginalChecksum=b62d3046f4bd1b9c1f78ed4f125b06d3 (do not edit this line) */

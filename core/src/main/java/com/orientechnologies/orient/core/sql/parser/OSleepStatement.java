/* Generated By:JJTree: Do not edit this line. OSleepStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.sql.executor.OInternalResultSet;
import com.orientechnologies.orient.core.sql.executor.OResultInternal;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.Map;

public class OSleepStatement extends OSimpleExecStatement {

  protected OInteger millis;

  public OSleepStatement(int id) {
    super(id);
  }

  public OSleepStatement(OrientSql p, int id) {
    super(p, id);
  }

  @Override
  public OResultSet executeSimple(OCommandContext ctx) {

    OInternalResultSet result = new OInternalResultSet();
    OResultInternal item = new OResultInternal();
    item.setProperty("operation", "sleep");
    try {
      Thread.sleep(millis.getValue().intValue());
      item.setProperty("result", "OK");
      item.setProperty("millis", millis.getValue().intValue());
    } catch (InterruptedException e) {
      item.setProperty("result", "failure");
      item.setProperty("errorType", e.getClass().getSimpleName());
      item.setProperty("errorMessage", e.getMessage());
    }
    result.add(item);
    return result;
  }

  @Override
  public void toString(Map<Object, Object> params, StringBuilder builder) {
    builder.append("SLEEP ");
    millis.toString(params, builder);
  }

  @Override
  public OSleepStatement copy() {
    OSleepStatement result = new OSleepStatement(-1);
    result.millis = millis == null ? null : millis.copy();
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OSleepStatement that = (OSleepStatement) o;

    if (millis != null ? !millis.equals(that.millis) : that.millis != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return millis != null ? millis.hashCode() : 0;
  }
}
/* JavaCC - OriginalChecksum=2ea765ee266d4215414908b0e09c0779 (do not edit this line) */

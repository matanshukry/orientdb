/* Generated By:JJTree: Do not edit this line. OMathExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.record.OIdentifiable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OMathExpression extends SimpleNode {

  public enum Operator {
    PLUS {
      @Override
      public Number apply(Integer left, Integer right) {
        final Integer sum = left + right;
        if (sum < 0 && left.intValue() > 0 && right.intValue() > 0)
          // SPECIAL CASE: UPGRADE TO LONG
          return left.longValue() + right;
        return sum;
      }

      @Override
      public Number apply(Long left, Long right) {
        return left + right;
      }

      @Override
      public Number apply(Float left, Float right) {
        return left + right;
      }

      @Override
      public Number apply(Double left, Double right) {
        return left + right;
      }

      @Override
      public Number apply(BigDecimal left, BigDecimal right) {
        return left.add(right);
      }
    },
    MINUS {
      @Override
      public Number apply(Integer left, Integer right) {
        int result = left - right;
        if (result > 0 && left.intValue() < 0 && right.intValue() > 0)
          // SPECIAL CASE: UPGRADE TO LONG
          return left.longValue() - right;

        return result;
      }

      @Override
      public Number apply(Long left, Long right) {
        return left - right;
      }

      @Override
      public Number apply(Float left, Float right) {
        return left - right;
      }

      @Override
      public Number apply(Double left, Double right) {
        return left - right;
      }

      @Override
      public Number apply(BigDecimal left, BigDecimal right) {
        return left.subtract(right);
      }
    },
    STAR {
      @Override
      public Number apply(Integer left, Integer right) {
        return left * right;
      }

      @Override
      public Number apply(Long left, Long right) {
        return left * right;
      }

      @Override
      public Number apply(Float left, Float right) {
        return left * right;
      }

      @Override
      public Number apply(Double left, Double right) {
        return left * right;
      }

      @Override
      public Number apply(BigDecimal left, BigDecimal right) {
        return left.multiply(right);
      }
    },
    SLASH {
      @Override
      public Number apply(Integer left, Integer right) {
        return left / right;
      }

      @Override
      public Number apply(Long left, Long right) {
        return left / right;
      }

      @Override
      public Number apply(Float left, Float right) {
        return left / right;
      }

      @Override
      public Number apply(Double left, Double right) {
        return left / right;
      }

      @Override
      public Number apply(BigDecimal left, BigDecimal right) {
        return left.divide(right, BigDecimal.ROUND_HALF_UP);
      }
    },
    REM {
      @Override
      public Number apply(Integer left, Integer right) {
        return left % right;
      }

      @Override
      public Number apply(Long left, Long right) {
        return left % right;
      }

      @Override
      public Number apply(Float left, Float right) {
        return left % right;
      }

      @Override
      public Number apply(Double left, Double right) {
        return left % right;
      }

      @Override
      public Number apply(BigDecimal left, BigDecimal right) {
        return left.remainder(right);
      }
    };

    public abstract Number apply(Integer left, Integer right);

    public abstract Number apply(Long left, Long right);

    public abstract Number apply(Float left, Float right);

    public abstract Number apply(Double left, Double right);

    public abstract Number apply(BigDecimal left, BigDecimal right);

  }

  protected List<OMathExpression> childExpressions = new ArrayList<OMathExpression>();
  protected List<Operator>        operators        = new ArrayList<Operator>();

  public OMathExpression(int id) {
    super(id);
  }

  public OMathExpression(OrientSql p, int id) {
    super(p, id);
  }

  public Object execute(OIdentifiable iCurrentRecord, OCommandContext ctx) {
    if (childExpressions.size() == 0) {
      return null;
    }

    OMathExpression nextExpression = childExpressions.get(0);
    Object nextValue = nextExpression.execute(iCurrentRecord, ctx);
    for (int i = 0; i < operators.size() && i + 1 < childExpressions.size(); i++) {
      Operator nextOperator = operators.get(i);
      Object rightValue = childExpressions.get(i + 1).execute(iCurrentRecord, ctx);
      nextValue = apply(nextValue, nextOperator, rightValue);
    }
    return nextValue;
  }

  /** Accept the visitor. **/
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public List<OMathExpression> getChildExpressions() {
    return childExpressions;
  }

  public void setChildExpressions(List<OMathExpression> childExpressions) {
    this.childExpressions = childExpressions;
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {
    for (int i = 0; i < childExpressions.size(); i++) {
      if (i > 0) {
        builder.append(" ");
        switch (operators.get(i - 1)) {
        case PLUS:
          builder.append("+");
          break;
        case MINUS:
          builder.append("-");
          break;
        case STAR:
          builder.append("*");
          break;
        case SLASH:
          builder.append("/");
          break;
        case REM:
          builder.append("%");
          break;
        }
        builder.append(" ");
      }
      childExpressions.get(i).toString(params, builder);
    }
  }

  public Object apply(final Object a, final Operator operation, final Object b) {
    if (b == null) {
      return a;
    }
    if (a == null) {
      return b;
    }
    if (a instanceof Number && b instanceof Number) {
      return apply((Number) a, operation, (Number) b);
    }
    if (a instanceof String || b instanceof String) {
      return "" + a + b;
    }
    throw new IllegalArgumentException("Cannot apply operaton " + operation + " to value '" + a + "' (" + a.getClass() + ") with '"
        + b + "' (" + b.getClass() + ")");

  }

  public Number apply(final Number a, final Operator operation, final Number b) {
    if (a == null || b == null)
      throw new IllegalArgumentException("Cannot increment a null value");

    if (a instanceof Integer || a instanceof Short) {
      if (b instanceof Integer || b instanceof Short) {
        return operation.apply(a.intValue(), b.intValue());
      } else if (b instanceof Long) {
        return operation.apply(a.longValue(), b.longValue());
      } else if (b instanceof Float)
        return operation.apply(a.floatValue(), b.floatValue());
      else if (b instanceof Double)
        return operation.apply(a.doubleValue(), b.doubleValue());
      else if (b instanceof BigDecimal)
        return operation.apply(new BigDecimal((Integer) a), (BigDecimal) b);
    } else if (a instanceof Long) {
      if (b instanceof Integer || b instanceof Long || b instanceof Short)
        return operation.apply(a.longValue(), b.longValue());
      else if (b instanceof Float)
        return operation.apply(a.floatValue(), b.floatValue());
      else if (b instanceof Double)
        return operation.apply(a.doubleValue(), b.doubleValue());
      else if (b instanceof BigDecimal)
        return operation.apply(new BigDecimal((Long) a), (BigDecimal) b);
    } else if (a instanceof Float) {
      if (b instanceof Short || b instanceof Integer || b instanceof Long || b instanceof Float)
        return operation.apply(a.floatValue(), b.floatValue());
      else if (b instanceof Double)
        return operation.apply(a.doubleValue(), b.doubleValue());
      else if (b instanceof BigDecimal)
        return operation.apply(new BigDecimal((Float) a), (BigDecimal) b);

    } else if (a instanceof Double) {
      if (b instanceof Short || b instanceof Integer || b instanceof Long || b instanceof Float || b instanceof Double)
        return operation.apply(a.doubleValue(), b.doubleValue());
      else if (b instanceof BigDecimal)
        return operation.apply(new BigDecimal((Double) a), (BigDecimal) b);

    } else if (a instanceof BigDecimal) {
      if (b instanceof Integer)
        return operation.apply((BigDecimal) a, new BigDecimal((Integer) b));
      else if (b instanceof Long)
        return operation.apply((BigDecimal) a, new BigDecimal((Long) b));
      else if (b instanceof Short)
        return operation.apply((BigDecimal) a, new BigDecimal((Short) b));
      else if (b instanceof Float)
        return operation.apply((BigDecimal) a, new BigDecimal((Float) b));
      else if (b instanceof Double)
        return operation.apply((BigDecimal) a, new BigDecimal((Double) b));
      else if (b instanceof BigDecimal)
        return operation.apply((BigDecimal) a, (BigDecimal) b);
    }

    throw new IllegalArgumentException("Cannot increment value '" + a + "' (" + a.getClass() + ") with '" + b + "' ("
        + b.getClass() + ")");

  }

  protected boolean supportsBasicCalculation() {
    for (OMathExpression expr : this.childExpressions) {
      if (!expr.supportsBasicCalculation()) {
        return false;
      }
    }
    return true;

  }

  public boolean isIndexedFunctionCall() {
    if (this.childExpressions.size() != 1) {
      return false;
    }
    return this.childExpressions.get(0).isIndexedFunctionCall();
  }

  public long estimateIndexedFunction(OFromClause target, OCommandContext context, OBinaryCompareOperator operator, Object right) {
    if (this.childExpressions.size() != 1) {
      return -1;
    }
    return this.childExpressions.get(0).estimateIndexedFunction(target, context, operator, right);
  }

  public Iterable<OIdentifiable> executeIndexedFunction(OFromClause target, OCommandContext context,
      OBinaryCompareOperator operator, Object right) {
    if (this.childExpressions.size() != 1) {
      return null;
    }
    return this.childExpressions.get(0).executeIndexedFunction(target, context, operator, right);
  }
}
/* JavaCC - OriginalChecksum=c255bea24e12493e1005ba2a4d1dbb9d (do not edit this line) */

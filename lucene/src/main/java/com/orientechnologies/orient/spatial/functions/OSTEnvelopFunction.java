/*
 *
 *  * Copyright 2014 Orient Technologies.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  
 */

package com.orientechnologies.orient.spatial.functions;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.sql.functions.OSQLFunctionAbstract;
import com.orientechnologies.orient.spatial.shape.OShapeFactory;
import com.spatial4j.core.shape.Shape;

/**
 * Created by Enrico Risa on 06/08/15.
 */
public class OSTEnvelopFunction extends OSQLFunctionAbstract {

  public static final String NAME    = "ST_Envelope";

  OShapeFactory              factory = OShapeFactory.INSTANCE;

  public OSTEnvelopFunction() {
    super(NAME, 1, 1);
  }

  @Override
  public Object execute(Object iThis, OIdentifiable iCurrentRecord, Object iCurrentResult, Object[] iParams,
      OCommandContext iContext) {
    Shape shape = factory.fromObject(iParams[0]);
    return factory.toDoc(shape.getBoundingBox());
  }

  @Override
  public String getSyntax() {
    return "ST_AsBinary(<doc>)";
  }
}

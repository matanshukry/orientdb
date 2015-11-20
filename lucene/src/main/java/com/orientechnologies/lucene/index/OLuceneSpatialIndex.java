/*
 * Copyright 2014 Orient Technologies.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.orientechnologies.lucene.index;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.impl.local.OAbstractPaginatedStorage;
import com.orientechnologies.orient.spatial.shape.OShapeFactory;
import com.spatial4j.core.shape.Shape;
import com.vividsolutions.jts.geom.Geometry;

public class OLuceneSpatialIndex extends OLuceneIndexNotUnique {

  OShapeFactory shapeFactory = OShapeFactory.INSTANCE;

  public OLuceneSpatialIndex(String name, String typeId, String algorithm, int version, OAbstractPaginatedStorage storage,
      String valueContainerAlgorithm, ODocument metadata) {
    super(name, typeId, algorithm, version, storage, valueContainerAlgorithm, metadata);

  }

  @Override
  protected Object encodeKey(Object key) {

    Shape shape = shapeFactory.fromDoc((ODocument) key);
    return shapeFactory.toGeometry(shape);
  }

  @Override
  protected Object decodeKey(Object key) {
    Geometry geom = (Geometry) key;
    return shapeFactory.toDoc(geom);
  }
}

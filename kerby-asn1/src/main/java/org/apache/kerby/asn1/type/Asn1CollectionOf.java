/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class Asn1CollectionOf<T extends Asn1Type> extends Asn1Collection
{
    public Asn1CollectionOf(UniversalTag universalTag) {
        super(universalTag);
    }

    public List<T> getElements() {
        List<Asn1Type> items = getValue();
        int nElements = items != null ? items.size() : 0;
        List<T> results = new ArrayList<T>(nElements);
        if (nElements > 0) {
            for (Asn1Type itemObj : items) {
                if (itemObj instanceof Asn1Item) {
                    Asn1Item item = (Asn1Item) itemObj;
                    if (!item.isFullyDecoded()) {
                        try {
                            item.decodeValueAs(getElementType());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    itemObj = item.getValue();
                }
                results.add((T) itemObj);
            }
        }
        return results;
    }

    public void setElements(List<T> elements) {
        super.clear();

        for (T ele : elements) {
            addElement(ele);
        }
    }

    public void addElements(T ... elements) {
        for (T ele : elements) {
            addElement(ele);
        }
    }

    public void addElement(T element) {
        super.addItem(element);
    }

    protected Class<T> getElementType() {
        Class<T> elementType = (Class<T>) ((ParameterizedType)
                getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return elementType;
    }
}

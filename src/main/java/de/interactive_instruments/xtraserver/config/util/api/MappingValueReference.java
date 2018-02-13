/**
 * Copyright 2018 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.util.api;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * Represents a reference mapping for a mapping target path
 *
 * @author zahnen
 */
public class MappingValueReference extends MappingValueExpression {

    private final String referencedFeatureType;

    MappingValueReference(String targetPath, List<QName> qualifiedTargetPath, String value, String description, TYPE type, String referencedFeatureType) {
        super(targetPath, qualifiedTargetPath, value, description, type);
        this.referencedFeatureType = referencedFeatureType;
    }

    /**
     * Returns the prefixed name of the referenced feature type
     *
     * @return the referenced feature type
     */
    public String getReferencedFeatureType() {
        return referencedFeatureType;
    }

    /**
     * Returns the mapping target path of the reference
     *
     * @return the mapping target path
     */
    public String getReferencedTarget() {
        return getTargetPath().substring(0, getTargetPath().lastIndexOf("/"));
    }
}

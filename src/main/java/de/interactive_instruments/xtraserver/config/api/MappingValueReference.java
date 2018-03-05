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
package de.interactive_instruments.xtraserver.config.api;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Objects;

/**
 * Represents a reference mapping for a mapping target path
 *
 * @author zahnen
 */
public class MappingValueReference extends MappingValueExpression {

    private final String referencedFeatureType;

    MappingValueReference(final String targetPath, final List<QName> qualifiedTargetPath, final String value, final String description, final TYPE type, final String referencedFeatureType) {
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final MappingValueReference that = (MappingValueReference) o;
        return Objects.equals(referencedFeatureType, that.referencedFeatureType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), referencedFeatureType);
    }
}

/**
 * Copyright 2018 interactive instruments GmbH
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
 * Represents a classification or nil mapping for a mapping target path
 *
 * @author zahnen
 */
public class MappingValueClassification extends MappingValue {

    private final List<String> keys;
    private final List<String> values;

    MappingValueClassification(final String targetPath, final List<QName> qualifiedTargetPath, final String value, final String description, final MappingValue.TYPE type, final List<String> keys, final List<String> values) {
        super(targetPath, qualifiedTargetPath, value, description, type);
        this.keys = keys;
        this.values = values;
    }

    /**
     * Returns the classification keys
     *
     * @return the classification keys
     */
    public List<String> getKeys() {
        return keys;
    }

    /**
     * Returns the classification values
     *
     * @return the classification values
     */
    public List<String> getValues() {
        return values;
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
        final MappingValueClassification that = (MappingValueClassification) o;
        return Objects.equals(keys, that.keys) &&
                Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), keys, values);
    }
}

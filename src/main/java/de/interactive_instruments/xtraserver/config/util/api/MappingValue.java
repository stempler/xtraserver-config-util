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
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a value mapping for a mapping target path
 *
 * @author zahnen
 */
public class MappingValue {
    enum TYPE {COLUMN, EXPRESSION, CONSTANT, REFERENCE, GEOMETRY, CLASSIFICATION, NIL}

    private final String targetPath;
    private final List<QName> qualifiedTargetPath;
    private final String value;
    private final String description;
    private final TYPE type;

    MappingValue(final String targetPath, List<QName> qualifiedTargetPath, final String value, final String description, final TYPE type) {
        this.targetPath = targetPath;
        this.qualifiedTargetPath = qualifiedTargetPath;
        this.value = value;
        this.description = description;
        this.type = type;
    }

    /**
     * Returns the column referenced in the value, if any
     *
     * @return the column name
     */
    public Optional<String> getValueColumn() {
        return Optional.of(value);
    }

    /**
     * Returns the mapping target path
     *
     * @return the target path
     */
    public String getTargetPath() {
        return targetPath;
    }

    /**
     * Returns the list of qualified path elements in the target path
     *
     * @return the target path
     */
    public List<QName> getQualifiedTargetPath() {
        return qualifiedTargetPath;
    }

    /**
     * Returns the value, might be a column name, an expression or a constant
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the description
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    TYPE getType() {
        return type;
    }

    /**
     * Is this value mapping of type column?
     *
     * @return true if column or geometry
     */
    public boolean isColumn() {
        return type.equals(TYPE.COLUMN) || type.equals(TYPE.GEOMETRY);
    }

    /**
     * Is this value mapping of type expression?
     *
     * @return true if expression or reference
     */
    public boolean isExpression() {
        return type.equals(TYPE.EXPRESSION) || type.equals(TYPE.REFERENCE);
    }

    /**
     * Is this value mapping of type constant?
     *
     * @return true if constant
     */
    public boolean isConstant() {
        return type.equals(TYPE.CONSTANT);
    }

    /**
     * Is this value mapping of type reference?
     *
     * @return true if reference
     */
    public boolean isReference() {
        return type.equals(TYPE.REFERENCE);
    }

    /**
     * Is this value mapping of type geometry?
     *
     * @return true if geometry
     */
    public boolean isGeometry() {
        return type.equals(TYPE.GEOMETRY);
    }

    /**
     * Is this value mapping of type classification?
     *
     * @return true if classification or nil
     */
    public boolean isClassification() {
        return type.equals(TYPE.CLASSIFICATION) || type.equals(TYPE.NIL);
    }

    /**
     * Is this value mapping of type nil?
     *
     * @return true if nil
     */
    public boolean isNil() {
        return type.equals(TYPE.NIL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappingValue that = (MappingValue) o;
        return Objects.equals(targetPath, that.targetPath) &&
                Objects.equals(qualifiedTargetPath, that.qualifiedTargetPath) &&
                Objects.equals(value, that.value) &&
                Objects.equals(description, that.description) &&
                type == that.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(targetPath, qualifiedTargetPath, value, description, type);
    }
}

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
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link MappingValue} and its subclasses {@link MappingValueExpression}, {@link MappingValueClassification} and {@link MappingValueReference}
 *
 * @author zahnen
 */
public class MappingValueBuilder {
    private final Builder builder;

    /**
     * Create new builder
     */
    public MappingValueBuilder() {
        this.builder = new Builder();
    }

    /**
     * Build a value mapping of type column
     *
     * @return the builder
     */
    public ValueDefault column() {
        builder.type = MappingValue.TYPE.COLUMN;
        return builder;
    }

    /**
     * Build a value mapping of type expression
     *
     * @return the builder
     */
    public ValueDefault expression() {
        builder.type = MappingValue.TYPE.EXPRESSION;
        return builder;
    }

    /**
     * Build a value mapping of type constant
     *
     * @return the builder
     */
    public ValueDefault constant() {
        builder.type = MappingValue.TYPE.CONSTANT;
        return builder;
    }

    /**
     * Build a value mapping of type reference
     *
     * @return the builder
     */
    public ValueReference reference() {
        builder.type = MappingValue.TYPE.REFERENCE;
        return builder;
    }

    /**
     * Build a value mapping of type geometry
     *
     * @return the builder
     */
    public ValueDefault geometry() {
        builder.type = MappingValue.TYPE.GEOMETRY;
        return builder;
    }

    /**
     * Build a value mapping of type classification
     *
     * @return the builder
     */
    public ValueClassification classification() {
        builder.type = MappingValue.TYPE.CLASSIFICATION;
        return builder;
    }

    /**
     * Build a value mapping of type nil
     *
     * @return the builder
     */
    public ValueClassification nil() {
        builder.type = MappingValue.TYPE.NIL;
        return builder;
    }

    /**
     * Copy targetPath, qualifiedTargetPath, value, description and type from given {@link MappingValue}
     *
     * @param mappingValue the copy source
     * @return the builder
     */
    public ValueDefault shallowCopyOf(final MappingValue mappingValue) {
        builder.targetPath = mappingValue.getTargetPath();
        builder.qualifiedTargetPath = mappingValue.getQualifiedTargetPath();
        builder.value = mappingValue.getValue();
        builder.description = mappingValue.getDescription();
        builder.type = mappingValue.getType();

        if (mappingValue.isReference()) {
            builder.referencedFeatureType = ((MappingValueReference) mappingValue).getReferencedFeatureType();
        }

        return builder;
    }

    /**
     * Same as {@link MappingValueBuilder#shallowCopyOf(MappingValue)}
     *
     * @param mappingValue the copy source
     * @return the builder
     */
    public ValueDefault copyOf(final MappingValue mappingValue) {
        shallowCopyOf(mappingValue);

        if (mappingValue.isClassification() || mappingValue.isNil()) {
            builder.keys.addAll(((MappingValueClassification) mappingValue).getKeys());
            builder.values.addAll(((MappingValueClassification) mappingValue).getValues());
        }

        return builder;
    }

    /**
     * Same as {@link MappingValueBuilder#copyOf(MappingValue)}, additionally copies key value pairs
     *
     * @param mappingValueClassification the copy source
     * @return the builder
     */
    public ValueClassification copyOf(final MappingValueClassification mappingValueClassification) {
        return (ValueClassification) copyOf((MappingValue) mappingValueClassification);
    }

    /**
     * Same as {@link MappingValueBuilder#shallowCopyOf(MappingValue)}
     *
     * @param mappingValueClassification the copy source
     * @return the builder
     */
    public ValueClassification shallowCopyOf(final MappingValueClassification mappingValueClassification) {
        return (ValueClassification) shallowCopyOf((MappingValue) mappingValueClassification);
    }

    /**
     * Same as {@link MappingValueBuilder#shallowCopyOf(MappingValueReference)}
     *
     * @param mappingValueReference the copy source
     * @return the builder
     */
    public ValueReference copyOf(final MappingValueReference mappingValueReference) {
        return (ValueReference) copyOf((MappingValue) mappingValueReference);
    }

    /**
     * Same as {@link MappingValueBuilder#shallowCopyOf(MappingValue)}, additionally copies referenced feature type
     *
     * @param mappingValueReference the copy source
     * @return the builder
     */
    public ValueReference shallowCopyOf(final MappingValueReference mappingValueReference) {
        return (ValueReference) shallowCopyOf((MappingValue) mappingValueReference);
    }

    /**
     * Builder for {@link MappingValue}s of type column, constant and geometry
     */
    public interface ValueDefault {
        /**
         * Set the target path (required)
         *
         * @param targetPath target path
         * @return the builder
         */
        ValueDefault targetPath(String targetPath);

        /**
         * Set the qualified target path
         *
         * @param qualifiedTargetPath list of qualified path elements
         * @return the builder
         */
        ValueDefault qualifiedTargetPath(List<QName> qualifiedTargetPath);

        /**
         * Set the value (required, may be a column name, an expression or a constant)
         *
         * @param value value
         * @return the builder
         */
        ValueDefault value(String value);

        /**
         * Set the description
         *
         * @param description description
         * @return the builder
         */
        ValueDefault description(String description);

        /**
         * Builds the {@link MappingValue}, validates required fields
         *
         * @return a new immutable {@link MappingValue}
         */
        MappingValue build();
    }

    /**
     * Builder for {@link MappingValueClassification}s of type classification and nil
     */
    public interface ValueClassification extends ValueDefault {
        /**
         * Add a key value pair (at least one is required)
         *
         * @param key   the key
         * @param value the value
         * @return the builder
         */
        ValueClassification keyValue(String key, String value);
    }

    /**
     * Builder for {@link MappingValueReference}s of type reference
     */
    public interface ValueReference {
        /**
         * Set the referenced feature type name
         *
         * @param referencedFeatureType prefixed name
         * @return the builder
         */
        ValueDefault referencedFeatureType(String referencedFeatureType);
    }

    private static class Builder implements ValueDefault, ValueClassification, ValueReference {

        private String targetPath;
        private List<QName> qualifiedTargetPath;
        private String value;
        private String description;
        private MappingValue.TYPE type;
        private final List<String> keys;
        private final List<String> values;
        private String referencedFeatureType;

        Builder() {
            this.keys = new ArrayList<>();
            this.values = new ArrayList<>();
        }

        @Override
        public ValueDefault targetPath(final String targetPath) {
            this.targetPath = targetPath;
            return this;
        }

        @Override
        public ValueDefault qualifiedTargetPath(final List<QName> qualifiedTargetPath) {
            this.qualifiedTargetPath = qualifiedTargetPath;
            return this;
        }

        @Override
        public ValueDefault value(final String value) {
            this.value = value;
            return this;
        }

        @Override
        public ValueDefault description(final String description) {
            this.description = description;
            return this;
        }

        @Override
        public ValueClassification keyValue(final String key, final String value) {
            this.keys.add(key);
            this.values.add(value);
            return this;
        }

        @Override
        public ValueDefault referencedFeatureType(final String referencedFeatureType) {
            this.referencedFeatureType = referencedFeatureType;
            return this;
        }

        @Override
        public MappingValue build() {
            final MappingValue mappingValue;

            switch (type) {

                case EXPRESSION:
                    mappingValue = new MappingValueExpression(targetPath, qualifiedTargetPath, value, description, type);
                    break;
                case REFERENCE:
                    mappingValue = new MappingValueReference(targetPath, qualifiedTargetPath, value, description, type, referencedFeatureType);
                    break;
                case CLASSIFICATION:
                case NIL:
                    mappingValue = new MappingValueClassification(targetPath, qualifiedTargetPath, value, description, type, keys, values);
                    break;
                case COLUMN:
                case CONSTANT:
                case GEOMETRY:
                default:
                    mappingValue = new MappingValue(targetPath, qualifiedTargetPath, value, description, type);
            }

            validate(mappingValue);

            return mappingValue;
        }

        private void validate(final MappingValue mappingValue) {
            if ((targetPath == null || targetPath.isEmpty())
                    && (qualifiedTargetPath == null || qualifiedTargetPath.isEmpty())) {
                throw new IllegalStateException("Value has no targetPath or qualifiedTargetPath");
            }
        }

    }
}

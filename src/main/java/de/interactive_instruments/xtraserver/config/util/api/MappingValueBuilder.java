package de.interactive_instruments.xtraserver.config.util.api;

import de.interactive_instruments.xtraserver.config.util.api.MappingValue.TYPE;

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
        builder.type = TYPE.COLUMN;
        return builder;
    }

    /**
     * Build a value mapping of type expression
     *
     * @return the builder
     */
    public ValueDefault expression() {
        builder.type = TYPE.EXPRESSION;
        return builder;
    }

    /**
     * Build a value mapping of type constant
     *
     * @return the builder
     */
    public ValueDefault constant() {
        builder.type = TYPE.CONSTANT;
        return builder;
    }

    /**
     * Build a value mapping of type reference
     *
     * @return the builder
     */
    public ValueReference reference() {
        builder.type = TYPE.REFERENCE;
        return builder;
    }

    /**
     * Build a value mapping of type geometry
     *
     * @return the builder
     */
    public ValueDefault geometry() {
        builder.type = TYPE.GEOMETRY;
        return builder;
    }

    /**
     * Build a value mapping of type classification
     *
     * @return the builder
     */
    public ValueClassification classification() {
        builder.type = TYPE.CLASSIFICATION;
        return builder;
    }

    /**
     * Build a value mapping of type nil
     *
     * @return the builder
     */
    public ValueClassification nil() {
        builder.type = TYPE.NIL;
        return builder;
    }

    /**
     * Copy targetPath, qualifiedTargetPath, value, description and type from given {@link MappingValue}
     *
     * @param mappingValue the copy source
     * @return the builder
     */
    public ValueDefault shallowCopyOf(MappingValue mappingValue) {
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
    public ValueDefault copyOf(MappingValue mappingValue) {
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
    public ValueClassification copyOf(MappingValueClassification mappingValueClassification) {
        return (ValueClassification) copyOf((MappingValue) mappingValueClassification);
    }

    /**
     * Same as {@link MappingValueBuilder#shallowCopyOf(MappingValue)}
     *
     * @param mappingValueClassification the copy source
     * @return the builder
     */
    public ValueClassification shallowCopyOf(MappingValueClassification mappingValueClassification) {
        return (ValueClassification) shallowCopyOf((MappingValue) mappingValueClassification);
    }

    /**
     * Same as {@link MappingValueBuilder#shallowCopyOf(MappingValueReference)}
     *
     * @param mappingValueReference the copy source
     * @return the builder
     */
    public ValueReference copyOf(MappingValueReference mappingValueReference) {
        return (ValueReference) copyOf((MappingValue) mappingValueReference);
    }

    /**
     * Same as {@link MappingValueBuilder#shallowCopyOf(MappingValue)}, additionally copies referenced feature type
     *
     * @param mappingValueReference the copy source
     * @return the builder
     */
    public ValueReference shallowCopyOf(MappingValueReference mappingValueReference) {
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
    public interface ValueReference extends ValueDefault {
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
        private TYPE type;
        private final List<String> keys;
        private final List<String> values;
        private String referencedFeatureType;

        Builder() {
            this.keys = new ArrayList<>();
            this.values = new ArrayList<>();
        }

        @Override
        public ValueDefault targetPath(String targetPath) {
            this.targetPath = targetPath;
            return this;
        }

        @Override
        public ValueDefault qualifiedTargetPath(List<QName> qualifiedTargetPath) {
            this.qualifiedTargetPath = qualifiedTargetPath;
            return this;
        }

        @Override
        public ValueDefault value(String value) {
            this.value = value;
            return this;
        }

        @Override
        public ValueDefault description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public ValueClassification keyValue(String key, String value) {
            this.keys.add(key);
            this.values.add(value);
            return this;
        }

        @Override
        public ValueDefault referencedFeatureType(String referencedFeatureType) {
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
            /* TODO if (mappingTable.getName() == null || mappingTable.getName().isEmpty()) {
                throw new IllegalStateException("Table has no name");
            }
            if (!mappingTable.isPredicate() && (mappingTable.getPrimaryKey() == null || mappingTable.getPrimaryKey().isEmpty())) {
                throw new IllegalStateException("Table has no primary key");
            }

            if (!mappingTable.isPrimary() && !mappingTable.isJoined() && !mappingTable.isPredicate()) {
                throw new  IllegalStateException("Invalid table. Valid configurations are primary (no targetPath + no joinPaths), joined (targetPath + at least one joinPath) and predicate (targetPath + predicate).");
            }

            if (mappingTable.getJoiningTables().stream().flatMap(joiningTable -> joiningTable.getJoinPaths().stream())
                    .anyMatch(mappingJoin -> !mappingJoin.getSourceTable().equals(mappingTable.getName()))) {
                throw new IllegalStateException("Join source table of joining table does not match");
            }

            if (mappingTable.getJoinPaths().stream()
                    .anyMatch(mappingJoin -> !mappingJoin.getTargetTable().equals(mappingTable.getName()))) {
                throw new IllegalStateException("Join target table does not match");
            }*/
        }

    }
}

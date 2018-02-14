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

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link MappingJoin}
 *
 * @author zahnen
 */
public class MappingJoinBuilder {
    private String targetPath;
    private final List<MappingJoin.Condition> joinConditions;

    /**
     * Create new builder
     */
    public MappingJoinBuilder() {
        this.joinConditions = new ArrayList<>();
    }

    /**
     * Set the target path (required)
     *
     * @param targetPath target path
     * @return the builder
     */
    public MappingJoinBuilder targetPath(String targetPath) {
        this.targetPath = targetPath;
        return this;
    }

    /**
     * Add a join condition (at least one condition is required).
     * When multiple conditions are added, the source table of a subsequent condition needs to match the target table of the previous one.
     * Join conditions can be created with {@link ConditionBuilder}
     *
     * @param joinCondition the join condition
     * @return the builder
     */
    public MappingJoinBuilder joinCondition(MappingJoin.Condition joinCondition) {
        this.joinConditions.add(joinCondition);
        return this;
    }

    /**
     * Add a list of join conditions (at least one condition is required).
     * When multiple conditions are added, the source table of a subsequent condition needs to match the target table of the previous one.
     * Join conditions can be created with {@link ConditionBuilder}
     *
     * @param joinConditions the join conditions
     * @return the builder
     */
    public MappingJoinBuilder joinConditions(List<MappingJoin.Condition> joinConditions) {
        this.joinConditions.addAll(joinConditions);
        return this;
    }

    /**
     * Copy targetPath and joinConditions from given {@link MappingJoin}
     *
     * @param mappingJoin the copy source
     * @return the builder
     */
    public MappingJoinBuilder copyOf(MappingJoin mappingJoin) {
        this.targetPath = mappingJoin.getTargetPath();
        this.joinConditions.addAll(mappingJoin.getJoinConditions());

        return this;
    }

    /**
     * Builds the {@link MappingJoin}, validates required fields
     *
     * @return a new immutable {@link MappingJoin}
     */
    public MappingJoin build() {
        final MappingJoin mappingJoin = new MappingJoin(targetPath, ImmutableList.copyOf(joinConditions));

        if (mappingJoin.getTargetPath() == null || mappingJoin.getTargetPath().isEmpty()) {
            throw new IllegalStateException("Join has no target path");
        }
        if (mappingJoin.getJoinConditions().isEmpty()) {
            throw new IllegalStateException("Join has no conditions");
        }
        for (int i = 0; i < mappingJoin.getJoinConditions().size(); i++) {
            MappingJoin.Condition condition = mappingJoin.getJoinConditions().get(i);
            if (condition.getSourceTable() == null || condition.getSourceTable().isEmpty()) {
                throw new IllegalStateException("Join condition is incomplete: source table is missing");
            }
            if (condition.getSourceField() == null || condition.getSourceTable().isEmpty()) {
                throw new IllegalStateException("Join condition is incomplete: source field is missing");
            }
            if (condition.getTargetTable() == null || condition.getSourceTable().isEmpty()) {
                throw new IllegalStateException("Join condition is incomplete: targetPath table is missing");
            }
            if (condition.getTargetField() == null || condition.getSourceTable().isEmpty()) {
                throw new IllegalStateException("Join condition is incomplete: targetPath field is missing");
            }
            if (i > 0 && !condition.getSourceTable().equals(mappingJoin.getJoinConditions().get(i - 1).getTargetTable())) {
                throw new IllegalStateException("Join condition does not match previously added condition");
            }
        }

        return mappingJoin;
    }

    /**
     * Builder for {@link MappingJoin.Condition}
     */
    public static class ConditionBuilder {
        private String sourceTable;
        private String sourceField;
        private String targetTable;
        private String targetField;

        /**
         * Create new builder
         */
        public ConditionBuilder() {
        }

        /**
         * Set the source table (required)
         *
         * @param sourceTable source table
         * @return the builder
         */
        public ConditionBuilder sourceTable(String sourceTable) {
            this.sourceTable = sourceTable;
            return this;
        }

        /**
         * Set the source field (required)
         *
         * @param sourceField source field
         * @return the builder
         */
        public ConditionBuilder sourceField(String sourceField) {
            this.sourceField = sourceField;
            return this;
        }

        /**
         * Set the target table (required)
         *
         * @param targetTable target table
         * @return the builder
         */
        public ConditionBuilder targetTable(String targetTable) {
            this.targetTable = targetTable;
            return this;
        }

        /**
         * Set the target field (required)
         *
         * @param targetField target field
         * @return the builder
         */
        public ConditionBuilder targetField(String targetField) {
            this.targetField = targetField;
            return this;
        }

        /**
         * Builds the {@link MappingJoin.Condition}, validates required fields
         *
         * @return a new immutable {@link MappingJoin.Condition}
         */
        public MappingJoin.Condition build() {
            return new MappingJoin.Condition(sourceTable, sourceField, targetTable, targetField);
        }
    }
}
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

import java.util.List;
import java.util.Objects;

/**
 * Represents a join path that joins multiple tables based on conditions
 *
 * @author zahnen
 */
public class MappingJoin {
    private final String targetPath;
    private final List<Condition> joinConditions;
    private final String description;

    MappingJoin(final String targetPath, final List<Condition> joinConditions, String description) {
        this.targetPath = targetPath;
        this.joinConditions = joinConditions;
        this.description = description;
    }

    /**
     * returns the mapping target path
     *
     * @return the target path
     */
    public String getTargetPath() {
        return targetPath;
    }

    /**
     * Returns the join conditions
     *
     * @return the join conditions
     */
    public List<Condition> getJoinConditions() {
        return joinConditions;
    }

    /**
     * Returns the source table of the join
     *
     * @return the source table of the join
     */
    public String getSourceTable() {
        if (joinConditions.isEmpty()) {
            return null;
        }

        return joinConditions.get(0).getSourceTable();
    }

    /**
     * Returns the target table of the join
     *
     * @return the target table of the join
     */
    public String getTargetTable() {
        if (joinConditions.isEmpty()) {
            return null;
        }

        return joinConditions.get(joinConditions.size() - 1).getTargetTable();
    }

    /**
     * Returns the description
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MappingJoin that = (MappingJoin) o;
        return Objects.equals(targetPath, that.targetPath) &&
                Objects.equals(joinConditions, that.joinConditions);
    }

    @Override
    public int hashCode() {

        return Objects.hash(targetPath, joinConditions);
    }

    @Override
    public String toString() {
        final StringBuilder repr = new StringBuilder();
        int i = 0;
        for (final Condition cndtn : joinConditions) {
            repr.append(cndtn.toString());
            if (++i < joinConditions.size()) {
                repr.append(" && ");
            }
        }
        return repr.toString();
    }

    /**
     * Represents the join condition between two tables
     */
    public static class Condition {
        private final String sourceTable;
        private final String sourceField;
        private final String targetTable;
        private final String targetField;

        Condition(final String sourceTable, final String sourceField, final String targetTable, final String targetField) {
            this.sourceTable = sourceTable;
            this.sourceField = sourceField;
            this.targetTable = targetTable;
            this.targetField = targetField;
        }

        /**
         * Returns the source table of the condition
         *
         * @return the source table of the condition
         */
        public String getSourceTable() {
            return sourceTable;
        }

        /**
         * Returns the source field of the condition
         *
         * @return the source field of the condition
         */
        public String getSourceField() {
            return sourceField;
        }

        /**
         * Returns the target table of the condition
         *
         * @return the target table of the condition
         */
        public String getTargetTable() {
            return targetTable;
        }

        /**
         * Returns the target field of the condition
         *
         * @return the target field of the condition
         */
        public String getTargetField() {
            return targetField;
        }

        @Override
        public String toString() {
            return sourceTable + "[" + sourceField + "]=" + targetTable + "[" + targetField + "]";
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Condition condition = (Condition) o;
            return Objects.equals(sourceTable, condition.sourceTable) &&
                    Objects.equals(sourceField, condition.sourceField) &&
                    Objects.equals(targetTable, condition.targetTable) &&
                    Objects.equals(targetField, condition.targetField);
        }

        @Override
        public int hashCode() {

            return Objects.hash(sourceTable, sourceField, targetTable, targetField);
        }
    }
}

package de.interactive_instruments.xtraserver.config.util.api;

import java.util.List;
import java.util.Objects;

/**
 * Represents a join path that joins multiple tables based on conditions
 *
 * @author zahnen
 */
public class MappingJoin {
    private String targetPath;
    private final List<Condition> joinConditions;

    MappingJoin(String targetPath, List<Condition> joinConditions) {
        this.targetPath = targetPath;
        this.joinConditions = joinConditions;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappingJoin that = (MappingJoin) o;
        return Objects.equals(targetPath, that.targetPath) &&
                Objects.equals(joinConditions, that.joinConditions);
    }

    @Override
    public int hashCode() {

        return Objects.hash(targetPath, joinConditions);
    }

    @Override
    public String toString() {
        StringBuilder repr = new StringBuilder();
        int i = 0;
        for (Condition cndtn : joinConditions) {
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

        Condition(String sourceTable, String sourceField, String targetTable, String targetField) {
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Condition condition = (Condition) o;
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

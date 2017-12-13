package de.interactive_instruments.xtraserver.config.util.api;

import de.interactive_instruments.xtraserver.config.util.MappingJoinImpl;

import java.util.List;

/**
 * A join definition
 *
 * @author zahnen
 */
public interface MappingJoin {
    /**
     * factory method
     *
     * @return
     */
    static MappingJoin create() {
        return new MappingJoinImpl();
    }

    String getTarget();

    String getAxis();

    String getPath();

    void setTarget(String target);

    boolean isSuppressJoin();

    List<Condition> getJoinConditions();

    /**
     * A join condition
     */
    interface Condition {
        /**
         * factory method
         *
         * @return
         */
        static Condition create() {
            return new MappingJoinImpl.ConditionImpl();
        }

        String getSourceTable();

        String getSourceField();

        String getTargetTable();

        String getTargetField();

        void setSourceTable(MappingTable sourceTable);

        void setSourceField(String sourceField);

        void setTargetTable(MappingTable targetTable);

        void setTargetField(String targetField);
    }
}

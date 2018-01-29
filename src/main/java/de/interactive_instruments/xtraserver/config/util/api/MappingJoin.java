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

    void addCondition(Condition condition);

    String getSourceTable();

    String getTargetTable();

    /**
     * A join condition
     */
    interface Condition {
        /**
         * factory method
         *
         * @return
         */
        static Condition create(MappingTable sourceTable, String sourceField, MappingTable targetTable, String targetField) {
            return new MappingJoinImpl.ConditionImpl(sourceTable.getName(), sourceField, targetTable.getName(), targetField);
        }

        String getSourceTable();

        String getSourceField();

        String getTargetTable();

        String getTargetField();
    }
}

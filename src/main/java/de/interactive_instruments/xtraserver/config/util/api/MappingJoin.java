package de.interactive_instruments.xtraserver.config.util.api;

import java.util.List;

/**
 * A join definition
 *
 * @author zahnen
 */
public interface MappingJoin {
    String getTarget();

    String getAxis();

    String getPath();

    boolean isSuppressJoin();

    List<Condition> getJoinConditions();

    /**
     * A join condition
     */
    interface Condition {
        String getSourceTable();

        String getSourceField();

        String getTargetTable();

        String getTargetField();
    }
}

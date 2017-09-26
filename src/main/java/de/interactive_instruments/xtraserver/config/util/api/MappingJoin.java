package de.interactive_instruments.xtraserver.config.util.api;

import java.util.List;

/**
 * @author zahnen
 */
public interface MappingJoin {
    String getTarget();

    String getAxis();

    String getPath();

    boolean isSuppressJoin();

    List<Condition> getJoinConditions();

    interface Condition {
        String getSourceTable();

        String getSourceField();

        String getTargetTable();

        String getTargetField();
    }
}

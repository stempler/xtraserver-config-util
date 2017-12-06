package de.interactive_instruments.xtraserver.config.util.api;

import java.util.List;

/**
 * A table definition including related joins
 *
 * @author zahnen
 */
public interface MappingTable {
    String getName();

    String getOidCol();

    String getTarget();

    boolean hasTarget();

    void setTarget(String target);

    // TODO: check if table is join target
    boolean isPrimary();

    void addJoinPath(MappingJoin joinPath);

    List<MappingJoin> getJoinPaths();

    boolean hasJoinPath();
}

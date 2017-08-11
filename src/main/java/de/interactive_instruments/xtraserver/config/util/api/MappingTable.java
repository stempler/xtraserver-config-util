package de.interactive_instruments.xtraserver.config.util.api;

/**
 * @author zahnen
 */
public interface MappingTable {
    String getName();

    String getOidCol();

    String getTarget();

    // TODO: check if table is join target
    boolean isPrimary();

    MappingJoin getJoinPath();

    void setJoinPath(MappingJoin joinPath);
}

package de.interactive_instruments.xtraserver.config.util.api;

import de.interactive_instruments.xtraserver.config.util.MappingTableImpl;

import java.util.List;

/**
 * A table definition including related joins
 *
 * @author zahnen
 */
public interface MappingTable {

    /**
     * factory method
     *
     * @return
     */
    static MappingTable create() {
        return new MappingTableImpl();
    }

    String getName();

    String getOidCol();

    String getTarget();

    boolean hasTarget();

    void setName(String name);

    void setOidCol(String oidCol);

    void setTarget(String target);

    boolean isPrimary();

    void addJoinPath(MappingJoin joinPath);

    List<MappingJoin> getJoinPaths();

    boolean hasJoinPath();

    List<MappingValue> getValues();
}

package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.schema.MappingsSequenceType;

/**
 * @author zahnen
 */
public class MappingTableImpl implements de.interactive_instruments.xtraserver.config.util.api.MappingTable {

    private String name;
    private String oidCol;
    private String target;
    private String joinPath;

    public MappingTableImpl(MappingsSequenceType.Table table) {
        this.name = table.getTable_Name();
        this.oidCol = table.getOid_Col();
        this.target = table.getTarget();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOidCol() {
        return oidCol;
    }

    @Override
    public String getTarget() {
        return target;
    }

    // TODO: check if table is join target
    @Override
    public boolean isPrimary() {
        return target == null || target.isEmpty();
    }

    @Override
    public String getJoinPath() {
        return joinPath;
    }

    @Override
    public void setJoinPath(String joinPath) {
        this.joinPath = joinPath;
    }
}

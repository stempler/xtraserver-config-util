package de.interactive_instruments.xtraserver.config.api;

import de.interactive_instruments.xtraserver.schema.MappingsSequenceType;

/**
 * @author zahnen
 */
public class MappingTable {

    private String name;
    private String oidCol;
    private String target;
    private String joinPath;

    public MappingTable(MappingsSequenceType.Table table) {
        this.name = table.getTable_Name();
        this.oidCol = table.getOid_Col();
        this.target = table.getTarget();
    }

    public String getName() {
        return name;
    }

    public String getOidCol() {
        return oidCol;
    }

    public String getTarget() {
        return target;
    }

    // TODO: check if table is join target
    public boolean isPrimary() {
        return target == null || target.isEmpty();
    }

    public String getJoinPath() {
        return joinPath;
    }

    public void setJoinPath(String joinPath) {
        this.joinPath = joinPath;
    }
}

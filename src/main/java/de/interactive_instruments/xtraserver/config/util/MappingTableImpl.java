package de.interactive_instruments.xtraserver.config.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import de.interactive_instruments.xtraserver.config.schema.MappingsSequenceType;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.util.api.MappingTable;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zahnen
 */
public class MappingTableImpl implements de.interactive_instruments.xtraserver.config.util.api.MappingTable {

    private String name;
    private String oidCol;
    private String target;

    private final List<MappingJoin> joinPaths;
    private final List<MappingValue> values;

    public MappingTableImpl() {
        this.joinPaths = new ArrayList<>();
        this.values = new ArrayList<>();
        this.target = "";
    }

    MappingTableImpl(MappingsSequenceType.Table table) {
        this.name = table.getTable_Name();
        if (this.name.contains("[")) {
            System.out.println("PREDICATE " + name);
            this.name = name.substring(0, name.indexOf("["));
        }
        this.oidCol = table.getOid_Col();
        if (this.oidCol.contains(":=SEQUENCE")) {
            this.oidCol = oidCol.substring(0, oidCol.indexOf(":=SEQUENCE"));
        }
        this.target = table.getTarget();
        this.joinPaths = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    MappingTableImpl(MappingTable mappingTable, String target) {
        this.name = mappingTable.getName();
        this.oidCol = mappingTable.getOidCol();
        this.target = target;
        this.joinPaths = Lists.newArrayList(Collections2.filter(mappingTable.getJoinPaths(), new Predicate<MappingJoin>() {
            @Override
            public boolean apply(MappingJoin join) {
                return join.getTarget().equals(target);
            }
        }));
        this.values = new ArrayList<>();
    }

    MappingTableImpl(MappingTable mappingTable, MappingJoin join) {
        this.name = mappingTable.getName();
        this.oidCol = mappingTable.getOidCol();
        this.target = join.getTarget();
        this.joinPaths = Lists.newArrayList(join);
        this.values = new ArrayList<>();
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

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setOidCol(String oidCol) {
        this.oidCol = oidCol;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public boolean hasTarget() {return target != null && !target.isEmpty();}

    // TODO: check if table is join target
    @Override
    public boolean isPrimary() {
        return !hasTarget();
    }

    @Override
    public void addJoinPath(MappingJoin joinPath) {
        this.joinPaths.add(joinPath);
    }

    @Override
    public List<MappingJoin> getJoinPaths() {
        return joinPaths;
    }

    @Override
    public boolean hasJoinPath() {
        return !this.joinPaths.isEmpty();
    }

    void addValue(MappingValue value) {
        values.add(value);
    }

    @Override
    public List<MappingValue> getValues() {
        return values;
    }
}

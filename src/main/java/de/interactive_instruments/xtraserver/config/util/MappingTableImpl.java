package de.interactive_instruments.xtraserver.config.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import de.interactive_instruments.xtraserver.config.schema.MappingsSequenceType;
import de.interactive_instruments.xtraserver.config.util.api.AssociationTarget;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.util.api.MappingTable;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zahnen
 */
public class MappingTableImpl implements de.interactive_instruments.xtraserver.config.util.api.MappingTable {

    private String name;
    private String oidCol;
    private String target;
    public boolean isJoined;
    public String featureType;

    private final List<MappingJoin> joinPaths;
    private final List<MappingValue> values;
    private final List<AssociationTarget> associationTargets;

    public MappingTableImpl() {
        this.joinPaths = new ArrayList<>();
        this.values = new ArrayList<>();
        this.associationTargets = new ArrayList<>();
        this.target = "";
    }

    MappingTableImpl(MappingTableImpl mappingTable) {
        this.name = mappingTable.name;
        this.oidCol = mappingTable.oidCol;
        this.target = mappingTable.target;
        this.joinPaths = new ArrayList<>();
        this.values = new ArrayList<>();
        this.associationTargets = new ArrayList<>();
        this.isJoined = ((MappingTableImpl)mappingTable).isJoined;

        mappingTable.joinPaths.forEach(mappingJoin -> this.joinPaths.add(new MappingJoinImpl((MappingJoinImpl) mappingJoin)));
        mappingTable.values.forEach(mappingValue -> this.values.add(new MappingValueImpl((MappingValueImpl) mappingValue)));
        mappingTable.associationTargets.forEach(associationTarget -> this.associationTargets.add(new AssociationTargetImpl((AssociationTargetImpl) associationTarget)));
    }

    MappingTableImpl(MappingTable mappingTable, String target) {
        this.name = mappingTable.getName();
        this.oidCol = mappingTable.getOidCol();
        this.target = target;
        this.joinPaths = new ArrayList<>();//Lists.newArrayList(Collections2.filter(mappingTable.getJoinPaths(), join -> join.getTarget().equals(target)));
        this.values = new ArrayList<>();
        this.associationTargets = new ArrayList<>();
        this.isJoined = ((MappingTableImpl)mappingTable).isJoined;

        mappingTable.getJoinPaths().stream()
                .filter(mappingJoin -> mappingJoin.getTarget().equals(target))
                .forEach(mappingJoin -> this.joinPaths.add(new MappingJoinImpl((MappingJoinImpl) mappingJoin)));
        ((MappingTableImpl) mappingTable).getAssociationTargets().stream()
                .filter(associationTarget -> associationTarget.getTarget().equals(target))
                .forEach(associationTarget -> this.associationTargets.add(new AssociationTargetImpl((AssociationTargetImpl) associationTarget)));
    }

    MappingTableImpl(MappingTable mappingTable, MappingJoin join) {
        this.name = mappingTable.getName();
        this.oidCol = mappingTable.getOidCol();
        this.target = join.getTarget();
        this.joinPaths = Lists.newArrayList(new MappingJoinImpl((MappingJoinImpl) join));
        this.values = new ArrayList<>();
        this.associationTargets = new ArrayList<>();
        this.isJoined = ((MappingTableImpl)mappingTable).isJoined;

        ((MappingTableImpl) mappingTable).getAssociationTargets().stream()
                .filter(associationTarget -> associationTarget.getTarget().equals(target))
                .forEach(associationTarget -> this.associationTargets.add(new AssociationTargetImpl((AssociationTargetImpl) associationTarget)));
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
        joinPaths.forEach(mappingJoin -> mappingJoin.setTarget(target));
    }

    @Override
    public boolean hasTarget() {return target != null && !target.isEmpty();}

    @Override
    public boolean isPrimary() {
        return !hasTarget() && !hasJoinPath() /*&& !values.isEmpty()*/ && !isJoined;
    }

    @Override
    public void setJoined(boolean joined) {
        isJoined = joined;
    }

    @Override
    public void addJoinPath(MappingJoin joinPath) {
        if (!this.joinPaths.contains(joinPath)) {
            this.joinPaths.add(joinPath);
        }
    }

    @Override
    public List<MappingJoin> getJoinPaths() {
        return joinPaths;
    }

    @Override
    public boolean hasJoinPath() {
        return this.joinPaths.stream().anyMatch(mappingJoin -> !mappingJoin.isSuppressJoin());
    }

    void addValue(MappingValue value) {
        if (!this.values.contains(value)) {
            values.add(value);
        }
    }

    @Override
    public List<MappingValue> getValues() {
        return values;
    }

    public boolean isReference() {
        return hasTarget() && getJoinPaths().size() == 1 && getValues().size() == 1 && getAssociationTargets().size() == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappingTableImpl that = (MappingTableImpl) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(oidCol, that.oidCol) &&
                Objects.equals(target, that.target) &&
                Objects.equals(joinPaths, that.joinPaths) &&
                Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, oidCol, target, joinPaths, values);
    }

    @Override
    public String toString() {
        return "\nMappingTableImpl{" +
                "\nname='" + name + '\'' +
                "\n, oidCol='" + oidCol + '\'' +
                "\n, target='" + target + '\'' +
                "\n, joinPaths=" + joinPaths +
                "\n, values=" + values +
                "\n}";
    }

    List<AssociationTarget> getAssociationTargets() {
        return associationTargets;
    }

    void addAssociationTarget(AssociationTarget associationTarget) {
        if (!associationTargets.contains(associationTarget)) {
            associationTargets.add(associationTarget);
        }
    }
}

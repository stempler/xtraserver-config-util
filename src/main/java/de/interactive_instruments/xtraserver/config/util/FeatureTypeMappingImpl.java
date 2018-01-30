package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.util.api.*;

import javax.xml.namespace.QName;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
public class FeatureTypeMappingImpl implements FeatureTypeMapping {
    private final String name;
    private final QName qualifiedTypeName;
    private final List<MappingTable> tables;
    private final List<MappingJoin> joins;
    private final List<MappingValue> values;
    private final List<AssociationTarget> associationTargets;
    private final Namespaces namespaces;

    public FeatureTypeMappingImpl(String name, QName qualifiedTypeName, Namespaces namespaces) {
        this.name = name;
        this.qualifiedTypeName = qualifiedTypeName;
        this.tables = new ArrayList<>();
        this.joins = new ArrayList<>();
        this.values = new ArrayList<>();
        this.associationTargets = new ArrayList<>();
        this.namespaces = namespaces;
    }

    FeatureTypeMappingImpl(FeatureTypeMapping mainMapping, List<FeatureTypeMapping> mergedMappings, Namespaces namespaces) {
        this.name = mainMapping.getName();
        this.qualifiedTypeName = mainMapping.getQName();
        this.tables = new ArrayList<>();
        this.joins = new ArrayList<>();
        this.values = new ArrayList<>();
        this.associationTargets = new ArrayList<>();
        this.namespaces = namespaces;

        mainMapping.getTables().forEach(mappingTable -> this.tables.add(new MappingTableImpl((MappingTableImpl) mappingTable)));
        mainMapping.getJoins().forEach(mappingJoin -> this.joins.add(new MappingJoinImpl((MappingJoinImpl) mappingJoin)));
        mainMapping.getValues().forEach(mappingValue -> this.values.add(new MappingValueImpl((MappingValueImpl) mappingValue)));
        mainMapping.getAssociationTargets().forEach(associationTarget -> this.associationTargets.add(new AssociationTargetImpl((AssociationTargetImpl) associationTarget)));

        //TODO: merge AssociationTargets???
        for (FeatureTypeMapping mergedMapping : mergedMappings) {
            // TODO: first merge joins and tables
            List<MappingTable> referencedTables = extractJoinedTables(mergedMapping);

            for (MappingTable mappingTable : referencedTables) {
                if (!hasTable(mappingTable.getName())) {
                    this.tables.add(new MappingTableImpl((MappingTableImpl)mappingTable));
                }
            }

            for (MappingValue mappingValue : mergedMapping.getValues()) {
                //System.out.println(mappingValue.getTarget());
                if (!values.contains(mappingValue)) {
                    Optional<MappingTable> mappingTable = getTable(mappingValue.getTable());
                    if ((mappingTable.isPresent() && hasValueMappingForTable(mappingValue.getTable(), mappingTable.get().getTarget())) ||
                            (mappingTable.isPresent() && mappingValue.getTarget().startsWith(mappingTable.get().getTarget()))) {
                        MappingValue mappingValue1 = new MappingValueImpl((MappingValueImpl)mappingValue);
                        this.values.add(mappingValue1);
                        mappingTable.get().getValues().add(mappingValue1);
                    }
                }
            }
        }
    }

    private Collection<String> getTableNames(boolean primary, boolean hasValueMapping) {
        return tables.stream()
                .filter(
                        table -> primary ? table.isPrimary() : (!table.isPrimary() && hasValueMapping == hasValueMappingForTable(table.getName()))
                )
                .map(MappingTable::getName)
                .collect(Collectors.toList());
    }

    private Optional<MappingTable> getTableOptional(String tableName) {
        return tables.stream()
                .filter(
                        table -> tableName.equals(table.getName())
                )
                .findFirst();
    }

    private Optional<MappingTable> getTableOptional(String tableName, String target) {
        return tables.stream()
                .filter(
                        table -> tableName.equals(table.getName()) && target.equals(table.getTarget())
                )
                .findFirst();
    }

    private Collection<MappingValue> getTableValues(String tableName, boolean isReference, String target) {
        return values.stream()
                .filter(
                        value -> tableName.equals(value.getTable()) && value.getValue() != null && !value.getValue().isEmpty()
                                && isReference == value.getTarget().endsWith("/@xlink:href")
                                && (target == null || value.getTarget().startsWith(target))
                )
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public QName getQName() {
        return qualifiedTypeName;
    }

    @Override
    public Collection<String> getPrimaryTableNames() {

        return getTableNames(true, false);
    }

    @Override
    public Collection<String> getJoinedTableNames() {

        return getTableNames(false, true);
    }

    @Override
    public Collection<String> getReferenceTableNames() {

        return getTableNames(false, false);
    }

    @Override
    public List<MappingTable> getTables() {
        return tables;
    }

    @Override
    public List<MappingJoin> getJoins() {
        return joins;
    }

    @Override
    public List<MappingValue> getValues() {
        return values;
    }

    @Override
    public List<AssociationTarget> getAssociationTargets() {
        return associationTargets;
    }

    @Override
    public boolean hasTable(String name) {
        return getTableOptional(name).isPresent();
    }

    @Override
    public boolean hasTable(String name, String target) {
        return getTableOptional(name, target).isPresent();
    }

    @Override
    public Optional<MappingTable> getTable(String name) {
        return getTableOptional(name);
    }

    @Override
    public Optional<MappingTable> getTable(String name, String target) {
        return getTableOptional(name, target);
    }

    @Override
    public boolean hasValueMappingForTable(String name) {
        return !getTableValues(name, false, null).isEmpty();
    }

    @Override
    public boolean hasValueMappingForTable(String name, String target) {
        return !getTableValues(name, false, target).isEmpty();
    }

    @Override
    public boolean hasReferenceMappingForTable(String name, String target) {
        return !getTableValues(name, true, target).isEmpty();
    }

    @Override
    public void addTable(MappingTable mappingTable) {
        if (mappingTable.getName() == null || mappingTable.getName().isEmpty()) {
            throw new IllegalArgumentException("Table has no name");
        }
        if (!mappingTable.hasTarget() && (mappingTable.getOidCol() == null || mappingTable.getOidCol().isEmpty())) {
            throw new IllegalArgumentException("Table has no oidCol");
        }

        /*if (tables.contains(mappingTable)) {
            tables.remove(mappingTable);
        }*/

        if (!hasTable(mappingTable.getName())) {
            tables.add(mappingTable);
        }
    }

    @Override
    public void addJoin(MappingJoin mappingJoin) {
        if (mappingJoin.getJoinConditions().size() == 0) {
            throw new IllegalArgumentException("Join has no conditions");
        }

        final String targetTableName = mappingJoin.getTargetTable();
        final Optional<MappingTable> targetTable = getTable(targetTableName);
        final String sourceTableName = mappingJoin.getSourceTable();
        final Optional<MappingTable> sourceTable = getTable(sourceTableName);

        if (!targetTable.isPresent()) {
            throw new IllegalArgumentException("Join target table '" + targetTableName + "' does not exist");
        }
        if (!sourceTable.isPresent()) {
            throw new IllegalArgumentException("Join source table '" + sourceTableName + "' does not exist");
        }

        if (!mappingJoin.isSuppressJoin()) {
            // if join has target, overwrite table target (for table definitions parsed from value mappings)
            if (mappingJoin.getTarget() != null && !mappingJoin.getTarget().isEmpty()) {
                targetTable.get().setTarget(mappingJoin.getTarget());
            }
            // if join has no target, overwrite with table target (for exports from hale)
            else {
                mappingJoin.setTarget(targetTable.get().getTarget());
            }
        }

        targetTable.get().addJoinPath(mappingJoin);
        joins.add(mappingJoin);
    }

    @Override
    public void addValue(MappingValue mappingValue) {
        if (mappingValue.getTable() == null || mappingValue.getTable().isEmpty()) {
            throw new IllegalArgumentException("Value mapping has no table name");
        }
        if (mappingValue.getValue() == null) {
            throw new IllegalArgumentException("Value mapping has no value");
        }
        if (mappingValue.getTarget() == null || mappingValue.getTarget().isEmpty()) {
            throw new IllegalArgumentException("Value mapping has no target");
        }

        values.add(mappingValue);
    }

    /*@Override
    public void addAssociationTarget(AssociationTarget associationTarget) {
        if (associationTarget.getObjectRef() == null || associationTarget.getObjectRef().isEmpty()) {
            throw new IllegalArgumentException("Association target has no object reference");
        }
        if (associationTarget.getTarget() == null || associationTarget.getTarget().isEmpty()) {
            throw new IllegalArgumentException("Association target has no target");
        }

        Optional<MappingValue> mappingValue = values.stream()
                .filter(
                        value -> value.getTarget().equals(associationTarget.getTarget() + "/@xlink:href")
                )
                .findFirst();

        if (!mappingValue.isPresent()) {
            throw new IllegalArgumentException("No value mapping found for given association target");
        }

        if (mappingValue.get().getValue().equals("href")) {
            mappingValue.get().setValue("");
        }

        Optional<MappingTable> mappingTable = tables.stream()
                .filter(
                        table -> table.getValues().contains(mappingValue.get())
                )
                .findFirst();

        if (!mappingTable.isPresent()) {
            throw new IllegalArgumentException("No table mapping found for given association target");
        }

        ((MappingTableImpl)mappingTable.get()).addAssociationTarget(associationTarget);

        associationTargets.add(associationTarget);
    }*/

    @Override
    public void addAssociationTarget(AssociationTarget associationTarget) {
        if (associationTarget.getObjectRef() == null || associationTarget.getObjectRef().isEmpty()) {
            throw new IllegalArgumentException("Association target has no object reference");
        }
        if (associationTarget.getTarget() == null || associationTarget.getTarget().isEmpty()) {
            throw new IllegalArgumentException("Association target has no target");
        }

        List<MappingValue> mappingValues = values.stream()
                .filter(
                        value -> value.getTarget().equals(associationTarget.getTarget() + "/@xlink:href")
                )
                .collect(Collectors.toList());

        if (mappingValues.isEmpty()) {
            throw new IllegalArgumentException("No value mapping found for given association target");
        }

        mappingValues.forEach(mappingValue -> {
            if (mappingValue.getValue().equals("href")) {
                mappingValue.setValue("");
            }
        });

        List<MappingTable> mappingTables = tables.stream()
                .filter(
                        table -> table.getValues().stream().anyMatch(mappingValues::contains)//contains(mappingValue.get())
                )
                .collect(Collectors.toList());

        if (mappingTables.isEmpty()) {
            throw new IllegalArgumentException("No table mapping found for given association target");
        }

        mappingTables.forEach(mappingTable -> ((MappingTableImpl)mappingTable).addAssociationTarget(associationTarget));

        associationTargets.add(associationTarget);
    }

    private List<MappingTable> extractJoinedTables(FeatureTypeMapping mergedMapping) {
        List<String> referencedTablesString = new ArrayList<>();
        List<MappingTable> referencedTables = new ArrayList<>();
        List<String> joinedTables = new ArrayList<>();
        List<String> joinedTablesString = new ArrayList<>();

        if (mergedMapping != null && mergedMapping.getTables() != null) {
            boolean added;
            do {
                added = false;
                for (MappingTable mappingTable : mergedMapping.getTables()) {
                    for (MappingJoin join : mappingTable.getJoinPaths()) {
                        String sourceTableName = join.getSourceTable();
                        String targetTableName = join.getTargetTable();

                        /*if (mappingTable.getName().equals("o51009")) {
                            System.out.println("FOO " + join.toString() + " " + join.getTarget());
                            System.out.println("BAR " + sourceTableName + (hasTable(sourceTableName) ? join.getTarget().startsWith(getTable(sourceTableName).getTarget()) : "NOPE"));
                        }*/

                        // merged join can be connected to existing table and target
                        if (!join.getJoinConditions().isEmpty() && hasTable(sourceTableName) && join.getTarget().startsWith(getTable(sourceTableName).get().getTarget())) {

                            /*if (targetTableName.equals("o51009")) {
                                System.out.println("BLA " + targetTableName + " " + mergedMapping.hasTable(targetTableName) + " " + hasTable(targetTableName));
                            }*/

                            if (mergedMapping.hasTable(targetTableName)) {
                                //merged join is needed for a value mapping
                                if (mergedMapping.hasValueMappingForTable(targetTableName, join.getTarget())) {
                                    if (!hasTable(targetTableName)) {
                                        this.tables.add(new MappingTableImpl(mergedMapping.getTable(targetTableName).get(), join.getTarget()));
                                        joinedTablesString.add(targetTableName + "[" + join.getTarget() + "]");

                                        added = true;
                                    }
                                }
                                //merged join is needed for a reference mapping
                                else if (mergedMapping.hasReferenceMappingForTable(targetTableName, join.getTarget())) {
                                    // merged join target table is added if it does not exist yet
                                    if (!hasTable(targetTableName)) {
                                        referencedTablesString.add(targetTableName + "[" + join.getTarget() + "]" + "[" + sourceTableName + "]" + "[" + sourceTableName + "]");
                                        referencedTables.add(new MappingTableImpl(mappingTable, join));
                                    } else {
                                        // if it does exist, we have self join, which is added to the target table
                                        if (!getTable(targetTableName).get().getJoinPaths().contains(join) && getTable(targetTableName).get().getTarget().startsWith(join.getTarget())) {
                                            //System.out.println("SELF JOIN " + targetTableName);
                                            getTable(targetTableName).get().addJoinPath(new MappingJoinImpl((MappingJoinImpl) join));
                                        }
                                    }
                                }
                            } else {
                                //System.out.println("IGNORED " + targetTableName);
                            }
                            //}
                        }
                    }
                }
            } while (added);
        }

        //System.out.println(mergedMapping.getName() + " JOINED " + Joiner.on(" | ").join(joinedTablesString));
        //System.out.println(mergedMapping.getName() + " REFERENCED " + Joiner.on(" | ").join(referencedTablesString));

        return referencedTables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureTypeMappingImpl that = (FeatureTypeMappingImpl) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(qualifiedTypeName, that.qualifiedTypeName) &&
                Objects.equals(tables, that.tables) &&
                Objects.equals(joins, that.joins) &&
                Objects.equals(values, that.values) &&
                Objects.equals(associationTargets, that.associationTargets);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, qualifiedTypeName, tables, joins, values, associationTargets);
    }

    @Override
    public String toString() {
        return "\nFeatureTypeMappingImpl{" +
                "\nname='" + name + '\'' +
                "\n, qualifiedTypeName=" + qualifiedTypeName +
                "\n, tables=" + tables +
                "\n, joins=" + joins +
                "\n, values=" + values +
                "\n, associationTargets=" + associationTargets +
                "\n}";
    }
}

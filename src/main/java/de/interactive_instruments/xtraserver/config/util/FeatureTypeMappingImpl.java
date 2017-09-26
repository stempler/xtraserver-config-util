package de.interactive_instruments.xtraserver.config.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import de.interactive_instruments.xtraserver.config.schema.AdditionalMappings;
import de.interactive_instruments.xtraserver.config.schema.FeatureType;
import de.interactive_instruments.xtraserver.config.schema.MappingsSequenceType;
import de.interactive_instruments.xtraserver.config.schema.SQLFeatureTypeImplType;
import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.util.api.MappingTable;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zahnen
 */
public class FeatureTypeMappingImpl implements FeatureTypeMapping {
    private String name;
    private List<MappingTable> tables;
    private List<MappingJoin> joins;
    private List<MappingValue> values;
    private ApplicationSchema applicationSchema;

    public FeatureTypeMappingImpl(FeatureType featureType, ApplicationSchema applicationSchema) {
        this.name = featureType.getName();

        MappingsSequenceType mappings = extractMappings(featureType);
        this.tables = new ArrayList<>();
        extractTables(mappings);
        extractJoins(mappings);
        this.values = extractValues(mappings);
        this.applicationSchema = applicationSchema;
    }

    public FeatureTypeMappingImpl(AdditionalMappings additionalMappings, ApplicationSchema applicationSchema) {
        this.name = additionalMappings.getRootElementName();
        this.tables = new ArrayList<>();
        extractTables(additionalMappings.getMappings());
        extractJoins(additionalMappings.getMappings());
        this.values = extractValues(additionalMappings.getMappings());
        this.applicationSchema = applicationSchema;
    }

    public FeatureTypeMappingImpl(FeatureTypeMapping mainMapping, List<FeatureTypeMapping> mergedMappings, ApplicationSchema applicationSchema) {
        this.name = mainMapping.getName();
        this.tables = mainMapping.getTables();
        this.joins = mainMapping.getJoins();
        this.values = mainMapping.getValues();
        this.applicationSchema = applicationSchema;


        for (FeatureTypeMapping mergedMapping : mergedMappings) {
            // TODO: first merge joins and tables
            List<MappingTable> referencedTables = extractJoinedTables(mergedMapping);

            for (MappingTable mappingTable : referencedTables) {
                if (!hasTable(mappingTable.getName())) {
                    this.tables.add(this.tables.size(), mappingTable);
                }
            }

            for (MappingValue mappingValue : mergedMapping.getValues()) {
                //System.out.println(mappingValue.getTarget());
                if (!values.contains(mappingValue)) {
                    if ((hasTable(mappingValue.getTable()) && hasValueMappingForTable(mappingValue.getTable(), getTable(mappingValue.getTable()).getTarget())) ||
                            (hasTable(mappingValue.getTable()) && mappingValue.getTarget().startsWith(getTable(mappingValue.getTable()).getTarget()))) {
                        this.values.add(this.values.size(), mappingValue);
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public QName getQName() {
        return applicationSchema.getType(name);
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

    private Collection<String> getTableNames(boolean primary, boolean hasValueMapping) {

        return Collections2.transform(
                Collections2.filter(tables,
                        new Predicate<MappingTable>() {
                            @Override
                            public boolean apply(MappingTable table) {
                                return primary ? table.isPrimary() : (!table.isPrimary() && hasValueMapping == hasValueMappingForTable(table.getName()));
                            }
                        }
                ),
                new Function<MappingTable, String>() {
                    @Override
                    public String apply(MappingTable table) {
                        return table.getName();
                        /*String name = table.getName() + "[" + table.getOidCol() + "]";
                        if (!primary)
                            name += "[" + table.getJoinPaths().get(0).toString() + "]";
                        return name;*/
                    }
                }
        );
    }

    @Override
    public boolean hasTable(String name) {
        return tables != null && !Collections2.filter(tables,
                new Predicate<MappingTable>() {
                    @Override
                    public boolean apply(MappingTable table) {
                        return name.equals(table.getName());
                    }
                }
        ).isEmpty();
    }

    @Override
    public MappingTable getTable(String name) {
        if (tables == null) return null;

        return Iterables.find(tables,
                new Predicate<MappingTable>() {
                    @Override
                    public boolean apply(MappingTable table) {
                        return name.equals(table.getName());
                    }
                }
        );
    }

    @Override
    public boolean hasValueMappingForTable(String name) {
        return values != null && !Collections2.filter(values,
                new Predicate<MappingValue>() {
                    @Override
                    public boolean apply(MappingValue value) {
                        return name.equals(value.getTable()) && value.getValue() != null && !value.getValue().isEmpty() && !value.getTarget().endsWith("/@xlink:href");
                    }
                }
        ).isEmpty();
    }

    @Override
    public boolean hasValueMappingForTable(String name, String target) {
        return values != null && !Collections2.filter(values,
                new Predicate<MappingValue>() {
                    @Override
                    public boolean apply(MappingValue value) {
                        return name.equals(value.getTable()) && value.getValue() != null && !value.getValue().isEmpty() && !value.getTarget().endsWith("/@xlink:href") && value.getTarget().startsWith(target);
                    }
                }
        ).isEmpty();
    }

    @Override
    public boolean hasReferenceMappingForTable(String name, String target) {
        return values != null && !Collections2.filter(values,
                new Predicate<MappingValue>() {
                    @Override
                    public boolean apply(MappingValue value) {
                        return name.equals(value.getTable()) && value.getValue() != null && !value.getValue().isEmpty() && value.getTarget().endsWith("/@xlink:href") && value.getTarget().startsWith(target);
                    }
                }
        ).isEmpty();
    }

    private List<MappingTable> extractTables(MappingsSequenceType mappings) {
        List<MappingTable> mappingTables = new ArrayList<>();

        if (mappings != null) {
            for (Object mapping : mappings.getTableOrJoinOrAssociationTarget()) {
                try {
                    MappingsSequenceType.Table table = (MappingsSequenceType.Table) mapping;

                    if (table.getTable_Name() != null && !table.getTable_Name().isEmpty()) {
                        if (table.getOid_Col() != null && !table.getOid_Col().isEmpty()) {
                            //if (table.getValue4() == null || table.getValue4().isEmpty()) {
                            if (!hasTable(table.getTable_Name())) {
                                tables.add(new MappingTableImpl(table));
                            } else {
                                MappingTable mappingTable = getTable(table.getTable_Name());
                                String newTarget = Strings.commonPrefix(table.getTarget(), mappingTable.getTarget());
                                mappingTable.setTarget(newTarget);
                            }
                        }
                    }
                } catch (ClassCastException e) {

                }
            }
        }

        return mappingTables;
    }

    private List<MappingValue> extractValues(MappingsSequenceType mappings) {
        List<MappingValue> mappingValues = new ArrayList<>();

        if (mappings != null) {
            for (Object mapping : mappings.getTableOrJoinOrAssociationTarget()) {
                try {
                    MappingsSequenceType.Table table = (MappingsSequenceType.Table) mapping;

                    if (table.getTable_Name() != null && !table.getTable_Name().isEmpty() && hasTable(table.getTable_Name())) {
                        if (table.getTarget() != null && !table.getTarget().isEmpty() &&
                                table.getTarget().startsWith(getTable(table.getTable_Name()).getTarget())) {
                            if (table.getValue4() != null && !table.getValue4().isEmpty() && table.getUse_Geotypes() == null && table.isMapped_Geometry() == null) {
                                Namespaces namespaces = applicationSchema != null ? applicationSchema.getNamespaces() : new Namespaces();
                                MappingValue mappingValue = new MappingValueImpl(table, namespaces);
                                if (!mappingValues.contains(mappingValue)) {
                                    mappingValues.add(mappingValue);
                                }
                            }
                        }
                    }
                } catch (ClassCastException e) {

                }
            }
        }

        return mappingValues;
    }

    private void extractJoins(MappingsSequenceType mappings) {

        boolean disableMultiJoins = true;

        if (mappings != null) {
            for (Object mapping : mappings.getTableOrJoinOrAssociationTarget()) {
                try {
                    MappingsSequenceType.Join join = (MappingsSequenceType.Join) mapping;

                    if (join.getJoin_Path() != null && !join.getJoin_Path().isEmpty()) {
                        String table = join.getJoin_Path().split("/")[0];
                        if (table.contains("[")) {
                            System.out.println("JOIN PREDICATE " + join.getJoin_Path());
                            table = table.substring(0, table.indexOf("["));
                        }
                        if (hasTable(table) && (!getTable(table).hasTarget() || getTable(table).getTarget().startsWith(join.getTarget()))) {
                            MappingJoin mappingJoin = new MappingJoinImpl(join);
                            if (!disableMultiJoins || mappingJoin.getJoinConditions().size() == 1) {
                                getTable(table).addJoinPath(mappingJoin);
                            }
                        }
                    }
                } catch (ClassCastException e) {
                    // ignore
                }
            }
        }
    }

    private List<MappingTable> extractJoinedTables(FeatureTypeMapping mergedMapping) {
        List<String> joinedTables = new ArrayList<>();
        List<String> joinedTables2 = new ArrayList<>();
        List<MappingTable> referencedTables = new ArrayList<>();

        if (mergedMapping != null && mergedMapping.getTables() != null) {
            boolean added;
            do {
                added = false;
                for (MappingTable mappingTable : mergedMapping.getTables()) {
                    for (MappingJoin join : mappingTable.getJoinPaths()) {
                        if (mappingTable.getName().equals("o51009")) {
                            System.out.println("FOO " + join.toString() + " " + join.getTarget());
                            System.out.println("BAR " + join.getJoinConditions().get(0).getSourceTable() + (hasTable(join.getJoinConditions().get(0).getSourceTable()) ? join.getTarget().startsWith(getTable(join.getJoinConditions().get(0).getSourceTable()).getTarget()) : "NOPE"));
                        }
                        if (!join.getJoinConditions().isEmpty()
                                && (/*joinedTables.contains(join.getJoinConditions().get(0).getSourceTable()) ||*/ hasTable(join.getJoinConditions().get(0).getSourceTable()) && join.getTarget().startsWith(getTable(join.getJoinConditions().get(0).getSourceTable()).getTarget()))) {
                            // for (MappingJoin.Condition condition : join.getJoinConditions()) {
                            //     String targetTableName = condition.getTargetTable();
                            String targetTableName = join.getJoinConditions().get(join.getJoinConditions().size() - 1).getTargetTable();
                            if (targetTableName.equals("o51009")) {
                                System.out.println("BLA " + targetTableName + " " + mergedMapping.hasTable(targetTableName) + " " + hasTable(targetTableName));
                            }
                            if (mergedMapping.hasTable(targetTableName)) {
                                if (/*mergedMapping.getTable(targetTableName).hasTarget() ||*/ mergedMapping.hasValueMappingForTable(targetTableName, join.getTarget())) {
                                    //if (!joinedTables.contains(targetTableName)) {

                                        /*for (MappingTable mappingTable : mergedMapping.getTables()) {
                                            if (joinedTables.contains(mappingTable.getName()) && !hasTable(mappingTable.getName())) {
                                                this.tables.add(this.tables.size(), mappingTable);
                                            }
                                        }*/
                                    if (!hasTable(targetTableName)) {
                                        this.tables.add(new MappingTableImpl(mergedMapping.getTable(targetTableName), join.getTarget()));
                                        joinedTables2.add(targetTableName + "[" + join.getTarget() + "]");

                                        //joinedTables.add(targetTableName);
                                        added = true;
                                    }
                                } else if (mergedMapping.hasReferenceMappingForTable(targetTableName, join.getTarget())) {
                                    if (!hasTable(targetTableName)) {
                                        joinedTables.add(targetTableName + "[" + join.getTarget() + "]" + "[" + join.getJoinConditions().get(0).getSourceTable() + "]" + "[" + join.getJoinConditions().get(0).getSourceTable() + "]");
                                        referencedTables.add(new MappingTableImpl(mappingTable, join));
                                    } else {
                                        // self join
                                        if (!getTable(targetTableName).getJoinPaths().contains(join)) {
                                            System.out.println("SELF JOIN " + targetTableName);
                                            getTable(targetTableName).addJoinPath(join);
                                        }
                                    }
                                }
                            } else {
                                System.out.println("IGNORED " + targetTableName);
                            }
                            //}
                        }
                    }
                }
            } while (added);
        }

        System.out.println(mergedMapping.getName() + " JOINED " + Joiner.on(" | ").join(joinedTables2));
        System.out.println(mergedMapping.getName() + " REFERENCED " + Joiner.on(" | ").join(joinedTables));

        return referencedTables;
    }

    private MappingsSequenceType extractMappings(FeatureType featureType) {
        MappingsSequenceType mappings = null;

        if (featureType.getPGISFeatureTypeImpl() != null) {
            mappings = featureType.getPGISFeatureTypeImpl();
        } else if (featureType.getOraSFeatureTypeImpl() != null) {
            mappings = featureType.getOraSFeatureTypeImpl();
        } else if (featureType.getGDBSQLFeatureTypeImpl() != null) {
            mappings = featureType.getGDBSQLFeatureTypeImpl();
        }

        return mappings;
    }
}

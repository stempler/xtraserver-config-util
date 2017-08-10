package de.interactive_instruments.xtraserver.config.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import de.interactive_instruments.xtraserver.config.schema.FeatureType;
import de.interactive_instruments.xtraserver.config.schema.MappingsSequenceType;
import de.interactive_instruments.xtraserver.config.schema.SQLFeatureTypeImplType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zahnen
 */
public class FeatureTypeMapping {
    private String name;
    private List<MappingTable> tables;
    private List<MappingJoin> joins;
    private List<MappingValue> values;

    public FeatureTypeMapping(FeatureType featureType) {
        this.name = featureType.getName();
        this.tables = extractTables(featureType);
        extractJoins(featureType);
        this.values = extractValues(featureType);
    }

    public String getName() {
        return name;
    }

    public Collection<String> getPrimaryTableNames() {

        return getTableNames(true);
    }

    public Collection<String> getJoinedTableNames() {

        return getTableNames(false);
    }

    public List<MappingValue> getValues() {
        return values;
    }

    private Collection<String> getTableNames(boolean primary) {

        return Collections2.transform(
                Collections2.filter(tables,
                        new Predicate<MappingTable>() {
                            @Override
                            public boolean apply(MappingTable table) {
                                return primary == table.isPrimary();
                            }
                        }
                ),
                new Function<MappingTable, String>() {
                    @Override
                    public String apply(MappingTable table) {
                        String name = table.getName() + "[" + table.getOidCol() + "]";
                        if (!primary)
                            name += "[" + table.getJoinPath() + "]";
                        return name;
                    }
                }
        );
    }

    private boolean hasTable(String name) {
        return tables != null && !Collections2.filter(tables,
                new Predicate<MappingTable>() {
                    @Override
                    public boolean apply(MappingTable table) {
                        return name.equals(table.getName());
                    }
                }
        ).isEmpty();
    }

    private MappingTable getTable(String name) {
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

    private List<MappingTable> extractTables(FeatureType featureType) {
        List<MappingTable> mappingTables = new ArrayList<>();
        SQLFeatureTypeImplType mappings = extractMappings(featureType);

        if (mappings != null) {
            for (Object mapping : mappings.getTableOrJoinOrAssociationTarget()) {
                try {
                    MappingsSequenceType.Table table = (MappingsSequenceType.Table) mapping;

                    if (table.getTable_Name() != null && !table.getTable_Name().isEmpty() && !hasTable(table.getTable_Name())) {
                        if (table.getOid_Col() != null && !table.getOid_Col().isEmpty()) {
                            if (table.getValue4() == null || table.getValue4().isEmpty()) {
                                mappingTables.add(new MappingTable(table));
                            }
                        }
                    }
                } catch (ClassCastException e) {

                }
            }
        }

        return mappingTables;
    }

    private List<MappingValue> extractValues(FeatureType featureType) {
        List<MappingValue> mappingValues = new ArrayList<>();
        SQLFeatureTypeImplType mappings = extractMappings(featureType);

        if (mappings != null) {
            for (Object mapping : mappings.getTableOrJoinOrAssociationTarget()) {
                try {
                    MappingsSequenceType.Table table = (MappingsSequenceType.Table) mapping;

                    if (table.getTable_Name() != null && !table.getTable_Name().isEmpty() && hasTable(table.getTable_Name())) {
                        if (table.getTarget() != null && !table.getTarget().isEmpty()) {
                            if (table.getValue4() != null && !table.getValue4().isEmpty()) {
                                mappingValues.add(new MappingValue(table));
                            }
                        }
                    }
                } catch (ClassCastException e) {

                }
            }
        }

        return mappingValues;
    }

    private void extractJoins(FeatureType featureType) {
        SQLFeatureTypeImplType mappings = extractMappings(featureType);

        if (mappings != null) {
            for (Object mapping : mappings.getTableOrJoinOrAssociationTarget()) {
                try {
                    MappingsSequenceType.Join join = (MappingsSequenceType.Join) mapping;

                    if (join.getJoin_Path() != null && !join.getJoin_Path().isEmpty()) {
                        String table = join.getJoin_Path().split("/")[0];
                        if (hasTable(table) && getTable(table).getTarget().equals(join.getTarget()))  {
                            getTable(table).setJoinPath(join.getJoin_Path());
                        }
                    }
                } catch (ClassCastException e) {

                }
            }
        }
    }

    private SQLFeatureTypeImplType extractMappings(FeatureType featureType) {
        SQLFeatureTypeImplType mappings = null;

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

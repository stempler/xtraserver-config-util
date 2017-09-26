package de.interactive_instruments.xtraserver.config.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import de.interactive_instruments.xtraserver.config.schema.AdditionalMappings;
import de.interactive_instruments.xtraserver.config.schema.FeatureType;
import de.interactive_instruments.xtraserver.config.schema.FeatureTypes;
import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.util.api.MappingTable;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;

import java.util.*;

/**
 * @author zahnen
 */
public class XtraServerMappingImpl implements de.interactive_instruments.xtraserver.config.util.api.XtraServerMapping {
    private final ApplicationSchema applicationSchema;
    private List<FeatureTypeMapping> featureTypeMappings;
    private List<FeatureTypeMapping> additionalMappings;

    public XtraServerMappingImpl(FeatureTypes featureTypes, ApplicationSchema applicationSchema) {
        this.featureTypeMappings = new ArrayList<>();
        this.additionalMappings = new ArrayList<>();
        this.applicationSchema = applicationSchema;

        for (Object a : featureTypes.getFeatureTypeOrAdditionalMappings()) {
            try {
                FeatureType ft = (FeatureType) a;

                FeatureTypeMapping ftm = new FeatureTypeMappingImpl(ft, applicationSchema);

                featureTypeMappings.add(ftm);

            } catch (ClassCastException e) {
                try {
                    AdditionalMappings am = (AdditionalMappings) a;

                    FeatureTypeMapping ftm = new FeatureTypeMappingImpl(am, applicationSchema);

                    additionalMappings.add(ftm);

                } catch (ClassCastException e2) {
                    // ignore
                }
            }
        }
    }

    @Override
    public boolean hasFeatureType(String featureType) {

        return !Collections2.filter(featureTypeMappings,
                new Predicate<FeatureTypeMapping>() {
                    @Override
                    public boolean apply(FeatureTypeMapping ftm) {
                        return featureType.equals(ftm.getName());
                    }
                }
        ).isEmpty();
    }

    @Override
    public boolean hasType(String type) {

        return hasFeatureType(type) || !Collections2.filter(additionalMappings,
                new Predicate<FeatureTypeMapping>() {
                    @Override
                    public boolean apply(FeatureTypeMapping ftm) {
                        return type.equals(ftm.getName());
                    }
                }
        ).isEmpty();
    }

    @Override
    public FeatureTypeMapping getFeatureTypeMapping(String featureType, boolean flattenInheritance) {
        FeatureTypeMapping featureTypeMapping = Iterables.find(featureTypeMappings,
                new Predicate<FeatureTypeMapping>() {
                    @Override
                    public boolean apply(FeatureTypeMapping ftm) {
                        return featureType.equals(ftm.getName());
                    }
                }
        );

        if (flattenInheritance) {
            List<String> parents = applicationSchema.getAllParents(featureTypeMapping.getName());
            List<FeatureTypeMapping> parentMappings = new ArrayList<>();
            for (String parent : parents) {
                if (hasType(parent)) {
                    parentMappings.add(getTypeMapping(parent));
                }
            }

            featureTypeMapping = new FeatureTypeMappingImpl(featureTypeMapping, parentMappings, applicationSchema);
        }

        return featureTypeMapping;
    }

    private FeatureTypeMapping getTypeMapping(String type) {
        return getTypeMapping(type, false);
    }

    private FeatureTypeMapping getTypeMapping(String type, boolean flattenInheritance) {
        FeatureTypeMapping featureTypeMapping = null;

        try {
            featureTypeMapping = getFeatureTypeMapping(type, flattenInheritance);
        } catch (NoSuchElementException e) {
            try {
                featureTypeMapping = Iterables.find(additionalMappings,
                        new Predicate<FeatureTypeMapping>() {
                            @Override
                            public boolean apply(FeatureTypeMapping ftm) {
                                return type.equals(ftm.getName());
                            }
                        }
                );

                if (flattenInheritance) {
                    List<String> parents = applicationSchema.getAllParents(featureTypeMapping.getName());
                    List<FeatureTypeMapping> parentMappings = new ArrayList<>();
                    for (String parent : parents) {
                        if (hasType(parent)) {
                            parentMappings.add(getTypeMapping(parent));
                        }
                    }

                    featureTypeMapping = new FeatureTypeMappingImpl(featureTypeMapping, parentMappings, applicationSchema);
                }

            } catch (NoSuchElementException e2) {
                // ignore
            }
        }

        return featureTypeMapping;
    }

    @Override
    public Collection<String> getFeatureTypeList() {
        return getFeatureTypeList(true);
    }

    @Override
    public Collection<String> getFeatureTypeList(boolean includeAbstract) {
        return Collections2.transform(
                Collections2.filter(featureTypeMappings,
                        new Predicate<FeatureTypeMapping>() {
                            @Override
                            public boolean apply(FeatureTypeMapping ftm) {
                                return includeAbstract || !applicationSchema.isAbstract(ftm.getName());
                            }
                        }
                ),
                new Function<FeatureTypeMapping, String>() {
                    @Override
                    public String apply(FeatureTypeMapping ftm) {
                        return ftm.getName();
                    }
                }
        );
    }

    public Collection<String> getTypeList(boolean includeAbstract) {
        Set<String> typeList = new HashSet<>();

        typeList.addAll(getFeatureTypeList(includeAbstract));

        typeList.addAll(Collections2.transform(
                Collections2.filter(additionalMappings,
                        new Predicate<FeatureTypeMapping>() {
                            @Override
                            public boolean apply(FeatureTypeMapping ftm) {
                                return includeAbstract || !applicationSchema.isAbstract(ftm.getName());
                            }
                        }
                ),
                new Function<FeatureTypeMapping, String>() {
                    @Override
                    public String apply(FeatureTypeMapping ftm) {
                        return ftm.getName();
                    }
                }
        ));

        return typeList;
    }

    public void print() {
        System.out.println("FeatureTypes:" + featureTypeMappings.size());
        System.out.println("FeatureTypes:" + getTypeList(false));
        System.out.println("FeatureTypes:" + getTypeList(true));
        for (String ft : getTypeList(true)) {
            if (hasType(ft) && (ft.endsWith("adv:AX_Leitung"))) {
                FeatureTypeMapping ftm = getTypeMapping(ft, true);
                String name = ftm.getName();
                String name2 = name;
                if (applicationSchema.isAbstract(name))
                    name2 += "[abstract]";
                //if (applicationSchema.getParent(name) != null && hasFeatureType(name))
                //    name2 += "[" + applicationSchema.getParent(name) + "]";
                if (!applicationSchema.getAllParents(name).isEmpty())
                    name2 += "[" + String.join(",", applicationSchema.getAllParents(name)) + "]";
                System.out.println("Name: " + name2 + "\n");

                Collection<String> rootTables = getDecoratedTableNames(ftm.getPrimaryTableNames(), ftm);
                Collection<String> joinedTables = getDecoratedTableNames(ftm.getJoinedTableNames(), ftm);
                Collection<String> referenceTables = getDecoratedTableNames(ftm.getReferenceTableNames(), ftm);

                Collection<String> multiJoins = Collections2.transform(
                        Collections2.filter(ftm.getTables(),
                                new Predicate<MappingTable>() {
                                    @Override
                                    public boolean apply(MappingTable table) {
                                        return Collections2.filter(table.getJoinPaths(),
                                                    new Predicate<MappingJoin>() {
                                                        @Override
                                                        public boolean apply(MappingJoin join) {
                                                            return join.getJoinConditions().size() > 1;
                                                        }
                                                    }
                                            ).size() > 0;
                                    }
                                }
                        ),
                        new Function<MappingTable, String>() {
                            @Override
                            public String apply(MappingTable table) {
                                return Joiner.on("\n").join(Collections2.filter(table.getJoinPaths(),
                                        new Predicate<MappingJoin>() {
                                            @Override
                                            public boolean apply(MappingJoin join) {
                                                return join.getJoinConditions().size() > 1;
                                            }
                                        }
                                ));
                            }
                        }
                );

                System.out.println("  Root Tables: \n" + Joiner.on("\n").join(rootTables) + "\n");
                System.out.println("  Self Joins: \n" + Joiner.on("\n").join(ftm.getTable((String)ftm.getPrimaryTableNames().toArray()[0]).getJoinPaths()) + "\n");
                System.out.println("  Multi Joins: \n" + Joiner.on("\n").join(multiJoins) + "\n");
                System.out.println("  Joined Value Tables: \n" + Joiner.on("\n").join(joinedTables) + "\n");
                System.out.println("  Joined Reference Tables: \n" + Joiner.on("\n").join(referenceTables) + "\n");

                for (MappingValue mv : ftm.getValues()) {
                    try {
                        System.out.println("  Table: " + mv.getTable());
                        System.out.println("  Target: " + mv.getTarget());
                        System.out.println("  Value: " + mv.getValue());
                        System.out.println("  ValueType: " + mv.getValueType() + "\n");
                    } catch (ClassCastException e) {

                    }
                }
            }
        }
    }

    public Collection<String> getDecoratedTableNames(Collection<String> tableNames, FeatureTypeMapping ftm) {
        return Collections2.transform(tableNames, new Function<String, String>() {
            @Override
            public String apply(String tableName) {
                MappingTable table = ftm.getTable(tableName);
                String name = table.getName() + "[" + table.getOidCol() + "]";
                if (!table.isPrimary())
                    name += "[" + table.getJoinPaths().get(0).toString() + "]";
                name += "[" + table.getTarget() + "]";
                return name;
            }
        });
    }
}

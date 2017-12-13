package de.interactive_instruments.xtraserver.config.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import de.interactive_instruments.xtraserver.config.schema.*;
import de.interactive_instruments.xtraserver.config.util.api.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
public class XtraServerMappingImpl implements XtraServerMapping {
    private ApplicationSchema applicationSchema;
    private final List<FeatureTypeMapping> featureTypeMappings;
    private final List<FeatureTypeMapping> additionalMappings;

    public XtraServerMappingImpl() {
        this.featureTypeMappings = new ArrayList<>();
        this.additionalMappings = new ArrayList<>();
    }

    public XtraServerMappingImpl(ApplicationSchema applicationSchema) {
        this();
        this.applicationSchema = applicationSchema;
    }

    /*public XtraServerMappingImpl(FeatureTypes featureTypes, ApplicationSchema applicationSchema) {
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
    }*/

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

            featureTypeMapping = new FeatureTypeMappingImpl(featureTypeMapping, parentMappings, applicationSchema.getNamespaces());
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

                    featureTypeMapping = new FeatureTypeMappingImpl(featureTypeMapping, parentMappings, applicationSchema.getNamespaces());
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

    @Override
    public void addFeatureTypeMapping(FeatureTypeMapping featureTypeMapping) {
        featureTypeMappings.add(featureTypeMapping);
    }

    @Override
    public void writeToStream(OutputStream outputStream) throws IOException, JAXBException, SAXException {
        MappingParser.marshal(outputStream, this.toJaxb());
    }

    private FeatureTypes toJaxb() {
        ObjectFactory objectFactory = new ObjectFactory();
        FeatureTypes featureTypes = objectFactory.createFeatureTypes();

        featureTypes.getFeatureTypeOrAdditionalMappings().addAll(
                additionalMappings.stream().map(additionalMapping -> {
                    MappingsSequenceType mappingsSequenceType = objectFactory.createMappingsSequenceType();

                    mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
                      additionalMapping.getTables().stream().map(mappingTable -> {
                          MappingsSequenceType.Table table = objectFactory.createMappingsSequenceTypeTable();
                          table.setTable_Name(mappingTable.getName());
                          table.setOid_Col(mappingTable.getOidCol());
                          return table;
                      }).collect(Collectors.toList())
                    );

                    AdditionalMappings jaxbAdditionalMappings = objectFactory.createAdditionalMappings();
                    jaxbAdditionalMappings.setRootElementName(additionalMapping.getName());
                    jaxbAdditionalMappings.setMappings(mappingsSequenceType);

                    return jaxbAdditionalMappings;
                }).collect(Collectors.toList())
        );

        featureTypes.getFeatureTypeOrAdditionalMappings().addAll(
                featureTypeMappings.stream().map(featureTypeMapping -> {
                    SQLFeatureTypeImplType sqlFeatureTypeImplType = objectFactory.createSQLFeatureTypeImplType();

                    featureTypeMapping.getTables().forEach(mappingTable -> {
                        sqlFeatureTypeImplType.getTableOrJoinOrAssociationTarget().addAll(
                                mappingTable.getJoinPaths().stream().map(mappingJoin -> {
                                    MappingsSequenceType.Join join = objectFactory.createMappingsSequenceTypeJoin();
                                    join.setAxis(mappingJoin.getAxis());
                                    join.setTarget(mappingJoin.getTarget());
                                    join.setJoin_Path(mappingJoin.getPath());
                                    return join;
                                }).collect(Collectors.toList())
                        );

                        MappingsSequenceType.Table table = objectFactory.createMappingsSequenceTypeTable();
                        table.setTable_Name(mappingTable.getName());
                        table.setOid_Col(mappingTable.getOidCol());
                        table.setTarget(mappingTable.getTarget());

                        sqlFeatureTypeImplType.getTableOrJoinOrAssociationTarget().add(table);

                        sqlFeatureTypeImplType.getTableOrJoinOrAssociationTarget().addAll(
                                mappingTable.getValues().stream().map(mappingValue -> {
                                    MappingsSequenceType.Table value = objectFactory.createMappingsSequenceTypeTable();
                                    value.setTable_Name(mappingValue.getTable());
                                    value.setTarget(mappingValue.getTarget());
                                    value.setValue4(mappingValue.getValue());
                                    value.setValue_Type(mappingValue.getValueType());
                                    value.setMapping_Mode(mappingValue.getMappingMode());
                                    value.setDb_Codes(mappingValue.getDbCodes());
                                    value.setSchema_Codes(mappingValue.getDbValues());
                                    return value;
                                }).collect(Collectors.toList())
                        );
                    });
                    /*sqlFeatureTypeImplType.getTableOrJoinOrAssociationTarget().addAll(
                            featureTypeMapping.getTables().stream().map(mappingTable -> {
                                MappingsSequenceType.Table table = objectFactory.createMappingsSequenceTypeTable();
                                table.setTable_Name(mappingTable.getName());
                                table.setOid_Col(mappingTable.getOidCol());
                                table.setTarget(mappingTable.getTarget());
                                return table;
                            }).collect(Collectors.toList())
                    );*/



                    FeatureType featureType = objectFactory.createFeatureType();
                    featureType.setName(featureTypeMapping.getName());
                    featureType.setPGISFeatureTypeImpl(sqlFeatureTypeImplType);

                    return featureType;
                }).collect(Collectors.toList())
        );

        return featureTypes;
    }

    private Collection<String> getTypeList(boolean includeAbstract) {
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

    void print() {
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
                System.out.println("  Self Joins: \n" + Joiner.on("\n").join(ftm.getTable((String) ftm.getPrimaryTableNames().toArray()[0]).getJoinPaths()) + "\n");
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

    private Collection<String> getDecoratedTableNames(Collection<String> tableNames, FeatureTypeMapping ftm) {
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

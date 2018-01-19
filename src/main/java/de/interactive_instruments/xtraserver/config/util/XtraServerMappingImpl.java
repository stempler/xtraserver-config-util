package de.interactive_instruments.xtraserver.config.util;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import de.interactive_instruments.xtraserver.config.util.api.*;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
public class XtraServerMappingImpl implements XtraServerMapping {
    private final ApplicationSchema applicationSchema;
    private final List<FeatureTypeMapping> featureTypeMappings;
    private final List<FeatureTypeMapping> additionalMappings;

    public XtraServerMappingImpl(ApplicationSchema applicationSchema) {
        this.featureTypeMappings = new ArrayList<>();
        this.additionalMappings = new ArrayList<>();
        this.applicationSchema = applicationSchema;
    }

    List<FeatureTypeMapping> getFeatureTypeMappings() {
        return featureTypeMappings;
    }

    List<FeatureTypeMapping> getAdditionalMappings() {
        return additionalMappings;
    }

    private Collection<String> getTypeNames(Collection<FeatureTypeMapping> typeList, boolean includeAbstract) {
        return typeList.stream()
                .filter(
                        featureTypeMapping -> includeAbstract || !applicationSchema.isAbstract(featureTypeMapping.getName())
                )
                .map(FeatureTypeMapping::getName)
                .collect(Collectors.toList());
    }

    private Optional<FeatureTypeMapping> getTypeMappingOptional(Collection<FeatureTypeMapping> typeList, String typeName) {
        return getTypeMappingOptional(typeList, typeName, false);
    }

    private Optional<FeatureTypeMapping> getTypeMappingOptional(Collection<FeatureTypeMapping> typeList, String typeName, boolean flattenInheritance) {
        Optional<FeatureTypeMapping> featureTypeMappingOptional = typeList.stream()
                .filter(
                        featureTypeMapping -> typeName.equals(featureTypeMapping.getName())
                ).findFirst();

        if (featureTypeMappingOptional.isPresent() && flattenInheritance) {
            List<String> parents = applicationSchema.getAllParents(featureTypeMappingOptional.get().getName());
            List<FeatureTypeMapping> parentMappings = parents.stream().filter(this::hasType).map(this::getTypeMapping).map(Optional::get).collect(Collectors.toList());
            featureTypeMappingOptional = Optional.of(new FeatureTypeMappingImpl(featureTypeMappingOptional.get(), parentMappings, applicationSchema.getNamespaces()));
        }

        return featureTypeMappingOptional;
    }

    @Override
    public boolean hasFeatureType(String featureType) {
        return getTypeMappingOptional(featureTypeMappings, featureType).isPresent();
    }

    @Override
    public boolean hasType(String type) {
        return hasFeatureType(type) || hasAbstractType(type);
    }

    private boolean hasAbstractType(String type) {
        return getTypeMappingOptional(additionalMappings, type).isPresent();
    }

    @Override
    public Optional<FeatureTypeMapping> getFeatureTypeMapping(String featureType, boolean flattenInheritance) {
        return getTypeMappingOptional(featureTypeMappings, featureType, flattenInheritance);
    }

    private Optional<FeatureTypeMapping> getTypeMapping(String type) {
        return getTypeMapping(type, false);
    }

    private Optional<FeatureTypeMapping> getTypeMapping(String type, boolean flattenInheritance) {
        Optional<FeatureTypeMapping> optionalFeatureTypeMapping = getTypeMappingOptional(featureTypeMappings, type, flattenInheritance);

        if (!optionalFeatureTypeMapping.isPresent()) {
            optionalFeatureTypeMapping = getTypeMappingOptional(additionalMappings, type, flattenInheritance);
        }

        return optionalFeatureTypeMapping;
    }

    @Override
    public Collection<String> getFeatureTypeList() {
        return getFeatureTypeList(true);
    }

    @Override
    public Collection<String> getFeatureTypeList(boolean includeAbstract) {
        return getTypeNames(featureTypeMappings, includeAbstract);
    }

    private Collection<String> getTypeList(boolean includeAbstract) {
        Set<String> typeList = new HashSet<>();

        typeList.addAll(getFeatureTypeList(includeAbstract));
        typeList.addAll(getTypeNames(additionalMappings, includeAbstract));

        return typeList;
    }

    @Override
    public void addFeatureTypeMapping(FeatureTypeMapping featureTypeMapping) {
        addFeatureTypeMapping(featureTypeMapping, false);
    }

    @Override
    public void addFeatureTypeMapping(FeatureTypeMapping featureTypeMapping, boolean fanOutInheritance) {
        if (hasFeatureType(featureTypeMapping.getName())) {
            throw new IllegalArgumentException("A mapping for FeatureType '" + featureTypeMapping.getName() + "' does already exist");
        }

        if (fanOutInheritance) {
            //List<XmlSchemaComplexType> types = applicationSchema.getAllTypes(featureTypeMapping.getQName());
            List<XmlSchemaElement> types = applicationSchema.getAllElements(featureTypeMapping.getQName());
            System.out.println("FeatureTypes:" + types);

            //for (XmlSchemaComplexType type : types) {
            for (XmlSchemaElement element : types) {
                if (element.getQName().getLocalPart().equals("AbstractFeature")) continue;

                XmlSchemaComplexType type = (XmlSchemaComplexType) element.getSchemaType();

                //String typeName = applicationSchema.getNamespaces().getPrefixedName(type.getQName());
                //typeName = typeName.replace("AbstractGMLType", "AbstractFeatureType");
                String typeName = applicationSchema.getNamespaces().getPrefixedName(element.getQName());
                typeName = typeName.replace("AbstractGML", "AbstractFeature");
                FeatureTypeMapping featureTypeMapping1 = getTypeMappingOptional(element.isAbstract() ? additionalMappings : featureTypeMappings, typeName).orElse(new FeatureTypeMappingImpl(typeName, type.getQName(), applicationSchema.getNamespaces()));

                for (MappingTable mappingTable : featureTypeMapping.getTables()) {
                    if (mappingTable.getTarget() != null && !mappingTable.getTarget().isEmpty()) {
                        QName property = applicationSchema.getNamespaces().getQualifiedName(mappingTable.getTarget().split("/")[0]);
                        if (property != null && applicationSchema.hasProperty(type, property)) {
                            System.out.println("Property:" + mappingTable.getTarget() + " || " + type.getName());
                            featureTypeMapping1.addTable(mappingTable);
                            mappingTable.getValues().forEach(featureTypeMapping1::addValue);
                        }
                    } else {
                        MappingTable mappingTable1 = new MappingTableImpl();
                        mappingTable1.setOidCol(mappingTable.getOidCol());
                        mappingTable1.setName(mappingTable.getName());
                        featureTypeMapping1.addTable(mappingTable1);

                        for (MappingValue mappingValue : mappingTable.getValues()) {
                            if (mappingValue.getTarget() != null && !mappingValue.getTarget().isEmpty()) {
                                QName property = applicationSchema.getNamespaces().getQualifiedName(mappingValue.getTarget().split("/")[0]);
                                if (property != null && applicationSchema.hasProperty(type, property)) {
                                    System.out.println("Property2:" + mappingValue.getTarget() + " || " + type.getName());
                                    mappingTable1.getValues().add(mappingValue);
                                    featureTypeMapping1.addValue(mappingValue);
                                }
                            }
                        }
                    }
                }
                for (AssociationTarget associationTarget : featureTypeMapping.getAssociationTargets()) {
                    if (associationTarget.getTarget() != null && !associationTarget.getTarget().isEmpty()) {
                        QName property = applicationSchema.getNamespaces().getQualifiedName(associationTarget.getTarget().split("/")[0]);
                        if (property != null && applicationSchema.hasProperty(type, property)) {
                            System.out.println("AssociationTarget:" + associationTarget.getTarget() + " || " + type.getName());
                            featureTypeMapping1.addAssociationTarget(associationTarget);
                        }
                    }
                }
                if (element.isAbstract()) {
                    if (!hasAbstractType(typeName)) {
                        additionalMappings.add(featureTypeMapping1);
                    }
                }
                else if (!hasFeatureType(typeName)) {
                    featureTypeMappings.add(featureTypeMapping1);
                }
            }
        } else {
            featureTypeMappings.add(featureTypeMapping);
        }

    }

    @Override
    public void writeToStream(OutputStream outputStream, boolean createArchiveWithAdditionalFiles) throws IOException, JAXBException, SAXException {
        JaxbReaderWriter.writeToStream(outputStream, this);
    }

    void print() {
        System.out.println("FeatureTypes:" + featureTypeMappings.size());
        System.out.println("FeatureTypes:" + getTypeList(false));
        System.out.println("FeatureTypes:" + getTypeList(true));
        for (String ft : getTypeList(true)) {
            if (hasType(ft) && (ft.endsWith("adv:AX_Leitung"))) {
                FeatureTypeMapping ftm = getTypeMapping(ft, true).get();
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
                                table -> Collections2.filter(table.getJoinPaths(),
                                        join -> join.getJoinConditions().size() > 1
                                ).size() > 0
                        ),
                        table -> Joiner.on("\n").join(Collections2.filter(table.getJoinPaths(),
                                (Predicate<MappingJoin>) join -> join.getJoinConditions().size() > 1
                        ))
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
        return Collections2.transform(tableNames, tableName -> {
            MappingTable table = ftm.getTable(tableName);
            String name = table.getName() + "[" + table.getOidCol() + "]";
            if (!table.isPrimary())
                name += "[" + table.getJoinPaths().get(0).toString() + "]";
            name += "[" + table.getTarget() + "]";
            return name;
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XtraServerMappingImpl that = (XtraServerMappingImpl) o;
        return Objects.equals(featureTypeMappings, that.featureTypeMappings) &&
                Objects.equals(additionalMappings, that.additionalMappings);
    }

    @Override
    public int hashCode() {

        return Objects.hash(featureTypeMappings, additionalMappings);
    }

    @Override
    public String toString() {
        return "\nXtraServerMappingImpl{" +
                "\nfeatureTypeMappings=" + featureTypeMappings +
                "\n, additionalMappings=" + additionalMappings +
                "\n}";
    }
}

/**
 * Copyright 2018 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.io;

import com.google.common.base.Joiner;
import com.google.common.io.Resources;
import de.interactive_instruments.xtraserver.config.schema.*;
import de.interactive_instruments.xtraserver.config.util.api.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Helper methods for JAXB marshalling
 */
public class JaxbWriter {

    private static final String MAPPING_FILE = "XtraSrvConfig_Mapping.inc.xml";
    private static final String GML_ABSTRACT_FEATURE = "gml:AbstractFeature";

    private final XtraServerMapping xtraServerMapping;
    private final ObjectFactory objectFactory;

    public JaxbWriter(XtraServerMapping xtraServerMapping) {
        this.xtraServerMapping = xtraServerMapping;
        this.objectFactory = new ObjectFactory();
    }

    public void writeToStream(final OutputStream outputStream, final boolean createArchiveWithAdditionalFiles) throws IOException, JAXBException, SAXException, XMLStreamException {
        final FeatureTypes featureTypes = objectFactory.createFeatureTypes();

        featureTypes.getFeatureTypeOrAdditionalMappings().addAll(createFeatureTypes());
        featureTypes.getFeatureTypeOrAdditionalMappings().addAll(createAdditionalMappings());

        if (createArchiveWithAdditionalFiles) {

            ZipOutputStream zipStream = new ZipOutputStream(outputStream);

            zipStream.putNextEntry(new ZipEntry(MAPPING_FILE));
            marshal(zipStream, featureTypes);

            new AdditionalFilesWriter().generate(zipStream, xtraServerMapping);

            zipStream.close();

        } else {
            marshal(outputStream, featureTypes);
            outputStream.close();
        }
    }

    private List<FeatureType> createFeatureTypes() {
        final List<FeatureType> featureTypes = xtraServerMapping.getFeatureTypeMappings().stream()
                .filter(featureTypeMapping -> !featureTypeMapping.isAbstract())
                .map(createFeatureType())
                .collect(Collectors.toList());

        // add gml:AbstractFeature despite being abstract
        if (xtraServerMapping.hasFeatureType(GML_ABSTRACT_FEATURE)) {
            featureTypes.add(createFeatureType().apply(xtraServerMapping.getFeatureTypeMapping(GML_ABSTRACT_FEATURE).get()));
        }

        return featureTypes;
    }


    private Function<FeatureTypeMapping, FeatureType> createFeatureType() {
        return featureTypeMapping -> {

            SQLFeatureTypeImplType sqlFeatureTypeImplType = objectFactory.createSQLFeatureTypeImplType();

            createMappings(sqlFeatureTypeImplType, featureTypeMapping);
            createXtraServerParameters(sqlFeatureTypeImplType, featureTypeMapping);

            FeatureType featureType = objectFactory.createFeatureType();
            featureType.setName(featureTypeMapping.getName());
            featureType.setPGISFeatureTypeImpl(sqlFeatureTypeImplType);

            return featureType;
        };
    }

    private List<AdditionalMappings> createAdditionalMappings() {
        return xtraServerMapping.getFeatureTypeMappings().stream()
                // exclude gml:AbstractFeature despite being abstract
                .filter(featureTypeMapping -> featureTypeMapping.isAbstract() && !featureTypeMapping.getName().equals(GML_ABSTRACT_FEATURE))
                .map(additionalMapping -> {
                    MappingsSequenceType mappingsSequenceType = objectFactory.createMappingsSequenceType();

                    createMappings(mappingsSequenceType, additionalMapping);

                    AdditionalMappings additionalMappings = objectFactory.createAdditionalMappings();
                    additionalMappings.setRootElementName(additionalMapping.getName());
                    additionalMappings.setMappings(mappingsSequenceType);

                    return additionalMappings;
                }).collect(Collectors.toList());
    }


    private void createMappings(MappingsSequenceType mappingsSequenceType, FeatureTypeMapping featureTypeMapping) {
        featureTypeMapping.getPrimaryTables().forEach(mappingTable -> {
            createTableMapping(mappingsSequenceType, mappingTable);
        });
    }

    private void createTableMapping(MappingsSequenceType mappingsSequenceType, MappingTable mappingTable) {
        if (!mappingTable.isJoined() || !mappingTable.getValues().isEmpty()) {
            TableCommentDecorator table = new TableCommentDecorator();//objectFactory.createMappingsSequenceTypeTable();
            table.setTable_Name(mappingTable.getName());
            if (mappingTable.getPredicate() != null && !mappingTable.getPredicate().isEmpty()) {
                table.setTable_Name(mappingTable.getName() + "[" + mappingTable.getPredicate() + "]");
            }
            table.setOid_Col(mappingTable.getPrimaryKey());
            table.setTarget(mappingTable.getTargetPath());
                /*if (((MappingTableImpl) mappingTable).isReference()) {
                    table.setComment(mappingTable.getTargetPath().split(":")[1]);
                } else*/
            if (mappingTable.isPrimary() && mappingTable.getDescription() != null /*&& !xtraServerMapping.hasFeatureType(featureTypeMapping.getName())*/) {
                table.setComment("# " + mappingTable.getDescription() + " #");
            } else if (!mappingTable.isPrimary() && mappingTable.getDescription() != null) {
                table.setComment(mappingTable.getDescription());
            }
            mappingsSequenceType.getTableOrJoinOrAssociationTarget().add(table);
        }

        mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
                mappingTable.getJoinPaths().stream().map(mappingJoin -> {
                    MappingsSequenceType.Join join = objectFactory.createMappingsSequenceTypeJoin();
                    join.setAxis("parent");
                    join.setTarget(mappingJoin.getTargetPath());
                    join.setJoin_Path(buildJoinPath(mappingJoin.getJoinConditions()));
                    return join;
                }).collect(Collectors.toList())
        );

        final String[] lastProperty = {""};
        mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
                mappingTable.getValues().stream().map(mappingValue -> {
                    TableCommentDecorator value = new TableCommentDecorator();//objectFactory.createMappingsSequenceTypeTable();
                    value.setTable_Name(mappingTable.getName());
                    value.setTarget(mappingValue.getTargetPath());
                    if (mappingValue.getValue() != null && !mappingValue.getValue().equals(""))
                        value.setValue(mappingValue.getValue());
                    value.setValue_Type(buildValueType(mappingValue));
                    if (mappingValue.isNil()) {
                        value.setMapping_Mode("nil");
                    }
                    if (mappingValue.isClassification() || mappingValue.isNil()) {
                        value.setDb_Codes(Joiner.on(' ').join(((MappingValueClassification) mappingValue).getKeys()));
                        value.setSchema_Codes(Joiner.on(' ').join(((MappingValueClassification) mappingValue).getValues()));
                    }
                    if (!mappingValue.isReference() && mappingValue.getDescription() != null && !lastProperty[0].equals(mappingValue.getDescription())) {
                        value.setComment(mappingValue.getDescription());
                        lastProperty[0] = mappingValue.getDescription();
                    }
                    return value;
                }).collect(Collectors.toList())
        );

        mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
                mappingTable.getValues().stream()
                        .filter(MappingValue::isReference)
                        .map(mappingValue -> {
                            MappingsSequenceType.AssociationTarget associationTarget = objectFactory.createMappingsSequenceTypeAssociationTarget();
                            associationTarget.setObject_Ref(((MappingValueReference) mappingValue).getReferencedFeatureType());
                            associationTarget.setTarget(((MappingValueReference) mappingValue).getReferencedTarget());
                            return associationTarget;
                        }).collect(Collectors.toList())
        );


        mappingTable.getJoiningTables().forEach(joiningTable -> createTableMapping(mappingsSequenceType, joiningTable));
    }

    private String buildValueType(MappingValue mappingValue) {
        if (mappingValue.isExpression()) {
            return "expression";
        } else if (mappingValue.isConstant()) {
            return "constant";
        } else if (mappingValue.isReference()) {
            // TODO: might be expression or column
            return "expression";
        }

        return null;
    }

    private String buildJoinPath(List<MappingJoin.Condition> conditions) {
        StringBuilder stringBuilder = new StringBuilder();

        for (MappingJoin.Condition condition : conditions) {
            if (stringBuilder.length() == 0) {
                stringBuilder.insert(0, condition.getSourceTable());
            }
            stringBuilder.insert(0, ")::");
            stringBuilder.insert(0, condition.getSourceField());
            stringBuilder.insert(0, ":");
            stringBuilder.insert(0, condition.getTargetField());
            stringBuilder.insert(0, "/ref(");
            stringBuilder.insert(0, condition.getTargetTable());
        }

        return stringBuilder.toString();
    }


    private void createXtraServerParameters(SQLFeatureTypeImplType sqlFeatureTypeImplType, FeatureTypeMapping featureTypeMapping) {
        sqlFeatureTypeImplType.setLogging("false");
        sqlFeatureTypeImplType.setUseTempTable(false);
        if (!featureTypeMapping.getName().endsWith(":AbstractFeature")) {
            sqlFeatureTypeImplType.setTempTableName("_xsv_tmp_" + Joiner.on('_').join(featureTypeMapping.getPrimaryTableNames()));
        }
    }

    private void marshal(OutputStream outputStream, FeatureTypes featureTypes) throws JAXBException, SAXException, XMLStreamException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(Resources.getResource(JaxbReader.class, JaxbReader.MAPPING_SCHEMA));
        JAXBContext jaxbContext = JAXBContext.newInstance(FeatureTypes.class.getPackage().getName());

        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        XMLStreamWriter xsw = new IndentingUTF8XMLStreamWriter(xof.createXMLStreamWriter(outputStream, "UTF-8"));

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setSchema(schema);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setListener(new JaxbCommentsWriter(xsw));
        marshaller.marshal(featureTypes, xsw);
        xsw.close();
    }

}

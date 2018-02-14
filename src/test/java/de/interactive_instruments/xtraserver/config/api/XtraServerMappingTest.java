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
package de.interactive_instruments.xtraserver.config.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import de.interactive_instruments.xtraserver.config.io.XtraServerMappingFile;
import de.interactive_instruments.xtraserver.config.transformer.XtraServerMappingTransformer;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.*;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class XtraServerMappingTest {

    private URI applicationSchemaUri;
    private ElementSelector mappingElementSelector;

    @Before
    public void setup() throws IOException, URISyntaxException {
        this.applicationSchemaUri = Resources.getResource("Cities.xsd").toURI();
        this.mappingElementSelector = ElementSelectors.conditionalBuilder()
                .whenElementIsNamed("FeatureType").thenUse(ElementSelectors.byXPath("./*[1]", ElementSelectors.byNameAndText))
                .whenElementIsNamed("AdditionalMappings").thenUse(ElementSelectors.byXPath("./*[1]", ElementSelectors.byNameAndText))
                .whenElementIsNamed("PGISFeatureTypeImpl").thenUse(ElementSelectors.byName)
                .whenElementIsNamed("Table").thenUse(ElementSelectors.byNameAndAttributes("table_name", "target", "value"))
                .elseUse(ElementSelectors.byNameAndAllAttributes)
                .build();
    }

    /*
        //@Test
        public void testImport() throws JAXBException, IOException, SAXException {

            XtraServerMapping actual = XtraServerMapping.createFromStream(Resources.asByteSource(Resources.getResource("cities-mapping.xml")).openBufferedStream(), applicationSchema);

            XtraServerMapping expected = buildCitiesMapping();

            assertThat(actual, sameBeanAs(expected).ignoring(startsWith("applicationSchema")).ignoring(startsWith("namespaces")).ignoring(startsWith("prefix")).ignoring(startsWith("path")).ignoring(startsWith("mappingMode")));
        }

        //@Test
        public void testImportFlatten() throws JAXBException, IOException, SAXException {

            XtraServerMapping actual = XtraServerMapping.createFromStream(Resources.asByteSource(Resources.getResource("cities-mapping-flattened.xml")).openBufferedStream(), applicationSchema);

            XtraServerMapping xtraServerMapping = buildCitiesMapping();

            XtraServerMapping expected = XtraServerMapping.create(applicationSchema);
            for (String featureType : xtraServerMapping.getFeatureTypeNames(false)) {
                expected.addFeatureTypeMapping(xtraServerMapping.getFeatureTypeMapping(featureType, true).get());
            }

            assertThat(actual, sameBeanAs(expected).ignoring(startsWith("applicationSchema")).ignoring(startsWith("namespaces")).ignoring(startsWith("prefix")).ignoring(startsWith("path")).ignoring(startsWith("mappingMode")));
        }

        //@Test
        public void testExport() throws JAXBException, IOException, SAXException, XMLStreamException {

            XtraServerMapping xtraServerMapping = buildCitiesMapping();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            xtraServerMapping.writeToStream(outputStream, false);

            System.out.println(outputStream.toString());

            Source expected = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
            Source actual = Input.fromByteArray(outputStream.toByteArray()).build();

            assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(mappingElementSelector)));
        }

        //@Test
        public void testExportFanOut() throws JAXBException, IOException, SAXException, XMLStreamException {

            XtraServerMapping xtraServerMapping = buildCitiesMappingFlat();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            xtraServerMapping.writeToStream(outputStream, false);

            System.out.println(outputStream.toString());

            Source expected = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
            Source actual = Input.fromByteArray(outputStream.toByteArray()).build();

            assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(mappingElementSelector)));
        }

        @Test
        public void testExportZip() throws JAXBException, IOException, SAXException {

            XtraServerMapping xtraServerMapping = buildCitiesMappingFlat();

            PipedInputStream inputStream = new PipedInputStream();
            PipedOutputStream outputStream = new PipedOutputStream(inputStream);
            ByteArrayOutputStream byteArrayBuffer = new ByteArrayOutputStream();

            new Thread(() -> {
                try {
                    xtraServerMapping.writeToStream(outputStream, true);
                } catch (IOException | JAXBException | SAXException | XMLStreamException e) {
                    //ignore
                }
            }).start();

            try (ZipInputStream stream = new ZipInputStream(inputStream)) {
                ZipEntry entry;
                while ((entry = stream.getNextEntry()) != null) {
                    if (entry.getName().equals("XtraSrvConfig_Mapping.inc.xml")) {
                        byte[] buffer = new byte[2048];

                        int len;
                        while ((len = stream.read(buffer)) > 0) {
                            byteArrayBuffer.write(buffer, 0, len);
                        }
                    } else {
                        stream.closeEntry();
                    }
                }
            }

            System.out.println(byteArrayBuffer.toString());

            Source expected = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
            Source actual = Input.fromByteArray(byteArrayBuffer.toByteArray()).build();

            assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(mappingElementSelector)));

            // TODO: extract and compare additional files
        }

    @Test
    public void testImportExport() throws JAXBException, IOException, SAXException, XMLStreamException {

        XtraServerMapping xtraServerMappingImport = XtraServerMapping.createFromStream(Resources.asByteSource(Resources.getResource("cities-mapping.xml")).openBufferedStream(), applicationSchema);

        XtraServerMapping xtraServerMappingFlattenFanout = XtraServerMapping.create(applicationSchema);
        for (String featureType : xtraServerMappingImport.getFeatureTypeNames(true)) {
            xtraServerMappingFlattenFanout.addFeatureTypeMapping(xtraServerMappingImport.getFeatureTypeMapping(featureType, true).get(), true);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xtraServerMappingFlattenFanout.writeToStream(outputStream, false);

        System.out.println(outputStream.toString());

        Source expected = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
        Source actual = Input.fromByteArray(outputStream.toByteArray()).build();

        assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(mappingElementSelector)));
    }*/

    //@Test
    public void testLocalImportExport() throws JAXBException, IOException, SAXException, XMLStreamException {
        //final StreamSource schemaSource = new StreamSource(new FileInputStream(""), "/home/zahnen/development/XSProjects/AAA-Suite/schema/NAS/6.0/schema");
        final URI localApplicationSchema = new File("/home/zahnen/development/XSProjects/AAA-Suite/schema/NAS/6.0/schema/AAA-Fachschema_XtraServer.xsd").toURI();
        final String inputFile = "/home/zahnen/development/XSProjects/AAA-Suite/config/alkis/sf/includes/1/includes/XtraSrvConfig_Mapping.inc.xml";
        final String outputFile = "/home/zahnen/Downloads/alkis-mapping.xml";
        //XtraServerMapping xtraServerMappingImport = XtraServerMapping.createFromStream(new FileInputStream(inputFile), localApplicationSchema);
        final XtraServerMapping xtraServerMappingImport = XtraServerMappingFile.read()
                .fromStream(new FileInputStream(inputFile));

        final XtraServerMapping xtraServerMappingNav = XtraServerMappingTransformer
                .forMapping(xtraServerMappingImport)
                .applySchemaInfo(localApplicationSchema)
                .flattenInheritance()
                .fanOutInheritance()
                .ensureRelationNavigability()
                .transform();

        /*XtraServerMapping xtraServerMappingFlattenFanout = XtraServerMapping.create(localApplicationSchema);
        for (String featureType : xtraServerMappingImport.getFeatureTypeNames(false)) {
            xtraServerMappingFlattenFanout.addFeatureTypeMapping(xtraServerMappingImport.getFeatureTypeMapping(featureType, true).get(), true);
        }*/

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XtraServerMappingFile.write()
                .mapping(xtraServerMappingNav)
                //.createArchiveWithAdditionalFiles()
                .toStream(outputStream);

        System.out.println(outputStream.toString());

        final Source expected = Input.fromStream(new FileInputStream(inputFile)).build();
        final Source actual = Input.fromByteArray(outputStream.toByteArray()).build();
        //Source actual = Input.fromStream(new FileInputStream(outputFile)).build();

        final List<String> ignoreAttributes = ImmutableList.of(
                "FTCode",
                "logging",
                "tempTableName",
                "useTempTable",
                "derivation_pattern",
                "assign",
                "no_output",
                "value_type",
                "oid_col",
                "gmlVersion",
                "generator",
                "filter_mapping",
                "significant_for_emptiness"
        );

        final Map<String, String> targetsNotSupportedInHaleForAlkis = new ImmutableMap.Builder<String, String>()
                .put("adv:beziehtSichAufFlurstueck", "adv:AX_Flurstueck")
                .put("adv:inversZu_an", "adv:AX_Buchungsstelle")
                .put("adv:haengtZusammenMit", "adv:AX_Gebaeude")
                .put("adv:istTeilVon", "*")
                .put("adv:inversZu_dientZurDarstellungVon_AP_PTO", "*")
                .put("adv:inversZu_dientZurDarstellungVon_AP_PPO", "*")
                .put("adv:istAbgeleitetAus", "*")
                .put("adv:hatDirektUnten", "*")
                .put("adv:inversZu_hatDirektUnten", "*")
                .build();

        final Map<String, String> targetsNotSupportedInHaleForAlkisFull = new ImmutableMap.Builder<String, String>()
                .put("adv:beziehtSichAufFlurstueck", "adv:AX_Flurstueck")
                .put("adv:gehoertZu", "adv:AX_Gebaeude")
                .put("adv:inversZu_an", "adv:AX_Buchungsstelle")
                .put("adv:haengtZusammenMit", "adv:AX_Gebaeude")
                .put("adv:istTeilVon", "*")
                .put("adv:inversZu_dientZurDarstellungVon_AP_PTO", "*")
                .put("adv:inversZu_dientZurDarstellungVon_AP_PPO", "*")
                .put("adv:istAbgeleitetAus", "*")
                .put("adv:hatDirektUnten", "*")
                .put("adv:inversZu_hatDirektUnten", "*")
                .put("adv:inversZu_zeigtAuf", "adv:AX_Grenzpunkt")
                .put("adv:gehoert", "adv:AX_Gebaeude")
                .put("adv:inversZu_dientZurDarstellungVon_AP_Darstellung", "adv:AA_Objekt")
                .put("adv:inversZu_dientZurDarstellungVon_AP_LTO", "adv:AA_Objekt")
                .put("adv:inversZu_dientZurDarstellungVon_AP_LPO", "adv:AA_Objekt")
                .put("adv:bestehtAus", "adv:AA_ZUSO") // TODO
                .build();

        assertThat(actual, isSimilarTo(expected)
                .throwComparisonFailure()
                .ignoreComments()
                .ignoreWhitespace()
                .withAttributeFilter(attr -> !ignoreAttributes.contains(attr.getLocalName()))
                .withNodeFilter(node -> {
                    if (node.getLocalName() != null) {
                        if (node.getLocalName().equals("Table") && node.getAttributes() != null && node.getAttributes().getLength() == 3
                                && node.getAttributes().getNamedItem("oid_col") != null && node.getAttributes().getNamedItem("oid_col").getNodeValue().equals("id")
                                && node.getAttributes().getNamedItem("target") != null && !node.getAttributes().getNamedItem("target").getNodeValue().isEmpty()
                                && node.getAttributes().getNamedItem("table_name") != null) {
                            return false;
                        } else if (node.getLocalName().equals("PathAliases") || node.getLocalName().equals("Content")) {
                            return false;
                        } else if (node.getLocalName().equals("Table") && node.getAttributes() != null && node.getAttributes().getNamedItem("filter_mapping") != null && node.getAttributes().getNamedItem("filter_mapping").getNodeValue().equals("true")) {
                            return false;
                        } else if (node.getLocalName().equals("Table") && node.getAttributes() != null && node.getAttributes().getNamedItem("mapped_geometry") != null && node.getAttributes().getNamedItem("mapped_geometry").getNodeValue().equals("true")) {
                            return false;
                        }/* else if ((node.getLocalName().equals("Table") || node.getLocalName().equals("Join") || node.getLocalName().equals("AssociationTarget"))
                                && node.getAttributes() != null && node.getAttributes().getNamedItem("target") != null
                                && targetsNotSupportedInHaleForAlkis.keySet().stream().anyMatch(node.getAttributes().getNamedItem("target").getNodeValue()::startsWith)) {
                            return false;
                        }*/ else if (node.getLocalName().equals("Join")
                                && node.getAttributes() != null && node.getAttributes().getNamedItem("join_path") != null
                                && node.getAttributes().getNamedItem("join_path").getNodeValue().contains("[1=2]/ref")) {
                            return false;
                        } else if (node.getLocalName().equals("AssociationTarget")
                                && node.getAttributes() != null && node.getAttributes().getNamedItem("target") != null
                                && node.getAttributes().getNamedItem("target").getNodeValue().equals("adv:inversZu_hatDirektUnten")) {
                            return false;
                        } else if (node.getLocalName().equals("Join")
                                && node.getAttributes() != null && node.getAttributes().getNamedItem("target") != null
                                && (node.getAttributes().getNamedItem("target").getNodeValue().startsWith("adv:inversZu_hatDirektUnten/adv:")
                                || node.getAttributes().getNamedItem("target").getNodeValue().startsWith("adv:hatDirektUnten/adv:"))) {
                            return false;
                        } else if (node.getLocalName().equals("FeatureType") && node.getFirstChild() != null && node.getFirstChild().getTextContent() != null && node.getFirstChild().getTextContent().startsWith("gmlx:_Feature")) {
                            return false;
                        }
                    }
                    return true;
                })
                .withNodeMatcher(new DefaultNodeMatcher(mappingElementSelector))
                .withDifferenceEvaluator(DifferenceEvaluators.downgradeDifferencesToSimilar(ComparisonType.CHILD_NODELIST_LENGTH, ComparisonType.CHILD_NODELIST_SEQUENCE, ComparisonType.ELEMENT_NUM_ATTRIBUTES))
        );

        /*Source actual2 = Input.fromByteArray(outputStream.toByteArray()).build();
        //Source actual2 = Input.fromStream(new FileInputStream(outputFile)).build();
        String xpathMatcher = "//*[" + targetsNotSupportedInHaleForAlkis.entrySet().stream().map(target -> "(starts-with(@target, '" + target.getKey() + "') and ../../*[1][.='" + target.getValue() + "'])").collect(Collectors.joining(" or ")) + "]";
        System.out.println(xpathMatcher);

        Iterable<Node> i = new JAXPXPathEngine().selectNodes(xpathMatcher, actual2);
        String message = "Mappings should not be contained in export:\n" + StreamSupport.stream(i.spliterator(), false).map(this::nodeToString).collect(Collectors.joining("\n"));

        assertFalse(message, i.iterator().hasNext());*/

    }

    private String nodeToString(final Node node) {
        final StringWriter sw = new StringWriter();
        try {
            final Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (final TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }

    /*private XtraServerMapping buildCitiesMapping() throws IOException {
        XtraServerMapping xtraServerMapping = XtraServerMapping.create(this.applicationSchema);
        xtraServerMapping.addFeatureTypeMapping(buildAbstractFeature());
        xtraServerMapping.addFeatureTypeMapping(buildNamedGeoObject());
        xtraServerMapping.addFeatureTypeMapping(buildNamedPlace());
        xtraServerMapping.addFeatureTypeMapping(buildCity());
        xtraServerMapping.addFeatureTypeMapping(buildRiver());
        xtraServerMapping.addFeatureTypeMapping(buildDistrict());

        return xtraServerMapping;
    }

    private XtraServerMapping buildCitiesMappingFlat() throws IOException {

        XtraServerMapping xtraServerMapping = XtraServerMapping.create(this.applicationSchema);
        xtraServerMapping.addFeatureTypeMapping(buildCityFlat(), true);
        xtraServerMapping.addFeatureTypeMapping(buildRiverFlat(), true);
        xtraServerMapping.addFeatureTypeMapping(buildDistrictFlat(), true);

        return xtraServerMapping;
    }

    private FeatureTypeMapping buildAbstractFeature() {

        MappingTable city = MappingTable.create();
        city.setName("city");
        city.setOidCol("id");

        MappingTable river = MappingTable.create();
        river.setName("river");
        river.setOidCol("id");

        MappingTable district = MappingTable.create();
        district.setName("district");
        district.setOidCol("id");

        MappingValue cityId = MappingValue.create(namespaces);
        cityId.setTable(city);
        cityId.setTarget("@gml:id");
        cityId.setValue("id");

        MappingValue riverId = MappingValue.create(namespaces);
        riverId.setTable(river);
        riverId.setTarget("@gml:id");
        riverId.setValue("id");

        MappingValue riverIdentifier = MappingValue.create(namespaces);
        riverIdentifier.setTable(river);
        riverIdentifier.setTarget("gml:identifier");
        riverIdentifier.setValue("id");

        MappingValue districtId = MappingValue.create(namespaces);
        districtId.setTable(district);
        districtId.setTarget("@gml:id");
        districtId.setValue("id");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("gml:AbstractFeature", new QName("http://www.opengis.net/gml/3.2", "AbstractFeatureType", "gml"), namespaces);
        featureTypeMapping.addTable(city);
        featureTypeMapping.addTable(district);
        featureTypeMapping.addTable(river);
        featureTypeMapping.addValue(cityId);
        featureTypeMapping.addValue(riverId);
        featureTypeMapping.addValue(riverIdentifier);
        featureTypeMapping.addValue(districtId);

        return featureTypeMapping;
    }

    private FeatureTypeMapping buildNamedGeoObject() {

        MappingTable city = MappingTable.create();
        city.setName("city");
        city.setOidCol("id");

        MappingTable river = MappingTable.create();
        river.setName("river");
        river.setOidCol("id");

        MappingValue cityName = MappingValue.create(namespaces);
        cityName.setTable(city);
        cityName.setTarget("ci:name");
        cityName.setValue("name");

        MappingValue riverName = MappingValue.create(namespaces);
        riverName.setTable(river);
        riverName.setTarget("ci:name");
        riverName.setValue("name");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("ci:NamedGeoObject", new QName("http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities", "NamedGeoObjectType", "ci"), namespaces);
        featureTypeMapping.addTable(city);
        featureTypeMapping.addTable(river);
        featureTypeMapping.addValue(cityName);
        featureTypeMapping.addValue(riverName);

        return featureTypeMapping;
    }

    private FeatureTypeMapping buildNamedPlace() {

        MappingTable city = MappingTable.create();
        city.setName("city");
        city.setOidCol("id");

        MappingTable alternativeName = MappingTable.create();
        alternativeName.setName("alternativename");
        alternativeName.setOidCol("id");
        alternativeName.setTarget("ci:alternativeName");

        MappingJoin city2alternativeNameJoin = MappingJoin.create();
        MappingJoin.Condition city2alternativeName = MappingJoin.Condition.create(city, "id", alternativeName, "cid");
        city2alternativeNameJoin.addCondition(city2alternativeName);

        MappingValue alternativeNameName = MappingValue.create(namespaces);
        alternativeNameName.setTable(alternativeName);
        alternativeNameName.setTarget("ci:alternativeName/ci:AlternativeName/ci:name");
        alternativeNameName.setValue("name");

        MappingValue alternativeNameLanguage = MappingValue.create(namespaces);
        alternativeNameLanguage.setTable(alternativeName);
        alternativeNameLanguage.setTarget("ci:alternativeName/ci:AlternativeName/ci:language");
        alternativeNameLanguage.setValue("language");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("ci:NamedPlace", new QName("http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities", "NamedPlaceType", "ci"), namespaces);
        featureTypeMapping.addTable(city);
        featureTypeMapping.addTable(alternativeName);
        featureTypeMapping.addJoin(city2alternativeNameJoin);
        featureTypeMapping.addValue(alternativeNameName);
        featureTypeMapping.addValue(alternativeNameLanguage);

        return featureTypeMapping;
    }

    private FeatureTypeMapping buildCityFlat() {
        FeatureTypeMapping cityMapping = buildCity();

        MappingTable city = cityMapping.getTable("city").get();

        MappingTable alternativeName = MappingTable.create();
        alternativeName.setName("alternativename");
        alternativeName.setOidCol("id");
        alternativeName.setTarget("ci:alternativeName");

        MappingJoin city2alternativeNameJoin = MappingJoin.create();
        MappingJoin.Condition city2alternativeName = MappingJoin.Condition.create(city, "id", alternativeName, "cid");
        city2alternativeNameJoin.addCondition(city2alternativeName);

        MappingValue alternativeNameName = MappingValue.create(namespaces);
        alternativeNameName.setTable(alternativeName);
        alternativeNameName.setTarget("ci:alternativeName/ci:AlternativeName/ci:name");
        alternativeNameName.setValue("name");

        MappingValue alternativeNameLanguage = MappingValue.create(namespaces);
        alternativeNameLanguage.setTable(alternativeName);
        alternativeNameLanguage.setTarget("ci:alternativeName/ci:AlternativeName/ci:language");
        alternativeNameLanguage.setValue("language");

        MappingValue cityName = MappingValue.create(namespaces);
        cityName.setTable(city);
        cityName.setTarget("ci:name");
        cityName.setValue("name");

        MappingValue cityId = MappingValue.create(namespaces);
        cityId.setTable(city);
        cityId.setTarget("@gml:id");
        cityId.setValue("id");


        cityMapping.addTable(alternativeName);
        cityMapping.addJoin(city2alternativeNameJoin);
        cityMapping.addValue(alternativeNameName);
        cityMapping.addValue(alternativeNameLanguage);
        cityMapping.addValue(cityName);
        cityMapping.addValue(cityId);

        return cityMapping;
    }

    private FeatureTypeMapping buildCity() {

        MappingTable city = MappingTable.create();
        city.setName("city");
        city.setOidCol("id");

        MappingValue cityLocation = MappingValue.create(namespaces);
        cityLocation.setTable(city);
        cityLocation.setTarget("ci:location");
        cityLocation.setValue("location");

        MappingValue cityCountry = MappingValue.create(namespaces);
        cityCountry.setTable(city);
        cityCountry.setTarget("ci:country");
        cityCountry.setValue("Germany");
        cityCountry.setValueType("constant");

        MappingValue cityFunction = MappingValue.create(namespaces);
        cityFunction.setTable(city);
        cityFunction.setTarget("ci:function");
        cityFunction.setValue("'urn:ci:function::' || $T$.function");
        cityFunction.setValueType("expression");

        MappingValue cityFunctionNil = MappingValue.create(namespaces);
        cityFunctionNil.setTable(city);
        cityFunctionNil.setTarget("ci:function");
        cityFunctionNil.setValue("function_void");
        cityFunctionNil.setMappingMode("nil");
        cityFunctionNil.setDbCodes("1 2 3 NULL");
        cityFunctionNil.setDbValues("'unknown' 'other:unpopulated' 'withheld'");

        MappingValue cityArea = MappingValue.create(namespaces);
        cityArea.setTable(city);
        cityArea.setTarget("ci:area");
        cityArea.setValue("$T$.width * $T$.height");
        cityArea.setValueType("expression");

        MappingValue cityBeginLifespan = MappingValue.create(namespaces);
        cityBeginLifespan.setTable(city);
        cityBeginLifespan.setTarget("ci:beginLifespan");
        cityBeginLifespan.setValue("regexp_replace(begin_lifespan, '([0-9]{4})([0-9]{2})([0-9]{2})', '\\\\1-\\\\2-\\\\3 00:00:00', 'g')");
        cityBeginLifespan.setValueType("expression");

        MappingTable river = MappingTable.create();
        river.setName("river");
        river.setOidCol("id");
        river.setTarget("ci:passingRiver");

        MappingTable cityRiver = MappingTable.create();
        cityRiver.setName("city_river");
        cityRiver.setOidCol("id");
        ((MappingTable) cityRiver).isJoined = true;

        MappingJoin city2RiverJoin = MappingJoin.create();
        MappingJoin.Condition city2cityRiver = MappingJoin.Condition.create(city, "id", cityRiver, "cid");
        MappingJoin.Condition cityRiver2River = MappingJoin.Condition.create(cityRiver, "rid", river, "id");
        city2RiverJoin.addCondition(city2cityRiver);
        city2RiverJoin.addCondition(cityRiver2River);

        MappingValue riverHref = MappingValue.create(namespaces);
        riverHref.setTable(river);
        riverHref.setTarget("ci:passingRiver/@xlink:href");
        riverHref.setValue("");

        AssociationTarget riverType = AssociationTarget.create();
        riverType.setObjectRef("ci:River");
        riverType.setTarget("ci:passingRiver");

        MappingTable cityDistrict = MappingTable.create();
        cityDistrict.setName("city_district");
        cityDistrict.setOidCol("id");
        cityDistrict.setTarget("ci:district");

        MappingJoin city2CityDistrictJoin = MappingJoin.create();
        MappingJoin.Condition city2cityDistrict = MappingJoin.Condition.create(city, "id", cityDistrict, "cid");
        city2CityDistrictJoin.addCondition(city2cityDistrict);

        MappingValue districtHref = MappingValue.create(namespaces);
        districtHref.setTable(cityDistrict);
        districtHref.setTarget("ci:district/@xlink:href");
        districtHref.setValue("'urn:adv:oid:' || $T$.did");
        districtHref.setValueType("expression");

        AssociationTarget districtType = AssociationTarget.create();
        districtType.setObjectRef("ci:District");
        districtType.setTarget("ci:district");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("ci:City", new QName("http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities", "CityType", "ci"), namespaces);
        featureTypeMapping.addTable(city);
        featureTypeMapping.addValue(cityLocation);
        featureTypeMapping.addValue(cityCountry);
        featureTypeMapping.addValue(cityFunction);
        featureTypeMapping.addValue(cityFunctionNil);
        featureTypeMapping.addValue(cityArea);
        featureTypeMapping.addValue(cityBeginLifespan);
        featureTypeMapping.addTable(river);
        featureTypeMapping.addTable(cityDistrict);
        featureTypeMapping.addTable(cityRiver);
        featureTypeMapping.addJoin(city2RiverJoin);
        featureTypeMapping.addValue(riverHref);
        featureTypeMapping.addAssociationTarget(riverType);
        featureTypeMapping.addJoin(city2CityDistrictJoin);
        featureTypeMapping.addValue(districtHref);
        featureTypeMapping.addAssociationTarget(districtType);

        return featureTypeMapping;
    }

    private FeatureTypeMapping buildRiverFlat() {
        FeatureTypeMapping riverMapping = buildRiver();

        MappingTable river = riverMapping.getTable("river").get();

        MappingValue riverName = MappingValue.create(namespaces);
        riverName.setTable(river);
        riverName.setTarget("ci:name");
        riverName.setValue("name");

        MappingValue riverId = MappingValue.create(namespaces);
        riverId.setTable(river);
        riverId.setTarget("@gml:id");
        riverId.setValue("id");

        MappingValue riverIdentifier = MappingValue.create(namespaces);
        riverIdentifier.setTable(river);
        riverIdentifier.setTarget("gml:identifier");
        riverIdentifier.setValue("id");

        riverMapping.addValue(riverName);
        riverMapping.addValue(riverId);
        riverMapping.addValue(riverIdentifier);

        return riverMapping;
    }

    private FeatureTypeMapping buildRiver() {

        MappingTable river = MappingTable.create();
        river.setName("river");
        river.setOidCol("id");

        MappingValue riverLocation = MappingValue.create(namespaces);
        riverLocation.setTable(river);
        riverLocation.setTarget("ci:location");
        riverLocation.setValue("location");

        MappingValue riverLength = MappingValue.create(namespaces);
        riverLength.setTable(river);
        riverLength.setTarget("ci:length");
        riverLength.setValue("length");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("ci:River", new QName("http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities", "RiverType", "ci"), namespaces);
        featureTypeMapping.addTable(river);
        featureTypeMapping.addValue(riverLocation);
        featureTypeMapping.addValue(riverLength);

        return featureTypeMapping;
    }

    private FeatureTypeMapping buildDistrictFlat() {
        FeatureTypeMapping districtMapping = buildDistrict();

        MappingTable district = districtMapping.getTable("district").get();

        MappingValue districtId = MappingValue.create(namespaces);
        districtId.setTable(district);
        districtId.setTarget("@gml:id");
        districtId.setValue("id");

        districtMapping.addValue(districtId);

        return districtMapping;
    }

    private FeatureTypeMapping buildDistrict() {

        MappingTable district = MappingTable.create();
        district.setName("district");
        district.setOidCol("id");

        MappingValue districtLocation = MappingValue.create(namespaces);
        districtLocation.setTable(district);
        districtLocation.setTarget("ci:location");
        districtLocation.setValue("location");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("ci:District", new QName("http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities", "DistrictType", "ci"), namespaces);
        featureTypeMapping.addTable(district);
        featureTypeMapping.addValue(districtLocation);

        return featureTypeMapping;
    }*/
}

package de.interactive_instruments.xtraserver.config.util.api;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import de.interactive_instruments.xtraserver.config.util.ApplicationSchema;
import de.interactive_instruments.xtraserver.config.util.MappingTableImpl;
import de.interactive_instruments.xtraserver.config.util.Namespaces;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.*;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class XtraServerMappingTest {

    private Namespaces namespaces;
    private ApplicationSchema applicationSchema;

    @Before
    public void setup() throws IOException {
        this.applicationSchema = new ApplicationSchema(Resources.asByteSource(Resources.getResource("Cities.xsd")).openBufferedStream());
        this.namespaces = applicationSchema.getNamespaces();
    }

    @Test
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
        for (String featureType : xtraServerMapping.getFeatureTypeList(false)) {
            expected.addFeatureTypeMapping(xtraServerMapping.getFeatureTypeMapping(featureType, true).get());
        }

        assertThat(actual, sameBeanAs(expected).ignoring(startsWith("applicationSchema")).ignoring(startsWith("namespaces")).ignoring(startsWith("prefix")).ignoring(startsWith("path")).ignoring(startsWith("mappingMode")));
    }

    @Test
    public void testExport() throws JAXBException, IOException, SAXException {

        XtraServerMapping xtraServerMapping = buildCitiesMapping();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xtraServerMapping.writeToStream(outputStream, false);

        System.out.println(outputStream.toString());

        Source expected = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
        Source actual = Input.fromByteArray(outputStream.toByteArray()).build();

        assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)));
    }

    @Test
    public void testExportFanOut() throws JAXBException, IOException, SAXException {

        XtraServerMapping xtraServerMapping = buildCitiesMappingFlat();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xtraServerMapping.writeToStream(outputStream, false);

        System.out.println(outputStream.toString());

        Source expected = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
        Source actual = Input.fromByteArray(outputStream.toByteArray()).build();

        assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)));
    }

    @Test
    public void testExportZip() throws JAXBException, IOException, SAXException {

        XtraServerMapping xtraServerMapping = buildCitiesMapping();

        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream(inputStream);
        ByteArrayOutputStream byteArrayBuffer = new ByteArrayOutputStream();

        new Thread(() -> {
            try {
                xtraServerMapping.writeToStream(outputStream, true);
            } catch (IOException | JAXBException | SAXException e) {
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

        assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)));

        // TODO: extract and compare additional files
    }

    @Test
    public void testImportExport() throws JAXBException, IOException, SAXException {

        XtraServerMapping xtraServerMappingImport = XtraServerMapping.createFromStream(Resources.asByteSource(Resources.getResource("cities-mapping.xml")).openBufferedStream(), applicationSchema);

        XtraServerMapping xtraServerMappingFlattenFanout = XtraServerMapping.create(applicationSchema);
        for (String featureType : xtraServerMappingImport.getFeatureTypeList(false)) {
            xtraServerMappingFlattenFanout.addFeatureTypeMapping(xtraServerMappingImport.getFeatureTypeMapping(featureType, true).get(), true);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xtraServerMappingFlattenFanout.writeToStream(outputStream, false);

        System.out.println(outputStream.toString());

        Source expected = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
        Source actual = Input.fromByteArray(outputStream.toByteArray()).build();

        assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)));
    }

    //@Test
    public void testLocalImportExport() throws JAXBException, IOException, SAXException {
        StreamSource schemaSource = new StreamSource(new FileInputStream("/home/zahnen/development/XSProjects/AAA-Suite/schema/NAS/6.0/schema/AAA-Fachschema_XtraServer.xsd"), "/home/zahnen/development/XSProjects/AAA-Suite/schema/NAS/6.0/schema");
        ApplicationSchema localApplicationSchema = new ApplicationSchema(schemaSource);
        String mappingFile = "/home/zahnen/development/XSProjects/AAA-Suite/config/alkis/sf/includes/1/includes/XtraSrvConfig_Mapping.inc.xml";
        XtraServerMapping xtraServerMappingImport = XtraServerMapping.createFromStream(new FileInputStream(mappingFile), localApplicationSchema);

        XtraServerMapping xtraServerMappingFlattenFanout = XtraServerMapping.create(localApplicationSchema);
        for (String featureType : xtraServerMappingImport.getFeatureTypeList(false)) {
            xtraServerMappingFlattenFanout.addFeatureTypeMapping(xtraServerMappingImport.getFeatureTypeMapping(featureType, true).get(), true);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xtraServerMappingFlattenFanout.writeToStream(outputStream, false);

        System.out.println(outputStream.toString());

        Source expected = Input.fromStream(new FileInputStream(mappingFile)).build();
        //Source actual = Input.fromByteArray(outputStream.toByteArray()).build();
        Source actual = Input.fromStream(new FileInputStream("/home/zahnen/Downloads/alkis-mapping.xml")).build();

        ElementSelector elementSelector = ElementSelectors.conditionalBuilder()
                .whenElementIsNamed("FeatureType").thenUse(ElementSelectors.byXPath("./*[1]", ElementSelectors.byNameAndText))
                .whenElementIsNamed("AdditionalMappings").thenUse(ElementSelectors.byXPath("./*[1]", ElementSelectors.byNameAndText))
                .whenElementIsNamed("PGISFeatureTypeImpl").thenUse(ElementSelectors.byName)
                .whenElementIsNamed("Table").thenUse(ElementSelectors.byNameAndAttributes("table_name", "target", "value"))
                .elseUse(ElementSelectors.byNameAndAllAttributes)
                .build();

        assertThat(actual, isSimilarTo(expected)
                .throwComparisonFailure()
                .ignoreComments()
                .ignoreWhitespace()
                .withAttributeFilter(attr -> !ImmutableList.of("FTCode", "logging", "tempTableName", "useTempTable", "derivation_pattern", "assign", "no_output", "value_type", "oid_col", "gmlVersion", "generator", "filter_mapping", "significant_for_emptiness").contains(attr.getLocalName()))
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
                        } else if ((node.getLocalName().equals("Table") || node.getLocalName().equals("Join") || node.getLocalName().equals("AssociationTarget"))
                                && (node.getAttributes() != null && node.getAttributes().getNamedItem("target") != null
                                && (node.getAttributes().getNamedItem("target").getNodeValue().contains("adv:beziehtSichAufFlurstueck") || node.getAttributes().getNamedItem("target").getNodeValue().contains("adv:inversZu_an")
                                || node.getAttributes().getNamedItem("target").getNodeValue().contains("adv:gehoertZu") || node.getAttributes().getNamedItem("target").getNodeValue().contains("adv:haengtZusammenMit")
                                || node.getAttributes().getNamedItem("target").getNodeValue().contains("adv:istTeilVon") || node.getAttributes().getNamedItem("target").getNodeValue().contains("adv:inversZu_dientZurDarstellungVon_AP_PTO")
                                || node.getAttributes().getNamedItem("target").getNodeValue().contains("adv:inversZu_dientZurDarstellungVon_AP_PPO") || node.getAttributes().getNamedItem("target").getNodeValue().contains("adv:istAbgeleitetAus")
                                || node.getAttributes().getNamedItem("target").getNodeValue().contains("adv:hatDirektUnten") || node.getAttributes().getNamedItem("target").getNodeValue().contains("adv:inversZu_hatDirektUnten")))) {
                            return false;
                        } else if (node.getLocalName().equals("FeatureType") && node.getFirstChild() != null && node.getFirstChild().getTextContent() != null && node.getFirstChild().getTextContent().startsWith("gmlx:_Feature")) {
                            return false;
                        }
                    }
                    return true;
                })
                .withNodeMatcher(new DefaultNodeMatcher(elementSelector))
                .withDifferenceEvaluator(DifferenceEvaluators.downgradeDifferencesToSimilar(ComparisonType.CHILD_NODELIST_LENGTH, ComparisonType.CHILD_NODELIST_SEQUENCE, ComparisonType.ELEMENT_NUM_ATTRIBUTES))
        );
    }

    private XtraServerMapping buildCitiesMapping() throws IOException {
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
        ((MappingTableImpl) cityRiver).isJoined = true;

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
    }
}

package de.interactive_instruments.xtraserver.config.util.api;

import com.google.common.io.Resources;
import de.interactive_instruments.xtraserver.config.util.ApplicationSchema;
import de.interactive_instruments.xtraserver.config.util.Namespaces;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class XtraServerMappingTest {

    Namespaces namespaces;

    @Before
    public void setup() {
        this.namespaces = new Namespaces();
    }

    @Test
    public void testImport() throws JAXBException, IOException, SAXException {

        XtraServerMapping actual = XtraServerMapping.createFromStream(Resources.asByteSource(Resources.getResource("cities-mapping.xml")).openBufferedStream());

        XtraServerMapping expected = buildCitiesMapping();

        //assertThat(test, is(equalTo(control)));
        com.shazam.shazamcrest.MatcherAssert.assertThat(actual, sameBeanAs(expected).ignoring(startsWith("applicationSchema")).ignoring(startsWith("namespaces")).ignoring(startsWith("prefix")).ignoring(startsWith("path")).ignoring(startsWith("mappingMode")));
    }

    @Test
    public void testImportFlatten() throws JAXBException, IOException, SAXException {

        XtraServerMapping actual = XtraServerMapping.createFromStream(Resources.asByteSource(Resources.getResource("cities-mapping-flattened.xml")).openBufferedStream());

        XtraServerMapping xtraServerMapping = buildCitiesMapping();

        XtraServerMapping expected = XtraServerMapping.create();
        for (String featureType: xtraServerMapping.getFeatureTypeList(false)) {
            expected.addFeatureTypeMapping(xtraServerMapping.getFeatureTypeMapping(featureType, true));
        }

        //assertThat(test, is(equalTo(control)));
        com.shazam.shazamcrest.MatcherAssert.assertThat(actual, sameBeanAs(expected).ignoring(startsWith("applicationSchema")).ignoring(startsWith("namespaces")).ignoring(startsWith("prefix")).ignoring(startsWith("path")).ignoring(startsWith("mappingMode")));
    }

    @Test
    public void testExport() throws JAXBException, IOException, SAXException {

        XtraServerMapping xtraServerMapping = buildCitiesMapping();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xtraServerMapping.writeToStream(outputStream);

        System.out.println(outputStream.toString());

        Source expected = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
        Source actual = Input.fromByteArray(outputStream.toByteArray()).build();

        com.shazam.shazamcrest.MatcherAssert.assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)));
    }

    @Test
    public void testExportFanOut() throws JAXBException, IOException, SAXException {

        XtraServerMapping xtraServerMapping = buildCitiesMappingFlat();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xtraServerMapping.writeToStream(outputStream);

        System.out.println(outputStream.toString());

        Source expected = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
        Source actual = Input.fromByteArray(outputStream.toByteArray()).build();

        com.shazam.shazamcrest.MatcherAssert.assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)));
    }

    private XtraServerMapping buildCitiesMapping() throws IOException {
        XtraServerMapping xtraServerMapping = XtraServerMapping.create();
        xtraServerMapping.addFeatureTypeMapping(buildAbstractFeature());
        xtraServerMapping.addFeatureTypeMapping(buildNamedGeoObject());
        xtraServerMapping.addFeatureTypeMapping(buildCity());
        xtraServerMapping.addFeatureTypeMapping(buildRiver());

        return xtraServerMapping;
    }

    private XtraServerMapping buildCitiesMappingFlat() throws IOException {
        ApplicationSchema applicationSchema = new ApplicationSchema();

        XtraServerMapping xtraServerMapping = XtraServerMapping.create();
        xtraServerMapping.addFeatureTypeMapping(buildCityFlat(), true);
        xtraServerMapping.addFeatureTypeMapping(buildRiverFlat(), true);

        return xtraServerMapping;
    }

    private FeatureTypeMapping buildAbstractFeature() {

        MappingTable city = MappingTable.create();
        city.setName("city");
        city.setOidCol("id");

        MappingTable river = MappingTable.create();
        river.setName("river");
        river.setOidCol("id");

        MappingValue cityId = MappingValue.create(namespaces);
        cityId.setTable(city);
        cityId.setTarget("@gml:id");
        cityId.setValue("objid");

        MappingValue riverId = MappingValue.create(namespaces);
        riverId.setTable(river);
        riverId.setTarget("@gml:id");
        riverId.setValue("objid");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("gml:AbstractFeature", new QName("http://www.opengis.net/gml/3.2", "AbstractFeatureType", "gml"), namespaces);
        featureTypeMapping.addTable(city);
        featureTypeMapping.addTable(river);
        featureTypeMapping.addValue(cityId);
        featureTypeMapping.addValue(riverId);

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

    private FeatureTypeMapping buildCityFlat() {
        FeatureTypeMapping cityMapping = buildCity();

        MappingTable city = cityMapping.getTable("city");

        MappingValue cityName = MappingValue.create(namespaces);
        cityName.setTable(city);
        cityName.setTarget("ci:name");
        cityName.setValue("name");

        MappingValue cityId = MappingValue.create(namespaces);
        cityId.setTable(city);
        cityId.setTarget("@gml:id");
        cityId.setValue("objid");

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
        cityFunctionNil.setValue("function");
        cityFunctionNil.setMappingMode("nil");
        cityFunctionNil.setDbCodes("1 2 3 NULL");
        cityFunctionNil.setDbValues("'unknown' 'other:unpopulated' 'withheld'");

        MappingTable alternativeName = MappingTable.create();
        alternativeName.setName("alternativename");
        alternativeName.setOidCol("id");
        alternativeName.setTarget("ci:alternativeName");

        MappingJoin.Condition city2alternativeName = MappingJoin.Condition.create();
        city2alternativeName.setSourceTable(city);
        city2alternativeName.setSourceField("id");
        city2alternativeName.setTargetTable(alternativeName);
        city2alternativeName.setTargetField("cid");

        MappingJoin city2alternativeNameJoin = MappingJoin.create();
        city2alternativeNameJoin.setTarget("ci:alternativeName");
        city2alternativeNameJoin.getJoinConditions().add(city2alternativeName);
        alternativeName.addJoinPath(city2alternativeNameJoin);

        MappingValue alternativeNameName = MappingValue.create(namespaces);
        alternativeNameName.setTable(alternativeName);
        alternativeNameName.setTarget("ci:alternativeName/ci:AlternativeName/ci:name");
        alternativeNameName.setValue("name");

        MappingValue alternativeNameLanguage = MappingValue.create(namespaces);
        alternativeNameLanguage.setTable(alternativeName);
        alternativeNameLanguage.setTarget("ci:alternativeName/ci:AlternativeName/ci:language");
        alternativeNameLanguage.setValue("language");

        MappingTable river = MappingTable.create();
        river.setName("river");
        river.setOidCol("id");
        river.setTarget("ci:passingRiver");

        MappingTable cityRiver = MappingTable.create();
        cityRiver.setName("city_river");
        cityRiver.setOidCol("id");
        cityRiver.setTarget("ci:passingRiver");

        MappingJoin.Condition city2cityRiver = MappingJoin.Condition.create();
        city2cityRiver.setSourceTable(city);
        city2cityRiver.setSourceField("id");
        city2cityRiver.setTargetTable(cityRiver);
        city2cityRiver.setTargetField("cid");

        MappingJoin.Condition cityRiver2River = MappingJoin.Condition.create();
        cityRiver2River.setSourceTable(cityRiver);
        cityRiver2River.setSourceField("rid");
        cityRiver2River.setTargetTable(river);
        cityRiver2River.setTargetField("id");

        MappingJoin city2RiverJoin = MappingJoin.create();
        city2RiverJoin.setTarget("ci:passingRiver");
        city2RiverJoin.getJoinConditions().add(city2cityRiver);
        city2RiverJoin.getJoinConditions().add(cityRiver2River);
        river.addJoinPath(city2RiverJoin);

        MappingValue riverHref = MappingValue.create(namespaces);
        riverHref.setTable(river);
        riverHref.setTarget("ci:passingRiver/@xlink:href");

        AssociationTarget riverType = AssociationTarget.create();
        riverType.setObjectRef("ci:River");
        riverType.setTarget("ci:passingRiver");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("ci:City", new QName("http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities", "CityType", "ci"), namespaces);
        featureTypeMapping.addTable(city);
        featureTypeMapping.addValue(cityLocation);
        featureTypeMapping.addValue(cityCountry);
        featureTypeMapping.addValue(cityFunction);
        featureTypeMapping.addValue(cityFunctionNil);
        featureTypeMapping.addJoin(city2alternativeNameJoin);
        featureTypeMapping.addTable(alternativeName);
        featureTypeMapping.addValue(alternativeNameName);
        featureTypeMapping.addValue(alternativeNameLanguage);
        featureTypeMapping.addJoin(city2RiverJoin);
        featureTypeMapping.addTable(river);
        featureTypeMapping.addValue(riverHref);
        featureTypeMapping.addAssociationTarget(riverType);

        return featureTypeMapping;
    }

    private FeatureTypeMapping buildRiverFlat() {
        FeatureTypeMapping riverMapping = buildRiver();

        MappingTable river = riverMapping.getTable("river");

        MappingValue riverName = MappingValue.create(namespaces);
        riverName.setTable(river);
        riverName.setTarget("ci:name");
        riverName.setValue("name");

        MappingValue riverId = MappingValue.create(namespaces);
        riverId.setTable(river);
        riverId.setTarget("@gml:id");
        riverId.setValue("objid");

        riverMapping.addValue(riverName);
        riverMapping.addValue(riverId);

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
}

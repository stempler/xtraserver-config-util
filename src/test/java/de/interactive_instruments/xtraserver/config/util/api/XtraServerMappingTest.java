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

        //assertThat(test, is(equalTo(control)));
        com.shazam.shazamcrest.MatcherAssert.assertThat(actual, sameBeanAs(expected).ignoring(startsWith("applicationSchema")).ignoring(startsWith("namespaces")).ignoring(startsWith("prefix")).ignoring(startsWith("path")).ignoring(startsWith("mappingMode")));
    }

    @Test
    public void testImportFlatten() throws JAXBException, IOException, SAXException {

        XtraServerMapping actual = XtraServerMapping.createFromStream(Resources.asByteSource(Resources.getResource("cities-mapping-flattened.xml")).openBufferedStream(), applicationSchema);

        XtraServerMapping xtraServerMapping = buildCitiesMapping();

        XtraServerMapping expected = XtraServerMapping.create(applicationSchema);
        for (String featureType: xtraServerMapping.getFeatureTypeList(false)) {
            expected.addFeatureTypeMapping(xtraServerMapping.getFeatureTypeMapping(featureType, true).get());
        }

        //assertThat(test, is(equalTo(control)));
        com.shazam.shazamcrest.MatcherAssert.assertThat(actual, sameBeanAs(expected).ignoring(startsWith("applicationSchema")).ignoring(startsWith("namespaces")).ignoring(startsWith("prefix")).ignoring(startsWith("path")).ignoring(startsWith("mappingMode")));
    }

    @Test
    public void testExport() throws JAXBException, IOException, SAXException {

        XtraServerMapping xtraServerMapping = buildCitiesMapping();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xtraServerMapping.writeToStream(outputStream, false);

        System.out.println(outputStream.toString());

        Source expected = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
        Source actual = Input.fromByteArray(outputStream.toByteArray()).build();

        com.shazam.shazamcrest.MatcherAssert.assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)));
    }

    @Test
    public void testExportFanOut() throws JAXBException, IOException, SAXException {

        XtraServerMapping xtraServerMapping = buildCitiesMappingFlat();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xtraServerMapping.writeToStream(outputStream, false);

        System.out.println(outputStream.toString());

        Source expected = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
        Source actual = Input.fromByteArray(outputStream.toByteArray()).build();

        com.shazam.shazamcrest.MatcherAssert.assertThat(actual, isSimilarTo(expected).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)));
    }

    private XtraServerMapping buildCitiesMapping() throws IOException {
        XtraServerMapping xtraServerMapping = XtraServerMapping.create(this.applicationSchema);
        xtraServerMapping.addFeatureTypeMapping(buildAbstractFeature());
        xtraServerMapping.addFeatureTypeMapping(buildNamedGeoObject());
        xtraServerMapping.addFeatureTypeMapping(buildCity());
        xtraServerMapping.addFeatureTypeMapping(buildRiver());

        return xtraServerMapping;
    }

    private XtraServerMapping buildCitiesMappingFlat() throws IOException {

        XtraServerMapping xtraServerMapping = XtraServerMapping.create(this.applicationSchema);
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
        cityId.setValue("id");

        MappingValue riverId = MappingValue.create(namespaces);
        riverId.setTable(river);
        riverId.setTarget("@gml:id");
        riverId.setValue("id");

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
        cityId.setValue("id");

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

        MappingTable river = MappingTable.create();
        river.setName("river");
        river.setOidCol("id");
        river.setTarget("ci:passingRiver");

        MappingTable cityRiver = MappingTable.create();
        cityRiver.setName("city_river");
        cityRiver.setOidCol("id");
        //cityRiver.setTarget("ci:passingRiver");

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

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("ci:City", new QName("http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities", "CityType", "ci"), namespaces);
        featureTypeMapping.addTable(city);
        featureTypeMapping.addValue(cityLocation);
        featureTypeMapping.addValue(cityCountry);
        featureTypeMapping.addValue(cityFunction);
        featureTypeMapping.addValue(cityFunctionNil);
        featureTypeMapping.addValue(cityArea);
        featureTypeMapping.addValue(cityBeginLifespan);
        featureTypeMapping.addTable(alternativeName);
        featureTypeMapping.addJoin(city2alternativeNameJoin);
        featureTypeMapping.addValue(alternativeNameName);
        featureTypeMapping.addValue(alternativeNameLanguage);
        featureTypeMapping.addTable(river);
        featureTypeMapping.addTable(cityRiver);
        featureTypeMapping.addJoin(city2RiverJoin);
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
        riverId.setValue("id");

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

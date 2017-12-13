package de.interactive_instruments.xtraserver.config.util.api;

import com.google.common.io.Resources;
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

import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class XtraServerMappingTest {

    @Test
    public void testExport() throws JAXBException, IOException, SAXException {

        XtraServerMapping xtraServerMapping = XtraServerMapping.create();
        xtraServerMapping.addFeatureTypeMapping(buildAbstractFeature());
        xtraServerMapping.addFeatureTypeMapping(buildNamedGeoObject());
        xtraServerMapping.addFeatureTypeMapping(buildCity());
        xtraServerMapping.addFeatureTypeMapping(buildRiver());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xtraServerMapping.writeToStream(outputStream);

        System.out.println(outputStream.toString());

        Source control = Input.fromURL(Resources.getResource("cities-mapping.xml")).build();
        Source test = Input.fromByteArray(outputStream.toByteArray()).build();

        assertThat(test, isSimilarTo(control).ignoreComments().ignoreWhitespace().withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)));
    }

    public FeatureTypeMapping buildAbstractFeature() {

        MappingTable city = MappingTable.create();
        city.setName("city");
        city.setOidCol("id");

        MappingTable river = MappingTable.create();
        river.setName("river");
        river.setOidCol("id");

        MappingValue cityId = MappingValue.create();
        cityId.setTable(city);
        cityId.setTarget("@gml:id");
        cityId.setValue("objid");

        MappingValue riverId = MappingValue.create();
        riverId.setTable(river);
        riverId.setTarget("@gml:id");
        riverId.setValue("objid");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("gml:AbstractFeature", new QName("http://www.opengis.net/gml/3.2", "AbstractFeatureType", "gml"));
        featureTypeMapping.addTable(city);
        featureTypeMapping.addTable(river);
        featureTypeMapping.addValue(cityId);
        featureTypeMapping.addValue(riverId);

        return featureTypeMapping;
    }

    public FeatureTypeMapping buildNamedGeoObject() {

        MappingTable city = MappingTable.create();
        city.setName("city");
        city.setOidCol("id");

        MappingTable river = MappingTable.create();
        river.setName("river");
        river.setOidCol("id");

        MappingValue cityName = MappingValue.create();
        cityName.setTable(city);
        cityName.setTarget("ci:name");
        cityName.setValue("name");

        MappingValue riverName = MappingValue.create();
        riverName.setTable(river);
        riverName.setTarget("ci:name");
        riverName.setValue("name");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("ci:NamedGeoObject", new QName("http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities", "NamedGeoObjectType", "ci"));
        featureTypeMapping.addTable(city);
        featureTypeMapping.addTable(river);
        featureTypeMapping.addValue(cityName);
        featureTypeMapping.addValue(riverName);

        return featureTypeMapping;
    }

    public FeatureTypeMapping buildCity() {

        MappingTable city = MappingTable.create();
        city.setName("city");
        city.setOidCol("id");

        MappingValue cityLocation = MappingValue.create();
        cityLocation.setTable(city);
        cityLocation.setTarget("ci:location");
        cityLocation.setValue("location");

        MappingValue cityCountry = MappingValue.create();
        cityCountry.setTable(city);
        cityCountry.setTarget("ci:country");
        cityCountry.setValue("Germany");
        cityCountry.setValueType("constant");

        MappingValue cityFunction = MappingValue.create();
        cityFunction.setTable(city);
        cityFunction.setTarget("ci:function");
        cityFunction.setValue("'urn:ci:function::' || $T$.function");
        cityFunction.setValueType("expression");

        MappingValue cityFunctionNil = MappingValue.create();
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
        city.addJoinPath(city2alternativeNameJoin);

        MappingValue alternativeNameName = MappingValue.create();
        alternativeNameName.setTable(alternativeName);
        alternativeNameName.setTarget("ci:alternativeName/ci:AlternativeName/ci:name");
        alternativeNameName.setValue("name");

        MappingValue alternativeNameLanguage = MappingValue.create();
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
        city.addJoinPath(city2RiverJoin);

        MappingValue riverHref = MappingValue.create();
        riverHref.setTable(river);
        riverHref.setTarget("ci:passingRiver/@xlink:href");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("ci:City", new QName("http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities", "CityType", "ci"));
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

        return featureTypeMapping;
    }

    public FeatureTypeMapping buildRiver() {

        MappingTable river = MappingTable.create();
        river.setName("river");
        river.setOidCol("id");

        MappingValue riverLocation = MappingValue.create();
        riverLocation.setTable(river);
        riverLocation.setTarget("ci:location");
        riverLocation.setValue("location");

        MappingValue riverLength = MappingValue.create();
        riverLength.setTable(river);
        riverLength.setTarget("ci:length");
        riverLength.setValue("length");

        FeatureTypeMapping featureTypeMapping = FeatureTypeMapping.create("ci:River", new QName("http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities", "RiverType", "ci"));
        featureTypeMapping.addTable(river);
        featureTypeMapping.addValue(riverLocation);
        featureTypeMapping.addValue(riverLength);

        return featureTypeMapping;
    }
}

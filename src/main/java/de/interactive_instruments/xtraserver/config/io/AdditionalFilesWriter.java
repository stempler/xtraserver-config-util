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

import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author zahnen
 */
class AdditionalFilesWriter {

    void generate(final ZipOutputStream zipStream, final XtraServerMapping xtraServerMapping) throws IOException {

        zipStream.putNextEntry(new ZipEntry("XtraSrvConfig_FeatureTypes.inc.xml"));
        createFeatureTypesFile(zipStream, xtraServerMapping);

        zipStream.putNextEntry(new ZipEntry("XtraSrvConfig_Geoindexes.inc.xml"));
        createGeoIndexesFile(zipStream, xtraServerMapping);

        zipStream.putNextEntry(new ZipEntry("XtraSrvConfig_GetSpatialDataSetSQ.inc.xml"));
        Resources.asByteSource(Resources.getResource(JaxbWriter.class, "/XtraSrvConfig_GetSpatialDataSetSQ.inc.xml.start")).copyTo(zipStream);
        createGetSpatialDataSetSQFileEnd(zipStream, xtraServerMapping);

        zipStream.putNextEntry(new ZipEntry("XtraSrvConfig_StoredQueriesToCache.inc.xml"));
        Resources.asByteSource(Resources.getResource(JaxbWriter.class, "/XtraSrvConfig_StoredQueriesToCache.inc.xml")).copyTo(zipStream);

        zipStream.putNextEntry(new ZipEntry("XtraSrvConfig_VirtualTables.inc.xml"));
        createVirtualTablesFile(zipStream, xtraServerMapping);

    }

    private void createFeatureTypesFile(final OutputStream outputStream, final XtraServerMapping xtraServerMapping) throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<FeatureTypes xmlns=\"http://www.interactive-instruments.de/namespaces/XtraServer\">\n");

        xtraServerMapping.getFeatureTypeMappings(true).forEach(featureTypeMapping -> {
            final String featureTypeName = featureTypeMapping.getName();
            final String featureTypeNameWithoutPrefix = featureTypeMapping.getQualifiedName().getLocalPart();

            try {
                writer.append("\t<FeatureType defaultSRS=\"{if {$defaultSRS}}{$defaultSRS}{else}EPSG:{$nativeEpsgCode}{fi}\" supportedSRSs=\"srslistWFS\">\n\t\t");
                writer.append("<Name>");
                writer.append(featureTypeName);
                writer.append("</Name>\n\t\t");
                writer.append("{if {$featureTypes.metadataUrl.enabled} == true}{if {$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".metadataUrl}}{if {$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".WFS11.metadataFormat}}{if {$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".WFS11.metadataType}}<MetadataURL format=\"{$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".WFS11.metadataFormat}\" type=\"{$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".WFS11.metadataType}\">{$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".metadataUrl}</MetadataURL>{fi}{else}<MetadataURL>{$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".metadataUrl}</MetadataURL>{fi}{fi}{fi}\n\t");
                writer.append("</FeatureType>\n");
            } catch (final IOException e) {
                // ignore
            }
        });

        writer.append("</FeatureTypes>\n");
        writer.flush();
    }

    // TODO: test
    private void createGeoIndexesFile(final OutputStream outputStream, final XtraServerMapping xtraServerMapping) throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<GeoIndexes xmlns=\"http://www.interactive-instruments.de/namespaces/XtraServer\">\n");

        xtraServerMapping.getFeatureTypeMappings(true).forEach(featureTypeMapping -> {
            final String featureTypeName = featureTypeMapping.getName();
            final String featureTypeNameWithoutPrefix = featureTypeMapping.getQualifiedName().getLocalPart();

            // find the first geometric property descending from top to bottom of the inheritance tree
            final Optional<MappingValue> geometricProperty = xtraServerMapping.getFeatureTypeMappingInheritanceChain(featureTypeName).stream()
                    .map(FeatureTypeMapping::getGeometry)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();

            geometricProperty.ifPresent(mappingValue -> {
                final String propertyName = mappingValue.getTargetPath();
                final String propertyNameWithoutPrefix = Splitter.on(':').splitToList(propertyName).get(1);

                try {
                    writer.append("\t<GeoIndex id=\"gidx_");
                    writer.append(featureTypeNameWithoutPrefix);
                    writer.append("_");
                    writer.append(propertyNameWithoutPrefix);
                    writer.append("\">\n\t\t");
                    writer.append("<PGISGeoIndexImpl>\n\t\t\t");
                    writer.append("<PGISGeoIndexFeatures>\n\t\t\t\t");
                    writer.append("<PGISFeatureType>");
                    writer.append(featureTypeName);
                    writer.append("</PGISFeatureType>\n\t\t\t\t");
                    writer.append("<PGISGeoPropertyName>");
                    writer.append(propertyName);
                    writer.append("</PGISGeoPropertyName>\n\t\t\t");
                    writer.append("</PGISGeoIndexFeatures>\n\t\t");
                    writer.append("</PGISGeoIndexImpl>\n\t");
                    writer.append("</GeoIndex>\n");
                } catch (final IOException e) {
                    // ignore
                }
            });
        });

        writer.append("</GeoIndexes>\n");
        writer.flush();
    }

    private void createGetSpatialDataSetSQFileEnd(final OutputStream outputStream, final XtraServerMapping xtraServerMapping) throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        xtraServerMapping.getFeatureTypeNames(true).forEach(featureTypeName -> {
            try {
                writer.append("\t\t\t<wfs:Query srsName=\"${CRS}\" typeNames=\"");
                writer.append(featureTypeName);
                writer.append("\"/>\n");
            } catch (final IOException e) {
                // ignore
            }
        });

        writer.append("\t\t</wfs:QueryExpressionText>\n");
        writer.append("\t</StoredQueryDefinition>\n");
        writer.append("</InitialStoredQueries>\n");
        writer.flush();
    }

    private void createVirtualTablesFile(final OutputStream outputStream, final XtraServerMapping xtraServerMapping) throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<VirtualTables xmlns=\"http://www.interactive-instruments.de/namespaces/XtraServer\">\n");

        xtraServerMapping.getVirtualTables().forEach(virtualTable -> {

                try {
                    writer.append("\t<VirtualTable name=\"");
                    writer.append(virtualTable.getName());
                    writer.append("\" query=\"");
                    writer.append(virtualTable.getQuery());
                    writer.append("\"/>\n");
                } catch (final IOException e) {
                    // ignore
                }
        });

        writer.append("</VirtualTables>\n");
        writer.flush();
    }

}

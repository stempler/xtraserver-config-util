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
package de.interactive_instruments.xtraserver.config.transformer;

import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;

import java.net.URI;

/**
 * Transformer chain builder for {@link XtraServerMapping}s.
 * Every transformer takes an immutable {@link XtraServerMapping} as input and will generate a new immutable {@link XtraServerMapping} as output.
 *
 * @author zahnen
 */
public class XtraServerMappingTransformer {

    private final XtraServerMapping xtraServerMapping;
    private final ApplicationSchema applicationSchema;
    private final URI applicationSchemaUri;
    private final boolean flattenInheritance;
    private final boolean fanOutInheritance;
    private final boolean ensureRelationNavigability;

    private XtraServerMappingTransformer(final XtraServerMapping xtraServerMapping, final URI applicationSchemaUri, final boolean flattenInheritance, final boolean fanOutInheritance, final boolean ensureRelationNavigability) {
        this.xtraServerMapping = xtraServerMapping;
        this.applicationSchemaUri = applicationSchemaUri;
        this.applicationSchema = new ApplicationSchema(applicationSchemaUri);
        this.flattenInheritance = flattenInheritance;
        this.fanOutInheritance = fanOutInheritance;
        this.ensureRelationNavigability = ensureRelationNavigability;
    }

    /**
     * Create a new transformer chain builder for the given {@link XtraServerMapping}
     *
     * @param xtraServerMapping the mapping that should be transformed
     * @return the transformer builder
     */
    public static SchemaInfo forMapping(final XtraServerMapping xtraServerMapping) {
        return new Builder(xtraServerMapping);
    }

    private XtraServerMapping transform() {
        XtraServerMapping transformedXtraServerMapping = xtraServerMapping;
        String description = xtraServerMapping.getDescription() + "\n  Transformations:\n    - applySchemaInfo (" + applicationSchemaUri.toString() + ")\n";

        transformedXtraServerMapping = new MappingTransformerSchemaInfo(transformedXtraServerMapping, applicationSchema).transform();

        if (flattenInheritance) {
            transformedXtraServerMapping = new MappingTransformerFlattenInheritance(transformedXtraServerMapping).transform();
            description += "    - flattenInheritance\n";
        }
        if (fanOutInheritance) {
            transformedXtraServerMapping = new MappingTransformerFanOutInheritance(transformedXtraServerMapping, applicationSchema).transform();
            description += "    - fanOutInheritance\n";
        }
        if (ensureRelationNavigability) {
            transformedXtraServerMapping = new MappingTransformerRelationNavigability(transformedXtraServerMapping).transform();
            description += "    - ensureRelationNavigability\n";
        }

        transformedXtraServerMapping = new XtraServerMappingBuilder()
                .copyOf(transformedXtraServerMapping)
                .description(description)
                .build();


        return transformedXtraServerMapping;
    }

    /**
     * Schema info transformers for {@link XtraServerMapping}s
     */
    public interface SchemaInfo {
        /**
         * Adds schema info like qualified names, super types and geometry properties to the mapping.
         * Prerequisite for all other transformers
         *
         * @param applicationSchemaUri the application schema URI
         * @return the transformer builder
         */
        Transform applySchemaInfo(URI applicationSchemaUri);
    }

    /**
     * Structural transformers for {@link XtraServerMapping}s
     */
    public interface Transform {
        /**
         * Merges mappings attached to super types down to the lowest sub types in the inheritance tree.
         * For example mappings for gml:id that are contained in the feature type mapping for gml:AbstractFeature
         * will be moved to the respective sub type mappings and the mapping for gml:AbstractFeature will be removed.
         *
         * @return the transformer builder
         */
        Transform flattenInheritance();

        /**
         * Fans out mappings attached to sub types to the corresponding super types in the inheritance tree.
         * For example mappings for gml:id that are contained in a feature type mapping will be moved to the mapping
         * for gml:AbstractFeature, which will be created if does not exist yet.
         *
         * @return the transformer builder
         */
        Transform fanOutInheritance();

        /**
         * Ensures that XtraServer will be able to navigate relations, e.g. for a GetFeature request with resolveDepth.
         * For example the transformer will check if for each reference mapping joins are provided that establish a
         * connection to all of the referenced feature types primary tables. Missing joins will then be added.
         *
         * @return the transformer builder
         */
        Transform ensureRelationNavigability();

        /**
         * Executes the transformer chain
         *
         * @return the transformed {@link XtraServerMapping}
         */
        XtraServerMapping transform();
    }

    private static class Builder implements SchemaInfo, Transform {
        private final XtraServerMapping xtraServerMapping;
        private URI applicationSchemaUri;
        private boolean flattenInheritance;
        private boolean fanOutInheritance;
        private boolean ensureRelationNavigability;

        Builder(final XtraServerMapping xtraServerMapping) {
            this.xtraServerMapping = xtraServerMapping;
        }

        @Override
        public Transform applySchemaInfo(final URI applicationSchemaUri) {
            this.applicationSchemaUri = applicationSchemaUri;
            return this;
        }

        @Override
        public Transform flattenInheritance() {
            this.flattenInheritance = true;
            return this;
        }

        @Override
        public Transform fanOutInheritance() {
            this.fanOutInheritance = true;
            return this;
        }

        @Override
        public Transform ensureRelationNavigability() {
            this.ensureRelationNavigability = true;
            return this;
        }

        @Override
        public XtraServerMapping transform() {
            return new XtraServerMappingTransformer(xtraServerMapping, applicationSchemaUri, flattenInheritance, fanOutInheritance, ensureRelationNavigability)
                    .transform();
        }
    }
}

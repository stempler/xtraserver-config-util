/**
 * Copyright 2018 interactive instruments GmbH
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents all mappings for a certain feature type
 *
 * @author zahnen
 */
public class FeatureTypeMapping {
    private final String name;
    private final QName qualifiedName;
    private final String superTypeName;
    private final boolean isAbstract;
    private final ImmutableSet<MappingTable> tables;

    FeatureTypeMapping(final String name, final QName qualifiedName, final String superTypeName, final boolean isAbstract, final List<MappingTable> tables) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.superTypeName = superTypeName;
        this.isAbstract = isAbstract;
        this.tables = ImmutableSet.copyOf(tables);
    }

    /**
     * Returns the prefixed feature type name
     *
     * @return the prefixed feature type name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the qualified feature type name
     *
     * @return the qualified feature type name
     */
    public QName getQualifiedName() {
        return qualifiedName;
    }

    /**
     * Returns the prefixed super type name
     *
     * @return the prefixed super type name
     */
    public Optional<String> getSuperTypeName() {
        return Optional.ofNullable(superTypeName);
    }

    /**
     * Is the feature type abstract?
     *
     * @return true if abstract
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * Returns the primary table mappings
     *
     * @return the list of {@link MappingTable}s
     */
    public ImmutableList<MappingTable> getPrimaryTables() {
        return tables.asList();
    }

    /**
     * Returns the primary table names for this mapping
     *
     * @return the list of table names
     */
    public List<String> getPrimaryTableNames() {

        return tables.stream().map(MappingTable::getName).collect(Collectors.toList());
    }

    /**
     * Does a mapping exist for the given table name?
     *
     * @param name the table name
     * @return true if exists
     */
    public boolean hasTable(final String name) {
        return getTableOptional(name).isPresent();
    }

    /**
     * Returns the mapping for the given table name if found
     *
     * @param name the table name
     * @return the {@link MappingTable}, if exists
     */
    public Optional<MappingTable> getTable(final String name) {
        return getTableOptional(name);
    }

    /**
     * Return the first found value mapping of type geometry contained in the nested {@link MappingTable}s
     *
     * @return a {@link MappingValue} of type geometry, if found
     */
    public Optional<MappingValue> getGeometry() {
        return tables.stream()
                .flatMap(MappingTable::getAllValuesStream)
                .filter(MappingValue::isGeometry)
                .findFirst();
    }

    /**
     * Returns all table and value mappings, where the value target path matches the given one
     *
     * @param targetPath the mapping target path
     * @return a map of found {@link MappingTable}s and {@link MappingValue}s
     */
    public ImmutableMap<MappingTable, MappingValue> getTableValuesForPath(final String targetPath) {
        final ImmutableMap.Builder<MappingTable, MappingValue> map = ImmutableMap.builder();

        tables.stream()
                .filter(table -> table.hasValueForPath(targetPath))
                .forEach(table -> map.putAll(table.getTableValuesForPath(targetPath)));

        return map.build();
    }

    private Optional<MappingTable> getTableOptional(final String tableName) {
        return tables.stream()
                .filter(table -> tableName.equals(table.getName()))
                .findFirst();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FeatureTypeMapping that = (FeatureTypeMapping) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(qualifiedName, that.qualifiedName) &&
                Objects.equals(tables, that.tables);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, qualifiedName, tables);
    }

    @Override
    public String toString() {
        return "\nFeatureTypeMappingImpl{" +
                "\nname='" + name + '\'' +
                "\n, tables=" + tables +
                "\n}";
    }
}

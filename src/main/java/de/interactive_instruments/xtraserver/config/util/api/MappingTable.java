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
package de.interactive_instruments.xtraserver.config.util.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Represents all mappings for a certain table
 *
 * @author zahnen
 */
public class MappingTable {

    private final String name;
    private final String primaryKey;
    private final String targetPath;
    private final List<QName> qualifiedTargetPath;
    private final String description;
    private final String predicate;

    private final ImmutableSet<MappingTable> joiningTables;
    private final ImmutableSet<MappingValue> values;
    private final ImmutableSet<MappingJoin> joinPaths;

    MappingTable(final String name, final String primaryKey, final String targetPath, List<QName> qualifiedTargetPath, final String description, final String predicate, final List<MappingTable> joiningTables, final List<MappingValue> values, final List<MappingJoin> joinPaths) {
        this.name = name;
        this.primaryKey = primaryKey;
        this.targetPath = targetPath;
        this.qualifiedTargetPath = qualifiedTargetPath;
        this.description = description;
        this.predicate = predicate;
        this.joiningTables = ImmutableSet.copyOf(joiningTables);
        this.values = ImmutableSet.copyOf(values);
        this.joinPaths = ImmutableSet.copyOf(joinPaths);
    }

    /**
     * Returns the table name
     *
     * @return the table name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the primary key column name
     *
     * @return the primary key
     */
    public String getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Returns the mapping target path
     *
     * @return the target path
     */
    public String getTargetPath() {
        return targetPath;
    }

    /**
     * Returns the list of qualified path elements in the target path
     *
     * @return the target path
     */
    public List<QName> getQualifiedTargetPath() {
        return qualifiedTargetPath;
    }

    /**
     * Returns the description
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the predicate
     *
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * Returns the value mappings
     *
     * @return the list of {@link MappingValue}s
     */
    public ImmutableSet<MappingValue> getValues() {
        return values;
    }

    /**
     * Returns the joined table mappings
     *
     * @return the list of {@link MappingTable}s
     */
    public ImmutableSet<MappingTable> getJoiningTables() {
        return joiningTables;
    }

    /**
     * Returns the join paths
     *
     * @return the list of {@link MappingJoin}s
     */
    public ImmutableSet<MappingJoin> getJoinPaths() {
        return joinPaths;
    }

    /**
     * Is this a primary table?
     *
     * @return true if primary
     */
    public boolean isPrimary() {
        return (targetPath == null || targetPath.isEmpty()) && joinPaths.isEmpty();
    }

    /**
     * Is this a joined table?
     *
     * @return true if joined
     */
    public boolean isJoined() {
        return targetPath != null && !targetPath.isEmpty() && !joinPaths.isEmpty();
    }

    /**
     * Is this a table predicate mapping?
     *
     * @return true if predicate
     */
    public boolean isPredicate() {
        return targetPath != null && !targetPath.isEmpty() && predicate != null && !predicate.isEmpty();
    }

    /**
     * Does a value mapping exist for the given target path?
     *
     * @param targetPath the target path
     * @return true if exists
     */
    public boolean hasValueForPath(final String targetPath) {
        return values.stream().anyMatch(value -> value.getTargetPath().equals(targetPath))
                || joiningTables.stream().anyMatch(joiningTable -> joiningTable.hasValueForPath(targetPath));
    }

    /**
     * Returns all table and value mappings, where the value target path matches the given one
     *
     * @param targetPath the mapping target path
     * @return a map of found {@link MappingTable}s and {@link MappingValue}s
     */
    public ImmutableMap<MappingTable, MappingValue> getTableValuesForPath(final String targetPath) {
        ImmutableMap.Builder<MappingTable, MappingValue> map = ImmutableMap.builder();

        // TODO: shallow copy with nested value instead of this ??? or just return tables and get values with additional function?
        values.stream()
                .filter(value -> value.getTargetPath().equals(targetPath))
                .findFirst()
                .ifPresent(mappingValue -> map.put(this, mappingValue));

        joiningTables.stream()
                .filter(joiningTable -> joiningTable.hasValueForPath(targetPath))
                .forEach(mappingTable -> map.putAll(mappingTable.getTableValuesForPath(targetPath)));

        return map.build();
    }

    Stream<MappingValue> getAllValues() {
        final Stream<MappingValue> joinedValues = joiningTables.stream()
                .flatMap(mappingTable -> mappingTable.getValues().stream());

        return Stream.concat(values.stream(), joinedValues);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappingTable that = (MappingTable) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(primaryKey, that.primaryKey) &&
                Objects.equals(targetPath, that.targetPath) &&
                Objects.equals(description, that.description) &&
                Objects.equals(joiningTables, that.joiningTables) &&
                Objects.equals(values, that.values) &&
                Objects.equals(joinPaths, that.joinPaths);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, primaryKey, targetPath, description, joiningTables, values, joinPaths);
    }

    @Override
    public String toString() {
        return "MappingTable{" +
                "name='" + name + '\'' +
                ", primaryKey='" + primaryKey + '\'' +
                ", targetPath='" + targetPath + '\'' +
                ", description='" + description + '\'' +
                ", joiningTables=" + joiningTables +
                ", values=" + values +
                ", joinPaths=" + joinPaths +
                '}';
    }
}

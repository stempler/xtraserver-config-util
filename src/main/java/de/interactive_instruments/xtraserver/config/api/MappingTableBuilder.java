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

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Builder for {@link MappingTable}
 *
 * @author zahnen
 */
public class MappingTableBuilder {

    private String name;
    private String primaryKey;
    private String targetPath;
    private List<QName> qualifiedTargetPath;
    private String description;
    private String predicate;
    private final List<MappingValue> values;
    private final List<MappingTable> joiningTables;
    private final List<MappingJoin> joinPaths;

    /**
     * Create a new builder
     */
    public MappingTableBuilder() {
        this.joiningTables = new ArrayList<>();
        this.values = new ArrayList<>();
        this.joinPaths = new ArrayList<>();
    }

    /**
     * Set the table name (required)
     *
     * @param name table name
     * @return the builder
     */
    public MappingTableBuilder name(final String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the primary key (required)
     *
     * @param primaryKey primary key
     * @return the builder
     */
    public MappingTableBuilder primaryKey(final String primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    /**
     * Set the target path (must be empty for primary tables, required for joining tables)
     *
     * @param targetPath target path
     * @return the builder
     */
    public MappingTableBuilder targetPath(final String targetPath) {
        this.targetPath = targetPath;
        return this;
    }

    /**
     * Set the qualified target path
     *
     * @param qualifiedTargetPath list of qualified path elements
     * @return the builder
     */
    public MappingTableBuilder qualifiedTargetPath(final List<QName> qualifiedTargetPath) {
        this.qualifiedTargetPath = qualifiedTargetPath;
        return this;
    }

    /**
     * Set the description
     *
     * @param description description
     * @return the builder
     */
    public MappingTableBuilder description(final String description) {
        this.description = description;
        return this;
    }

    /**
     * Set the predicate
     *
     * @param predicate predicate
     * @return the builder
     */
    public MappingTableBuilder predicate(final String predicate) {
        this.predicate = predicate;
        return this;
    }

    /**
     * Add a value mapping.
     * Value mappings can be created with {@link MappingValueBuilder}
     *
     * @param mappingValue the value mapping
     * @return the builder
     */
    public MappingTableBuilder value(final MappingValue mappingValue) {
        this.values.add(mappingValue);
        return this;
    }

    /**
     * Add a list of value mappings.
     * Value mappings can be created with {@link MappingValueBuilder}
     *
     * @param mappingValues the value mappings
     * @return the builder
     */
    public MappingTableBuilder values(final Collection<MappingValue> mappingValues) {
        this.values.addAll(mappingValues);
        return this;
    }

    /**
     * Add a joining table (the joining table needs to have at least one join path where the source is the table that is to be build).
     * Joining tables can be created with {@link MappingTableBuilder}
     *
     * @param mappingTable the joining table
     * @return the builder
     */
    public MappingTableBuilder joiningTable(final MappingTable mappingTable) {
        this.joiningTables.add(mappingTable);
        return this;
    }

    /**
     * Add a list of joining tables (each joining table needs to have at least one join path where the source is the table that is to be build).
     * Joining tables can be created with {@link MappingTableBuilder}
     *
     * @param mappingTables the joining tables
     * @return the builder
     */
    public MappingTableBuilder joiningTables(final Collection<MappingTable> mappingTables) {
        this.joiningTables.addAll(mappingTables);
        return this;
    }

    /**
     * Add a join path (for joining tables at least one join path is required) (the target has to be the table that is to be build).
     * Join paths can be created with {@link MappingJoinBuilder}
     *
     * @param mappingJoin the join path
     * @return the builder
     */
    public MappingTableBuilder joinPath(final MappingJoin mappingJoin) {
        this.joinPaths.add(mappingJoin);
        return this;
    }

    /**
     * Add a list of join paths (for joining tables at least one join path is required) (the target has to be the table that is to be build).
     * Join paths can be created with {@link MappingJoinBuilder}
     *
     * @param mappingJoins the join paths
     * @return the builder
     */
    public MappingTableBuilder joinPaths(final Collection<MappingJoin> mappingJoins) {
        this.joinPaths.addAll(mappingJoins);
        return this;
    }

    /**
     * Builds the {@link MappingTable}, validates required fields
     *
     * @return a new immutable {@link MappingTable}
     */
    public MappingTable build() {

        autoComplete();

        final MappingTable mappingTable = new MappingTable(name, primaryKey, targetPath, ImmutableList.copyOf(qualifiedTargetPath), description, predicate, ImmutableList.copyOf(joiningTables), ImmutableList.copyOf(values), ImmutableList.copyOf(joinPaths));

        validate(mappingTable);

        return mappingTable;
    }

    /**
     * Copy name, primaryKey, targetPath, qualifiedTargetPath, description and predicate from given {@link MappingTable}
     *
     * @param mappingTable the copy source
     * @return the builder
     */
    public MappingTableBuilder shallowCopyOf(final MappingTable mappingTable) {
        this.name = mappingTable.getName();
        this.primaryKey = mappingTable.getPrimaryKey();
        this.targetPath = mappingTable.getTargetPath();
        this.qualifiedTargetPath = mappingTable.getQualifiedTargetPath();
        this.description = mappingTable.getDescription();
        this.predicate = mappingTable.getPredicate();

        return this;
    }

    /**
     * Same as {@link MappingTableBuilder#shallowCopyOf(MappingTable)}, additionally copies values, joining tables and join paths
     *
     * @param mappingTable the copy source
     * @return the builder
     */
    public MappingTableBuilder copyOf(final MappingTable mappingTable) {
        shallowCopyOf(mappingTable);

        this.joinPaths.addAll(mappingTable.getJoinPaths());
        this.joiningTables.addAll(mappingTable.getJoiningTables());
        this.values.addAll(mappingTable.getValues());

        return this;
    }

    /**
     * Builds a {@link MappingTableDraft}, omits validation
     *
     * @return a new immutable {@link MappingTableDraft}
     */
    public MappingTableDraft buildDraft() {
        autoComplete();

        return new MappingTableDraft(name, primaryKey, targetPath, ImmutableList.copyOf(qualifiedTargetPath), description, predicate, ImmutableList.copyOf(joiningTables), ImmutableList.copyOf(values), ImmutableList.copyOf(joinPaths));
    }

    /**
     * A mapping table draft is a potentially invalid {@link MappingTable} that can be used for iterative building.
     * Mapping table drafts are not valid inputs for {@link FeatureTypeMappingBuilder#primaryTable(MappingTable)} or {@link MappingTableBuilder#joiningTable(MappingTable)}.
     *
     * @see MappingTable
     */
    public static class MappingTableDraft extends MappingTable {
        MappingTableDraft(final String name, final String primaryKey, final String targetPath, final List<QName> qualifiedTargetPath, final String description, final String predicate, final List<MappingTable> joiningTables, final List<MappingValue> values, final List<MappingJoin> joinPaths) {
            super(name, primaryKey, targetPath, qualifiedTargetPath, description, predicate, joiningTables, values, joinPaths);
        }
    }

    private void autoComplete() {
        if (targetPath == null) {
            this.targetPath = "";
        }
        if (qualifiedTargetPath == null) {
            this.qualifiedTargetPath = new ArrayList<>();
        }

        // if join has target, overwrite table target (for table definitions parsed from value mappings)
        /*if (mappingJoin.getTargetPath() != null && !mappingJoin.getTargetPath().isEmpty()) {
            targetTable.get().setTarget(mappingJoin.getTargetPath());
        }*/
        // if join has no target, overwrite with table target (for exports from hale)
        /*else {
            mappingJoin.setTarget(targetTable.get().getTargetPath());
        }*/
    }

    private void validate(final MappingTable mappingTable) {
        if (mappingTable.getName() == null || mappingTable.getName().isEmpty()) {
            throw new IllegalStateException("Table has no name");
        }
        if (!mappingTable.isPredicate() && (mappingTable.getPrimaryKey() == null || mappingTable.getPrimaryKey().isEmpty())) {
            throw new IllegalStateException("Table has no primary key");
        }

        if (!mappingTable.isPrimary() && !mappingTable.isJoined() && !mappingTable.isPredicate()) {
            throw new IllegalStateException("Invalid table. Valid configurations are primary (no targetPath + no joinPaths), joined (targetPath + at least one joinPath) and predicate (targetPath + predicate).");
        }

        if (mappingTable.getJoiningTables().stream().anyMatch(MappingTableBuilder.MappingTableDraft.class::isInstance)) {
            throw new IllegalStateException("Joining tables contain MappingTableDraft");
        }

        if (mappingTable.getJoiningTables().stream().flatMap(joiningTable -> joiningTable.getJoinPaths().stream())
                .anyMatch(mappingJoin -> !mappingJoin.getSourceTable().equals(mappingTable.getName()))) {
            throw new IllegalStateException("Join source table of joining table does not match");
        }

        if (mappingTable.getJoinPaths().stream()
                .anyMatch(mappingJoin -> !mappingJoin.getTargetTable().equals(mappingTable.getName()))) {
            throw new IllegalStateException("Join target table does not match");
        }
    }
}

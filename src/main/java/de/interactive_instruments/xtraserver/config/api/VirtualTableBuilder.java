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

import com.google.common.base.Joiner;

import java.util.*;
import java.util.stream.Collectors;

public class VirtualTableBuilder {
    private String name;
    private String query;
    private final Set<MappingJoin> joinPaths;
    private final Set<String> columns;

    public VirtualTableBuilder() {
        joinPaths = new LinkedHashSet<>();
        columns = new LinkedHashSet<>();
    }

    public VirtualTableBuilder name(String name) {
        this.name = name;
        return this;
    }

    public VirtualTableBuilder query(String query) {
        this.query = query;
        return this;
    }

    public VirtualTableBuilder originalTable(final MappingTable mappingTable) {
        this.joinPaths.addAll(mappingTable.getJoinPaths());

        if (columns.isEmpty()) {
            columns.add(mappingTable.getName() + "." + mappingTable.getPrimaryKey());
        }

        mappingTable.getValues().stream()
                    .flatMap(mappingValue -> mappingValue.getValueColumns().stream())
                    .map(column -> mappingTable.getName() + "." + column)
                    .forEach(columns::add);
        return this;
    }

    public VirtualTable build() {
        if (query == null && !joinPaths.isEmpty()) {
            final String primaryTable = joinPaths.iterator().next()
                                                 .getSourceTable();
            if (joinPaths.stream()
                         .anyMatch(mappingJoin -> !mappingJoin.getSourceTable()
                                                              .equals(primaryTable))) {
                System.out.println("WARNING: joins for VirtualTable " + name + " have differing source tables");
            }
            query = "SELECT ";
            query += Joiner.on(",").join(columns) + " FROM " + primaryTable + " ";
            query += joinPaths.stream()
                              .flatMap(mappingJoin -> mappingJoin.getJoinConditions().stream())
                              .map(condition -> "INNER JOIN " + condition.getTargetTable() + " ON " + condition.getTargetTable() + "." + condition.getTargetField() + " = " + condition.getSourceTable() + "." + condition.getSourceField() + " ")
                              .collect(Collectors.joining());
        }

        return new VirtualTable(name, query);
    }
}
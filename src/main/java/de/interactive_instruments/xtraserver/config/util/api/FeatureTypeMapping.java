package de.interactive_instruments.xtraserver.config.util.api;

import java.util.Collection;
import java.util.List;

/**
 * @author zahnen
 */
public interface FeatureTypeMapping {
    String getName();

    Collection<String> getPrimaryTableNames();

    Collection<String> getJoinedTableNames();

    Collection<String> getReferenceTableNames();

    List<MappingTable> getTables();

    List<MappingJoin> getJoins();

    List<MappingValue> getValues();
}

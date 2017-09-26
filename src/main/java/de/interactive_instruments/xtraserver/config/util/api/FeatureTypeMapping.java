package de.interactive_instruments.xtraserver.config.util.api;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.List;

/**
 * @author zahnen
 */
public interface FeatureTypeMapping {
    String getName();

    QName getQName();

    Collection<String> getPrimaryTableNames();

    Collection<String> getJoinedTableNames();

    Collection<String> getReferenceTableNames();

    List<MappingTable> getTables();

    List<MappingJoin> getJoins();

    List<MappingValue> getValues();

    boolean hasTable(String name);

    MappingTable getTable(String name);

    boolean hasValueMappingForTable(String name);

    boolean hasValueMappingForTable(String name, String target);

    boolean hasReferenceMappingForTable(String name, String target);
}

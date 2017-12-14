package de.interactive_instruments.xtraserver.config.util.api;

import de.interactive_instruments.xtraserver.config.util.FeatureTypeMappingImpl;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.List;

/**
 * A collection of mappings related to a specific FeatureType
 * @author zahnen
 */
public interface FeatureTypeMapping {

    static FeatureTypeMapping create(String name, QName qualifiedTypeName) {

        return new FeatureTypeMappingImpl(name, qualifiedTypeName);
    }

    /**
     * Get the local name of the FeatureType
     * @return
     */
    String getName();

    /**
     * Get the qualified name of the FeatureType
     * @return
     */
    QName getQName();

    /**
     * Get the list of primary tables, i.e. tables that are mapped to the FeatureType without a join
     * @return
     */
    Collection<String> getPrimaryTableNames();

    /**
     * Get the list of joined tables, i.e. tables that are mapped to the FeatureType with a join and provide at least one value mapping
     * @return
     */
    Collection<String> getJoinedTableNames();

    /**
     * Get the list of reference tables, i.e. tables that are mapped to the FeatureType with a join, but do not provide values, just references to other Features
     * @return
     */
    Collection<String> getReferenceTableNames();

    /**
     * Get all table mappings for primary, joined and reference tables
     * @return
     */
    List<MappingTable> getTables();

    /**
     * Get all join mappings
     * @return
     */
    List<MappingJoin> getJoins();

    /**
     * Get all value mappings
     * @return
     */
    List<MappingValue> getValues();

    /**
     * Get all AssociationTargets
     * @return
     */
    List<AssociationTarget> getAssociationTargets();

    /**
     * Does a table mapping exist for the given table?
     * @param name
     * @return
     */
    boolean hasTable(String name);

    /**
     * Get the table mapping for the given table
     * @param name
     * @return
     */
    MappingTable getTable(String name);

    /**
     * Does a value mapping exist for the given table?
     * @param name
     * @return
     */
    boolean hasValueMappingForTable(String name);

    /**
     * Does a value mapping exist for the given table and target path?
     * @param name
     * @param target
     * @return
     */
    boolean hasValueMappingForTable(String name, String target);

    /**
     * Does a reference mapping exist for the given table and target path?
     * @param name
     * @param target
     * @return
     */
    boolean hasReferenceMappingForTable(String name, String target);

    /**
     * Add a table mapping
     *
     * @param mappingTable
     */
    void addTable(MappingTable mappingTable);

    /**
     * Add a join mapping
     *
     * @param mappingJoin
     */
    void addJoin(MappingJoin mappingJoin);

    /**
     * Add a value mapping
     *
     * @param mappingValue
     */
    void addValue(MappingValue mappingValue);

    /**
     * Add an AssociationTarget
     *
     * @param associationTarget
     */
    void addAssociationTarget(AssociationTarget associationTarget);
}

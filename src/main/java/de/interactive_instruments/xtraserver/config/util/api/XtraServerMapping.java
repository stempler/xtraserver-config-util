package de.interactive_instruments.xtraserver.config.util.api;

import java.util.Collection;

/**
 * @author zahnen
 */
public interface XtraServerMapping {
    boolean hasFeatureType(String featureType);

    FeatureTypeMapping getFeatureTypeMapping(String featureType, boolean flattenInheritance);

    Collection<String> getFeatureTypeList();

    Collection<String> getFeatureTypeList(boolean includeAbstract);
}

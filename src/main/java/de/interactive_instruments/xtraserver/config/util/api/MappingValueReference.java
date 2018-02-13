package de.interactive_instruments.xtraserver.config.util.api;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * Represents a reference mapping for a mapping target path
 *
 * @author zahnen
 */
public class MappingValueReference extends MappingValueExpression {

    private final String referencedFeatureType;

    MappingValueReference(String targetPath, List<QName> qualifiedTargetPath, String value, String description, TYPE type, String referencedFeatureType) {
        super(targetPath, qualifiedTargetPath, value, description, type);
        this.referencedFeatureType = referencedFeatureType;
    }

    /**
     * Returns the prefixed name of the referenced feature type
     *
     * @return the referenced feature type
     */
    public String getReferencedFeatureType() {
        return referencedFeatureType;
    }

    /**
     * Returns the mapping target path of the reference
     *
     * @return the mapping target path
     */
    public String getReferencedTarget() {
        return getTargetPath().substring(0, getTargetPath().lastIndexOf("/"));
    }
}

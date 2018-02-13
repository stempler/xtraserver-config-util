package de.interactive_instruments.xtraserver.config.util.api;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * Represents a classification or nil mapping for a mapping target path
 *
 * @author zahnen
 */
public class MappingValueClassification extends MappingValue {

    private final List<String> keys;
    private final List<String> values;

    MappingValueClassification(String targetPath, List<QName> qualifiedTargetPath, String value, String description, MappingValue.TYPE type, List<String> keys, List<String> values) {
        super(targetPath, qualifiedTargetPath, value, description, type);
        this.keys = keys;
        this.values = values;
    }

    /**
     * Returns the classification keys
     *
     * @return the classification keys
     */
    public List<String> getKeys() {
        return keys;
    }

    /**
     * Returns the classification values
     *
     * @return the classification values
     */
    public List<String> getValues() {
        return values;
    }
}

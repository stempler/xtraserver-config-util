package de.interactive_instruments.xtraserver.config.util.api;

/**
 * @author zahnen
 */
public interface MappingValue {
    String getTable();

    String getTarget();

    String getValue();

    String getValueType();

    String getMappingMode();

    String getDbCodes();

    String getDbValues();
}

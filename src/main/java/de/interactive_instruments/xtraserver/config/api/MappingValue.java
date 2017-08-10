package de.interactive_instruments.xtraserver.config.api;

import de.interactive_instruments.xtraserver.schema.MappingsSequenceType;

/**
 * @author zahnen
 */
public class MappingValue {
    private String table;
    private String target;
    private String value;
    private String valueType;
    private String mappingMode;
    private String dbCodes;
    private String dbValues;

    public MappingValue(MappingsSequenceType.Table table) {
        this.table = table.getTable_Name();
        this.target = table.getTarget();
        this.value = table.getValue4();
        this.valueType = table.getValue_Type();
        this.mappingMode = table.getMapping_Mode();
        this.dbCodes = table.getDb_Codes();
        this.dbValues = table.getSchema_Codes();
    }

    public String getTable() {
        return table;
    }

    public String getTarget() {
        return target;
    }

    public String getValue() {
        return value;
    }

    public String getValueType() {
        return valueType;
    }

    public String getMappingMode() {
        return mappingMode;
    }

    public String getDbCodes() {
        return dbCodes;
    }

    public String getDbValues() {
        return dbValues;
    }
}

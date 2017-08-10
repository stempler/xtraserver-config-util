package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.schema.MappingsSequenceType;

/**
 * @author zahnen
 */
public class MappingValueImpl implements de.interactive_instruments.xtraserver.config.util.api.MappingValue {
    private String table;
    private String target;
    private String value;
    private String valueType;
    private String mappingMode;
    private String dbCodes;
    private String dbValues;

    public MappingValueImpl(MappingsSequenceType.Table table) {
        this.table = table.getTable_Name();
        this.target = table.getTarget();
        this.value = table.getValue4();
        this.valueType = table.getValue_Type();
        this.mappingMode = table.getMapping_Mode();
        this.dbCodes = table.getDb_Codes();
        this.dbValues = table.getSchema_Codes();
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getValueType() {
        return valueType;
    }

    @Override
    public String getMappingMode() {
        return mappingMode;
    }

    @Override
    public String getDbCodes() {
        return dbCodes;
    }

    @Override
    public String getDbValues() {
        return dbValues;
    }
}

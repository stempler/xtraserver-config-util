package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.schema.MappingsSequenceType;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

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
    private Namespaces namespaces;

    public MappingValueImpl(MappingsSequenceType.Table table, Namespaces namespaces) {
        this.table = table.getTable_Name();
        this.target = table.getTarget();
        this.value = table.getValue4();
        this.valueType = table.getValue_Type();
        this.mappingMode = table.getMapping_Mode();
        this.dbCodes = table.getDb_Codes();
        this.dbValues = table.getSchema_Codes();
        this.namespaces = namespaces;

        if (valueType == null) {
            if (value.contains("$T$") || value.contains("||")) {
                this.valueType = "expression";
            } else {
                this.valueType = "value";
            }
        }
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
    public QName getTargetQName() {
        return namespaces.getQualifiedName(target);
    }

    @Override
    public List<QName> getTargetQNameList() {
        List<QName> qNameList = new ArrayList<>();
        String[] targets = target.split("/");

        for (String prefixedName: targets) {
            QName qualifiedName = namespaces.getQualifiedName(prefixedName);
            if (qualifiedName != null) {
                qNameList.add(qualifiedName);
            }
        }

        return qNameList;
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

    @Override
    public boolean equals(Object o) {
        MappingValue mappingValue = (MappingValue)o;
        return target.equals(mappingValue.getTarget()) &&
                table.equals(mappingValue.getTable()) &&
                value.equals(mappingValue.getValue()) &&
                valueType.equals(mappingValue.getValueType());
    }
}

package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.schema.MappingsSequenceType;
import de.interactive_instruments.xtraserver.config.util.api.MappingTable;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private boolean isGeometry;
    private String rootProperty;

    public MappingValueImpl(Namespaces namespaces) {
        this.valueType = "value";
        //this.mappingMode = "value";
        this.namespaces = namespaces;
    }

    MappingValueImpl(MappingValueImpl mappingValue) {
        this.table = mappingValue.table;
        this.target = mappingValue.target;
        this.value = mappingValue.value;
        this.valueType = mappingValue.valueType;
        this.mappingMode = mappingValue.mappingMode;
        this.dbCodes = mappingValue.dbCodes;
        this.dbValues = mappingValue.dbValues;
        this.namespaces = mappingValue.namespaces;
        this.isGeometry = mappingValue.isGeometry;
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
    public void setTable(MappingTable table) {
        this.table = table.getName();
        ((MappingTableImpl)table).addValue(this);
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    @Override
    public void setMappingMode(String mappingMode) {
        this.mappingMode = mappingMode;
    }

    @Override
    public void setDbCodes(String dbCodes) {
        this.dbCodes = dbCodes;
    }

    @Override
    public void setDbValues(String dbValues) {
        this.dbValues = dbValues;
    }

    public boolean isGeometry() {
        return isGeometry;
    }

    public void setGeometry(boolean geometry) {
        isGeometry = geometry;
    }

    public String getRootProperty() {
        return rootProperty;
    }

    public void setRootProperty(String rootProperty) {
        this.rootProperty = rootProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappingValueImpl that = (MappingValueImpl) o;
        return Objects.equals(table, that.table) &&
                Objects.equals(target, that.target) &&
                Objects.equals(value, that.value) &&
                Objects.equals(valueType, that.valueType) &&
                Objects.equals(mappingMode, that.mappingMode) &&
                Objects.equals(dbCodes, that.dbCodes) &&
                Objects.equals(dbValues, that.dbValues);
    }

    @Override
    public int hashCode() {

        return Objects.hash(table, target, value, valueType, mappingMode, dbCodes, dbValues);
    }

    @Override
    public String toString() {
        return "\nMappingValueImpl{" +
                "\ntable='" + table + '\'' +
                "\n, target='" + target + '\'' +
                "\n, value='" + value + '\'' +
                "\n, valueType='" + valueType + '\'' +
                "\n, mappingMode='" + mappingMode + '\'' +
                "\n, dbCodes='" + dbCodes + '\'' +
                "\n, dbValues='" + dbValues + '\'' +
                "\n}";
    }
}

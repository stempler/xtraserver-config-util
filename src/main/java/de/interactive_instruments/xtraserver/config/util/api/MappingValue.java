package de.interactive_instruments.xtraserver.config.util.api;

import de.interactive_instruments.xtraserver.config.util.MappingValueImpl;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * A value mapping
 *
 * @author zahnen
 */
public interface MappingValue {

    /**
     * factory method
     *
     * @return
     */
    static MappingValue create() {
        return new MappingValueImpl();
    }

    String getTable();

    String getTarget();

    QName getTargetQName();

    List<QName> getTargetQNameList();

    String getValue();

    String getValueType();

    String getMappingMode();

    String getDbCodes();

    String getDbValues();

    void setTable(MappingTable table);

    void setTarget(String target);

    void setValue(String value);

    void setValueType(String valueType);

    void setMappingMode(String mappingMode);

    void setDbCodes(String dbCodes);

    void setDbValues(String dbValues);
}

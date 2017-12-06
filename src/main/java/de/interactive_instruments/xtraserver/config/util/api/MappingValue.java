package de.interactive_instruments.xtraserver.config.util.api;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * A value mapping
 *
 * @author zahnen
 */
public interface MappingValue {
    String getTable();

    String getTarget();

    QName getTargetQName();

    List<QName> getTargetQNameList();

    String getValue();

    String getValueType();

    String getMappingMode();

    String getDbCodes();

    String getDbValues();
}

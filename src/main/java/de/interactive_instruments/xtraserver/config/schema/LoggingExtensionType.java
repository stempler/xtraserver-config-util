
package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für loggingExtensionType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="loggingExtensionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="short"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "loggingExtensionType")
@XmlEnum
public enum LoggingExtensionType {

    @XmlEnumValue("short")
    SHORT("short");
    private final String value;

    LoggingExtensionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LoggingExtensionType fromValue(String v) {
        for (LoggingExtensionType c: LoggingExtensionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

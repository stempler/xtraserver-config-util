//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.07.26 um 01:38:15 PM CEST 
//


package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für SQLFeatureTypeImplType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="SQLFeatureTypeImplType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.interactive-instruments.de/namespaces/XtraServer}MappingsSequenceType">
 *       &lt;attribute name="logging" default="false">
 *         &lt;simpleType>
 *           &lt;union memberTypes=" {http://www.w3.org/2001/XMLSchema}boolean {http://www.interactive-instruments.de/namespaces/XtraServer}loggingExtensionType">
 *           &lt;/union>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="useTempTable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="tempTableName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="FTCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SQLFeatureTypeImplType")
public class SQLFeatureTypeImplType
    extends MappingsSequenceType
{

    @XmlAttribute(name = "logging")
    protected String logging;
    @XmlAttribute(name = "useTempTable")
    protected Boolean useTempTable;
    @XmlAttribute(name = "tempTableName")
    protected String tempTableName;
    @XmlAttribute(name = "FTCode")
    protected String ftCode;

    /**
     * Ruft den Wert der logging-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogging() {
        if (logging == null) {
            return "false";
        } else {
            return logging;
        }
    }

    /**
     * Legt den Wert der logging-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogging(String value) {
        this.logging = value;
    }

    /**
     * Ruft den Wert der useTempTable-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isUseTempTable() {
        if (useTempTable == null) {
            return false;
        } else {
            return useTempTable;
        }
    }

    /**
     * Legt den Wert der useTempTable-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseTempTable(Boolean value) {
        this.useTempTable = value;
    }

    /**
     * Ruft den Wert der tempTableName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTempTableName() {
        return tempTableName;
    }

    /**
     * Legt den Wert der tempTableName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTempTableName(String value) {
        this.tempTableName = value;
    }

    /**
     * Ruft den Wert der ftCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFTCode() {
        return ftCode;
    }

    /**
     * Legt den Wert der ftCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFTCode(String value) {
        this.ftCode = value;
    }

}

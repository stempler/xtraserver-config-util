/**
 * Copyright 2018 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * &lt;complexType name="SQLFeatureTypeImplType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.interactive-instruments.de/namespaces/XtraServer}MappingsSequenceType"&gt;
 *       &lt;attribute name="logging" default="false"&gt;
 *         &lt;simpleType&gt;
 *           &lt;union memberTypes=" {http://www.w3.org/2001/XMLSchema}boolean {http://www.interactive-instruments.de/namespaces/XtraServer}loggingExtensionType"&gt;
 *           &lt;/union&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="useTempTable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="tempTableName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="FTCode" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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

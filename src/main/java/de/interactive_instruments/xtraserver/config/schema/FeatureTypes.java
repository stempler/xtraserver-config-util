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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}FeatureType"/&gt;
 *         &lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}AdditionalMappings"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="defaultDbSchema" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="appSchemaGenerator" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "featureTypeOrAdditionalMappings"
})
@XmlRootElement(name = "FeatureTypes")
public class FeatureTypes {

    @XmlElements({
        @XmlElement(name = "FeatureType", type = FeatureType.class),
        @XmlElement(name = "AdditionalMappings", type = AdditionalMappings.class)
    })
    protected List<Object> featureTypeOrAdditionalMappings;
    @XmlAttribute(name = "defaultDbSchema")
    protected String defaultDbSchema;
    @XmlAttribute(name = "appSchemaGenerator")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object appSchemaGenerator;

    /**
     * Gets the value of the featureTypeOrAdditionalMappings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the featureTypeOrAdditionalMappings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFeatureTypeOrAdditionalMappings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FeatureType }
     * {@link AdditionalMappings }
     * 
     * 
     */
    public List<Object> getFeatureTypeOrAdditionalMappings() {
        if (featureTypeOrAdditionalMappings == null) {
            featureTypeOrAdditionalMappings = new ArrayList<Object>();
        }
        return this.featureTypeOrAdditionalMappings;
    }

    /**
     * Ruft den Wert der defaultDbSchema-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultDbSchema() {
        return defaultDbSchema;
    }

    /**
     * Legt den Wert der defaultDbSchema-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultDbSchema(String value) {
        this.defaultDbSchema = value;
    }

    /**
     * Ruft den Wert der appSchemaGenerator-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getAppSchemaGenerator() {
        return appSchemaGenerator;
    }

    /**
     * Legt den Wert der appSchemaGenerator-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setAppSchemaGenerator(Object value) {
        this.appSchemaGenerator = value;
    }

}

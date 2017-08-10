//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.07.26 um 01:38:15 PM CEST 
//


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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}FeatureType"/>
 *         &lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}AdditionalMappings"/>
 *       &lt;/choice>
 *       &lt;attribute name="defaultDbSchema" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="appSchemaGenerator" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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

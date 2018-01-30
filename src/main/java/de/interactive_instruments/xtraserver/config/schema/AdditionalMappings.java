
package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *       &lt;sequence&gt;
 *         &lt;element name="RootElementName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element ref="{http://www.interactive-instruments.de/namespaces/XtraServer}Mappings"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "rootElementName",
    "mappings"
})
@XmlRootElement(name = "AdditionalMappings")
public class AdditionalMappings {

    @XmlElement(name = "RootElementName", required = true)
    protected String rootElementName;
    @XmlElement(name = "Mappings", required = true)
    protected MappingsSequenceType mappings;

    /**
     * Ruft den Wert der rootElementName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootElementName() {
        return rootElementName;
    }

    /**
     * Legt den Wert der rootElementName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootElementName(String value) {
        this.rootElementName = value;
    }

    /**
     * Ruft den Wert der mappings-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MappingsSequenceType }
     *     
     */
    public MappingsSequenceType getMappings() {
        return mappings;
    }

    /**
     * Legt den Wert der mappings-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MappingsSequenceType }
     *     
     */
    public void setMappings(MappingsSequenceType value) {
        this.mappings = value;
    }

}

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
import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse für MappingsSequenceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="MappingsSequenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element name="Table"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                   &lt;attribute name="apply_mapping_to_path" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="applyMappingToPath" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="assign" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="assign1" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="db_codes" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="derivation_pattern" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="disambiguate" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="filter_mapping" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *                   &lt;attribute name="for_each_select_id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="ft_col" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="generator" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="is_reference" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="isMappedGeometry" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="isReference" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="map_targetpath" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="mapped_geometry" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="mapping_mode" default="value"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                         &lt;enumeration value="value"/&gt;
 *                         &lt;enumeration value="nil"/&gt;
 *                         &lt;enumeration value="nil_attr"/&gt;
 *                         &lt;enumeration value="nilAttr"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/attribute&gt;
 *                   &lt;attribute name="match" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="nil_reason" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="nil_value" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="no_output" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="noOutput" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="oid_col" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="schema_codes" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="select_id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="significant_for_emptiness" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="significantForEmptiness" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="srid" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="srs" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="suppress_xml_entities_encoding" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="suppressXMLEntitiesEncoding" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                   &lt;attribute name="table_name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&gt;
 *                   &lt;attribute name="use_geotypes" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="useGeotypes" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="value_type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="valueType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="Join"&gt;
 *             &lt;complexType&gt;
 *               &lt;simpleContent&gt;
 *                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                   &lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&gt;
 *                   &lt;attribute name="filter_mapping" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *                   &lt;attribute name="join_path" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="axis" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="idref" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="match" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="disambiguate" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;/extension&gt;
 *               &lt;/simpleContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="AssociationTarget"&gt;
 *             &lt;complexType&gt;
 *               &lt;simpleContent&gt;
 *                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                   &lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&gt;
 *                   &lt;attribute name="object_ref" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;/extension&gt;
 *               &lt;/simpleContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="Content"&gt;
 *             &lt;complexType&gt;
 *               &lt;simpleContent&gt;
 *                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                   &lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&gt;
 *                   &lt;attribute name="representation" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="implementation" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="mode" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;/extension&gt;
 *               &lt;/simpleContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="Substitution"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                   &lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                   &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&gt;
 *                   &lt;attribute name="implementation" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MappingsSequenceType", propOrder = {
    "tableOrJoinOrAssociationTarget"
})
@XmlSeeAlso({
    SQLFeatureTypeImplType.class
})
public class MappingsSequenceType {

    @XmlElements({
        @XmlElement(name = "Table", type = MappingsSequenceType.Table.class),
        @XmlElement(name = "Join", type = MappingsSequenceType.Join.class),
        @XmlElement(name = "AssociationTarget", type = MappingsSequenceType.AssociationTarget.class),
        @XmlElement(name = "Content", type = MappingsSequenceType.Content.class),
        @XmlElement(name = "Substitution", type = MappingsSequenceType.Substitution.class)
    })
    protected List<Object> tableOrJoinOrAssociationTarget;

    /**
     * Gets the value of the tableOrJoinOrAssociationTarget property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tableOrJoinOrAssociationTarget property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTableOrJoinOrAssociationTarget().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MappingsSequenceType.Table }
     * {@link MappingsSequenceType.Join }
     * {@link MappingsSequenceType.AssociationTarget }
     * {@link MappingsSequenceType.Content }
     * {@link MappingsSequenceType.Substitution }
     * 
     * 
     */
    public List<Object> getTableOrJoinOrAssociationTarget() {
        if (tableOrJoinOrAssociationTarget == null) {
            tableOrJoinOrAssociationTarget = new ArrayList<Object>();
        }
        return this.tableOrJoinOrAssociationTarget;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;simpleContent&gt;
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *       &lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&gt;
     *       &lt;attribute name="object_ref" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/extension&gt;
     *   &lt;/simpleContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class AssociationTarget {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "gmlVersion")
        protected String gmlVersion;
        @XmlAttribute(name = "target")
        protected String target;
        @XmlAttribute(name = "object_ref")
        protected String object_Ref;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Ruft den Wert der gmlVersion-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGmlVersion() {
            return gmlVersion;
        }

        /**
         * Legt den Wert der gmlVersion-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGmlVersion(String value) {
            this.gmlVersion = value;
        }

        /**
         * Ruft den Wert der target-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTarget() {
            if (target == null) {
                return "";
            } else {
                return target;
            }
        }

        /**
         * Legt den Wert der target-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTarget(String value) {
            this.target = value;
        }

        /**
         * Ruft den Wert der object_Ref-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getObject_Ref() {
            return object_Ref;
        }

        /**
         * Legt den Wert der object_Ref-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setObject_Ref(String value) {
            this.object_Ref = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;simpleContent&gt;
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *       &lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&gt;
     *       &lt;attribute name="representation" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="implementation" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="mode" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/extension&gt;
     *   &lt;/simpleContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Content {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "gmlVersion")
        protected String gmlVersion;
        @XmlAttribute(name = "target")
        protected String target;
        @XmlAttribute(name = "representation")
        protected String representation;
        @XmlAttribute(name = "implementation")
        protected String implementation;
        @XmlAttribute(name = "mode")
        protected String mode;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Ruft den Wert der gmlVersion-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGmlVersion() {
            return gmlVersion;
        }

        /**
         * Legt den Wert der gmlVersion-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGmlVersion(String value) {
            this.gmlVersion = value;
        }

        /**
         * Ruft den Wert der target-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTarget() {
            if (target == null) {
                return "";
            } else {
                return target;
            }
        }

        /**
         * Legt den Wert der target-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTarget(String value) {
            this.target = value;
        }

        /**
         * Ruft den Wert der representation-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRepresentation() {
            return representation;
        }

        /**
         * Legt den Wert der representation-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRepresentation(String value) {
            this.representation = value;
        }

        /**
         * Ruft den Wert der implementation-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getImplementation() {
            return implementation;
        }

        /**
         * Legt den Wert der implementation-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setImplementation(String value) {
            this.implementation = value;
        }

        /**
         * Ruft den Wert der mode-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMode() {
            return mode;
        }

        /**
         * Legt den Wert der mode-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMode(String value) {
            this.mode = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;simpleContent&gt;
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *       &lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&gt;
     *       &lt;attribute name="filter_mapping" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
     *       &lt;attribute name="join_path" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="axis" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="idref" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="match" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="disambiguate" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/extension&gt;
     *   &lt;/simpleContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Join {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "gmlVersion")
        protected String gmlVersion;
        @XmlAttribute(name = "target")
        protected String target;
        @XmlAttribute(name = "filter_mapping")
        protected Boolean filter_Mapping;
        @XmlAttribute(name = "join_path")
        protected String join_Path;
        @XmlAttribute(name = "axis")
        protected String axis;
        @XmlAttribute(name = "idref")
        protected String idref;
        @XmlAttribute(name = "match")
        protected String match;
        @XmlAttribute(name = "disambiguate")
        protected String disambiguate;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Ruft den Wert der gmlVersion-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGmlVersion() {
            return gmlVersion;
        }

        /**
         * Legt den Wert der gmlVersion-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGmlVersion(String value) {
            this.gmlVersion = value;
        }

        /**
         * Ruft den Wert der target-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTarget() {
            if (target == null) {
                return "";
            } else {
                return target;
            }
        }

        /**
         * Legt den Wert der target-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTarget(String value) {
            this.target = value;
        }

        /**
         * Ruft den Wert der filter_Mapping-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public boolean isFilter_Mapping() {
            if (filter_Mapping == null) {
                return false;
            } else {
                return filter_Mapping;
            }
        }

        /**
         * Legt den Wert der filter_Mapping-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setFilter_Mapping(Boolean value) {
            this.filter_Mapping = value;
        }

        /**
         * Ruft den Wert der join_Path-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getJoin_Path() {
            return join_Path;
        }

        /**
         * Legt den Wert der join_Path-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setJoin_Path(String value) {
            this.join_Path = value;
        }

        /**
         * Ruft den Wert der axis-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAxis() {
            return axis;
        }

        /**
         * Legt den Wert der axis-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAxis(String value) {
            this.axis = value;
        }

        /**
         * Ruft den Wert der idref-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIdref() {
            return idref;
        }

        /**
         * Legt den Wert der idref-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIdref(String value) {
            this.idref = value;
        }

        /**
         * Ruft den Wert der match-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMatch() {
            return match;
        }

        /**
         * Legt den Wert der match-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMatch(String value) {
            this.match = value;
        }

        /**
         * Ruft den Wert der disambiguate-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDisambiguate() {
            return disambiguate;
        }

        /**
         * Legt den Wert der disambiguate-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDisambiguate(String value) {
            this.disambiguate = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&gt;
     *       &lt;attribute name="implementation" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Substitution {

        @XmlAttribute(name = "gmlVersion")
        protected String gmlVersion;
        @XmlAttribute(name = "target")
        protected String target;
        @XmlAttribute(name = "implementation")
        protected String implementation;

        /**
         * Ruft den Wert der gmlVersion-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGmlVersion() {
            return gmlVersion;
        }

        /**
         * Legt den Wert der gmlVersion-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGmlVersion(String value) {
            this.gmlVersion = value;
        }

        /**
         * Ruft den Wert der target-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTarget() {
            if (target == null) {
                return "";
            } else {
                return target;
            }
        }

        /**
         * Legt den Wert der target-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTarget(String value) {
            this.target = value;
        }

        /**
         * Ruft den Wert der implementation-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getImplementation() {
            return implementation;
        }

        /**
         * Legt den Wert der implementation-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setImplementation(String value) {
            this.implementation = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;attribute name="apply_mapping_to_path" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="applyMappingToPath" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="assign" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="assign1" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="db_codes" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="derivation_pattern" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="disambiguate" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="filter_mapping" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
     *       &lt;attribute name="for_each_select_id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="ft_col" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="generator" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="gmlVersion" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="is_reference" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="isMappedGeometry" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="isReference" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="map_targetpath" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="mapped_geometry" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="mapping_mode" default="value"&gt;
     *         &lt;simpleType&gt;
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *             &lt;enumeration value="value"/&gt;
     *             &lt;enumeration value="nil"/&gt;
     *             &lt;enumeration value="nil_attr"/&gt;
     *             &lt;enumeration value="nilAttr"/&gt;
     *           &lt;/restriction&gt;
     *         &lt;/simpleType&gt;
     *       &lt;/attribute&gt;
     *       &lt;attribute name="match" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="nil_reason" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="nil_value" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="no_output" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="noOutput" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="oid_col" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="schema_codes" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="select_id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="significant_for_emptiness" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="significantForEmptiness" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="srid" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="srs" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="suppress_xml_entities_encoding" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="suppressXMLEntitiesEncoding" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="table_name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&gt;
     *       &lt;attribute name="use_geotypes" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="useGeotypes" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="value_type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="valueType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Table {

        @XmlAttribute(name = "apply_mapping_to_path")
        protected Boolean apply_Mapping_To_Path;
        @XmlAttribute(name = "applyMappingToPath")
        protected Boolean applyMappingToPath;
        @XmlAttribute(name = "assign")
        protected String assign;
        @XmlAttribute(name = "assign1")
        protected String assign1;
        @XmlAttribute(name = "db_codes")
        protected String db_Codes;
        @XmlAttribute(name = "derivation_pattern")
        protected String derivation_Pattern;
        @XmlAttribute(name = "disambiguate")
        protected String disambiguate;
        @XmlAttribute(name = "filter_mapping")
        protected Boolean filter_Mapping;
        @XmlAttribute(name = "for_each_select_id")
        protected String for_Each_Select_Id;
        @XmlAttribute(name = "ft_col")
        protected String ft_Col;
        @XmlAttribute(name = "generator")
        protected String generator;
        @XmlAttribute(name = "gmlVersion")
        protected String gmlVersion;
        @XmlAttribute(name = "id")
        protected String id;
        @XmlAttribute(name = "is_reference")
        protected Boolean is_Reference;
        @XmlAttribute(name = "isMappedGeometry")
        protected Boolean isMappedGeometry;
        @XmlAttribute(name = "isReference")
        protected Boolean isReference;
        @XmlAttribute(name = "map_targetpath")
        protected Boolean map_Targetpath;
        @XmlAttribute(name = "mapped_geometry")
        protected Boolean mapped_Geometry;
        @XmlAttribute(name = "mapping_mode")
        protected String mapping_Mode;
        @XmlAttribute(name = "match")
        protected String match;
        @XmlAttribute(name = "nil_reason")
        protected String nil_Reason;
        @XmlAttribute(name = "nil_value")
        protected String nil_Value;
        @XmlAttribute(name = "no_output")
        protected Boolean no_Output;
        @XmlAttribute(name = "noOutput")
        protected Boolean noOutput;
        @XmlAttribute(name = "oid_col")
        protected String oid_Col;
        @XmlAttribute(name = "schema_codes")
        protected String schema_Codes;
        @XmlAttribute(name = "select_id")
        protected String select_Id;
        @XmlAttribute(name = "significant_for_emptiness")
        protected Boolean significant_For_Emptiness;
        @XmlAttribute(name = "significantForEmptiness")
        protected Boolean significantForEmptiness;
        @XmlAttribute(name = "srid")
        protected String srid;
        @XmlAttribute(name = "srs")
        protected String srs;
        @XmlAttribute(name = "suppress_xml_entities_encoding")
        protected Boolean suppress_Xml_Entities_Encoding;
        @XmlAttribute(name = "suppressXMLEntitiesEncoding")
        protected Boolean suppressXMLEntitiesEncoding;
        @XmlAttribute(name = "table_name")
        protected String table_Name;
        @XmlAttribute(name = "target")
        protected String target;
        @XmlAttribute(name = "use_geotypes")
        protected String use_Geotypes;
        @XmlAttribute(name = "useGeotypes")
        protected String useGeotypes;
        @XmlAttribute(name = "value")
        protected String value;
        @XmlAttribute(name = "value_type")
        protected String value_Type;
        @XmlAttribute(name = "valueType")
        protected String valueType;

        /**
         * Ruft den Wert der apply_Mapping_To_Path-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isApply_Mapping_To_Path() {
            return apply_Mapping_To_Path;
        }

        /**
         * Legt den Wert der apply_Mapping_To_Path-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setApply_Mapping_To_Path(Boolean value) {
            this.apply_Mapping_To_Path = value;
        }

        /**
         * Ruft den Wert der applyMappingToPath-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isApplyMappingToPath() {
            return applyMappingToPath;
        }

        /**
         * Legt den Wert der applyMappingToPath-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setApplyMappingToPath(Boolean value) {
            this.applyMappingToPath = value;
        }

        /**
         * Ruft den Wert der assign-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAssign() {
            return assign;
        }

        /**
         * Legt den Wert der assign-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAssign(String value) {
            this.assign = value;
        }

        /**
         * Ruft den Wert der assign1-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAssign1() {
            return assign1;
        }

        /**
         * Legt den Wert der assign1-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAssign1(String value) {
            this.assign1 = value;
        }

        /**
         * Ruft den Wert der db_Codes-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDb_Codes() {
            return db_Codes;
        }

        /**
         * Legt den Wert der db_Codes-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDb_Codes(String value) {
            this.db_Codes = value;
        }

        /**
         * Ruft den Wert der derivation_Pattern-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDerivation_Pattern() {
            return derivation_Pattern;
        }

        /**
         * Legt den Wert der derivation_Pattern-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDerivation_Pattern(String value) {
            this.derivation_Pattern = value;
        }

        /**
         * Ruft den Wert der disambiguate-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDisambiguate() {
            return disambiguate;
        }

        /**
         * Legt den Wert der disambiguate-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDisambiguate(String value) {
            this.disambiguate = value;
        }

        /**
         * Ruft den Wert der filter_Mapping-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public boolean isFilter_Mapping() {
            if (filter_Mapping == null) {
                return false;
            } else {
                return filter_Mapping;
            }
        }

        /**
         * Legt den Wert der filter_Mapping-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setFilter_Mapping(Boolean value) {
            this.filter_Mapping = value;
        }

        /**
         * Ruft den Wert der for_Each_Select_Id-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFor_Each_Select_Id() {
            return for_Each_Select_Id;
        }

        /**
         * Legt den Wert der for_Each_Select_Id-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFor_Each_Select_Id(String value) {
            this.for_Each_Select_Id = value;
        }

        /**
         * Ruft den Wert der ft_Col-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFt_Col() {
            return ft_Col;
        }

        /**
         * Legt den Wert der ft_Col-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFt_Col(String value) {
            this.ft_Col = value;
        }

        /**
         * Ruft den Wert der generator-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGenerator() {
            return generator;
        }

        /**
         * Legt den Wert der generator-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGenerator(String value) {
            this.generator = value;
        }

        /**
         * Ruft den Wert der gmlVersion-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGmlVersion() {
            return gmlVersion;
        }

        /**
         * Legt den Wert der gmlVersion-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGmlVersion(String value) {
            this.gmlVersion = value;
        }

        /**
         * Ruft den Wert der id-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Legt den Wert der id-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * Ruft den Wert der is_Reference-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isIs_Reference() {
            return is_Reference;
        }

        /**
         * Legt den Wert der is_Reference-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setIs_Reference(Boolean value) {
            this.is_Reference = value;
        }

        /**
         * Ruft den Wert der isMappedGeometry-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isIsMappedGeometry() {
            return isMappedGeometry;
        }

        /**
         * Legt den Wert der isMappedGeometry-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setIsMappedGeometry(Boolean value) {
            this.isMappedGeometry = value;
        }

        /**
         * Ruft den Wert der isReference-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isIsReference() {
            return isReference;
        }

        /**
         * Legt den Wert der isReference-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setIsReference(Boolean value) {
            this.isReference = value;
        }

        /**
         * Ruft den Wert der map_Targetpath-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isMap_Targetpath() {
            return map_Targetpath;
        }

        /**
         * Legt den Wert der map_Targetpath-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setMap_Targetpath(Boolean value) {
            this.map_Targetpath = value;
        }

        /**
         * Ruft den Wert der mapped_Geometry-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isMapped_Geometry() {
            return mapped_Geometry;
        }

        /**
         * Legt den Wert der mapped_Geometry-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setMapped_Geometry(Boolean value) {
            this.mapped_Geometry = value;
        }

        /**
         * Ruft den Wert der mapping_Mode-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMapping_Mode() {
            if (mapping_Mode == null) {
                return "value";
            } else {
                return mapping_Mode;
            }
        }

        /**
         * Legt den Wert der mapping_Mode-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMapping_Mode(String value) {
            this.mapping_Mode = value;
        }

        /**
         * Ruft den Wert der match-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMatch() {
            return match;
        }

        /**
         * Legt den Wert der match-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMatch(String value) {
            this.match = value;
        }

        /**
         * Ruft den Wert der nil_Reason-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNil_Reason() {
            return nil_Reason;
        }

        /**
         * Legt den Wert der nil_Reason-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNil_Reason(String value) {
            this.nil_Reason = value;
        }

        /**
         * Ruft den Wert der nil_Value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNil_Value() {
            return nil_Value;
        }

        /**
         * Legt den Wert der nil_Value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNil_Value(String value) {
            this.nil_Value = value;
        }

        /**
         * Ruft den Wert der no_Output-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isNo_Output() {
            return no_Output;
        }

        /**
         * Legt den Wert der no_Output-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setNo_Output(Boolean value) {
            this.no_Output = value;
        }

        /**
         * Ruft den Wert der noOutput-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isNoOutput() {
            return noOutput;
        }

        /**
         * Legt den Wert der noOutput-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setNoOutput(Boolean value) {
            this.noOutput = value;
        }

        /**
         * Ruft den Wert der oid_Col-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOid_Col() {
            return oid_Col;
        }

        /**
         * Legt den Wert der oid_Col-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOid_Col(String value) {
            this.oid_Col = value;
        }

        /**
         * Ruft den Wert der schema_Codes-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSchema_Codes() {
            return schema_Codes;
        }

        /**
         * Legt den Wert der schema_Codes-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSchema_Codes(String value) {
            this.schema_Codes = value;
        }

        /**
         * Ruft den Wert der select_Id-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSelect_Id() {
            return select_Id;
        }

        /**
         * Legt den Wert der select_Id-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSelect_Id(String value) {
            this.select_Id = value;
        }

        /**
         * Ruft den Wert der significant_For_Emptiness-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSignificant_For_Emptiness() {
            return significant_For_Emptiness;
        }

        /**
         * Legt den Wert der significant_For_Emptiness-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSignificant_For_Emptiness(Boolean value) {
            this.significant_For_Emptiness = value;
        }

        /**
         * Ruft den Wert der significantForEmptiness-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSignificantForEmptiness() {
            return significantForEmptiness;
        }

        /**
         * Legt den Wert der significantForEmptiness-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSignificantForEmptiness(Boolean value) {
            this.significantForEmptiness = value;
        }

        /**
         * Ruft den Wert der srid-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSrid() {
            return srid;
        }

        /**
         * Legt den Wert der srid-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSrid(String value) {
            this.srid = value;
        }

        /**
         * Ruft den Wert der srs-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSrs() {
            return srs;
        }

        /**
         * Legt den Wert der srs-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSrs(String value) {
            this.srs = value;
        }

        /**
         * Ruft den Wert der suppress_Xml_Entities_Encoding-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSuppress_Xml_Entities_Encoding() {
            return suppress_Xml_Entities_Encoding;
        }

        /**
         * Legt den Wert der suppress_Xml_Entities_Encoding-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSuppress_Xml_Entities_Encoding(Boolean value) {
            this.suppress_Xml_Entities_Encoding = value;
        }

        /**
         * Ruft den Wert der suppressXMLEntitiesEncoding-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSuppressXMLEntitiesEncoding() {
            return suppressXMLEntitiesEncoding;
        }

        /**
         * Legt den Wert der suppressXMLEntitiesEncoding-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSuppressXMLEntitiesEncoding(Boolean value) {
            this.suppressXMLEntitiesEncoding = value;
        }

        /**
         * Ruft den Wert der table_Name-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTable_Name() {
            return table_Name;
        }

        /**
         * Legt den Wert der table_Name-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTable_Name(String value) {
            this.table_Name = value;
        }

        /**
         * Ruft den Wert der target-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTarget() {
            if (target == null) {
                return "";
            } else {
                return target;
            }
        }

        /**
         * Legt den Wert der target-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTarget(String value) {
            this.target = value;
        }

        /**
         * Ruft den Wert der use_Geotypes-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUse_Geotypes() {
            return use_Geotypes;
        }

        /**
         * Legt den Wert der use_Geotypes-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUse_Geotypes(String value) {
            this.use_Geotypes = value;
        }

        /**
         * Ruft den Wert der useGeotypes-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUseGeotypes() {
            return useGeotypes;
        }

        /**
         * Legt den Wert der useGeotypes-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUseGeotypes(String value) {
            this.useGeotypes = value;
        }

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Ruft den Wert der value_Type-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue_Type() {
            return value_Type;
        }

        /**
         * Legt den Wert der value_Type-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue_Type(String value) {
            this.value_Type = value;
        }

        /**
         * Ruft den Wert der valueType-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValueType() {
            return valueType;
        }

        /**
         * Legt den Wert der valueType-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValueType(String value) {
            this.valueType = value;
        }

    }

}

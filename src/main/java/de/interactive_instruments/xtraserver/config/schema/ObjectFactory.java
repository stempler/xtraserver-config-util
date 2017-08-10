//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.07.26 um 01:38:15 PM CEST 
//


package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.interactive_instruments.xtraserver.schema package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Title_QNAME = new QName("http://www.interactive-instruments.de/namespaces/XtraServer", "Title");
    private final static QName _Name_QNAME = new QName("http://www.interactive-instruments.de/namespaces/XtraServer", "Name");
    private final static QName _OraSFeatureTypeImpl_QNAME = new QName("http://www.interactive-instruments.de/namespaces/XtraServer", "OraSFeatureTypeImpl");
    private final static QName _PGISFeatureTypeImpl_QNAME = new QName("http://www.interactive-instruments.de/namespaces/XtraServer", "PGISFeatureTypeImpl");
    private final static QName _GDBSQLFeatureTypeImpl_QNAME = new QName("http://www.interactive-instruments.de/namespaces/XtraServer", "GDBSQLFeatureTypeImpl");
    private final static QName _Mappings_QNAME = new QName("http://www.interactive-instruments.de/namespaces/XtraServer", "Mappings");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.interactive_instruments.xtraserver.schema
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FeatureType }
     * 
     */
    public FeatureType createFeatureType() {
        return new FeatureType();
    }

    /**
     * Create an instance of {@link MappingsSequenceType }
     * 
     */
    public MappingsSequenceType createMappingsSequenceType() {
        return new MappingsSequenceType();
    }

    /**
     * Create an instance of {@link FeatureType.PathAliases }
     * 
     */
    public FeatureType.PathAliases createFeatureTypePathAliases() {
        return new FeatureType.PathAliases();
    }

    /**
     * Create an instance of {@link FeatureType.OutputFormat }
     * 
     */
    public FeatureType.OutputFormat createFeatureTypeOutputFormat() {
        return new FeatureType.OutputFormat();
    }

    /**
     * Create an instance of {@link SQLFeatureTypeImplType }
     * 
     */
    public SQLFeatureTypeImplType createSQLFeatureTypeImplType() {
        return new SQLFeatureTypeImplType();
    }

    /**
     * Create an instance of {@link FeatureTypes }
     * 
     */
    public FeatureTypes createFeatureTypes() {
        return new FeatureTypes();
    }

    /**
     * Create an instance of {@link AdditionalMappings }
     * 
     */
    public AdditionalMappings createAdditionalMappings() {
        return new AdditionalMappings();
    }

    /**
     * Create an instance of {@link MappingsSequenceType.Table }
     * 
     */
    public MappingsSequenceType.Table createMappingsSequenceTypeTable() {
        return new MappingsSequenceType.Table();
    }

    /**
     * Create an instance of {@link MappingsSequenceType.Join }
     * 
     */
    public MappingsSequenceType.Join createMappingsSequenceTypeJoin() {
        return new MappingsSequenceType.Join();
    }

    /**
     * Create an instance of {@link MappingsSequenceType.AssociationTarget }
     * 
     */
    public MappingsSequenceType.AssociationTarget createMappingsSequenceTypeAssociationTarget() {
        return new MappingsSequenceType.AssociationTarget();
    }

    /**
     * Create an instance of {@link MappingsSequenceType.Content }
     * 
     */
    public MappingsSequenceType.Content createMappingsSequenceTypeContent() {
        return new MappingsSequenceType.Content();
    }

    /**
     * Create an instance of {@link MappingsSequenceType.Substitution }
     * 
     */
    public MappingsSequenceType.Substitution createMappingsSequenceTypeSubstitution() {
        return new MappingsSequenceType.Substitution();
    }

    /**
     * Create an instance of {@link FeatureType.PathAliases.PathAlias }
     * 
     */
    public FeatureType.PathAliases.PathAlias createFeatureTypePathAliasesPathAlias() {
        return new FeatureType.PathAliases.PathAlias();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.interactive-instruments.de/namespaces/XtraServer", name = "Title")
    public JAXBElement<String> createTitle(String value) {
        return new JAXBElement<String>(_Title_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.interactive-instruments.de/namespaces/XtraServer", name = "Name")
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SQLFeatureTypeImplType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.interactive-instruments.de/namespaces/XtraServer", name = "OraSFeatureTypeImpl")
    public JAXBElement<SQLFeatureTypeImplType> createOraSFeatureTypeImpl(SQLFeatureTypeImplType value) {
        return new JAXBElement<SQLFeatureTypeImplType>(_OraSFeatureTypeImpl_QNAME, SQLFeatureTypeImplType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SQLFeatureTypeImplType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.interactive-instruments.de/namespaces/XtraServer", name = "PGISFeatureTypeImpl")
    public JAXBElement<SQLFeatureTypeImplType> createPGISFeatureTypeImpl(SQLFeatureTypeImplType value) {
        return new JAXBElement<SQLFeatureTypeImplType>(_PGISFeatureTypeImpl_QNAME, SQLFeatureTypeImplType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SQLFeatureTypeImplType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.interactive-instruments.de/namespaces/XtraServer", name = "GDBSQLFeatureTypeImpl")
    public JAXBElement<SQLFeatureTypeImplType> createGDBSQLFeatureTypeImpl(SQLFeatureTypeImplType value) {
        return new JAXBElement<SQLFeatureTypeImplType>(_GDBSQLFeatureTypeImpl_QNAME, SQLFeatureTypeImplType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MappingsSequenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.interactive-instruments.de/namespaces/XtraServer", name = "Mappings")
    public JAXBElement<MappingsSequenceType> createMappings(MappingsSequenceType value) {
        return new JAXBElement<MappingsSequenceType>(_Mappings_QNAME, MappingsSequenceType.class, null, value);
    }

}

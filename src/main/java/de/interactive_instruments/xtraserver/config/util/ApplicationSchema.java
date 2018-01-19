package de.interactive_instruments.xtraserver.config.util;

import com.google.common.collect.Lists;
import org.apache.ws.commons.schema.*;
import org.apache.ws.commons.schema.utils.NamespacePrefixList;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
public class ApplicationSchema {

    public final XmlSchema xmlSchema;
    private final Namespaces namespaces;

    public ApplicationSchema(final StreamSource streamSource) {
        final XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        this.xmlSchema = schemaCol.read(streamSource, new ValidationEventHandler());

        final Map<String, String> namespaceUriToPrefixMap = new HashMap<>();
        Arrays.stream(schemaCol.getXmlSchemas()).forEach(s -> addNamespaces(
                s.getNamespaceContext(), namespaceUriToPrefixMap));
        addNamespaces(this.xmlSchema.getNamespaceContext(), namespaceUriToPrefixMap);
        this.namespaces = new Namespaces(namespaceUriToPrefixMap);
    }

    public ApplicationSchema(final InputStream inputStream) {
        this(new StreamSource(inputStream));
    }

    public ApplicationSchema(final URI inputUri) throws IOException {
        this(toStreamSource(inputUri));
    }

    private static StreamSource toStreamSource(final URI uri) throws IOException {
        if("file".equalsIgnoreCase(uri.getScheme())) {
            final File file = new File(uri.isAbsolute() ? uri : Paths.get(uri).toUri());
            return new StreamSource(file);
        }
        return new StreamSource(uri.toURL().openStream());
    }

    private static void addNamespaces(final NamespacePrefixList nsc, final Map<String, String> namespaceUriToPrefixMap) {
        if(nsc!=null) {
            for (final String prefix : nsc.getDeclaredPrefixes()) {
                namespaceUriToPrefixMap.put(nsc.getNamespaceURI(prefix), prefix);
            }
        }
    }

    public boolean isAbstract(String featureType) {
        QName ft = namespaces.getQualifiedName(featureType);
        if (ft != null) {
            XmlSchemaElement elem = xmlSchema.getElementByName(ft);
            return elem != null && elem.isAbstract();
        }
        return false;
    }

    public String getParent(String featureType) {
        String parent = null;
        QName ft = namespaces.getQualifiedName(featureType);

        if (ft != null && getParent(ft) != null) {
            parent = namespaces.getPrefixedName(getParent(ft));
        }

        return parent;
    }

    private QName getParent(QName featureType) {
        QName parent = null;
        XmlSchemaElement elem = xmlSchema.getElementByName(featureType);

        if (elem != null) {
            //parent = getParent(elem.getSchemaType());
            parent = elem.getSubstitutionGroup();
        }

        return parent;
    }

    public QName getType(String featureType) {
        QName ft = namespaces.getQualifiedName(featureType);
        if (ft != null) {
            XmlSchemaElement elem = xmlSchema.getElementByName(ft);
            if (elem!= null) {
                return elem.getSchemaType().getQName();
            }
        }
        return null;
    }

    public Namespaces getNamespaces() {
        return namespaces;
    }

    private QName getParentForType(QName featureType) {
        QName parent = null;
        XmlSchemaType type = xmlSchema.getTypeByName(featureType);

        if (type != null) {
            parent = getParent(type);
        }

        return parent;
    }

    private QName getParent(XmlSchemaType type) {
        QName parent = null;

        try {
            XmlSchemaComplexType complexType = (XmlSchemaComplexType) type;
            parent = complexType.getBaseSchemaTypeName();
        } catch (ClassCastException e) {
            // ignore
        }

        return parent;
    }

    private XmlSchemaComplexType getParentType(XmlSchemaType type) {
        XmlSchemaComplexType parent = null;

        try {
            XmlSchemaComplexType complexType = (XmlSchemaComplexType) type;
            parent = (XmlSchemaComplexType) complexType.getBaseSchemaType();
        } catch (ClassCastException e) {
            // ignore
        }

        return parent;
    }

    public List<String> getAllParents(String featureType) {
        List<String> parents = new ArrayList<>();

        QName ft = namespaces.getQualifiedName(featureType);

        /*if (ft != null && getParent(ft) != null) {
            parents.add(namespaces.getPrefixedName(getParent(ft)));

            ft = getParent(ft);

            while (ft != null && getParentForType(ft) != null) {
                parents.add(namespaces.getPrefixedName(getParentForType(ft)));
                ft = getParentForType(ft);
            }
        }*/

        while (ft != null && getParent(ft) != null) {
            parents.add(namespaces.getPrefixedName(getParent(ft)));
            ft = getParent(ft);
        }

        return parents;
    }

    public List<XmlSchemaComplexType> getAllTypes(QName featureType) {
        List<XmlSchemaComplexType> types = new ArrayList<>();

        try {
            XmlSchemaComplexType type = (XmlSchemaComplexType) xmlSchema.getTypeByName(featureType);
            QName lastBaseType = null;

            // recursively find the extension base type and add it to the list
            while (type != null) {
                types.add(type);

                XmlSchemaContentModel model = type.getContentModel();
                if (model != null) {
                    XmlSchemaContent content = model.getContent();
                    if (content != null && content instanceof XmlSchemaComplexContentExtension) {
                        XmlSchemaComplexContentExtension ext = (XmlSchemaComplexContentExtension)content;
                        QName baseType = ext.getBaseTypeName();

                        if (baseType != null) {
                            type = (XmlSchemaComplexType) xmlSchema.getTypeByName(baseType);
                            boolean next = type != null && !baseType.equals(lastBaseType);
                            lastBaseType = baseType;
                            if (next) continue;
                        }
                    }
                }

                break;
            }
        } catch (ClassCastException e) {
            // ignore
        }

        return Lists.reverse(types);
    }

    public List<XmlSchemaElement> getAllElements(QName featureType) {
        List<XmlSchemaComplexType> types = getAllTypes(featureType);

        List<XmlSchemaElement> elements = types.stream().map(type -> {

            Iterator i = xmlSchema.getElements().getValues();
            while (i.hasNext()) {
                XmlSchemaElement element = (XmlSchemaElement) i.next();
                System.out.println("FeatureTypes:" + type.getName() + " - " + element.getName());
                if (type.getQName().equals(element.getSchemaTypeName())) {
                    return element;
                }
            }

            XmlSchemaElement element = new XmlSchemaElement();
            element.setSchemaType(type);
            element.setSchemaTypeName(type.getQName());
            element.setQName(new QName(type.getQName().getNamespaceURI(), type.getQName().getLocalPart().replace("Type", "")));
            return element;
        }).collect(Collectors.toList());

        return elements;
    }

    public boolean hasProperty(XmlSchemaComplexType type, QName propertyName) {
        try {
            // check if property is contained in the element sequence of the type
            XmlSchemaContentModel model = type.getContentModel();
            if (model != null) {
                XmlSchemaContent content = model.getContent();
                if (content != null && content instanceof XmlSchemaComplexContentExtension) {
                    XmlSchemaComplexContentExtension ext = (XmlSchemaComplexContentExtension) content;
                    XmlSchemaSequence sequence = (XmlSchemaSequence) ext.getParticle();
                    Iterator i = sequence.getItems().getIterator();
                    while(i.hasNext()) {
                        XmlSchemaElement element = (XmlSchemaElement) i.next();
                       if (element.getQName() != null && element.getQName().equals(propertyName)) {
                            return true;
                        }
                    }
                }
            } else if (type.getName().endsWith("AbstractGMLType") && propertyName.getLocalPart().equals("identifier") &&propertyName.getNamespaceURI().equals(type.getQName().getNamespaceURI())) {
                return true;
            }

            // check if property is contained in the attributes of the type
            Iterator j = type.getAttributes().getIterator();
            while(j.hasNext()) {
                XmlSchemaAttribute attribute = (XmlSchemaAttribute) j.next();
                if (attribute.getQName() != null && attribute.getQName().equals(propertyName)
                        || attribute.getRefName() != null && attribute.getRefName().equals(propertyName)) {
                    return true;
                }
            }

        } catch (ClassCastException e) {
            // ignore
        }

        return false;
    }
}

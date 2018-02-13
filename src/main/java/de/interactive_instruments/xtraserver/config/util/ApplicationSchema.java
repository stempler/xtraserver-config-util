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
package de.interactive_instruments.xtraserver.config.util;

import com.google.common.collect.ImmutableList;
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

    private final XmlSchema xmlSchema;
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

    public boolean isAbstract(QName featureTypeName) {
        if (featureTypeName != null) {
            XmlSchemaElement elem = xmlSchema.getElementByName(featureTypeName);
            return elem != null && elem.isAbstract();
        }
        return false;
    }

    public Optional<String> getSuperTypeName(QName featureTypeName) {
        final XmlSchemaElement featureType = xmlSchema.getElementByName(featureTypeName);

        if (featureType != null && featureType.getSubstitutionGroup() != null) {
            return Optional.ofNullable(namespaces.getPrefixedName(featureType.getSubstitutionGroup()));
        }

        return Optional.empty();
    }


    public XmlSchemaComplexType getType(QName qualifiedTypeName) {
        if (qualifiedTypeName != null) {
            XmlSchemaElement element = xmlSchema.getElementByName(qualifiedTypeName);
            if (element!= null) {
                return (XmlSchemaComplexType)element.getSchemaType();
            }
        }
        return null;
    }

    public boolean hasElement(QName qualifiedName) {
        return getType(qualifiedName) != null;
    }

    public boolean hasElement(String elementName) {
        return hasElement(namespaces.getQualifiedName(elementName));
    }

    public Namespaces getNamespaces() {
        return namespaces;
    }

    public List<String> getAllSuperTypeNames(QName featureTypeName) {
        final List<String> superTypes = new ArrayList<>();
        Optional<String> superTypeName = getSuperTypeName(featureTypeName);

        while (superTypeName.isPresent()) {
            superTypes.add(superTypeName.get());
            superTypeName = getSuperTypeName(namespaces.getQualifiedName(superTypeName.get()));
        }

        return superTypes;
    }

    public List<QName> getAllSuperTypeQualifiedNames(QName featureTypeName) {
        return getAllSuperTypeNames(featureTypeName).stream()
                .map(namespaces::getQualifiedName)
                .collect(Collectors.toList());
    }

    // TODO
    public boolean isGeometry(XmlSchemaComplexType type, QName propertyName) {
        XmlSchemaElement element = getProperty(type, propertyName);
        if (element != null && element.getSchemaType() != null && element.getSchemaType().getQName() != null) {
            return ImmutableList.of("GeometryPropertyType",
                    "GeometricPrimitivePropertyType",
                    "PointPropertyType",
                    "MultiPointPropertyType",
                    "LineStringPropertyType",
                    "MultiLineStringPropertyType",
                    "CurvePropertyType",
                    "MultiCurvePropertyType",
                    "SurfacePropertyType",
                    "MultiSurfacePropertyType",
                    "PolygonPropertyType",
                    "MultiPolygonPropertyType")
                    .contains(element.getSchemaType().getQName().getLocalPart());
        }

        return false;
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



    private XmlSchemaElement getProperty(XmlSchemaComplexType type, QName propertyName) {
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
                            return element;
                        }
                    }
                }
            }

        } catch (ClassCastException e) {
            // ignore
        }

        return null;
    }
}

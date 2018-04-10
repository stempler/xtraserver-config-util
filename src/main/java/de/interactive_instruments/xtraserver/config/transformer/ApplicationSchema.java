/**
 * Copyright 2018 interactive instruments GmbH
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.transformer;

import com.google.common.collect.ImmutableList;
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
class ApplicationSchema {

    private final XmlSchema xmlSchema;
    private final Namespaces namespaces;

    ApplicationSchema(final URI inputUri) {
        this(toStreamSource(inputUri));
    }

    private ApplicationSchema(final StreamSource streamSource) {
        final XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        this.xmlSchema = schemaCol.read(streamSource, new ValidationEventHandler());

        final Map<String, String> namespaceUriToPrefixMap = new HashMap<>();
        Arrays.stream(schemaCol.getXmlSchemas())
              .forEach(s -> addNamespaces(s.getNamespaceContext(), namespaceUriToPrefixMap));
        addNamespaces(this.xmlSchema.getNamespaceContext(), namespaceUriToPrefixMap);
        this.namespaces = new Namespaces(namespaceUriToPrefixMap);
    }

    private static StreamSource toStreamSource(final URI uri) {
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            final File file = new File(uri.isAbsolute() ? uri : Paths.get(uri)
                                                                     .toUri());
            return new StreamSource(file);
        }
        final InputStream inputStream;
        try {
            inputStream = uri.toURL()
                             .openStream();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid URI: " + uri.toString());
        }
        return new StreamSource(inputStream);
    }

    private static void addNamespaces(final NamespacePrefixList nsc, final Map<String, String> namespaceUriToPrefixMap) {
        if (nsc != null) {
            for (final String prefix : nsc.getDeclaredPrefixes()) {
                namespaceUriToPrefixMap.put(nsc.getNamespaceURI(prefix), prefix);
            }
        }
    }

    public boolean isAbstract(final QName featureTypeName) {
        if (featureTypeName != null) {
            final XmlSchemaElement elem = xmlSchema.getElementByName(featureTypeName);
            return elem != null && elem.isAbstract();
        }
        return false;
    }

    public Optional<String> getSuperTypeName(final QName featureTypeName) {
        final XmlSchemaElement featureType = xmlSchema.getElementByName(featureTypeName);

        if (featureType != null && featureType.getSubstitutionGroup() != null) {
            return Optional.ofNullable(namespaces.getPrefixedName(featureType.getSubstitutionGroup()));
        }

        return Optional.empty();
    }


    public XmlSchemaComplexType getType(final QName qualifiedTypeName) {
        if (qualifiedTypeName != null) {
            final XmlSchemaElement element = xmlSchema.getElementByName(qualifiedTypeName);
            if (element != null) {
                return (XmlSchemaComplexType) element.getSchemaType();
            }
        }
        return null;
    }

    public XmlSchemaElement getElement(final QName qualifiedTypeName) {
        if (qualifiedTypeName != null) {
            return xmlSchema.getElementByName(qualifiedTypeName);
        }
        return null;
    }

    public boolean hasElement(final QName qualifiedName) {
        return getType(qualifiedName) != null;
    }

    public boolean hasElement(final String elementName) {
        return hasElement(namespaces.getQualifiedName(elementName));
    }

    public Namespaces getNamespaces() {
        return namespaces;
    }

    private List<String> getAllSuperTypeNames(final QName featureTypeName) {
        final List<String> superTypes = new ArrayList<>();
        Optional<String> superTypeName = getSuperTypeName(featureTypeName);

        while (superTypeName.isPresent()) {
            superTypes.add(superTypeName.get());
            superTypeName = getSuperTypeName(namespaces.getQualifiedName(superTypeName.get()));
        }

        return superTypes;
    }

    public List<QName> getAllSuperTypeQualifiedNames(final QName featureTypeName) {
        return getAllSuperTypeNames(featureTypeName).stream()
                                                    .map(namespaces::getQualifiedName)
                                                    .collect(Collectors.toList());
    }

    public boolean isGeometry(final QName qualifiedTypeName, final List<QName> propertyPath) {
        final XmlSchemaComplexType type = getType(qualifiedTypeName);
        final XmlSchemaElement element = getProperty(type, propertyPath);
        if (element != null && element.getSchemaType() != null && element.getSchemaType()
                                                                         .getQName() != null) {
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
                                .contains(element.getSchemaType()
                                                 .getQName()
                                                 .getLocalPart());
        }

        return false;
    }

    public boolean isMultiple(final QName qualifiedTypeName, final List<QName> propertyPath) {
        final XmlSchemaComplexType type = getType(qualifiedTypeName);
        final XmlSchemaElement element = getProperty(type, propertyPath);

        return element != null && element.getMaxOccurs() > 1;
    }

    public List<QName> getLastMultiplePropertyPath(final QName qualifiedTypeName, final List<QName> propertyPath) {
        for (int i = propertyPath.size(); i >= 0; i--) {
            if (isMultiple(qualifiedTypeName, propertyPath.subList(0, i))) {
                return ImmutableList.copyOf(propertyPath.subList(0, i));
            }
        }

        return ImmutableList.of();
    }

    public boolean hasProperty(final XmlSchemaComplexType type, final QName propertyName) {
        try {
            // check if property is contained in the element sequence of the type
            final XmlSchemaContentModel model = type.getContentModel();
            if (model != null) {
                final XmlSchemaContent content = model.getContent();
                if (content != null && content instanceof XmlSchemaComplexContentExtension) {
                    final XmlSchemaComplexContentExtension ext = (XmlSchemaComplexContentExtension) content;
                    final XmlSchemaSequence sequence = (XmlSchemaSequence) ext.getParticle();
                    final Iterator i = sequence.getItems()
                                               .getIterator();
                    while (i.hasNext()) {
                        final XmlSchemaElement element = (XmlSchemaElement) i.next();
                        if (element.getQName() != null && element.getQName()
                                                                 .equals(propertyName)) {
                            return true;
                        }
                    }
                }
            }

            if (type.getName()
                    .endsWith("AbstractGMLType") && propertyName.getLocalPart()
                                                                .equals("identifier") && propertyName.getNamespaceURI()
                                                                                                     .equals(type.getQName()
                                                                                                                 .getNamespaceURI())) {
                return true;
            }

            if (propertyName.getLocalPart()
                            .startsWith("@")) {
                // check if property is contained in the attributes of the type
                final QName attributeName = new QName(propertyName.getNamespaceURI(), propertyName.getLocalPart()
                                                                                                  .substring(1));
                final Iterator j = type.getAttributes()
                                       .getIterator();
                while (j.hasNext()) {
                    final XmlSchemaAttribute attribute = (XmlSchemaAttribute) j.next();
                    if (attribute.getQName() != null && attribute.getQName()
                                                                 .equals(attributeName)
                            || attribute.getRefName() != null && attribute.getRefName()
                                                                          .equals(attributeName)) {
                        return true;
                    }
                }
            }

        } catch (final ClassCastException e) {
            // ignore
        }

        return false;
    }


    private XmlSchemaElement getProperty(final XmlSchemaComplexType type, final QName propertyName) {
        if (type == null || propertyName == null) {
            return null;
        }

        try {
            // check if property is contained in the element sequence of the type
            XmlSchemaSequence sequence = null;
            final XmlSchemaContentModel model = type.getContentModel();
            if (model != null) {
                final XmlSchemaContent content = model.getContent();
                if (content != null && content instanceof XmlSchemaComplexContentExtension) {
                    final XmlSchemaComplexContentExtension ext = (XmlSchemaComplexContentExtension) content;
                    sequence = (XmlSchemaSequence) ext.getParticle();
                }
            } else {
                sequence = (XmlSchemaSequence) type.getParticle();
            }

            if (sequence != null) {
                final Iterator i = sequence.getItems().getIterator();
                while (i.hasNext()) {
                    final XmlSchemaElement element = (XmlSchemaElement) i.next();
                    if (element.getQName() != null && element.getQName().equals(propertyName)) {
                        return element;
                    }
                    if (element.getRefName() != null && element.getRefName().equals(propertyName)) {
                        return getElement(propertyName);
                    }
                }
            }

        } catch (final ClassCastException e) {
            // ignore
        }

        return null;
    }

    private XmlSchemaElement getProperty(final XmlSchemaComplexType type, final List<QName> path) {
        if (type == null || path == null) {
            return null;
        }

        try {
            XmlSchemaComplexType typ = type;
            XmlSchemaElement property = null;
            for (int i = 0; i < path.size(); i++) {
                property = getProperty(typ, path.get(i));
                typ = (XmlSchemaComplexType) property.getSchemaType();
            }

            return property;

        } catch (final ClassCastException | NullPointerException e) {
            // ignore
        }

        return null;
    }
}

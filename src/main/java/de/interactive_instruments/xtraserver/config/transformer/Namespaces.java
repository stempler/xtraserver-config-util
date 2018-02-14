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
package de.interactive_instruments.xtraserver.config.transformer;

import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
class Namespaces {
    private final BiMap<String, String> namespaces;

    /**
     * Construct a Namespaces object and add namespaces URIs to namespace prefixes mappings.
     * Predefined, existing prefixes are not overridden.
     *
     * @param namespaceUriToPrefixMapping a map with namespace URI keys and the associated prefixes
     */
    Namespaces(final Map<String, String> namespaceUriToPrefixMapping) {
        this.namespaces = HashBiMap.create();

        namespaceUriToPrefixMapping.forEach((key, value) -> {
            // containsValue() check required as bimap does not handle putIfAbsent for values
            if (key != null && !key.isEmpty() && !this.namespaces.containsValue(key)) {
                // Change NS URI -> prefix to internal representation prefix -> NS URI
                this.namespaces.putIfAbsent(value, key);
            }
        });
    }

    public QName getQualifiedName(final String prefixedName) {
        final String[] name = prefixedName.replaceAll("@", "").split(":");

        if (name.length == 2 && namespaces.get(name[0]) != null) {
            return new QName(namespaces.get(name[0]), name[1], name[0]);
        } else if (name.length == 1) {
            return new QName(name[0]);
        }

        return null;
    }

    public String getPrefixedName(final QName qualifiedName) {
        if (namespaces.inverse().get(qualifiedName.getNamespaceURI()) != null) {
            return namespaces.inverse().get(qualifiedName.getNamespaceURI()) + ":" + qualifiedName.getLocalPart();
        }
        if ("".equals(qualifiedName.getNamespaceURI())) {
            if (!"".equals(qualifiedName.getPrefix())) {
                return qualifiedName.getPrefix() + ":" + qualifiedName.getLocalPart();
            }
            return qualifiedName.getLocalPart();
        } else {
            return qualifiedName.getNamespaceURI() + ":" + qualifiedName.getLocalPart();
        }
    }

    public List<QName> getQualifiedPathElements(final String prefixedPath) {
        return Splitter
                .on('/')
                .splitToList(prefixedPath)
                .stream()
                .map(this::getQualifiedName)
                .collect(Collectors.toList());
    }
}

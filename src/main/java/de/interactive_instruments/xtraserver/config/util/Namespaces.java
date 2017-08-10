package de.interactive_instruments.xtraserver.config.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.xml.namespace.QName;

/**
 * @author zahnen
 */
public class Namespaces {
    BiMap<String, String> namespaces;

    public Namespaces() {
        this.namespaces = HashBiMap.create();

        namespaces.put("gml", "http://www.opengis.net/gml/3.2");
        namespaces.put("ci", "http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities");
    }

    public QName getQualifiedName(String prefixedName) {
        String[] name = prefixedName.split(":");

        if (name.length == 2 && namespaces.get(name[0]) != null) {
            return new QName(namespaces.get(name[0]), name[1], name[0]);
        }

        return null;
    }

    public String getPrefixedName(QName qualifiedName) {
        if (namespaces.inverse().get(qualifiedName.getNamespaceURI()) != null) {
            return namespaces.inverse().get(qualifiedName.getNamespaceURI()) + ":" + qualifiedName.getLocalPart();
        }
        return null;
    }
}

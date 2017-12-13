package de.interactive_instruments.xtraserver.config.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.xml.namespace.QName;

/**
 * @author zahnen
 */
public class Namespaces {
    private BiMap<String, String> namespaces;

    public Namespaces() {
        this.namespaces = HashBiMap.create();

        namespaces.put("gml", "http://www.opengis.net/gml/3.2");
        namespaces.put("ci", "http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities");

        namespaces.put("gmlx", "http://www.opengis.net/gml");
        namespaces.put("gco", "http://www.isotc211.org/2005/gco");
        namespaces.put("gmd", "http://www.isotc211.org/2005/gmd");
        namespaces.put("ogc", "http://www.opengis.net/ogc");
        namespaces.put("sld", "http://www.opengis.net/sld");
        namespaces.put("seii", "http://www.interactive-instruments.de/namespaces/se-1.1.0-extended");
        namespaces.put("xlink", "http://www.w3.org/1999/xlink");
        namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespaces.put("xs", "http://www.w3.org/2001/XMLSchema");
        namespaces.put("xi", "http://www.w3.org/2001/XInclude");
        namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
        namespaces.put("wfsx", "http://www.opengis.net/wfs");
        namespaces.put("wfsa", "http://www.adv-online.de/namespaces/adv/gid/wfs");
        namespaces.put("wfs", "http://www.opengis.net/wfs/2.0");
        namespaces.put("fes", "http://www.opengis.net/fes/2.0");
        namespaces.put("adv", "http://www.adv-online.de/namespaces/adv/gid/6.0");
        namespaces.put("fg", "http://www.interactive-instruments.de/ns/aaa/flurgema");
        namespaces.put("dnm", "http://www.interactive-instruments.de/namespaces/XtraServer/addons/dnm");
        namespaces.put("xpalias", "http://www.interactive-instruments.de/namespaces/XtraServer/addons/XPathAlias");
    }

    public QName getQualifiedName(String prefixedName) {
        String[] name = prefixedName.replaceAll("@", "").split(":");

        if (name.length == 2 && namespaces.get(name[0]) != null) {
            return new QName(namespaces.get(name[0]), name[1], name[0]);
        }
        else if (name.length == 1) {
            return new QName(name[0]);
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

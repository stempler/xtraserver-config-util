package de.interactive_instruments.xtraserver.config.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.xml.namespace.QName;
import java.util.Map;

/**
 * @author zahnen
 */
public class Namespaces {
    private final BiMap<String, String> namespaces;

    private Namespaces() {
        this.namespaces = HashBiMap.create();

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

    /**
     * Construct a Namespaces object and add namespaces URIs to namespace prefixes mappings.
     * Predefined, existing prefixes are not overridden.
     *
     * @param namespaceUriToPrefixMapping a map with namespace URI keys and the associated prefixes
     */
    Namespaces(final Map<String, String> namespaceUriToPrefixMapping) {
        this();
        namespaceUriToPrefixMapping.forEach((key, value) -> {
            // containsValue() check required as bimap does not handle putIfAbsent for values
            if(key!=null && !key.isEmpty() && !this.namespaces.containsValue(key)) {
                // Change NS URI -> prefix to internal representation prefix -> NS URI
                this.namespaces.putIfAbsent(value, key);
            }
        });
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
        if("".equals(qualifiedName.getNamespaceURI())) {
            if(!"".equals(qualifiedName.getPrefix())) {
                return qualifiedName.getPrefix()+ ":" +qualifiedName.getLocalPart();
            }
            return qualifiedName.getLocalPart();
        }else{
            return qualifiedName.getNamespaceURI() + ":" + qualifiedName.getLocalPart();
        }
    }
}

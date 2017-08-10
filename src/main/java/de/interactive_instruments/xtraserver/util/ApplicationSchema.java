package de.interactive_instruments.xtraserver.util;

import org.apache.ws.commons.schema.*;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zahnen
 */
public class ApplicationSchema {
    XmlSchema xmlSchema;
    Namespaces namespaces;

    public ApplicationSchema(String fileName, Namespaces namespaces) throws FileNotFoundException {

        this.namespaces = namespaces;

        InputStream is = new FileInputStream(fileName);
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        this.xmlSchema = schemaCol.read(new StreamSource(is));
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
            parent = getParent(elem.getSchemaType());
        }

        return parent;
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

    public List<String> getAllParents(String featureType) {
        List<String> parents = new ArrayList<>();

        QName ft = namespaces.getQualifiedName(featureType);

        if (ft != null && getParent(ft) != null) {
            parents.add(namespaces.getPrefixedName(getParent(ft)));

            ft = getParent(ft);

            while (ft != null && getParentForType(ft) != null) {
                parents.add(namespaces.getPrefixedName(getParentForType(ft)));
                ft = getParentForType(ft);
            }
        }

        return parents;
    }
}

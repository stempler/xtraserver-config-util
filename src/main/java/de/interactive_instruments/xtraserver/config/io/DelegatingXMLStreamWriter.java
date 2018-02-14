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
package de.interactive_instruments.xtraserver.config.io;

/**
 * @author zahnen
 */

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

abstract class DelegatingXMLStreamWriter implements XMLStreamWriter {
    private final XMLStreamWriter writer;

    DelegatingXMLStreamWriter(final XMLStreamWriter writer) {
        this.writer = writer;
    }

    public void writeStartElement(final String localName) throws XMLStreamException {
        this.writer.writeStartElement(localName);
    }

    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writer.writeStartElement(namespaceURI, localName);
    }

    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.writer.writeStartElement(prefix, localName, namespaceURI);
    }

    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writer.writeEmptyElement(namespaceURI, localName);
    }

    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.writer.writeEmptyElement(prefix, localName, namespaceURI);
    }

    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.writer.writeEmptyElement(localName);
    }

    public void writeEndElement() throws XMLStreamException {
        this.writer.writeEndElement();
    }

    public void writeEndDocument() throws XMLStreamException {
        this.writer.writeEndDocument();
    }

    public void close() throws XMLStreamException {
        this.writer.close();
    }

    public void flush() throws XMLStreamException {
        this.writer.flush();
    }

    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.writer.writeAttribute(localName, value);
    }

    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.writer.writeAttribute(prefix, namespaceURI, localName, value);
    }

    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.writer.writeAttribute(namespaceURI, localName, value);
    }

    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
        this.writer.writeNamespace(prefix, namespaceURI);
    }

    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        this.writer.writeDefaultNamespace(namespaceURI);
    }

    public void writeComment(final String data) throws XMLStreamException {
        this.writer.writeComment(data);
    }

    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        this.writer.writeProcessingInstruction(target);
    }

    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        this.writer.writeProcessingInstruction(target, data);
    }

    public void writeCData(final String data) throws XMLStreamException {
        this.writer.writeCData(data);
    }

    public void writeDTD(final String dtd) throws XMLStreamException {
        this.writer.writeDTD(dtd);
    }

    public void writeEntityRef(final String name) throws XMLStreamException {
        this.writer.writeEntityRef(name);
    }

    public void writeStartDocument() throws XMLStreamException {
        this.writer.writeStartDocument();
    }

    public void writeStartDocument(final String version) throws XMLStreamException {
        this.writer.writeStartDocument(version);
    }

    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        this.writer.writeStartDocument(encoding, version);
    }

    public void writeCharacters(final String text) throws XMLStreamException {
        this.writer.writeCharacters(text);
    }

    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.writer.writeCharacters(text, start, len);
    }

    public String getPrefix(final String uri) throws XMLStreamException {
        return this.writer.getPrefix(uri);
    }

    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        this.writer.setPrefix(prefix, uri);
    }

    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.writer.setDefaultNamespace(uri);
    }

    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        this.writer.setNamespaceContext(context);
    }

    public NamespaceContext getNamespaceContext() {
        return this.writer.getNamespaceContext();
    }

    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.writer.getProperty(name);
    }
}

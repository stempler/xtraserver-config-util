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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Stack;

class IndentingUTF8XMLStreamWriter extends DelegatingXMLStreamWriter {
    private static final Object SEEN_NOTHING = new Object();
    private static final Object SEEN_ELEMENT = new Object();
    private static final Object SEEN_DATA = new Object();
    private Object state;
    private final Stack<Object> stateStack;
    private String indentStep;
    private int depth;
    private boolean empty;

    IndentingUTF8XMLStreamWriter(final XMLStreamWriter writer) {
        super(writer);
        this.state = SEEN_NOTHING;
        this.stateStack = new Stack<>();
        this.indentStep = "  ";
        this.depth = 0;
        this.empty = false;
    }

    /**
     * @deprecated
     */
    int getIndentStep() {
        return this.indentStep.length();
    }

    /**
     * @deprecated
     */
    void setIndentStep(int indentStep) {
        final StringBuilder s;
        for (s = new StringBuilder(); indentStep > 0; --indentStep) {
            s.append(' ');
        }

        this.setIndentStep(s.toString());
    }

    private void setIndentStep(final String s) {
        this.indentStep = s;
    }

    private void onStartElement() throws XMLStreamException {
        this.stateStack.push(SEEN_ELEMENT);
        this.state = SEEN_NOTHING;
        if (this.depth >= 0) {
            super.writeCharacters("\n");
        }

        this.doIndent();
        ++this.depth;
    }

    private void onEndElement() throws XMLStreamException {
        --this.depth;
        if (this.state == SEEN_ELEMENT) {
            super.writeCharacters("\n");
            this.doIndent();
        }

        this.state = this.stateStack.pop();
    }

    private void onEmptyElement() throws XMLStreamException {
        this.state = SEEN_ELEMENT;
        this.empty = true;
        if (this.depth > 0) {
            super.writeCharacters("\n");
        }

        this.doIndent();
    }

    private void doIndent() throws XMLStreamException {
        if (this.depth > 0) {
            for (int i = 0; i < this.depth; ++i) {
                super.writeCharacters(this.indentStep);
            }
        }

    }

    public void writeStartDocument() throws XMLStreamException {
        super.writeStartDocument("UTF-8", "1.0");
        super.writeCharacters("\n");
    }

    public void writeStartDocument(final String version) throws XMLStreamException {
        super.writeStartDocument(version);
        super.writeCharacters("\n");
    }

    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        super.writeStartDocument(encoding, version);
        super.writeCharacters("\n");
    }

    public void writeStartElement(final String localName) throws XMLStreamException {
        this.onStartElement();
        super.writeStartElement(localName);
    }

    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.onStartElement();
        super.writeStartElement(namespaceURI, localName);
    }

    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        if (localName.equals("Table") || localName.equals("Join") || localName.equals("AssociationTarget")) {
            this.empty = true;
            this.onEmptyElement();
            super.writeEmptyElement(prefix, localName, namespaceURI);
        } else {
            this.onStartElement();
            super.writeStartElement(prefix, localName, namespaceURI);
        }
    }

    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.onEmptyElement();
        super.writeEmptyElement(namespaceURI, localName);
    }

    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.onEmptyElement();
        super.writeEmptyElement(prefix, localName, namespaceURI);
    }

    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.onEmptyElement();
        super.writeEmptyElement(localName);
    }

    public void writeEndElement() throws XMLStreamException {
        if (!empty) {
            this.onEndElement();
            super.writeEndElement();
        } else {
            this.empty = false;
        }
    }

    public void writeCharacters(final String text) throws XMLStreamException {
        this.state = SEEN_DATA;
        super.writeCharacters(text);
    }

    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.state = SEEN_DATA;
        super.writeCharacters(text, start, len);
    }

    public void writeCData(final String data) throws XMLStreamException {
        this.state = SEEN_DATA;
        super.writeCData(data);
    }

    @Override
    public void writeComment(final String data) throws XMLStreamException {
        if (this.depth > 0) {
            super.writeCharacters("\n");
        }

        this.doIndent();
        super.writeComment(data);
    }
}


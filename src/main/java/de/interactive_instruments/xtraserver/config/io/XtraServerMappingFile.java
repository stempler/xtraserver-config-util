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

import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Reader and Writer for XtraServer mapping files
 *
 * @author zahnen
 */
public class XtraServerMappingFile {

    /**
     * Create a new reader
     *
     * @return the {@link Reader}
     */
    public static Reader read() {
        return new Builder();
    }

    /**
     * Create a new writer
     *
     * @return the {@link Writer}
     */
    public static Writer write() {
        return new Builder();
    }


    /**
     * Reader for XtraServer mapping files
     */
    public interface Reader {
        /**
         * Reads the mapping file from the given input stream and generates a {@link XtraServerMapping}
         *
         * @param inputStream the input stream to read from
         * @return the generated immutable {@link XtraServerMapping}
         * @throws JAXBException
         * @throws IOException
         * @throws SAXException
         */
        XtraServerMapping fromStream(final InputStream inputStream) throws JAXBException, IOException, SAXException;
    }

    /**
     * Writer for XtraServer mapping files
     */
    public interface Writer {
        /**
         * Set the XtraServer mapping that should be written
         *
         * @param xtraServerMapping the mapping
         * @return the writer
         */
        Writer mapping(final XtraServerMapping xtraServerMapping);

        /**
         * Create a zip archive with additional files for FeatureTypes, GeoIndexes and StoredQueries
         *
         * @return the writer
         */
        Writer createArchiveWithAdditionalFiles();

        /**
         * Writes the {@link XtraServerMapping} to a XtraServer mapping file
         *
         * @param outputStream the output stream to write to
         * @throws SAXException
         * @throws JAXBException
         * @throws XMLStreamException
         * @throws IOException
         */
        void toStream(final OutputStream outputStream) throws SAXException, JAXBException, XMLStreamException, IOException;
    }

    private static class Builder implements Reader, Writer {
        private XtraServerMapping xtraServerMapping;
        private boolean createArchiveWithAdditionalFiles;

        @Override
        public XtraServerMapping fromStream(final InputStream inputStream) throws JAXBException, IOException, SAXException {
            return new JaxbReader().readFromStream(inputStream);
        }

        @Override
        public Writer mapping(final XtraServerMapping xtraServerMapping) {
            this.xtraServerMapping = xtraServerMapping;
            return this;
        }

        @Override
        public Writer createArchiveWithAdditionalFiles() {
            this.createArchiveWithAdditionalFiles = true;
            return this;
        }

        @Override
        public void toStream(final OutputStream outputStream) throws SAXException, JAXBException, XMLStreamException, IOException {
            new JaxbWriter(xtraServerMapping).writeToStream(outputStream, createArchiveWithAdditionalFiles);
        }
    }
}

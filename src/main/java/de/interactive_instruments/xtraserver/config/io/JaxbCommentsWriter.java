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

import de.interactive_instruments.xtraserver.config.schema.FeatureTypes;
import de.interactive_instruments.xtraserver.config.schema.FeatureTypesWithComment;
import de.interactive_instruments.xtraserver.config.schema.TableWithComment;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Date;

/**
 * @author zahnen
 */
class JaxbCommentsWriter extends Marshaller.Listener {

    private final XMLStreamWriter xsw;
    private boolean headerWritten;
    private String lastComment = "";

    JaxbCommentsWriter(final XMLStreamWriter xsw) {
        this.xsw = xsw;
    }

    @Override
    public void beforeMarshal(final Object source) {
        try {
            try {
                final TableWithComment table = ((TableWithComment) source);

                if (table.hasComment() && !table.getComment().equals(lastComment)) {
                    xsw.writeComment(table.getComment());
                    lastComment = table.getComment();
                }
            } catch (final ClassCastException e) {
                // ignore
            }
            try {
                final FeatureTypesWithComment featureTypes = ((FeatureTypesWithComment) source);

                // TODO: version, settings, warnings
                if (!headerWritten && featureTypes.hasComment()) {
                    xsw.writeComment(featureTypes.getComment());
                    this.headerWritten = true;
                }
            } catch (final ClassCastException e) {
                // ignore
            }
        } catch (final XMLStreamException e) {
            // TODO: handle exception
        }
    }

}

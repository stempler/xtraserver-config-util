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
import de.interactive_instruments.xtraserver.config.schema.TableCommentDecorator;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Date;

/**
 * @author zahnen
 */
public class JaxbCommentsWriter extends Marshaller.Listener {

    private final XMLStreamWriter xsw;
    private boolean headerWritten;

    JaxbCommentsWriter(XMLStreamWriter xsw) {
        this.xsw = xsw;
    }

    @Override
    public void beforeMarshal(Object source) {
        try {
            try {
                TableCommentDecorator table = ((TableCommentDecorator) source);

                if (table.hasComment()) {
                    xsw.writeComment(table.getComment());
                }
            } catch (ClassCastException e) {
                // ignore
            }
            try {
                FeatureTypes featureTypes = ((FeatureTypes) source);

                // TODO: version, settings, warnings
                if (!headerWritten) {
                    xsw.writeComment("\n  created by xtraserver-config-util - " + new Date().toString() + "\n");
                    this.headerWritten = true;
                }
            } catch (ClassCastException e) {
                // ignore
            }
        } catch (XMLStreamException e) {
            // TODO: handle exception
        }
    }

}

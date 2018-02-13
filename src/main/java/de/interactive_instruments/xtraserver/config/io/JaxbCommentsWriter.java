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

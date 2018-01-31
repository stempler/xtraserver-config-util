package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @author zahnen
 */
public class TableCommentDecorator extends MappingsSequenceType.Table{
    //private MappingsSequenceType.Table table;

    @XmlTransient
    String comment;

    /*public TableCommentDecorator(MappingsSequenceType.Table table) {
        this.table = table;
    }*/

    public boolean hasComment() {
        return this.comment != null && !this.comment.isEmpty();
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

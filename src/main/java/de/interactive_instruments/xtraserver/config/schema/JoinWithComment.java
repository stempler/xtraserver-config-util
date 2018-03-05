package de.interactive_instruments.xtraserver.config.schema;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @author zahnen
 */
public class JoinWithComment extends MappingsSequenceType.Join implements WithComment {
    //private MappingsSequenceType.Table table;

    @XmlTransient
    String comment;

    /*public TableCommentDecorator(MappingsSequenceType.Table table) {
        this.table = table;
    }*/

    @Override
    public boolean hasComment() {
        return this.comment != null && !this.comment.isEmpty();
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }
}
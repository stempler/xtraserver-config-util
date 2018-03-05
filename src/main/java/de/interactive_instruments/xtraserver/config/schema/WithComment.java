package de.interactive_instruments.xtraserver.config.schema;

/**
 * @author zahnen
 */
public interface WithComment {
    boolean hasComment();

    String getComment();

    void setComment(String comment);
}

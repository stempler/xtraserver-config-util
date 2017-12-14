package de.interactive_instruments.xtraserver.config.util.api;

import de.interactive_instruments.xtraserver.config.util.AssociationTargetImpl;

/**
 * @author zahnen
 */
public interface AssociationTarget {

    /**
     * factory method
     *
     * @return
     */
    static AssociationTarget create() {
        return new AssociationTargetImpl();
    }

    String getObjectRef();

    String getTarget();

    void setObjectRef(String objectRef);

    void setTarget(String target);


}

package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.util.api.AssociationTarget;

/**
 * @author zahnen
 */
public class AssociationTargetImpl implements AssociationTarget {

    private String objectRef;
    private String target;

    @Override
    public String getObjectRef() {
        return objectRef;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public void setObjectRef(String objectRef) {
        this.objectRef = objectRef;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }
}

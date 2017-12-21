package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.util.api.AssociationTarget;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssociationTargetImpl that = (AssociationTargetImpl) o;
        return Objects.equals(objectRef, that.objectRef) &&
                Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {

        return Objects.hash(objectRef, target);
    }

    @Override
    public String toString() {
        return "\nAssociationTargetImpl{" +
                "\nobjectRef='" + objectRef + '\'' +
                "\n, target='" + target + '\'' +
                "\n}";
    }
}

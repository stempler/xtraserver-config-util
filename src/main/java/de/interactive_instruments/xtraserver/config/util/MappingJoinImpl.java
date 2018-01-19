package de.interactive_instruments.xtraserver.config.util;

import com.google.common.collect.ImmutableList;
import de.interactive_instruments.xtraserver.config.schema.MappingsSequenceType;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.util.api.MappingTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zahnen
 */
public class MappingJoinImpl implements MappingJoin {
    private String target;
    private String axis;
    private final String path;
    private final List<Condition> joinConditions;
    boolean suppressJoin;

    public MappingJoinImpl() {
        this.axis = "parent";
        this.path = null;
        this.joinConditions = new ArrayList<>();
    }

    private String buildJoinPath(List<Condition> conditions) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Condition condition: conditions) {
            if (stringBuilder.length() == 0) {
                stringBuilder.insert(0, condition.getSourceTable());
            }
            stringBuilder.insert(0, ")::");
            stringBuilder.insert(0, condition.getSourceField());
            stringBuilder.insert(0, ":");
            stringBuilder.insert(0, condition.getTargetField());
            stringBuilder.insert(0, "/ref(");
            stringBuilder.insert(0, condition.getTargetTable());
        }

        return stringBuilder.toString();
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String getAxis() {
        return axis;
    }

    @Override
    public String getPath() {
        if (path != null) {
            return path;
        }

        return buildJoinPath(getJoinConditions());
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public boolean isSuppressJoin() {
        return suppressJoin;
    }

    @Override
    public String toString() {
        StringBuilder repr = new StringBuilder();
        int i = 0;
        for (Condition cndtn: joinConditions) {
            repr.append(cndtn.toString());
            if (++i < joinConditions.size()) {
                repr.append(" && ");
            }
        }
        return repr.toString();
    }

    @Override
    public List<Condition> getJoinConditions() {
        return ImmutableList.copyOf(joinConditions);
    }

    @Override
    public void addCondition(Condition condition) {
        if (condition.getSourceTable() == null || condition.getSourceTable().isEmpty()) {
            throw new IllegalArgumentException("Join condition is incomplete: source table is missing");
        }
        if (condition.getSourceField() == null || condition.getSourceTable().isEmpty()) {
            throw new IllegalArgumentException("Join condition is incomplete: source field is missing");
        }
        if (condition.getTargetTable() == null || condition.getSourceTable().isEmpty()) {
            throw new IllegalArgumentException("Join condition is incomplete: target table is missing");
        }
        if (condition.getTargetField() == null || condition.getSourceTable().isEmpty()) {
            throw new IllegalArgumentException("Join condition is incomplete: target field is missing");
        }
        if (joinConditions.size() > 0 && !condition.getSourceTable().equals(joinConditions.get(joinConditions.size()-1).getTargetTable())) {
            throw new IllegalArgumentException("Join condition does not match previously added conditions");
        }

        joinConditions.add(condition);
    }

    @Override
    public String getSourceTable() {
        if (joinConditions.isEmpty()) {
            return null;
        }

        return joinConditions.get(0).getSourceTable();
    }

    @Override
    public String getTargetTable() {
        if (joinConditions.isEmpty()) {
            return null;
        }

        return joinConditions.get(joinConditions.size() - 1).getTargetTable();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappingJoinImpl that = (MappingJoinImpl) o;
        return Objects.equals(target, that.target) &&
                Objects.equals(axis, that.axis) &&
                Objects.equals(getPath(), that.getPath());
    }

    @Override
    public int hashCode() {

        return Objects.hash(target, axis, getPath());
    }

    public static class ConditionImpl implements Condition {
        private final MappingTable sourceTable;
        private final String sourceField;
        private final MappingTable targetTable;
        private final String targetField;

        public ConditionImpl(MappingTable sourceTable, String sourceField, MappingTable targetTable, String targetField) {
            this.sourceTable = sourceTable;
            this.sourceField = sourceField;
            this.targetTable = targetTable;
            this.targetField = targetField;
        }

        @Override
        public String getSourceTable() {
            return sourceTable.getName();
        }

        @Override
        public String getSourceField() {
            return sourceField;
        }

        @Override
        public String getTargetTable() {
            return targetTable.getName();
        }

        @Override
        public String getTargetField() {
            return targetField;
        }

        @Override
        public String toString() {
            return sourceTable.getName() + "[" + sourceField + "]=" + targetTable.getName() + "[" + targetField + "]";
        }

        @Override
        public boolean equals(Object o) {
            Condition condition = (Condition)o;
            return sourceTable.equals(condition.getSourceTable()) &&
                    sourceField.equals(condition.getSourceField()) &&
                    targetTable.equals(condition.getTargetTable()) &&
                    targetField.equals(condition.getTargetField());
        }
    }
}

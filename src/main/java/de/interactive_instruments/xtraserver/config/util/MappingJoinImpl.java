package de.interactive_instruments.xtraserver.config.util;

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
    private final String axis;
    private final String path;
    private final List<Condition> joinConditions;
    private boolean suppressJoin;

    public MappingJoinImpl() {
        this.axis = "parent";
        this.path = null;
        this.joinConditions = new ArrayList<>();
    }

    MappingJoinImpl(MappingsSequenceType.Join join) {
        this.target = join.getTarget();
        this.axis = join.getAxis();
        this.path = join.getJoin_Path();
        this.joinConditions = parseJoinPath(path);
    }

    private List<Condition> parseJoinPath(String path) {
        List<Condition> pathTables = new ArrayList<>();
        String pathElems[] = path.split("::|/");

        int i = pathElems.length-1;
        while (i > 0) {
            String props[] = pathElems[i-1].split(":");

            String sourceTable = pathElems[i];
            if (sourceTable.contains("[")) {
                if (sourceTable.substring(sourceTable.indexOf("[")).equals("[1=2]")) {
                    this.suppressJoin = true;
                }
                sourceTable = sourceTable.substring(0, sourceTable.indexOf("["));
            }
            String sourceField = props[1].substring(0, props[1].length()-1);
            String targetTable = pathElems[i-2];
            if (targetTable.contains("[")) {
                if (targetTable.substring(targetTable.indexOf("[")).equals("[1=2]")) {
                    this.suppressJoin = true;
                }
                targetTable = targetTable.substring(0, targetTable.indexOf("["));
            }
            String targetField = props[0].substring(4);

            pathTables.add(new ConditionImpl(sourceTable, sourceField, targetTable, targetField));

            i = i - 2;
        }

        return pathTables;
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
        return joinConditions;
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
        private String sourceTable;
        private String sourceField;
        private String targetTable;
        private String targetField;

        public ConditionImpl() {

        }

        ConditionImpl(String sourceTable, String sourceField, String targetTable, String targetField) {
            this.sourceTable = sourceTable;
            this.sourceField = sourceField;
            this.targetTable = targetTable;
            this.targetField = targetField;
        }

        @Override
        public String getSourceTable() {
            return sourceTable;
        }

        @Override
        public String getSourceField() {
            return sourceField;
        }

        @Override
        public String getTargetTable() {
            return targetTable;
        }

        @Override
        public String getTargetField() {
            return targetField;
        }

        @Override
        public void setSourceTable(MappingTable sourceTable) {
            this.sourceTable = sourceTable.getName();
        }

        @Override
        public void setSourceField(String sourceField) {
            this.sourceField = sourceField;
        }

        @Override
        public void setTargetTable(MappingTable targetTable) {
            this.targetTable = targetTable.getName();
        }

        @Override
        public void setTargetField(String targetField) {
            this.targetField = targetField;
        }

        @Override
        public String toString() {
            return sourceTable + "[" + sourceField + "]=" + targetTable + "[" + targetField + "]";
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

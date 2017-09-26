package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.schema.MappingsSequenceType;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zahnen
 */
public class MappingJoinImpl implements MappingJoin {
    private String target;
    private String axis;
    private String path;
    private List<Condition> joinConditions;
    private boolean suppressJoin;

    public MappingJoinImpl(MappingsSequenceType.Join join) {
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
        return path;
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
    public boolean equals(Object o) {
        MappingJoin mappingJoin = (MappingJoin)o;
        return target.equals(mappingJoin.getTarget()) &&
                axis.equals(mappingJoin.getAxis()) &&
                path.equals(mappingJoin.getPath());
    }

    @Override
    public List<Condition> getJoinConditions() {
        return joinConditions;
    }

    public static class ConditionImpl implements Condition {
        private String sourceTable;
        private String sourceField;
        private String targetTable;
        private String targetField;

        public ConditionImpl(String sourceTable, String sourceField, String targetTable, String targetField) {
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

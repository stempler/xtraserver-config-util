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
            String sourceField = props[1].substring(0, props[1].length()-1);
            String targetTable = pathElems[i-2];
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

    public String toString() {
        String repr = "";
        int i = 0;
        for (Condition cndtn: joinConditions) {
            repr += cndtn.getSourceTable() + ":" + cndtn.getSourceField() + "=" + cndtn.getTargetTable() + ":" + cndtn.getTargetField();
            if (++i < joinConditions.size()) {
                repr += " && ";
            }
        }
        return repr;
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
    }
}

package de.interactive_instruments.xtraserver.config.util.api;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an expression mapping for a mapping target path
 *
 * @author zahnen
 */
public class MappingValueExpression extends MappingValue {

    MappingValueExpression(String targetPath, List<QName> qualifiedTargetPath, String value, String description, TYPE type) {
        super(targetPath, qualifiedTargetPath, value, description, type);
    }

    /**
     * @see MappingValue#getValueColumn()
     */
    @Override
    public Optional<String> getValueColumn() {
        Matcher matcher = Pattern.compile("\\$T\\$\\.(?<column>[\\S]+)").matcher(getValue());

        if (matcher.find()) {
            return Optional.ofNullable(matcher.group("column"));
        }

        return Optional.empty();
    }
}

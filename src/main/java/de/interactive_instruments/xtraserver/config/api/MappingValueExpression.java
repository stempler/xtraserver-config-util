/**
 * Copyright 2018 interactive instruments GmbH
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.api;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an expression mapping for a mapping target path
 *
 * @author zahnen
 */
public class MappingValueExpression extends MappingValue {

    MappingValueExpression(final String targetPath, final List<QName> qualifiedTargetPath, final String value, final String description, final TYPE type) {
        super(targetPath, qualifiedTargetPath, value, description, type);
    }

    /**
     * @see MappingValue#getValueColumn()
     */
    @Override
    public Optional<String> getValueColumn() {
        final Matcher matcher = Pattern.compile("\\$T\\$\\.(?<column>[\\S]+)").matcher(getValue());

        if (matcher.find()) {
            return Optional.ofNullable(matcher.group("column"));
        }

        return Optional.empty();
    }
}

package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.util.api.XtraServerMapping;

/**
 * @author zahnen
 */
public interface MappingTransformer {
    XtraServerMapping transform();

    FeatureTypeMapping transform(FeatureTypeMapping featureTypeMapping);
}

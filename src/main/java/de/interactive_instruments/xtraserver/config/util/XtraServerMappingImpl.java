package de.interactive_instruments.xtraserver.config.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import de.interactive_instruments.xtraserver.config.schema.FeatureType;
import de.interactive_instruments.xtraserver.config.schema.FeatureTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zahnen
 */
public class XtraServerMapping {
    private ApplicationSchema applicationSchema;
    private List<FeatureTypeMapping> featureTypeMappings;

    public XtraServerMapping(FeatureTypes featureTypes, ApplicationSchema applicationSchema) {
        this.featureTypeMappings = new ArrayList<>();
        this.applicationSchema = applicationSchema;

        for (Object a : featureTypes.getFeatureTypeOrAdditionalMappings()) {
            try {
                FeatureType ft = (FeatureType) a;

                FeatureTypeMapping ftm = new FeatureTypeMapping(ft);

                featureTypeMappings.add(ftm);

            } catch (ClassCastException e) {
                // ignore
            }
        }
    }

    public boolean hasFeatureType(String featureType) {
        System.out.println(featureType);
        return featureTypeMappings != null && !Collections2.filter(featureTypeMappings,
                new Predicate<FeatureTypeMapping>() {
                    @Override
                    public boolean apply(FeatureTypeMapping ftm) {
                        return featureType.equals(ftm.getName());
                    }
                }
        ).isEmpty();
    }

    public FeatureTypeMapping getFeatureTypeMapping(String featureType) {
        if (featureTypeMappings == null) return null;

        return Iterables.find(featureTypeMappings,
                new Predicate<FeatureTypeMapping>() {
                    @Override
                    public boolean apply(FeatureTypeMapping ftm) {
                        return featureType.equals(ftm.getName());
                    }
                }
        );
    }

    public Collection<String> getFeatureTypeList() {
        return getFeatureTypeList(true);
    }

    public Collection<String> getFeatureTypeList(boolean includeAbstract) {
        return Collections2.transform(
                Collections2.filter(featureTypeMappings,
                        new Predicate<FeatureTypeMapping>() {
                            @Override
                            public boolean apply(FeatureTypeMapping ftm) {
                                return includeAbstract || !applicationSchema.isAbstract(ftm.getName());
                            }
                        }
                ),
                new Function<FeatureTypeMapping, String>() {
                    @Override
                    public String apply(FeatureTypeMapping ftm) {
                        return ftm.getName();
                    }
                }
        );
    }

    public void print() {
        System.out.println("FeatureTypes:" + featureTypeMappings.size());
        System.out.println("FeatureTypes:" + getFeatureTypeList(false));
        System.out.println("FeatureTypes:" + getFeatureTypeList(true));
        for (FeatureTypeMapping ftm : featureTypeMappings) {
            String name = ftm.getName();
            String name2 = name;
            if (applicationSchema.isAbstract(name))
                name2 += "[abstract]";
            //if (applicationSchema.getParent(name) != null && hasFeatureType(name))
            //    name2 += "[" + applicationSchema.getParent(name) + "]";
            if (!applicationSchema.getAllParents(name).isEmpty())
                name2 += "[" + String.join(",", applicationSchema.getAllParents(name)) + "]";
            System.out.println("Name: " + name2 + "\n");

            Collection<String> rootTables = ftm.getPrimaryTableNames();
            System.out.println("  Root Tables: " + rootTables.toString() + "\n");
            System.out.println("  Joined Tables: " + ftm.getJoinedTableNames().toString() + "\n");

            for (MappingValue mv : ftm.getValues()) {
                try {
                    System.out.println("  Table: " + mv.getTable());
                    System.out.println("  Value: " + mv.getValue());
                    System.out.println("  Target: " + mv.getTarget() + "\n");
                } catch (ClassCastException e) {

                }
            }
        }
    }
}

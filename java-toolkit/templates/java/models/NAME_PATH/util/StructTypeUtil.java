<%
/* **********************************************************
 * Copyright 2013 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%><%
   def lines = [
    "Skeleton file for model: ${model.name}.",
] %>\
${java.render('header.comment', [model:model, extra:lines])}
package ${naming.packageName};

import java.util.Map;

import org.apache.commons.lang.Validate;

import com.vmware.vapi.bindings.type.StructType;

/**
 * Utility for StructType.
 */
public final class StructTypeUtil {

    /**
     * Populates mappings for structures defined in a model (set of VMODL
     * definitions).
     *
     * For each top level structure and each structure defined in a service,
     * adds an entry to the map where the key is the fully qualified canonical
     * name of the structure, and the value is a StructType. It allows to
     * retrieve the StructType from the canonical name of an structure.
     *
     * @param mapping map to fill with mappings for each structure in the bindings;
     *                must not be null.
     */
    public static void populateCanonicalNameToStructTypeMap(Map<String, StructType> mapping) {
        Validate.notNull(mapping);
<% model.packages.each { pkg -> %>\
<%   pkg.structures.each { structure -> %>\
        add(mapping,
            "${structure.getQualifiedName('asCanonical')}",
            ${java.getStructDefFullName(structure)});
<%   } %>\
<%   pkg.services.each { s -> %>\
<%     s.structures.each { structure -> %>\
        add(mapping,
            "${structure.getQualifiedName('asCanonical')}",
            ${java.getStructDefFullName(structure)});
<%     } %>\
<%   } %>\
<% } %>\
    }

    private static void add(Map<String, StructType> mapping, String key, StructType structType) {
        if (mapping.containsKey(key)) {
            throw new IllegalArgumentException("Two structures with the same canonical name detected: " + key +
                                               ". Unable to populate the map.");
        }
        mapping.put(key, structType);
    }
}

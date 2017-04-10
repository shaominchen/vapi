<%
/* **********************************************************
 * Copyright 2012 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%><%
   def lines = [
    "Skeleton file for package: ${service.namespace.name}.",
    "Contains type descriptors.",
] %>\
${java.render('header.comment', [model:model, extra:lines])}
package ${java.naming.packageName};

<% // TODO: based on the profile target @link to client or provider iface (or both?) %>\
/**
 * Defines the type descriptors for the {@link ${java.getClientInterfaceName(service)}}
 * API service.
 * <p>
 * <b>WARNING:</b> Internal API, subject to change in future versions.
 */
public class ${java.getDefinitionsName(service)} {

${java.render('definitions.partial', [structures:service.structures,operations:service.operations,service:service], 0)}

}

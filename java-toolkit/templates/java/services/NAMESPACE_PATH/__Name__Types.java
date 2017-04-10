<%
/* **********************************************************
 * Copyright 2012 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%><%
   def lines = [
    "Skeleton file for package: ${service.namespace.name}.",
    "Contains only data type definitions and no methods.",
    "Shared by client-side stubs and server-side skeletons to ensure type",
    "compatibility.",
] %>\
${java.render('header.comment', [model:model, extra:lines])}
package ${java.naming.packageName};

<% // TODO: based on the profile target @link to client or provider iface (or both?)
   // TODO: also remove the client iface from --profile=provider
%>\
/**
 * Defines the data types of the {@link ${java.getClientInterfaceName(service)}}
 * API service.
 */
public interface ${java.getCommonInterfaceName(service)} {
<% service.constants.each { c -> %>\
<% /* constants */ %>\
${java.getComment(c.doc, 1)}\
    public static final ${java.getConstantTypeName(c.type)} ${java.getJavaConstantName(c.name)} = ${java.getConstantValue(c)};
<% } %>\

<% service.enumerations.each { e -> %>\
${java.render('enum.partial', [enumeration:e], 1)}

<% } %>\
<% service.structures.each { s -> %>\
${java.render('struct.partial', [structure:s], 1)}
<% } %>\
}

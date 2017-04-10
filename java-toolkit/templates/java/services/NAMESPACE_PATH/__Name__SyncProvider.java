<%
/* **********************************************************
 * Copyright 2012 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%><%
   def lines = [
    "Used by server-side synchronous skeletons.",
] %>\
${java.render('header.comment', [model:model, extra:lines])}
package ${java.naming.packageName};

${java.getComment(service.doc, 0)}\
public interface ${java.getSyncProviderInterfaceName(service)} extends com.vmware.vapi.bindings.Service, ${java.naming.packageName}.${java.getCommonInterfaceName(service)} {

<% service.operations.each { o -> %>\
${java.getMethodComment(o, 1, false, false, true)}\
    public ${java.getTypeName(o.result)} ${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterList(o))}com.vmware.vapi.bindings.server.InvocationContext invocationContext);

<% } %>\
}

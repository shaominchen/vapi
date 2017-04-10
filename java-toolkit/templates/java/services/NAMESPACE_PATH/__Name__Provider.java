<%
/* **********************************************************
 * Copyright 2012 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%><%
   def lines = [
    "Skeleton file for package: ${service.namespace.name}.",
    "Used by server-side asynchronous skeletons.",
] %>\
${java.render('header.comment', [model:model, extra:lines])}
package ${java.naming.packageName};

${java.getComment(service.doc, 0)}\
public interface ${java.getProviderInterfaceName(service)} extends com.vmware.vapi.bindings.Service, ${java.naming.packageName}.${java.getCommonInterfaceName(service)} {

<% service.operations.each { o -> %>\
${java.getMethodComment(o, 1, false, true)}\
    public void ${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterList(o))}com.vmware.vapi.bindings.server.AsyncContext<${java.getTypeNameForTypeParameterForLvalue(o.result.type)}> asyncContext);

<% } %>\
}

<%
/* **********************************************************
 * Copyright 2012 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%><%
   def lines = [
    "Interface file for package: ${service.namespace.name}.",
    "Used by client-side stubs.",
] %>\
${java.render('header.comment', [model:model, extra:lines])}
package ${java.naming.packageName};

${java.getComment(service.doc, 0)}\
public interface ${java.getClientInterfaceName(service)} extends com.vmware.vapi.bindings.Service, ${java.naming.packageName}.${java.getCommonInterfaceName(service)} {

<% service.operations.each { o -> %>\
<% /* synchronous method */ %>\
${java.getMethodComment(o, 1)}\
    public ${java.getTypeName(o.result)} ${java.getJavaMethodName(o)}(${java.getParameterList(o)});

<% /* synchronous method with invocation config */ %>\
${java.getMethodComment(o, 1, true)}\
    public ${java.getTypeName(o.result)} ${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterList(o))}com.vmware.vapi.bindings.client.InvocationConfig invocationConfig);

<% /* asynchronous method */ %>\
${java.getMethodComment(o, 1, false, true)}\
    public void ${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterList(o))}com.vmware.vapi.bindings.client.AsyncCallback<${java.getTypeNameForTypeParameterForLvalue(o.result.type)}> asyncCallback);

<% /* asynchronous method with invocation config */ %>\
${java.getMethodComment(o, 1, true, true)}\
    public void ${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterList(o))}com.vmware.vapi.bindings.client.AsyncCallback<${java.getTypeNameForTypeParameterForLvalue(o.result.type)}> asyncCallback, com.vmware.vapi.bindings.client.InvocationConfig invocationConfig);

<% } %>\
}

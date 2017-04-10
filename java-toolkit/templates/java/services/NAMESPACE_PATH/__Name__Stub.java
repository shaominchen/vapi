<%
/* **********************************************************
 * Copyright 2012-2016 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%><%
   def lines = [
    "Skeleton file for package: ${service.namespace.name}."
] %>\
${java.render('header.comment', [model:model, extra:lines])}
package ${java.naming.packageName};

/**
 * Remote stub implementation of the {@link ${java.getClientInterfaceName(service)}}
 * API service.
 * <p>
 * <b>WARNING:</b> Internal API, subject to change in future versions.
 */
public class ${java.getJavaStubName(service)} extends com.vmware.vapi.internal.bindings.Stub
                                              implements ${java.naming.packageName}.${java.getClientInterfaceName(service)} {
    public ${java.getJavaStubName(service)}(com.vmware.vapi.core.ApiProvider apiProvider,
                                            com.vmware.vapi.bindings.StubConfigurationBase config) {
        super(apiProvider,
              new com.vmware.vapi.core.InterfaceIdentifier("${service.getQualifiedName('asCanonical')}"),
              config,
              java.util.Arrays.<com.vmware.vapi.internal.bindings.OperationDef>asList(${java.getOperationDefListForService(service)}));
    }

<% service.operations.each { o -> %>\
<% /* synchronous method */ %>\
    @java.lang.Override
    public ${java.getTypeName(o.result)} ${java.getJavaMethodName(o)}(${java.getParameterList(o)}) {
        <% if (!o.result.type.isVoid()) { %>return <% } %>${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterListForInvocation(o))}(com.vmware.vapi.bindings.client.InvocationConfig) null);
    }

<% /* synchronous method with InvocationConfig parameter */ %>\
    @java.lang.Override
    public ${java.getTypeName(o.result)} ${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterList(o))}com.vmware.vapi.bindings.client.InvocationConfig invocationConfig) {
        com.vmware.vapi.internal.bindings.StructValueBuilder strBuilder = new com.vmware.vapi.internal.bindings.StructValueBuilder(${java.getInputDefFullName(o)}, converter);
<% o.parameters.each { p -> %>\
        strBuilder.addStructField("${p.name.asCanonical}", ${p.name.asCamelCase});
<% } %>\
<% if (!o.result.type.isVoid()) { %>\
        ${java.getOptionalTypeName(o.result.type)} result = null;
<% } %>\
        <% if (!o.result.type.isVoid()) { %>result = <% } %>invokeMethod(new com.vmware.vapi.core.MethodIdentifier(ifaceId, "${o.name.asCanonical}"),
                                                                         strBuilder,
                                                                         ${java.getInputDefFullName(o)},
                                                                         ${java.getOutputDefFullName(o)},
                                                                         ${java.getErrorTypesListAsJava(o)},
                                                                         invocationConfig);
<% if (!o.result.type.isVoid()) { %>\
        return result;
<% } %>\
    }

<% /* asynchronous method */ %>\
    @java.lang.Override
    public void ${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterList(o))}com.vmware.vapi.bindings.client.AsyncCallback<${java.getTypeNameForTypeParameterForLvalue(o.result.type)}> asyncCallback) {
        ${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterListForInvocation(o))}asyncCallback, (com.vmware.vapi.bindings.client.InvocationConfig) null);
    }

<% /* asynchronous method with invocation config */ %>\
    @java.lang.Override
    public void ${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterList(o))}com.vmware.vapi.bindings.client.AsyncCallback<${java.getTypeNameForTypeParameterForLvalue(o.result.type)}> asyncCallback, com.vmware.vapi.bindings.client.InvocationConfig invocationConfig) {
        com.vmware.vapi.internal.bindings.StructValueBuilder strBuilder = new com.vmware.vapi.internal.bindings.StructValueBuilder(${java.getInputDefFullName(o)}, converter);
<% o.parameters.each { p -> %>\
        strBuilder.addStructField("${p.name.asCanonical}", ${p.name.asCamelCase});
<% } %>\
        invokeMethodAsync(new com.vmware.vapi.core.MethodIdentifier(ifaceId, "${o.name.asCanonical}"),
                          strBuilder,
                          ${java.getInputDefFullName(o)},
                          ${java.getOutputDefFullName(o)},
                          ${java.getErrorTypesListAsJava(o)},
                          invocationConfig,
                          asyncCallback);
    }

<% } %>\
}

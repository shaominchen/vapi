<%
/* **********************************************************
 * Copyright 2012-2016 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%><%
   def lines = [
    "Skeleton file for package: ${service.namespace.name}.",
] %>\
${java.render('header.comment', [model:model, extra:lines])}
package ${java.naming.packageName};

/**
 * Implementation of {@link com.vmware.vapi.internal.bindings.ApiInterfaceSkeleton} which
 * adapts API service implementation (class that implements
 * the generated {@link ${java.getSyncProviderInterfaceFullName(service)}} interface).
 *
 * <p>This adapter expects synchronous API service implementation.</p>
 */
public class ${java.getSyncApiIfaceName(service)} extends com.vmware.vapi.internal.bindings.ApiInterfaceSkeleton {
<% def emitFieldImpl = service.operations.size() > 0; %>\
<% if (emitFieldImpl) { %>\
    private ${java.getSyncProviderInterfaceFullName(service)} impl;
<% } %>\
<% service.operations.each { o -> %>\
<%   def emitVarExtr = o.parameters.size() > 0; %>\

    private class ${java.getApiMethodName(o)} extends com.vmware.vapi.internal.bindings.ApiMethodSkeleton {
        public ${java.getApiMethodName(o)}() {
            super(${java.getSyncApiIfaceName(service)}.this.getIdentifier(),
                  "${o.name.asCanonical}", ${java.getInputDefFullName(o)},
                  ${java.getOutputDefFullName(o)},
                  getTypeConverter(),
                  ${java.getErrorTypesListAsJava(o)});
        }

        @java.lang.Override
        public void doInvoke(com.vmware.vapi.bindings.server.InvocationContext invocationContext,
                             com.vmware.vapi.data.StructValue inStruct,
                             com.vmware.vapi.core.AsyncHandle<com.vmware.vapi.core.MethodResult> asyncHandle) {
<%   if (emitVarExtr) { %>\
            com.vmware.vapi.internal.bindings.StructValueExtractor extr =
                    new com.vmware.vapi.internal.bindings.StructValueExtractor(inStruct,
                                                                               ${java.getInputDefFullName(o)},
                                                                               getTypeConverter());
<%   } else { %>\
            new com.vmware.vapi.internal.bindings.StructValueExtractor(inStruct,
                                                                       ${java.getInputDefFullName(o)},
                                                                       getTypeConverter());
<%   } %>\
<%   o.parameters.each { p -> %>\
            ${java.getTypeName(p)} ${p.name.asCamelCase} = extr.<${java.getOptionalTypeName(p.type)}>valueForField("${p.name.asCanonical}");
<%   } %>\
            try {
<%   if (o.result.type.isVoid()) { %>\
                impl.${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterListForInvocation(o))}invocationContext);
                asyncHandle.setResult(com.vmware.vapi.core.MethodResult.newResult(com.vmware.vapi.data.VoidValue.getInstance()));
<%   } else { %>\
                asyncHandle.setResult(com.vmware.vapi.core.MethodResult.newResult(getTypeConverter().convertToVapi(impl.${java.getJavaMethodName(o)}(${java.addTrailingComma(java.getParameterListForInvocation(o))}invocationContext),
                                                                                                                   ${java.getOutputDefFullName(o)})));
<%   } %>\
            } catch (java.lang.RuntimeException ex) {
                asyncHandle.setResult(com.vmware.vapi.core.MethodResult.newErrorResult(toErrorValue(ex)));
            }
        }
    }
<% } %>\

    /**
     * Constructor.
     *
     * Creates an {@link ${java.getSyncApiIfaceName(service)}} instance with
     * default API service implementation.
     * <p>
     * The default service implementation is assumed to be
     * instance of the {@code ${java.getJavaImplFullName(service)}} class. This
     * class will be loaded and instantiated.
     */
    public ${java.getSyncApiIfaceName(service)}() {
        this((java.lang.Class<${java.getSyncProviderInterfaceFullName(service)}>) null);
    }

    /**
     * Constructor.
     *
     * Creates an {@link ${java.getSyncApiIfaceName(service)}} instance
     * for specified {@code class} of the API service implementation. The
     * specified class will be instantiated.
     *
     * @param implClass The {@code Class} implementing {@link ${java.getSyncProviderInterfaceFullName(service)}}
     *                  API service. If {@code null} default implementation will be
     *                  used as in {@link #${java.getSyncApiIfaceName(service)}()}.
     */
    public ${java.getSyncApiIfaceName(service)}(java.lang.Class<? extends ${java.getSyncProviderInterfaceFullName(service)}> implClass) {
        this(createImplInstance(implClass,
                                "${java.getJavaImplFullName(service)}",
                                ${java.getSyncProviderInterfaceFullName(service)}.class));
    }

    /**
     * Constructor.
     *
     * Creates an {@link ${java.getSyncApiIfaceName(service)}} instance
     * for specified instance of the API service implementation.
     *
     * @param impl The implementation of {@link ${java.getSyncProviderInterfaceFullName(service)}}
     *             API service. Must not be {@code null}.
     */
    public ${java.getSyncApiIfaceName(service)}(${java.getSyncProviderInterfaceFullName(service)} impl) {
        super("${service.getQualifiedName('asCanonical')}");

        org.apache.commons.lang.Validate.notNull(impl);
<% if (emitFieldImpl) { %>\
        this.impl = impl;
<% } %>
<% service.operations.each { o -> %>\
        registerMethod(new ${java.getApiMethodName(o)}());
<% } %>\
   }
}

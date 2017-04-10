<%
/* **********************************************************
 * Copyright 2012 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%><%
   def lines = [
    "Contains type descriptors.",
] %>\
${java.render('header.comment', [model:model, extra:lines])}
package ${java.naming.packageName};

/**
 * Defines the type descriptors for the top-level types in
 * {@code ${java.naming.packageName}} package.
 * <p>
 * <b>WARNING:</b> Internal API, subject to change in future versions.
 */
public class StructDefinitions {

${java.render('definitions.partial', [structures:idlPackage.structures,operations:[],service:null], 0)}

}

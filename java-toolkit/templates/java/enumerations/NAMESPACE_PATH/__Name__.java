<%
/* **********************************************************
 * Copyright 2012 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%><%
   def lines = [
    "Interface file for package: ${enumeration.namespace.name}.",
    "Represents a binding of a top level enumeration.",
] %>\
${java.render('header.comment', [model:model, extra:lines])}
package ${java.naming.packageName};

${java.render('enum.partial', [enumeration:enumeration])}
<%
/* **********************************************************
 * Copyright 2012 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%><%
   def lines = [
    "Skeleton file for package: ${structure.namespace.name}.",
    "Represents a binding of a top level structure.",
] %>\
${java.render('header.comment', [model:model, extra:lines])}
package ${java.naming.packageName};

${java.render('struct.partial', [structure:structure], 0)}

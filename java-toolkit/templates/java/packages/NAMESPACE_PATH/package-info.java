<%
/* **********************************************************
 * Copyright (c) 2013-2016 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/
%>\
${java.render('header.comment', [model:model, extra:null])}

${java.getComment(idlPackage?.doc, 0)}\
package ${java.naming.packageName};

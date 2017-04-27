var searchData = [
<% model.packages.each { pkg -> %>
{ label: "${pkg.name.asCanonical}", description: "Package ${pkg.name}", url: "${idldoc.packageSummaryHtml(pkg)}", icon: "http://icons.iconarchive.com/icons/iconexpo/speech-balloon-orange/16/speech-balloon-orange-p-icon.png"},
<% }; model.services.each { service -> %>
{ label: "${service.name.asCanonical}", description: "Service ${service.getQualifiedName('asCanonical')}", url: "${idldoc.serviceHtml(service)}", icon: "http://icons.iconarchive.com/icons/iconexpo/speech-balloon-green/16/speech-balloon-green-s-icon.png"},
<% }; model.structures.each { structure -> %>
{ label: "${structure.name.asCanonical}", description: "Structure ${structure.getQualifiedName('asCanonical')}", url: "${idldoc.structureHtml(structure)}", icon: "http://icons.iconarchive.com/icons/iconexpo/speech-balloon-green/16/speech-balloon-green-d-icon.png"},
<% }; model.enumerations.each { enumeration -> %>
{ label: "${enumeration.name.asCanonical}", description: "Enumeration ${enumeration.getQualifiedName('asCanonical')}", url: "${idldoc.enumerationHtml(enumeration)}", icon: "http://icons.iconarchive.com/icons/iconexpo/speech-balloon-green/16/speech-balloon-green-e-icon.png"},
<% }; idldoc.getOperationsInfo(model).each { opInfo -> %>
{ label: "${opInfo.operation.name.asCanonical}", description: "Operation in ${opInfo.service.getQualifiedName('asCanonical')} service", url: "${idldoc.operationUrl(opInfo)}", icon: "http://icons.iconarchive.com/icons/iconarchive/red-orb-alphabet/16/Letter-O-icon.png"},
<% }; idldoc.withComma(idldoc.getStructuresInfo(model), { dsInfo, comma -> %>
{ label: "${dsInfo.structure.name.asCanonical}", description: "Nested structure in ${dsInfo.service.qualifiedName} service", url: "${idldoc.structureUrl(dsInfo)}", icon: "http://icons.iconarchive.com/icons/iconarchive/red-orb-alphabet/16/Letter-D-icon.png"}${comma}
<% }) %>
]

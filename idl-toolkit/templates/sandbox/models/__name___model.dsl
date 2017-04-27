model "${model.name}" {
<% model.packages.each { p -> %>\
    package "$p.name" {
<%  p.enumerations.each { e -> %>\
        enumeration "$e.name"
<%  } %>\
<%  p.structures.each { s -> %>\
        structure "$s.name"
<%  } %>\
<%  p.services.each { s -> %>\
        service "$s.name"
<%  } %>\
    }
<% } %>
    fileSet {
<% model.fileSet.files.each { file -> %>\
        file "${file.filename}" {
<%  file.enumerations.each { e -> %>\
            enumeration "$e.name"
<%  } %>\
<%  file.structures.each { s -> %>\
            structure "$s.name"
<%  } %>\
<%  file.services.each { s -> %>\
            service "$s.name"
<%  } %>\
        }
<% } %>\
    }
}

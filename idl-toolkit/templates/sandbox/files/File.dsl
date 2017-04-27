idlFile( filename:"${idlFile.filename}" ) {
<% idlFile.services.each { s -> %>
    service "${s.name}"<% } %>
}

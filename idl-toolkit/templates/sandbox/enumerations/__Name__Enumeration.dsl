enumeration "${enumeration.name}" {
<% enumeration.constants.each { c -> %>
    constant "${c.name}"
<% } %>
}

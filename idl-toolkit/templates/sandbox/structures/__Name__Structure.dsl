structure "${structure.name}" {
<% structure.attributes.each { a -> %>
    attribute "${a.name}" {
        type "${a.type}" {
            //TODO: IdlType has changed, so this output is not correct
        }
    }
<% } %>
}

service "${service.name}" {
<% service.structures.each { s -> %>
    structure "${s.name}" {
<% s.attributes.each { a -> %>
        attribute "${a.name}" {
            type "${a.type}" {
		//TODO: IdlType has changed, so this output is not correct
	    }
        }<% } %>
    }
<% } %>
<% service.operations.each { o -> %>
    operation "${o.name}" {
<% o.parameters.each { p -> %>
        parameter "${p.name}" {
            type "${p.type}" {
	    }
        }<% } %>
        result {
            type "${o.result.type}" {
	    }
        }
<% o.errors.each { e -> %>
        error "${e.name}"<% } %>
    }
<% } %>
}

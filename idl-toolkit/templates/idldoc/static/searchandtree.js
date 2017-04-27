//Initialize tree nav and search
//Go To
function goToUrl(url) {
//    alert('
    window.location.href=url
}

//Create Tree
function initiallySelectedId() {
    if (window.apiHierarchyCurrentNodeId) {
        return [window.apiHierarchyCurrentNodeId]
    } else {
        return []
    }
}
$(function(){
    $("#apiHierarchy")
    .bind('select_node.jstree', function(event, data) {
        var selectedId = data.rslt.obj.attr('id')
        if (!window.apiHierarchyCurrentNodeId || window.apiHierarchyCurrentNodeId != selectedId ) {
            if (selectedId) {
                var href= data.rslt.obj.children("a").attr('href')
                window.location.href = href
            }
        }
    })
    .jstree({
        "plugins" : ["themes", "json_data", "ui"],
        "json_data": {
            data: treeData
        },
        "ui" : {
            "select_limit" : 1,
            "selected_parent_close" : false,
            "initially_select" : initiallySelectedId()
        }
    });
})


//Autocomplete

$(document).ready(function(){
    $('#searchInput').autocomplete({
        minLength: 2,
        source: searchData,
        focus: function(event, ui) {
            $('#searchInput').val(ui.item.label);
            return false;
        },
        select: function(event, ui) {
            goToUrl(ui.item.url);
            return false;
        }
    }).data("autocomplete")._renderItem = function(ul, item) {
        return $("<li></li>")
            .data("item.autocomplete", item)
            .append('<a><img src="' + item.icon + '"/>' + item.label + '<br/>' + item.description + "</a>")
            .appendTo(ul)
    };
});

$().ready(function(){
    $('#tree-link').click(function(){
        $('#tree-dropdown').toggle();
    });
});

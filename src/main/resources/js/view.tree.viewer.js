function buildTreeViewer() {
	resetTreeViewer();
	resizeTreeViewer();
	var rootElementType = $("select[name='select-root-element-type']").val();
	getTreeViewer(rootElementType, function(core) {
		$('#jstree').jstree({
			"core" : core,
			"plugins" : [ "dnd", "contextmenu", "wholerow", "sort", "search" ],
			"search" : {
				"show_only_matches" : true
			},
			"contextmenu" : {
				"items" : contextMenuActions
			}
		});
		$("#jstree-search-input").keyup(function() {
			var searchString = $(this).val();
			$('#jstree').jstree(true).search(searchString);
		});
	});
	addDragAndDropSupportForTreeViewer();
}

function resetTreeViewer() {
	var treeViewer = $('#jstree').jstree(true);
	if (treeViewer) {
		treeViewer.destroy();
	}
}

function getTreeViewerNodeById(nodeId) {
	if (nodeId === "#") {
		return nodeId;
	}
	return $('#jstree').jstree(true).get_node(nodeId);
}

function selectNodeInTreeViewer(nodeId) {
	$('#jstree').on("ready.jstree", function() {
		var treeViewer = $('#jstree').jstree(true);
		if (treeViewer) {
			treeViewer.select_node(nodeId);
		}
	});
}

function addDragAndDropSupportForTreeViewer() {
	$('#jstree').on('move_node.jstree', function(object, nodeInContext) {
		var node = nodeInContext.node;
		var parentNode = getTreeViewerNodeById(nodeInContext.parent);
		var oldParentNode = getTreeViewerNodeById(nodeInContext.old_parent);

		var nodeId = node.data.id;
		if (oldParentNode === "#" && parentNode !== "#") {
			createLinkToExistingElement(parentNode.data.id, nodeId);
		}
		if (parentNode === "#" && oldParentNode !== "#") {
			deleteLink(oldParentNode.data.id, nodeId, function() {
				updateIssueModule();
			});
		}
		if (parentNode !== '#' && oldParentNode !== '#') {
			deleteLink(oldParentNode.data.id, nodeId, function() {
				createLinkToExistingElement(parentNode.data.id, nodeId);
			});
		}
	});
}

function resizeTreeViewer() {
    $(function () {
        $("#jstree").resizable();
    });
}

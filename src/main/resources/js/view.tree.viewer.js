function buildTreeViewer() {
	resetTreeViewer();
	getTreeViewer(function(core) {
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

	$('#jstree').on('move_node.jstree', function(object, node) {
		if (node.node.id !== 0 && node.parent !== 0 && node.old_parent !== 0) {
			addDragAndDropSupportForTreeViewer(node.node.id, node.parent, node.old_parent);
		}
	});
}

function resetTreeViewer() {
	if ($('#jstree').jstree(true)) {
		var tree = $('#jstree').jstree(true);
		tree.destroy();
	}
}

function addDragAndDropSupportForTreeViewer(nodeId, parentId, oldParentId) {
	if (oldParentId === "#") {
		showFlag("error", "Decisions cannot be linked to another decision knowledge element.");
		document.location.reload();
	}
	if (parentId === "#") {
		showFlag("error", "This decision knowledge element cannot be a root element.");
		document.location.reload();
	}
	if (parentId !== '#' && oldParentId !== '#') {
		deleteLinkToExistingElement(oldParentId, nodeId);
		createLinkToExistingElement(parentId, nodeId);
	}
}
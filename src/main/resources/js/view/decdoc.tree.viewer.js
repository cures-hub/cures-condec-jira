function buildTreeViewer(nodeId) {
	getTreeViewer(function(core) {
		if ($('#evts').jstree(true)) {
			var tree = $('#evts').jstree(true);
			tree.destroy();
		}
		$('#evts').on("ready.jstree", function() {
			if (nodeId) {
				var tree = $('#evts').jstree(true);
				if (tree) {
					tree.select_node("" + nodeId)
					console.log("select_node");
				} else {
					console.log("set_back");
				}
			}
		}).jstree({
			"core" : core,
			"plugins" : [ "dnd", "contextmenu", "wholerow", "sort", "search" ],
			"search" : {
				"show_only_matches" : true
			},
			"contextmenu" : {
				"items" : contextMenuActions
			}
		});
		document.getElementById("Details").style.display = "block";
		$(".search-input").keyup(function() {
			var searchString = $(this).val();
			$('#evts').jstree(true).search(searchString);
		});
	});

	$('#evts').on('move_node.jstree', function(object, node) {
		if (node.node.id != 0 && node.parent != 0 && node.old_parent != 0) {
			addDragAndDropSupportForTreeViewer(node.node.id, node.parent, node.old_parent);
		}
	});
}

function addDragAndDropSupportForTreeViewer(nodeId, parentId, oldParentId) {
	if (oldParentId == "#") {
		showFlag("error", "Decisions cannot be linked to another decision knowledge element.");
		document.location.reload();
	}
	if (parentId == "#") {
		showFlag("error", "This decision knowledge element cannot be a root element.");
		document.location.reload();
	}
	if (parentId != '#' && oldParentId != '#') {
		deleteLinkToExistingElement(oldParentId, nodeId);
		createLinkToExistingElement(parentId, nodeId);
	}
}
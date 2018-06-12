function buildTreeViewer(nodeId) {
	getTreeViewer(function(core) {
		if ($('#evts').jstree(true)) {
			var tree = $('#evts').jstree(true);
			tree.destroy();
		}
		$('#evts').on("select_node.jstree", function(error, data) {
			var node = data.node.data;
			fillAccordion(node);
			buildTreant(node);
		}).on("ready.jstree", function() {
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
			// TODO: add drag n drop
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
	$(document).on('dnd_stop.vakata', function(error, data) {
		dragAndDropTreeViewer(error, data);
	});
}

function dragAndDropTreeViewer(error, data) {
	// Moved node id
	var nodeId = data.data.nodes[0];

	// New parent node id
	var target = $(data.event.target);
	var targetNode = target.closest('.jstree-node');
	var newParentId = targetNode.attr("id");

	// Old parent node id
	var tree = $('#evts').jstree(true);
	var oldParentId = tree.get_parent(nodeId);

	if (oldParentId == '#') {
		console.log("Decisions cannot be linked to another decision knowledge element.");
		location.reload();
	} else {
		deleteLinkToExistingElement(oldParentId, nodeId);
		createLinkToExistingElement(newParentId, nodeId);
		location.reload();
	}
}
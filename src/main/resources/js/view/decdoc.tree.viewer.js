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
			"plugins" : [ "dnd","contextmenu", "wholerow", "sort", "search" ],
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
    $(document)
        .on('dnd_stop.vakata', function (e, data) {
		//Moved NodeID
			var nodeID = data.data.nodes[0];
            var t = $(data.event.target);
            var targetnode = t.closest('.jstree-node');
            //New parentID
            var parentID = targetnode.attr("id");
            var tree = $('#evts').jstree(true);
            var oldParentID = tree.get_parent(nodeID);
            console.log(oldParentID);
            if(oldParentID == '#'){
		console.log("Decisions can not be linked to another Decision or Decision Knowledge Element");
		location.reload();
			} else {
                deleteLinkToExistingElement(oldParentID, nodeID);
                createLinkToExistingElement(parentID, nodeID);
                location.reload();
            }
        });
}
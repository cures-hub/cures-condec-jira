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
			},
			"dnd" : {
				"drag_finish" : function (data) {
					//TODO Adding functionality to Link the new Element
                    console.log(data);
                },
				"drop_finish" : function () {
                    //TODO Adding functionality to Unlink the old Element
                    console.log("DROP");
                }
			}
		});
		document.getElementById("Details").style.display = "block";
		$(".search-input").keyup(function() {
			var searchString = $(this).val();
			$('#evts').jstree(true).search(searchString);
		});
	});
}
function buildTreeViewer(projectKey, nodeId) {
	var treeJSONUrl = getTreeView(projectKey);
	getJSON(treeJSONUrl, function(err, data) {
		if (err !== null) {
			displayGetJsonError(err);
		} else {
			if ($('#evts').jstree(true)) {
				var tree = $('#evts').jstree(true);
				tree.destroy();
			}
			$('#evts').on("select_node.jstree", function(e, data) {
				setBack("");
				var node = data.node.data;
				fillAccordion(data, projectKey, node);
				buildTreant(projectKey, node);
			}).on('ready.jstree', function() {
				if (nodeId) {
					var tree = $('#evts').jstree(true);
					if (tree) {
						tree.select_node("" + nodeId)
						console.log("select_node");
						// no need in any scenario
					} else {
						console.log("set_back");
						// setBack("No Element has been
						// selected");
					}
				}
			}).jstree({
				'core' : data,
				// TODO: add drag n drop
				'plugins' : [ 'contextmenu', 'wholerow', 'sort', 'search', '' ],
				'search' : {
					'show_only_matches' : true
				},
				'contextmenu' : {
					'items' : contextMenuActions
				}
			});
			document.getElementById("Details").style.display = "block";
			$(".search-input").keyup(function() {
				var searchString = $(this).val();
				$('#evts').jstree(true).search(searchString);
			});
		}
	});
}
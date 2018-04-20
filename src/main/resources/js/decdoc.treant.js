function buildTreant(projectKey, node) {
	var depthOfTree = document.getElementById("depthOfTreeInput").value;
	getTreant(projectKey, node.key, depthOfTree, function(treant){
        document.getElementById("treant-container").innerHTML = "";
        new Treant(treant);
        createContextMenuForTreantNodes(projectKey);
	});
}

function createContextMenuForTreantNodes(projectKey) {
	$(function() {
		$.contextMenu({
			selector : ".decision, .rationale, .context, .problem, .solution",
			items : contextMenuActions
		});
	});
}
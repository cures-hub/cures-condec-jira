function buildTreant(decisionKnowledgeElement) {
	var depthOfTree = document.getElementById("depthOfTreeInput").value;
	getTreant(getProjectKey(), decisionKnowledgeElement.key, depthOfTree, function(treant) {
		document.getElementById("treant-container").innerHTML = "";
		new Treant(treant);
		createContextMenuForTreantNodes();
	});
}

function createContextMenuForTreantNodes() {
	$(function() {
		$.contextMenu({
			selector : ".decision, .rationale, .context, .problem, .solution",
			items : contextMenuActions
		});
	});
}
function initializeDecisionKnowledgePage() {
	updateView(getProjectKey());

	var createDecisionButton = document.getElementById("create-decision-button");
	var decisionInputField = document.getElementById("decision-input-field");
	createDecisionButton.addEventListener("click", function() {
		var summary = decisionInputField.value;
		decisionInputField.value = "";
		createDecisionKnowledgeElement(summary, "", "Decision", function(childId) {
			updateView(getProjectKey(), childId);
		});
	});

	var depthOfTreeInput = document.getElementById("depth-of-tree-input");
	depthOfTreeInput.addEventListener("input", function() {
		var depthOfTreeWarningLabel = document.getElementById("depth-of-tree-warning");
		if (this.value > 0) {
			depthOfTreeWarningLabel.style.visibility = "hidden";
		} else {
			depthOfTreeWarningLabel.style.visibility = "visible";
		}
	});
}

function updateView(nodeId){
    if ($('#jstree').jstree(true)) {
        var tree = $('#jstree').jstree(true);
        tree.destroy();
    }
    $('#jstree').on("select_node.jstree", function(error, data) {
        var node = data.node.data;
        buildTreant(node.key);
    });
    buildTreeViewer(nodeId);
}
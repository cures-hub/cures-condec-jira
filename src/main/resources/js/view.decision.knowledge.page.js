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

	var graphContainer = document.getElementById("graph-container");
	var treantContainer = document.getElementById("treant-container");

	var viewEditorButton = document.getElementById("view-graph");
	viewEditorButton.addEventListener("click", function() {
		graphContainer.style.display = "block";
		treantContainer.style.display = "none";
	});

	var viewTreeButton = document.getElementById("view-tree");
	viewTreeButton.addEventListener("click", function() {
		treantContainer.style.display = "block";
		graphContainer.style.display = "none";
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
    if ($('#evts').jstree(true)) {
        var tree = $('#evts').jstree(true);
        tree.destroy();
    }
    $('#evts').on("select_node.jstree", function(error, data) {
        var node = data.node.data;
        buildTreant(node.key);
    });
    buildTreeViewer(nodeId);
}
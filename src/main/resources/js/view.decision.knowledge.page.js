function initializeDecisionKnowledgePage() {
	updateView(getProjectKey());

	var createDecisionButton = document.getElementById("create-decision-button");
	var decisionInputField = document.getElementById("decision-input-field");
	createDecisionButton.addEventListener("click", function() {
		var summary = decisionInputField.value;
		decisionInputField.value = "";
		createDecisionKnowledgeElement(summary, "", "Decision", function(id) {
			updateView(id);
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

function updateView(nodeId) {
	buildTreeViewer();
	if (nodeId == null) {
		var rootElement = getCurrentRootElement();
		if (rootElement) {
			selectNodeInTreeViewer(rootElement.id);
		}
	} else {
		selectNodeInTreeViewer(nodeId);
	}
	$('#jstree').on("select_node.jstree", function(error, tree) {
		var node = tree.node.data;
		buildTreant(node.key);
	});
}

function getCurrentRootElement() {
	if (treantTree) {
		return treantTree.tree.initJsonConfig.graph.rootElement;
	}
}

function selectNodeInTreeViewer(nodeId) {
	$('#jstree').on("ready.jstree", function() {
		var treeViewer = $('#jstree').jstree(true);
		if (treeViewer) {
			treeViewer.select_node("" + nodeId);
		}
	});
}
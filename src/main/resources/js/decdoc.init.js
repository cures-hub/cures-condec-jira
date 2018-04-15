var knowledgeTypes = [ "Alternative", "Assessment", "Assumption", "Claim", "Comment", "Constraint", "Contra Argument",
		"Context", "Decision", "Goal", "Implication", "Issue", "Problem", "Pro Argument", "Solution" ];

function initializeSite() {
	buildTreeViewer(getProjectKey());

	/* ClickHandler for accordion elements */
	$(document).ready(function() {
		$("dt").click(function() {
			$(this).next("dd").slideToggle("fast");
		});
	});
	/* ClickHandler for the creation of decisions */
	var createDecisionButton = document.getElementById("CreateDecision");
	var DecisionInputField = document.getElementById("DecisionInputField");
	createDecisionButton.addEventListener('click', function() {
		var tempDecString = DecisionInputField.value;
		DecisionInputField.value = "";
		createDecisionKnowledgeElement(tempDecString, "TODO", "Decision", function(newId) {
			var tree = $('#evts').jstree(true);
			var nodeId = tree.create_node('#', newId, 'last', tree.redraw(true), true);
			tree.deselect_all();
			tree.select_node(nodeId);
		});
	});
	/* ClickHandler for the Editor Button */
	var viewEditorButton = document.getElementById("view-editor");
	viewEditorButton.addEventListener('click', function() {
		var editorContainer = document.getElementById("container");
		var treantContainer = document.getElementById("treant-container");
		editorContainer.style.display = "block";
		treantContainer.style.visibility = "hidden";
	});
	/* ClickHandler for the Tree Button */
	var viewTreeButton = document.getElementById("view-tree");
	viewTreeButton.addEventListener('click', function() {
		var editorContainer = document.getElementById("container");
		var treantContainer = document.getElementById("treant-container");
		treantContainer.style.visibility = "visible";
		editorContainer.style.display = "none";
	});
	var DepthOfTreeInput = document.getElementById("depthOfTreeInput");
	DepthOfTreeInput.addEventListener('input', function() {
		var DepthOfTreeWarningLabel = document.getElementById("DepthOfTreeWarning");
		if (this.value > 0) {
			DepthOfTreeWarningLabel.style.visibility = "hidden";
		} else {
			DepthOfTreeWarningLabel.style.visibility = "visible";
		}
	});
	window.onkeydown = function(event) {
		if (event.keyCode == 27) {
			var modal = document.getElementById('ContextMenuModal');
			if (modal.style.display === "block") {
				closeModal();
			}
		}
	};
}

function clearInner(node) {
	while (node.hasChildNodes()) {
		clear(node.firstChild);
	}
}

function clear(node) {
	while (node.hasChildNodes()) {
		clear(node.firstChild);
	}
	node.parentNode.removeChild(node);
}
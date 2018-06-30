var knowledgeTypes = [ "Alternative", "Assessment", "Assumption", "Claim", "Comment", "Constraint", "Contra Argument",
		"Context", "Decision", "Goal", "Implication", "Issue", "Problem", "Pro Argument", "Solution" ];

var simpleKnowledgeTypes = [ "Alternative", "Argument", "Assessment", "Assumption", "Claim", "Constraint", "Context",
		 "Goal", "Implication", "Issue", "Problem", "Solution" ];

function initializeSite() {
	updateView(getProjectKey());

	/* accordion elements */
	$(document).ready(function() {
		$("dt").click(function() {
			$(this).next("dd").slideToggle("fast");
		});
	});

	var createDecisionButton = document.getElementById("create-decision-button");
	var decisionInputField = document.getElementById("decision-input-field");
	createDecisionButton.addEventListener("click", function() {
		var summary = decisionInputField.value;
		decisionInputField.value = "";
		createDecisionKnowledgeElement(summary, "", "Decision", function(childId) {
			updateView(getProjectKey(), childId);
		});
	});

	var viewEditorButton = document.getElementById("view-editor");
	viewEditorButton.addEventListener("click", function() {
		var editorContainer = document.getElementById("container");
		var treantContainer = document.getElementById("treant-container");
		editorContainer.style.display = "block";
		treantContainer.style.visibility = "hidden";
	});

	var viewTreeButton = document.getElementById("view-tree");
	viewTreeButton.addEventListener("click", function() {
		var editorContainer = document.getElementById("container");
		var treantContainer = document.getElementById("treant-container");
		treantContainer.style.visibility = "visible";
		editorContainer.style.display = "none";
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
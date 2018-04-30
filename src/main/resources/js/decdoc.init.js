var knowledgeTypes = [ "Alternative", "Assessment", "Assumption", "Claim", "Comment", "Constraint", "Contra Argument",
		"Context", "Decision", "Goal", "Implication", "Issue", "Problem", "Pro Argument", "Solution" ];

var simpleKnowledgeTypes = [ "Alternative", "Argument", "Assessment", "Assumption", "Claim", "Constraint", "Context",
		 "Goal", "Implication", "Issue", "Problem", "Solution" ];

function initializeSite() {
	buildTreeViewer(getProjectKey());

	/* accordion elements */
	$(document).ready(function() {
		$("dt").click(function() {
			$(this).next("dd").slideToggle("fast");
		});
	});

	var createDecisionButton = document.getElementById("CreateDecision");
	var DecisionInputField = document.getElementById("DecisionInputField");
	createDecisionButton.addEventListener("click", function() {
		var summary = DecisionInputField.value;
		DecisionInputField.value = "";
		createDecisionKnowledgeElement(summary, "", "Decision", function(childId) {
			buildTreeViewer(getProjectKey(), childId);
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

	var depthOfTreeInput = document.getElementById("depthOfTreeInput");
	depthOfTreeInput.addEventListener("input", function() {
		var DepthOfTreeWarningLabel = document.getElementById("DepthOfTreeWarning");
		if (this.value > 0) {
			DepthOfTreeWarningLabel.style.visibility = "hidden";
		} else {
			DepthOfTreeWarningLabel.style.visibility = "visible";
		}
	});
}
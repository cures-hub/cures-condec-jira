var knowledgeTypes = [ "Alternative", "Argument", "Assessment", "Assumption", "Claim",
		"Constraint", "Context", "Goal", "Implication", "Issue", "Problem", "Solution" ];

function initializeSite() {
	buildTreeViewer(getProjectKey());

	/*ClickHandler for accordion elements*/
	$(document).ready(function() {
		$("dt").click(function() {
			$(this).next("dd").slideToggle("fast");
		});
	});
	/*ClickHandler for the creation of decisions*/
	var createDecisionButton = document.getElementById("CreateDecision");
	var DecisionInputField = document.getElementById("DecisionInputField");
	createDecisionButton.addEventListener('click', function() {
		var tempDecString = DecisionInputField.value;
		DecisionInputField.value = "";
		createDecisionKnowledgeElement(tempDecString, "Decision", function(newId) {
			var tree = $('#evts').jstree(true);
			var nodeId = tree.create_node('#', newId, 'last', tree.redraw(true), true);
			tree.deselect_all();
			tree.select_node(nodeId);
		});
	});
	/*ClickHandler for the Editor Button*/
	var viewEditorButton = document.getElementById("view-editor");
	viewEditorButton.addEventListener('click', function() {
		var editorContainer = document.getElementById("container");
		var treantContainer = document.getElementById("treant-container");
		editorContainer.style.display = "block";
		treantContainer.style.visibility = "hidden";
	});
	/*ClickHandler for the Tree Button*/
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

function closeModal() {
	// Get the modal window
	var modal = document.getElementById('ContextMenuModal');
	modal.style.display = "none";
	var modalHeader = document.getElementById('modal-header');
	if (modalHeader.hasChildNodes()) {
		var childNodes = modalHeader.childNodes;
		for (var index = 0; index < childNodes.length; ++index) {
			var child = childNodes[index];
			if (child.nodeType === 3) {
				child.parentNode.removeChild(child);
			}
		}
	}
	var modalContent = document.getElementById('modal-content');
	if (modalContent) {
		clearInner(modalContent);
	}
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

function setSubmitFunction(submitButton, type_select, projectKey, id) {
	submitButton.onclick = function() {
		var summary = document.getElementById('form-input-name').value;
		var type = type_select.val();
		if (type === "Argument") {
			var argumentCheckBoxGroup = document.getElementsByName("type-of-argument");
			for (var i = 0; i < argumentCheckBoxGroup.length; i++) {
				if (argumentCheckBoxGroup[i].checked === true) {
					var selectedNatureOfArgument = argumentCheckBoxGroup[i].value;
					if (selectedNatureOfArgument === "pro") {
						createDecisionKnowledgeElement(summary, type, function(newId) {
							createLink(id, newId, "support", function() {
								buildTreeViewer(projectKey, newId);
							});
						});
					} else if (selectedNatureOfArgument === "contra") {
						createDecisionKnowledgeElement(summary, type, function(newId) {
							createLink(id, newId, "attack", function() {
								buildTreeViewer(projectKey, newId);
							});
						});
					} else if (selectedNatureOfArgument === "comment") {
						createDecisionKnowledgeElement(summary, type, function(newId) {
							createLink(id, idOfNewObject, "comment", function() {
								buildTreeViewer(projectKey, newId);
							});
						});
					}
				}
			}
		} else {
			createDecisionKnowledgeElement(summary, type, function(newId) {
				createLink(id, newId, "contain", function() {
					buildTreeViewer(projectKey, newId);
				});
			});
		}
		closeModal();
	};
}
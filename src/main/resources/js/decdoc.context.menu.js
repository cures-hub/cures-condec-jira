var createKnowledgeElementText = "Add Decision Component";
var editKnowledgeElementText = "Edit Decision Component";
var deleteKnowledgeElementText = "Delete Decision Component";

function setHeaderText(headerText) {
	var header = document.getElementById("context-menu-header");
	header.textContent = headerText;
}

function setUpContextMenuContentForCreateAction(id) {
	setUpModal();
	setHeaderText(createKnowledgeElementText);
	setUpContextMenuContent("", "", null, createKnowledgeElementText);

	var submitButton = document.getElementById("form-input-submit");
	submitButton.onclick = function() {
		var summary = document.getElementById('form-input-summary').value;
		var description = document.getElementById('form-input-description').value;
		var type = $("select[name='form-select-type']").val();
		// TODO: Enable to show arguments. They are currently not shown due to
		// an inward-outward link problem.
		switch (type) {
		case "Pro Argument":
			createDecisionKnowledgeElement(summary, description, "Argument", function(newId) {
				createLink(newId, id, "support", function() {
					buildTreeViewer(getProjectKey(), newId);
				});
			});
			break;
		case "Contra Argument":
			createDecisionKnowledgeElement(summary, description, "Argument", function(newId) {
				createLink(newId, id, "attack", function() {
					buildTreeViewer(getProjectKey(), newId);
				});
			});
			break;
		case "Comment":
			createDecisionKnowledgeElement(summary, description, "Argument", function(newId) {
				createLink(newId, id, "comment", function() {
					buildTreeViewer(getProjectKey(), newId);
				});
			});
			break;
		default:
			createDecisionKnowledgeElement(summary, description, type, function(newId) {
				createLink(id, newId, "contain", function() {
					buildTreeViewer(getProjectKey(), newId);
				});
			});
		}
		closeModal();
	};
}

var contextMenuCreateAction = {
	// label is used in Tree Viewer context menu
	"label" : createKnowledgeElementText,
	// name is used in Treant context menu
	"name" : createKnowledgeElementText,
	// action is used in Tree Viewer context menu
	"action" : function(node) {
		// gets the selected Tree Viewer node
		var treeNode = getSelectedTreeViewerNode(node);
		var id = treeNode.id;
		setUpContextMenuContentForCreateAction(id);
	},
	// callback is used in Treant context menu
	"callback" : function(key, options) {
		// gets the selected Treant node
		var context = options.$trigger.context;
		var id = context.id;
		setUpContextMenuContentForCreateAction(id);
	}
}

function setUpContextMenuContentForEditAction(id) {
	setUpModal();
	setHeaderText(editKnowledgeElementText);
	getDecisionKnowledgeElement(id, getProjectKey(), function(decisionKnowledgeElement) {
		var summary = decisionKnowledgeElement.summary;
		var description = decisionKnowledgeElement.description;
		var type = decisionKnowledgeElement.type;
		setUpContextMenuContent(summary, description, type, editKnowledgeElementText);
		var submitButton = document.getElementById("form-input-submit");
		submitButton.onclick = function() {
			var summary = document.getElementById("form-input-summary").value;
			var description = document.getElementById("form-input-description").value;
			var type = $("select[name='form-select-type']").val();

			switch (type) {
			case "Pro Argument":
				editDecisionKnowledgeElement(id, summary, description, "Argument", function() {
					buildTreeViewer(getProjectKey(), id);
				});
				break;
			case "Contra Argument":
				editDecisionKnowledgeElement(id, summary, description, "Argument", function() {
					buildTreeViewer(getProjectKey(), id);
				});
				break;
			case "Comment":
				editDecisionKnowledgeElement(id, summary, description, "Argument", function() {
					buildTreeViewer(getProjectKey(), id);
				});
				break;
			default:
				editDecisionKnowledgeElement(id, summary, description, type, function() {
					buildTreeViewer(getProjectKey(), id);
				});
			}
			closeModal();
		};
	});
}

var contextMenuEditAction = {
	// label is used in Tree Viewer context menu
	"label" : editKnowledgeElementText,
	// name is used in Treant context menu
	"name" : editKnowledgeElementText,
	// action is used in Tree Viewer context menu
	"action" : function(node) {
        //TODO action is deprecated after Updating to 3.3.1 Jquery?
		// treeNode.id Cannot read property 'id' of undefined after ContextMenu Delete and Edit
		var treeNode = getSelectedTreeViewerNode(node);
		var id = treeNode.id;
		setUpContextMenuContentForEditAction(id);
	},
	// callback is used in Treant context menu
	"callback" : function(key, options) {
		var context = options.$trigger.context;
		var id = context.id;
		setUpContextMenuContentForEditAction(id);
	}
}

function setUpContextMenuContentForDeleteAction(id) {
	setUpModal();
	setHeaderText(deleteKnowledgeElementText);

	var content = document.getElementById("modal-content");
	content.insertAdjacentHTML("afterBegin",
			"<p><input id='abort-submit' type='submit' value='Abort Deletion' style='float:right;'/>"
					+ "<input id='form-input-submit' type='submit' value=" + deleteKnowledgeElementText
					+ " style='float:right;'/></p>");

	var abortButton = document.getElementById("abort-submit");
	abortButton.onclick = function() {
		closeModal();
	};

	var submitButton = document.getElementById("form-input-submit");
	submitButton.onclick = function() {
		deleteDecisionKnowledgeElement(id, function() {
			buildTreeViewer(getProjectKey(), id);
		});
		closeModal();
	};
}

var contextMenuDeleteAction = {
	// label is used in Tree Viewer context menu
	"label" : deleteKnowledgeElementText,
	// name is used in Treant context menu
	"name" : deleteKnowledgeElementText,
	// action is used in Tree Viewer context menu
	"action" : function(node) {
		// gets the selected Tree Viewer node
		var treeNode = getSelectedTreeViewerNode(node);
		var id = treeNode.id;
		setUpContextMenuContentForDeleteAction(id);
	},
	// callback is used in Treant context menu
	"callback" : function(key, options) {
		// gets the selected Treant node
		var context = options.$trigger.context;
		var id = context.id;
		setUpContextMenuContentForDeleteAction(id);
	}
}

var contextMenuActions = {
	"create" : contextMenuCreateAction,
	"edit" : contextMenuEditAction,
	"delete" : contextMenuDeleteAction,
}

function getSelectedTreeViewerNode(node) {
	var selector = node.reference.prevObject.selector;
	return $("#evts").jstree(true).get_node(selector).data;
}

function setUpModal() {
	var modal = document.getElementById("ContextMenuModal");
	modal.style.display = "block";
}

function closeModal() {
	var modal = document.getElementById("ContextMenuModal");
	modal.style.display = "none";
	var modalHeader = document.getElementById("modal-header");
	if (modalHeader.hasChildNodes()) {
		var childNodes = modalHeader.childNodes;
		for (var index = 0; index < childNodes.length; ++index) {
			var child = childNodes[index];
			if (child.nodeType === 3) {
				child.parentNode.removeChild(child);
			}
		}
	}
	var modalContent = document.getElementById("modal-content");
	if (modalContent) {
		clearInner(modalContent);
	}
}

function setUpContextMenuContent(summary, description, decisionType, buttonText) {
	var content = document.getElementById("modal-content");
	content.insertAdjacentHTML(
		"afterBegin",
		"<p><label for='form-input-summary' style='display:block;width:45%;float:left;'>Summary:</label>"
				+ "<input id='form-input-summary' type='text' placeholder='Summary' value='"
				+ summary
				+ "' style='width:50%;'/></p>"
				+ "<p><label for='form-input-description' style='display:block;width:45%;float:left;'>Description:</label>"
				+ "<input id='form-input-description' type='text' placeholder='Description' value='"
				+ description
				+ "' style='width:50%;'/></p>"
				+ "<p><label for='form-select-type' style='display:block;width:45%;float:left;'>Knowledge type:</label>"
				+ "<select name='form-select-type' style='width:50%;'/></p>"
				+ "<p><input id='form-input-submit' type='submit' value='" + buttonText
				+ "' style='float:right;'/></p>");

    
	for (var index = 0; index < knowledgeTypes.length; index++) {
		if(decisionType.toLowerCase() == knowledgeTypes[index].toLocaleLowerCase()) {
            $("select[name='form-select-type']")[0].insertAdjacentHTML("beforeend", "<option selected value='" + knowledgeTypes[index] + "'>"
                + knowledgeTypes[index] + "</option>");
        } else {
            $("select[name='form-select-type']")[0].insertAdjacentHTML("beforeend", "<option value='" + knowledgeTypes[index] + "'>"
                + knowledgeTypes[index] + "</option>");
		}
	}
}
var createKnowledgeElementText = "Add Decision Component";
var linkKnowledgeElementText = "Link Decision Component";
var editKnowledgeElementText = "Edit Decision Component";
var deleteKnowledgeElementText = "Delete Decision Component";

function setUpModal() {
	var modal = document.getElementById("ContextMenuModal");
	modal.style.display = "block";

	// add click-handler for elements in modal to close modal window
	var elementsWithCloseFunction = document.getElementsByClassName("modal-close");
	for (var counter = 0; counter < elementsWithCloseFunction.length; counter++) {
		elementsWithCloseFunction[counter].onclick = function() {
			closeModal();
		}
	}

	// close modal window if user clicks anywhere outside of the modal
	window.onclick = function(event) {
		if (event.target == modal) {
			closeModal();
		}
	};
}

function setHeaderText(headerText) {
	var header = document.getElementById("context-menu-header");
	header.textContent = headerText;
}

function isKnowledgeTypeLocatedAtIndex(knowledgeType, index) {
	return knowledgeType.toLowerCase() == knowledgeTypes[index].toLocaleLowerCase();
}

function setUpContextMenuContent(summary, description, knowledgeType, buttonText) {
	document
			.getElementById("modal-content")
			.insertAdjacentHTML(
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
		if (isKnowledgeTypeLocatedAtIndex(knowledgeType, index)) {
			var isSelected = "selected ";
		} else {
			var isSelected = "";
		}
		$("select[name='form-select-type']")[0].insertAdjacentHTML("beforeend", "<option " + isSelected + "value='"
				+ knowledgeTypes[index] + "'>" + knowledgeTypes[index] + "</option>");
	}
}

function setUpContextMenuContentForCreateAction(id) {
	setUpModal();
	setHeaderText(createKnowledgeElementText);
	setUpContextMenuContent("", "", "Alternative", createKnowledgeElementText);

	var submitButton = document.getElementById("form-input-submit");
	submitButton.onclick = function() {
		var summary = document.getElementById('form-input-summary').value;
		var description = document.getElementById('form-input-description').value;
		var type = $("select[name='form-select-type']").val();
		createDecisionKnowledgeElementAsChild(summary, description, type, id);
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
		var id = getSelectedTreeViewerNodeId(node);
		setUpContextMenuContentForCreateAction(id);
	},
	// callback is used in Treant context menu
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
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
			editDecisionKnowledgeElementAsChild(summary, description, type, id);
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
		var id = getSelectedTreeViewerNodeId(node);
		setUpContextMenuContentForEditAction(id);
	},
	// callback is used in Treant context menu
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
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
		var id = getSelectedTreeViewerNodeId(node);
		setUpContextMenuContentForDeleteAction(id);
	},
	// callback is used in Treant context menu
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpContextMenuContentForDeleteAction(id);
	}
}

function setUpContextMenuContentForLinkAction(id) {
	setUpModal();
	setHeaderText(linkKnowledgeElementText);

	getUnlinkedDecisionComponents(
			id,
			getProjectKey(),
			function(unlinkedDecisionComponents) {
				var insertString = "<p><label for='form-select-component' style='display:block;width:45%;float:left;'>Unlinked Element:</label>"
						+ "<select name='form-select-component' style='width:50%;' />";
				for (var index = 0; index < unlinkedDecisionComponents.length; index++) {
					insertString += "<option value='" + unlinkedDecisionComponents[index].id + "'>"
							+ unlinkedDecisionComponents[index].text + "</option>";
				}
				insertString += "</p> <p><input name='form-input-submit' id='form-input-submit' type='submit' value='"
						+ linkKnowledgeElementText + "' style='float:right;'/></p>";

				var content = document.getElementById("modal-content");
				content.insertAdjacentHTML("afterBegin", insertString);

				var submitButton = document.getElementById("form-input-submit");
				submitButton.onclick = function() {
					var childId = $("select[name='form-select-component']").val();
					createLinkToExistingElement(id, childId);
					closeModal();
				};
			});
}

var contextMenuLinkAction = {
	// label is used in Tree Viewer context menu
	"label" : linkKnowledgeElementText,
	// name is used in Treant context menu
	"name" : linkKnowledgeElementText,
	// action is used in Tree Viewer context menu
	"action" : function(node) {
		var id = getSelectedTreeViewerNodeId(node);
		setUpContextMenuContentForLinkAction(id);
	},
	// callback is used in Treant context menu
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpContextMenuContentForLinkAction(id);
	}
}

var contextMenuActions = {
	"create" : contextMenuCreateAction,
	"link" : contextMenuLinkAction,
	"edit" : contextMenuEditAction,
	"delete" : contextMenuDeleteAction
}

function getSelectedTreeViewerNodeId(node) {
	// TODO action is deprecated after Updating to 3.3.1 Jquery?
	// treeNode.id Cannot read property 'id' of undefined after ContextMenu
	// Delete and Edit
	var selector = node.reference.prevObject.selector;
	nodeData = $("#evts").jstree(true).get_node(selector).data;
	return nodeData.id;
}

function getSelectedTreantNodeId(options) {
	var context = options.$trigger.context;
	return context.id;
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
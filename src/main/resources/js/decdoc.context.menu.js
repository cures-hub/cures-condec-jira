var createKnowledgeElementText = "Add Decision Component";
var linkKnowledgeElementText = "Link Decision Component";
var editKnowledgeElementText = "Edit Decision Component";
var deleteKnowledgeElementText = "Delete Decision Component";

var contextMenuCreateAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : createKnowledgeElementText,
	"name" : createKnowledgeElementText,
	"action" : function(node) {
		var id = getSelectedTreeViewerNodeId(node);
		setUpContextMenuContentForCreateAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpContextMenuContentForCreateAction(id);
	}
}

function getSelectedTreeViewerNodeId(node) {
	// TODO deprecated after Updating to 3.3.1 jquery
	// treeNode.id cannot read property 'id'
	var selector = node.reference.prevObject.selector;
	nodeData = $("#evts").jstree(true).get_node(selector).data;
	return nodeData.id;
}

function getSelectedTreantNodeId(options) {
	var context = options.$trigger.context;
	return context.id;
}

function setUpContextMenuContentForCreateAction(id) {
	setUpModal();
	setHeaderText(createKnowledgeElementText);
	setUpContextMenuContent("", "", "Alternative", createKnowledgeElementText);

	var submitButton = document.getElementById("form-input-submit");
	submitButton.onclick = function() {
		var summary = document.getElementById("form-input-summary").value;
		var description = document.getElementById("form-input-description").value;
		var type = $("select[name='form-select-type']").val();
		createDecisionKnowledgeElementAsChild(summary, description, type, id);
		closeModal();
	};
}

function setUpModal() {
	var modal = document.getElementById("ContextMenuModal");
	modal.style.display = "block";

	// adds click-handler for elements in modal to close modal window
	var elementsWithCloseFunction = document.getElementsByClassName("modal-close");
	for (var counter = 0; counter < elementsWithCloseFunction.length; counter++) {
		elementsWithCloseFunction[counter].onclick = function() {
			closeModal();
		}
	}

	// closes modal window if user clicks anywhere outside of the modal
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

function isKnowledgeTypeLocatedAtIndex(knowledgeType, index) {
	return knowledgeType.toLowerCase() == knowledgeTypes[index].toLocaleLowerCase();
}

var contextMenuLinkAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : linkKnowledgeElementText,
	"name" : linkKnowledgeElementText,
	"action" : function(node) {
		var id = getSelectedTreeViewerNodeId(node);
		setUpContextMenuContentForLinkAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpContextMenuContentForLinkAction(id);
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
							+ unlinkedDecisionComponents[index].type + ' / ' + unlinkedDecisionComponents[index].summary + "</option>";
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

var contextMenuEditAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : editKnowledgeElementText,
	"name" : editKnowledgeElementText,
	"action" : function(node) {
		var id = getSelectedTreeViewerNodeId(node);
		setUpContextMenuContentForEditAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpContextMenuContentForEditAction(id);
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

var contextMenuDeleteAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : deleteKnowledgeElementText,
	"name" : deleteKnowledgeElementText,
	"action" : function(node) {
		var id = getSelectedTreeViewerNodeId(node);
		setUpContextMenuContentForDeleteAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpContextMenuContentForDeleteAction(id);
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

var contextMenuActions = {
	"create" : contextMenuCreateAction,
	"link" : contextMenuLinkAction,
	"edit" : contextMenuEditAction,
	"delete" : contextMenuDeleteAction
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
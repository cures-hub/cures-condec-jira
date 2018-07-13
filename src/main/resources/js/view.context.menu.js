var createKnowledgeElementText = "Add Element";
var linkKnowledgeElementText = "Link Existing Element";
var deleteLinkToParentText = "Delete Link to Parent";
var editKnowledgeElementText = "Edit Element";
var deleteKnowledgeElementText = "Delete Element";

var contextMenuCreateAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : createKnowledgeElementText,
	"name" : createKnowledgeElementText,
	"action" : function(position) {
		var id = getSelectedTreeViewerNodeId(position);
		setUpContextMenuContentForCreateAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpContextMenuContentForCreateAction(id);
	}
};

function getSelectedTreeViewerNode(position) {
	var selector = position.reference.prevObject.selector;
	return $("#evts").jstree(true).get_node(selector);
}

function getSelectedTreeViewerNodeId(node) {
	return getSelectedTreeViewerNode(node).id;
}

function getSelectedTreantNodeId(options) {
	var context = options.$trigger.context;
	return context.id;
}

function setUpContextMenuContentForCreateAction(id) {
	setUpModal();
	setHeaderText(createKnowledgeElementText);
	setUpContextMenuContent("", "", "Alternative");

	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = createKnowledgeElementText;
	submitButton.onclick = function() {
		var summary = document.getElementById("form-input-summary").value;
		var description = document.getElementById("form-input-description").value;
		var type = $("select[name='form-select-type']").val();
		createDecisionKnowledgeElementAsChild(summary, description, type, id);
		closeModal();
	};
}

function setUpModal() {
	AJS.dialog2("#context-menu-modal").show();
	AJS.dialog2("#context-menu-modal").on("hide", function() {
		clearModalContent();
	});
}

function setHeaderText(headerText) {
	var header = document.getElementById("context-menu-header");
	header.textContent = headerText;
}

function setUpContextMenuContent(summary, description, knowledgeType) {
	document
			.getElementById("modal-content")
			.insertAdjacentHTML(
					"afterBegin",
					"<form class='aui'><div class='field-group'><label for='form-input-summary'>Summary:</label>"
							+ "<input id='form-input-summary' type='text' placeholder='Summary' value='"
							+ summary
							+ "' class='text long-field'/></div>"
							+ "<div class='field-group'><label for='form-input-description'>Description:</label>"
							+ "<input id='form-input-description' type='text' placeholder='Description' value='"
							+ description
							+ "' class='text long-field'/></div>"
							+ "<div class='field-group'><label for='form-select-type'>Knowledge type:</label>"
							+ "<select name='form-select-type' class='select'/></div></form>");

	for (var index = 0; index < extendedKnowledgeTypes.length; index++) {
		var isSelected = "";
		if (isKnowledgeTypeLocatedAtIndex(knowledgeType, index)) {
			isSelected = "selected ";
		}
		$("select[name='form-select-type']")[0].insertAdjacentHTML("beforeend", "<option " + isSelected + "value='"
				+ extendedKnowledgeTypes[index] + "'>" + extendedKnowledgeTypes[index] + "</option>");
	}
}

function isKnowledgeTypeLocatedAtIndex(knowledgeType, index) {
	return knowledgeType.toLowerCase() === extendedKnowledgeTypes[index].toLocaleLowerCase();
}

var contextMenuLinkAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : linkKnowledgeElementText,
	"name" : linkKnowledgeElementText,
	"action" : function(position) {
		var id = getSelectedTreeViewerNodeId(position);
		setUpContextMenuContentForLinkAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpContextMenuContentForLinkAction(id);
	}
};

function setUpContextMenuContentForLinkAction(id) {
	setUpModal();
	setHeaderText(linkKnowledgeElementText);

	getUnlinkedDecisionComponents(
			id,
			function(unlinkedDecisionComponents) {
				var insertString = "<form class='aui'><div class='field-group'><label for='form-select-component'>Unlinked Element:</label>"
						+ "<select name='form-select-component' class='select'/>";
				for (var index = 0; index < unlinkedDecisionComponents.length; index++) {
					insertString += "<option value='" + unlinkedDecisionComponents[index].id + "'>"
							+ unlinkedDecisionComponents[index].type + ' / '
							+ unlinkedDecisionComponents[index].summary + "</option>";
				}
				insertString += "</div></form>";

				var content = document.getElementById("modal-content");
				content.insertAdjacentHTML("afterBegin", insertString);

				var submitButton = document.getElementById("dialog-submit-button");
				submitButton.textContent = linkKnowledgeElementText;
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
	"action" : function(position) {
		var id = getSelectedTreeViewerNodeId(position);
		setUpContextMenuContentForEditAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpContextMenuContentForEditAction(id);
	}
};

function setUpContextMenuContentForEditAction(id) {
	isIssueStrategy(id, function(isIssueStrategy) {
		if (isIssueStrategy === true) {
			setUpModal();
			var modal = document.getElementById("modal-content");
			var url = AJS.contextPath() + "/secure/EditIssue!default.jspa?id=" + id;
			var iframe = "<iframe src='" + url + "' style='border:none' height='100%' width='100%'></iframe>";
			modal.insertAdjacentHTML("afterBegin", iframe);
		} else {
			setUpModal();
			setHeaderText(editKnowledgeElementText);
			getDecisionKnowledgeElement(id, function(decisionKnowledgeElement) {
				var summary = decisionKnowledgeElement.summary;
				var description = decisionKnowledgeElement.description;
				var type = decisionKnowledgeElement.type;
				setUpContextMenuContent(summary, description, type);

				var submitButton = document.getElementById("dialog-submit-button");
				submitButton.textContent = editKnowledgeElementText;
				submitButton.onclick = function() {
					var summary = document.getElementById("form-input-summary").value;
					var description = document.getElementById("form-input-description").value;
					var type = $("select[name='form-select-type']").val();
					editDecisionKnowledgeElementAsChild(summary, description, type, id);
					closeModal();
				};
			});
		}
	});
}

var contextMenuDeleteAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : deleteKnowledgeElementText,
	"name" : deleteKnowledgeElementText,
	"action" : function(position) {
		var id = getSelectedTreeViewerNodeId(position);
		setUpContextMenuContentForDeleteAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpContextMenuContentForDeleteAction(id);
	}
};

function setUpContextMenuContentForDeleteAction(id) {
	setUpModal();
	setHeaderText(deleteKnowledgeElementText);

	var content = document.getElementById("modal-content");
	content.textContent = "Do you really want to delete this element?";

	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = deleteKnowledgeElementText;
	submitButton.onclick = function() {
		deleteDecisionKnowledgeElement(id, function() {
			updateView(id);
		});
		closeModal();
	};
}

var contextMenuDeleteLinkAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : deleteLinkToParentText,
	"name" : deleteLinkToParentText,
	"action" : function(position) {
		var node = getSelectedTreeViewerNode(position);
		var id = node.id;
		var parentId = node.parent;
		setUpContextMenuContentForDeleteLinkAction(id, parentId);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		var parentId = findParentId(id);
		setUpContextMenuContentForDeleteLinkAction(id, parentId);
	}
};

function setUpContextMenuContentForDeleteLinkAction(id, parentId) {
	setUpModal();
	setHeaderText(deleteLinkToParentText);

	var content = document.getElementById("modal-content");
	content.textContent = "Do you really want to delete the link to the parent element?";

	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = deleteLinkToParentText;
	submitButton.onclick = function() {
		deleteLinkToExistingElement(parentId, id);
		closeModal();
	};
}

var contextMenuActions = {
	"create" : contextMenuCreateAction,
	"edit" : contextMenuEditAction,
	"link" : contextMenuLinkAction,
	"deleteLink" : contextMenuDeleteLinkAction,
	"delete" : contextMenuDeleteAction
};

function closeModal() {
	AJS.dialog2("#context-menu-modal").hide();
}

function clearModalContent() {
	var modalHeader = document.getElementById("context-menu-header");
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
// TODO create closure/object
/**
 * This module is responsible for:
 * + showing a context menu upon left mouse click
 * ++ on a sentence or on an issue element
 * + building the context menu
 * ++ setUpDialogForCreateAction
 * ++ setUpDialogForLinkAction
 * ++ setUpDialogForEditAction
 * ++ setUpDialogForDeleteAction
 * ++ setUpDialogForDeleteLinkAction
 * ++ setUpDialogForEditSentenceAction

 * + opening a dialog
 * + pre-filling the dialog depending on action and element
 * + setting the click event for closing the dialog (too many times)
 *
 *
 */

// closure locals
var conDecDialogOnHideSet = false;

/* TODO replace labels with a i18n resource */
var makeRootText = "Set as Root";
var openIssueText = "Open JIRA Issue";
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
		setUpDialogForCreateAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpDialogForCreateAction(id);
	}
};

function getSelectedTreeViewerNode(position) {
	console.log("view.context.menu.js getSelectedTreeViewerNode");
	var selector = position.reference.prevObject.selector;
	return jQueryConDec("#jstree").jstree(true).get_node(selector);
}

function getSelectedTreeViewerNodeId(node) {
	console.log("view.context.menu.js getSelectedTreeViewerNodeId");
	return getSelectedTreeViewerNode(node).data.id;
}

function getSelectedTreantNodeId(options) {
	console.log("view.context.menu.js getSelectedTreantNodeId");
	var context = options.$trigger.context;
	return context.id;
}

function setUpDialogForCreateAction(id) {
	console.log("view.context.menu.js setUpDialogForCreateAction");
	setHeaderText(createKnowledgeElementText);
	setUpCreateOrEditDialog("", "", "Alternative");

	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = createKnowledgeElementText;
	submitButton.onclick = function() {
		var summary = document.getElementById("form-input-summary").value;
		var description = document.getElementById("form-input-description").value;
		var type = $("select[name='form-select-type']").val();
		createDecisionKnowledgeElementAsChild(summary, description, type, id);
		AJS.dialog2("#dialog").hide();
	};

	conDecAPI.isIssueStrategy(id, function(isIssueStrategy) { // TODO: rename
		// param name,
		// confusing.
		if (isIssueStrategy === true) {
			var extensionButton = document.getElementById("dialog-extension-button");
			extensionButton.style.visibility = "visible";
			extensionButton.onclick = function() {
				var createCreateIssueForm = require('quick-edit/form/factory/create-issue');
				createCreateIssueForm({
					parentIssueId : id,
					pid : getProjectId()
				}).asDialog({
					windowTitle : createKnowledgeElementText
				}).show();
				AJS.dialog2("#dialog").hide();
			};
		}
	});

	setUpDialog();
}

/*
 * attaches cancel button handler shows(creates) the dialog TODO: attach should
 * be moved out from this function TODO: rename to maybe showDialog()
 */
function setUpDialog() {
	console.log("view.context.menu.js setUpDialog");
	AJS.dialog2("#dialog").show();
	if (!conDecDialogOnHideSet) {
		AJS.dialog2("#dialog").on("hide", function() {
			resetDialog();
		});
		conDecDialogOnHideSet = true;
	}
}

function setHeaderText(headerText) {
	console.log("view.context.menu.js headerText");
	var header = document.getElementById("dialog-header");
	header.textContent = headerText;
}

function setUpCreateOrEditDialog(summary, description, knowledgeType) {
	console.log("view.context.menu.js setUpCreateOrEditDialog");
	document.getElementById("dialog-content").insertAdjacentHTML(
			"afterBegin",
			"<form class='aui'><div class='field-group'><label for='form-input-summary'>Summary:</label>"
					+ "<input id='form-input-summary' type='text' placeholder='Summary' value='" + summary
					+ "' class='text full-width-field'/></div>"
					+ "<div class='field-group'><label for='form-input-description'>Description:</label>"
					+ "<textarea id='form-input-description' placeholder='Description' value='" + description
					+ "' class='textarea full-width-field'>" + description + "</textarea></div>"
					+ "<div class='field-group'><label for='form-select-type'>Knowledge type:</label>"
					+ "<select id='form-select-type' name='form-select-type' class='select full-width-field'/></div>"
					+ "</form>");

	for (var index = 0; index < extendedKnowledgeTypes.length; index++) {
		var isSelected = "";
		if (isKnowledgeTypeLocatedAtIndex(knowledgeType, index)) {
			isSelected = "selected ";
		}
		$("select[name='form-select-type']")[0].insertAdjacentHTML("beforeend", "<option " + isSelected + "value='"
				+ extendedKnowledgeTypes[index] + "'>" + extendedKnowledgeTypes[index] + "</option>");
	}
	AJS.$("#form-select-type").auiSelect2();
}

function isKnowledgeTypeLocatedAtIndex(knowledgeType, index) {
	console.log("view.context.menu.js isKnowledgeTypeLocatedAtIndex");
	return knowledgeType.toLowerCase() === extendedKnowledgeTypes[index].toLowerCase();
}

var contextMenuLinkAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : linkKnowledgeElementText,
	"name" : linkKnowledgeElementText,
	"action" : function(position) {
		var id = getSelectedTreeViewerNodeId(position);
		setUpDialogForLinkAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpDialogForLinkAction(id);
	}
};

function setUpDialogForLinkAction(id) {
	console.log("view.context.menu.js setUpDialogForLinkAction");
	console.log(id);
	setUpDialog();
	setHeaderText(linkKnowledgeElementText);

	conDecAPI.getUnlinkedElements(id, function(unlinkedElements) {
		var insertString = "<form class='aui'><div class='field-group' id='select-field-group'></div>"
				+ "<div class='field-group' id='argument-field-group'></div></form>";
		var content = document.getElementById("dialog-content");
		content.insertAdjacentHTML("afterBegin", insertString);

		insertString = "<label for='form-select-component'>Unlinked Element:</label>"
				+ "<select id='form-select-component' name='form-select-component' "
				+ "onchange='addFormForArguments()' class='select full-width-field'/>";
		for (var index = 0; index < unlinkedElements.length; index++) {
			insertString += "<option value='" + unlinkedElements[index].id + "'>" + unlinkedElements[index].type
					+ ' / ' + unlinkedElements[index].summary + "</option>";
		}
		var selectFieldGroup = document.getElementById("select-field-group");
		selectFieldGroup.insertAdjacentHTML("afterBegin", insertString);
		AJS.$("#form-select-component").auiSelect2();
		addFormForArguments();

		var submitButton = document.getElementById("dialog-submit-button");
		submitButton.textContent = linkKnowledgeElementText;
		submitButton.onclick = function() {
			var childId = $("select[name='form-select-component']").val();
			var knowledgeTypeOfChild = $('input[name=form-radio-argument]:checked').val();
			createLinkToExistingElement(id, childId, knowledgeTypeOfChild);
			AJS.dialog2("#dialog").hide();
		};
	});
}

function addFormForArguments() {
	console.log("view.context.menu.js addFormForArguments");
	var childId = $("select[name='form-select-component']").val();
	var argumentFieldGroup = document.getElementById("argument-field-group");
	argumentFieldGroup.innerHTML = "";
	conDecAPI
			.getDecisionKnowledgeElement(
					childId,
					function(decisionKnowledgeElement) {
						if (decisionKnowledgeElement && decisionKnowledgeElement.type === "Argument") {
							insertString = "<label for='form-radio-argument'>Type of Argument:</label>"
									+ "<div class='radio'><input type='radio' class='radio' name='form-radio-argument' id='Pro-argument' value='Pro-argument' checked='checked'>"
									+ "<label for='Pro'>Pro-argument</label></div>"
									+ "<div class='radio'><input type='radio' class='radio' name='form-radio-argument' id='Con-argument' value='Con-argument'>"
									+ "<label for='Contra'>Con-argument</label></div>";
							argumentFieldGroup.insertAdjacentHTML("afterBegin", insertString);
						}
					});
}

var contextMenuEditAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : editKnowledgeElementText,
	"name" : editKnowledgeElementText,
	"action" : function(position) {
		var id = getSelectedTreeViewerNodeId(position);
		setUpDialogForEditAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpDialogForEditAction(id);
	}
};

function setUpDialogForEditAction(id, type) {
	console.log("view.context.menu.js setUpDialogForEditAction");
	conDecAPI.getDecisionKnowledgeElement(id, function(decisionKnowledgeElement) {
		var summary = decisionKnowledgeElement.summary;
		var description = decisionKnowledgeElement.description;
		var type = decisionKnowledgeElement.type;

		conDecAPI.isIssueStrategy(id, function(isIssueStrategy) { // TODO:
			// refactor
			// param
			// name,
			// confusing!
			if (isIssueStrategy) {
				var createEditIssueForm = require('quick-edit/form/factory/edit-issue');
				createEditIssueForm({
					issueId : id
				}).asDialog({
					windowTitle : editKnowledgeElementText
				}).show();
				AJS.dialog2("#dialog").hide();
			} else {
				setUpDialog();
				setHeaderText(editKnowledgeElementText);
				setUpCreateOrEditDialog(summary, description, type);

				var submitButton = document.getElementById("dialog-submit-button");
				submitButton.textContent = editKnowledgeElementText;
				submitButton.onclick = function() {
					var summary = document.getElementById("form-input-summary").value;
					var description = document.getElementById("form-input-description").value;
					var type = $("select[name='form-select-type']").val();
					updateDecisionKnowledgeElementAsChild(id, summary, description, type);
					AJS.dialog2("#dialog").hide();
				};
			}
		});
	});
}

var contextMenuDeleteAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : deleteKnowledgeElementText,
	"name" : deleteKnowledgeElementText,
	"action" : function(position) {
		var id = getSelectedTreeViewerNodeId(position);
		setUpDialogForDeleteAction(id);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setUpDialogForDeleteAction(id);
	}
};

function setUpDialogForDeleteAction(id) {
	console.log("view.context.menu.js setUpDialogForDeleteAction");
	setUpDialog();
	setHeaderText(deleteKnowledgeElementText);

	var content = document.getElementById("dialog-content");
	content.textContent = "Do you really want to delete this element?";

	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = deleteKnowledgeElementText;
	submitButton.onclick = function() {
		conDecAPI.deleteDecisionKnowledgeElement(id, function() {
			notify();
		});
		AJS.dialog2("#dialog").hide();
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
		setUpDialogForDeleteLinkAction(id, parentId);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		var parentId = findParentId(id);
		setUpDialogForDeleteLinkAction(id, parentId);
	}
};

function setUpDialogForDeleteLinkAction(id, parentId) {
	console.log("view.context.menu.js setUpDialogForDeleteLinkAction");
	setUpDialog();
	setHeaderText(deleteLinkToParentText);

	var content = document.getElementById("dialog-content");
	content.textContent = "Do you really want to delete the link to the parent element?";

	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = deleteLinkToParentText;
	submitButton.onclick = function() {
		conDecAPI.deleteLink(parentId, id, function() {
			notify();
		});
		AJS.dialog2("#dialog").hide();
	};
}

var contextMenuSetAsRootAction = {
	// label for Tree Viewer, name for Treant context menu
	"name" : makeRootText,
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setAsRootElement(id);
	}
};

var contextMenuOpenJiraIssueAction = {
	// label for Tree Viewer, name for Treant context menu
	"name" : openIssueText,
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		openIssue(id);
	}
};

/* used by view.tree.viewer.js */
var contextMenuActions = {
	"create" : contextMenuCreateAction,
	"edit" : contextMenuEditAction,
	"link" : contextMenuLinkAction,
	"deleteLink" : contextMenuDeleteLinkAction,
	"delete" : contextMenuDeleteAction
};

function resetDialog() {
	console.log("view.context.menu.js resetDialog");
	document.getElementById("dialog-header").innerHTML = "";
	document.getElementById("dialog-content").innerHTML = "";
	if (document.getElementById("dialog-extension-button")) {
		document.getElementById("dialog-extension-button").style.visibility = "hidden";
	}
	var dialog = document.getElementById("dialog");
	if (dialog.classList.contains("aui-dialog2-large")) {
		dialog.classList.remove("aui-dialog2-large");
	}
	if (!dialog.classList.contains("aui-dialog2-medium")) {
		dialog.classList.add("aui-dialog2-medium");
	}
}

var changeKnowledgeTypeAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : "Change Knowledge Type",
	"name" : "Change Knowledge Type",
	"submenu" : {
		'Issue' : {
			'label' : 'Issue',
			"action" : function(position) {
				changeKtTo(getSelectedTreeViewerNodeId(position), position, "Issue");
			},
		},
		'Decision' : {
			'label' : 'Decision',
			"action" : function(position) {
				changeKtTo(getSelectedTreeViewerNodeId(position), position, "Decision");
			},
		},
		'Alternative' : {
			'label' : 'Alternative',
			"action" : function(position) {
				changeKtTo(getSelectedTreeViewerNodeId(position), position, "Alternative");
			},
		},
		'Pro' : {
			'label' : 'Pro',
			"action" : function(position) {
				changeKtTo(getSelectedTreeViewerNodeId(position), position, "Pro");
			},
		},
		'Con' : {
			'label' : 'Con',
			"action" : function(position) {
				changeKtTo(getSelectedTreeViewerNodeId(position), position, "Con");
			},
		},

	}
};

/* TODO: refactor name. "changeKnowledgeTypetTo" ? */
function changeKtTo(id, position, type) {
	conDecAPI.changeKnowledgeTypeOfSentence(id, type, function() {
		if (document.getElementById("Relevant") !== null) {
			resetTreeViewer();
			conDecIssueTab.buildTreeViewer(document.getElementById("Relevant").checked);
			var idOfUiElement = "ui" + id;
			replaceTagsFromContent(idOfUiElement, type);
			document.getElementById(idOfUiElement).classList.remove("Decision", "Issue", "Alternative", "Pro", "Con",
					"isNotRelevant");
			document.getElementById(idOfUiElement).classList.add(type);
		} else {
			updateIssueModuleView();
		}
	});
}

// TODO: replace with shorter arrow function?
function getArrayId(array, id) {
	for (var i = array.length - 1; i >= 0; i--) {
		if (array[i].id === id) {
			return i;
		}
	}
}

function getIconUrl(core, indexOfNode, type) {
	var url = core.data[indexOfNode].icon;
	if (type.includes("Pro")) {
		url = url.replace("Other", "argument_pro");
	}
	if (type.includes("Con")) {
		url = url.replace("Other", "argument_con");
	}
	return url;
}

// local usage only
function replaceTagsFromContent(idOfUiElement, type) {
	if (!type.toLowerCase().includes("other") && type.length > 1) {
		document.getElementById(idOfUiElement).getElementsByClassName("tag")[0].textContent = "[" + type + "]";
		document.getElementById(idOfUiElement).getElementsByClassName("tag")[1].textContent = "[/" + type + "]";
	} else {
		document.getElementById(idOfUiElement).getElementsByClassName("tag")[0].textContent = "";
		document.getElementById(idOfUiElement).getElementsByClassName("tag")[1].textContent = "";
	}
}

// local usage only
var contextMenuDeleteSentenceLinkAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : "Delete link to parent",
	"name" : "Delete link to parent",
	"action" : function(position) {
		var node = getSelectedTreeViewerNode(position);
		var issueId = AJS.$("meta[name='ajs-issue-key']").attr("content");
		if (issueId === undefined) {
			issueId = getIssueKey();
		}

		var id = node.id;
		var parentId = node.parent;

		var nodeType = (node.li_attr['class'] === "sentence") ? "s" : "i";

		conDecAPI.deleteGenericLink(parentId, node.id, "i", nodeType, conDecAPI.linkGenericElements(JIRA.Issue
				.getIssueId(), node.id, "i", nodeType, refreshTreeViewer), false);
		conDecAPI.deleteGenericLink(parentId, node.id, "s", nodeType, conDecAPI.linkGenericElements(JIRA.Issue
				.getIssueId(), node.id, "i", nodeType, refreshTreeViewer), false);

	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		var parentId = findParentId(id);

		var parentClass = (document.getElementById(parentId).className.includes("sentence")) ? "s" : "i";
		var nodeClass = (document.getElementById(id).className.includes("sentence")) ? "s" : "i";

		conDecAPI.deleteGenericLink(parentId, id, parentClass, nodeClass, conDecAPI.linkGenericElements(JIRA.Issue
				.getIssueId(), id, "i", nodeClass, notify), false);

	}
};

// local usage only
var contextMenuDeleteSentenceAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : "Irrelevant",
	"name" : "Irrelevant",
	"action" : function(position) {
		var node = getSelectedTreeViewerNode(position);
		var id = node.id;
		conDecAPI.setSentenceIrrelevant(id, function(core, node) {
			jQueryConDec("#jstree").jstree(true).set_icon(jQueryConDec("#jstree").jstree(true).get_node(id),
					"https://player.fm/static/images/128pixel.png");
			if (!(document.getElementById("Relevant") == null)) {
				document.getElementById("ui" + id).getElementsByClassName("tag")[0].textContent = "";
				document.getElementById("ui" + id).getElementsByClassName("tag")[1].textContent = "";
				document.getElementById("ui" + id).classList.remove("Decision", "Issue", "Alternative", "Pro", "Con");
			} else {
				refreshTreeViewer();
			}
		});
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		conDecAPI.setSentenceIrrelevant(id, function(core, options, id) {
			notify();
		});
	}
};

// local usage only
var contextMenuEditSentenceAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : "Edit Sentence",
	"name" : "Edit Sentence",
	"action" : function(position) {
		var id = getSelectedTreeViewerNodeId(position);
		var node = getSelectedTreeViewerNode(position);
		setUpDialogForEditSentenceAction(id, node.data.summary, node.data.type);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		var nodes = document.getElementById("treant-container").getElementsByClassName("sentence");
		var node = getNodeWithId(nodes, id);
		var description = node.getElementsByClassName("node-title")[0].innerHTML;

		var type = "Other";
		if (node.getElementsByClassName("node-name").length > 0) {
			type = node.getElementsByClassName("node-name")[0].innerHTML;
		}
		setUpDialogForEditSentenceAction(id, description, type);
	}
};

// local usage only
/**
 * returns a node with given id from a node list
 */
function getNodeWithId(nodes, id) {
	for (var i = nodes.length - 1; i >= 0; i--) {
		if (nodes[i].id === id) {
			return nodes[i];
		}
	}
}

/**
 * fills HTML of dialog with contents
 */
function setUpDialogForEditSentenceAction(id, description, type) {
	setUpDialog();
	setHeaderText(editKnowledgeElementText);
	setUpEditSentenceDialogView(description, type);
	setUpEditSentenceDialogContext(id, description, type);
}

/**
 * fills HTML view-protion of dialog with contents
 */
function setUpEditSentenceDialogView(description, type) {

	document.getElementById("dialog-content").innerHTML = "";
	document.getElementById("dialog").classList.remove("aui-dialog2-large");
	document.getElementById("dialog").classList.add("aui-dialog2-medium");
	document.getElementById("dialog").style.zIndex = 9999;
	document.getElementById("dialog-content").insertAdjacentHTML(
			"afterBegin",
			"<form class='aui'>" + "<div class='field-group'><label for='form-input-description'>Sentence:</label>"
					+ "<textarea id='form-input-description' placeholder='Description' value='" + description
					+ "' class='textarea full-width-field'>" + description + "</textarea></div>"
					+ "<div class='field-group'><label for='form-select-type'>Knowledge type:</label>"
					+ "<select id='form-select-type' name='form-select-type' class='select full-width-field'/></div>"
					+ "</form>");

	for (var index = 0; index < extendedKnowledgeTypes.length; index++) {
		var isSelected = "";
		if (type.toLowerCase() === extendedKnowledgeTypes[index].toLowerCase()) {
			isSelected = "selected ";
		}
		if (type.toLowerCase() === "argument" && extendedKnowledgeTypes[index].toLowerCase().includes("pro")) {
			isSelected = "selected ";
		}
		$("select[name='form-select-type']")[0].insertAdjacentHTML("beforeend", "<option " + isSelected + "value='"
				+ extendedKnowledgeTypes[index] + "'>" + extendedKnowledgeTypes[index] + "</option>");
	}
}

/**
 * sets-up submit button
 */
function setUpEditSentenceDialogContext(id, description, type) {

	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = "Change";
	$("#dialog-extension-button").remove();
	submitButton.onclick = function() {
		var description = document.getElementById("form-input-description").value;
		var type = $("select[name='form-select-type']").val().split("-")[0];
		conDecAPI
				.editSentenceBody(
						id,
						description,
						type,
						function() {
							if (!(document.getElementById("Relevant") == null)) {
								var idOfUiElement = "ui" + id;
								replaceTagsFromContent(idOfUiElement, type);

								document.getElementById(idOfUiElement).classList.remove("Decision", "Issue",
										"Alternative", "Pro", "Con");
								document.getElementById(idOfUiElement).classList.add(type);
								document.getElementById(idOfUiElement).getElementsByClassName("sentenceBody")[0].textContent = description;
								AJS.dialog2("#dialog").hide();
							} else {
								AJS.dialog2("#dialog").hide();
								notify();
							}

						});
	};
	AJS.$("#form-select-type").auiSelect2();
}

/**
 * local resets tree viewer and builds it again
 */
function refreshTreeViewer() {
	console.log("view.context.menu.js refreshTreeViewer");
	if (document.getElementById("Relevant") !== null) {
		resetTreeViewer();
		conDecIssueTab.buildTreeViewer(document.getElementById("Relevant").checked);
	} else {
		AJS.dialog2("#dialog").hide();
		notify();
	}
}

var contextMenuActionsForSentences = {
	"edit" : contextMenuEditSentenceAction,
	"deleteLink" : contextMenuDeleteSentenceLinkAction,
	"delete" : contextMenuDeleteSentenceAction,
	"changeKt" : changeKnowledgeTypeAction
};

var contextMenuActionsForSentencesInTreant = {
	"edit" : contextMenuEditSentenceAction,
	"deleteLink" : contextMenuDeleteSentenceLinkAction,
	"delete" : contextMenuDeleteSentenceAction
};
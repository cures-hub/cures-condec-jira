/*
 * This module is responsible for showing a context menu upon right mouse click.
 * 
 */
(function(global) {

	var isContextMenuOpen;

	var ConDecContextMenu = function ConDecContextMenu() {
	};

	ConDecContextMenu.prototype.setUpContext = function setUpContext() {
		$(document).ready(function() {
			var treantNodes = document.getElementsByClassName("node");
			var i;
			for (i = 0; i < treantNodes.length; i++) {
				treantNodes[i].addEventListener('contextmenu', function(event) {
					event.preventDefault();
					// TODO Find correct position in issue module
					var left = event.pageX;
					var top = event.pageY;
					console.log(left);
					console.log(top);

					console.log(this.id);
					createContextMenu(left, top, this.id);
				});
			}

			var jsTreeNodes = document.getElementsByClassName("jstree-node");
			var j;
			for (j = 0; j < jsTreeNodes.length; j++) {
				jsTreeNodes[j].addEventListener('contextmenu', function(event) {
					event.preventDefault();
					var left = event.pageX;
					var top = event.pageY;
					console.log(left);
					console.log(top);

					console.log(this.id);
					createContextMenu(left, top, this.id);
				});
			}

			function hideContextMenu() {
				if (isContextMenuOpen) {
					console.log("contextmenu closed");
					document.querySelector("#condec-context-menu").setAttribute('aria-hidden', 'true');
				}
				isContextMenuOpen = false;
			}
			$(global).blur(hideContextMenu);
			$(document).click(hideContextMenu);
		});
	};

	function createContextMenu(posX, posY, id) {
		isContextMenuOpen = true;
		console.log("contextmenu opened");

		$("#condec-context-menu").css({
			left : posX,
			top : posY
		});
		document.querySelector("#condec-context-menu").setAttribute('aria-hidden', 'false');

		document.getElementById("condec-context-menu-create-item").onclick = function() {
			conDecDialog.showCreateDialog(id);
		};
		document.getElementById("condec-context-menu-edit-item").onclick = function() {
			conDecDialog.showEditDialog(id);
		};
		document.getElementById("condec-context-menu-change-type-item").onclick = function() {
			conDecDialog.showChangeTypeDialog(id);
		};
		document.getElementById("condec-context-menu-link-item").onclick = function() {
			conDecDialog.showLinkDialog(id);
		};
		document.getElementById("condec-context-menu-delete-link-item").onclick = function() {
			var parentId = findParentId(id);
			conDecDialog.showDeleteLinkDialog(id, parentId);
		};
		document.getElementById("condec-context-menu-delete-item").onclick = function() {
			conDecDialog.showDeleteDialog(id);
		};

		document.getElementById("condec-context-menu-set-root-item").onclick = function() {
			if (window.conDecIssueModule !== undefined) {
				window.conDecIssueModule.setAsRootElement(id);
			} else if (window.conDecKnowledgePage !== undefined) {
				window.conDecKnowledgePage.setAsRootElement(id);
			}
		};

		document.getElementById("condec-context-menu-open-jira-issue-item").onclick = function() {
			if (window.conDecKnowledgePage !== undefined) {
				window.conDecKnowledgePage.openIssue(id);
			}
		};
	}

	// export ConDecContext
	global.conDecContextMenu = new ConDecContextMenu();
})(window);

function getSelectedTreeViewerNode(position) {
	console.log("view.context.menu.js getSelectedTreeViewerNode");
	var selector = position.reference.prevObject.selector;
	return jQueryConDec("#jstree").jstree(true).get_node(selector);
}

function getSelectedTreeViewerNodeId(node) {
	console.log("view.context.menu.js getSelectedTreeViewerNodeId");
	console.log(getSelectedTreeViewerNode(node).data.id);
	return getSelectedTreeViewerNode(node).data.id;
}

function getSelectedTreantNodeId(options) {
	console.log("view.context.menu.js getSelectedTreantNodeId");
	var context = options.$trigger.context;
	console.log(context.id);
	return context.id;
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

		conDecAPI.deleteGenericLink(parentId, node.id, "i", nodeType, conDecAPI.setSentenceIrrelevant(node.id), false);
		conDecAPI.deleteGenericLink(parentId, node.id, "s", nodeType, conDecAPI.setSentenceIrrelevant(node.id), false);

	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		var parentId = findParentId(id);

		var parentClass = (document.getElementById(parentId).className.includes("sentence")) ? "s" : "i";
		var nodeClass = (document.getElementById(id).className.includes("sentence")) ? "s" : "i";

		conDecAPI.deleteGenericLink(parentId, id, parentClass, nodeClass, conDecAPI.setSentenceIrrelevant(id,
				conDecObservable.notify), false);

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
			conDecObservable.notify();
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
		console.log(node);
		conDecDialog.setUpDialogForEditSentenceAction(id, node.data.summary, node.data.type, node.data.type);
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
		conDecDialog.setUpDialogForEditSentenceAction(id, description, type, node.classList.value);
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

// local usage only
var contextMenuCreateIssueFromSentence = {
	// label for Tree Viewer, name for Treant context menu
	"label" : "Convert to JIRA Issue",
	"name" : "Convert to JIRA Issue",
	"action" : function(position) {
		var id = getSelectedTreeViewerNodeId(position);
		console.log(id);
		conDecAPI.createIssueFromSentence(id, conDecObservable.notify);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		console.log(id);
		conDecAPI.createIssueFromSentence(id, conDecObservable.notify);
	}
};

var contextMenuActionsForSentences = {
	"edit" : contextMenuEditSentenceAction,
	"deleteLink" : contextMenuDeleteSentenceLinkAction,
	"delete" : contextMenuDeleteSentenceAction,
	"createIssue" : contextMenuCreateIssueFromSentence,
	"changeKt" : changeKnowledgeTypeAction
};

var contextMenuActionsForSentencesInTreant = {
	"edit" : contextMenuEditSentenceAction,
	"deleteLink" : contextMenuDeleteSentenceLinkAction,
	"createIssue" : contextMenuCreateIssueFromSentence,
	"delete" : contextMenuDeleteSentenceAction
};
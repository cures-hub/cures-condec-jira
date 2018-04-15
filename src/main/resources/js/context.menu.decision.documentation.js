var createKnowledgeElementText = "Add Decision Component";
var editKnowledgeElementText = "Edit Decision Component";
var deleteKnowledgeElementText = "Delete Decision Component";

function setHeaderText(headerText) {
	var header = document.getElementById("context-menu-header");
	header.textContent = headerText;
}

function setUpContextMenuContentForCreateAction() {
	setUpModal();
	setHeaderText(createKnowledgeElementText);
	var content = document.getElementById('modal-content');
	content
			.insertAdjacentHTML(
					'afterBegin',
					'<p><label for="form-input-name" style="display:block;width:45%;float:left;">Name</label>'
							+ '<input id="form-input-name" type="text" name="summary" placeholder="Name of decision component" style="width:50%;"/></p>'
							+ '<p><label for="form-select-type" style="display:block;width:45%;float:left;">Knowledge type</label>'
							+ '<select name="form-select-type" style="width:50%;"/></p>'
							+ '<p><input id="form-input-submit" type="submit" value="Add Decision Component" style="float:right;"/></p>');

	var type_select = $('select[name="form-select-type"]');
	type_select
			.on(
					'change',
					function() {
						var type = type_select.val();
						if (type === 'Argument') {
							type_select
									.insertAdjacentHTML(
											'afterEnd',
											'<p id="type-of-argument-para"><label for="type-of-argument" style="display:block;width:45%;float:left;">Type of Argument</label><input type="radio" name="type-of-argument" value="pro" checked="checked">Pro<input type="radio" name="type-of-argument" value="contra">Contra<input type="radio" name="type-of-argument" value="comment">Comment</p>');
						} else {
							var para = document.getElementById("type-of-argument-para");
							if (para) {
								clearInner(para);
								para.parentNode.removeChild(para);
							}
						}
					});
	return type_select;
}

var contextMenuCreateAction = {
	// label is used in Tree Viewer context menu
	"label" : createKnowledgeElementText,
	// name is used in Treant context menu
	"name" : createKnowledgeElementText,
	// action is used in Tree Viewer context menu
	"action" : function(node) {
		// setUpContextMenuContentForCreateAction();
		// var selector = node.reference.prevObject.selector;
		// var tree_node = $('#evts').jstree(true).get_node(selector).data;
		//
		// var content = document.getElementById('modal-content');
		// content
		// .insertAdjacentHTML(
		// 'afterBegin',
		// '<p><label for="form-input-name"
		// style="display:block;width:45%;float:left;">Name</label>'
		// + '<input id="form-input-name" type="text" name="summary"
		// placeholder="Name of decision component" style="width:50%;"/></p>'
		// + '<p><label for="form-select-type"
		// style="display:block;width:45%;float:left;">Knowledge type</label>'
		// + '<select name="form-select-type" style="width:50%;"/></p>'
		// + '<p><input id="form-input-submit" type="submit" value="Add Decision
		// Component" style="float:right;"/></p>');
		//
		// var type_select = $('select[name="form-select-type"]');
		// type_select
		// .on(
		// 'change',
		// function() {
		// var type = type_select.val();
		// if (type === 'Argument') {
		// type_select
		// .insertAdjacentHTML(
		// 'afterEnd',
		// '<p id="type-of-argument-para"><label for="type-of-argument"
		// style="display:block;width:45%;float:left;">Type of Argument</label>'
		// + '<input type="radio" name="type-of-argument" value="pro"
		// checked="checked">Pro<input type="radio" name="type-of-argument"
		// value="contra">Contra<input type="radio" name="type-of-argument"
		// value="comment">Comment</p>');
		// } else {
		// var para = document.getElementById("type-of-argument-para");
		// if (para) {
		// clearInner(para);
		// para.parentNode.removeChild(para);
		// }
		// }
		// });
		//
		// for (var index = 0; index < knowledgeTypes.length; index++) {
		// type_select[0].insertAdjacentHTML('beforeend', '<option value="' +
		// knowledgeTypes[index] + '">'
		// + knowledgeTypes[index] + '</option>');
		// }
		//
		// var submitButton = document.getElementById('form-input-submit');
		// submitButton.onclick = function() {
		// var summary = document.getElementById('form-input-name').value;
		// var type = type_select.val();
		// if (type === "Argument") {
		// var argumentCheckBoxGroup =
		// document.getElementsByName("type-of-argument");
		// for (var i = 0; i < argumentCheckBoxGroup.length; i++) {
		// if (argumentCheckBoxGroup[i].checked === true) {
		// var selectedNatureOfArgument = argumentCheckBoxGroup[i].value;
		// if (selectedNatureOfArgument === "pro") {
		// createDecisionKnowledgeElement(summary, type, function(newId) {
		// createLink(id, newId, "support", function() {
		// buildTreeViewer(projectKey, newId);
		// });
		// });
		// } else if (selectedNatureOfArgument === "contra") {
		// createDecisionKnowledgeElement(summary, type, function(newId) {
		// createLink(id, newId, "attack", function() {
		// buildTreeViewer(projectKey, newId);
		// });
		// });
		// } else if (selectedNatureOfArgument === "comment") {
		// createDecisionKnowledgeElement(summary, type, function(newId) {
		// createLink(id, idOfNewObject, "comment", function() {
		// buildTreeViewer(projectKey, newId);
		// });
		// });
		// }
		// }
		// }
		// } else {
		// createDecisionKnowledgeElement(summary, type, function(newId) {
		// createLink(id, newId, "contain", function() {
		// buildTreeViewer(projectKey, newId);
		// });
		// });
		// }
		// closeModal();
		// };
		//
		// var modal = document.getElementById('ContextMenuModal');
		// modal.style.display = "block";
	},
	// callback is used in Treant context menu
	"callback" : function(key, options) {
		// setUpContextMenuContentForCreateAction();
		//
		// var content = document.getElementById('modal-content');
		// content
		// .insertAdjacentHTML(
		// 'afterBegin',
		// '<p><label for="form-input-name"
		// style="display:block;width:45%;float:left;">Name</label>'
		// + '<input id="form-input-name" type="text" name="summary"
		// placeholder="Name of decision component" style="width:50%;"/></p>'
		// + '<p><label for="form-select-type"
		// style="display:block;width:45%;float:left;">Knowledge type</label>'
		// + '<select name="form-select-type" style="width:50%;"/></p>'
		// + '<p><input id="form-input-submit" type="submit" value="Add Decision
		// Component" style="float:right;"/></p>');
		//
		// var type_select = $('select[name="form-select-type"]');
		// type_select
		// .on(
		// 'change',
		// function() {
		// var type = type_select.val();
		// if (type === 'Argument') {
		// type_select
		// .insertAdjacentHTML(
		// 'afterEnd',
		// '<p id="type-of-argument-para"><label for="type-of-argument"
		// style="display:block;width:45%;float:left;">Type of Argument</label>'
		// + '<input type="radio" name="type-of-argument" value="pro"
		// checked="checked">Pro<input type="radio" name="type-of-argument"
		// value="contra">Contra<input type="radio" name="type-of-argument"
		// value="comment">Comment</p>');
		// } else {
		// var para = document.getElementById("type-of-argument-para");
		// if (para) {
		// clearInner(para);
		// para.parentNode.removeChild(para);
		// }
		// }
		// });
		//
		// for (var index = 0; index < knowledgeTypes.length; index++) {
		// type_select[0].insertAdjacentHTML('beforeend', '<option value="' +
		// knowledgeTypes[index] + '">'
		// + knowledgeTypes[index] + '</option>');
		// }
		//
		// var submitButton = document.getElementById('form-input-submit');
		// setSubmitFunction(submitButton, type_select, projectKey,
		// options.$trigger.context.id);
		//
		// var modal = document.getElementById('ContextMenuModal');
		// modal.style.display = "block";
	}
}

function setUpContextMenuContentForEditAction() {
	setUpModal();
	setHeaderText(editKnowledgeElementText);

	var content = document.getElementById('modal-content');
	content
			.insertAdjacentHTML(
					'afterBegin',
					'<p><label for="form-input-name" style="display:block;width:45%;float:left;">Summary:</label>'
							+ '<input id="form-input-name" type="text" name="summary" value="'
							+ tree_node.summary
							+ '" style="width:50%;" readonly/></p>'
							+ '<p><label for="form-input-description" style="display:block;width:45%;float:left;">Description:</label>'
							+ '<input id="form-input-description" type="text" name="type" placeholder="Description" style="width:50%;"/></p>'
							+ '<p><input id="form-input-submit" type="submit" value=' + editKnowledgeElementText
							+ ' style="float:right;"/></p>');
}

var contextMenuEditAction = {
	// label is used in Tree Viewer context menu
	"label" : editKnowledgeElementText,
	// name is used in Treant context menu
	"name" : editKnowledgeElementText,
	// action is used in Tree Viewer context menu
	"action" : function(node) {
		setUpContextMenuContentForEditAction();

		var selector = node.reference.prevObject.selector;
		var tree_node = $('#evts').jstree(true).get_node(selector).data;

		var submitButton = document.getElementById('form-input-submit');
		submitButton.onclick = function() {
			var summary = document.getElementById('form-input-name').value;
			var description = document.getElementById('form-input-description').value;
			editDecisionKnowledgeElement(tree_node.id, summary, description, function() {
				buildTreeViewer(projectKey, tree_node.id);
			});
			closeModal();
		};
	},
	// callback is used in Treant context menu
	"callback" : function(key, options) {
		setUpContextMenuContentForEditAction();

		var context = options.$trigger.context;
		var children = context.childNodes;
		for (var index = 0; index < children.length; index++) {
			if (children.hasOwnProperty(index)) {
				if (index === 0) {
					document.getElementById('form-input-name').value = children[index].innerText;
				} else if (index == 1) {
					// title, not needed right now
				} else if (index == 2) {
					// description, not needed right now
				} else {
					// not implemented, not needed right now
				}
			}
		}

		var submitButton = document.getElementById('form-input-submit');
		submitButton.onclick = function() {
			var summary = document.getElementById('form-input-name').value;
			var description = document.getElementById('form-input-description').value;
			editDecisionKnowledgeElement(context.id, summary, description, function() {
				var nodeId = $.jstree.reference('#evts').get_selected()[0];
				buildTreeViewer(projectKey, nodeId);
			});
			closeModal();
		};
	}
}

function setUpContextMenuContentForDeleteAction() {
	setUpModal();
	setHeaderText(deleteKnowledgeElementText);

	var content = document.getElementById('modal-content');
	content.insertAdjacentHTML('afterBegin',
			'<p><input id="abort-submit" type="submit" value="Abort Deletion" style="float:right;"/>'
					+ '<input id="form-input-submit" type="submit" value=' + deleteKnowledgeElementText
					+ ' style="float:right;"/></p>');

	var abortButton = document.getElementById('abort-submit');
	abortButton.onclick = function() {
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
		setUpContextMenuContentForDeleteAction();

		// gets the selected Tree Viewer node
		var selector = node.reference.prevObject.selector;
		var tree_node = $('#evts').jstree(true).get_node(selector).data;

		var submitButton = document.getElementById('form-input-submit');
		submitButton.onclick = function() {
			deleteDecisionKnowledgeElement(tree_node.id, function() {
				buildTreeViewer(projectKey, tree_node.id);
			});
			closeModal();
		};
	},
	// callback is used in Treant context menu
	"callback" : function(key, options) {
		setUpContextMenuContentForDeleteAction();

		// gets the selected Treant node
		var context = options.$trigger.context;

		var submitButton = document.getElementById('form-input-submit');
		submitButton.onclick = function() {
			deleteDecisionKnowledgeElement(context.id, function() {
				var nodeId = $.jstree.reference('#evts').get_selected()[0];
				buildTreeViewer(projectKey, nodeId);
			});
			closeModal();
		};
	}
}

var contextMenuActions = {
	"create" : contextMenuCreateAction,
	"edit" : contextMenuEditAction,
	"delete" : contextMenuDeleteAction,
}

function setUpModal() {
	var modal = document.getElementById("ContextMenuModal");
	modal.style.display = "block";
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
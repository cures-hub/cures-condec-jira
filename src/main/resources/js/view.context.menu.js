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
	var selector = position.reference.prevObject.selector;
	return $("#jstree").jstree(true).get_node(selector);
}

function getSelectedTreeViewerNodeId(node) {
	return getSelectedTreeViewerNode(node).data.id;
}

function getSelectedTreantNodeId(options) {
	var context = options.$trigger.context;
	return context.id;
}

function setUpDialogForCreateAction(id) {
	setUpDialog();
	setHeaderText(createKnowledgeElementText);
	setUpCreateOrEditDialog("", "", "Alternative");

	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = createKnowledgeElementText;
	submitButton.onclick = function() {
		var summary = document.getElementById("form-input-summary").value;
		var description = document.getElementById("form-input-description").value;
		var type = $("select[name='form-select-type']").val();
		createDecisionKnowledgeElementAsChild(summary, description, type, id);
		closeDialog();
	};

	isIssueStrategy(id, function(isIssueStrategy) {
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
				closeDialog();
			};
		}
	});
}

function setUpDialog() {
	resetDialog();
	AJS.dialog2("#dialog").show();
	AJS.dialog2("#dialog").on("show", function() {
		resetDialog();
	});
	AJS.$(document).on("click", "#dialog-cancel-button", function(e) {
		e.preventDefault();
		AJS.dialog2("#dialog").hide();
	});
}

function setHeaderText(headerText) {
	var header = document.getElementById("dialog-header");
	header.textContent = headerText;
}

function setUpCreateOrEditDialog(summary, description, knowledgeType) {
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
	setUpDialog();
	setHeaderText(linkKnowledgeElementText);

	getUnlinkedElements(id, function(unlinkedElements) {
		var insertString = "<form class='aui'><div class='field-group' id='select-field-group'></div>"
				+ "<div class='field-group' id='argument-field-group'></div></form>";
		var content = document.getElementById("dialog-content");
		content.insertAdjacentHTML("afterBegin", insertString);

		insertString = "<label for='form-select-component'>Unlinked Element:</label>"
				+ "<select id='form-select-component' name='form-select-component' "
				+ "onchange='addFormForArguments()' class='select full-width-field'/>";
		for (var index = 0; index < unlinkedElements.length; index++) {
			insertString += "<option value='" + unlinkedElements[index].id + "'>"
					+ unlinkedElements[index].type + ' / ' + unlinkedElements[index].summary
					+ "</option>";
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
			closeDialog();
		};
	});
}

function addFormForArguments() {
	var childId = $("select[name='form-select-component']").val();
	var argumentFieldGroup = document.getElementById("argument-field-group");
	argumentFieldGroup.innerHTML = "";
	getDecisionKnowledgeElement(
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
	setUpDialog();
	setHeaderText(editKnowledgeElementText);
	getDecisionKnowledgeElement(id, function(decisionKnowledgeElement) {
		var summary = decisionKnowledgeElement.summary;
		var description = decisionKnowledgeElement.description;
		var type = decisionKnowledgeElement.type;
		setUpCreateOrEditDialog(summary, description, type);

		var submitButton = document.getElementById("dialog-submit-button");
		submitButton.textContent = editKnowledgeElementText;
		submitButton.onclick = function() {
			var summary = document.getElementById("form-input-summary").value;
			var description = document.getElementById("form-input-description").value;
			var type = $("select[name='form-select-type']").val();
			updateDecisionKnowledgeElementAsChild(id, summary, description, type);
			closeDialog();
		};

		isIssueStrategy(id, function(isIssueStrategy) {
			if (isIssueStrategy === true) {
				var extensionButton = document.getElementById("dialog-extension-button");
				extensionButton.style.visibility = "visible";
				extensionButton.onclick = function() {
					var createEditIssueForm = require('quick-edit/form/factory/edit-issue');
					createEditIssueForm({
						issueId : id
					}).asDialog({
						windowTitle : editKnowledgeElementText
					}).show();
					closeDialog();
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
	setUpDialog();
	setHeaderText(deleteKnowledgeElementText);

	var content = document.getElementById("dialog-content");
	content.textContent = "Do you really want to delete this element?";

	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = deleteKnowledgeElementText;
	submitButton.onclick = function() {
		deleteDecisionKnowledgeElement(id, function() {
			updateView();
		});
		closeDialog();
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
	setUpDialog();
	setHeaderText(deleteLinkToParentText);

	var content = document.getElementById("dialog-content");
	content.textContent = "Do you really want to delete the link to the parent element?";

	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = deleteLinkToParentText;
	submitButton.onclick = function() {
		deleteLink(parentId, id, function() {
			updateView();
		});
		closeDialog();
	};
}

var contextMenuActions = {
	"create" : contextMenuCreateAction,
	"edit" : contextMenuEditAction,
	"link" : contextMenuLinkAction,
	"deleteLink" : contextMenuDeleteLinkAction,
	"delete" : contextMenuDeleteAction
};

function closeDialog() {
	AJS.dialog2("#dialog").hide();
}

function resetDialog() {
	document.getElementById("dialog-header").innerHTML = "";
	document.getElementById("dialog-content").innerHTML = "";
	document.getElementById("dialog-extension-button").style.visibility = "hidden";
}


var contextMenuActionsForSentences = {
	"edit" : contextMenuEditSentenceAction,
	//"deleteLink" : contextMenuDeleteSentenceLinkAction,
	"delete" : contextMenuDeleteSentenceAction,
	"changeKt": changeKnowledgeTypeAction
};

var contextMenuActionsForSentencesInTreant = {
	"edit" : contextMenuEditSentenceAction,
	"delete" : contextMenuDeleteSentenceAction
};

var changeKnowledgeTypeAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : "Change Knowledge Type",
	"name" : "Change Knowledge Type",
	"submenu" :{
                'Issue': {
                    'label' : 'Issue',
                    "action" : function(position) {
						changeKtTo(getSelectedTreeViewerNodeId(position),position,"Issue");
					},
                },
                'Decision': {
                    'label' : 'Decision',
                    "action" : function(position) {
						changeKtTo(getSelectedTreeViewerNodeId(position),position,"Decision");
					},
                },
                'Alternative': {
                    'label' : 'Alternative',
                    "action" : function(position) {
						changeKtTo(getSelectedTreeViewerNodeId(position),position,"Alternative");
					},
                },
                'Pro': {
                    'label' : 'Pro',
                    "action" : function(position) {
						changeKtTo(getSelectedTreeViewerNodeId(position),position,"Pro");
					},
                },
                'Con': {
                    'label' : 'Con',
                    "action" : function(position) {
						changeKtTo(getSelectedTreeViewerNodeId(position),position,"Con");
					},
                },

            }
};

function changeKtTo(id,position,type){
	changeKnowledgeTypeOfSentence(id,type, function() {
		if(!(document.getElementById("Relevant") == null)){
			resetTreeViewer();
			buildTreeViewer2(document.getElementById("Relevant").checked);
			// getTreeViewerWithoutRootElement(document.getElementById("Relevant").checked, function(core) {
			// 	var indexOfNode = getArrayId(core.data,getSelectedTreeViewerNodeId(position));
			// 	var url = getIconUrl(core,indexOfNode,type);
			// 	 $("#jstree").jstree(true).set_icon(getSelectedTreeViewerNode(position),url);
			// });
			var idOfUiElement = "ui"+id;
			replaceTagsFromContent(idOfUiElement,type);
			document.getElementById(idOfUiElement).classList.remove("Decision","Issue","Alternative","Pro","Con");
			document.getElementById(idOfUiElement).classList.add(type);
		}else{
			updateView();
			
		}
	});
}

function getArrayId(array,id){
	for (var i = array.length - 1; i >= 0; i--) {
		if(array[i].id == id){
			return i;
		}
	}
}

function getIconUrl(core,indexOfNode,type){
	var url = core.data[indexOfNode].icon;
	if(type.includes("Pro")){
		url = url.replace("Other","argument_pro");
	}
	if(type.includes("Con")){
		url = url.replace("Other","argument_con");
	}
	return url;
}

function replaceTagsFromContent(idOfUiElement,type){
	document.getElementById(idOfUiElement).getElementsByClassName("tag")[0].textContent = "["+type+"]";
	document.getElementById(idOfUiElement).getElementsByClassName("tag")[1].textContent = "[/"+type+"]";

}


var contextMenuDeleteSentenceLinkAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : "Delete link to parent",
	"name" : "Delete link to parent",
	"action" : function(position) {
		var node = getSelectedTreeViewerNode(position);
		var id = node.id;
		var parentId = node.parent;

		var nodeType = (node.li_attr['class'] == "sentence") ? "s" : "i";
			
		deleteGenericLink(parentId,node.id,"i",nodeType,refreshTreeViewer,false);
		deleteGenericLink(parentId,node.id,"s",nodeType,refreshTreeViewer,false);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		var parentId = findParentId(id);
		deleteGenericLink(parentId, id,function(core){updateView();});
	}
};



var contextMenuDeleteSentenceAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : "Irrelevant",
	"name" : "Irrelevant",
	"action" : function(position) {
		var node = getSelectedTreeViewerNode(position);
		var id = node.id;
		setSentenceIrrelevant(id,function(core,node){
			$("#jstree").jstree(true).set_icon($('#jstree').jstree(true).get_node(id),"https://player.fm/static/images/128pixel.png");
			if(!(document.getElementById("Relevant") == null)){
				document.getElementById("ui"+id).getElementsByClassName("tag")[0].textContent="";
				document.getElementById("ui"+id).getElementsByClassName("tag")[1].textContent="";
				document.getElementById("ui"+id).classList.remove("Decision","Issue","Alternative","Pro","Con");
			}else{
				refreshTreeViewer();
			}
		})
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		setSentenceIrrelevant(id,function(core,options,id){
			refreshTreeViewer();
		})
	}
};

var contextMenuEditSentenceAction = {
	// label for Tree Viewer, name for Treant context menu
	"label" : editKnowledgeElementText,
	"name" : editKnowledgeElementText,
	"action" : function(position) {
		var id = getSelectedTreeViewerNodeId(position);
		var node = getSelectedTreeViewerNode(position);
		setUpDialogForEditSentenceAction(id,node.data.summary,node.data.type);
	},
	"callback" : function(key, options) {
		var id = getSelectedTreantNodeId(options);
		var nodes = document.getElementById("treant-container").getElementsByClassName("sentence");
		var node = getNodeWithId(nodes,id);
		var description = node.getElementsByClassName("node-title")[0].innerHTML;

		var	type = "Other";
		if(node.getElementsByClassName("node-name").length >0){
			type = node.getElementsByClassName("node-name")[0].innerHTML
		}
		setUpDialogForEditSentenceAction(id,description,type);
	}
};

function getNodeWithId(nodes,id) {
	for (var i = nodes.length - 1; i >= 0; i--) {
		if(nodes[i].id == id){
			return nodes[i]
		}
	}
}

function setUpDialogForEditSentenceAction(id,description,type) {
	setUpDialog();
	setHeaderText(editKnowledgeElementText);
	setUpEditSentenceDialogView(description,type);
	setUpEditSentenceDialog(id,description,type);
}


function setUpEditSentenceDialogView(description,type){
	document.getElementById("dialog").style.zIndex = 9999;
	document.getElementById("dialog-content").insertAdjacentHTML(
			"afterBegin",
			"<form class='aui'>"
					+ "<div class='field-group'><label for='form-input-description'>Sentence:</label>"
					+ "<textarea id='form-input-description' placeholder='Description' value='" + description
					+ "' class='textarea full-width-field'>" + description + "</textarea></div>"
					+ "<div class='field-group'><label for='form-select-type'>Knowledge type:</label>"
					+ "<select id='form-select-type' name='form-select-type' class='select full-width-field'/></div>"
					+ "</form>");

	var knowledgeTypesWithIrrelevant = Array.from(extendedKnowledgeTypes);
	if(!knowledgeTypesWithIrrelevant.includes("Other")){
		knowledgeTypesWithIrrelevant.push("Other");
	}
	
	for (var index = 0; index < knowledgeTypesWithIrrelevant.length; index++) {
		var isSelected = "";
		if (type.toLowerCase() === knowledgeTypesWithIrrelevant[index].toLowerCase()) {
			isSelected = "selected ";
		}
		$("select[name='form-select-type']")[0].insertAdjacentHTML("beforeend", "<option " + isSelected + "value='"
				+ knowledgeTypesWithIrrelevant[index] + "'>" + knowledgeTypesWithIrrelevant[index] + "</option>");
	}
}

function setUpEditSentenceDialog(id,description,type) {
	

	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = createKnowledgeElementText;
	submitButton.onclick = function() {
		var description = document.getElementById("form-input-description").value;
		var type = $("select[name='form-select-type']").val();
		editSentenceBody(id,description,type,function(){
			if(!(document.getElementById("Relevant") == null)){
				var idOfUiElement = "ui"+id;
				replaceTagsFromContent(idOfUiElement,type);

				document.getElementById(idOfUiElement).classList.remove("Decision","Issue","Alternative","Pro","Con");
				document.getElementById(idOfUiElement).classList.add(type);
				document.getElementById(idOfUiElement).getElementsByClassName("sentenceBody")[0].textContent=description;
				closeDialog();
			}else{
				closeDialog();
				updateView();
			}

			//callDialog2();
		});
	};
	AJS.$("#form-select-type").auiSelect2();
}

function refreshTreeViewer(){
	if(!(document.getElementById("Relevant") == null)){
		resetTreeViewer();
		buildTreeViewer2(document.getElementById("Relevant").checked);
	}else{
		closeDialog();
		updateView();
	}
}
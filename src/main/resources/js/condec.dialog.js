/*
 This module provides the dialogs for managing decision knowledge. The user can
 * create a new decision knowledge element,
 * edit an existing decision knowledge element,
 * delete an existing knowledge element,
 * create a new link between two knowledge elements,
 * delete a link between two knowledge elements,
 * change the documentation location (e.g. from issue comments to single JIRA issues),
 * set an element to the root element in the knowledge tree.
 
 Requires
 * conDecAPI
 
 Is required by
 * conDecContextMenu
 */
(function(global) {

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
	var changeKnowledgeTypeText = "Change Element Type";

	var ConDecDialog = function ConDecDialog() {
	};

	ConDecDialog.prototype.showCreateDialog = function showCreateDialog(idOfParentElement,
			documentationLocationOfParentElement) {
		console.log("view.context.menu.js setUpDialogForCreateAction");
		console.log(idOfParentElement);
		setHeaderText(createKnowledgeElementText);
		setUpCreateOrEditDialog("", "", "Alternative", true);

		var submitButton = document.getElementById("dialog-submit-button");
		submitButton.textContent = createKnowledgeElementText;
		submitButton.onclick = function() {
			var summary = document.getElementById("form-input-summary").value;
			var description = document.getElementById("form-input-description").value;
			var type = $("select[name='form-select-type']").val();
			var documentationLocation = $("select[name='form-select-location']").val();
			conDecAPI.createDecisionKnowledgeElement(summary, description, type, documentationLocation,
					idOfParentElement, documentationLocationOfParentElement, function() {
						conDecObservable.notify();
					});
			AJS.dialog2("#dialog").hide();
		};
		setUpDialog();
	};

	/*
	 * attaches cancel button handler shows(creates) the dialog TODO: attach
	 * should be moved out from this function TODO: rename to maybe showDialog()
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

	function setUpCreateOrEditDialog(summary, description, knowledgeType, addDocumentLocation) {
		console.log("view.context.menu.js setUpCreateOrEditDialog");
		var documentationLocation = "";
		if (addDocumentLocation) {
			documentationLocation = "<div class='field-group'><label for='form-select-location'>Documentation Location:</label>"
					+ "<select id='form-select-location' name='form-select-location' class='select full-width-field'>"
					+ "<option selected value = 'i'>JIRA Issue</option>"
					+ "<option value = 's'>JIRA Issue Comment</option></select></div>";
		}
		document
				.getElementById("dialog-content")
				.insertAdjacentHTML(
						"afterBegin",
						"<form class='aui'><div class='field-group'><label for='form-input-summary'>Summary:</label>"
								+ "<input id='form-input-summary' type='text' placeholder='Summary' value='"
								+ summary
								+ "' class='text full-width-field'/></div>"
								+ "<div class='field-group'><label for='form-input-description'>Description:</label>"
								+ "<textarea id='form-input-description' placeholder='Description' value='"
								+ description
								+ "' class='textarea full-width-field'>"
								+ description
								+ "</textarea></div>"
								+ documentationLocation
								+ "<div class='field-group'><label for='form-select-type'>Knowledge type:</label>"
								+ "<select id='form-select-type' name='form-select-type' class='select full-width-field'/></div>"
								+ "</form>");

		var extendedKnowledgeTypes = conDecAPI.extendedKnowledgeTypes;
		for (var index = 0; index < extendedKnowledgeTypes.length; index++) {
			var isSelected = "";
			if (isKnowledgeTypeLocatedAtIndex(knowledgeType, extendedKnowledgeTypes, index)) {
				isSelected = "selected ";
			}
			$("select[name='form-select-type']")[0].insertAdjacentHTML("beforeend", "<option " + isSelected + "value='"
					+ extendedKnowledgeTypes[index] + "'>" + extendedKnowledgeTypes[index] + "</option>");
		}
		AJS.$("#form-select-type").auiSelect2();
		AJS.$("#form-select-location").auiSelect2();
	}

	function setUpTypeChangeDialog(knowledgeType) {
		console.log("view.context.menu.js setUpTypeChangeDialog");
		document
				.getElementById("dialog-content")
				.insertAdjacentHTML(
						"afterBegin",
						"<form class='aui'><div class='field-group'><label for='form-select-type'>Knowledge type:</label>"
								+ "<select id='form-select-type' name='form-select-type' class='select full-width-field'/></div>"
								+ "</form>");
		var extendedKnowledgeTypes = conDecAPI.extendedKnowledgeTypes;
		for (var index = 0; index < extendedKnowledgeTypes.length; index++) {
			var isSelected = "";
			if (isKnowledgeTypeLocatedAtIndex(knowledgeType, extendedKnowledgeTypes, index)) {
				isSelected = "selected ";
			}
			$("select[name='form-select-type']")[0].insertAdjacentHTML("beforeend", "<option " + isSelected + "value='"
					+ extendedKnowledgeTypes[index] + "'>" + extendedKnowledgeTypes[index] + "</option>");
		}
		AJS.$("#form-select-type").auiSelect2();
	}

	function isKnowledgeTypeLocatedAtIndex(knowledgeType, extendedKnowledgeTypes, index) {
		console.log("view.context.menu.js isKnowledgeTypeLocatedAtIndex");
		return knowledgeType.toLowerCase() === extendedKnowledgeTypes[index].toLowerCase();
	}

	ConDecDialog.prototype.showLinkDialog = function showLinkDialog(id) {
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
					+ "onchange='conDecDialog.addFormForArguments()' class='select full-width-field'/>";
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
				conDecAPI.createLinkToExistingElement(id, childId, knowledgeTypeOfChild);
				AJS.dialog2("#dialog").hide();
			};
		});
	};

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

	ConDecDialog.prototype.addFormForArguments = addFormForArguments;

	ConDecDialog.prototype.showEditDialog = function showEditDialog(id, type) {
		console.log("view.context.menu.js setUpDialogForEditAction");
		conDecAPI.getDecisionKnowledgeElement(id, function(decisionKnowledgeElement) {
			var summary = decisionKnowledgeElement.summary;
			var description = decisionKnowledgeElement.description;
			var type = decisionKnowledgeElement.type;

			conDecAPI.isIssueStrategy(function(isDocumentedInJIRAIssue) {
				if (isDocumentedInJIRAIssue) {
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
					setUpCreateOrEditDialog(summary, description, type, false);

					var submitButton = document.getElementById("dialog-submit-button");
					submitButton.textContent = editKnowledgeElementText;
					submitButton.onclick = function() {
						var summary = document.getElementById("form-input-summary").value;
						var description = document.getElementById("form-input-description").value;
						var type = $("select[name='form-select-type']").val();
						conDecAPI.updateDecisionKnowledgeElementAsChild(id, summary, description, type);
						AJS.dialog2("#dialog").hide();
					};
				}
			});
		});
	};

	ConDecDialog.prototype.showDeleteDialog = function showDeleteDialog(id) {
		console.log("view.context.menu.js setUpDialogForDeleteAction");
		setUpDialog();
		setHeaderText(deleteKnowledgeElementText);

		var content = document.getElementById("dialog-content");
		content.textContent = "Do you really want to delete this element?";

		var submitButton = document.getElementById("dialog-submit-button");
		submitButton.textContent = deleteKnowledgeElementText;
		submitButton.onclick = function() {
			conDecAPI.deleteDecisionKnowledgeElement(id, function() {
				conDecObservable.notify();
			});
			AJS.dialog2("#dialog").hide();
		};
	};

	ConDecDialog.prototype.showDeleteLinkDialog = function showDeleteLinkDialog(id, parentId) {
		console.log("view.context.menu.js setUpDialogForDeleteLinkAction");
		setUpDialog();
		setHeaderText(deleteLinkToParentText);

		var content = document.getElementById("dialog-content");
		content.textContent = "Do you really want to delete the link to the parent element?";

		var submitButton = document.getElementById("dialog-submit-button");
		submitButton.textContent = deleteLinkToParentText;
		submitButton.onclick = function() {
			conDecAPI.deleteLink(parentId, id, "i", "i", function() {
				conDecObservable.notify();
			});
			AJS.dialog2("#dialog").hide();
		};
	};

	ConDecDialog.prototype.showChangeTypeDialog = function showChangeTypeDialog(id) {
		console.log("view.context.menu.js setUpDialogForChangeTypeAction");
		setUpDialog();
		setHeaderText(changeKnowledgeTypeText);
		conDecAPI.getDecisionKnowledgeElement(id, function(decisionKnowledgeElement) {
			var summary = decisionKnowledgeElement.summary;
			var description = decisionKnowledgeElement.description;
			var type = decisionKnowledgeElement.type;
			setUpTypeChangeDialog(type);

			var submitButton = document.getElementById("dialog-submit-button");
			submitButton.textContent = editKnowledgeElementText;
			submitButton.onclick = function() {
				var type = $("select[name='form-select-type']").val();
				conDecAPI.updateDecisionKnowledgeElementAsChild(id, summary, description, type);
				AJS.dialog2("#dialog").hide();
			};
		});
	};

	function resetDialog() {
		console.log("view.context.menu.js resetDialog");
		document.getElementById("dialog-header").innerHTML = "";
		document.getElementById("dialog-content").innerHTML = "";
		var dialog = document.getElementById("dialog");
		if (dialog.classList.contains("aui-dialog2-large")) {
			dialog.classList.remove("aui-dialog2-large");
		}
		if (!dialog.classList.contains("aui-dialog2-medium")) {
			dialog.classList.add("aui-dialog2-medium");
		}
	}

	/**
	 * fills HTML view-protion of dialog with contents
	 */
	function setUpEditSentenceDialogView(description, type, node) {
		document.getElementById("dialog-content").innerHTML = "";
		document.getElementById("dialog").classList.remove("aui-dialog2-large");
		document.getElementById("dialog").classList.add("aui-dialog2-medium");
		document.getElementById("dialog").style.zIndex = 9999;
		document
				.getElementById("dialog-content")
				.insertAdjacentHTML(
						"afterBegin",
						"<form class='aui'>"
								+ "<div class='field-group'><label for='form-input-description'>Sentence:</label>"
								+ "<textarea id='form-input-description' placeholder='Description' value='"
								+ description
								+ "' class='textarea full-width-field'>"
								+ description
								+ "</textarea></div>"
								+ "<div class='field-group'><label for='form-select-type'>Knowledge type:</label>"
								+ "<select id='form-select-type' name='form-select-type' class='select full-width-field'/></div>"
								+ "</form>");

		var knowledgeTypes = conDecAPI.knowledgeTypes;
		if (knowledgeTypes.includes("Issue") && knowledgeTypes.includes("Problem")) {
			var index = knowledgeTypes.indexOf("Issue");
			if (index > -1) {
				knowledgeTypes.splice(index, 1);
			}
		}
		if (!knowledgeTypes.includes("Pro") && !knowledgeTypes.includes("Con") && knowledgeTypes.includes("Argument")) {
			knowledgeTypes.splice(knowledgeTypes.indexOf("Argument"), 1);
			knowledgeTypes.push("Pro");
			knowledgeTypes.push("Con");
		}
		for (index = 0; index < knowledgeTypes.length; index++) {
			var isSelected = "";
			// first clause for treant, second for tree viewer
			if (node.includes(knowledgeTypes[index].toLowerCase()) || node === knowledgeTypes[index]) {
				isSelected = "selected ";
			}

			$("select[name='form-select-type']")[0].insertAdjacentHTML("beforeend", "<option " + isSelected + "value='"
					+ knowledgeTypes[index] + "'>" + knowledgeTypes[index] + "</option>");
		}
	}

	/**
	 * sets-up submit button
	 */
	function setUpEditSentenceDialogContext(id, description, type) {
		var submitButton = document.getElementById("dialog-submit-button");
		submitButton.textContent = "Change";
		submitButton.onclick = function() {
			var description = document.getElementById("form-input-description").value;
			var type = $("select[name='form-select-type']").val().split("-")[0];
			conDecAPI.editSentenceBody(id, description, type, function() {
				AJS.dialog2("#dialog").hide();
				conDecObservable.notify();
				JIRA.trigger(JIRA.Events.REFRESH_ISSUE_PAGE, [JIRA.Issue.getIssueId()]);
			});
		};
		AJS.$("#form-select-type").auiSelect2();
	}

	ConDecDialog.prototype.setUpDialogForEditSentenceAction = function setUpDialogForEditSentenceAction(id) {
		conDecAPI.getSentenceElement(id, function(result) {
			var description = result["description"];
			var type = result["type"];
			setUpDialog();
			setHeaderText(editKnowledgeElementText);
			setUpEditSentenceDialogView(description, type, type);
			setUpEditSentenceDialogContext(id, description, type);
		});
	};

	// export ConDecDialog
	global.conDecDialog = new ConDecDialog();
})(window);
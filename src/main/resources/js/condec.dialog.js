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

	var ConDecDialog = function ConDecDialog() {
	};

	ConDecDialog.prototype.showCreateDialog = function showCreateDialog(idOfParentElement,
			documentationLocationOfParentElement) {
		console.log("conDecDialog showCreateDialog");

		// HTML elements
		var createDialog = document.getElementById("create-dialog");
		var inputSummaryField = document.getElementById("create-form-input-summary");
		var inputDescriptionField = document.getElementById("create-form-input-description");
		var selectTypeField = document.getElementById("create-form-select-type");
		var selectLocationField = document.getElementById("create-form-select-location");
		var submitButton = document.getElementById("create-dialog-submit-button");
		var cancelButton = document.getElementById("create-dialog-cancel-button");

		// Fill HTML elements
		inputSummaryField.value = "";
		inputDescriptionField.value = "";
		fillSelectTypeField(selectTypeField, "Alternative");
		fillSelectLocationField(selectLocationField, documentationLocationOfParentElement);

		// Set onclick listener on buttons
		submitButton.onclick = function() {
			var summary = inputSummaryField.value;
			var description = inputDescriptionField.value;
			var type = selectTypeField.value;
			var documentationLocation = selectLocationField.value;
			conDecAPI.createDecisionKnowledgeElement(summary, description, type, documentationLocation,
					idOfParentElement, documentationLocationOfParentElement, function() {
						conDecObservable.notify();
					});
			AJS.dialog2(createDialog).hide();
		};

		cancelButton.onclick = function() {
			AJS.dialog2(createDialog).hide();
		};

		// Show dialog
		AJS.dialog2(createDialog).show();
	};

	ConDecDialog.prototype.showDeleteDialog = function showDeleteDialog(id, documentationLocation) {
		console.log("conDecDialog showDeleteDialog");

		// HTML elements
		var deleteDialog = document.getElementById("delete-dialog");
		var content = document.getElementById("delete-dialog-content");
		var submitButton = document.getElementById("delete-dialog-submit-button");
		var cancelButton = document.getElementById("delete-dialog-cancel-button");

		// Set onclick listener on buttons
		submitButton.onclick = function() {
			conDecAPI.deleteDecisionKnowledgeElement(id, documentationLocation, function() {
				conDecObservable.notify();
			});
			AJS.dialog2(deleteDialog).hide();
		};

		cancelButton.onclick = function() {
			AJS.dialog2(deleteDialog).hide();
		};

		// Show dialog
		AJS.dialog2(deleteDialog).show();
	};

	ConDecDialog.prototype.showDeleteLinkDialog = function showDeleteLinkDialog(id, documentationLocation) {
		console.log("conDecDialog showDeleteLinkDialog");

		// HTML elements
		var deleteLinkDialog = document.getElementById("delete-link-dialog");
		var content = document.getElementById("delete-link-dialog-content");
		var submitButton = document.getElementById("delete-link-dialog-submit-button");
		var cancelButton = document.getElementById("delete-link-dialog-cancel-button");

		// Set onclick listener on buttons
		submitButton.onclick = function() {
			var parentElement = conDecTreant.findParentElement(id);
			conDecAPI.deleteLink(parentElement["id"], id, parentElement["documentationLocation"],
					documentationLocation, function() {
						conDecObservable.notify();
					});
			AJS.dialog2(deleteLinkDialog).hide();
		};

		cancelButton.onclick = function() {
			AJS.dialog2(deleteLinkDialog).hide();
		};

		// Show dialog
		AJS.dialog2(deleteLinkDialog).show();
	};

	ConDecDialog.prototype.showLinkDialog = function showLinkDialog(id, documentationLocation) {
		console.log("conDecDialog showLinkDialog");

		// HTML elements
		var linkDialog = document.getElementById("link-dialog");
		var selectElementField = document.getElementById("link-form-select-element");
		var submitButton = document.getElementById("link-dialog-submit-button");
		var cancelButton = document.getElementById("link-dialog-cancel-button");
		var argumentFieldGroup = document.getElementById("argument-field-group");
		var radioPro = document.getElementById("link-form-radio-pro");
		var radioCon = document.getElementById("link-form-radio-con");

		// Fill HTML elements
		fillSelectElementField(selectElementField, id, documentationLocation);
		argumentFieldGroup.style.display = "none";
		radioPro.checked = false;
		radioCon.checked = false;

		selectElementField.onchange = function() {
			conDecAPI.getDecisionKnowledgeElement(this.value, "i", function(decisionKnowledgeElement) {
				if (decisionKnowledgeElement && decisionKnowledgeElement.type === "Argument") {
					argumentFieldGroup.style.display = "inherit";
					radioPro.checked = true;
				}
			});
		};

		// Set onclick listener on buttons
		submitButton.onclick = function() {
			var childId = selectElementField.value;
			var knowledgeTypeOfChild = $('input[name=form-radio-argument]:checked').val();
			conDecAPI.createLink(knowledgeTypeOfChild, id, childId, "i", "i", function() {
				conDecObservable.notify();
			});
			AJS.dialog2(linkDialog).hide();
		};

		cancelButton.onclick = function() {
			AJS.dialog2(linkDialog).hide();
		};

		// Show dialog
		AJS.dialog2(linkDialog).show();
	};

	function fillSelectElementField(selectField, id, documentationLocation) {
		if (selectField === null) {
			return;
		}
		selectField.innerHTML = "";
		conDecAPI.getUnlinkedElements(id, documentationLocation, function(unlinkedElements) {
			var insertString = "";
			var isSelected = "selected";
			for (var index = 0; index < unlinkedElements.length; index++) {
				insertString += "<option " + isSelected + " value='" + unlinkedElements[index].id + "'>"
						+ unlinkedElements[index].type + ' / ' + unlinkedElements[index].summary + "</option>";
				isSelected = "";
			}
			selectField.insertAdjacentHTML("afterBegin", insertString);
		});
		AJS.$(selectField).auiSelect2();
	}

	ConDecDialog.prototype.showEditDialog = function showEditDialog(id, documentationLocation, type) {
		console.log("conDecDialog showEditDialog");

		conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function(decisionKnowledgeElement) {
			var summary = decisionKnowledgeElement.summary;
			var description = decisionKnowledgeElement.description;
			var type = decisionKnowledgeElement.type;
			var documentationLocation = decisionKnowledgeElement.documentationLocation;

			if (documentationLocation === "i") {
				var createEditIssueForm = require('quick-edit/form/factory/edit-issue');
				createEditIssueForm({
					issueId : id
				}).asDialog({}).show();
				return;
			}

			// HTML elements
			var editDialog = document.getElementById("edit-dialog");
			var inputSummaryField = document.getElementById("edit-form-input-summary");
			var inputDescriptionField = document.getElementById("edit-form-input-description");
			var selectTypeField = document.getElementById("edit-form-select-type");
			var selectLocationField = document.getElementById("edit-form-select-location");
			var submitButton = document.getElementById("edit-dialog-submit-button");
			var cancelButton = document.getElementById("edit-dialog-cancel-button");

			// Fill HTML elements
			inputSummaryField.value = summary;
			inputDescriptionField.value = description;
			fillSelectTypeField(selectTypeField, type);
			fillSelectLocationField(selectLocationField, documentationLocation);
			if (documentationLocation === "s") {
				inputSummaryField.disabled = true;
				selectLocationField.disabled = true;
			}

			// Set onclick listener on buttons
			submitButton.onclick = function() {
				var summary = inputSummaryField.value;
				var description = inputDescriptionField.value;
				var type = selectTypeField.value;
				conDecAPI.updateDecisionKnowledgeElement(id, summary, description, type, documentationLocation,
						function() {
							conDecObservable.notify();
						});
				AJS.dialog2(editDialog).hide();
			};

			cancelButton.onclick = function() {
				AJS.dialog2(editDialog).hide();
			};

			// Show dialog
			AJS.dialog2(editDialog).show();
		});
	};

	function fillSelectTypeField(selectField, selectedKnowledgeType) {
		if (selectField === null) {
			return;
		}
		selectField.innerHTML = "";
		var extendedKnowledgeTypes = conDecAPI.extendedKnowledgeTypes;
		for (var index = 0; index < extendedKnowledgeTypes.length; index++) {
			var isSelected = "";
			if (isKnowledgeTypeLocatedAtIndex(selectedKnowledgeType, extendedKnowledgeTypes, index)) {
				isSelected = "selected";
			}
			selectField.insertAdjacentHTML("beforeend", "<option " + isSelected + " value='"
					+ extendedKnowledgeTypes[index] + "'>" + extendedKnowledgeTypes[index] + "</option>");
		}
		AJS.$(selectField).auiSelect2();
	}

	function isKnowledgeTypeLocatedAtIndex(knowledgeType, extendedKnowledgeTypes, index) {
		console.log("conDecDialog isKnowledgeTypeLocatedAtIndex");
		return knowledgeType.toLowerCase() === extendedKnowledgeTypes[index].toLowerCase().split("-")[0];
	}

	function fillSelectLocationField(selectField, documentationLocationOfParentElement) {
		if (selectField === null) {
			return;
		}
		selectField.innerHTML = "";
		selectField.insertAdjacentHTML("beforeend", "<option selected value = 'i'>JIRA Issue</option>"
				+ "<option value = 's'>JIRA Issue Comment</option></select></div>");

		AJS.$(selectField).auiSelect2();
	}

	ConDecDialog.prototype.showChangeTypeDialog = function showChangeTypeDialog(id, documentationLocation) {
		console.log("conDecDialog showChangeTypeDialog");

		// HTML elements
		var changeTypeDialog = document.getElementById("change-type-dialog");
		var selectTypeField = document.getElementById("change-type-form-select-type");
		var submitButton = document.getElementById("change-type-dialog-submit-button");
		var cancelButton = document.getElementById("change-type-dialog-cancel-button");

		// Fill HTML elements
		conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function(decisionKnowledgeElement) {
			fillSelectTypeField(selectTypeField, decisionKnowledgeElement.type);
		});

		// Set onclick listener on buttons
		submitButton.onclick = function() {
			var type = selectTypeField.value;
			conDecAPI.changeKnowledgeType(id, type, documentationLocation, function() {
				conDecObservable.notify();
			});
			AJS.dialog2(changeTypeDialog).hide();
		};

		cancelButton.onclick = function() {
			AJS.dialog2(changeTypeDialog).hide();
		};

		// Show dialog
		AJS.dialog2(changeTypeDialog).show();
	};

	ConDecDialog.prototype.showSummarizedDialog = function showSummarizedDialog(id, documentationLocation) {
		console.log("conDecDialog summarizedDialog");

		// HTML elements
		var summarizedDialog = document.getElementById("summarization-dialog");
		var cancelButton = document.getElementById("summarization-dialog-cancel-button");
		var content = document.getElementById("summarization-dialog-content");

		conDecAPI.getSummarizedCode(id, documentationLocation, function(text) {
			var insertString = "<form class='aui'>" + "<div>" + text + "</div>" + "</form>";
			content.innerHTML = insertString;
		});

		cancelButton.onclick = function() {
			AJS.dialog2(summarizedDialog).hide();
		};

		// Show dialog
		AJS.dialog2(summarizedDialog).show();
	};
	ConDecDialog.prototype.showExportDialog = function showExportDialog(decisionElementKey) {
		console.log("conDecDialog exportDialog");

		// HTML elements
		var exportDialog = document.getElementById("export-dialog");
		var hiddenDiv= document.getElementById("exportQueryFallback");
		// set hidden attribute
		hiddenDiv.setAttribute("data-tree-element-key",decisionElementKey);
		//open dialog
		AJS.dialog2(exportDialog).show();
	};

	// export ConDecDialog
	global.conDecDialog = new ConDecDialog();
})(window);
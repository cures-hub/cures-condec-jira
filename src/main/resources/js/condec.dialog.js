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

		// Fill HTML elements
		inputSummaryField.value = "";
		inputDescriptionField.value = "";
		fillSelectTypeField(selectTypeField, "Alternative");
		fillSelectLocationField(selectLocationField, documentationLocationOfParentElement);

		// Set onclick listener on submit button
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

		// Show dialog
		AJS.dialog2(createDialog).show();
	};

	ConDecDialog.prototype.showDeleteDialog = function showDeleteDialog(id, documentationLocation) {
		console.log("conDecDialog showDeleteDialog");

		// HTML elements
		var deleteDialog = document.getElementById("delete-dialog");
		var content = document.getElementById("delete-dialog-content");
		var submitButton = document.getElementById("delete-dialog-submit-button");

		// Fill HTML elements
		content.textContent = "Do you really want to delete this element?";

		// Set onclick listener on submit button
		submitButton.onclick = function() {
			conDecAPI.deleteDecisionKnowledgeElement(id, documentationLocation, function() {
				conDecObservable.notify();
			});
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

		// Fill HTML elements
		content.textContent = "Do you really want to delete the link to the parent element?";

		// Set onclick listener on submit button
		submitButton.onclick = function() {
			var parentElement = conDecTreant.findParentElement(id);
			conDecAPI.deleteLink(parentElement["id"], id, parentElement["documentationLocation"],
					documentationLocation, function() {
						conDecObservable.notify();
					});
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

		// Fill HTML elements
		fillSelectElementField(selectElementField, id, documentationLocation);
		//addFormForArguments();

		// Set onclick listener on submit button
		submitButton.onclick = function() {
			var childId = selectElementField.value;
			var knowledgeTypeOfChild = $('input[name=form-radio-argument]:checked').val();
			conDecAPI.createLink(knowledgeTypeOfChild, id, childId, "i", "i", function() {
				conDecObservable.notify();
			});
			AJS.dialog2(linkDialog).hide();
		};

		// Show dialog
		AJS.dialog2(linkDialog).show();
	};

	function fillSelectElementField(selectField, id, documentationLocation) {
		if (selectField == null) {
			return;
		}
		selectField.innerHTML = "";
		conDecAPI.getUnlinkedElements(id, documentationLocation, function(unlinkedElements) {
			insertString = "";
			for (var index = 0; index < unlinkedElements.length; index++) {
				insertString += "<option value='" + unlinkedElements[index].id + "'>" + unlinkedElements[index].type
						+ ' / ' + unlinkedElements[index].summary + "</option>";
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

			// Fill HTML elements
			inputSummaryField.value = summary;
			inputDescriptionField.value = description;
			fillSelectTypeField(selectTypeField, type);
			fillSelectLocationField(selectLocationField, documentationLocation);
			if (documentationLocation === "s") {
				inputSummaryField.disabled = true;
				selectLocationField.disabled = true;
			}

			// Set onclick listener on submit button
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

			// Show dialog
			AJS.dialog2(editDialog).show();
		});
	};

	function fillSelectTypeField(selectField, selectedKnowledgeType) {
		if (selectField == null) {
			return;
		}
		selectField.innerHTML = "";
		var extendedKnowledgeTypes = conDecAPI.extendedKnowledgeTypes;
		for (var index = 0; index < extendedKnowledgeTypes.length; index++) {
			var isSelected = "";
			if (isKnowledgeTypeLocatedAtIndex(selectedKnowledgeType, extendedKnowledgeTypes, index)) {
				isSelected = "selected ";
			}
			selectField.insertAdjacentHTML("beforeend", "<option " + isSelected + "value='"
					+ extendedKnowledgeTypes[index] + "'>" + extendedKnowledgeTypes[index] + "</option>");
		}
		AJS.$(selectField).auiSelect2();
	}

	function isKnowledgeTypeLocatedAtIndex(knowledgeType, extendedKnowledgeTypes, index) {
		console.log("conDecDialog isKnowledgeTypeLocatedAtIndex");
		return knowledgeType.toLowerCase() === extendedKnowledgeTypes[index].toLowerCase().split("-")[0];
	}

	function fillSelectLocationField(selectField, documentationLocationOfParentElement) {
		if (selectField == null) {
			return;
		}
		selectField.innerHTML = "";
		selectField.insertAdjacentHTML("beforeend", "<option selected value = 'i'>JIRA Issue</option>"
				+ "<option value = 's'>JIRA Issue Comment</option></select></div>");

		AJS.$(selectField).auiSelect2();
	}

	function setUpTypeChangeDialog(knowledgeType) {
		console.log("conDecDialog setUpTypeChangeDialog");
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

	function addFormForArguments() {
		console.log("conDecDialog addFormForArguments");
		var childId = $("select[name='form-select-component']").val();
		var argumentFieldGroup = document.getElementById("argument-field-group");
		argumentFieldGroup.innerHTML = "";
		conDecAPI
				.getDecisionKnowledgeElement(
						childId,
						"i",
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

	ConDecDialog.prototype.showChangeTypeDialog = function showChangeTypeDialog(id, documentationLocation) {
		console.log("conDecDialog setUpDialogForChangeTypeAction");

		conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function(decisionKnowledgeElement) {
			var type = decisionKnowledgeElement.type;
			setUpTypeChangeDialog(type);

			var submitButton = document.getElementById("dialog-submit-button");
			submitButton.textContent = editKnowledgeElementText;
			submitButton.onclick = function() {
				var type = $("select[name='form-select-type']").val();
				conDecAPI.changeKnowledgeType(id, type, documentationLocation, function() {
					conDecObservable.notify();
				});
				AJS.dialog2("#create-dialog").hide();
			};
		});
	};

	function resetDialog() {
		console.log("conDecDialog resetDialog");
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

	// export ConDecDialog
	global.conDecDialog = new ConDecDialog();
})(window);
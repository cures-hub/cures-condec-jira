/**
 This module provides the dialogs for managing decision knowledge. The user can
 * create a new decision knowledge element,
 * edit an existing decision knowledge element,
 * delete an existing knowledge element,
 * create a new link between two knowledge elements,
 * delete a link between two knowledge elements,
 * change the documentation location (e.g. from issue comments to single Jira issues),
 * set an element to the root element in the knowledge tree.

 Requires
 * conDecAPI

 Is required by
 * conDecContextMenu
 */
(function(global) {
	var ConDecDialog = function() {
	};

	ConDecDialog.prototype.showCreateDialog = function(idOfParentElement, documentationLocationOfParentElement, selectedType = "Alternative", summary = "", description = "", callback = function() { }) {
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
		fillElementField("create-form-source-element", idOfParentElement, documentationLocationOfParentElement);
		inputSummaryField.value = summary;
		inputDescriptionField.value = description;
		fillSelectTypeField(selectTypeField, selectedType);
		fillSelectLocationField(selectLocationField, documentationLocationOfParentElement);

		// Set onclick listener on buttons
		submitButton.onclick = function() {
			var summary = inputSummaryField.value;
			var description = inputDescriptionField.value;
			var type = selectTypeField.value;
			var documentationLocation = selectLocationField.value;
			conDecAPI.createDecisionKnowledgeElement(summary, description, type, documentationLocation,
				idOfParentElement, documentationLocationOfParentElement, function(id) {
					callback(id, documentationLocationOfParentElement)
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

	function fillElementField(elementFieldName, id, documentationLocation) {
		var elementField = document.getElementById(elementFieldName);

		if (id !== undefined && id !== -1 && id !== 0 && documentationLocation !== undefined && documentationLocation !== null) {
			conDecAPI.getKnowledgeElement(id, documentationLocation, function(sourceElement) {
				elementField.value = sourceElement.type + " / " + sourceElement.summary;
			});
			document.getElementById(elementFieldName + "-group").style.display = "block";
		} else {
			document.getElementById(elementFieldName + "-group").style.display = "none";
		}
	}

	ConDecDialog.prototype.showDeleteDialog = function(id, documentationLocation, callback = function() {
	}) {
		console.log("conDecDialog showDeleteDialog");

		// HTML elements
		var deleteDialog = document.getElementById("delete-dialog");
		var submitButton = document.getElementById("delete-dialog-submit-button");
		var cancelIcon = document.getElementById("delete-dialog-cancel-icon");
		var cancelButton = document.getElementById("delete-dialog-cancel-button");

		// Set onclick listener on buttons
		submitButton.onclick = function() {
			conDecAPI.deleteDecisionKnowledgeElement(id, documentationLocation, function() {
				conDecObservable.notify();
			});
			AJS.dialog2(deleteDialog).hide();
		};

		cancelIcon.onclick = function() {
			callback(null);
		};

		cancelButton.onclick = function() {
			AJS.dialog2(deleteDialog).hide();
			callback(null);
		};

		// Show dialog
		AJS.dialog2(deleteDialog).show();
	};

	ConDecDialog.prototype.showDeleteLinkDialog = function(id, documentationLocation, idOfParent, documentationLocationOfParent, callback = function() { }) {
		console.log("conDecDialog showDeleteLinkDialog");

		// HTML elements
		var deleteLinkDialog = document.getElementById("delete-link-dialog");
		var submitButton = document.getElementById("delete-link-dialog-submit-button");
		var cancelIcon = document.getElementById("delete-link-dialog-cancel-icon");
		var cancelButton = document.getElementById("delete-link-dialog-cancel-button");

		// Fill HTML elements
		fillElementField("delete-link-dialog-source-element", id, documentationLocation);
		if (idOfParent === null || idOfParent === undefined || idOfParent <= 0) {
			var parentElement = conDecTreant.findParentElement(id);
			idOfParent = parentElement["id"];
			documentationLocationOfParent = parentElement["documentationLocation"];
		}
		fillElementField("delete-link-dialog-target-element", idOfParent, documentationLocationOfParent);

		var targetElementDiv = document.getElementById("delete-link-dialog-target-element-group");
		// Disable the submit button in case no target link element exists
		if (targetElementDiv.style.display === "none") {
			document.getElementById("delete-link-dialog-submit-button").disabled = true;
			document.getElementById("delete-link-dialog-submit-button").title = "No target element found";
		} else {
			document.getElementById("delete-link-dialog-submit-button").disabled = false;
		}

		// Set onclick listener on buttons
		submitButton.onclick = function() {
			conDecAPI.deleteLink(idOfParent, id, documentationLocationOfParent,
				documentationLocation, function() {
					conDecObservable.notify();
				});
			AJS.dialog2(deleteLinkDialog).hide();
		};

		cancelIcon.onclick = function() {
			callback(null);
		};

		cancelButton.onclick = function() {
			AJS.dialog2(deleteLinkDialog).hide();
			callback(null);
		};

		// Show dialog
		AJS.dialog2(deleteLinkDialog).show();
	};

	ConDecDialog.prototype.showLinkDialog = function(id, documentationLocation, idOfTarget, documentationLocationOfTarget, linkType) {
		console.log("conDecDialog showLinkDialog");

		// HTML elements
		var linkDialog = document.getElementById("link-dialog");
		var selectElementField = document.getElementById("link-form-select-element");
		var selectLinkTypeField = document.getElementById("link-form-select-linktype");
		var submitButton = document.getElementById("link-dialog-submit-button");
		var cancelButton = document.getElementById("link-dialog-cancel-button");

		// Fill HTML elements
		fillElementField("link-form-source-element", id, documentationLocation);

		if (idOfTarget !== undefined && documentationLocationOfTarget !== undefined) {
			conDecAPI.getKnowledgeElement(idOfTarget, documentationLocationOfTarget, function(targetElement) {
				selectElementField.innerHTML = "";
				var text = targetElement.type + " / " + targetElement.summary;
				var value = targetElement.id + ":" + targetElement.documentationLocation;
				var option = new Option(text, value, false, false);
				$(selectElementField).append(option);
				if (linkType === null || linkType === undefined) {
					$(selectElementField).trigger("change");
				}
			});
		} else {
			fillSelectElementField(selectElementField, id, documentationLocation);
		}

		fillSelectLinkTypeField(selectLinkTypeField, id, documentationLocation, linkType);

		selectElementField.onchange = function() {
			conDecAPI.getKnowledgeElement(id, documentationLocation,
				function(element) {
					selectLinkTypeField.value = suggestLinkType(element.type);
					AJS.$(selectLinkTypeField).auiSelect2();
				});
		};

		// Set onclick listener on buttons
		submitButton.onclick = function() {
			var idOfChild = selectElementField.value.split(":")[0];
			var documentationLocationOfChild = selectElementField.value.split(":")[1];
			var linkType = selectLinkTypeField.value;
			conDecAPI.createLink(id, idOfChild, documentationLocation, documentationLocationOfChild,
				linkType, function() { conDecObservable.notify() });
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
			for (var index = 0; index < unlinkedElements.length; index++) {
				insertString += "<option value='" + unlinkedElements[index].id + ":"
					+ unlinkedElements[index].documentationLocation + "'>"
					+ unlinkedElements[index].type + ' / ' + unlinkedElements[index].summary + "</option>";
			}
			selectField.insertAdjacentHTML("afterBegin", insertString);
		});
		AJS.$(selectField).auiSelect2();
	}

	function fillSelectLinkTypeField(selectField, id, documentationLocation, linkType) {
		if (selectField === null) {
			return;
		}
		selectField.innerHTML = "";
		var linkTypes = conDecAPI.getLinkTypes();
		var insertString = "";
		for (index = 0; index < linkTypes.length; index++) {
			if (linkTypes[index] !== "Other" && linkTypes[index] !== "Transitive") {
				insertString += "<option " + " value='" + linkTypes[index] + "'>"
					+ linkTypes[index] + "</option>";
			}
		}
		selectField.insertAdjacentHTML("afterBegin", insertString);
		if (linkType !== null && linkType !== undefined) {
			selectField.value = linkTypes.find(type => type.toLowerCase().startsWith(linkType));
		} else {
			selectField.value = "Relates";
		}
		AJS.$(selectField).auiSelect2();
	}

	function suggestLinkType(knowledgeType) {
		if (knowledgeType === "Argument" || knowledgeType === "Pro") {
			return "Supports";
		} else if (knowledgeType === "Con") {
			return "Attacks";
		}
		return "Relates";
	}

	/**
	 * external references: conDecVis
	 */
	ConDecDialog.prototype.showEditDialog = function(id, documentationLocation, callback = function() { }) {
		console.log("conDecDialog showEditDialog");

		conDecAPI.getKnowledgeElement(id, documentationLocation, function(decisionKnowledgeElement) {
			var summary = decisionKnowledgeElement.summary;
			var description = decisionKnowledgeElement.description;
			var type = decisionKnowledgeElement.type;
			var documentationLocation = decisionKnowledgeElement.documentationLocation;

			if (documentationLocation === "i") {
				var createEditIssueForm = require("quick-edit/form/factory/edit-issue");
				createEditIssueForm({
					issueId: id
				}).asDialog({}).show();
				return;
			}

			// HTML elements
			var editDialog = document.getElementById("edit-dialog");
			var inputSummaryField = document.getElementById("edit-form-input-summary");
			var inputDescriptionField = document.getElementById("edit-form-input-description");
			var selectTypeField = document.getElementById("edit-form-select-type");
			var selectStatusField = document.getElementById("edit-form-select-status");
			var selectLocationField = document.getElementById("edit-form-select-location");
			var submitButton = document.getElementById("edit-dialog-submit-button");
			var cancelIcon = document.getElementById("edit-dialog-cancel-icon");
			var cancelButton = document.getElementById("edit-dialog-cancel-button");

			var selectLevelField = document.getElementById("edit-form-select-level");
			var inputExistingGroupsField = document.getElementById("edit-form-input-existing");

			// Fill HTML elements
			inputSummaryField.value = summary;
			inputDescriptionField.value = description;
			fillSelectTypeField(selectTypeField, type);
			fillSelectStatusField(selectStatusField, decisionKnowledgeElement);
			fillSelectLocationField(selectLocationField, documentationLocation);
			if (documentationLocation === "s") {
				inputSummaryField.disabled = true;
				selectLocationField.disabled = true;
			}
			conDecGroupingAPI.getDecisionGroupsForElement(id, documentationLocation, function(groups) {
				if (groups.length > 0) {
					var level = groups[0];
					selectLevelField.value = level;
				}
				if (groups.length > 1) {
					groups.shift();
					inputExistingGroupsField.value = groups;
				} else {
					inputExistingGroupsField.value = "";
				}
			});

			// Set onclick listener on buttons
			submitButton.onclick = function() {
				var summary = inputSummaryField.value;
				var description = inputDescriptionField.value;
				var type = selectTypeField.value;
				var status = selectStatusField.value;
				conDecAPI.updateDecisionKnowledgeElement(id, summary, description, type, documentationLocation, status,
					function() {
						conDecObservable.notify();
					});
				var level = selectLevelField.value;
				var existingGroups = inputExistingGroupsField.value;
				var addgroup = "";
				conDecGroupingAPI.assignDecisionGroup(level, existingGroups, addgroup,
					id, documentationLocation, function(id) {
						conDecObservable.notify();
					});
				AJS.dialog2(editDialog).hide();
			};

			cancelIcon.onclick = function() {
				callback(null);
			};

			cancelButton.onclick = function() {
				AJS.dialog2(editDialog).hide();
				callback(null);
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
		var extendedKnowledgeTypes = conDecAPI.getExtendedKnowledgeTypes();
		for (var index = 0; index < extendedKnowledgeTypes.length; index++) {
			var isSelected = "";
			if (isKnowledgeTypeLocatedAtIndex(selectedKnowledgeType, extendedKnowledgeTypes, index)) {
				isSelected = "selected";
			}
			selectField.insertAdjacentHTML("beforeend", "<option " + isSelected + " value='"
				+ extendedKnowledgeTypes[index] + "'>" + extendedKnowledgeTypes[index] + "</option>");
		}
	}

	function isKnowledgeTypeLocatedAtIndex(knowledgeType, extendedKnowledgeTypes, index) {
		console.log("conDecDialog isKnowledgeTypeLocatedAtIndex");
		return extendedKnowledgeTypes[index].toLowerCase().startsWith(knowledgeType.toLowerCase());
	}

	function fillSelectLocationField(selectField, documentationLocationOfParentElement) {
		if (selectField === null) {
			return;
		}
		selectField.innerHTML = "";
		conDecAPI.isJiraIssueDocumentationLocationActivated(function(isEnabled) {
			if (documentationLocationOfParentElement !== null) {
				selectField.insertAdjacentHTML("beforeend", "<option selected value = 's'>Jira issue comment</option>");
			}
			if (isEnabled) {
				selectField.insertAdjacentHTML("beforeend", "<option value = 'i'>Jira issue</option>");
			}
		});
	}

	ConDecDialog.prototype.showChangeStatusDialog = function(id, documentationLocation) {
		console.log("conDecDialog showChangeStatusDialog");

		// HTML elements
		var changeStatusDialog = document.getElementById("change-status-dialog");
		var selectStatusField = document.getElementById("change-status-form-select-status");
		var submitButton = document.getElementById("change-status-dialog-submit-button");
		var cancelButton = document.getElementById("change-status-dialog-cancel-button");

		// Fill HTML elements
		conDecAPI.getKnowledgeElement(id, documentationLocation, function(element) {
			fillSelectStatusField(selectStatusField, element);

			// Set onclick listener on buttons
			submitButton.onclick = function() {
				var status = selectStatusField.value;
				conDecAPI.updateDecisionKnowledgeElement(id, element.summary, element.description, element.type,
					documentationLocation, status, function() {
						conDecObservable.notify();
					});
				AJS.dialog2(changeStatusDialog).hide();
			};
		});

		cancelButton.onclick = function() {
			AJS.dialog2(changeStatusDialog).hide();
		};

		// Show dialog
		AJS.dialog2(changeStatusDialog).show();
	};

	function fillSelectStatusField(selectField, element) {
		if (selectField === null) {
			return;
		}
		var knowledgeStatus = null;
		if (element.type === "Issue" || element.type === "Problem") {
			knowledgeStatus = conDecAPI.issueStatus;
		} else if (element.type === "Decision" || element.type === "Solution") {
			knowledgeStatus = conDecAPI.decisionStatus;
		} else if (element.type === "Alternative") {
			knowledgeStatus = conDecAPI.alternativeStatus;
		} else {
			knowledgeStatus = [];
		}
		selectField.innerHTML = "";
		for (var index = 0; index < knowledgeStatus.length; index++) {
			var isSelected = "";
			if (element.status.toUpperCase() === knowledgeStatus[index].toUpperCase()) {
				isSelected = "selected";
			}
			selectField.insertAdjacentHTML("beforeend", "<option " + isSelected + " value='"
				+ knowledgeStatus[index] + "'>" + knowledgeStatus[index] + "</option>");
		}
		AJS.$(selectField).auiSelect2();
	}

	ConDecDialog.prototype.showSummarizedDialog = function(id = document.getElementById("id").value,
		documentationLocation = document.getElementById("documentationLocation").value) {
		// HTML elements
		var summarizedDialog = document.getElementById("summarization-dialog");
		var cancelButton = document.getElementById("summarization-dialog-cancel-button");
		var content = document.getElementById("summarization-dialog-content");
		var probabilityOfCorrectness = document.getElementById("summarization-probabilityOfCorrectness").valueAsNumber;

		conDecGitAPI.getSummarizedCode(id, documentationLocation, probabilityOfCorrectness)
			.then(summary => {
				document.getElementById("id").value = id;
				document.getElementById("documentationLocation").value = documentationLocation;
				content.innerHTML = summary;
			});

		cancelButton.onclick = function() {
			AJS.dialog2(summarizedDialog).hide();
		};

		// Show dialog
		AJS.dialog2(summarizedDialog).show();
	};

	ConDecDialog.prototype.showExportDialog = function(id, documentationLocation) {
		console.log("conDecDialog showExportDialog");

		// HTML elements
		var exportDialog = document.getElementById("export-dialog");
		var submitButton = document.getElementById("export-dialog-submit-button");
		var cancelButton = document.getElementById("export-dialog-cancel-button");

		// Init filter settings
		conDecFiltering.fillDropdownMenus("export");
		document.getElementById("is-transitive-links-input-export").checked = true;
		document.getElementById("is-decision-knowledge-only-input-export").checked = true;
		conDecAPI.getKnowledgeElement(id, documentationLocation, function(element) {
			conDecFiltering.setSelectedElement("export", element.key);
		});

		// Set onclick listener on buttons
		submitButton.onclick = function() {
			var exportFormat = $('input[name=form-radio-export-format]:checked').val();
			conDecExport.exportLinkedElements(exportFormat, id, documentationLocation);
			AJS.dialog2(exportDialog).hide();
		};

		cancelButton.onclick = function() {
			AJS.dialog2(exportDialog).hide();
		};

		// Show dialog
		AJS.dialog2(exportDialog).show();
	};


	global.conDecDialog = new ConDecDialog();
})(window);

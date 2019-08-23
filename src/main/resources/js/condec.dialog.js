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

	function fillSelectStatusFiled(selectField, elementStatus, element) {
	    if(selectField === null) {
	        return;
        }
        var knowledgeStatus = null;
        if(element.type === "Issue") {
	        knowledgeStatus = conDecAPI.issueStatus;
        } else {
            knowledgeStatus  = conDecAPI.knowledgeStatus;
        }
        selectField.innerHTML = "";
	    for( var index = 0; index < knowledgeStatus.length; index++) {
	        var isSelected = "";
	        console.log(elementStatus);
	        if(knowledgeStatus[index].toLocaleUpperCase() === elementStatus) {
	            isSelected = "selected";
            }
            selectField.insertAdjacentHTML("beforeend", "<option " + isSelected + " value='"
                + knowledgeStatus[index] + "'>" + knowledgeStatus[index] + "</option>");
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

	ConDecDialog.prototype.showChangeStatusDialog = function showChangeStatusDialog(id,documentationLocation) {
        console.log("conDecDialog showChangeStatusDialog");

        // HTML elements
        var changeStatusDialog = document.getElementById("change-status-dialog");
        var selectStatusField = document.getElementById("change-status-form-select-type");
        var submitButton = document.getElementById("change-status-dialog-submit-button");
        var cancelButton = document.getElementById("change-status-dialog-cancel-button");

        // Fill HTML elements
        conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function(decisionKnowledgeElement) {
          conDecAPI.getStatus(decisionKnowledgeElement, function (status) {
              fillSelectStatusFiled(selectStatusField, status, decisionKnowledgeElement);
          });
        });

        // Set onclick listener on buttons
        submitButton.onclick = function() {
            var status = selectStatusField.value;
            conDecAPI.setStatus(id, documentationLocation, status, function() {
                conDecObservable.notify();
            });
            AJS.dialog2(changeStatusDialog).hide();
        };

        cancelButton.onclick = function() {
            AJS.dialog2(changeStatusDialog).hide();
        };

        // Show dialog
        AJS.dialog2(changeStatusDialog).show();
    };

    ConDecDialog.prototype.showSummarizedDialog = function showSummarizedDialog(id, documentationLocation) {
		// HTML elements
		var summarizedDialog = document.getElementById("summarization-dialog");
		var cancelButton = document.getElementById("summarization-dialog-cancel-button");
		var content = document.getElementById("summarization-dialog-content");
		var probabilityOfCorrectness = document.getElementById("summarization-probabilityOfCorrectness").valueAsNumber;
		var projectId = document.getElementById("summarization-projectId").value;
        if (projectId === undefined || projectId.length === 0 || projectId === "") {
            document.getElementById("summarization-projectId").value = id;
            projectId = id;
        }
        conDecAPI.getSummarizedCode(parseInt(projectId, 10), documentationLocation, probabilityOfCorrectness, function(text) {
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
		var hiddenDiv = document.getElementById("exportQueryFallback");
		// set hidden attribute
		hiddenDiv.setAttribute("data-tree-element-key", decisionElementKey);
		// open dialog
		AJS.dialog2(exportDialog).show();
	};

	ConDecDialog.prototype.showCreateReleaseNoteDialog = function showCreateReleaseNoteDialog() {
		// HTML elements
		//set button busy before we show the dialog
		var openingButton=document.getElementById("openCreateReleaseNoteDialogButton");
		setButtonBusyAndDisabled(openingButton,true);
		var releaseNoteDialog = document.getElementById("create-release-note-dialog");
		var cancelButton = document.getElementById("create-release-note-dialog-cancel-button");
		var configurationSubmitButton = document.getElementById("create-release-note-submit-button");
		var loader = document.getElementById("createReleaseNoteDialogLoader");
		// add task prioritisation
		var criteria=[
			{title:"#Description Knowledge",id:"count_decision_knowledge"},
			{title:"Priority",id:"priority"},
			{title:"#Comments",id:"count_comments"},
			{title:"Words Description",id:"size_description"},
			{title:"Words Summary",id:"size_summary"},
			{title:"Days to completion",id:"days_completion"},
			{title:"Experience Resolver",id:"experience_resolver"},
			{title:"Experience Reporter",id:"experience_reporter"}
		];
		addTaskCriteriaPrioritisation(criteria);
		removeListItemIssues();
		AJS.tabs.change(jQuery('a[href="#tab-configuration"]'));
		makeAsyncCalls();
		var sprintsArray;

		function removeListItemIssues() {
			var listItem = document.getElementById("listItemTabIssues");
			if (listItem) {
				listItem.remove();
			}
		}


		function makeAsyncCalls() {

			//load sprints
			var sprintPromise = new Promise(function (resolve, reject) {
				conDecAPI.getSprintsByProject()
					.then(function (sprints) {
						sprintsArray = sprints.map(function (sprint) {
							return sprint.values;
						});
						if (sprintsArray && sprintsArray.length && sprintsArray[0] && sprintsArray[0].length) {
							$('#selectSprints').empty();
							sprintsArray[0].map(function (sprint) {
								$('#selectSprints').append('<option value="' + sprint.id + '">' + sprint.name + '</option>');
							})
						} else {
							disableSprintBox();
						}
						resolve();
					}).catch(function (err) {
					disableSprintBox();
					reject();
				});

			});
			//load issue types
			var issueTypePromise = new Promise(function (resolve, reject) {
				conDecAPI.getIssueTypes()
					.then(function (issueTypes) {
						resolve();
						if (issueTypes && issueTypes.length) {
							issueTypes.map(function (issueType) {
								$('#multipleBugs').append('<option value="' + issueType.id + '">' + issueType.name + '</option>');
								$('#multipleFeatures').append('<option value="' + issueType.id + '">' + issueType.name + '</option>');
								$('#multipleImprovements').append('<option value="' + issueType.id + '">' + issueType.name + '</option>');
							})
						}
					}).catch(function (err) {
					//@todo handle error
					reject();
				});
			});

			Promise.all([sprintPromise,issueTypePromise]).finally(function(){
				//disable busy button
				setButtonBusyAndDisabled(openingButton,false);
				//open dialog

				// Show dialog
				AJS.dialog2(releaseNoteDialog).show();
			})
		}
		function disableSprintBox() {
			$("#useSprint").attr("disabled", true);
			$("#selectSprints").attr("disabled", true);
		}

		function addListItemIssues() {
			var listItem = document.getElementById("listItemTabIssues");
			if (!listItem) {
				$("#tab-list-menu").append('<li class="menu-item" id="listItemTabIssues"><a href="#tab-issues">Issues</a></li>');
			}
		}

		function addTaskCriteriaPrioritisation(listOfCriteria){
			var elementToAppend = $("#taskCriteriaPriority");
			listOfCriteria.map(function (element) {
				elementToAppend.append("<div class='field-group'>" +
					"<label for='" + element.id + "'>" + element.title + "</label>" +
					"<input class='medium-field' type='number' step='0.1' value='1' max='10' min='0' id='" + element.id + "'>" +
					"</div>")
			})
		}
		function setButtonBusyAndDisabled(button,busy){
			if(busy){
				button.busy();
				button.setAttribute('aria-disabled', 'true');
			}else{
				button.idle();
				button.setAttribute('aria-disabled', 'false');
			}
		}


		configurationSubmitButton.onclick = function () {
			var startDate = $("#start-range").val();
			var endDate = $("#final-range").val();
			var useSprints = $("#useSprint").prop("checked");
			var selectedSprint = parseInt($("#selectSprints").val()) || "";
			//first check : both dates have to be selected or a sprint and the checkbox
			if ((!useSprints) && (!!startDate === false || !!endDate === false)) {
				throwAlert("Select Date or Sprint", "The start date and the end date have to be selected, if the sprints are not available or not selected");
				return
			}

			function getStartAndEndDate() {
				var result = {startDate: "", endDate: ""};

				if (useSprints && sprintsArray && sprintsArray.length && sprintsArray[0] && sprintsArray[0].length) {
					//get dates of selected sprint
					var selectedDates = sprintsArray[0].filter(function (sprint) {
						return sprint.id === selectedSprint;
					});
					if (selectedDates && selectedDates.length && selectedDates[0] && selectedDates[0].startDate && selectedDates[0].endDate) {
						var formattedStartDate = formatSprintDate(selectedDates[0].startDate);
						var formattedEndDate = formatSprintDate(selectedDates[0].endDate);
						if (formattedStartDate && formattedEndDate) {
							result.startDate = formattedStartDate;
							result.endDate = formattedEndDate;
						} else {
							throwAlert("An error occured", "Neither a sprint was selected or start dates were filled");
							return false;
						}
					} else {
						throwAlert("An error occured", "Neither a sprint was selected or start dates were filled");
						return false;
					}
				} else if (!!startDate && !!endDate) {
					result.startDate = startDate;
					result.endDate = endDate;
				} else {
					//throw exception
					throwAlert("An error occured", "Neither a sprint was selected or start dates were filled");
					return false;
				}
				return result;
			}

			function formatSprintDate(date) {
				var dateFormat = new Date(date);
				if (typeof dateFormat.getFullYear === "function") {
					var month = dateFormat.getMonth() + 1;
					return dateFormat.getFullYear() + "-" + month + "-" + dateFormat.getDate();
				}
			}
			function getTaskCriteriaPrioritisation(listOfCriteria){
				return listOfCriteria.map(function(element){
					var value=$("#"+element.id).val();
					var key=element.id;
					return {[key]:value};
				})
			}

			function throwAlert(title, message) {
				AJS.flag({
					type: "error",
					close: "auto",
					title: title,
					body: message
				});
			}

			var timeRange = getStartAndEndDate();
			if (!timeRange) {
				return;
			}
			//set button busy and disabled
			this.busy();
			this.setAttribute('aria-disabled', 'true');
			var taskCriteriaPrioritisation = getTaskCriteriaPrioritisation(criteria);
			var targetGroup = $("#selectTargetGroup").val();
			var bugFixes = $("#multipleBugs").val();
			var features = $("#multipleFeatures").val();
			var improvements = $("#multipleImprovements").val();
			//submit configuration
			var configuration = {
				startDate: timeRange.startDate,
				endDate: timeRange.endDate,
				sprintId: selectedSprint,
				targetGroup: targetGroup,
				bugFixMapping: bugFixes,
				featureMapping: features,
				improvementMapping: improvements,
				taskCriteriaPrioritisation: taskCriteriaPrioritisation
			};


			setTimeout(function () {
				//@todo move this down when backend is ready
				//set button idle
				this.idle();
				addListItemIssues();
				//change tab
				AJS.tabs.change(jQuery('a[href="#tab-issues"]'));
				this.setAttribute('aria-disabled', 'false');
			}.bind(this), 3000);

			conDecAPI.getProposedIssues(configuration, function (response) {
				console.log(response);
				//display issues and information
			});

		};


		cancelButton.onclick = function () {
			AJS.dialog2(releaseNoteDialog).hide();
		};

	};

	// export ConDecDialog
	global.conDecDialog = new ConDecDialog();
})(window);
/*
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
 * conDecReleaseNotePage
 */
(function (global) {
    var ConDecDialog = function () {
    };

    ConDecDialog.prototype.showCreateDialog = function (idOfParentElement, documentationLocationOfParentElement, defaultSelectTypeField = "Alternative") {
        console.log("conDecDialog showCreateDialog");
        console.log(idOfParentElement);
        console.log(documentationLocationOfParentElement);
        console.log(defaultSelectTypeField);
        
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
        fillSelectTypeField(selectTypeField, defaultSelectTypeField);
        fillSelectLocationField(selectLocationField, documentationLocationOfParentElement);

        // Set onclick listener on buttons
        submitButton.onclick = function () {
            var summary = inputSummaryField.value;
            var description = inputDescriptionField.value;
            var type = selectTypeField.value;
            var documentationLocation = selectLocationField.value;
            conDecAPI.createDecisionKnowledgeElement(summary, description, type, documentationLocation,
                idOfParentElement, documentationLocationOfParentElement, function (id) {
                    conDecObservable.notify();
                });
            AJS.dialog2(createDialog).hide();
        };

        cancelButton.onclick = function () {
            AJS.dialog2(createDialog).hide();
        };

        // Show dialog
        AJS.dialog2(createDialog).show();
    };

    ConDecDialog.prototype.showAssignDialog = function (sourceId, documentationLocation) {
        console.log("conDecDialog showAssignDialog");

        // HTML elements
        var assignDialog = document.getElementById("assign-dialog");
        var selectLevelField = document.getElementById("assign-form-select-level");
        var example = document.getElementById("example-tag");
        var inputExistingGroupsField = document.getElementById("assign-form-input-existing");
        var inputAddGroupField = document.getElementById("assign-form-input-add");
        var submitButton = document.getElementById("assign-dialog-submit-button");
        var cancelButton = document.getElementById("assign-dialog-cancel-button");

        // Fill HTML elements
        inputAddGroupField.value = "";

        conDecAPI.getDecisionGroups(sourceId, documentationLocation, inputExistingGroupsField, selectLevelField, function (selectLevelField, inputExistingGroupsField, groups) {
            if (!(groups === null) && groups.length > 0) {
                var groupZero = groups[0];
                if ("High_Level" === groupZero) {
                    selectLevelField.innerHTML = "";
                    selectLevelField.insertAdjacentHTML("beforeend", "<option selected  value='High_Level'>High Level</option>" +
                        "<option value='Medium_Level'>Medium Level</option>" +
                        "<option value='Realization_Level'>Realization Level</option>");
                } else if ("Medium_Level" === groupZero) {
                    selectLevelField.innerHTML = "";
                    selectLevelField.insertAdjacentHTML("beforeend", "<option value='High_Level'>High Level</option>" +
                        "<option selected value='Medium_Level'>Medium Level</option>" +
                        "<option value='Realization_Level'>Realization Level</option>");
                } else {
                    selectLevelField.innerHTML = "";
                    selectLevelField.insertAdjacentHTML("beforeend", "<option value='High_Level'>High Level</option>" +
                        "<option value='Medium_Level'>Medium Level</option>" +
                        "<option selected value='Realization_Level'>Realization Level</option>");
                }
            } else {
                selectLevelField.innerHTML = "";
                selectLevelField.insertAdjacentHTML("beforeend", "<option selected  value='High_Level'>High Level</option>" +
                    "<option value='Medium_Level'>Medium Level</option>" +
                    "<option value='Realization_Level'>Realization Level</option>");
            }
            if (groups.length > 1) {
                groups.shift();
                inputExistingGroupsField.value = groups;
            } else {
                inputExistingGroupsField.value = "";
            }

        });

        // Set onclick listener on buttons
        submitButton.onclick = function () {
            var level = selectLevelField.value;
            var existingGroups = inputExistingGroupsField.value;
            var addgroup = inputAddGroupField.value;
            conDecAPI.assignDecisionGroup(level, existingGroups, addgroup,
                sourceId, documentationLocation, function (id) {
                    conDecObservable.notify();
                });
            AJS.dialog2(assignDialog).hide();
        };
        
        cancelButton.onclick = function () {
            AJS.dialog2(assignDialog).hide();
        };

        // Show dialog
        AJS.dialog2(assignDialog).show();

    };

    ConDecDialog.prototype.showRenameGroupDialog = function (groupName) {
        console.log("conDecDialog showRenameGroupDialog");
        // HTML elements
        var renameGroupDialog = document.getElementById("rename-group-dialog");
        if (groupName === "High_Level" || groupName === "Medium_Level" || groupName === "Realization_Level") {
            alert("Can't rename Decision Levels.");
            AJS.dialog2(renameGroupDialog).hide();
        } else {
            var inputGroupName = document.getElementById("rename-group-input");
            var submitButton = document.getElementById("rename-group-dialog-submit-button");
            var cancelButton = document.getElementById("rename-group-dialog-cancel-button");

            inputGroupName.value = groupName;

            // Set onclick listener on buttons
            submitButton.onclick = function () {
                var newName = inputGroupName.value;
                conDecAPI.renameDecisionGroup(groupName, newName, function () {
                    conDecObservable.notify();
                });
                AJS.dialog2(renameGroupDialog).hide();
            };

            cancelButton.onclick = function () {
                AJS.dialog2(renameGroupDialog).hide();
            };

            // Show dialog
            AJS.dialog2(renameGroupDialog).show();
        }
    };

    ConDecDialog.prototype.showDeleteGroupDialog = function (groupName) {
        console.log("conDecDialog showDeleteGroupDialog");
        // HTML elements
        var deleteGroupDialog = document.getElementById("delete-group-dialog");
        if (groupName === "High_Level" || groupName === "Medium_Level" || groupName === "Realization_Level") {
            alert("Can't delete Decision Levels.");
            AJS.dialog2(deleteGroupDialog).hide();
        } else {
            var deleteMessageLabel = document.getElementById("delete-group-label");
            var submitButton = document.getElementById("delete-group-dialog-submit-button");
            var cancelButton = document.getElementById("delete-group-dialog-cancel-button");

            deleteMessageLabel.innerHTML = "<br> Are you sure that you want to remove the Decision Group: <b>" + groupName + "</b> ?";

            // Set onclick listener on buttons
            submitButton.onclick = function () {
                conDecAPI.deleteDecisionGroup(groupName, function () {
                    conDecObservable.notify();
                });
                AJS.dialog2(deleteGroupDialog).hide();
            };

            cancelButton.onclick = function () {
                AJS.dialog2(deleteGroupDialog).hide();
            };

            // Show dialog
            AJS.dialog2(deleteGroupDialog).show();
        }
    };

    ConDecDialog.prototype.showDeleteDialog = function (id, documentationLocation) {
        console.log("conDecDialog showDeleteDialog");

        // HTML elements
        var deleteDialog = document.getElementById("delete-dialog");
        var content = document.getElementById("delete-dialog-content");
        var submitButton = document.getElementById("delete-dialog-submit-button");
        var cancelButton = document.getElementById("delete-dialog-cancel-button");

        // Set onclick listener on buttons
        submitButton.onclick = function () {
            conDecAPI.deleteDecisionKnowledgeElement(id, documentationLocation, function () {
                conDecObservable.notify();
            });
            AJS.dialog2(deleteDialog).hide();
        };

        cancelButton.onclick = function () {
            AJS.dialog2(deleteDialog).hide();
        };

        // Show dialog
        AJS.dialog2(deleteDialog).show();
    };

    ConDecDialog.prototype.showDeleteLinkDialog = function (id, documentationLocation, idOfParent, documentationLocationOfParent) {
        console.log("conDecDialog showDeleteLinkDialog");

        // HTML elements
        var deleteLinkDialog = document.getElementById("delete-link-dialog");
        var content = document.getElementById("delete-link-dialog-content");
        var submitButton = document.getElementById("delete-link-dialog-submit-button");
        var cancelButton = document.getElementById("delete-link-dialog-cancel-button");

        // Set onclick listener on buttons
        submitButton.onclick = function () {
            if (idOfParent === null || idOfParent === undefined || idOfParent === 0) {
                var parentElement = conDecTreant.findParentElement(id);
                idOfParent = parentElement["id"];
                documentationLocationOfParent = parentElement["documentationLocation"];
            }
            conDecAPI.deleteLink(idOfParent, id, documentationLocationOfParent,
                documentationLocation, function () {
                    conDecObservable.notify();
                });
            AJS.dialog2(deleteLinkDialog).hide();
        };

        cancelButton.onclick = function () {
            AJS.dialog2(deleteLinkDialog).hide();
        };

        // Show dialog
        AJS.dialog2(deleteLinkDialog).show();
    };

    ConDecDialog.prototype.showLinkDialog = function (id, documentationLocation, idOfTarget, documentationLocationOfTarget) {
        console.log("conDecDialog showLinkDialog");

        // HTML elements
        var linkDialog = document.getElementById("link-dialog");
        var selectElementField = document.getElementById("link-form-select-element");
        var selectLinkTypeField = document.getElementById("link-form-select-linktype");
        var submitButton = document.getElementById("link-dialog-submit-button");
        var cancelButton = document.getElementById("link-dialog-cancel-button");
        
        fillSelectElementField(selectElementField, id, documentationLocation, idOfTarget, documentationLocationOfTarget);     
        fillSelectLinkTypeField(selectLinkTypeField, id, documentationLocation);

        selectElementField.onchange = function () {
        	var idOfSelectedElement = this.value.split(":")[0];
        	var documentationLocationOfSelectedElement = this.value.split(":")[1];
            conDecAPI.getDecisionKnowledgeElement(idOfSelectedElement, documentationLocationOfSelectedElement, 
            		function (element) {
                if (element.type === "Argument" || element.type === "Pro") {
                	$("#link-form-select-linktype").val("Supports");                
                }
                if (element.type === "Con") {
                	$("#link-form-select-linktype").val("Attacks");                
                }
            });
        };

        // Set onclick listener on buttons
        submitButton.onclick = function () {
            var idOfChild = selectElementField.value.split(":")[0];
            var documentationLocationOfChild = selectElementField.value.split(":")[1];
            var linkType = selectLinkTypeField.value;
            conDecAPI.createLink(null, id, idOfChild, documentationLocation, documentationLocationOfChild, 
            		linkType, function () {conDecObservable.notify()});
            AJS.dialog2(linkDialog).hide();
        };

        cancelButton.onclick = function () {
            AJS.dialog2(linkDialog).hide();
        };

        // Show dialog
        AJS.dialog2(linkDialog).show();
    };

    function fillSelectElementField(selectField, id, documentationLocation, idOfTarget, documentationLocationOfTarget) {
        if (selectField === null) {
            return;
        }
        selectField.innerHTML = "";
        conDecAPI.getUnlinkedElements(id, documentationLocation, function (unlinkedElements) {
            var insertString = "";
            for (var index = 0; index < unlinkedElements.length; index++) {
                insertString += "<option value='" + unlinkedElements[index].id + ":" 
                	+ unlinkedElements[index].documentationLocation + "'>"
                    + unlinkedElements[index].type + ' / ' + unlinkedElements[index].summary + "</option>";
            }
            selectField.insertAdjacentHTML("afterBegin", insertString);
            
            if (idOfTarget !== undefined && documentationLocationOfTarget !== undefined) {
            	console.log("Target element provided");
                $("#link-form-select-element").val(idOfTarget + ":" + documentationLocationOfTarget);
            }
        });
        AJS.$(selectField).auiSelect2();
    }

    function fillSelectLinkTypeField(selectField, id, documentationLocation) {
        if (selectField === null) {
            return;
        }
        selectField.innerHTML = "";
        /*
		 * NOTE! Instead the Jira API could be called using GET
		 * "/rest/api/2/issueLinkType". This call "[r]eturns a list of available
		 * issue link types, if issue linking is enabled. Each issue link type
		 * has an id, a name and a label for the outward and inward link
		 * relationship."
		 * 
		 * @see https://docs.atlassian.com/software/jira/docs/api/REST/8.5.4/#api/2/issueLinkType-getIssueLinkTypes
		 */
        var linkTypes = conDecAPI.getLinkTypes();
        var linkTypeNames = Object.keys(linkTypes);
        var insertString = "";
        var isSelected = "";
        for (var index in linkTypeNames) {
            if (linkTypeNames[index] == "Relates") {
                isSelected = "selected";
            } else {
                isSelected = "";
            }
            console.log(linkTypeNames[index]);
            insertString += "<option " + isSelected + " value='" + linkTypeNames[index] + "'>"
                + linkTypeNames[index] + "</option>";
        }
        selectField.insertAdjacentHTML("afterBegin", insertString);
        AJS.$(selectField).auiSelect2();
    }

    ConDecDialog.prototype.showEditDialog = function (id, documentationLocation, type) {
        console.log("conDecDialog showEditDialog");

        conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function (decisionKnowledgeElement) {
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
            var selectLocationField = document.getElementById("edit-form-select-location");
            var submitButton = document.getElementById("edit-dialog-submit-button");
            var cancelButton = document.getElementById("edit-dialog-cancel-button");

            var selectLevelField = document.getElementById("edit-form-select-level");
            var inputExistingGroupsField = document.getElementById("edit-form-input-existing");

            // Fill HTML elements
            inputSummaryField.value = summary;
            inputDescriptionField.value = description;
            fillSelectTypeField(selectTypeField, type);
            fillSelectLocationField(selectLocationField, documentationLocation);
            if (documentationLocation === "s") {
                inputSummaryField.disabled = true;
                selectLocationField.disabled = true;
            }
            conDecAPI.getDecisionGroups(id, documentationLocation, inputExistingGroupsField, selectLevelField, function (selectLevelField, inputExistingGroupsField, groups) {
                if (!(groups === null) && groups.length > 0) {
                    var groupZero = groups[0];
                    if ("High_Level" === groupZero) {
                        selectLevelField.innerHTML = "";
                        selectLevelField.insertAdjacentHTML("beforeend", "<option selected  value='High_Level'>High Level</option>" +
                            "<option value='Medium_Level'>Medium Level</option>" +
                            "<option value='Realization_Level'>Realization Level</option>");
                    } else if ("Medium_Level" === groupZero) {
                        selectLevelField.innerHTML = "";
                        selectLevelField.insertAdjacentHTML("beforeend", "<option value='High_Level'>High Level</option>" +
                            "<option selected value='Medium_Level'>Medium Level</option>" +
                            "<option value='Realization_Level'>Realization Level</option>");
                    } else {
                        selectLevelField.innerHTML = "";
                        selectLevelField.insertAdjacentHTML("beforeend", "<option value='High_Level'>High Level</option>" +
                            "<option value='Medium_Level'>Medium Level</option>" +
                            "<option selected value='Realization_Level'>Realization Level</option>");
                    }
                } else {
                    selectLevelField.innerHTML = "";
                    selectLevelField.insertAdjacentHTML("beforeend", "<option selected  value='High_Level'>High Level</option>" +
                        "<option value='Medium_Level'>Medium Level</option>" +
                        "<option value='Realization_Level'>Realization Level</option>");
                }
                if (groups.length > 1) {
                    groups.shift();
                    inputExistingGroupsField.value = groups;
                } else {
                    inputExistingGroupsField.value = "";
                }

            });

            // Set onclick listener on buttons
            submitButton.onclick = function () {
                var summary = inputSummaryField.value;
                var description = inputDescriptionField.value;
                var type = selectTypeField.value;
                conDecAPI.updateDecisionKnowledgeElement(id, summary, description, type, documentationLocation, null,
                    function () {
                        conDecObservable.notify();
                    });
                var level = selectLevelField.value;
                var existingGroups = inputExistingGroupsField.value;
                var addgroup = "";
                conDecAPI.assignDecisionGroup(level, existingGroups, addgroup,
                    id, documentationLocation, function (id) {
                        conDecObservable.notify();
                    });
                AJS.dialog2(editDialog).hide();
            };

            cancelButton.onclick = function () {
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
        var extendedKnowledgeTypes = conDecAPI.getExtendedKnowledgeTypes();
        console.log(extendedKnowledgeTypes, selectedKnowledgeType);
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
        return knowledgeType.toLowerCase() === extendedKnowledgeTypes[index].toLowerCase();
    }

    function fillSelectLocationField(selectField, documentationLocationOfParentElement) {
        if (selectField === null) {
            return;
        }
        selectField.innerHTML = "";
        conDecAPI.isIssueStrategy(function (isEnabled) {
            if (documentationLocationOfParentElement !== null) {
                selectField.insertAdjacentHTML("beforeend", "<option selected value = 's'>Jira issue comment</option>");
            }
            if (isEnabled) {
                selectField.insertAdjacentHTML("beforeend", "<option value = 'i'>Jira issue</option>");
            }
        });
    }

    ConDecDialog.prototype.showChangeTypeDialog = function (id, documentationLocation) {
        console.log("conDecDialog showChangeTypeDialog");

        // HTML elements
        var changeTypeDialog = document.getElementById("change-type-dialog");
        var selectTypeField = document.getElementById("change-type-form-select-type");
        var submitButton = document.getElementById("change-type-dialog-submit-button");
        var cancelButton = document.getElementById("change-type-dialog-cancel-button");

        // Fill HTML elements
        conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function (decisionKnowledgeElement) {
            fillSelectTypeField(selectTypeField, decisionKnowledgeElement.type);
        });

        // Set onclick listener on buttons
        submitButton.onclick = function () {
            var type = selectTypeField.value;
            conDecAPI.changeKnowledgeType(id, type, documentationLocation, function () {
                conDecObservable.notify();
            });
            AJS.dialog2(changeTypeDialog).hide();
        };

        cancelButton.onclick = function () {
            AJS.dialog2(changeTypeDialog).hide();
        };

        // Show dialog
        AJS.dialog2(changeTypeDialog).show();
    };

    ConDecDialog.prototype.showChangeStatusDialog = function (id, documentationLocation) {
        console.log("conDecDialog showChangeStatusDialog");

        // HTML elements
        var changeStatusDialog = document.getElementById("change-status-dialog");
        var selectStatusField = document.getElementById("change-status-form-select-type");
        var submitButton = document.getElementById("change-status-dialog-submit-button");
        var cancelButton = document.getElementById("change-status-dialog-cancel-button");

        // Fill HTML elements
        conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function (decisionKnowledgeElement) {
            fillSelectStatusField(selectStatusField, decisionKnowledgeElement);

            // Set onclick listener on buttons
            submitButton.onclick = function () {
                var status = selectStatusField.value;
                conDecAPI.setStatus(id, documentationLocation, decisionKnowledgeElement.type, status, function () {
                    conDecObservable.notify();
                });
                AJS.dialog2(changeStatusDialog).hide();
            };
        });

        cancelButton.onclick = function () {
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
        if (element.type === "Issue") {
            knowledgeStatus = conDecAPI.issueStatus;
        } else if (element.type === "Decision") {
            knowledgeStatus = conDecAPI.decisionStatus;
        } else {
        	knowledgeStatus = conDecAPI.alternativeStatus;
		}
        selectField.innerHTML = "";
        for (var index = 0; index < knowledgeStatus.length; index++) {
            var isSelected = "";
            console.log(element.status);
            if (element.status.toUpperCase() === knowledgeStatus[index].toUpperCase()) {
                isSelected = "selected";
            }
            selectField.insertAdjacentHTML("beforeend", "<option " + isSelected + " value='"
                + knowledgeStatus[index] + "'>" + knowledgeStatus[index] + "</option>");
        }
        AJS.$(selectField).auiSelect2();
    }

    ConDecDialog.prototype.showSummarizedDialog = function (id, documentationLocation) {
        // HTML elements
        var summarizedDialog = document.getElementById("summarization-dialog");
        var cancelButton = document.getElementById("summarization-dialog-cancel-button");
        var content = document.getElementById("summarization-dialog-content");
        var probabilityOfCorrectness = document.getElementById("summarization-probabilityOfCorrectness").valueAsNumber;
        var summarizationId = document.getElementById("summarization-id").value;
        if (summarizationId === undefined || summarizationId.length === 0 || summarizationId === "") {
            document.getElementById("summarization-id").value = id;
            summarizationId = id;
        }
        conDecAPI.getSummarizedCode(summarizationId, documentationLocation, probabilityOfCorrectness, function (text) {
            var insertString = "<form class='aui'>" + "<div>" + text + "</div>" + "</form>";
            content.innerHTML = insertString;
        });

        cancelButton.onclick = function () {
            AJS.dialog2(summarizedDialog).hide();
        };

        // Show dialog
        AJS.dialog2(summarizedDialog).show();
    };

	ConDecDialog.prototype.showAddCriterionToDecisionTableDialog = function (projectKey, currentCriteria, callback) {
		let addCriterionDialog = document.getElementById("decisionTableCriteriaDialog");
		let closeButton = document.getElementById("dialog-apply-button");
		let exitButton = document.getElementById("dialog-exit-button");
		
        let changes = new Map();
		let allCriteria;
        let uniqueCriteria;
        
		conDecAPI.getDecisionTableCriteria(function (criteria) {
			allCriteria = criteria.concat(currentCriteria);
			uniqueCriteria = new Set(allCriteria.map(item => item.id));
			createDialogContent(criteria, currentCriteria, projectKey);
		});
        
        function createDialogContent(criteria, currentCriteria, projectKey) {
        	let tableBody = document.getElementById("table-body");
        	let queryReference = document.getElementById("decisionTableCriteriaQueryReference");
        	tableBody.innerHTML = "";
        	queryReference.innerHTML = "";
        	
        	for (let criterion of uniqueCriteria) {
        		tableBody.innerHTML += `<tr id="bodyRowCriteria${criterion}"></tr>`;
        		let rowElement = document.getElementById(`bodyRowCriteria${criterion}`);
           		let checked = currentCriteria.find(item => item.id === criterion) ? "checked" : "";
   
        		rowElement.innerHTML += `<td headers="basic-number">
        			<div class="checkbox">
        				<input id="ckb${criterion}" class="checkbox" type="checkbox" name="ckbCriterion" id="checkBoxOne" ${checked}>
        			</div>
        			</td>
        			<td headers="basic-fname">${allCriteria.find(item => item.id === criterion).summary}</td>`;
        	}
        	
        	queryReference.innerHTML = `<div>Available criteria are fetched from decision table criteria query from 
        		<a href="../../../plugins/servlet/condec/settings?projectKey=${projectKey}&category=rationaleModel">
        		rationale model settings page</a>.</div>`;
        	
        	addCheckboxEventListener();
        }
        
        function addCheckboxEventListener() {
        	let checkboxes = document.querySelectorAll("input[type=checkbox][class=checkbox][name=ckbCriterion]");
        	for (let checkbox of checkboxes) {
        		checkbox.addEventListener("change", function () {
        			let tmpCriterionId = this.id.replace("ckb", "");
        			let isInCurrentCriteria = currentCriteria.find(item => item.id == tmpCriterionId) ? true : false;
        			
        			if (!this.checked && isInCurrentCriteria) {
        				changes.set(tmpCriterionId, 
        				{"status": this.checked, "criterion": allCriteria.find(item => item.id == tmpCriterionId)});
        			} else if (this.checked && isInCurrentCriteria && changes.has(tmpCriterionId)) {
        				changes.delete(tmpCriterionId);
        			} else if (changes.has(tmpCriterionId)) {
        				changes.delete(tmpCriterionId);
        			} else {
        				changes.set(tmpCriterionId, 
        				{"status": this.checked, "criterion": allCriteria.find(item => item.id == tmpCriterionId)});
        			}
        		});
        	}
        }
        
        // Show dialog
        AJS.dialog2(addCriterionDialog).show();
        
        // send callback when dialog was closed not via apply or close button
		//AJS.dialog2(addCriterionDialog).on("hide", removeDialogHideListener());

        exitButton.onclick = function () {
        	applyChanges([]);
			AJS.dialog2(addCriterionDialog).hide();
        }
        
		closeButton.onclick = function () {
			applyChanges(changes);
			AJS.dialog2(addCriterionDialog).hide();
		}
				
		function removeDialogHideListener() {
			applyChanges([]);
        	addCriterionDialog.removeEventListener("hide", removeDialogHideListener);
		}
		
		function applyChanges(changes) {
			callback(changes);
		}
	}
	
    ConDecDialog.prototype.showCreateReleaseNoteDialog = function () {
        // HTML elements
        // set button busy before we show the dialog
        var openingButton = document.getElementById("openCreateReleaseNoteDialogButton");
        setButtonBusyAndDisabled(openingButton, true);
        var releaseNoteDialog = document.getElementById("create-release-note-dialog");
        var cancelButton = document.getElementById("create-release-note-dialog-cancel-button");
        var configurationSubmitButton = document.getElementById("create-release-note-submit-button");
        var issueSelectSubmitButton = document.getElementById("create-release-note-submit-issues-button");
        var saveContentButton = document.getElementById("create-release-note-submit-content");
        var loader = document.getElementById("createReleaseNoteDialogLoader");
        var useSprintSelect = document.getElementById("useSprint");
        var titleInput = document.getElementById("title");
        var sprintOptions = document.getElementById("selectSprints");
        var useReleaseSelect = document.getElementById("useReleases");
        var releaseOptions = document.getElementById("selectReleases");

        var titleWasChanged = false;
        var editor;
        var firstResultObject = {};

        AJS.tabs.setup();

        // add task prioritisation
        var criteria = [
            {title: "#Decision Knowledge", id: "count_decision_knowledge"},
            {title: "Priority", id: "priority"},
            {title: "#Comments", id: "count_comments"},
            {title: "Words Description", id: "size_description"},
            {title: "Words Summary", id: "size_summary"},
            {title: "Days to completion", id: "days_completion"},
            {title: "Experience Resolver", id: "experience_resolver"},
            {title: "Experience Reporter", id: "experience_reporter"}
        ];

        var targetGroupMapping = [
            {id: "DEVELOPER", title: "Developer", includes: ["include_decision_knowledge", "include_bug_fixes"]},
            {id: "TESTER", title: "Tester", includes: ["include_bug_fixes", "include_test_instructions"]},
            {id: "ENDUSER", title: "Enduser", includes: []}
        ];

        var softwareTypeMapping =
            [
                {includes: [], title: "Simple website", id: "simple_website"},
                {includes: ["include_breaking_changes", "include_extra_link"], title: "Framework", id: "framework"},
                {includes: ["include_upgrade_guide"], title: "Installable software", id: "software"},
                {includes: ["include_breaking_changes"], title: "API", id: "api"}
            ];

        var allTargetGroupIncludes = ["include_decision_knowledge", "include_bug_fixes", "include_test_instructions"];
        var allSoftwareTypeIncludes = ["include_breaking_changes", "include_extra_link", "include_upgrade_guide"];

        addjiraIssueMetric(criteria);
        removeListItemIssues();
        AJS.tabs.change(jQuery('a[href="#tab-configuration"]'));
        makeAsyncCalls();
        fillSoftwaretypesAndTargetGroups();
        var sprintsArray;
        var releasesArray = [];
        removeEditor();

        function removeEditor() {
            var editorDiv = document.getElementById("create-release-note-dialog-contain-editor");
            editorDiv.parentNode.removeChild(editorDiv);
            $("#create-release-note-dialog-contain-editor-content").append("<div id='create-release-note-dialog-contain-editor'>" +
                "<textarea id='create-release-note-textarea'></textarea></div>")
        }

        function removeListItemIssues() {
            var listItem = document.getElementById("listItemTabIssues");
            if (listItem) {
                listItem.remove();
            }
        }

        function prefillDateBox() {
            var today = new Date();
            var twoWeeksAgo = new Date(today.getTime() - 12096e5);
            var todayString = today.getFullYear() + '-' + ('0' + (today.getMonth() + 1)).slice(-2) + '-' + ('0' + today.getDate()).slice(-2);
            var twoWeeksAgoString = twoWeeksAgo.getFullYear() + '-' + ('0' + (twoWeeksAgo.getMonth() + 1)).slice(-2) + '-' + ('0' + twoWeeksAgo.getDate()).slice(-2);
            document.getElementById("start-range").value = twoWeeksAgoString;
            document.getElementById("final-range").value = todayString;
        }

        function fillSoftwaretypesAndTargetGroups() {
            $("#selectSoftwareType").empty();
            $("#selectTargetGroup").empty();
            softwareTypeMapping.map(function (type) {
                $("#selectSoftwareType").append("<option value='" + type.id + "'>" + type.title + "</option>")
            });
            targetGroupMapping.map(function (targetGroup) {
                $("#selectTargetGroup").append("<option value='" + targetGroup.id + "'>" + targetGroup.title + "</option>")
            });
            document.getElementById("selectSoftwareType").onchange = onSoftwareTypeChange;
            document.getElementById("selectTargetGroup").onchange = onTargetGroupChange;
            onSoftwareTypeChange();
            onTargetGroupChange();
        }

        function onTargetGroupChange() {
            var selectedGroup = $("#selectTargetGroup").val();

            var foundGroup = targetGroupMapping.filter(function (targetGroup) {
                return targetGroup.id === selectedGroup
            });
            allTargetGroupIncludes.map(function (include) {
                if (foundGroup[0].includes.indexOf(include) > -1) {
                    $("#" + include).prop("checked", true);
                } else {
                    $("#" + include).prop("checked", false);
                }
            })
        }

        function onSoftwareTypeChange() {
            var selectedType = $("#selectSoftwareType").val();
            var foundType = softwareTypeMapping.filter(function (type) {
                return type.id === selectedType
            });
            allSoftwareTypeIncludes.map(function (include) {
                if (foundType[0].includes.indexOf(include) > -1) {
                    $("#" + include).prop("checked", true);
                } else {
                    $("#" + include).prop("checked", false);
                }
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

        function makeAsyncCalls() {
            // load sprints
            var sprintPromise = new Promise(function (resolve, reject) {
                conDecAPI.getSprintsByProject()
                    .then(function (sprints) {
                        var hasValidSprints = false;
                        sprintsArray = sprints.map(function (sprint) {
                            return sprint.values;
                        });
                        if (sprintsArray && sprintsArray.length && sprintsArray[0] && sprintsArray[0].length) {
                            $('#selectSprints').empty();
                            sprintsArray[0].map(function (sprint) {
                                if (sprint && sprint.startDate && sprint.endDate) {
                                    hasValidSprints = true;
                                    $('#selectSprints').append('<option class="sprint-option" value="' + sprint.id + '">' + sprint.name + '</option>');
                                } else {
                                    $('#selectSprints').append('<option class="sprint-option" disabled value="' + sprint.id + '">' + sprint.name + '</option>');
                                }
                            });
                        }
                        if (hasValidSprints) {
                            resolve();
                        } else {
                            reject("No valid Sprints found");
                        }
                    }).catch(function (err) {
                    reject(err);
                });
            }).catch(function (err) {
                disableSprintBox();
                throwAlert("No sprints could be loaded", err);
            });
            // load issue types
            var issueTypePromise = new Promise(function (resolve, reject) {
                conDecAPI.getIssueTypes()
                    .then(function (issueTypes) {
                        conDecAPI.getProjectWideSelectedIssueTypes().then(function (preSelectedIssueTypes) {
                            resolve({issueTypes: issueTypes, preSelectedIssueTypes: preSelectedIssueTypes});
                        }).catch(function () {
                            resolve({issueTypes: issueTypes, preSelectedIssueTypes: null});
                        });
                    }).catch(function (err) {
                    reject(err);
                });
            }).then(function (values) {
                // set issue types
                var issueTypes = values.issueTypes;
                var preSelectedIssueTypes = values.preSelectedIssueTypes;
                manageIssueTypes(issueTypes, preSelectedIssueTypes);
            }).catch(function (err) {
                throwAlert("No issue-types could be loaded", "This won't be working without Jira-Issues associated to a project: " + err);
            });

            var releasesPromise = new Promise(function (resolve, reject) {
                conDecAPI.getReleases().then(function (releases) {
                    var hasValidReleases = false;
                    var releaseSelector = $('#selectReleases');
                    releaseSelector.empty();
                    releases.map(function (release) {
                        if (release && release.startDate.iso && release.releaseDate.iso) {
                            hasValidReleases = true;
                            releaseSelector.append('<option value="' + release.id + '">' + release.name + '</option>');
                            releasesArray.push(release);
                        } else {
                            releaseSelector.append('<option disabled value="' + release.id + '">' + release.name + '</option>');
                        }
                    });
                    if (!hasValidReleases) {
                        disableReleaseBox();
                    }
                    resolve();
                }).catch(function (err) {
                    disableReleaseBox();
                    reject(err);
                })
            }).catch(function (err) {
                throwAlert("Loading the Releases went wrong", err)
            });


            Promise.all([sprintPromise, issueTypePromise, releasesPromise])
                .finally(function () {
                    // disable busy button
                    setButtonBusyAndDisabled(openingButton, false);
                    // Show dialog
                    AJS.dialog2(releaseNoteDialog).show();
                    prefillDateBox();
                })
        }

        function manageIssueTypes(issueTypes, preSelectedIssueTypes) {
            if (issueTypes && issueTypes.length) {
                // empty lists
                var bugSelector = $("#multipleBugs");
                var featureSelector = $("#multipleFeatures");
                var improvementSelector = $("#multipleImprovements");
                bugSelector.empty();
                featureSelector.empty();
                improvementSelector.empty();
                console.log(preSelectedIssueTypes);
                issueTypes.map(function (issueType) {
                    var bugSelected = false;
                    var bugString = '<option value="' + issueType.id + '"';
                    var featureSelected = false;
                    var featureString = '<option value="' + issueType.id + '"';
                    var improvementSelected = false;
                    var improvementString = '<option value="' + issueType.id + '"';
                    if (preSelectedIssueTypes) {
                        if (preSelectedIssueTypes.bug_fixes) {
                            bugSelected = preSelectedIssueTypes.bug_fixes.indexOf(issueType.name) > -1;
                        }
                        if (preSelectedIssueTypes.new_features) {
                            featureSelected = preSelectedIssueTypes.new_features.indexOf(issueType.name) > -1;
                        }
                        if (preSelectedIssueTypes.improvements) {
                            improvementSelected = preSelectedIssueTypes.improvements.indexOf(issueType.name) > -1;
                        }
                    }
                    if (bugSelected) {
                        bugString += "selected";
                    }
                    if (featureSelected) {
                        featureString += "selected";
                    }
                    if (improvementSelected) {
                        improvementString += "selected";
                    }
                    bugSelector.append(bugString + '>' + issueType.name + '</option>');
                    featureSelector.append(featureString + '>' + issueType.name + '</option>');
                    improvementSelector.append(improvementString + '>' + issueType.name + '</option>');
                })
            }
        }

        function disableSprintBox() {
            $("#useSprint").attr("disabled", true);
            $("#selectSprints").attr("disabled", true);
        }

        function disableReleaseBox() {
            $("#useReleases").attr("disabled", true);
            $("#selectReleases").attr("disabled", true);
        }

        function addTabAndChangeToIt(tabId, title) {
            var listItem = document.getElementById("listItemTab" + tabId);
            if (!listItem) {
                $("#tab-list-menu").append("<li class='menu-item' id='listItemTab" + tabId + "'><a href='#" + tabId + "'>" + title + "</a></li>");
            }
            AJS.tabs.setup();
            AJS.tabs.change(jQuery('a[href="#' + tabId + '"]'));


        }

        function addjiraIssueMetric(listOfCriteria) {
            var elementToAppend = $("#metricWeight");
            // first empty list
            elementToAppend.empty();
            elementToAppend.append("<form class='aui'>")
            listOfCriteria.map(function (element) {
                elementToAppend.append("<div class='field-group'>" +
                    "<label for='" + element.id + "'>" + element.title + "</label>" +
                    "<input class='medium-field' type='number' value='1' max='10' min='0' id='" + element.id + "'>" +
                    "</div>")
            });
            elementToAppend.append("</form>")
        }

        function setButtonBusyAndDisabled(button, busy) {
            if (busy) {
                button.busy();
                button.setAttribute('aria-disabled', 'true');
            } else {
                button.idle();
                button.setAttribute('aria-disabled', 'false');
            }
        }

        useSprintSelect.onchange = function () {
            setSprintOrReleaseOption("selectSprints");
        };
        sprintOptions.onchange = function () {
            setSprintOrReleaseOption("selectSprints");
        };
        useReleaseSelect.onchange = function () {
            setSprintOrReleaseOption("selectReleases")
        };
        releaseOptions.onchange = function () {
            setSprintOrReleaseOption("selectReleases")
        };

        function setSprintOrReleaseOption(selectId) {
            if (!titleWasChanged) {
                var options = document.getElementById(selectId).options;
                for (var i = 0; i < options.length; i++) {
                    if (options[i].selected) {
                        titleInput.value = options[i].innerText;
                    }
                }
            }
        }

        titleInput.onchange = function () {
            titleWasChanged = titleInput.value;
        };

        configurationSubmitButton.onclick = function () {
            var startDate = $("#start-range").val();
            var endDate = $("#final-range").val();
            var useSprints = $("#useSprint").prop("checked");
            var useReleases = $("#useReleases").prop("checked");
            var selectedSprint = parseInt($("#selectSprints").val()) || "";
            var selectedRelease = parseInt($("#selectReleases").val()) || "";
            // first check : both dates have to be selected or a sprint or
            // releases and the checkbox
            if ((!useSprints) && (!useReleases) && (!!startDate === false || !!endDate === false)) {
                throwAlert("Select Date or Sprint or Release", "The start date and the end date have to be selected, if the sprints or releases are not available or not selected");
                return
            }

            function getStartAndEndDate() {
                var result = {startDate: "", endDate: ""};
                if (useReleases && releasesArray && releasesArray.length) {
                    var selectedDates = releasesArray.filter(function (release) {
                        return release.id = selectedRelease;
                    });
                    if (selectedDates && selectedDates.length) {
                        result.startDate = selectedDates[0].startDate.iso;
                        result.endDate = selectedDates[0].releaseDate.iso;
                    } else {
                        throwAlert("An error occured", "Something went wrong with the release selection");
                        return false;
                    }
                } else if (useSprints && sprintsArray && sprintsArray.length && sprintsArray[0] && sprintsArray[0].length) {
                    // get dates of selected sprint
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
                    // throw exception
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

            function getjiraIssueMetric(listOfCriteria) {
                var result = {};
                listOfCriteria.map(function (element) {
                    var value = $("#" + element.id).val();
                    var key = element.id.toUpperCase();
                    result[key] = value;
                });
                return result;
            }


            var additionalConfiguration = {};

            function getAdditionalConfiguration() {
                $(".advancedOptionalConfiguration").each(function (i) {
                    var item = ($(".advancedOptionalConfiguration").get(i));
                    var name = item.name;
                    var isChecked = item.checked;
                    additionalConfiguration[name.toUpperCase()] = isChecked;
                })
            }

            var timeRange = getStartAndEndDate();
            if (!timeRange) {
                return;
            }
            // set button busy and disabled
            setButtonBusyAndDisabled(configurationSubmitButton, true);
            getAdditionalConfiguration();
            var jiraIssueMetric = getjiraIssueMetric(criteria);
            var targetGroup = $("#selectTargetGroup").val();
            var bugFixes = $("#multipleBugs").val();
            var features = $("#multipleFeatures").val();
            var improvements = $("#multipleImprovements").val();
            var title = $("#title").val();
            // submit configuration
            var configuration = {
                title: title,
                startDate: timeRange.startDate,
                endDate: timeRange.endDate,
                sprintId: selectedSprint,
                targetGroup: targetGroup,
                bugFixMapping: bugFixes,
                featureMapping: features,
                improvementMapping: improvements,
                additionalConfiguration: additionalConfiguration,
                jiraIssueMetric: jiraIssueMetric
            };

            conDecAPI.getProposedIssues(configuration).then(function (response) {

                if (response) {
                    // change tab
                    addTabAndChangeToIt("tab-issues", "Suggested Issues");
                    console.log(response);

                    firstResultObject = response;
                    // display issues and information
                    if (response.proposals) {
                        showTables(response.proposals);
                    }
                    if (response.title) {
                        showTitle(response.title)
                    }
                }

            }).catch(function (err) {
                // we handle this exception directly in condec.api
            }).finally(function () {
                // set button idle
                setButtonBusyAndDisabled(configurationSubmitButton, false);
            });

            function showTables(response) {
                // first remove old tables
                $("#displayIssueTables").empty();
                Object.keys(response).map(function (category) {
                    if (response[category] && response[category].length) {
                        showTable(category, response[category]);
                    }
                })
            }

            function showTitle(title) {
                $("#suggestedIssuesTitle").text("Suggested Issues for Release Notes: " + title);
            }

            function showTable(category, issues) {
                var mapCategoryToTitles = {
                    "bug_fixes": "Bug Fixes",
                    "new_features": "New Features",
                    "improvements": "Improvements"
                };
                var divToAppend = $("#displayIssueTables");
                var title = "<h2>" + mapCategoryToTitles[category] + "</h2>";
                var table = "<table class='aui'><thead><tr>" +
                    "<th>Include</th>" +
                    "<th>Relevance-Rating</th>" +
                    "<th>Key</th>" +
                    "<th>Summary</th>" +
                    "<th>Type</th>" +
                    "</tr></thead>";
                var tableRows = "";
                issues.map(function (issue) {
                    var expander = "<div id='expanderOfRating_" + category + issue.decisionKnowledgeElement.key + "' class='aui-expander-content'>" +
                        "<ul class='noDots'>" +
                        "<li>#Comments: " + issue.jiraIssueMetrics.COUNT_COMMENTS + "</li>" +
                        "<li>#Decision Knowledge: " + issue.jiraIssueMetrics.COUNT_DECISION_KNOWLEDGE + "</li>" +
                        "<li>Days Completion: " + issue.jiraIssueMetrics.DAYS_COMPLETION + "</li>" +
                        "<li>Exp. Reporter: " + issue.jiraIssueMetrics.EXPERIENCE_REPORTER + "</li>" +
                        "<li>Exp. Resolver: " + issue.jiraIssueMetrics.EXPERIENCE_RESOLVER + "</li>" +
                        "<li>Priority: " + issue.jiraIssueMetrics.PRIORITY + "</li>" +
                        "<li>Description Size: " + issue.jiraIssueMetrics.SIZE_DESCRIPTION + "</li>" +
                        "<li>Summary Size: " + issue.jiraIssueMetrics.SIZE_SUMMARY + "</li>" +
                        "</ul>" +
                        "</div>" +
                        "<a data-replace-text='" + issue.rating + " less' class='aui-expander-trigger' aria-controls='expanderOfRating_" + category + issue.decisionKnowledgeElement.key + "'>" + issue.rating + " details</a>";
                    var tableRow = "<tr>" +
                        "<td><input class='checkbox includeInReleaseNote_" + category + "' checked type='checkbox' name='useSprint' id='includeInReleaseNote_" + issue.decisionKnowledgeElement.key + "'></td>" +
                        "<td>" + expander + "</td>" +
                        "<td><a target='_blank' href='" + issue.decisionKnowledgeElement.url + "'>" + issue.decisionKnowledgeElement.key + "</a></td>" +
                        "<td>" + issue.decisionKnowledgeElement.summary + "</td>" +
                        "<td>" + issue.decisionKnowledgeElement.type + "</td>" +
                        "</tr>";
                    tableRows += tableRow;
                });
                table += tableRows;
                divToAppend.append(title);
                divToAppend.append(table);
                divToAppend.append("</table>");


            }
        };
        
        issueSelectSubmitButton.onclick = function () {
            // set button busy
            setButtonBusyAndDisabled(issueSelectSubmitButton, true);

            var checkedItems = {"bug_fixes": [], "new_features": [], "improvements": []}
            Object.keys(checkedItems).map(function (cat) {
                var queryElement = $(".includeInReleaseNote_" + cat);
                queryElement.each(function (i) {
                    if ($(queryElement[i]).prop("checked")) {
                        var key = queryElement[i].id.split(/includeInReleaseNote_(.+)/)[1];
                        checkedItems[cat].push(key);
                    }
                })
            });
            var additionalConfigurationObjectSelected = [];
            Object.getOwnPropertyNames(firstResultObject.additionalConfiguration).forEach(function (val, idx, array) {
                if (firstResultObject.additionalConfiguration[val]) {
                    additionalConfigurationObjectSelected.push(val)
                }
            });

            var postObject = {
                selectedKeys: checkedItems,
                title: {id: [firstResultObject.title]},
                additionalConfiguration: {id: additionalConfigurationObjectSelected}
            };
            conDecAPI.postProposedKeys(postObject)
                .then(function (response) {
                    if (response) {
                        // remove editor
                        removeEditor();
                        // change tab
                        addTabAndChangeToIt("tab-editor", "Final edit");
                        if (response.markdown) {
                            // display editor and text
                            editor = new Editor({element: document.getElementById("create-release-note-textarea")});
                            editor.render();
                            editor.codemirror.setValue(response.markdown);
                        }
                    }

                }.bind(this)).catch(function (err) {
                throwAlert("An error occurred", err.toString());
            }).finally(function () {
                // set button idle
                setButtonBusyAndDisabled(issueSelectSubmitButton, false);
            });
        };
        
        saveContentButton.onclick = function () {
            console.log("editor", editor.codemirror.getValue());
            var content = editor.codemirror.getValue();
            var postObject = {
                content: content,
                title: firstResultObject.title,
                startDate: firstResultObject.startDate,
                endDate: firstResultObject.endDate
            };

            conDecAPI.createReleaseNote(postObject).then(function (response) {
                if (response && response > 0) {
                    var event = new Event('updateReleaseNoteTable');
                    document.getElementById("release-notes-table").dispatchEvent(event);
                    AJS.dialog2(releaseNoteDialog).hide();
                }
            }).catch(function (err) {
                throwAlert("An error saving occurred", err.toString());
            })
        };

        cancelButton.onclick = function () {
            AJS.dialog2(releaseNoteDialog).hide();
        };
    };

    ConDecDialog.prototype.showExportDialog = function (id, documentationLocation) {
        console.log("conDecDialog showExportDialog");

        // HTML elements
        var exportDialog = document.getElementById("export-dialog");
        var submitButton = document.getElementById("export-dialog-submit-button");
        var cancelButton = document.getElementById("export-dialog-cancel-button");

        // Set onclick listener on buttons
        submitButton.onclick = function () {
            var exportFormat = $('input[name=form-radio-export-format]:checked').val();
            conDecExport.exportLinkedElements(exportFormat, id, documentationLocation);
            AJS.dialog2(exportDialog).hide();
        };

        cancelButton.onclick = function () {
            AJS.dialog2(exportDialog).hide();
        };

        // Show dialog
        AJS.dialog2(exportDialog).show();
    };

    ConDecDialog.prototype.showEditReleaseNoteDialog = function (id) {
        var editDialog = document.getElementById("edit-release-note-dialog");
        var saveButton = document.getElementById("edit-release-note-submit-content");
        var cancelButton = document.getElementById("edit-release-note-dialog-cancel-button");
        var openingButton = document.getElementById("openEditReleaseNoteDialogButton_" + id);
        var deleteButton = document.getElementById("deleteReleaseNote");
        var titleInput = document.getElementById("edit-release-note-dialog-title");
        var exportMDButton = document.getElementById("edit-release-note-dialog-export-as-markdown-button");
        var exportWordButton = document.getElementById("edit-release-note-dialog-export-as-word-button");
        var editor;
        setButtonBusyAndDisabled(openingButton, true);

        conDecAPI.getReleaseNotesById(id).then(function (result) {
            $(".editor-preview").empty();
            AJS.dialog2(editDialog).show();
            removeEditor();
            titleInput.value = result.title;
            editor = new Editor({element: document.getElementById("edit-release-note-textarea")});
            editor.render();
            editor.codemirror.setValue(result.content);

        }).catch(function (error) {
            throwAlert("Retrieving Release notes failed", "Could not retrieve the release notes.")
        }).finally(function () {
            setButtonBusyAndDisabled(openingButton, false);
        });

        function removeEditor() {
            var editorDiv = document.getElementById("edit-release-note-dialog-contain-editor");
            editorDiv.parentNode.removeChild(editorDiv);
            $("#edit-release-note-dialog-content").append("<div id='edit-release-note-dialog-contain-editor'>" +
                "<textarea id='edit-release-note-textarea'></textarea>" +
                "</div>")
        }

        function setButtonBusyAndDisabled(button, busy) {
            if (busy) {
                button.busy();
                button.setAttribute('aria-disabled', 'true');
            } else {
                button.idle();
                button.setAttribute('aria-disabled', 'false');
            }
        }

        saveButton.onclick = function () {
            setButtonBusyAndDisabled(saveButton, true);
            var releaseNote = {id: id, title: titleInput.value, content: editor.codemirror.getValue()};
            conDecAPI.updateReleaseNote(releaseNote).then(function (response) {
                if (response) {
                    fireChangeEvent();
                    AJS.dialog2(editDialog).hide();
                } else {
                    throwAlert("Saving failed", "Could not save the release notes")
                }
            }).catch(function (err) {
                throwAlert("Saving failed", err.toString());
            }).finally(function () {
                setButtonBusyAndDisabled(saveButton, false);
            });
        };
        cancelButton.onclick = function () {

            AJS.dialog2(editDialog).hide();
        };
        deleteButton.onclick = function () {
            setButtonBusyAndDisabled(deleteButton, true);
            conDecAPI.deleteReleaseNote(id).then(function (response) {
                if (response) {
                    fireChangeEvent();
                    AJS.dialog2(editDialog).hide();
                } else {
                    throwAlert("Deleting failed", "The release notes could not be deleted");
                }
            }).catch(function (err) {
                throwAlert("Deleting failed", err.toString());

            }).finally(function () {
                setButtonBusyAndDisabled(deleteButton, false);
            });
        };

        function fireChangeEvent() {
            var event = new Event('updateReleaseNoteTable');
            document.getElementById("release-notes-table").dispatchEvent(event);
        }

        exportMDButton.onclick = function () {
            var mdString = "data:text/plain;charset=utf-8," + encodeURIComponent(editor.codemirror.getValue());
            downloadFile(mdString, "md")
        };
        
        exportWordButton.onclick = function () {
            var htmlString = $(".editor-preview").html();
            if (htmlString) {
                var htmlContent = $("<html>").html(htmlString).html();
                var wordString = "data:text/html," + htmlContent;
                downloadFile(wordString, "doc")
            } else {
                throwAlert("Error downloading Word", "Please change to the preview view of the editor first, then try again.")
            }
        };

        function downloadFile(content, fileEnding) {
            var fileName = "releaseNote." + fileEnding;
            var link = document.createElement('a');
            link.style.display = 'none';
            link.setAttribute('href', content);
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }

        function throwAlert(title, message) {
            AJS.flag({
                type: "error",
                close: "auto",
                title: title,
                body: message
            });
        }
    };
    // export ConDecDialog
    global.conDecDialog = new ConDecDialog();
})(window);
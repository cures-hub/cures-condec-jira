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
 */
(function (global) {
    var ConDecDialog = function () {
    };

    ConDecDialog.prototype.showCreateDialog = function (idOfParentElement, documentationLocationOfParentElement, selectedType = "Alternative") {
        console.log("conDecDialog showCreateDialog");
        console.log(idOfParentElement);
        console.log(documentationLocationOfParentElement);
        console.log(selectedType);
        
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
        fillSelectTypeField(selectTypeField, selectedType);
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

        conDecAPI.getDecisionGroups(sourceId, documentationLocation, function (groups) {
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

    ConDecDialog.prototype.showDeleteDialog = function (id, documentationLocation, callback = function(){}) {
        console.log("conDecDialog showDeleteDialog");

        // HTML elements
        var deleteDialog = document.getElementById("delete-dialog");
        var content = document.getElementById("delete-dialog-content");
        var submitButton = document.getElementById("delete-dialog-submit-button");
        var cancelIcon = document.getElementById("delete-dialog-cancel-icon");
        var cancelButton = document.getElementById("delete-dialog-cancel-button");

        // Set onclick listener on buttons
        submitButton.onclick = function () {
            conDecAPI.deleteDecisionKnowledgeElement(id, documentationLocation, function () {
                conDecObservable.notify();
            });
            AJS.dialog2(deleteDialog).hide();
        };
        
        cancelIcon.onclick = function () {
            callback(null);
        };

        cancelButton.onclick = function () {
            AJS.dialog2(deleteDialog).hide();
            callback(null);
        };

        // Show dialog
        AJS.dialog2(deleteDialog).show();
    };

    ConDecDialog.prototype.showDeleteLinkDialog = function (id, documentationLocation, idOfParent, documentationLocationOfParent, callback = function(){}) {
        console.log("conDecDialog showDeleteLinkDialog");

        // HTML elements
        var deleteLinkDialog = document.getElementById("delete-link-dialog");
        var content = document.getElementById("delete-link-dialog-content");
        var submitButton = document.getElementById("delete-link-dialog-submit-button");
        var cancelIcon = document.getElementById("delete-link-dialog-cancel-icon");
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
        
        cancelIcon.onclick = function () {
            callback(null);
        };

        cancelButton.onclick = function () {
            AJS.dialog2(deleteLinkDialog).hide();
            callback(null);
        };

        // Show dialog
        AJS.dialog2(deleteLinkDialog).show();
    };

    ConDecDialog.prototype.showLinkDialog = function (id, documentationLocation, idOfTarget, documentationLocationOfTarget, linkType) {
        console.log("conDecDialog showLinkDialog");

        // HTML elements
        var linkDialog = document.getElementById("link-dialog");
        var sourceElementField = document.getElementById("link-form-source-element");
        var selectElementField = document.getElementById("link-form-select-element");
        var selectLinkTypeField = document.getElementById("link-form-select-linktype");
        var submitButton = document.getElementById("link-dialog-submit-button");
        var cancelButton = document.getElementById("link-dialog-cancel-button");
        
        conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function(sourceElement) {
        	sourceElementField.value = sourceElement.type + " / " + sourceElement.summary;
        });
        
        if (idOfTarget !== undefined && documentationLocationOfTarget !== undefined) {
        	conDecAPI.getDecisionKnowledgeElement(idOfTarget, documentationLocationOfTarget, function(targetElement) {
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

        selectElementField.onchange = function () {
        	var idOfSelectedElement = this.value.split(":")[0];
        	var documentationLocationOfSelectedElement = this.value.split(":")[1];
            conDecAPI.getDecisionKnowledgeElement(idOfSelectedElement, documentationLocationOfSelectedElement, 
            		function (element) {
                selectLinkTypeField.value = suggestLinkType(element.type);
                AJS.$(selectLinkTypeField).auiSelect2();
            });
        };

        // Set onclick listener on buttons
        submitButton.onclick = function () {
            var idOfChild = selectElementField.value.split(":")[0];
            var documentationLocationOfChild = selectElementField.value.split(":")[1];
            var linkType = selectLinkTypeField.value;
            conDecAPI.createLink(id, idOfChild, documentationLocation, documentationLocationOfChild, 
            		linkType, function () {conDecObservable.notify()});
            AJS.dialog2(linkDialog).hide();
        };

        cancelButton.onclick = function () {
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
        conDecAPI.getUnlinkedElements(id, documentationLocation, function (unlinkedElements) {
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
            insertString += "<option " + " value='" + linkTypes[index] + "'>"
                + linkTypes[index] + "</option>";
        }
        selectField.insertAdjacentHTML("afterBegin", insertString);
        if (linkType !== null && linkType !== undefined) {
        	console.log(linkType);
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
    ConDecDialog.prototype.showEditDialog = function (id, documentationLocation, callback = function(){}) {
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
            var cancelIcon = document.getElementById("edit-dialog-cancel-icon");
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
            conDecAPI.getDecisionGroups(id, documentationLocation, function (groups) {
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
            
            cancelIcon.onclick = function () {
                callback(null);
            };

            cancelButton.onclick = function () {
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
		// AJS.dialog2(addCriterionDialog).on("hide",
		// removeDialogHideListener());

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

    global.conDecDialog = new ConDecDialog();
})(window);
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
(function (global) {

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
		submitButton.onclick = function () {
			var summary = inputSummaryField.value;
			var description = inputDescriptionField.value;
			var type = selectTypeField.value;
			var documentationLocation = selectLocationField.value;
			conDecAPI.createDecisionKnowledgeElement(summary, description, type, documentationLocation,
				idOfParentElement, documentationLocationOfParentElement, function () {
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

	ConDecDialog.prototype.showDeleteDialog = function showDeleteDialog(id, documentationLocation) {
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

	ConDecDialog.prototype.showDeleteLinkDialog = function showDeleteLinkDialog(id, documentationLocation) {
		console.log("conDecDialog showDeleteLinkDialog");

		// HTML elements
		var deleteLinkDialog = document.getElementById("delete-link-dialog");
		var content = document.getElementById("delete-link-dialog-content");
		var submitButton = document.getElementById("delete-link-dialog-submit-button");
		var cancelButton = document.getElementById("delete-link-dialog-cancel-button");

		// Set onclick listener on buttons
		submitButton.onclick = function () {
			var parentElement = conDecTreant.findParentElement(id);
			conDecAPI.deleteLink(parentElement["id"], id, parentElement["documentationLocation"],
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

		selectElementField.onchange = function () {
			conDecAPI.getDecisionKnowledgeElement(this.value, "i", function (decisionKnowledgeElement) {
				if (decisionKnowledgeElement && decisionKnowledgeElement.type === "Argument") {
					argumentFieldGroup.style.display = "inherit";
					radioPro.checked = true;
				}
			});
		};

		// Set onclick listener on buttons
		submitButton.onclick = function () {
			var childId = selectElementField.value;
			var knowledgeTypeOfChild = $('input[name=form-radio-argument]:checked').val();
			conDecAPI.createLink(knowledgeTypeOfChild, id, childId, "i", "i", function () {
				conDecObservable.notify();
			});
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

		conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function (decisionKnowledgeElement) {
			var summary = decisionKnowledgeElement.summary;
			var description = decisionKnowledgeElement.description;
			var type = decisionKnowledgeElement.type;
			var documentationLocation = decisionKnowledgeElement.documentationLocation;

			if (documentationLocation === "i") {
				var createEditIssueForm = require('quick-edit/form/factory/edit-issue');
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
			submitButton.onclick = function () {
				var summary = inputSummaryField.value;
				var description = inputDescriptionField.value;
				var type = selectTypeField.value;
				conDecAPI.updateDecisionKnowledgeElement(id, summary, description, type, documentationLocation,
					function () {
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
		if (selectField === null) {
			return;
		}
		var knowledgeStatus = null;
		if (element.type === "Issue") {
			knowledgeStatus = conDecAPI.issueStatus;
		} else {
			knowledgeStatus = conDecAPI.knowledgeStatus;
		}
		selectField.innerHTML = "";
		for (var index = 0; index < knowledgeStatus.length; index++) {
			var isSelected = "";
			console.log(elementStatus);
			if (knowledgeStatus[index].toLocaleUpperCase() === elementStatus) {
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

	ConDecDialog.prototype.showChangeStatusDialog = function showChangeStatusDialog(id, documentationLocation) {
		console.log("conDecDialog showChangeStatusDialog");

		// HTML elements
		var changeStatusDialog = document.getElementById("change-status-dialog");
		var selectStatusField = document.getElementById("change-status-form-select-type");
		var submitButton = document.getElementById("change-status-dialog-submit-button");
		var cancelButton = document.getElementById("change-status-dialog-cancel-button");

		// Fill HTML elements
		conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function (decisionKnowledgeElement) {
			conDecAPI.getStatus(decisionKnowledgeElement, function (status) {
				fillSelectStatusFiled(selectStatusField, status, decisionKnowledgeElement);
			});
		});

		// Set onclick listener on buttons
		submitButton.onclick = function () {
			var status = selectStatusField.value;
			conDecAPI.setStatus(id, documentationLocation, status, function () {
				conDecObservable.notify();
			});
			AJS.dialog2(changeStatusDialog).hide();
		};

		cancelButton.onclick = function () {
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
	
	ConDecDialog.prototype.showCreateReleaseNoteDialog = function showCreateReleaseNoteDialog() {
		// HTML elements
		// set button busy before we show the dialog
		var openingButton = document.getElementById("openCreateReleaseNoteDialogButton");
		setButtonBusyAndDisabled(openingButton, true);
		var releaseNoteDialog = document.getElementById("create-release-note-dialog");
		var cancelButton = document.getElementById("create-release-note-dialog-cancel-button");
		var configurationSubmitButton = document.getElementById("create-release-note-submit-button");
		var issueSelectSubmitButton= document.getElementById("create-release-note-submit-issues-button");
		var saveContentButton= document.getElementById("create-release-note-submit-content");
		var loader = document.getElementById("createReleaseNoteDialogLoader");
		var useSprintSelect= document.getElementById("useSprint");
		var titleInput= document.getElementById("title");
		var sprintOptions= document.getElementById("selectSprints");
		var useReleaseSelect= document.getElementById("useReleases");
		var releaseOptions= document.getElementById("selectReleases");

		var titleWasChanged=false;
		var editor;
		var firstResultObject={};

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

		var allTargetGroupIncludes=["include_decision_knowledge", "include_bug_fixes","include_test_instructions"];
		var allSoftwareTypeIncludes=["include_breaking_changes", "include_extra_link","include_upgrade_guide"];

		addjiraIssueMetric(criteria);
		removeListItemIssues();
		AJS.tabs.change(jQuery('a[href="#tab-configuration"]'));
		makeAsyncCalls();
		fillSoftwaretypesAndTargetGroups();
		var sprintsArray;
		var releasesArray=[];
		removeEditor();
		function removeEditor(){
			var editorDiv=document.getElementById("create-release-note-dialog-contain-editor");
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
		function prefillDateBox(){
			var today= new Date();
			var twoWeeksAgo = new Date(today.getTime()-12096e5);
			var todayString =today.getFullYear()  + '-' + ('0' + (today.getMonth()+1)).slice(-2) + '-' +('0' + today.getDate()).slice(-2) ;
			var twoWeeksAgoString =twoWeeksAgo.getFullYear()  + '-' + ('0' + (twoWeeksAgo.getMonth()+1)).slice(-2) + '-' +('0' + twoWeeksAgo.getDate()).slice(-2) ;
			document.getElementById("start-range").value=twoWeeksAgoString;
			document.getElementById("final-range").value=todayString;
		}

		function fillSoftwaretypesAndTargetGroups(){
			$("#selectSoftwareType").empty();
			$("#selectTargetGroup").empty();
			softwareTypeMapping.map(function(type){
				$("#selectSoftwareType").append("<option value='"+type.id+"'>"+type.title+"</option>")
			});
			targetGroupMapping.map(function(targetGroup){
				$("#selectTargetGroup").append("<option value='"+targetGroup.id+"'>"+targetGroup.title+"</option>")
			});
			document.getElementById("selectSoftwareType").onchange=onSoftwareTypeChange;
			document.getElementById("selectTargetGroup").onchange=onTargetGroupChange;
			onSoftwareTypeChange();
			onTargetGroupChange();
		}
		function onTargetGroupChange(){
			var selectedGroup = $("#selectTargetGroup").val();

			var foundGroup=targetGroupMapping.filter(function(targetGroup){
				return targetGroup.id===selectedGroup
			});
			allTargetGroupIncludes.map(function(include){
				if(foundGroup[0].includes.indexOf(include)>-1){
					$("#"+include).prop("checked",true);
				}else{
					$("#"+include).prop("checked",false);
				}
			})
		}
		function onSoftwareTypeChange(){
			var selectedType = $("#selectSoftwareType").val();
			var foundType=softwareTypeMapping.filter(function(type){
				return type.id===selectedType
			});
			allSoftwareTypeIncludes.map(function(include){
				if(foundType[0].includes.indexOf(include)>-1){
					$("#"+include).prop("checked",true);
				}else{
					$("#"+include).prop("checked",false);
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
						var hasValidSprints=false;
						sprintsArray = sprints.map(function (sprint) {
							return sprint.values;
						});
						if (sprintsArray && sprintsArray.length && sprintsArray[0] && sprintsArray[0].length) {
							$('#selectSprints').empty();
							sprintsArray[0].map(function (sprint) {
								if(sprint && sprint.startDate && sprint.endDate){
									hasValidSprints=true;
									$('#selectSprints').append('<option class="sprint-option" value="' + sprint.id + '">' + sprint.name + '</option>');
								}else{
									$('#selectSprints').append('<option class="sprint-option" disabled value="' + sprint.id + '">' + sprint.name + '</option>');
								}
							});
						}
						if(!hasValidSprints){
							disableSprintBox();
						}
						resolve();
					}).catch(function (err) {
					disableSprintBox();
					reject();
				});

			});
			// load issue types
			var issueTypePromise = new Promise(function (resolve, reject) {
				conDecAPI.getIssueTypes()
					.then(function (issueTypes) {
						resolve(issueTypes);

					}).catch(function (err) {
					throwAlert("No issue-types could be loaded");
					reject();
				});
			});
			var releasesPromise= new Promise(function(resolve,reject){
				conDecAPI.getReleases().then(function(releases){
					var hasValidReleases=false;
					var releaseSelector=$('#selectReleases');
					releaseSelector.empty();
					releases.map(function(release){
						if(release && release.startDate.iso && release.releaseDate.iso){
							hasValidReleases=true;
							releaseSelector.append('<option value="' + release.id + '">' + release.name+'</option>');
							releasesArray.push(release);
						}else{
							releaseSelector.append('<option disabled value="' + release.id + '">' + release.name +'</option>');
						}
					});
					if(!hasValidReleases){
						disableReleaseBox();
					}
					resolve();
				}).catch(function (err) {
					disableReleaseBox();
					reject();
				})

			});

			var preSelectedIssueTypesPromise = new Promise(function(resolve,reject){
				conDecAPI.getProjectWideSelectedIssueTypes().then(function(result){
					resolve(result);
				}).catch(function(err){
					reject();
				})
			});

			Promise.all([sprintPromise, issueTypePromise, releasesPromise, preSelectedIssueTypesPromise]).then(function (values) {
				//set issue types
				var issueTypes = values[1];
				var preSelectedIssueTypes = values[3];
				manageIssueTypes(issueTypes, preSelectedIssueTypes);


				// disable busy button
				setButtonBusyAndDisabled(openingButton, false);
				// open dialog

				// Show dialog
				AJS.dialog2(releaseNoteDialog).show();
				prefillDateBox();

			})
		}
		function manageIssueTypes(issueTypes, preSelectedIssueTypes){
			if (issueTypes && issueTypes.length) {
				// empty lists
				var bugSelector=$("#multipleBugs");
				var featureSelector=$("#multipleFeatures");
				var improvementSelector=$("#multipleImprovements");
				bugSelector.empty();
				featureSelector.empty();
				improvementSelector.empty();
				console.log(preSelectedIssueTypes);
				issueTypes.map(function (issueType) {
					var bugSelected=false;
					var bugString='<option value="' + issueType.id + '"';
					var featureSelected=false;
					var featureString='<option value="' + issueType.id + '"';
					var improvementSelected=false;
					var improvementString='<option value="' + issueType.id + '"';
					if(preSelectedIssueTypes){
						if(preSelectedIssueTypes.bug_fixes){
							bugSelected=preSelectedIssueTypes.bug_fixes.indexOf(issueType.name)>-1;
						}
						if(preSelectedIssueTypes.new_features){
							featureSelected = preSelectedIssueTypes.new_features.indexOf(issueType.name)>-1;
						}
						if(preSelectedIssueTypes.improvements){
							improvementSelected = preSelectedIssueTypes.improvements.indexOf(issueType.name)>-1;
						}
					}
					if(bugSelected){
						bugString +="selected";
					}
					if(featureSelected){
						featureString +="selected";
					}
					if(improvementSelected){
						improvementString +="selected";
					}
					bugSelector.append(bugString+'>' + issueType.name + '</option>');
					featureSelector.append(featureString+'>' + issueType.name + '</option>');
					improvementSelector.append(improvementString+'>' + issueType.name + '</option>');

				})
			}
		}

		function disableSprintBox() {
			$("#useSprint").attr("disabled", true);
			$("#selectSprints").attr("disabled", true);
		}
		function disableReleaseBox(){
			$("#useReleases").attr("disabled", true);
			$("#selectReleases").attr("disabled", true);
		}
		function addTabAndChangeToIt(tabId,title) {
			var listItem = document.getElementById("listItemTab"+tabId);
			if (!listItem) {
				$("#tab-list-menu").append("<li class='menu-item' id='listItemTab"+tabId+"'><a href='#" + tabId +"'>"+title+"</a></li>");
			}
			AJS.tabs.setup();
			AJS.tabs.change(jQuery('a[href="#'+tabId+'"]'));


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
		useSprintSelect.onchange= function(){
			setSprintOrReleaseOption("selectSprints");
		};
		sprintOptions.onchange= function(){
			setSprintOrReleaseOption("selectSprints");
		};
		useReleaseSelect.onchange = function () {
			setSprintOrReleaseOption("selectReleases")
		};
		releaseOptions.onchange =function () {
			setSprintOrReleaseOption("selectReleases")
		};
		function setSprintOrReleaseOption(selectId) {
			if (!titleWasChanged) {
				var options = document.getElementById(selectId).options;
				for (var i=0;i<options.length;i++){
					if(options[i].selected){
						titleInput.value=options[i].innerText;
					}
				}
			}
		}
		titleInput.onchange = function(){
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
			if ((!useSprints)&& (!useReleases) && (!!startDate === false || !!endDate === false)) {
				throwAlert("Select Date or Sprint or Release", "The start date and the end date have to be selected, if the sprints or releases are not available or not selected");
				return
			}

			function getStartAndEndDate() {
				var result = {startDate: "", endDate: ""};
				if(useReleases && releasesArray && releasesArray.length){
					var selectedDates= releasesArray.filter(function(release){
						return release.id=selectedRelease;
					});
					if(selectedDates && selectedDates.length){
						result.startDate=selectedDates[0].startDate.iso;
						result.endDate=selectedDates[0].releaseDate.iso;
					}else{
						throwAlert("An error occured", "Something went wrong with the release selection");
						return false;
					}
				}else if (useSprints && sprintsArray && sprintsArray.length && sprintsArray[0] && sprintsArray[0].length) {
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



			var additionalConfiguration={};
			function getAdditionalConfiguration() {
				$(".advancedOptionalConfiguration").each(function (i) {
					var item = ($(".advancedOptionalConfiguration").get(i));
					var name=item.name;
					var isChecked=item.checked;
					additionalConfiguration[name.toUpperCase()]=isChecked;
				})
			}
			var timeRange = getStartAndEndDate();
			if (!timeRange) {
				return;
			}
			// set button busy and disabled
			setButtonBusyAndDisabled(configurationSubmitButton,true);
			getAdditionalConfiguration();
			var jiraIssueMetric = getjiraIssueMetric(criteria);
			var targetGroup = $("#selectTargetGroup").val();
			var bugFixes = $("#multipleBugs").val();
			var features = $("#multipleFeatures").val();
			var improvements = $("#multipleImprovements").val();
			var title= $("#title").val();
			// submit configuration
			var configuration = {
				title:title,
				startDate: timeRange.startDate,
				endDate: timeRange.endDate,
				sprintId: selectedSprint,
				targetGroup: targetGroup,
				bugFixMapping: bugFixes,
				featureMapping: features,
				improvementMapping: improvements,
				additionalConfiguration:additionalConfiguration,
				jiraIssueMetric: jiraIssueMetric
			};

			conDecAPI.getProposedIssues(configuration).then(function(response){

				if(response){
					// change tab
					addTabAndChangeToIt("tab-issues", "Suggested Issues");
					console.log(response);

					firstResultObject=response;
					// display issues and information
					if (response.proposals) {
						showTables(response.proposals);
					}
					if(response.title){
						showTitle(response.title)
					}
				}

			}).catch(function(err){
				// we handle this exception directly in condec.api
			}).finally(function(){
				// set button idle
				setButtonBusyAndDisabled(configurationSubmitButton,false);
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
			function showTitle(title){
				$("#suggestedIssuesTitle").text("Suggested Issues for Release Notes: "+title);
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
					"<th>Rating</th>" +
					"<th>Key</th>" +
					"<th>Summary</th>" +
					"<th>Type</th>" +
					"</tr></thead>";
				var tableRows = "";
				issues.map(function (issue) {
					var expander = "<div id='expanderOfRating_" +category+ issue.decisionKnowledgeElement.key + "' class='aui-expander-content'>" +
						"<ul class='noDots'>" +
						"<li>#Comments: "+issue.jiraIssueMetrics.COUNT_COMMENTS+"</li>" +
						"<li>#Decision Knowledge: "+issue.jiraIssueMetrics.COUNT_DECISION_KNOWLEDGE+"</li>" +
						"<li>Days Completion: "+issue.jiraIssueMetrics.DAYS_COMPLETION+"</li>" +
						"<li>#Comments: "+issue.jiraIssueMetrics.COUNT_COMMENTS+"</li>" +
						"<li>Exp. Reporter: "+issue.jiraIssueMetrics.EXPERIENCE_REPORTER+"</li>" +
						"<li>Exp. Resolver: "+issue.jiraIssueMetrics.EXPERIENCE_RESOLVER+"</li>" +
						"<li>Priority: "+issue.jiraIssueMetrics.PRIORITY+"</li>" +
						"<li>Description Size: "+issue.jiraIssueMetrics.SIZE_DESCRIPTION+"</li>" +
						"<li>Summary Size: "+issue.jiraIssueMetrics.SIZE_SUMMARY+"</li>" +
						"</ul>" +
						"</div>" +
						"<a data-replace-text='"+issue.rating+" less' class='aui-expander-trigger' aria-controls='expanderOfRating_" +category+ issue.decisionKnowledgeElement.key + "'>"+issue.rating+" details</a>";
					var tableRow = "<tr>" +
						"<td><input class='checkbox includeInReleaseNote_" + category + "' checked type='checkbox' name='useSprint' id='includeInReleaseNote_" + issue.decisionKnowledgeElement.key + "'></td>" +
						"<td>" + expander +"</td>" +
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
			setButtonBusyAndDisabled(issueSelectSubmitButton,true);

			var checkedItems={"bug_fixes":[],"new_features":[],"improvements":[]}
			Object.keys(checkedItems).map(function(cat){
				var queryElement=$(".includeInReleaseNote_"+cat);
				queryElement.each(function(i){
					if($(queryElement[i]).prop("checked")){
						var key=queryElement[i].id.split(/includeInReleaseNote_(.+)/)[1];
						checkedItems[cat].push(key);
					}
				})
			});
			var additionalConfigurationObjectSelected=[];
			Object.getOwnPropertyNames(firstResultObject.additionalConfiguration).forEach(function(val, idx, array) {
				if(firstResultObject.additionalConfiguration[val]){
					additionalConfigurationObjectSelected.push(val)
				}
			});

			var postObject={selectedKeys:checkedItems,title:{id:[firstResultObject.title]},additionalConfiguration:{id:additionalConfigurationObjectSelected}};
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

			}.bind(this)).catch(function(err){
				throwAlert("An error occurred",err.toString());
			}).finally(function(){
				// set button idle
				setButtonBusyAndDisabled(issueSelectSubmitButton,false);
			});


		};
		saveContentButton.onclick= function(){
			console.log("editor",editor.codemirror.getValue());
			var content=editor.codemirror.getValue();
			var postObject = {
				content: content,
				title: firstResultObject.title,
				startDate: firstResultObject.startDate,
				endDate: firstResultObject.endDate
			};

			conDecAPI.createReleaseNote(postObject).then(function(response){
				if(response && response>0){
					var event = new Event('updateReleaseNoteTable');
					document.getElementById("release-notes-table").dispatchEvent(event);
					AJS.dialog2(releaseNoteDialog).hide();
				}
			}).catch(function (err) {
				throwAlert("An error saving occurred",err.toString());
			})
		};
		cancelButton.onclick = function () {
			AJS.dialog2(releaseNoteDialog).hide();
		};

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

	ConDecDialog.prototype.showEditReleaseNoteDialog = function showEditReleaseNoteDialog(id){
		var editDialog = document.getElementById("edit-release-note-dialog");
		var saveButton = document.getElementById("edit-release-note-submit-content");
		var cancelButton = document.getElementById("edit-release-note-dialog-cancel-button");
		var openingButton = document.getElementById("openEditReleaseNoteDialogButton_"+id);
		var deleteButton = document.getElementById("deleteReleaseNote");
		var titleInput = document.getElementById("edit-release-note-dialog-title");
		var exportMDButton= document.getElementById("edit-release-note-dialog-export-as-markdown-button");
		var exportWordButton= document.getElementById("edit-release-note-dialog-export-as-word-button");
		var editor;
		setButtonBusyAndDisabled(openingButton,true);

		conDecAPI.getReleaseNotesById(id).then(function(result){
			AJS.dialog2(editDialog).show();
			removeEditor();
			titleInput.value=result.title;
			editor=new Editor({element:document.getElementById("edit-release-note-textarea")});
			editor.render();
			editor.codemirror.setValue(result.content);

		}).catch(function(error){
			throwAlert("Retrieving Release notes failed", "Could not retrieve the release notes.")
		}).finally(function(){
			setButtonBusyAndDisabled(openingButton,false);
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
			setButtonBusyAndDisabled(saveButton,true);
			var releaseNote = {id: id, title: titleInput.value, content: editor.codemirror.getValue()};
			conDecAPI.updateReleaseNote(releaseNote).then(function (response) {
				if(response){
					fireChangeEvent();
					AJS.dialog2(editDialog).hide();
				}else{
					throwAlert("Saving failed", "Could not save the release notes")
				}
			}).catch(function(err){
				throwAlert("Saving failed", err.toString());
			}).finally(function () {
				setButtonBusyAndDisabled(saveButton,false);
			});
		};
		cancelButton.onclick = function () {

			AJS.dialog2(editDialog).hide();
		};
		deleteButton.onclick = function () {
			setButtonBusyAndDisabled(deleteButton,true);
			conDecAPI.deleteReleaseNote(id).then(function (response) {
				if(response){
					fireChangeEvent();
					AJS.dialog2(editDialog).hide();
				}else{
					throwAlert("Deleting failed","The release notes could not be deleted");
				}
			}).catch(function (err) {
				throwAlert("Deleting failed",err.toString());

			}).finally(function () {
				setButtonBusyAndDisabled(deleteButton,false);
			});
		};
		function fireChangeEvent(){
			var event = new Event('updateReleaseNoteTable');
			document.getElementById("release-notes-table").dispatchEvent(event);
		}
		exportMDButton.onclick = function () {
			var mdString ="data:text/plain;charset=utf-8,"+ encodeURIComponent(editor.codemirror.getValue());
			downloadFile(mdString,"md")
		};
		exportWordButton.onclick =function () {
			var htmlString=$(".editor-preview").html();
			if(htmlString){
				var htmlContent=$("<html>").html(htmlString).html();
				var wordString ="data:text/html,"+ htmlContent;
				downloadFile(wordString,"doc")
			}else{
				throwAlert("Error downloading Word","Please change to the preview view of the editor first, then try again.")
			}
		};

		function downloadFile(content,fileEnding){
			var fileName="releaseNote."+fileEnding;
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
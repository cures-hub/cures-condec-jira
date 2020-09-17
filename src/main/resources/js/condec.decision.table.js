(function (global) {

	let ConDecDecisionTable = function ConDecDecisionTable() {
	};

	const dropDownID = "selectDecisionProblem";
	let issues = [];
	let decisionTableData = [];
	let currentIssue;
	
	ConDecDecisionTable.prototype.initView = function () {		
		document.getElementById("link-distance-input-decision-table").addEventListener("change", function (event){
			conDecDecisionTable.loadDecisionProblems();
		});
		addOnClickEventToDecisionTableButtons();
		
		conDecDecisionGuidance.addOnClickListenerForRecommendations();
		
		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);
		
		// Fill view
		this.updateView();
	};
	
	ConDecDecisionTable.prototype.updateView = function () {
		this.loadDecisionProblems();
	};
	
	function addOnClickEventToDecisionTableButtons (viewIdentifier = "decision-table") {
		var addCriterionButton = document.getElementById("add-criterion-button-" + viewIdentifier);
		if (!addCriterionButton.hasAttribute("listener")) {
			addCriterionButton.addEventListener("click", function (event) {
				conDecDecisionTable.showAddCriteriaToDecisionTableDialog(viewIdentifier);		
				this.setAttribute("listener", "true");			
			});
		}
		document.getElementById("add-alternative-button-" + viewIdentifier).addEventListener("click", function (event) {
			conDecDecisionTable.showCreateDialogForIssue();
		});
	}
	
 	/*
	 * external references: none, called in initView function
	 */
	ConDecDecisionTable.prototype.loadDecisionProblems = function () {
		console.log("conDecDecisionTable loadDecisionProblems");
		const linkDistance = document.getElementById("link-distance-input-decision-table").value;		
		const selectedElementKey = conDecAPI.getIssueKey();		
		const filterSettings = {
				"linkDistance": linkDistance,
				"selectedElement": selectedElementKey,
				"knowledgeTypes": ["Issue", "Problem", "Goal"]
		};
		conDecAPI.getKnowledgeElements(filterSettings, function (knowledgeElements) {
			issues = knowledgeElements.filter(element => !isSelectedElement(element, selectedElementKey) || filterSettings["knowledgeTypes"].includes(element.type));
			addDropDownItems(issues, selectedElementKey, "decision-table");
		});
	};
	
	/**
	 * True if the element is the selected element in the filter settings.
	 */
	function isSelectedElement(element, selectedElementKey) {
		return selectedElementKey.match(element.key);
	}
	
	ConDecDecisionTable.prototype.build = function (filterSettings, viewIdentifier = "decision-table", decisionProblem) {
		this.viewIdentifier = viewIdentifier;
		currentIssue = decisionProblem;
		addOnClickEventToDecisionTableButtons(viewIdentifier);
		conDecAPI.getDecisionTable(filterSettings, function (decisionTable) {
			buildDecisionTable(decisionTable, viewIdentifier);
		});
	}

	ConDecDecisionTable.prototype.showAddCriteriaToDecisionTableDialog = function (viewIdentifier) {
		showAddCriterionToDecisionTableDialog(decisionTableData["criteria"], function (selectedCriteria) {					
			var removedCriteria = decisionTableData["criteria"].filter(criterion => !selectedCriteria.find(item => item.id === criterion.id));
			for (removedCriterion of removedCriteria) {
				const index = decisionTableData["criteria"].findIndex(criterion => criterion.id === removedCriterion.id);
				decisionTableData["criteria"].splice(index, index >= 0 ? 1 : 0);
				
				for (alternative of decisionTableData["alternatives"]) {
					for (argument of alternative.arguments) {
						if (argument.hasOwnProperty("criterion") && argument.criterion.id == removedCriterion.id) {
							deleteLink(argument, argument.criterion);
						}
					}
				}
			}
			
			for (selectedCriterion of selectedCriteria) {
				var isCriteriaAlreadyShown = decisionTableData["criteria"].find(item => item.id === selectedCriterion.id) ? true : false;
				if (!isCriteriaAlreadyShown) {
					decisionTableData["criteria"].push(selectedCriterion);
				}
			}

			buildDecisionTable(decisionTableData, viewIdentifier);
		});
	};
	
	function showAddCriterionToDecisionTableDialog (currentCriteria, callback) {
		let criteriaDialog = document.getElementById("criteria-dialog");
		let submitButton = document.getElementById("criteria-dialog-submit-button");
		let cancelButton = document.getElementById("criteria-dialog-cancel-button");
		let tableBody = document.getElementById("criteria-table-body");
    	tableBody.innerHTML = "";
        
		conDecAPI.getDecisionTableCriteria(function (allCriteria) {        	
        	for (let criterion of allCriteria) {
        		let tableRow = document.createElement("tr");
        		tableBody.appendChild(tableRow);
           		let isChecked = currentCriteria.find(item => item.id === criterion.id) ? "checked" : "";
           		let summary = allCriteria.find(item => item.id === criterion.id).summary;
   
           		tableRow.innerHTML += `<td headers="criterion-number">`
           			+ `<input class="checkbox" type="checkbox" name="criteria-checkboxes" id="checkBoxOne" ${isChecked} value="${criterion.id}">`
        			+ `</td>`
        			+ `<td headers="criterion-name">${summary}</td>`;
        	}
        	
        	document.getElementById("link-to-settings").href = "../../../plugins/servlet/condec/settings?projectKey=" + conDecAPI.getProjectKey() 
        		+ "&category=rationaleModel";
			
			submitButton.onclick = function () {				
				var selectedCriteria = $('input[type="checkbox"][name="criteria-checkboxes"]:checked').map(function() { 
					return allCriteria.find(criterion => (this.value).match(criterion.id));
				}).get();
				callback(selectedCriteria);
				AJS.dialog2(criteriaDialog).hide();
			}
		});
        
        // Show dialog
        AJS.dialog2(criteriaDialog).show();

        cancelButton.onclick = function () {
			AJS.dialog2(criteriaDialog).hide();
        }
	}
	
	ConDecDecisionTable.prototype.showCreateDialogForIssue = function () {
		if (currentIssue) {
			conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative");		
		}
	}

	function buildDecisionTable(decisionTable, viewIdentifier = "decision-table") {
		decisionTableData = decisionTable;

		addCriteriaToToDecisionTable(decisionTable["criteria"], viewIdentifier);
		addAlternativesToDecisionTable(decisionTable["alternatives"], decisionTable["criteria"], viewIdentifier);
		addDragAndDropSupportForArguments();
		buildCreateArgumentsButton(decisionTable["alternatives"], viewIdentifier);

		addContextMenuToElements("argument");
		addContextMenuToElements("alternative");
	}

	function buildCreateArgumentsButton(alternatives) {
		let dropDownMenu = document.getElementById("alternative-dropdown-items");
		dropDownMenu.innerHTML = "";
		for (i in alternatives) {
			const alternative = alternatives[i];
			dropDownMenu.innerHTML +=  `<li id="${alternative.id}">
			<a><div style="float: left; margin-right: 8px;"><img src="${alternative.image}"</img></div>
			${alternative.summary}</a></li>`;
		}
		
		document.getElementById("alternative-dropdown-items").addEventListener("click", function (event) {
			const alternative = getElementObj(event.target.parentNode.id);
			if (alternative) {
				conDecDialog.showCreateDialog(alternative.id, alternative.documentationLocation, "Pro-argument");
			}
		});
	}
	
	/**
	 * 
	 * @param {Array
	 *            <KnowledgeElement> or empty object} alternatives
	 */
	function addAlternativesToDecisionTable(alternatives, criteria, viewIdentifier) {
		let body = document.getElementById("decision-table-body-" + viewIdentifier);
		body.innerHTML = "";
		
		if (Object.keys(alternatives).length === 0) {
			body.innerHTML += `<tr id="bodyRowAlternatives"></tr>`;
			let rowElement = document.getElementById(`bodyRowAlternatives`);
			rowElement.innerHTML += `<td>Please add at least one solution option for this issue</td>`;
		} else {
			for (let i in alternatives) {
				const alternative = alternatives[i];
				body.innerHTML += `<tr id="bodyRowAlternatives${alternative.id}"></tr>`;
				let rowElement = document.getElementById(`bodyRowAlternatives${alternative.id}`);
				
				let image = "";
				if (alternatives[i].image) {
					image = `<img src="${alternative.image}"</img>`;
				}
				
				let content = `<div style="clear: left;">
					<p style="float: left; margin-right: 8px;">${image}</p>
					    <p> ${alternative.summary}</p>
					</div>`;
				
				rowElement.innerHTML += `<td>
					<div class="alternative" id="${alternative.id}">${content}</div></td>`;
				if (Object.keys(criteria).length > 0) {
					for (x in criteria) {
						rowElement.innerHTML += `<td id="cell${alternative.id}:${criteria[x].id}" headers="${criteria[x].summary}" class="droppable"></td>`;
					}
				}
				rowElement.innerHTML += `<td id="cellUnknown${alternative.id}" headers="criteriaClmTitleUnknown" class="droppable"></td>`;
				addArgumentsToDecisionTable(alternatives[i]);
			}
		}
	}

	/**
	 * Creates the column header of the decision table.
	 * 
	 * @param criteria
	 *            array of criteria as knowledge element objects.
	 */
	function addCriteriaToToDecisionTable(criteria, viewIdentifier) {
		let header = document.getElementById("decision-table-header-row-" + viewIdentifier);
		header.innerHTML = "";
		
		let firstRowHeaderCell = document.createElement("th");
		firstRowHeaderCell.innerText = "Solution options (Alternatives and Decision)";
		header.appendChild(firstRowHeaderCell);
		
		for (criterion of criteria) {
			let criteriaColumn = document.createElement("th");
			criteriaColumn.id = "criteriaClmTitle" + criterion.id;
			criteriaColumn.innerHTML += `<a href="${criterion.url}">${criterion.summary}</a>`;
			header.appendChild(criteriaColumn);
		}
		header.innerHTML += '<th id="criteriaClmTitleUnknown">Arguments without criteria</th>';
	}

	/**
	 * 
	 * @param {Array
	 *            <KnowledgeElement> or empty object} alternatives
	 */
	function addArgumentsToDecisionTable(alternative) {
		for (let index = 0; index < alternative.arguments.length; index++) {
			const argument = alternative.arguments[index];
			let rowElement;
			if (argument.hasOwnProperty("criterion")) {
				rowElement = document.getElementById(`cell${alternative.id}:${argument.criterion.id}`);
			}
			if (!rowElement) {
				rowElement = document.getElementById(`cellUnknown${alternative.id}`);
				document.getElementById("criteriaClmTitleUnknown").setAttribute("style", "display:block");
			}
			// rowElement.setAttribute("style", "white-space: pre;");
			
			let image = "";
			if (argument.image) {
				image = `<img src="${argument.image}"</img>`;
			}
			
			let content = `<div id="${alternative.id}:${argument.id}" class="argument draggable" draggable="true" 
				style="clear: left;">
				<p style="float: left; margin-right: 8px;">${image}</p>
				    <p> ${argument.summary}</p>
				</div>`;
			rowElement.innerHTML += rowElement.innerHTML.length ?
				`<br>${content}` : content;
		}
	}

	function addContextMenuToElements(className) {
		let elements = document.getElementsByClassName(className);
		for (let index = 0; index < elements.length; index++) {
			const element = elements[index];
			element.addEventListener("contextmenu", function (event) {
				event.preventDefault();
				let object;
				if (className === "argument") {
					let tmpIDs = this.id.split(":");
					let argumentID = tmpIDs[1];
					let alternativeID = tmpIDs[0];
					object = decisionTableData["alternatives"].find(alternative => alternative.id == alternativeID).arguments.find(
							argument => argument.id == argumentID);
				} else {
					object = getElementObj(this.id);
				}
				object = Array.isArray(object) ? object[0] : object;
				if (object) {
					conDecContextMenu.createContextMenu(object.id, object.documentationLocation, event, "tbldecisionTable-"+ className);
				}
			});
		}
	}

	/**
	 * 
	 * @param {Array
	 *            or empty object} data
	 * @param {string}
	 *            elementKey
	 */
	function addDropDownItems(issues, elementKey, viewIdentifier) {
		let dropDown = document.getElementById(`${dropDownID}`);
		let btnAddCriterion = document.getElementById("add-criterion-button-" + viewIdentifier);
		let btnAddAlternative = document.getElementById("add-alternative-button-" + viewIdentifier);
		let btnAddArgument = document.getElementById("add-argument-button-" + viewIdentifier);
		
		dropDown.innerHTML = "";
		if (!issues.length) {
			btnAddCriterion.disabled = true;
			btnAddAlternative.disabled = true;
			btnAddArgument.disabled = true;
			dropDown.innerHTML += "<option disabled>Could not find any issue. Please create a new issue!</otpion>";
			return;
		} else {
			btnAddCriterion.disabled = false;
			btnAddAlternative.disabled = false;
			btnAddArgument.disabled = false;
			for (let i = 0; i < issues.length; i++) {
				if (i == 0) {
					dropDown.innerHTML += "<option value=\"" + issues[i].id + "\" checked>" + issues[i].summary + "</option>";
					currentIssue = issues[i];
					const filterSettings = {
							"selectedElement" : issues[i].key
					}
					conDecAPI.getDecisionTable(filterSettings, function (decisionTable) {
						buildDecisionTable(decisionTable);
					});
				} else {
					dropDown.innerHTML += "<option value=\"" + issues[i].id + "\">" + issues[i].summary + "</option>";
				}
			}
		}

		let section = document.querySelector(`#${dropDownID}`);
		section.addEventListener('change', function (e) {
		currentIssue = issues.find(o => o.id == document.getElementById(dropDownID).value);
		if (typeof currentIssue !== "undefined") {
			const filterSettings = {
				"selectedElement" : currentIssue.key
			}
			conDecAPI.getDecisionTable(filterSettings, function (decisionTable) {
				buildDecisionTable(decisionTable);
			});
		} else {
			addAlternativesToDecisionTable([], []);
			}
		});
	}

	function addDragAndDropSupportForArguments() {
		let draggables = document.getElementsByClassName("draggable");
		let droppables = document.getElementsByClassName("droppable");
		for (let x = 0; x < draggables.length; x++) {
			draggables[x].addEventListener("dragstart", function (event) {
				drag(event);
			});
		}
		for (let x = 0; x < droppables.length; x++) {
			droppables[x].addEventListener("drop", function (event) {
				drop(event);
			});
			droppables[x].addEventListener("dragover", function (event) {
				allowDrop(event);
			});
		}
	}

	function allowDrop(ev) {
		ev.preventDefault();
	}

	function drag(ev) {
		ev.dataTransfer.setData("argumentId", ev.target.id);
		ev.dataTransfer.setData("criteriaId", ev.target.parentNode.id);
	}

	function drop(ev) {
		ev.preventDefault();
		let argumentId = ev.dataTransfer.getData("argumentId");
		let criteriaId = ev.dataTransfer.getData("criteriaId");
		arguments = document.getElementsByClassName("argument");
		for (let x = 0; x < arguments.length; x++) {
			const argument = arguments[x];
			if (argument.id === argumentId) {
				if (!event.target.id.includes("cell")) {
					ev.target.parentNode.appendChild(argument);
					updateArgumentCriteriaLink(criteriaId, ev.target.parentNode.id, argumentId.split(":")[1]);
					break;
				} else {
					ev.target.appendChild(argument);
					updateArgumentCriteriaLink(criteriaId, ev.target.id, argumentId.split(":")[1]);
					break;
				}
			}
		}
	}

	function updateArgumentCriteriaLink(source, target, elemId) {
		// moved arg. from unknown to criteria column
		if (source.toLowerCase().includes("unknown") && target.toLowerCase().includes("unknown")) {
			const sourceAlternative = getElementObj(source);
			const targetAlternative = getElementObj(target);			
			const argument = decisionTableData["alternatives"]
				.find(alternative => alternative.id == sourceAlternative.id).arguments
				.find(argument => argument.id == elemId);
			deleteLink(sourceAlternative, argument);
			createLink(targetAlternative, argument);
		} else if (source.toLowerCase().includes("unknown")) {
			const sourceAlternative = getElementObj(source);
			const targetInformation = getElementObj(target);
			const targetAlternative = targetInformation[0];
			const criteria = targetInformation[1];
			const argument = decisionTableData["alternatives"]
				.find(alternative => alternative.id == sourceAlternative.id).arguments
				.find(argument => argument.id == elemId);
			if (sourceAlternative.id !== targetAlternative.id) {
				deleteLink(sourceAlternative, argument);
				createLink(targetAlternative, argument);
			} else {
				createLink(argument, criteria);
			}
			// moved arg. from criteria column to unknown column
		} else if (target.toLowerCase().includes("unknown")) {
			const sourceInformation = getElementObj(source);
			const sourceAlternative = sourceInformation[0];
			const targetAlternative = getElementObj(target);
			const criteria = sourceInformation[1];
			const argument = decisionTableData["alternatives"]
				.find(alternative => alternative.id == sourceAlternative.id).arguments
				.find(argument => argument.id == elemId);
			if (sourceAlternative.id !== targetAlternative.id) {
				deleteLink(sourceAlternative, argument);
				createLink(targetAlternative, argument);
			}
			deleteLink(argument, criteria);
			// moved arg. from one criteria column to another criteria column
		} else {
			const sourceInformation = getElementObj(source);
			const targetInformation = getElementObj(target);
			const sourceAlternative = sourceInformation[0];
			const sourceCriteria = sourceInformation[1];
			const targetAlternative = targetInformation[0];
			const targetCriteria = targetInformation[1];
			const argument = decisionTableData["alternatives"]
				.find(alternative => alternative.id == sourceAlternative.id).arguments
				.find(argument => argument.id == elemId);
			if (sourceAlternative.id !== targetAlternative.id) {
				deleteLink(sourceAlternative, argument);
				createLink(targetAlternative, argument);
			}
			deleteLink(argument, sourceCriteria);
			createLink(argument, targetCriteria);
		}
	}

	function getElementObj(element) {
		if (element.toLowerCase().includes("unknown")) {
			let alternativeId = element.replace("cellUnknown", "");
			return decisionTableData["alternatives"].find(alternative => alternative.id == alternativeId);
		} else if (element.includes("cell")) {
			let concatinated = element.replace("cell", "").split(":");
			let alternativeId = concatinated[0];
			let criteriaId = concatinated[1];
			return [decisionTableData["alternatives"].find(alternative => alternative.id == alternativeId), 
				decisionTableData["criteria"].find(criteria => criteria.id == criteriaId)];
		} else {
			return decisionTableData["alternatives"].find(object => object.id == element);
		}
	}

	function createLink(parentObj, childObj) {
		conDecAPI.createLink(parentObj.id, childObj.id, parentObj.documentationLocation, childObj.documentationLocation, null, function (data) {
		});
	}

	function deleteLink(parentObj, childObj) {
		conDecAPI.deleteLink(childObj.id, parentObj.id, childObj.documentationLocation, parentObj.documentationLocation, function (data) {
		});
	}
	
	// export ConDecDecisionTable
	global.conDecDecisionTable = new ConDecDecisionTable();
})(window);
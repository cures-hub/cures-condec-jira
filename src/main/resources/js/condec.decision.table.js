(function (global) {

	let ConDecDecisionTable = function ConDecDecisionTable() {
	};

	const auiTableID = "tbldecisionTable";
	const dropDownID = "selectDecisionProblem";
	const alternativeClmTitle = "Solution options (Alternatives and Decision)";
	let issues = [];
	let decisionTableData = [];
	let currentIssue;
	
	ConDecDecisionTable.prototype.initView = function () {		
		this.loadDecisionProblems();
		document.getElementById("link-distance-input-decision-table").addEventListener("change", function (event){
			conDecDecisionTable.loadDecisionProblems();
		});
		addOnClickEventToDecisionTableButtons();
		
		conDecDecisionGuidance.addOnClickListenerForRecommendations();
		
		// TODO Add to observable and add updateView method
		// Register/subscribe this view as an observer
		//conDecObservable.subscribe(this);
		
	};
	
	function addOnClickEventToDecisionTableButtons () {
		document.getElementById("btnAddCriterion").addEventListener("click", function (event) {
			conDecDecisionTable.showAddCriteriaToDecisionTableDialog();
		});
		document.getElementById("btnAddAlternative").addEventListener("click", function (event) {
			conDecDecisionTable.showCreateDialogForIssue();
		});
	}
	
 	/*
	 * external references: none, called in initView function
	 */
	ConDecDecisionTable.prototype.loadDecisionProblems = function () {
		console.log("conDecDecisionTable buildDecisionTable");
		const linkDistance = document.getElementById("link-distance-input-decision-table").value;		
		const elementKey = conDecAPI.getIssueKey();		
		const filterSettings = {
				"linkDistance": linkDistance,
				"selectedElement": elementKey,
				"knowledgeTypes": ["Issue", "Problem", "Goal"]
		};
		conDecAPI.getKnowledgeElements(filterSettings, function (knowledgeElements) {
			issues = knowledgeElements.filter(element => !isSelectedElement(element, elementKey) || filterSettings["knowledgeTypes"].includes(element.type));
			addDropDownItems(issues, elementKey);
		});
	};
	
	/**
	 * True if the element is the selected element in the filter settings.
	 */
	function isSelectedElement(element, elementKey) {
		return elementKey.match(element.key);
	}
	
	ConDecDecisionTable.prototype.build = function (filterSettings, viewIdentifier = "decision-table") {
		var container = document.getElementById("decision-table-container-" + viewIdentifier);
		conDecAPI.getDecisionTable(filterSettings, function (decisionTable) {
			buildDecisionTable(decisionTable, container);
		});
	}

	ConDecDecisionTable.prototype.showAddCriteriaToDecisionTableDialog = function () {
		conDecDialog.showAddCriterionToDecisionTableDialog(conDecAPI.getProjectKey(), decisionTableData["criteria"], function (data) {		
			for (key of data.keys()) {
				const tmpCriterion = data.get(key).criterion;
				if(data.get(key).status) {
					decisionTableData["criteria"].push(tmpCriterion)
				} else {
					const index = decisionTableData["criteria"].findIndex(criterion => criterion.id === tmpCriterion.id);
					decisionTableData["criteria"].splice(index, index >= 0 ? 1 : 0);
					
					for (alternative of decisionTableData["alternatives"]) {
						for (argument of alternative.arguments) {
							if (argument.hasOwnProperty("criterion") && argument.criterion.id == tmpCriterion.id) {
								deleteLink(argument, argument.criterion);
							}
						}
					}
				}
			}
			var container = document.getElementById("decision-table-container-decision-table");
			buildDecisionTable(decisionTableData, container);
		});
	}
	
	ConDecDecisionTable.prototype.showCreateDialogForIssue = function () {
		if (currentIssue) {
			conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative");		
		}
	}

	function buildDecisionTable(decisionTable, container) {
		decisionTableData = decisionTable;
		container.innerHTML = "";
		container.innerHTML += `<table id="${auiTableID}" class="aui">`;

		let table = document.getElementById(`${auiTableID}`);
		table.innerHTML += "<thead id=\"tblHeader\"><tr id=\"tblRow\">";

		let header = document.getElementById("tblRow");
		header.innerHTML += "<th id=\"alternativeClmTitle\">" + alternativeClmTitle + "</th>";
		table.innerHTML += "<tbody id=\"tblBody\">";

		addCriteriaToToDecisionTable(decisionTable["criteria"]);
		addAlternativesToDecisionTable(decisionTable["alternatives"], decisionTable["criteria"]);
		addDragAndDropSupportForArguments();
		buildCreateArgumentsButton(decisionTable["alternatives"]);

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
	function addAlternativesToDecisionTable(alternatives, criteria) {
		let body = document.getElementById("tblBody");
		
		if (Object.keys(alternatives).length === 0) {
			body.innerHTML += `<tr id="bodyRowAlternatives"></tr>`;
			let rowElement = document.getElementById(`bodyRowAlternatives`);
			rowElement.innerHTML += `<td headers="${alternativeClmTitle}">Please add at least one solution option for this issue</td>`;
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
				
				rowElement.innerHTML += `<td headers="${alternativeClmTitle}">
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
	 * 
	 * @param {Array
	 *            <KnowledgeElement> or empty object} data
	 */
	function addCriteriaToToDecisionTable(data) {
		if (Object.keys(data).length > 0) {
			for (criterion in data) {
				console.log(data[criterion].url);
				let header = document.getElementById("tblRow");
				const currentUrl = window.location;
				header.innerHTML += `<th id="criteriaClmTitle${data[criterion].id}">
					<a href="${data[criterion].url}">${data[criterion].summary}</a></th>`;
			}
		}
		let header = document.getElementById("tblRow");
		header.innerHTML += `<th style="display:none" id="criteriaClmTitleUnknown"></th>`;
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
			//rowElement.setAttribute("style", "white-space: pre;");
			
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
	function addDropDownItems(issues, elementKey) {
		let dropDown = document.getElementById(`${dropDownID}`);
		let btnAddCriterion = document.getElementById("btnAddCriterion");
		let btnAddAlternative = document.getElementById("btnAddAlternative");
		let btnAddArgument = document.getElementById("btnAddArgument");
		
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
						var container = document.getElementById("decision-table-container-decision-table");
						buildDecisionTable(decisionTable, container);
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
				var container = document.getElementById("decision-table-container-decision-table");
					buildDecisionTable(decisionTable, container);
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
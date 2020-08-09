(function (global) {

	let ConDecDecisionTable = function ConDecDecisionTable() {
	};

	const decisionTableID = "decisionTable-container";
	const auiTableID = "tbldecisionTable";
	const dropDownID = "selectDesionProblem";
	const alternativeClmTitle = "Options/Alternatives";
	let issues = [];
	let decisionTableData = [];
	let currentIssue;
	
 	/*
	 * external references: condec.jira.issue.module
	 */
	ConDecDecisionTable.prototype.loadDecisionProblems = function loadDecisionProblems(elementKey) {
		console.log("conDecDecisionTable buildDecisionTable");
		const linkDistance = document.getElementById("link-distance-input").value;

		
		conDecAPI.getDecisionIssues(elementKey, linkDistance, function (data) {
			issues = data;
			addDropDownItems(data, elementKey);
		});
		
		document.getElementById("link-distance-input-decision-table").addEventListener("change", function (event){
			const linkDistance = event.target.value;
			conDecAPI.getDecisionIssues(elementKey, linkDistance, function (data) {
				issues = data;
				addDropDownItems(data, elementKey);
			});
		});
	};

	ConDecDecisionTable.prototype.showAddCriteriaToDecisionTableDialog = function showAddCriteriaToDecisionTableDialog(projectKey) {
		conDecDialog.showAddCriterionToDecisionTableDialog(projectKey, decisionTableData["criteria"], function (data) {		
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
			buildDecisionTable(decisionTableData);
		});
	}
	
	ConDecDecisionTable.prototype.showCreateDialogForIssue = function showCreateDialogForIssue() {
		if (currentIssue) {
			conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation);		
		}
	}
	/**
	 * 
	 * @param {Array
	 *            <KnowledgeElement> or empty object} data
	 */
	function buildDecisionTable(data) {
		decisionTableData = data;
		let container = document.getElementById(decisionTableID);
		container.innerHTML = "";
		container.innerHTML += `<table id="${auiTableID}" class="aui">`;

		let table = document.getElementById(`${auiTableID}`);
		table.innerHTML += "<thead id=\"tblHeader\"><tr id=\"tblRow\">";

		let header = document.getElementById("tblRow");
		header.innerHTML += "<th id=\"alternativeClmTitle\">" + alternativeClmTitle + "</th>";
		table.innerHTML += "<tbody id=\"tblBody\">";

		addCriteriaToToDecisionTable(data["criteria"]);
		addAlternativesToDecisionTable(data["alternatives"], data["criteria"]);
		addDragAndDropSupportForArguments();
		buildCreateArgumentsButton(data["alternatives"]);

		addContextMenuToElements("argument");
		addContextMenuToElements("alternative");
	}

	function buildCreateArgumentsButton(alternatives) {
		let dropDownMenu = document.getElementById("split-container-dropdown");
		dropDownMenu.innerHTML = "";
		for (i in alternatives) {
			dropDownMenu.innerHTML +=  `<aui-item-link id="${alternatives[i].id}" class="">${alternatives[i].summary}</aui-item-link>`;
		}
		
		document.getElementById("split-container-dropdown").addEventListener("click", function (event) {
			const alternative = getElementObj(event.target.parentNode.id);
			if (alternative) {
				conDecDialog.showCreateDialog(alternative.id, alternative.documentationLocation);	
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
			rowElement.innerHTML += `<td headers="${alternativeClmTitle}">Please add at least one alternative for this issue</td>`;
		} else {
			for (let i in alternatives) {
				body.innerHTML += `<tr id="bodyRowAlternatives${alternatives[i].id}"></tr>`;
				let rowElement = document.getElementById(`bodyRowAlternatives${alternatives[i].id}`);
				rowElement.innerHTML += `<td headers="${alternativeClmTitle}">
					<div class="alternative" id="${alternatives[i].id}">${alternatives[i].summary}</div></td>`;
				if (Object.keys(criteria).length > 0) {
					for (x in criteria) {
						rowElement.innerHTML += `<td id="cell${alternatives[i].id}:${criteria[x].id}" headers="${criteria[x].summary}" class="droppable"></td>`;
					}
				}
				rowElement.innerHTML += `<td id="cellUnknown${alternatives[i].id}" headers="criteriaClmTitleUnknown" class="droppable"></td>`;
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
			for (x in data) {
				let header = document.getElementById("tblRow");
				header.innerHTML += `<th id="criteriaClmTitle${data[x].id}">${data[x].summary}</th>`;
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
			rowElement.setAttribute("style", "white-space: pre;");
			let content = "";
			if (argument.type === "Pro") {
				content = "+ " + argument.summary;
			} else if (argument.type === "Con") {
				content = "- " + argument.summary;
			}
			rowElement.innerHTML += rowElement.innerHTML.length ?
				`<br><div id="${alternative.id}:${argument.id}" class="argument draggable" draggable="true">${content}</div>` :
				`<div id="${alternative.id}:${argument.id}" class="argument draggable" draggable="true">${content}</div>`
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
	function addDropDownItems(data, elementKey) {
		let dropDown = document.getElementById(`${dropDownID}`);
		dropDown.innerHTML = "";
		if (!data.length) {
			dropDown.innerHTML += "<option disabled>Could not find any issue. Please create new issue!></otpion>";
			return;
		} else {
			for (let i = 0; i < data.length; i++) {
				if (i == 0) {
					dropDown.innerHTML += "<option value=\"" + data[i].id + "\" checked>" + data[i].summary + "</option>";
					currentIssue = data[i];
					conDecAPI.getDecisionTable(elementKey, data[i].id, data[i].documentationLocation, function (data) {
						buildDecisionTable(data);
					});
				} else {
					dropDown.innerHTML += "<option value=\"" + data[i].id + "\">" + data[i].summary + "</option>";
				}
			}
		}

		let section = document.querySelector(`#${dropDownID}`);
		section.addEventListener('change', function (e) {
		currentIssue = issues.find(o => o.id == document.getElementById(dropDownID).value);
		if (typeof currentIssue !== "undefined") {
			conDecAPI.getDecisionTable(elementKey, currentIssue.id, currentIssue.documentationLocation, function (data) {
					buildDecisionTable(data);
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
		let arguments = document.getElementsByClassName("argument");
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
			createLink(argument, targetAlternative);
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
				createLink(argument, targetAlternative);
			} else {
				createLink(argument, criteria);
			}
			// moved arg. from criteria column to unknown column
		} else if (target.toLowerCase().includes("unknown")) {
			const sourceInformation = getElementObj(source);
			const sourceAlternative = sourceInformation[0];
			const criteria = sourceInformation[1];
			const argument = decisionTableData["alternatives"]
				.find(alternative => alternative.id == sourceAlternative.id).arguments
				.find(argument => argument.id == elemId);
			if (sourceAlternative.id !== targetAlternative.id) {
				deleteLink(sourceAlternative, argument);
				createLink(argument, targetAlternative);
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
				createLink(argument, targetAlternative);
			}
			deleteLink(argument, sourceCriteria);
			createLink(targetCriteria, argument);
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
		conDecAPI.createLink(null, parentObj.id, childObj.id, parentObj.documentationLocation, childObj.documentationLocation, null, function (data) {
		});
	}

	function deleteLink(parentObj, childObj) {
		conDecAPI.deleteLink(childObj.id, parentObj.id, childObj.documentationLocation, parentObj.documentationLocation, function (data) {
		});
	}
	
	// export ConDecDecisionTable
	global.conDecDecisionTable = new ConDecDecisionTable();
})(window);
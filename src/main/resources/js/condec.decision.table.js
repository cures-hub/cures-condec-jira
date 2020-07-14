(function (global) {

	var ConDecDecisionTable = function ConDecDecisionTable() {
	};

	const decisionTableID = "decisionTable-container";
	const auiTableID = "tbldecisionTable";
	const dropDownID = "example-dropdown";
	const alternativeClmTitle = "Options/Alternatives";
	let issues = [];
	let decisionTableData = [];
	/*
    * external references: condec.jira.issue.module
    */
	ConDecDecisionTable.prototype.loadDecisionProblems = function loadDecisionProblems(elementKey) {
		console.log("conDecDecisionTable buildDecisionTable");
		conDecAPI.getDecisionIssues(elementKey, function (data) {
			issues = data;
			addDropDownItems(data, elementKey);
		});
	};

	/**
	 * 
	 * @param {Array<KnowledgeElement> or empty object} data 
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
		//addArgumentsToDecisionTable(data["alternatives"]);
		addDragAndDropSupportForArguments();

		addContextMenuToElements("argument");
		addContextMenuToElements("alternative");
	}

	/**
	 * 
	 * @param {Array<KnowledgeElement> or empty object} alternatives 
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
				rowElement.innerHTML += `<td id="cellUnknown${alternatives[i].id}" headers="criteriaClmTitleUnknown" class="droppable" style="display:none"></td>`;
				addArgumentsToDecisionTable(alternatives[i]);
			}
		}
	}

	/**
	 * 
	 * @param {Array<KnowledgeElement> or empty object} data 
	 */
	function addCriteriaToToDecisionTable(data) {
		if (Object.keys(data).length > 0) {
			for (x in data) {
				let header = document.getElementById("tblRow");
				header.innerHTML += `<th id="criteriaClmTitle${data[x].id}">${data[x].summary}</th>`;
			}
		}
		let header = document.getElementById("tblRow");
		header.innerHTML += `<th style="display:none" id="criteriaClmTitleUnknown">Unknown</th>`;
	}

	/**
	 * 
	 * @param {Array<KnowledgeElement> or empty object} alternatives 
	 */
	function addArgumentsToDecisionTable(alternative) {
		for (let index = 0; index < alternative.arguments.length; index++) {
			const argument = alternative.arguments[index];
			let rowElement;
			if (argument.hasOwnProperty("criteria")) {
				rowElement = document.getElementById(`cell${alternative.id}:${argument.criteria.id}`);
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
				`<br><div id="${argument.id}" class="argument draggable" draggable="true">${content}</div>` :
				`<div class="argument draggable" id="${argument.id}" draggable="true">${content}</div>`
		}
	}

	function addContextMenuToElements(elementID) {
		let alternatives = document.getElementsByClassName(elementID);
		for (let index = 0; index < alternatives.length; index++) {
			const alternative = alternatives[index];
			alternative.addEventListener("contextmenu", function (event) {
				event.preventDefault();
				conDecContextMenu.createContextMenu(this.id, "s", event, "tbldecisionTable");
			});
		}
	}

	/**
	 * 
	 * @param {Array or empty object} data 
	 * @param {string} elementKey 
	 */
	function addDropDownItems(data, elementKey) {
		let dropDown = document.getElementById(`${dropDownID}`);
		dropDown.innerHTML = "<aui-section id=\"ddIssueID\">";
		let dropDownSection = document.getElementById("ddIssueID");

		if (!data.length) {
			dropDownSection.innerHTML += "<aui-item-radio disabled>Could not find any issue. Please create new issue!</aui-item-radio>";
		} else {
			for (let i = 0; i < data.length; i++) {
				if (i == 0) {
					dropDownSection.innerHTML += "<aui-item-radio interactive checked>" + data[i].summary + "</aui-item-radio>";
				} else {
					dropDownSection.innerHTML += "<aui-item-radio interactive>" + data[i].summary + "</aui-item-radio>";
				}
			}
		}

		var section = document.querySelector('aui-section#ddIssueID');
		section.addEventListener('change', function (e) {
			var tagName = e.target.tagName.toLowerCase();
			if (tagName === 'aui-item-radio') {
				if (e.target.hasAttribute('checked')) {
					let tmp = issues.find(o => o.summary === e.target.textContent);
					if (typeof tmp !== "undefined") {
						conDecAPI.getDecisionTable(elementKey, tmp.id, tmp.documentationLocation, function (data) {
							console.log(data);
							buildDecisionTable(data);
						});
					} else {
						addAlternativesToTable([]);
					}
				}
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
		let elemId = ev.dataTransfer.getData("argumentId");
		let parentNode = ev.dataTransfer.getData("criteriaId");
		let elements = document.getElementsByClassName("argument");
		for (let x = 0; x < elements.length; x++) {
			const elem = elements[x];
			if (elem.id === elemId) {
				ev.target.appendChild(elem);
				updateArgumentCriteriaLink(parentNode, ev.target.id, elemId);
				break;
			}
		}
	}

	function updateArgumentCriteriaLink(source, target, elemId) {
		// moved arg. from unknown to criteria column
		if (source.toLowerCase().includes("unknown")) {
			const sourceAlternative = getElementObj(source);
			const targetInformation = getElementObj(target)
			const targetAlternative = targetInformation[0];
			const criteria = targetInformation[1];
			const argument = decisionTableData["alternatives"][sourceAlternative.id].find(argument => argument.id == elemId);
			createLink(argument, criteria);
			// moved arg. from criteria column to unknown column
		} else if (target.toLowerCase().includes("unknown")) {
			const sourceInformation = getElementObj(source);
			const sourceAlternative = sourceInformation[0];
			const criteria = sourceInformation[1];
			const argument = decisionTableData["alternatives"][sourceAlternative.id].find(argument => argument.id == elemId);
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
			console.log(sourceAlternative, targetAlternative);
			deleteLink(argument, sourceCriteria);
			createLink(argument, targetCriteria);
		}
	}

	function getElementObj(obj) {
		console.log(decisionTableData);
		console.log(obj);
		if (obj.toLowerCase().includes("unknown")) {
			let alternativeId = obj.replace("cellUnknown", "");
			return decisionTableData["alternatives"][alternativeId];
		} else if (obj.includes("cell")) {
			let concatinated = obj.replace("cell", "").split(":");
			let alternativeId = concatinated[0];
			let criteriaId = concatinated[1];
			return [decisionTableData["alternatives"].find(alternative => alternative.id == alternativeId), 
				decisionTableData["criteria"].find(criteria => criteria.id == criteriaId)];
		}
	}

	function createLink(parentObj, childObj) {
		conDecAPI.createLink(null, parentObj.id, childObj.id, parentObj.documentationLocation, childObj.documentationLocation, null, function (data) {
			console.log(data);
		});
	}

	function deleteLink(parentObj, childObj) {
		console.log(parentObj, childObj);
		conDecAPI.deleteLink(childObj.id, parentObj.id, childObj.documentationLocation, parentObj.documentationLocation, function (data) {
			console.log(data);
		});
	}
	// export ConDecDecisionTable
	global.conDecDecisionTable = new ConDecDecisionTable();
})(window);
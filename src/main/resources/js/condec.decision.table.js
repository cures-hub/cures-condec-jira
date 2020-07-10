(function (global) {

	var ConDecDecisionTable = function ConDecDecisionTable() {
	};

	const decisionTableID = "decisionTable-container";
	const auiTableID = "tbldecisionTable";
	const dropDownID = "example-dropdown";
	const alternativeClmTitle = "Options/Alternatives";
	let issues = [];
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
		let container = document.getElementById(decisionTableID);
		container.innerHTML = "";
		container.innerHTML += `<table id="${auiTableID}" class="aui">`;

		let table = document.getElementById(`${auiTableID}`);
		table.innerHTML += "<thead id=\"tblHeader\"><tr id=\"tblRow\">";

		let header = document.getElementById("tblRow");
		header.innerHTML += "<th id=\"alternativeClmTitle\">" + alternativeClmTitle + "</th>";
		table.innerHTML += "<tbody id=\"tblBody\">";

		addAlternativesToDecisionTable(data);
		addCriteriaToToDecisionTable(data);
		addArgumentsToDecisionTable(data);

		addContextMenuToElements("argument");
		addContextMenuToElements("alternative");
	}

	/**
	 * 
	 * @param {Array<KnowledgeElement> or empty object} alternatives 
	 */
	function addAlternativesToDecisionTable(alternatives) {
		let body = document.getElementById("tblBody");

		if (Object.keys(alternatives).length === 0) {
			body.innerHTML += `<tr id="bodyRowAlternatives"></tr>`;
			let rowElement = document.getElementById(`bodyRowAlternatives`);
			rowElement.innerHTML += `<td headers="${alternativeClmTitle}">Please add at least one alternative for this issue</td>`;
		} else {
			for (let key in alternatives) {
				body.innerHTML += `<tr id="bodyRowAlternatives${alternatives[key][0].id}"></tr>`;
				let rowElement = document.getElementById(`bodyRowAlternatives${alternatives[key][0].id}`);
				rowElement.innerHTML += `<td headers="${alternativeClmTitle}">
					<div class="alternative" id="${alternatives[key][0].id}">${alternatives[key][0].summary}</div></td>`;
			}
		}
	}

	/**
	 * 
	 * @param {Array<KnowledgeElement> or empty object} data 
	 */
	function addCriteriaToToDecisionTable(data) {
		if (Object.keys(data).length > 0) {
			let header = document.getElementById("tblRow");
			header.innerHTML += `<th id="criteriaClmTitlePro">Pro</th>`;
			header.innerHTML += `<th id="criteriaClmTitleCon">Con</th>`;

			for (let key in data) {
				let rowElement = document.getElementById(`bodyRowAlternatives${data[key][0].id}`);
				rowElement.innerHTML += `<td id="cellPro${data[key][0].id}" headers="Pro"></td>`;
				rowElement.innerHTML += `<td id="cellCon${data[key][0].id}" headers="Con"></td>`;
			}
		}
	}

	/**
	 * 
	 * @param {Array<KnowledgeElement> or empty object} alternatives 
	 */
	function addArgumentsToDecisionTable(alternatives) {
		for (let key in alternatives) {
			if (alternatives[key].length > 1) {
				const alternative = alternatives[key];
				for (let index = 1; index < alternative.length; index++) {
					const argument = alternative[index];
					let rowElement = document.getElementById(`cell${argument.type}${alternative[0].id}`);
					rowElement.setAttribute("class", `argument_${key}_${argument.id}`);
					rowElement.setAttribute("style", "white-space: pre;");
					let content = "";
					if (argument.type === "Pro") {
						content = "+ " + argument.summary;
					} else if (argument.type === "Con") {
						content = "- " + argument.summary;
					}
					rowElement.innerHTML += rowElement.innerHTML.length ?
						`<br><div id="${argument.id}" class="argument">${content}</div>` :
						`<div class="argument" id="${argument.id}">${content}</div>`
				}
			}
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
							buildDecisionTable(data);
						});
					} else {
						addAlternativesToTable([]);
					}
				}
			}
		});
	}

	// export ConDecDecisionTable
	global.conDecDecisionTable = new ConDecDecisionTable();
})(window);
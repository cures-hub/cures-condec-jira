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
	}

	 /**
	  * 
	  * @param {Array or empty} alternatives 
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
				rowElement.innerHTML += `<td headers="${alternativeClmTitle}">${alternatives[key][0].summary}</td>`;
			}
		}
	}

	/**
	 * 
	 * @param {*} data 
	 */
	function addCriteriaToToDecisionTable(data) {
		let header = document.getElementById("tblRow");
		header.innerHTML += `<th id=\"criteriaClmTitlePro\">Pro</th>`;
		header.innerHTML += `<th id=\"criteriaClmTitleCon\">Con</th>`;

		for (let key in data) {
			let rowElement = document.getElementById(`bodyRowAlternatives${data[key][0].id}`);
			rowElement.innerHTML += `<td id=\"cellPro${data[key][0].id}\" headers=\"Pro\"></td>`;
			rowElement.innerHTML += `<td id=\"cellCon${data[key][0].id}\" headers=\"Con\"></td>`;
		}
	}

	/**
	 * 
	 * @param {*} alternatives 
	 */
	function addArgumentsToDecisionTable(alternatives) {
		for (let key in alternatives) {
			if (alternatives[key].length > 1) {
				const alternative = alternatives[key];
				for (let index = 1; index < alternative.length; index++) {
					const argument = alternative[index];
					let rowElement = document.getElementById(`cell${argument.type}${alternative[0].id}`);
					rowElement.setAttribute("style", "white-space: pre;");
					rowElement.textContent += rowElement.textContent.length ? "\r\n" + argument.summary : argument.summary;
				}
			}
		}
	}

	/**
	 * 
	 * @param {*} data 
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
						conDecAPI.getDecisionTable(elementKey.split(":")[0] + ":" + tmp.id, function (data) {
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
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
		conDecAPI.getIssuesForDecisionProblem(elementKey, function (data) {
			console.log(data);
			addDropDownItems(data);
		});

		/*conDecAPI.getDecisionTable(elementKey, function (data) {
			if (data.qa != undefined) {
				let container = document.getElementById(decisionTableID);
				container.innerHTML = "";
				addTableHeader(container, data.qa);
				addTableBody(data.qa, data.description);
			}
		});*/
	};

	ConDecDecisionTable.prototype.updateDecisionTable = function updateDecisionTable() {

	}

	/**
	 * 
	 * @param {*} alternatives contains alternatives under a certain issue
	 */
	function addAlternativesToTable(alternatives) {
		let container = document.getElementById(decisionTableID);
		container.innerHTML = "";
		container.innerHTML += `<table id="${auiTableID}" class="aui">`;
		let table = document.getElementById(`${auiTableID}`);
		table.innerHTML += "<thead id=\"tblHeader\"><tr id=\"tblRow\">";
		let header = document.getElementById("tblRow");

		header.innerHTML += "<th id=\"alternativeClmTitle\">" + alternativeClmTitle + "</th>";
		table.innerHTML += "<thead id=\"tblHeader\"><tr id=\"tblRow\">";
		table.innerHTML += "<tbody id=\"tblBody\">";

		let body = document.getElementById("tblBody");


		if (alternatives.length) {
			for (let i = 0; i < alternatives.length; i++) {
				body.innerHTML += `<tr id="bodyRowAlternatives${alternatives[i].id}"></tr>`;
				let rowElement = document.getElementById(`bodyRowAlternatives${alternatives[i].id}`);
				rowElement.innerHTML += `<td headers="${alternativeClmTitle}">${alternatives[i].title}</td>`;
			}
		} else {
			body.innerHTML += `<tr id="bodyRowAlternatives"></tr>`;
			let rowElement = document.getElementById(`bodyRowAlternatives`);
			rowElement.innerHTML += `<td headers="${alternativeClmTitle}">Please add at least one alternative for this issue</td>`;
		}
	}

	function addTableBody2() {

	}

	function addTableHeader(container, headerData) {
		container.innerHTML += `<table id="${auiTableID}" class="aui">`;
		let table = document.getElementById(`${auiTableID}`);
		table.innerHTML += "<thead id=\"tblHeader\"><tr id=\"tblRow\">";
		let header = document.getElementById("tblRow");

		for (let index = 0; index < headerData.length; index++) {
			const el = headerData[index];
			header.innerHTML += "<th id=\"el\">" + el + "</th>";
		}
	}

	function addTableBody(headerData, descriptionData) {
		let table = document.getElementById(`${auiTableID}`);
		table.innerHTML += "<tbody id=\"tblBody\">";
		let body = document.getElementById("tblBody");

		for (let headerIndex = 0; headerIndex < headerData.length; headerIndex++) {
			const headerID = headerData[headerIndex];
			for (let bodyIndex = 0; bodyIndex < descriptionData[headerIndex].length; bodyIndex++) {
				const descriptionElement = descriptionData[headerIndex][bodyIndex] != undefined ? descriptionData[headerIndex][bodyIndex] : "";
				body.innerHTML += `<tr id="bodyRow${bodyIndex}"></tr>`;
				let rowElement = document.getElementById(`bodyRow${bodyIndex}`);
				rowElement.innerHTML += `<td headers="${headerID}">${descriptionElement}</td>`;
			}
		}
	}

	function addDropDownItems(data) {
		let dropDown = document.getElementById(`${dropDownID}`);
		dropDown.innerHTML = "<aui-section id=\"ddIssueID\">";
		let dropDownSection = document.getElementById("ddIssueID");

		if (!data.children.length) {
			dropDownSection.innerHTML += "<aui-item-radio disabled>" + data.text.title + "</aui-item-radio>";
		} else {
			issues = searchForRelevantIssues(data.children);
			console.log(issues);
			for (let i = 0; i < issues.length; i++) {
				if (i == 0) {
					dropDownSection.innerHTML += "<aui-item-radio interactive checked>" + issues[i].title + "</aui-item-radio>";
				} else {
					dropDownSection.innerHTML += "<aui-item-radio interactive>" + issues[i].title + "</aui-item-radio>";
				}
			}
		}

		var section = document.querySelector('aui-section#ddIssueID');
		section.addEventListener('change', function (e) {
			var tagName = e.target.tagName.toLowerCase();
			if (tagName === 'aui-item-radio') {
				if (e.target.hasAttribute('checked')) {
					let tmp = issues.find(o => o.title === e.target.textContent);
					if (typeof tmp !== "undefined" && tmp.hasOwnProperty("alternatives")) {
						addAlternativesToTable(tmp.alternatives);
					} else {
						addAlternativesToTable([]);
					}
					console.log(e.target.textContent, 'was selected.');
				} else {
					console.log(e.target.textContent, 'was deselected.');
				}
			}
		});
	}

	/**
	 * this function searches for certain issue types (problems/solutions)
	 * for further usage in drop down menu and decision table 
	 * @param {*} data treantNode
	 */
	function searchForRelevantIssues(data) {
		let problems = [];
		for (let i = 0; i < data.length; i++) {
			const child = data[i];
			if (child.HTMLclass === "problem") {
				problems.push({ id: child.text.desc, title: child.text.title });
			}
			if (child.hasOwnProperty("children") && child.children.length) {
				searchForChildProblems(child, child.children, problems);
			}
		}
		return problems;
	}

	function searchForChildProblems(parent, children, problems) {
		for (let i = 0; i < children.length; i++) {
			const child = children[i];
			if (child.HTMLclass === "problem") {
				problems.push({ id: child.text.desc, title: child.text.title });
			}
			if (child.HTMLclass === "solution") {
				let tmp = problems.find(o => o.id === parent.text.desc);
				if (typeof tmp !== "undefined" && !tmp.hasOwnProperty("alternatives")) {
					tmp.alternatives = [];
					tmp.alternatives.push({ id: child.text.desc, title: child.text.title });
				} else if (typeof tmp !== "undefined") {
					tmp.alternatives.push({ id: child.text.desc, title: child.text.title });
				}
			}
			if (child.hasOwnProperty("children") && child.children.length) {
				searchForChildProblems(child, child.children, problems);
			}
		}
	}

	// export ConDecDecisionTable
	global.conDecDecisionTable = new ConDecDecisionTable();
})(window);
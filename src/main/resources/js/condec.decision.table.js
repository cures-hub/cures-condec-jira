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
			console.log(data);
			issues = data;
			addDropDownItems(data, elementKey);
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

		if (Object.keys(alternatives).length === 0) {
			body.innerHTML += `<tr id="bodyRowAlternatives"></tr>`;
			let rowElement = document.getElementById(`bodyRowAlternatives`);
			rowElement.innerHTML += `<td headers="${alternativeClmTitle}">Please add at least one alternative for this issue</td>`;
		} else {
			for (let key in alternatives) {
				body.innerHTML += `<tr id="bodyRowAlternatives${alternatives[key][0].id}"></tr>`;
				let rowElement = document.getElementById(`bodyRowAlternatives${alternatives[key][0].id}`);
				rowElement.innerHTML += `<td headers="${alternativeClmTitle}">${alternatives[key][0].summary}</td>`;
				if(alternatives[key].length > 1) {
					addAlternativeBody(table, header);
				}
			}
		}
	}

	function addAlternativeBody(table, header, data) {
		
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

	function addDropDownItems(data, elementKey) {
		let dropDown = document.getElementById(`${dropDownID}`);
		dropDown.innerHTML = "<aui-section id=\"ddIssueID\">";
		let dropDownSection = document.getElementById("ddIssueID");

		if (!data.length) {
			dropDownSection.innerHTML += "<aui-item-radio disabled>Could not find any issue. Please create new issue!</aui-item-radio>";
		} else {
			console.log(data);
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
					console.log(e.target.textContent);
					let tmp = issues.find(o => o.summary === e.target.textContent);
					if (typeof tmp !== "undefined") {
						console.log(elementKey.split(":")[0] + ":" + tmp.id);
						conDecAPI.getDecisionTable(elementKey.split(":")[0] + ":" + tmp.id, function (data) {
							addAlternativesToTable(data);
							console.log(data);
						});
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

	// export ConDecDecisionTable
	global.conDecDecisionTable = new ConDecDecisionTable();
})(window);
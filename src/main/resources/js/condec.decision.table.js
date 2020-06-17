(function (global) {

	var ConDecDecisionTable = function ConDecDecisionTable() {
	};

	const decisionTableID = "decisionTable-container";
	const auiTableID = "tbldecisionTable";
    /*
    * external references: condec.jira.issue.module
    */
	ConDecDecisionTable.prototype.buildTable = function buildTable(elementKey) {
		console.log("conDecDecisionTable buildDecisionTable");
		conDecAPI.getDecisionTable(elementKey, function (data) {
			if (data.qa != undefined) {
				let container = document.getElementById(decisionTableID);
				container.innerHTML = "";
				addTableHeader(container, data.qa);
				addTableBody(data.qa, data.score, data.description);
			}
		});
	};

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

	function addTableBody(headerData, scoreData, descriptionData) {
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
	// export ConDecDecisionTable
	global.conDecDecisionTable = new ConDecDecisionTable();
})(window);
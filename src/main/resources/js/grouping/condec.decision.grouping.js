(function(global) {

	var ConDecDecisionGroups = function ConDecDecisionGroups() {
	};

	ConDecDecisionGroups.prototype.initView = function() {
		console.log("ConDecDecisionGroups initView");

		conDecObservable.subscribe(this);
		this.buildMatrix();
	};

	ConDecDecisionGroups.prototype.buildMatrix = function() {
		const body = document.getElementById("group-table-body");
		var knowledgeTypesWithoutCode = conDecAPI.getKnowledgeTypes();
		var index = knowledgeTypesWithoutCode.indexOf("Code");
		knowledgeTypesWithoutCode.splice(index, 1);

        var filterSettings = {
        		"projectKey" : conDecAPI.projectKey
        };
		conDecGroupingAPI.getDecisionGroupsMap(filterSettings, function(error, decisionGroupsMap) {
			for (var [group, elements] of decisionGroupsMap.entries()) {
				codeFiles = elements.filter(element => element.type === "Code");
				newTableRow(body, group, elements.length, codeFiles.length);
			}
		});
	};

	ConDecDecisionGroups.prototype.updateView = function() {
		const body = document.getElementById("group-table-body");
		body.innerHTML = "";
		this.buildMatrix();
	};

	function newTableRow(body, row1, row2, row3) {
		const row = document.createElement("tr");
		const tableRowElement = document.createElement("td");
		tableRowElement.innerHTML = row1;
		tableRowElement.addEventListener("contextmenu", function(e) {
			e.preventDefault();
			conDecContextMenu.createContextMenu(row1, "groups", e, null);
		}, false);
		row.appendChild(tableRowElement);
		const tableRowElement2 = document.createElement("td");
		tableRowElement2.innerHTML = row2;
		row.appendChild(tableRowElement2);
		const tableRowElement3 = document.createElement("td");
		tableRowElement3.innerHTML = row3;
		row.appendChild(tableRowElement3);
		body.appendChild(row);
	}

	global.conDecDecisionGroups = new ConDecDecisionGroups();
})(window);
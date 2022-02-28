(function(global) {

	var ConDecDecisionGroups = function ConDecDecisionGroups() {
	};

	ConDecDecisionGroups.prototype.initView = function() {
		console.log("ConDecDecisionGroups initView");

		conDecFiltering.fillFilterElements("decision-groups", ["Decision", "Solution", "Issue", "Problem"]);
		conDecFiltering.addOnClickEventToFilterButton("decision-groups", () => conDecDecisionGroups.updateView());

		conDecObservable.subscribe(this);
		this.updateView();
	};

	ConDecDecisionGroups.prototype.buildMatrix = function() {
		const body = document.getElementById("group-table-body");
		var knowledgeTypesWithoutCode = conDecAPI.getKnowledgeTypes();
		var index = knowledgeTypesWithoutCode.indexOf("Code");
		knowledgeTypesWithoutCode.splice(index, 1);

		var filterSettings = conDecFiltering.getFilterSettings("decision-groups");
		filterSettings["projectKey"] = conDecAPI.projectKey;
		conDecGroupingAPI.getDecisionGroupsMap(filterSettings, function(error, decisionGroupsMap) {
			for (var [group, elements] of decisionGroupsMap.entries()) {
				newTableRow(body, group, elements);
			}
		});
	};

	ConDecDecisionGroups.prototype.updateView = function() {
		const body = document.getElementById("group-table-body");
		body.innerHTML = "";
		this.buildMatrix();
	};

	function newTableRow(body, groupName, elements) {
		const row = document.createElement("tr");
		const tableRowElement = document.createElement("td");
		tableRowElement.innerHTML = groupName;
		tableRowElement.addEventListener("contextmenu", function(e) {
			e.preventDefault();
			conDecContextMenu.createContextMenu(groupName, "groups", e, null);
		}, false);
		row.appendChild(tableRowElement);
		
		const tableRowElement2 = document.createElement("td");
		tableRowElement2.innerHTML = elements.length;
		row.appendChild(tableRowElement2);
		
		const tableRowElement3 = document.createElement("td");
		for (element of elements) {
			var link = document.createElement("a");
			link.classList = "navigationLink";
			link.innerText = element.type + ": " + element.summary;
			link.title = element.key;
			link.href = decodeURIComponent(element.url);
			link.target = "_blank";
			tableRowElement3.appendChild(link);
		}		
		row.appendChild(tableRowElement3);
		body.appendChild(row);
	}

	global.conDecDecisionGroups = new ConDecDecisionGroups();
})(window);
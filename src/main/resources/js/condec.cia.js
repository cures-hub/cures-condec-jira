/*
 *	This view provides a support for change impact analysis on the knowledge graph.
 */
(function (global) {

	var ConDecCia = function ConDecCia() {
		this.selectedElement = conDecAPI.getIssueKey();
		this.linkTypes = conDecAPI.getAllLinkTypes();
	};

	ConDecCia.prototype.initView = function () {
		console.log("conDecCia initView");

		// Fill filter elements
		conDecFiltering.initDropdown("knowledge-type-dropdown-cia", conDecAPI.getKnowledgeTypes(), ["Alternative", "Decision", "Issue", "Argument"]);
		conDecFiltering.initDropdown("status-dropdown-cia", conDecAPI.knowledgeStatus);
		conDecFiltering.initDropdown("propagation-rule-dropdown-cia", conDecAPI.getPropagationRules(),[]);
		initTextDropdown("cia_menu_items", this.linkTypes);
		conDecAPI.getCiaSettings(conDecAPI.getProjectKey(), loadCiaSettings);

		addOnClickEventToFilterButton();
		//conDecObservable.subscribe(this); // updateView method needs to be implemented
	};

	function initTextDropdown(dropdownId, items) {
		var dropdown = document.getElementById(dropdownId);
		if (dropdown === null || dropdown === undefined || dropdown.length === 0) {
			return null;
		}
		dropdown.innerHTML = "";
		html = "";
		var outward = items.filter(item => !item.startsWith("is ")).sort()
		var inward = items.filter(item => item.startsWith("is ")).sort()
		for (var index = 0; index < outward.length; index++) {

			html += "<li>"
			html += "<a href='#'><input onclick='event.stopPropagation()' type='number' size='5' step='0.01'  min='0' max='1' value='1' id='" + outward[index] + "' /><span style='display: inline-block;width:150px'>" + outward[index] + "</span>"
			html += (inward[index]) ? "<input onclick='event.stopPropagation()' type='number' size='5' step='0.01'  min='0' max='1' value='1' id='" + inward[index] + "' /><span style='display: inline-block;width:150px'>" + inward[index] + "</span></a>" : ""
			html += "</li>";
		}
		dropdown.innerHTML = html;
	}

	function loadCiaSettings(error, response) {
		$("#decay-input-cia")[0].value = response["decayValue"];
		$("#threshold-input-cia")[0].value = response["threshold"];
		const obj = response["linkImpact"];
		for (let key in obj) {
			document.getElementById(key).value = obj[key]
		}
	}

	function addOnClickEventToFilterButton() {
		conDecFiltering.addOnClickEventToFilterButton("cia", function (filterSettings) {
			if (!filterSettings["selectedElement"]) {
				filterSettings["selectedElement"] = conDecAPI.getIssueKey();
			}
			if (!filterSettings["selectedElement"]) {
				filterSettings["selectedElement"] = conDecCia.selectedElement;
			}
			if (filterSettings["selectedElement"]) {
				filterSettings["linkImpact"] = extractLinkTypeSettings();
				updateCiaView(filterSettings);
			}
		});
	}

	function updateCiaView(filterSettings) {
		console.log("Update View")
		const displayType = filterSettings["displayType"];
		filterSettings["isCiaRequest"] = true;
		clearDisplayContent();
		switch (displayType) {
			case "Tree":
				renderTree(filterSettings);
				break;
			case "Graph":
				conDecVis.buildVis(filterSettings, "cia-container");
				break;
			case "Matrix":
				conDecMatrix.buildMatrix(filterSettings, "cia");
				break;
			default:
				renderTree(filterSettings);
				break;
		}
	}

	function clearDisplayContent() {
		document.getElementById("cia-container").innerHTML = "";
		document.getElementById("matrix-header-row-cia").innerHTML = "";
		document.getElementById("matrix-body-cia").innerHTML = "";
	}

	function renderTree(filterSettings) {
		conDecTreeViewer.buildTreeViewer(filterSettings, "#cia-container", "#search-input-cia", "cia-container");
	}

	function extractLinkTypeSettings() {
		var result = {}
		var items = conDecCia.linkTypes;

		for (var index = 0; index < items.length; index++) {
			result[items[index]] = document.getElementById(items[index]).value
		}
		return result;
	}

	global.conDecCia = new ConDecCia();
})(window);
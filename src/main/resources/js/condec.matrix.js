/**
 * Creates an adjancency matrix from the knowledge graph or its respective
 * filtered subgraph.
 * 
 * Requires: conDecAPI, conDecContextMenu, conDecFiltering, conDecObservable
 * 
 * Is required by: no other module
 * 
 * Is referenced in HTML by: matrix.vm
 */
(function(global) {

	/* private vars */
	var conDecObservable = null;
	var conDecAPI = null;

	var ConDecMatrix = function ConDecMatrix() {
	};

	ConDecMatrix.prototype.init = function(_conDecAPI, _conDecObservable) {
		console.log("ConDecMatrix init");
		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)) {
			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;

			// Fill HTML elements for filter criteria
			conDecFiltering.fillFilterElements("matrix", [ "Decision" ]);

			// Add event listener on buttons
			conDecFiltering.addOnClickEventToFilterButton("matrix", function(filterSettings) {
				conDecMatrix.updateView();
			});

			// Register/subscribe this view as an observer
			conDecObservable.subscribe(this);

			return true;
		}
		return false;
	};

	ConDecMatrix.prototype.buildMatrix = function() {
		AJS.$("#simple-tooltip").tooltip();
		var filterSettings = conDecFiltering.getFilterSettings("matrix");
		filterSettings["documentationLocations"] = null;
		conDecAPI.getMatrix(filterSettings, function(data) {
			const headerRow = document.getElementById("matrix-header-row");
            const firstRowHeaderElement = document.createElement("th");
            firstRowHeaderElement.classList.add("columnHeader");
            headerRow.appendChild(firstRowHeaderElement);
			
			for ( let d in data.headerElements) {
				const headerCell = newTableHeaderElement(data.headerElements[d], "columnHeader");
				headerRow.insertAdjacentElement("beforeend", headerCell);
			}

			const tbody = document.getElementById("matrix-body");
			for ( let d in data.coloredRows) {
				const row = data.coloredRows[d];
				tbody.appendChild(newTableRow(row, data.headerElements[d]));
			}

			conDecMatrix.buildLegend(data.linkTypesWithColor);
		});
	};

	ConDecMatrix.prototype.updateView = function() {
		document.getElementById("matrix-header-row").innerHTML = "";
		document.getElementById("matrix-body").innerHTML = "";
		conDecMatrix.buildMatrix();
	};

	function newTableHeaderElement(knowledgeElement, styleClass) {
		const headerCell = document.createElement("th");
		headerCell.addEventListener("contextmenu", function(event) {
			event.preventDefault();
			conDecContextMenu.createContextMenu(knowledgeElement.id, knowledgeElement.documentationLocation, event,
			        null);
		});
		headerCell.classList.add(styleClass);
		const div = document.createElement("div");
		div.innerText = knowledgeElement.type + ": " + knowledgeElement.summary;
		headerCell.title = knowledgeElement.type + ": " + knowledgeElement.summary;
		AJS.$(headerCell).tooltip();
		headerCell.appendChild(div);
		return headerCell;
	}

	function newTableRow(row, header) {
		const tableRow = document.createElement("tr");
		tableRow.appendChild(newTableHeaderElement(header, "rowHeader"));
		for ( let d in row) {
			tableRow.appendChild(newTableElement(row[d]));
		}
		return tableRow;
	}

	function newTableElement(color) {
		const tableRowCell = document.createElement("td");
		if (!color.match("white")) {
			tableRowCell.style.backgroundColor = color;
		}
		return tableRowCell;
	}

	ConDecMatrix.prototype.buildLegend = function(linkTypesWithColor) {
		const legend = document.getElementById("legend-list");
		legend.innerHTML = "";
		for ( let linkType in linkTypesWithColor) {
			const li = document.createElement("li");
			li.innerText = linkType;
			const span = document.createElement("span");
			span.style.background = linkTypesWithColor[linkType];
			li.appendChild(span);
			legend.appendChild(li);
		}
	};

	/*
	 * Init Helpers
	 */
	function isConDecAPIType(conDecAPI) {
		if (!(conDecAPI !== undefined && conDecAPI.getDecisionKnowledgeElement !== undefined && typeof conDecAPI.getDecisionKnowledgeElement === 'function')) {
			console.warn("ConDecMatrix: invalid ConDecAPI object received.");
			return false;
		}
		return true;
	}

	function isConDecObservableType(conDecObservable) {
		if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
			console.warn("ConDecMatrix: invalid ConDecObservable object received.");
			return false;
		}
		return true;
	}

	global.conDecMatrix = new ConDecMatrix();
})(window);
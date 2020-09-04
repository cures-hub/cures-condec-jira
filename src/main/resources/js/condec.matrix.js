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
			conDecFiltering.fillFilterElements("matrix", [ "Decision", "Alternative" ]);

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
		var filterSettings = conDecFiltering.getFilterSettings("matrix");
		filterSettings["documentationLocations"] = null;
		conDecAPI.getMatrix(filterSettings, function(data) {
			const matrix = document.getElementById("matrix");
			const thead = document.createElement("thead");
			const headerRow = document.createElement("tr");
			const firstRowHeaderElement = document.createElement("th");
			firstRowHeaderElement.innerText = "";
			firstRowHeaderElement.classList.add("columnHeader");
			headerRow.appendChild(firstRowHeaderElement);

			for ( let d in data.headerElements) {
				const headerCell = newTableHeaderElement(data.headerElements[d], "columnHeader");
				headerRow.appendChild(headerCell);
			}

			thead.appendChild(headerRow);
			matrix.appendChild(thead);

			const tbody = document.createElement("tbody");

			for ( let d in data.coloredRows) {
				const row = data.coloredRows[d];
				tbody.appendChild(newTableRow(row, data.headerElements[d]));
			}

			matrix.appendChild(tbody);

			conDecMatrix.buildLegend(data.linkTypesWithColor);
		});
	};

	ConDecMatrix.prototype.updateView = function() {
		const matrix = document.getElementById("matrix");
		matrix.innerHTML = "";
		conDecMatrix.buildMatrix();
	};

	function newTableHeaderElement(knowledgeElement, styleClass) {
		const headerColumn = document.createElement("th");
		headerColumn.addEventListener("contextmenu", function(event) {
			event.preventDefault();
			conDecContextMenu.createContextMenu(knowledgeElement.id, knowledgeElement.documentationLocation, event,
			        null);
		});
		headerColumn.classList.add(styleClass);
		const div = document.createElement("div");
		div.innerText = knowledgeElement.type + ": " + knowledgeElement.summary;
		headerColumn.appendChild(div);
		return headerColumn;
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
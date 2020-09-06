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
	var headerElements = [];

	var ConDecMatrix = function ConDecMatrix() {
	};

	ConDecMatrix.prototype.initView = function() {
		console.log("ConDecMatrix initView");
		
		// Fill HTML elements for filter criteria
		conDecFiltering.fillFilterElements("matrix", [ "Decision" ]);
		conDecFiltering.fillDatePickers("matrix", 30);

		// Add event listener on buttons
		conDecFiltering.addOnClickEventToFilterButton("matrix", function(filterSettings) {
			conDecMatrix.updateView();
		});

		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);
		
		// Fill view
		this.buildMatrix();
	};

	ConDecMatrix.prototype.buildMatrix = function() {
		AJS.$("#simple-tooltip").tooltip();
		var filterSettings = conDecFiltering.getFilterSettings("matrix");
		filterSettings["documentationLocations"] = null;
		conDecAPI.getMatrix(filterSettings, function(matrix) {
			this.headerElements = matrix.headerElements;
			let headerRow = document.getElementById("matrix-header-row");
			headerRow.innerHTML = "";
			let firstRowHeaderCell = document.createElement("th");
			firstRowHeaderCell.classList.add("columnHeader");
			headerRow.appendChild(firstRowHeaderCell);

			for ( let d in matrix.headerElements) {
				const headerCell = newTableHeaderCell(matrix.headerElements[d], "columnHeader");
				headerRow.insertAdjacentElement("beforeend", headerCell);
			}

			let tbody = document.getElementById("matrix-body");
			tbody.innerHTML = "";
			for ( let d in matrix.links) {
				let row = matrix.links[d];
				tbody.appendChild(newTableRow(row, matrix.headerElements[d], d));
			}

			conDecMatrix.buildLegend(matrix.linkTypesWithColor);
		});
	};

	ConDecMatrix.prototype.updateView = function() {
		conDecMatrix.buildMatrix();
	};

	function newTableHeaderCell(knowledgeElement, styleClass) {
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

	function newTableRow(row, sourceElement, positionX) {
		const tableRow = document.createElement("tr");
		tableRow.appendChild(newTableHeaderCell(sourceElement, "rowHeader"));
		for ( let d in row) {
			tableRow.appendChild(newTableCell(row[d], positionX, d));
		}
		return tableRow;
	}

	function newTableCell(link, positionX, positionY) {
		const tableRowCell = document.createElement("td");
		if (positionX === positionY) {
			tableRowCell.style.backgroundColor = "lightGray";
		}
		if (link !== null) {
			tableRowCell.style.backgroundColor = link.color;
			const sourceElement = this.headerElements[positionX];
			const targetElement = this.headerElements[positionY];
			tableRowCell.title = "Link Type: " + link.type + "; From " + sourceElement.type + ": "
			        + sourceElement.summary + " to " + targetElement.type + ": " + targetElement.summary;
			AJS.$(tableRowCell).tooltip();
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

	global.conDecMatrix = new ConDecMatrix();
})(window);
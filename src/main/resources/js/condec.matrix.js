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
	var linkTypesWithColor = null;

	var ConDecMatrix = function ConDecMatrix() {
	};

	ConDecMatrix.prototype.initView = function(isJiraIssueView = false) {
		console.log("ConDecMatrix initView");
		
		// Fill HTML elements for filter criteria and add on click listener
		if (isJiraIssueView) {
			conDecFiltering.fillFilterElements("matrix");
			conDecFiltering.addOnClickEventToFilterButton("matrix", function(filterSettings) {
				issueKey = conDecAPI.getIssueKey();
				filterSettings["selectedElement"] = issueKey;
				conDecMatrix.buildMatrix(filterSettings);
			});
		} else {
			conDecFiltering.fillFilterElements("matrix", [ "Decision" ]);
			conDecFiltering.fillDatePickers("matrix", 30);
			conDecFiltering.addOnClickEventToFilterButton("matrix", function(filterSettings) {
				conDecMatrix.buildMatrix(filterSettings);
			});
			document.getElementById("link-distance-input-label-matrix").remove();
			document.getElementById("link-distance-input-matrix").remove();
		}		

		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);
		
		// Fill view
		this.updateView();
	};

	ConDecMatrix.prototype.buildMatrix = function(filterSettings, viewIdentifier = "") {
		conDecAPI.getMatrix(filterSettings, function(matrix) {
			this.headerElements = matrix.headerElements;
			
			let headerRow = document.getElementById("matrix-header-row" + viewIdentifier);
			headerRow.innerHTML = "";
			let firstRowHeaderCell = document.createElement("th");
			firstRowHeaderCell.classList.add("columnHeader");
			headerRow.appendChild(firstRowHeaderCell);

			for ( let d in matrix.headerElements) {
				const headerCell = newTableHeaderCell(matrix.headerElements[d], "columnHeader");
				headerRow.insertAdjacentElement("beforeend", headerCell);
			}

			let tbody = document.getElementById("matrix-body" + viewIdentifier);
			tbody.innerHTML = "";
			for ( let d in matrix.links) {
				let row = matrix.links[d];
				tbody.appendChild(newTableRow(row, matrix.headerElements[d], d));
			}
			
			conDecMatrix.buildLegend(matrix.linkTypesWithColor);
		});	
	};

	ConDecMatrix.prototype.updateView = function() {
		document.getElementById("filter-button-matrix").click();
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
			return tableRowCell;
		}
		const sourceElement = this.headerElements[positionX];
		const targetElement = this.headerElements[positionY];
		
		var linkType = null;
		if (link !== null) {
			tableRowCell.style.backgroundColor = link.color;
			tableRowCell.title = sourceElement.type + ": " + sourceElement.summary + " is linked with type " + link.type
				+" to " + targetElement.type + ": " + targetElement.summary;
			linkType = link.type;
		} else {
			tableRowCell.title = sourceElement.type + ": " + sourceElement.summary + " is not linked to " 
				+ targetElement.type + ": " + targetElement.summary;
		}
		AJS.$(tableRowCell).tooltip();
		tableRowCell.addEventListener("click", function(event) {
			conDecDialog.showLinkDialog(sourceElement.id, sourceElement.documentationLocation, 
					targetElement.id, targetElement.documentationLocation, linkType);
		});
		
		tableRowCell.addEventListener("contextmenu", function(event) {
			event.preventDefault();
			conDecContextMenu.createContextMenu(sourceElement.id, sourceElement.documentationLocation, event, "matrix-body", 
					targetElement.id, targetElement.documentationLocation, linkType);
		});

		return tableRowCell;
	}

	ConDecMatrix.prototype.buildLegend = function (linkTypesWithColor) {
		const legend = document.getElementById("legend");
		legend.innerHTML = "<b>Relationship Types:</b>";
		for ( let linkType in linkTypesWithColor) {
			const coloredBlock = document.createElement("div");
			coloredBlock.classList.add("legend-labels");
			coloredBlock.style.background = linkTypesWithColor[linkType];			
			coloredBlock.title = linkType;
			AJS.$(coloredBlock).tooltip();
			legend.insertAdjacentElement("beforeend", coloredBlock);
			legend.insertAdjacentText("beforeend", linkType);
		}
	};

	global.conDecMatrix = new ConDecMatrix();
})(window);
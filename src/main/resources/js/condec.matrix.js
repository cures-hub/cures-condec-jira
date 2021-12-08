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
	
	const viewIdentifier = "adjacency-matrix";

	var ConDecMatrix = function ConDecMatrix() {
		this.headerElementsWithHighlighting = [];
		this.emptyColumns = [];
	};

	ConDecMatrix.prototype.initView = function(isJiraIssueView = false) {
		console.log("ConDecMatrix initView");

		// Fill HTML elements for filter criteria and add on click listener
		if (isJiraIssueView) {
			conDecFiltering.fillFilterElements(viewIdentifier);
			document.getElementById("is-transitive-links-input-" + viewIdentifier).checked = true;
			document.getElementById("is-decision-knowledge-only-input-" + viewIdentifier).checked = true;
		} else {
			conDecFiltering.fillFilterElements(viewIdentifier, ["Decision"]);
			conDecFiltering.fillDatePickers(viewIdentifier, 120);
		}
		conDecFiltering.addOnClickEventToFilterButton(viewIdentifier, conDecMatrix.buildMatrix);
		conDecFiltering.addOnClickEventToChangeImpactButton(viewIdentifier, conDecMatrix.buildMatrix);

		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);

		// Fill view
		this.updateView();
	};

	/*
	 * external references: condec.knowledge.page.js and condec.rationale.backlog.js
	 */
	ConDecMatrix.prototype.buildMatrix = function(filterSettings, viewIdentifier = "adjacency-matrix") {
		conDecAPI.getMatrix(filterSettings, function(matrix) {
			conDecMatrix.headerElementsWithHighlighting = matrix.headerElementsWithHighlighting;
			let headerRow = document.getElementById("matrix-header-row-" + viewIdentifier);
			headerRow.innerHTML = "";
			let firstRowHeaderCell = document.createElement("th");
			firstRowHeaderCell.classList.add("columnHeader");
			headerRow.appendChild(firstRowHeaderCell);

			transposedLinkMatrix = transpose(matrix.links);
			conDecMatrix.emptyColumns = [];

			for (let d in matrix.headerElementsWithHighlighting) {
				let col = transposedLinkMatrix[d];
				if (isLinkIncluded(col)) {
					const headerCell = newTableHeaderCell(matrix.headerElementsWithHighlighting[d], "columnHeader");
					headerRow.insertAdjacentElement("beforeend", headerCell);
				} else {
					conDecMatrix.emptyColumns.push(d);
				}
			}

			let tbody = document.getElementById("matrix-body-" + viewIdentifier);
			tbody.innerHTML = "";
			for (let d in matrix.links) {
				let row = matrix.links[d];
				if (isLinkIncluded(row)) {
					tbody.appendChild(newTableRow(row, matrix.headerElementsWithHighlighting[d], d));
				}
			}

			conDecMatrix.buildLegend(matrix.linkTypesWithColor);
		});
	};

	function isLinkIncluded(links) {
		for (var i = 0, len = links.length; i < len; i += 1) {
			if (links[i] !== null) {
				return true;
			}
		}
		return false;
	}

	function transpose(matrix) {
		if (matrix.length > 0) {
			return matrix[0].map((col, i) => matrix.map(row => row[i]));
		}
		return matrix;
	}

	ConDecMatrix.prototype.updateView = function() {
		document.getElementById("filter-button-" + viewIdentifier).click();
	};

	function newTableHeaderCell(knowledgeElementWithColors, styleClass) {
		const headerCell = document.createElement("th");
		var knowledgeElement = knowledgeElementWithColors.element;
		var textColor = knowledgeElementWithColors.qualityColor;
		var bgColor = knowledgeElementWithColors.changeImpactColor;
		headerCell.addEventListener("contextmenu", function(event) {
			event.preventDefault();
			conDecContextMenu.createContextMenu(knowledgeElement.id, knowledgeElement.documentationLocation, event,
				null);
		});
		headerCell.addEventListener("click", function(event) {
			event.preventDefault();
			document.getElementById("selected-element-" + viewIdentifier).innerText = knowledgeElement.key;
		});
		const div = document.createElement("div");
		div.innerText = knowledgeElement.type + ": " + knowledgeElement.summary;
		headerCell.title = knowledgeElement.type + ": " + knowledgeElement.summary;

		headerCell.classList.add(styleClass);
		if (textColor !== "#000000") {
			headerCell.style.color = textColor;
			headerCell.title = knowledgeElementWithColors.qualityProblemExplanation;
		}
		if (bgColor !== undefined && bgColor !== "#FFFFFF") {
			div.classList.add("ciaHighlighted");
			headerCell.style.color = "#000000";
			headerCell.style.backgroundColor = bgColor;
			headerCell.title = knowledgeElementWithColors.changeImpactExplanation;
		}
		
		// AJS.$(headerCell).tooltip();
		headerCell.appendChild(div);
		return headerCell;
	}

	function newTableRow(row, sourceElementWithColors, positionX) {
		const tableRow = document.createElement("tr");
		tableRow.appendChild(newTableHeaderCell(sourceElementWithColors, "rowHeader"));
		for (let d in row) {
			if (!conDecMatrix.emptyColumns.includes(d)) {
				tableRow.appendChild(newTableCell(row[d], positionX, d));
			}
		}
		return tableRow;
	}

	function newTableCell(link, positionX, positionY) {
		const tableRowCell = document.createElement("td");
		if (positionX === positionY) {
			tableRowCell.style.backgroundColor = "lightGray";
			return tableRowCell;
		}
		const sourceElement = conDecMatrix.headerElementsWithHighlighting[positionX].element;
		const targetElement = conDecMatrix.headerElementsWithHighlighting[positionY].element;

		var linkType = null;
		if (link !== null) {
			tableRowCell.style.backgroundColor = link.color;
			tableRowCell.title = sourceElement.type + ": " + sourceElement.summary + " is linked with type " + link.type
				+ " to " + targetElement.type + ": " + targetElement.summary;
			linkType = link.type;
		} else {
			tableRowCell.title = sourceElement.type + ": " + sourceElement.summary + " is not linked to "
				+ targetElement.type + ": " + targetElement.summary;
		}
		AJS.$(tableRowCell).tooltip();
		tableRowCell.addEventListener("click", function(event) {
			if (linkType === "transitive") {
				conDecAPI.showFlag("warning", "Transitive links should not be persisted.");
			} else {
				conDecDialog.showLinkDialog(sourceElement.id, sourceElement.documentationLocation,
					targetElement.id, targetElement.documentationLocation, linkType);
			}
		});

		tableRowCell.addEventListener("contextmenu", function(event) {
			event.preventDefault();
			conDecContextMenu.createContextMenu(sourceElement.id, sourceElement.documentationLocation, event, "matrix-body",
				targetElement.id, targetElement.documentationLocation, linkType);
		});

		return tableRowCell;
	}

	ConDecMatrix.prototype.buildLegend = function(linkTypesWithColor) {
		const legend = document.getElementById("legend");
		legend.innerHTML = "<b>Relationship Types:</b>";
		for (let linkType in linkTypesWithColor) {
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

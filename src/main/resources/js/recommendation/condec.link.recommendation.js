/**
 * This module implements the link suggestion and duplicate detection.
 */
(function(global) {

	let ConDecLinkRecommendation = function() {
		this.projectKey = conDecAPI.getProjectKey();
	};

	ConDecLinkRecommendation.prototype.init = function() {
		this.issueId = JIRA.Issue.getIssueId();

		// Duplicates
		this.duplicateResultsTableElement = document.getElementById("duplicate-results-table");
		this.duplicateResultsTableContentElement = document.getElementById("table-content-duplicate");

		// Related
		this.loadingSpinnerElement = document.getElementById("loading-spinner");
		this.resultsTableElement = document.getElementById("results-table");
		this.resultsTableContentElement = document.getElementById("table-content");

		this.loadData();
		this.loadDuplicateData();
	}

	ConDecLinkRecommendation.prototype.discardDuplicateRecommendation = function(index) {
		conDecLinkRecommendationAPI.discardRecommendation(this.projectKey, conDecLinkRecommendationAPI.currentDuplicates[index])
			.then((data) => {
				conDecAPI.showFlag("success", "Discarded duplicate recommendation sucessfully!");
				this.loadDuplicateData();
			})
			.catch((error) => displayErrorMessage(error));
	};

	ConDecLinkRecommendation.prototype.discardRecommendation = function(index) {
		conDecLinkRecommendationAPI.discardRecommendation(this.projectKey, conDecLinkRecommendationAPI.currentLinkRecommendations[index])
			.then((data) => {
				conDecAPI.showFlag("success", "Discarded link recommendation successfully!");
				this.loadData();
			})
			.catch((error) => displayErrorMessage(error));
	};

	ConDecLinkRecommendation.prototype.undoDiscardRecommendation = function(index) {
		conDecLinkRecommendationAPI.undoDiscardRecommendation(this.projectKey, conDecLinkRecommendationAPI.currentLinkRecommendations[index])
			.then((data) => {
				conDecAPI.showFlag("success", "Discarding link recommendation successfully undone!");
				this.loadData();
			})
			.catch((error) => displayErrorMessage(error));
	};

	ConDecLinkRecommendation.prototype.undoDiscardDuplicateRecommendation = function(index) {
		conDecLinkRecommendationAPI.undoDiscardRecommendation(this.projectKey, conDecLinkRecommendationAPI.currentDuplicates[index])
			.then((data) => {
				conDecAPI.showFlag("success", "Discarding duplicate recommendation successfully undone!");
				this.loadDuplicateData();
			})
			.catch((error) => displayErrorMessage(error));
	};

	ConDecLinkRecommendation.prototype.markAsDuplicate = function(index) {
		let duplicateElement = conDecLinkRecommendationAPI.currentDuplicates[index].target;

		let self = this;
		conDecAPI.createLink(this.issueId, duplicateElement.id, "i", duplicateElement.documentationLocation, "duplicate", () => self.loadDuplicateData());
	};

	//-----------------------------------------
	//			Generate table (Related)
	//-----------------------------------------
	ConDecLinkRecommendation.prototype.displayRelatedElements = function(relatedElements) {
		if (relatedElements.length === 0) {
			//reset table content to empty
			this.resultsTableContentElement.innerHTML = "<i>No related knowledge elements found!</i>";
		} else {
			//reset table content to empty
			this.resultsTableContentElement.innerHTML = "";
			//append table rows with possibly related issues
			for (let index in relatedElements) {
				let row = generateTableRow(relatedElements[index], index);
				this.resultsTableContentElement.appendChild(row);
			}
			AJS.tabs.setup();
			let numberOfNonDiscardedRecommendations = conDecRecommendation.getNumberOfNonDiscardedRecommendations(relatedElements);
			conDecNudgingAPI.decideAmbientFeedbackForTab(numberOfNonDiscardedRecommendations, "menu-item-link-recommendation");
			document.getElementById("discard-all-link-recommendations").addEventListener("click", function(event) {
				for (i in relatedElements) {
					if (!(relatedElements[i].isDiscarded)) {
						conDecLinkRecommendation.discardRecommendation(i);
					}
				}
			});
		}
	};

	let generateTableRow = function(linkRecommendation, index) {
		let row = document.createElement("tr");
		row.appendChild(generateTableCell(`<a href="${linkRecommendation.target.url}">${linkRecommendation.target.type}</a>`, "th-key"));
		row.appendChild(generateTableCell(linkRecommendation.target.summary, "th-name", {}));
		let scoreCell = (generateTableCell(conDecRecommendation.buildScore(linkRecommendation.score, "link_score_" + index), "th-score", ""));
		row.appendChild(scoreCell);

		if (linkRecommendation.isDiscarded) {
			row.classList.add("discarded");
			row.appendChild(generateTableCell(generateUndoDiscardButton(index), "th-options"));
		} else {
			row.appendChild(generateTableCell(generateOptionButtons(index), "th-options"));
		}

		return row;
	};

	let generateTableCell = function(content, headersId, attributes) {
		let tableCell = document.createElement("td");
		tableCell.headers = headersId;
		tableCell.innerHTML = content;
		for (let key in attributes) {
			//console.log(attributes[key]);
			tableCell[key] = JSON.stringify(attributes[key]);
		}
		return tableCell
	};

	let generateOptionButtons = function(suggestionIndex) {
		return `<button class='aui-button aui-button-primary' onclick="conDecLinkRecommendation.showDialog(${suggestionIndex})"> <span class='aui-icon aui-icon-small aui-iconfont-link'></span> Link </button>` +
			`<button class='aui-button' onclick="conDecLinkRecommendation.discardRecommendation(${suggestionIndex})"> <span class="aui-icon aui-icon-small aui-iconfont-trash"></span> Discard</button>`;
	};

	let generateUndoDiscardButton = function(suggestionIndex) {
		return `<button class='aui-button' onclick="conDecLinkRecommendation.undoDiscardRecommendation(${suggestionIndex})"> <span class="aui-icon aui-icon-small aui-iconfont-undo"></span> Undo Discard</button>`;
	};

	ConDecLinkRecommendation.prototype.showDialog = function(index) {
		let target = conDecLinkRecommendationAPI.currentLinkRecommendations[index].target;
		let self = this;
		conDecDialog.showLinkDialog(this.issueId, "i", target.id, target.documentationLocation, () => self.loadData());
	};

	ConDecLinkRecommendation.prototype.processRelatedIssuesResponse = function(relatedIssues) {
		return relatedIssues.map(suggestion => {
			return suggestion;
		}).sort((a, b) => b.score.value - a.score.value);
	};

	//-----------------------------------------
	//            Generate table (Duplicates)
	//-----------------------------------------
	ConDecLinkRecommendation.prototype.displayDuplicateIssues = function(duplicates) {
		if (duplicates.length === 0) {
			//reset table content to empty
			this.duplicateResultsTableContentElement.innerHTML = "<i>No duplicates found!</i>";
		} else {
			//reset table content to empty
			this.duplicateResultsTableContentElement.innerHTML = "";
			//append table rows with duplicates
			for (let index in duplicates) {
				let row = generateDuplicateTableRow(duplicates[index], index);
				this.duplicateResultsTableContentElement.appendChild(row);
			}
			AJS.tabs.setup();
			document.getElementById("discard-all-duplicate-recommendations").addEventListener("click", function(event) {
				for (i in duplicates) {
					if (!(duplicates[i].isDiscarded)) {
						conDecLinkRecommendation.discardDuplicateRecommendation(i);
					}
				}
			});
		}
	};

	let generateDuplicateTableRow = function(duplicateRecommendation, index) {
		let row = document.createElement("tr");
		row.appendChild(generateDuplicateTableCell(`<a href="${duplicateRecommendation.target.url}">${duplicateRecommendation.target.type}</a>`, "th-key-duplicate", {}));

		//TODO: visualize the duplicate fragment
		let scoreCell = generateDuplicateTableCell(duplicateRecommendation.preprocessedSummary.slice(duplicateRecommendation.startDuplicate, duplicateRecommendation.startDuplicate + duplicateRecommendation.length), "th-text-fragment-duplicate", { title: "Length:" + duplicateRecommendation.length });
		AJS.$(scoreCell).tooltip();
		row.appendChild(scoreCell);

		if (duplicateRecommendation.isDiscarded) {
			row.classList.add("discarded");
			row.appendChild(generateDuplicateTableCell(generateUndoDiscardDuplicateButton(index), "th-options-duplicate", {}));
		} else {
			row.appendChild(generateDuplicateTableCell(generateDuplicateOptionButtons(index), "th-options-duplicate", {}));
		}

		return row;
	};

	let generateDuplicateTableCell = function(content, headersId, attributes) {
		let tableCell = document.createElement("td");
		tableCell.headers = headersId;
		tableCell.innerHTML = content;
		for (let key in attributes) {
			tableCell[key] = attributes[key];
		}
		return tableCell
	};

	let generateDuplicateOptionButtons = function(index) {
		return `<button class='aui-button aui-button-primary' onclick="conDecLinkRecommendation.markAsDuplicate(${index})"> <span class='aui-icon aui-icon-small aui-iconfont-link'></span> Link as duplicate </button>` +
			`<button class='aui-button aui-button-removed' onclick="conDecLinkRecommendation.discardDuplicateRecommendation(${index})"> <span class="aui-icon aui-icon-small aui-iconfont-trash"></span> Discard</button>`;
	};

	let generateUndoDiscardDuplicateButton = function(suggestionIndex) {
		return `<button class='aui-button' onclick="conDecLinkRecommendation.undoDiscardDuplicateRecommendation(${suggestionIndex})"> <span class="aui-icon aui-icon-small aui-iconfont-undo"></span> Undo Discard</button>`;
	};

	let processDuplicateIssuesResponse = function(duplicates) {
		return duplicates.sort((a, b) => b.length - a.length);
	};

	//-----------------------------------------
	// Load data and call display logic.
	//-----------------------------------------
	ConDecLinkRecommendation.prototype.loadDuplicateData = function() {
		startLoadingVisualization(this.duplicateResultsTableElement, this.loadingSpinnerElement);

		Promise.resolve(conDecLinkRecommendationAPI.getDuplicateKnowledgeElement(this.projectKey, this.issueId, "i"))
			.then((duplicates) => this.displayDuplicateIssues(processDuplicateIssuesResponse(duplicates)))
			.catch((error) => displayErrorMessage(error))
			.finally(() => stopLoadingVisualization(this.duplicateResultsTableElement, this.loadingSpinnerElement));
	}

	ConDecLinkRecommendation.prototype.loadData = function() {
		startLoadingVisualization(this.resultsTableElement, this.loadingSpinnerElement);

		Promise.resolve(conDecLinkRecommendationAPI.getRelatedKnowledgeElements(this.projectKey, this.issueId, 'i'))
			.then((relatedIssues) => this.displayRelatedElements(this.processRelatedIssuesResponse(relatedIssues)))
			.catch((error) => displayErrorMessage(error))
			.finally(() => stopLoadingVisualization(this.resultsTableElement, this.loadingSpinnerElement));
	}

	//-----------------------------------------
	//		General purpose functions
	//-----------------------------------------

	function displayErrorMessage(error) {
		conDecAPI.showFlag("error", "Could not load knowledge element! </br>" + error)
	}

	function startLoadingVisualization(table, spinner) {
		//console.log(table);
		table.style.visibility = "hidden";
		spinner.style.display = "flex";
	}

	function stopLoadingVisualization(table, spinner) {
		spinner.style.display = "none";
		table.style.visibility = "visible";
	}

	global.conDecLinkRecommendation = new ConDecLinkRecommendation();
})(window);
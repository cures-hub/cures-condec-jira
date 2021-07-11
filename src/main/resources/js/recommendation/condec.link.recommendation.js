/**
 * This module implements the link suggestion and duplicate detection.
 */
(function(global) {

	let ConDecLinkRecommendation = function() {
		this.projectKey = conDecAPI.getProjectKey();
		this.currentLinkRecommendations = [];
		this.currentLinkDuplicates = [];
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

	ConDecLinkRecommendation.prototype.discardDuplicate = function(index) {
		conDecLinkRecommendationAPI.discardRecommendation(this.projectKey, this.currentDuplicates[index])
			.then((data) => {
				conDecAPI.showFlag("success", "Discarded duplicate recommendation sucessfully!");
				this.loadDuplicateData();
			})
			.catch((error) => displayErrorMessage(error));
	}

	ConDecLinkRecommendation.prototype.discardSuggestion = function(index) {
		conDecLinkRecommendationAPI.discardRecommendation(this.projectKey, this.currentLinkRecommendations[index])
			.then((data) => {
				conDecAPI.showFlag("success", "Discarded link recommendation sucessfully!");
				this.loadData();
			})
			.catch((error) => displayErrorMessage(error));
	}

	ConDecLinkRecommendation.prototype.markAsDuplicate = function(index) {
		let duplicateElement = this.currentDuplicates[index].target;

		let self = this;
		conDecAPI.createLink(this.issueId, duplicateElement.id, "i", duplicateElement.documentationLocation, "duplicate", () => self.loadDuplicateData());
	}

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
			this.currentLinkRecommendations = relatedElements;
			//append table rows with possibly related issues
			for (let index in relatedElements) {
				let row = generateTableRow(relatedElements[index], index);
				this.resultsTableContentElement.appendChild(row);
			}
			AJS.tabs.setup();
			conDecNudgingAPI.decideAmbientFeedbackForTab(relatedElements.length, "menu-item-link-recommendation");
		}
	};

	let generateTableRow = function(suggestion, index) {
		let row = document.createElement("tr");
		row.appendChild(generateTableCell(`<a href="${suggestion.target.url}">${suggestion.target.type}</a>`, "th-key"));
		row.appendChild(generateTableCell(suggestion.target.summary, "th-name", {}));
		let scoreCell = (generateTableCell(conDecRecommendation.buildScore(suggestion.score, "link_score_" + index), "th-score", ""));
		row.appendChild(scoreCell);

		row.appendChild(generateTableCell(generateOptionButtons(index), "th-options"));
		
		if (suggestion.isDiscarded) {
			row.style.background = "#e8e8e8";
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
			`<button class='aui-button aui-button-removed' onclick="conDecLinkRecommendation.discardSuggestion(${suggestionIndex})"> <span class="aui-icon aui-icon-small aui-iconfont-trash"></span> Discard</button>`;
	};

	ConDecLinkRecommendation.prototype.showDialog = function(index) {
		let target = this.currentLinkRecommendations[index].target;
		let self = this;
		conDecDialog.showLinkDialog(this.issueId, "i", target.id, target.documentationLocation, () => self.loadData());
	}

	ConDecLinkRecommendation.prototype.processRelatedIssuesResponse = function(relatedIssues) {
		return relatedIssues.map(suggestion => {
			return suggestion;
		}).sort((a, b) => b.score.value - a.score.value);
	}

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
			this.currentDuplicates = duplicates;
			//append table rows with duplicates
			for (let index in duplicates) {
				let row = generateDuplicateTableRow(duplicates[index], index);
				this.duplicateResultsTableContentElement.appendChild(row);
			}
			AJS.tabs.setup();
		}
	};

	let generateDuplicateTableRow = function(duplicate, index) {
		let row = document.createElement("tr");
		row.appendChild(generateDuplicateTableCell(`<a href="${duplicate.target.url}">${duplicate.target.type}</a>`, "th-key-duplicate", {}));

		//TODO: visualize the duplicate fragment
		let scoreCell = generateDuplicateTableCell(duplicate.preprocessedSummary.slice(duplicate.startDuplicate, duplicate.startDuplicate + duplicate.length), "th-text-fragment-duplicate", { title: "Length:" + duplicate.length });
		AJS.$(scoreCell).tooltip();
		row.appendChild(scoreCell);

		row.appendChild(generateDuplicateTableCell(generateDuplicateOptionButtons(index), "th-options-duplicate", {}));
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
			`<button class='aui-button aui-button-removed' onclick="conDecLinkRecommendation.discardDuplicate(${index})"> <span class="aui-icon aui-icon-small aui-iconfont-trash"></span> Discard</button>`;
	};

	let processDuplicateIssuesResponse = function(duplicates) {
		return duplicates.sort((a, b) => b.length - a.length);
	}

	//-----------------------------------------
	// Load data and call display logic.
	//-----------------------------------------
	ConDecLinkRecommendation.prototype.loadDuplicateData = function() {
		startLoadingVisualization(this.duplicateResultsTableElement, this.loadingSpinnerElement);

		conDecLinkRecommendationAPI.getDuplicateKnowledgeElement(this.projectKey, this.issueId, "i")
			.then((duplicates) => this.displayDuplicateIssues(processDuplicateIssuesResponse(duplicates)))
			.catch((error) => displayErrorMessage(error))
			.finally(() => stopLoadingVisualization(this.duplicateResultsTableElement, this.loadingSpinnerElement));
	}

	ConDecLinkRecommendation.prototype.loadData = function() {
		startLoadingVisualization(this.resultsTableElement, this.loadingSpinnerElement);
		conDecLinkRecommendationAPI.getRelatedKnowledgeElements(this.projectKey, this.issueId, 'i')
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
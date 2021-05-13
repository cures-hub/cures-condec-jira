/**
 * This module implements the link suggestion and duplicate detection.
 */
(function(global) {

	let ConDecLinkSuggestion = function() {
		this.projectKey = conDecAPI.getProjectKey();
		this.currentSuggestions = [];
	};

	ConDecLinkSuggestion.prototype.init = function() {
		this.issueId = JIRA.Issue.getIssueId();

		// Duplicates
		this.duplicateResultsTableElement = document.getElementById("duplicate-results-table");
		this.duplicateResultsTableContentElement = document.getElementById("table-content-duplicate");

		// Related
		this.loadingSpinnerElement = document.getElementById("loading-spinner");
		this.resultsTableElement = document.getElementById("results-table");
		this.resultsTableContentElement = document.getElementById("table-content");
	}

	ConDecLinkSuggestion.prototype.discardDuplicate = function(index) {
		let suggestionElement = this.currentSuggestions[index].target;
		conDecLinkSuggestionAPI.discardDuplicateSuggestion(this.projectKey, this.issueId, 'i', suggestionElement.id, suggestionElement.documentationLocation)
			.then((data) => {
				displaySuccessMessage("Discarded suggestion sucessfully!");
				this.loadDuplicateData();
			})
			.catch((error) => displayErrorMessage(error));
	}

	ConDecLinkSuggestion.prototype.discardSuggestion = function(index) {
		let suggestionElement = this.currentSuggestions[index].target;

		conDecLinkSuggestionAPI.discardLinkSuggestion(this.projectKey, this.issueId, 'i', suggestionElement.id, suggestionElement.documentationLocation)
			.then((data) => {
				displaySuccessMessage("Discarded suggestion sucessfully!");
				this.loadData();
			})
			.catch((error) => displayErrorMessage(error));
	}

	ConDecLinkSuggestion.prototype.markAsDuplicate = function(index) {
		let duplicateElement = this.currentSuggestions[index].target;

		let self = this;
		conDecAPI.createLink(this.issueId, duplicateElement.id, "i", duplicateElement.documentationLocation, "duplicates", () => self.loadDuplicateData());
	}

	//-----------------------------------------
	//			Generate table (Related)
	//-----------------------------------------
	ConDecLinkSuggestion.prototype.displayRelatedElements = function(relatedElements) {
		if (relatedElements.length === 0) {
			//reset table content to empty
			this.resultsTableContentElement.innerHTML = "<i>No related issues found!</i>";
		} else {
			//reset table content to empty
			this.resultsTableContentElement.innerHTML = "";
			this.currentSuggestions = relatedElements;
			//append table rows with possibly related issues
			for (let index in relatedElements) {
				let row = generateTableRow(relatedElements[index], index);
				this.resultsTableContentElement.appendChild(row);
			}
			AJS.tabs.setup();
		}
	};

	let generateTableRow = function(suggestion, index) {
		let row = document.createElement("tr");
		row.appendChild(generateTableCell(`<a href="${suggestion.target.url}">${suggestion.target.key}</a>`, "th-key"));
		row.appendChild(generateTableCell(suggestion.target.summary, "th-name", {}));
		let scoreCell = (generateTableCell(suggestion.totalScore, "th-score", { "title": suggestion.score }));
		AJS.$(scoreCell).tooltip();
		row.appendChild(scoreCell);

		row.appendChild(generateTableCell(generateOptionButtons(index), "th-options"));
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
		return `<button class='aui-button aui-button-primary' onclick="conDecLinkSuggestion.showDialog(${suggestionIndex})"> <span class='aui-icon aui-icon-small aui-iconfont-link'></span> Link </button>` +
			`<button class='aui-button aui-button-removed' onclick="conDecLinkSuggestion.discardSuggestion(${suggestionIndex})"> <span class="aui-icon aui-icon-small aui-iconfont-trash"></span> Discard suggestion </button>`;
	};

	ConDecLinkSuggestion.prototype.showDialog = function(index) {
		let target = this.currentSuggestions[index].target;
		console.dir(target);
		let self = this;
		conDecDialog.showLinkDialog(this.issueId, "i", target.id, target.documentationLocation, () => self.loadData());
	}

	ConDecLinkSuggestion.prototype.processRelatedIssuesResponse = function(relatedIssues) {
		return relatedIssues.map(suggestion => {
			suggestion.totalScore = Math.round(suggestion.totalScore * 1000) / 1000.;
			return suggestion;
		}).sort((a, b) => b.totalScore - a.totalScore);
	}

	//-----------------------------------------
	//            Generate table (Duplicates)
	//-----------------------------------------
	ConDecLinkSuggestion.prototype.displayDuplicateIssues = function(duplicates) {
		if (duplicates.length === 0) {
			//reset table content to empty
			this.duplicateResultsTableContentElement.innerHTML = "<i>No duplicates found!</i>";
		} else {
			//reset table content to empty
			this.duplicateResultsTableContentElement.innerHTML = "";
			this.currentSuggestions = duplicates;
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
		row.appendChild(generateDuplicateTableCell(`<a href="${duplicate.target.url}">${duplicate.target.key}</a>`, "th-key-duplicate", {}));

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
		return `<button class='aui-button aui-button-primary' onclick="conDecLinkSuggestion.markAsDuplicate(${index})"> <span class='aui-icon aui-icon-small aui-iconfont-link'></span> Link as duplicate </button>` +
			`<button class='aui-button aui-button-removed' onclick="conDecLinkSuggestion.discardDuplicate(${index})"> <span class="aui-icon aui-icon-small aui-iconfont-trash"></span> Discard suggestion </button>`;
	};

	let processDuplicateIssuesResponse = function(duplicates) {
		return duplicates.sort((a, b) => b.length - a.length);
	}

	//-----------------------------------------
	// Load data and call display logic.
	//-----------------------------------------
	ConDecLinkSuggestion.prototype.loadDuplicateData = function() {
		startLoadingVisualization(this.duplicateResultsTableElement, this.loadingSpinnerElement);

		conDecLinkSuggestionAPI.getDuplicateKnowledgeElement(this.projectKey, this.issueId, "i")
			.then((duplicates) => this.displayDuplicateIssues(processDuplicateIssuesResponse(duplicates)))
			.catch((error) => displayErrorMessage(error))
			.finally(() => stopLoadingVisualization(this.duplicateResultsTableElement, this.loadingSpinnerElement));
	}

	ConDecLinkSuggestion.prototype.loadData = function() {
		startLoadingVisualization(this.resultsTableElement, this.loadingSpinnerElement);
		conDecLinkSuggestionAPI.getRelatedKnowledgeElements(this.projectKey, this.issueId, 'i')
			.then((relatedIssues) => this.displayRelatedElements(this.processRelatedIssuesResponse(relatedIssues)))
			.catch((error) => displayErrorMessage(error))
			.finally(() => stopLoadingVisualization(this.resultsTableElement, this.loadingSpinnerElement));
	}

	//-----------------------------------------
	//		General purpose functions
	//-----------------------------------------

	function displayErrorMessage(error) {
		conDecAPI.showFlag("error", "Could not load Knowledge-Element! </br>" + error)
	}

	function displaySuccessMessage(message) {
		conDecAPI.showFlag("success", message)
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

	global.conDecLinkSuggestion = new ConDecLinkSuggestion();
})(window);
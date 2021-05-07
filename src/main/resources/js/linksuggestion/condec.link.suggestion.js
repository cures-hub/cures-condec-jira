/**
 * This object provides methods for the consistency tabs of the jira issue module.
 * This object is used in the following files:
 * duplicatesTab.vm
 * relatedIssuesTab.vm
 */
(function(global) {

	let ConsistencyTabsModule = function() {
		this.projectKey = conDecAPI.getProjectKey();
		this.currentSuggestions = [];
	};

	ConsistencyTabsModule.prototype.init = function() {
		this.issueId = JIRA.Issue.getIssueId();

		// Duplicates
		this.duplicateResultsTableElement = document.getElementById("duplicate-results-table");
		this.duplicateResultsTableContentElement = document.getElementById("table-content-duplicate");

		// Related
		this.loadingSpinnerElement = document.getElementById("loading-spinner");
		this.resultsTableElement = document.getElementById("results-table");
		this.resultsTableContentElement = document.getElementById("table-content");
	}

	ConsistencyTabsModule.prototype.discardDuplicate = function(index) {
		let suggestionElement = this.currentSuggestions[index].targetElement;
		consistencyAPI.discardDuplicateSuggestion(this.projectKey, this.issueId, 'i', suggestionElement.id, suggestionElement.documentationLocation)
			.then((data) => {
				displaySuccessMessage("Discarded suggestion sucessfully!");
				this.loadDuplicateData();
			})
			.catch((error) => displayErrorMessage(error));
	}

	ConsistencyTabsModule.prototype.discardSuggestion = function(index) {
		let suggestionElement = this.currentSuggestions[index].targetElement;

		consistencyAPI.discardLinkSuggestion(this.projectKey, this.issueId, 'i', suggestionElement.id, suggestionElement.documentationLocation)
			.then((data) => {
				displaySuccessMessage("Discarded suggestion sucessfully!");
				this.loadData();
			})
			.catch((error) => displayErrorMessage(error));
	}

	ConsistencyTabsModule.prototype.markAsDuplicate = function(index) {
		let duplicateElement = this.currentSuggestions[index].targetElement;

		let self = this;
		conDecAPI.createLink(duplicateElement.knowledgeType, this.issueId, duplicateElement.id, "i", duplicateElement.documentationLocation, "duplicates", () => self.loadDuplicateData());
	}

	//-----------------------------------------
	//			Generate table (Related)
	//-----------------------------------------
	ConsistencyTabsModule.prototype.displayRelatedElements = function(relatedElements) {
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
		row.appendChild(generateTableCell(`<a href="${suggestion.targetElement.url}">${suggestion.targetElement.key}</a>`, "th-key"));
		row.appendChild(generateTableCell(suggestion.targetElement.summary, "th-name", {}));
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
		return `<button class='aui-button aui-button-primary' onclick="consistencyTabsModule.showDialog(${suggestionIndex})"> <span class='aui-icon aui-icon-small aui-iconfont-link'></span> Link </button>` +
			`<button class='aui-button aui-button-removed' onclick="consistencyTabsModule.discardSuggestion(${suggestionIndex})"> <span class="aui-icon aui-icon-small aui-iconfont-trash"></span> Discard suggestion </button>`;
	};

	ConsistencyTabsModule.prototype.showDialog = function(index) {
		let targetElement = this.currentSuggestions[index].targetElement;
		console.dir(targetElement);
		let self = this;
		conDecDialog.showDecisionLinkDialog(this.issueId, targetElement.id, "i", targetElement.documentationLocation, () => self.loadData());
	}

	ConsistencyTabsModule.prototype.processRelatedIssuesResponse = function(response) {
		return response.relatedIssues.map(suggestion => {
			suggestion.totalScore = Math.round(suggestion.totalScore * 1000) / 1000.;
			return suggestion;
		}).sort((a, b) => b.totalScore - a.totalScore);
	}

	//-----------------------------------------
	//            Generate table (Duplicates)
	//-----------------------------------------
	ConsistencyTabsModule.prototype.displayDuplicateIssues = function(duplicates) {
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
		row.appendChild(generateDuplicateTableCell(`<a href="${duplicate.targetElement.url}">${duplicate.targetElement.key}</a>`, "th-key-duplicate", {}));

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
		return `<button class='aui-button aui-button-primary' onclick="consistencyTabsModule.markAsDuplicate(${index})"> <span class='aui-icon aui-icon-small aui-iconfont-link'></span> Link as duplicate </button>` +
			`<button class='aui-button aui-button-removed' onclick="consistencyTabsModule.discardDuplicate(${index})"> <span class="aui-icon aui-icon-small aui-iconfont-trash"></span> Discard suggestion </button>`;
	};

	let processDuplicateIssuesResponse = function(response) {
		return response.duplicates.sort((a, b) => b.length - a.length);
	}

	//-----------------------------------------
	// Load data and call display logic.
	//-----------------------------------------
	ConsistencyTabsModule.prototype.loadDuplicateData = function() {
		startLoadingVisualization(this.duplicateResultsTableElement, this.loadingSpinnerElement);

		consistencyAPI.getDuplicateKnowledgeElement(this.projectKey, this.issueId, "i")
			.then((data) => this.displayDuplicateIssues(processDuplicateIssuesResponse(data)))
			.catch((error) => displayErrorMessage(error))
			.finally(() => stopLoadingVisualization(this.duplicateResultsTableElement, this.loadingSpinnerElement));
	}

	ConsistencyTabsModule.prototype.loadData = function() {
		startLoadingVisualization(this.resultsTableElement, this.loadingSpinnerElement);
		consistencyAPI.getRelatedKnowledgeElements(this.projectKey, this.issueId, 'i')
			.then((data) => this.displayRelatedElements(this.processRelatedIssuesResponse(data)))
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

	global.consistencyTabsModule = new ConsistencyTabsModule();
})(window);
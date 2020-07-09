/**
 * This object provides methods for the consistency tabs of the jira issue module.
 * This object is used in the following files:
 * duplicatesTab.vm
 * relatedIssuesTab.vm
 */
(function (global) {

	/**
	 *
	 * @constructor
	 */
	let ConsistencyTabsModule = function ConsistencyTabsModule() {
		this.isInitialized = false;




		this.projectKey = conDecAPI.getProjectKey();
	};

	ConsistencyTabsModule.prototype.init = function () {
		let that = this;
		this.issueKey = conDecAPI.getIssueKey();
		this.issueId = JIRA.Issue.getIssueId();


		// Duplicates
		this.loadingDuplicateSpinnerElement = document.getElementById("loading-spinner-duplicate");
		this.duplicateResultsTableElement = document.getElementById("duplicate-results-table");
		this.duplicateResultsTableContentElement = document.getElementById("table-content-duplicate");

		// Related
		this.loadingSpinnerElement = document.getElementById("loading-spinner");
		this.resultsTableElement = document.getElementById("results-table");
		this.resultsTableContentElement = document.getElementById("table-content");

		$(document).ajaxComplete(function(event, request, settings){
			if(settings.url.includes("WorkflowUIDispatcher.jspa")){
				console.log("WorkflowUIDispatcher");
				consistencyAPI.displayConsistencyCheck()
			}
		});
	}


	ConsistencyTabsModule.prototype.discardDuplicate = function (otherIssueKey) {
		consistencyAPI.discardDuplicateSuggestion(this.issueKey, otherIssueKey, this.projectKey)
			.then((data) => {
				displaySuccessMessage("Discarded suggestion sucessfully!");
				this.loadDuplicateData();
			})
			.catch((error) => displayErrorMessage(error));
	}

	ConsistencyTabsModule.prototype.discardSuggestion = function (otherIssueKey) {
		consistencyAPI.discardLinkSuggestion(this.issueKey, otherIssueKey, this.projectKey)
			.then((data) => {
				displaySuccessMessage("Discarded suggestion sucessfully!");
				this.loadData();
			})
			.catch((error) => displayErrorMessage(error));
	}

	ConsistencyTabsModule.prototype.markAsDuplicate = function (otherIssueId) {
		let self = this;
		conDecAPI.createLink(null, this.issueId, otherIssueId, "i", "i", "duplicates", () => self.loadDuplicateData());
	}

	//-----------------------------------------
	//			Generate table (Related)
	//-----------------------------------------
	ConsistencyTabsModule.prototype.displayRelatedIssues = function (relatedIssues) {
		if (relatedIssues.length === 0) {
			//reset table content to empty
			this.resultsTableContentElement.innerHTML = "<i>No related issues found!</i>";
		} else {
			//reset table content to empty
			this.resultsTableContentElement.innerHTML = "";
			//append table rows with possibly related issues
			for (let relatedIssue of relatedIssues) {
				let row = generateTableRow(relatedIssue);
				this.resultsTableContentElement.appendChild(row);
			}
			AJS.tabs.setup();
		}

	};

	let generateTableRow = function (relatedIssue) {
		let row = document.createElement("tr");
		row.appendChild(generateTableCell(`<a href="${relatedIssue.key}">${relatedIssue.key}</a>`, "th-key"));
		row.appendChild(generateTableCell(relatedIssue.summary, "th-name", {}));
		let scoreCell = (generateTableCell(relatedIssue.score, "th-score", {"title": relatedIssue.results.scores}));
		AJS.$(scoreCell).tooltip();
		row.appendChild(scoreCell);

		row.appendChild(generateTableCell(generateOptionButtons(relatedIssue), "th-options"));
		return row;
	};

	let generateTableCell = function (content, headersId, attributes) {
		let tableCell = document.createElement("td");
		tableCell.headers = headersId;
		tableCell.innerHTML = content;
		for (let key in attributes) {
			//console.log(attributes[key]);
			tableCell[key] = JSON.stringify(attributes[key]);
		}
		return tableCell
	};

	let generateOptionButtons = function (relatedIssue) {
		return `<button class='aui-button aui-button-primary' onclick="consistencyTabsModule.showDialog('${relatedIssue.id}')"> <span class='aui-icon aui-icon-small aui-iconfont-link'></span> Link </button>` +
			`<button class='aui-button aui-button-removed' onclick="consistencyTabsModule.discardSuggestion('${relatedIssue.key}')"> <span class="aui-icon aui-icon-small aui-iconfont-trash"></span> Discard suggestion </button>`;
	};

	ConsistencyTabsModule.prototype.showDialog = function (targetIssueId) {
		let self = this;
		conDecDialog.showDecisionLinkDialog(this.issueId, targetIssueId, "i", "i", () => self.loadData());
	}

	ConsistencyTabsModule.prototype.processRelatedIssuesResponse = function (response) {
		return response.relatedIssues.map(suggestion => {
			suggestion.score = Math.round(suggestion.score * 1000) / 1000.;
			return suggestion;
		}).sort((a, b) => b.score - a.score);
	}

	//-----------------------------------------
	//            Generate table (Duplicates)
	//-----------------------------------------
	ConsistencyTabsModule.prototype.displayDuplicateIssues = function (duplicates) {
		if (duplicates.length === 0) {
			//reset table content to empty
			this.duplicateResultsTableContentElement.innerHTML = "<i>No duplicates found!</i>";
		} else {
			//reset table content to empty
			this.duplicateResultsTableContentElement.innerHTML = "";
			//append table rows with duplicates
			for (let duplicate of duplicates) {
				let row = generateDuplicateTableRow(duplicate);
				this.duplicateResultsTableContentElement.appendChild(row);
			}
			AJS.tabs.setup();

		}

	};


	let generateDuplicateTableRow = function (duplicate) {
		let row = document.createElement("tr");
		row.appendChild(generateDuplicateTableCell(`<a href="${duplicate.key}">${duplicate.key}</a>`, "th-key-duplicate", {}));

		//TODO: visualize the duplicate fragment
		let scoreCell = generateDuplicateTableCell(duplicate.preprocessedSummary.slice(duplicate.startDuplicate, duplicate.startDuplicate + duplicate.length), "th-text-fragment-duplicate", {title: "Length:" + duplicate.length});
		AJS.$(scoreCell).tooltip();
		row.appendChild(scoreCell);

		row.appendChild(generateDuplicateTableCell(generateDuplicateOptionButtons(duplicate), "th-options-duplicate", {}));
		return row;
	};

	let generateDuplicateTableCell = function (content, headersId, attributes) {
		let tableCell = document.createElement("td");
		tableCell.headers = headersId;
		tableCell.innerHTML = content;
		for (let key in attributes) {
			tableCell[key] = attributes[key];
		}
		return tableCell
	};

	let generateDuplicateOptionButtons = function (duplicate) {
		return `<button class='aui-button aui-button-primary' onclick="consistencyTabsModule.markAsDuplicate('${duplicate.id}')"> <span class='aui-icon aui-icon-small aui-iconfont-link'></span> Link as duplicate </button>` +
			`<button class='aui-button aui-button-removed' onclick="consistencyTabsModule.discardDuplicate('${duplicate.key}')"> <span class="aui-icon aui-icon-small aui-iconfont-trash"></span> Discard suggestion </button>`;
	};

	let processDuplicateIssuesResponse = function (response) {
		return response.duplicates.sort((a, b) => b.length - a.length);
	}

	//-----------------------------------------
	// Load data and call display logic.
	//-----------------------------------------
	ConsistencyTabsModule.prototype.loadDuplicateData = function () {
		startLoadingVisualization(this.duplicateResultsTableElement, this.loadingDuplicateSpinnerElement);

		consistencyAPI.getDuplicatesForIssue(this.issueKey)
			.then((data) => this.displayDuplicateIssues(processDuplicateIssuesResponse(data)))
			.catch((error) => displayErrorMessage(error))
			.finally(() => stopLoadingVisualization(this.duplicateResultsTableElement, this.loadingDuplicateSpinnerElement));
	}

	ConsistencyTabsModule.prototype.loadData = function () {
		startLoadingVisualization(this.resultsTableElement, this.loadingSpinnerElement);

		consistencyAPI.getRelatedIssues(this.issueKey)
			.then((data) => this.displayRelatedIssues(this.processRelatedIssuesResponse(data)))
			.catch((error) => displayErrorMessage(error))
			.finally(() => stopLoadingVisualization(this.resultsTableElement, this.loadingSpinnerElement));
	}

	//-----------------------------------------
	//		General purpose functions
	//-----------------------------------------

	function displayErrorMessage(error) {
		conDecAPI.showFlag("error", "Could not load issues! </br>" + error)
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
/**
 * This module implements the link suggestion and duplicate detection.
 */
(function(global) {

	let ConDecLinkRecommendation = function() {
		this.projectKey = conDecAPI.getProjectKey();
	};

	ConDecLinkRecommendation.prototype.init = function() {
		this.issueId = JIRA.Issue.getIssueId();

		this.loadingSpinnerElement = document.getElementById("loading-spinner");
		this.resultsTableElement = document.getElementById("results-table");
		this.resultsTableContentElement = document.getElementById("table-content");

		this.loadData();
	}

	ConDecLinkRecommendation.prototype.discardRecommendation = function(index) {		
		conDecLinkRecommendationAPI.discardRecommendation(this.projectKey, conDecLinkRecommendationAPI.currentLinkRecommendations.get(this.issueId)[index])
			.then((data) => {
				conDecAPI.showFlag("success", "Discarded link recommendation successfully!");
				this.loadData();
			})
			.catch((error) => displayErrorMessage(error));
	};

	ConDecLinkRecommendation.prototype.undoDiscardRecommendation = function(index) {
		conDecLinkRecommendationAPI.undoDiscardRecommendation(this.projectKey, conDecLinkRecommendationAPI.currentLinkRecommendations.get(this.issueId)[index])
			.then((data) => {
				conDecAPI.showFlag("success", "Discarding link recommendation successfully undone!");
				this.loadData();
			})
			.catch((error) => displayErrorMessage(error));
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

	ConDecLinkRecommendation.prototype.loadData = function() {
		startLoadingVisualization(this.resultsTableElement, this.loadingSpinnerElement);

		Promise.resolve(conDecLinkRecommendationAPI.getLinkRecommendations(this.projectKey, this.issueId, 'i'))
			.then((relatedIssues) => this.displayRelatedElements(relatedIssues))
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
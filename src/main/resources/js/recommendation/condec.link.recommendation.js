/**
 * Implements the view for link recommendation and duplicate detection.
 * 
 * Is referenced in HTML by
 * tabs/recommendation/linkRecommendation.vm
 */
(function(global) {

	let ConDecLinkRecommendation = function() {
		this.projectKey = conDecAPI.getProjectKey();
	};

	ConDecLinkRecommendation.prototype.init = function() {
		this.loadingSpinnerElement = document.getElementById("loading-spinner");
		this.resultsTableElement = document.getElementById("results-table");
		this.resultsTableContentElement = document.getElementById("table-content");

		// fill dropdown to select a knowledge element
		var jiraIssueId = JIRA.Issue.getIssueId();
		if (jiraIssueId) {
			conDecAPI.getKnowledgeElement(JIRA.Issue.getIssueId(), 'i',
				conDecLinkRecommendation.initKnowledgeElementDropdown);
		} else {
			conDecLinkRecommendation.initKnowledgeElementDropdown();
		}

		// fill link recommendation parameters from current configuration
		conDecLinkRecommendationAPI.getLinkRecommendationConfig().then(config => {
			console.log(config);
			document.getElementById("threshold-input-link-recommendation").value = config["minProbability"];
			document.getElementById("max-amount-input-link-recommendation").value = config["maxRecommendations"];
			var ruleNames = [];
			var selectedRules = [];
			for (var rule of config["contextInformationProviders"]) {
				var name = rule.description;
				ruleNames.push(name);
				if (rule.isActive) {
					selectedRules.push(name);
				}
			}
			conDecFiltering.initDropdown("rule-dropdown-link-recommendation", ruleNames, selectedRules);
		});

		linkConfigPage();

		// add button listener
		addOnClickListenerOnRecommendationButton();
	};

	ConDecLinkRecommendation.prototype.initKnowledgeElementDropdown = function(selectedElement) {
		filterSettings = {};
		if (selectedElement) {
			filterSettings.selectedElementObject = selectedElement;
		}
		let dropdown = document.getElementById("link-recommendation-dropdown");
		conDecAPI.getKnowledgeElements(filterSettings, (elements) => {
			conDecFiltering.initKnowledgeElementDropdown(dropdown, elements, selectedElement,
				"link-recommendation", (selectedElement) => {
					conDecLinkRecommendation.selectedElement = selectedElement;
				});
		});
	};

	function linkConfigPage() {
		var configLink = document.getElementById("config-link-link-recommendation");
		configLink.href = AJS.contextPath() + "/plugins/servlet/condec/settings?projectKey="
			+ conDecAPI.projectKey + "&category=linkRecommendation";
		AJS.$(configLink).tooltip();
	}

	function addOnClickListenerOnRecommendationButton() {
		$("#link-recommendation-button").click(function(event) {
			event.preventDefault();
			conDecLinkRecommendation.loadData();
		});
	}

	ConDecLinkRecommendation.prototype.discardRecommendation = function(index) {
		conDecLinkRecommendationAPI.discardRecommendation(this.projectKey, getSelectedLinkRecommendation(index))
			.then((data) => {
				conDecAPI.showFlag("success", "Discarded link recommendation successfully!");
				this.loadData();
			})
			.catch((error) => displayErrorMessage(error));
	};

	ConDecLinkRecommendation.prototype.undoDiscardRecommendation = function(index) {
		conDecLinkRecommendationAPI.undoDiscardRecommendation(this.projectKey, getSelectedLinkRecommendation(index))
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
		if (linkRecommendation.recommendationType == "DUPLICATE") {
			scoreCell.classList = "condec-warning";
			var icon = conDecNudgingAPI.createIcon("aui-iconfont-cross-circle");
			icon.title = "This element might be a potential duplicate!";
			scoreCell.appendChild(document.createTextNode(" "));
			scoreCell.appendChild(icon);
		}
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
		let target = getSelectedLinkRecommendation(index).target;
		conDecDialog.showLinkDialog(this.selectedElement.id, this.selectedElement.documentationLocation, target.id, target.documentationLocation);
	};

	function getSelectedLinkRecommendation(index) {
		var idOfSourceElement = conDecLinkRecommendation.selectedElement.id;
		var allRecommendationsForSourceElement = conDecLinkRecommendationAPI.currentLinkRecommendations.get(idOfSourceElement);
		return allRecommendationsForSourceElement[index];
	}

	ConDecLinkRecommendation.prototype.loadData = function() {
		startLoadingVisualization(this.resultsTableElement, this.loadingSpinnerElement);
		this.selectedElement.projectKey = this.projectKey;

		var filterSettings = {
			"selectedElementObject": this.selectedElement,
			"projectKey": this.projectKey,
			"linkRecommendationConfig": getLinkRecommendationConfig(),
			"isCacheCleared": document.getElementById("clear-link-recommendation-cache-input").checked
		}

		Promise.resolve(conDecLinkRecommendationAPI.getLinkRecommendations(filterSettings))
			.then((relatedIssues) => this.displayRelatedElements(relatedIssues))
			.catch((error) => displayErrorMessage(error))
			.finally(() => stopLoadingVisualization(this.resultsTableElement, this.loadingSpinnerElement));
	}

	function getLinkRecommendationConfig() {
		var selectedRuleNames = conDecFiltering.getSelectedItems("rule-dropdown-link-recommendation");
		var selectedRules = [];
		for (ruleName of selectedRuleNames) {
			selectedRules.push({
				"@type": ruleName
			});
		}
		return {
			"minProbability": document.getElementById("threshold-input-link-recommendation").value,
			"maxRecommendations": document.getElementById("max-amount-input-link-recommendation").value,
			"contextInformationProviders": selectedRules
		};
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
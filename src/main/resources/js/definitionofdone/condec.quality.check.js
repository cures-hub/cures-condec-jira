/**
 * This view provides a check of the quality of a knowledge element (e.g., requirement, 
 * code file, decision knowledge element, or work item).
 * 
 * Requires: condec.api.js, condec.quality.check.api.js
 * 
 * Is required by: condec.knowledge.page.js, condec.rationale.backlog.js, 
 * templates/tabs/qualityCheck.vm
 */
(function(global) {

	var ConDecQualityCheck = function ConDecQualityCheck() {
	};

	/**
	 * Initializes the view.
	 *
	 * external references: condec.knowledge.page.js
	 * condec.rationale.backlog.js,
	 *
	 * @param viewIdentifier identifies the html elements of the view
	 * @param node the selected node, used to extract the Jira issue key
	 *             used in the rationale backlog and overview
	 *             can be ignored otherwise
	 */
	ConDecQualityCheck.prototype.initView = function(viewIdentifier, node) {
		console.log("ConDecQualityCheck initView");

		var projectKey = conDecAPI.getProjectKey();
		var issueKey;
		if (node) {
			issueKey = node.key;
		} else {
			issueKey = conDecAPI.getIssueKey();
		}

		var filterSettings = {
			"projectKey": projectKey,
			"selectedElement": issueKey,
		};

		fillQualityCheckTab(filterSettings, viewIdentifier);
	};

	/**
	 * Fills the quality check tab with information by making REST-calls.
	 *
	 * @param filterSettings containing the projectKey and the selected element
	 * @param viewIdentifier identifies the html elements of the view
	 */
	function fillQualityCheckTab(filterSettings, viewIdentifier) {
		conDecDoDCheckingAPI.getQualityCheckResults(filterSettings, (qualityCheckResults) => {
			fillQualityProblems(qualityCheckResults, viewIdentifier);
			var problems = qualityCheckResults.filter(checkResult => checkResult.criterionViolated);
			updateTabStatus(problems, viewIdentifier);
		});
	}

	/**
	 * Fills the table with information about the quality check for a selected knowledge element.
	 *
	 * @param checkResults a list of the quality criterion check results for a selected knowledge element
	 * @param viewIdentifier identifies the html elements of the view
	 */
	function fillQualityProblems(checkResults, viewIdentifier) {
		var qualityCheckTableBody = document.getElementById("quality-check-table-body-" + viewIdentifier);
		qualityCheckTableBody.innerHTML = "";
		checkResults.forEach(function(checkResult) {
			var tableRow = document.createElement("tr");
			var criterionNameCell = document.createElement("td");
			criterionNameCell.innerText = checkResult.name;
			tableRow.appendChild(criterionNameCell);
			var statusCell = document.createElement("td");		
			var icon;
			if (checkResult.criterionViolated) {
				statusCell.classList = "condec-error";
				icon = createIcon("aui-iconfont-cross-circle");
			} else {
				statusCell.classList = "condec-fine";
				icon = createIcon("aui-iconfont-check-circle");
			}			
			statusCell.appendChild(icon);		
			statusCell.insertAdjacentText('beforeend', " " + checkResult.explanation);
			tableRow.appendChild(statusCell);

			qualityCheckTableBody.appendChild(tableRow);
		});
	}
	
	function createIcon(iconType) {
		var icon = document.createElement("span");
		icon.classList = "aui-icon aui-icon-small " + iconType;
		return icon;
	}

	/**
	 * Sets the colour of the tab according to the quality problems.
	 *
	 * @param qualityProblems a list of the quality problems of the element
	 * @param viewIdentifier identifies the html elements of the view
	 */
	function updateTabStatus(qualityProblems, viewIdentifier) {
		var qualityCheckTab = document.getElementById("menu-item-quality-check-" + viewIdentifier);

		if (!qualityProblems || !qualityProblems.length) {
			conDecNudgingAPI.setAmbientFeedback(qualityCheckTab, "condec-fine");
		} else if (qualityProblems.length <= 2) {
			conDecNudgingAPI.setAmbientFeedback(qualityCheckTab, "condec-warning");
		} else {
			conDecNudgingAPI.setAmbientFeedback(qualityCheckTab, "condec-error");
		}
	}

	global.conDecQualityCheck = new ConDecQualityCheck();
})(window);
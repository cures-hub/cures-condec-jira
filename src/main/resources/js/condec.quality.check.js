/*
 *	This view provides a check  of the quality of the decision knowledge of the jira issue.
 */

(function(global) {
	const pluralize = (count, noun, suffix = 's') =>
		`${count} ${noun}${count !== 1 ? suffix : ''}`;

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
	 * @param filterSettings containing the projectKey and the issueKey
	 * @param viewIdentifier identifies the html elements of the view
	 */
	function fillQualityCheckTab(filterSettings, viewIdentifier) {
		fillIssueKey(filterSettings.selectedElement, viewIdentifier);

		conDecDoDCheckingAPI.getDefinitionOfDone(filterSettings.projectKey, (definitionOfDone) => {
			fillCoverageRequired(definitionOfDone, viewIdentifier);
		});

		conDecDoDCheckingAPI.getCoverageOfJiraIssue(filterSettings, (coverage) => {
			fillCoverageReached(coverage, viewIdentifier);
		});

		conDecDoDCheckingAPI.getQualityProblems(filterSettings, (qualityProblems) => {
			fillQualityProblems(qualityProblems, viewIdentifier);
			updateTabStatus(qualityProblems, viewIdentifier);
		});
	}

	/**
	 * Fills the the tab with the key of the currently selected issue.
	 *
	 * @param issueKey the key of the selected issue
	 * @param viewIdentifier identifies the html elements of the view
	 */
	function fillIssueKey(issueKey, viewIdentifier) {
		var knowledgeTypeLabel = document.getElementById("quality-check-issue-key-" + viewIdentifier)

		if (issueKey === null || issueKey === undefined) {
			knowledgeTypeLabel.innerText = "This element";
			return;
		}

		knowledgeTypeLabel.innerText = issueKey;
	}

	/**
	 * Fills the tab with information about the requirements of the DoD-check.
	 *
	 * @param definitionOfDone containing the required coverage and the link distance
	 * @param viewIdentifier identifies the html elements of the view
	 */
	function fillCoverageRequired(definitionOfDone, viewIdentifier) {
		var minimumCoverageLabel = document.getElementById("quality-check-minimum-coverage-" + viewIdentifier);
		var linkDistanceLabel = document.getElementById("quality-check-link-distance-" + viewIdentifier);

		minimumCoverageLabel.innerText = pluralize(definitionOfDone.minimumDecisionsWithinLinkDistance, "decision");
		linkDistanceLabel.innerText = definitionOfDone.maximumLinkDistanceToDecisions;
	}

	/**
	 * Fills the tab with the coverage the element reached in the DoD-check.
	 *
	 * @param coverage the coverage reached
	 * @param viewIdentifier identifies the html elements of the view
	 */
	function fillCoverageReached(coverage, viewIdentifier) {
		var coverageLabel = document.getElementById("quality-check-coverage-" + viewIdentifier);

		coverageLabel.innerText = pluralize(coverage, "decision");
	}

	/**
	 * Fills the tab with information about the quality problems of the element.
	 *
	 * @param qualityProblems a list of the quality problems of the element
	 * @param viewIdentifier identifies the html elements of the view
	 */
	function fillQualityProblems(qualityProblems, viewIdentifier) {
		var qualityProblemsTextField = document.getElementById("quality-check-problems-text-" + viewIdentifier);

		var text = "";

		qualityProblems.forEach(function(problem) {
			text += problem.explanation;
			if (problem.name === "NO_DECISION_COVERAGE" || problem.name === "DECISION_COVERAGE_TOO_LOW" ||
				problem.name === "INCOMPLETE_KNOWLEDGE_LINKED") {				
				text += "\n\n";
			} else {
				text += "\n";
			}
		})

		qualityProblemsTextField.innerText = text;
		conDecNudgingAPI.setAmbientFeedback(qualityProblemsTextField, "condec-error");
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
		} else if (qualityProblems.some(problem => problem.name === "NO_DECISION_COVERAGE")) {
			conDecNudgingAPI.setAmbientFeedback(qualityCheckTab, "condec-error");
		} else {
			conDecNudgingAPI.setAmbientFeedback(qualityCheckTab, "condec-warning");
		}
	}

	global.conDecQualityCheck = new ConDecQualityCheck();
})(window);
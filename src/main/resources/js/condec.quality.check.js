/*
 *	This view provides a check  of the quality of the decision knowledge of the jira issue.
 */

(function(global) {
	const pluralize = (count, noun, suffix = 's') =>
		`${count} ${noun}${count !== 1 ? suffix : ''}`;

	var ConDecQualityCheck = function ConDecQualityCheck() {
	};

	ConDecQualityCheck.prototype.initView = function(viewIdentifier, node) {
		console.log("ConDecQualityCheck buildQualityCheck");

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

		fillQualityCheckTab(filterSettings, viewIdentifier)
	};

	function fillQualityCheckTab(filterSettings, viewIdentifier) {
		conDecDoDCheckingAPI.getDefinitionOfDone(filterSettings.projectKey, (definitionOfDone) => {
			var coverageRequired = definitionOfDone.minimumDecisionsWithinLinkDistance;
			fillCoverageRequired(definitionOfDone, viewIdentifier)

			conDecDoDCheckingAPI.getCoverageOfJiraIssue(filterSettings, (coverage) => {
				var coverageReached = coverage;
				fillCoverageReached(coverage, viewIdentifier)

				conDecDoDCheckingAPI.getQualityProblems(filterSettings, (text) => {
					var qualityProblems = text;
					fillQualityProblems(qualityProblems, viewIdentifier)
					updateTabStatus(coverageRequired, coverageReached, qualityProblems, viewIdentifier)
				});
			});
		});
	}

	function fillCoverageRequired(definitionOfDone, viewIdentifier) {
		var minimumCoverageLabel = document.getElementById("quality-check-minimum-coverage-" + viewIdentifier);
		var linkDistanceLabel = document.getElementById("quality-check-link-distance-" + viewIdentifier);

		minimumCoverageLabel.innerText = pluralize(definitionOfDone.minimumDecisionsWithinLinkDistance, "decision");
		linkDistanceLabel.innerText = definitionOfDone.maximumLinkDistanceToDecisions;
	}

	function fillCoverageReached(coverage, viewIdentifier) {
		var coverageLabel = document.getElementById("quality-check-coverage-" + viewIdentifier);

		coverageLabel.innerText = pluralize(coverage, "decision");
	}

	function fillQualityProblems(qualityProblems, viewIdentifier) {
		var qualityProblemsText = document.getElementById("quality-check-problems-text-" + viewIdentifier);

		qualityProblemsText.innerText = qualityProblems;
		addToken(qualityProblemsText, "condec-error");
	}

	function updateTabStatus(coverageRequired, coverageReached, qualityProblems, viewIdentifier) {
		var qualityCheckTab = document.getElementById("menu-item-quality-check-" + viewIdentifier);

		if (!qualityProblems || !qualityProblems[0].length) {
			addToken(qualityCheckTab, "condec-fine");
		} else if (coverageReached > 0) {
			addToken(qualityCheckTab, "condec-warning");
		} else if (coverageReached <= 0) {
			addToken(qualityCheckTab, "condec-error");
		}  else {
			addToken(qualityCheckTab, "condec-default");
		}
	}

	function addToken(element, tag) {
		if (element === null) {
			return;
		}
		element.classList.remove("condec-default");
		element.classList.remove("condec-error");
		element.classList.remove("condec-warning");
		element.classList.remove("condec-fine");
		element.classList.add(tag);
	}

	global.conDecQualityCheck = new ConDecQualityCheck();
})(window);
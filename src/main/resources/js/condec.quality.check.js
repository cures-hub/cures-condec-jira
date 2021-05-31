/*
 *	This view provides a check  of the quality of the decision knowledge of the jira issue.
 */

(function (global) {
	var qualityCheckTab;
	var minimumCoverageText;
	var linkDistanceText;
	var knowledgeCompleteText;
	var issueText;
	var decisionText;

	const KNOWLEDGE_COMPLETE = "Decision knowledge is complete."
	const KNOWLEDGE_INCOMPLETE = "Decision knowledge is incomplete."

	var ConDecQualityCheck = function ConDecQualityCheck() {
	};

	ConDecQualityCheck.prototype.initView = function (viewIdentifier) {
		console.log("ConDecQualityCheck initView");

		var projectKey = conDecAPI.getProjectKey();
		var issueKey = conDecAPI.getIssueKey();

		if (projectKey && issueKey) {
			updateView(projectKey, issueKey, viewIdentifier);
		}
	};

	ConDecQualityCheck.prototype.build = function (node, viewIdentifier) {
		console.log("ConDecQualityCheck build");

		var projectKey = conDecAPI.getProjectKey();
		var issueKey = node.key;

		if (projectKey && issueKey) {
			updateView(projectKey, issueKey, viewIdentifier);
		}
	};

	function updateView(projectKey, issueKey, viewIdentifier) {
		conDecAPI.getFilterSettings(projectKey, "", function(filterSettings) {
			var minimumCoverage = filterSettings.minimumDecisionCoverage;
			var linkDistance = filterSettings.linkDistance;

			conDecDoDCheckingAPI.getCoverageOfJiraIssue(projectKey, issueKey, function(result) {
				var numberOfIssues = result.Issue;
				var numberOfDecisions = result.Decision;

				var newFilterSettings = {
					"projectKey": projectKey,
					"selectedElement": issueKey,
				};

				conDecDoDCheckingAPI.getFailedDefinitionOfDoneCriteria(newFilterSettings, function(result) {
					var hasIncompleteKnowledgeLinked = false;
					var doesNotHaveMinimumCoverage = false
					if (result.includes("hasIncompleteKnowledgeLinked")) {
						hasIncompleteKnowledgeLinked = true;
					}
					if (result.includes("doesNotHaveMinimumCoverage")) {
						doesNotHaveMinimumCoverage = true;
					}

					getHTMLNodes("menu-item-quality-check-" + viewIdentifier
						, "condec-tab-minimum-coverage-" + viewIdentifier
						, "condec-tab-link-distance-" + viewIdentifier
						, "quality-check-knowledge-complete-text-" + viewIdentifier
						, "quality-check-issue-text-" + viewIdentifier
						, "quality-check-decision-text-" + viewIdentifier);

					updateTab(qualityCheckTab, hasIncompleteKnowledgeLinked, doesNotHaveMinimumCoverage, numberOfIssues, numberOfDecisions, minimumCoverage);
					updateLabel(minimumCoverageText, minimumCoverage);
					updateLabel(linkDistanceText, linkDistance);
					updateText(issueText, "issues", numberOfIssues, minimumCoverage);
					updateText(decisionText, "decisions", numberOfDecisions, minimumCoverage);
					updateIsKnowledgeComplete(hasIncompleteKnowledgeLinked);
				});
			});
		});
	}

	function updateLabel(label, text) {
		label.innerText = text;
	}

	function updateTab(tab, hasIncompleteKnowledgeLinked, doesNotHaveMinimumCoverage, coverageOfIssues, coverageOfDecisions) {
		if (!hasIncompleteKnowledgeLinked && !doesNotHaveMinimumCoverage) {
			addToken(tab, "condec-fine");
		} else if ((coverageOfIssues > 0) || (coverageOfDecisions > 0)) {
			addToken(tab, "condec-warning");
		} else if ((coverageOfIssues === 0) && (coverageOfDecisions === 0)) {
			addToken(tab, "condec-empty");
		} else {
			addToken(tab, "condec-default");
		}
	}

	function updateText(textField, type, coverage, minimum) {
		textField.textContent = "# " + type + ": " + coverage;
		if (coverage >= minimum) {
			addToken(textField, "condec-fine");
		} else if ((coverage < minimum) && (coverage > 0)) {
			addToken(textField, "condec-warning");
		} else if (coverage === 0) {
			addToken(textField, "condec-empty");
		} else {
			textField.textContent = "";
			addToken(textField, "condec-default");
		}
	}

	function updateIsKnowledgeComplete(hasIncompleteKnowledgeLinked) {
		if (hasIncompleteKnowledgeLinked) {
			knowledgeCompleteText.textContent = KNOWLEDGE_INCOMPLETE;
			addToken(knowledgeCompleteText, "condec-empty");
		} else {
			knowledgeCompleteText.textContent = KNOWLEDGE_COMPLETE;
			addToken(knowledgeCompleteText, "condec-fine");
		}
	}

	function getHTMLNodes(tabName, minimumCoverageName, linkDistanceName, knowledgeCompleteName,
						  issueName, decisionName) {
		qualityCheckTab = document.getElementById(tabName);
		minimumCoverageText = document.getElementById(minimumCoverageName);
		linkDistanceText = document.getElementById(linkDistanceName);
		knowledgeCompleteText = document.getElementById(knowledgeCompleteName);
		issueText = document.getElementById(issueName);
		decisionText = document.getElementById(decisionName);
	}

	function addToken(element, tag) {
		element.classList.remove("condec-default");
		element.classList.remove("condec-empty");
		element.classList.remove("condec-warning");
		element.classList.remove("condec-fine");
		element.classList.add(tag);
	}

	global.conDecQualityCheck = new ConDecQualityCheck();
})(window);
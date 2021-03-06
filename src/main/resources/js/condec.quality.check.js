/*
 *	This view provides a check  of the quality of the decision knowledge of the jira issue.
 */

(function(global) {
	var qualityCheckTab;
	var minimumCoverageText;
	var linkDistanceText;
	var knowledgeCompleteText;
	var knowledgeCompleteCriteriaText;
	var issueText;
	var decisionText;

	const KNOWLEDGE_COMPLETE = "Linked decision knowledge is complete.";
	const KNOWLEDGE_INCOMPLETE = "Linked decision knowledge is incomplete.";
	const KNOWLEDGE_CRITERIA = "Failed knowledge completeness criteria:";

	var ConDecQualityCheck = function ConDecQualityCheck() {
	};

	ConDecQualityCheck.prototype.initView = function(viewIdentifier) {
		console.log("ConDecQualityCheck initView");

		var projectKey = conDecAPI.getProjectKey();
		var issueKey = conDecAPI.getIssueKey();

		if (projectKey && issueKey) {
			updateView(projectKey, issueKey, viewIdentifier);
		} else {
			resetView(projectKey, viewIdentifier);
		}
	};

	ConDecQualityCheck.prototype.build = function(node, viewIdentifier) {
		console.log("ConDecQualityCheck build");

		var projectKey = conDecAPI.getProjectKey();
		var issueKey = node.key;

		if (projectKey && issueKey) {
			updateView(projectKey, issueKey, viewIdentifier);
		} else {
			resetView(projectKey, viewIdentifier);
		}
	};

	function resetView(projectKey, viewIdentifier) {
		conDecDoDCheckingAPI.getDefinitionOfDone(projectKey, (definitionOfDone) => {
			var minimumCoverage = definitionOfDone.minimumDecisionsWithinLinkDistance;
			var linkDistance = definitionOfDone.maximumLinkDistanceToDecisions;

			getHTMLNodes("menu-item-quality-check-" + viewIdentifier
				, "condec-tab-minimum-coverage-" + viewIdentifier
				, "condec-tab-link-distance-" + viewIdentifier
				, "quality-check-knowledge-complete-text-" + viewIdentifier
				, "quality-check-knowledge-complete-criteria-text-" + viewIdentifier
				, "quality-check-issue-text-" + viewIdentifier
				, "quality-check-decision-text-" + viewIdentifier);

			updateTab(qualityCheckTab, true, true, null, null);
			updateLabel(minimumCoverageText, minimumCoverage);
			updateLabel(linkDistanceText, linkDistance);
			updateText(issueText, "issues", null, minimumCoverage);
			updateText(decisionText, "decisions", null, minimumCoverage);
		});
	}

	function updateView(projectKey, issueKey, viewIdentifier) {
		conDecDoDCheckingAPI.getDefinitionOfDone(projectKey, (definitionOfDone) => {
			var minimumCoverage = definitionOfDone.minimumDecisionsWithinLinkDistance;
			var linkDistance = definitionOfDone.maximumLinkDistanceToDecisions;

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

					conDecDoDCheckingAPI.getFailedCompletenessCheckCriteria(newFilterSettings, function(result) {
						var failedCompletenessCheckCriteria = result;

						getHTMLNodes("menu-item-quality-check-" + viewIdentifier
							, "condec-tab-minimum-coverage-" + viewIdentifier
							, "condec-tab-link-distance-" + viewIdentifier
							, "quality-check-knowledge-complete-text-" + viewIdentifier
							, "quality-check-knowledge-complete-criteria-text-" + viewIdentifier
							, "quality-check-issue-text-" + viewIdentifier
							, "quality-check-decision-text-" + viewIdentifier);

						updateTab(qualityCheckTab, hasIncompleteKnowledgeLinked, doesNotHaveMinimumCoverage, numberOfIssues, numberOfDecisions);
						updateLabel(minimumCoverageText, minimumCoverage);
						updateLabel(linkDistanceText, linkDistance);
						updateText(issueText, "issues", numberOfIssues, minimumCoverage);
						updateText(decisionText, "decisions", numberOfDecisions, minimumCoverage);
						updateIsKnowledgeComplete(hasIncompleteKnowledgeLinked, failedCompletenessCheckCriteria);
					});
				});
			});
		});
	}

	function updateTab(tab, hasIncompleteKnowledgeLinked, doesNotHaveMinimumCoverage, coverageOfIssues, coverageOfDecisions) {
		if (!hasIncompleteKnowledgeLinked && !doesNotHaveMinimumCoverage) {
			addToken(tab, "condec-fine");
		} else if ((coverageOfIssues > 0) || (coverageOfDecisions > 0)) {
			addToken(tab, "condec-warning");
		} else if ((coverageOfIssues === 0) && (coverageOfDecisions === 0)) {
			addToken(tab, "condec-error");
		} else {
			addToken(tab, "condec-default");
		}
	}

	function updateLabel(label, text) {
		if (label !== null) {
			label.innerText = text;
		}
	}

	function updateText(textField, type, coverage, minimum) {
		if (textField === null) {
			return;
		}
		textField.textContent = "# " + type + ": " + coverage;
		if (coverage >= minimum) {
			addToken(textField, "condec-fine");
		} else if ((coverage < minimum) && (coverage > 0)) {
			addToken(textField, "condec-warning");
		} else if (coverage === 0) {
			addToken(textField, "condec-error");
		} else {
			textField.textContent = "";
			addToken(textField, "condec-default");
		}
	}

	function updateIsKnowledgeComplete(hasIncompleteKnowledgeLinked, failedCompletenessCheckCriteria) {
		if (hasIncompleteKnowledgeLinked === true) {
			knowledgeCompleteText.textContent = KNOWLEDGE_INCOMPLETE;
			addToken(knowledgeCompleteText, "condec-error");
		} else if (hasIncompleteKnowledgeLinked === false) {
			knowledgeCompleteText.textContent = KNOWLEDGE_COMPLETE;
			addToken(knowledgeCompleteText, "condec-fine");
		} else {
			knowledgeCompleteText.textContent = "";
			addToken(knowledgeCompleteText, "condec-default");
		}

		if (failedCompletenessCheckCriteria && failedCompletenessCheckCriteria.length) {
			addToken(knowledgeCompleteCriteriaText, "condec-error");
			knowledgeCompleteCriteriaText.innerHTML = KNOWLEDGE_CRITERIA + "<br>" +
				failedCompletenessCheckCriteria.join("<br>");
		}
	}

	function getHTMLNodes(tabName, minimumCoverageName, linkDistanceName, knowledgeCompleteName,
		knowledgeCompleteCriteriaName, issueName, decisionName) {
		qualityCheckTab = document.getElementById(tabName);
		minimumCoverageText = document.getElementById(minimumCoverageName);
		linkDistanceText = document.getElementById(linkDistanceName);
		knowledgeCompleteText = document.getElementById(knowledgeCompleteName);
		knowledgeCompleteCriteriaText = document.getElementById(knowledgeCompleteCriteriaName);
		issueText = document.getElementById(issueName);
		decisionText = document.getElementById(decisionName);
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
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

		var qualityCheckTab = document.getElementById("menu-item-quality-check-" + viewIdentifier);

		fillCoverageRequired(projectKey, viewIdentifier);

		if (issueKey) {
			fillCoverageReached(filterSettings, viewIdentifier);
			fillQualityProblems(filterSettings, viewIdentifier);
		}
	};

	function fillCoverageRequired(projectKey, viewIdentifier) {
		conDecDoDCheckingAPI.getDefinitionOfDone(projectKey, (definitionOfDone) => {
			var minimumCoverageLabel = document.getElementById("quality-check-minimum-coverage-" + viewIdentifier);
			var linkDistanceLabel = document.getElementById("quality-check-link-distance-" + viewIdentifier);

			minimumCoverageLabel.innerText = pluralize(definitionOfDone.minimumDecisionsWithinLinkDistance, "decision");
			linkDistanceLabel.innerText = definitionOfDone.maximumLinkDistanceToDecisions;
		});
	}

	function fillCoverageReached(filterSettings, viewIdentifier) {
		conDecDoDCheckingAPI.getCoverageOfJiraIssue(filterSettings, (coverage) => {
			var coverageLabel = document.getElementById("quality-check-coverage-" + viewIdentifier);

			coverageLabel.innerText = pluralize(coverage, "decision");
		});
	}

	function fillQualityProblems(filterSettings, viewIdentifier) {
		conDecDoDCheckingAPI.getQualityProblems(filterSettings, (text) => {
			var qualityProblemsText = document.getElementById("quality-check-problems-text-" + viewIdentifier);

			qualityProblemsText.innerText = text;
		});
	}

	function updateTab(tab, hasIncompleteKnowledgeLinked, doesNotHaveMinimumCoverage, coverage) {
		if (!hasIncompleteKnowledgeLinked && !doesNotHaveMinimumCoverage) {
			addToken(tab, "condec-fine");
		} else if (coverage > 0) {
			addToken(tab, "condec-warning");
		} else if (coverage === 0) {
			addToken(tab, "condec-error");
		} else {
			addToken(tab, "condec-default");
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
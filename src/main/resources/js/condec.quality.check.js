/*
 *	This view provides a check  of the quality of the decision knowledge of the jira issue.
 */

(function (global) {
	var qualityCheckTab;
	var issueText;
	var decisionText;

	var ConDecQualityCheck = function ConDecQualityCheck() {
	};

	ConDecQualityCheck.prototype.initView = function () {
		console.log("ConDecQualityChecking initView");

		var projectKey = conDecAPI.getProjectKey();
		var issueKey = conDecAPI.getIssueKey();

		updateView(projectKey, issueKey);
	};

	function updateView(projectKey, issueKey) {
		conDecAPI.getFilterSettings(projectKey, "", function(filterSettings) {
			var minimumCoverage = filterSettings.minimumDecisionCoverage;

			conDecDoDCheckingAPI.getCoverageOfJiraIssue(projectKey, issueKey, function(result) {
				var numberOfIssues = result.Issue;
				var numberOfDecisions = result.Decision;

				qualityCheckTab = document.getElementById("menu-item-quality-check");
				issueText = document.getElementById("quality-check-issue-text");
				decisionText = document.getElementById("quality-check-decision-text");

				updateTab(qualityCheckTab, numberOfIssues, numberOfDecisions, minimumCoverage);
				updateText(issueText, "issues", numberOfIssues, minimumCoverage);
				updateText(decisionText, "decisions", numberOfDecisions, minimumCoverage);
			});
		});
	}

	function updateTab(tab, coverageOfIssues, coverageOfDecisions, minimum) {
		if ((coverageOfIssues >= minimum) && (coverageOfDecisions >= minimum)) {
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
		if (coverage >= minimum) {
			textField.textContent = "# " + type + ": " + coverage;
			addToken(textField, "condec-fine");
		} else if ((coverage < minimum) && (coverage > 0)) {
			textField.textContent = "# " + type + ": " + coverage + " (at least " + minimum + " " + type + " required!)";
			addToken(textField, "condec-warning");
		} else if (coverage === 0) {
			textField.textContent = "# " + type + ": " + coverage + " (at least " + minimum + " " + type + " required!)";
			addToken(textField, "condec-empty");
		} else {
			textField.textContent = "";
			addToken(textField, "condec-default");
		}
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
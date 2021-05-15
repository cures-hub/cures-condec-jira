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
		conDecAPI.getFilterSettings(issueKey, "", function(filterSettings) {
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
			tab.style.background = "green";
		} else if ((coverageOfIssues > 0) || (coverageOfDecisions > 0)) {
			tab.style.background = "orange";
		} else if ((coverageOfIssues === 0) && (coverageOfDecisions === 0)) {
			tab.style.background = "red";
		} else {
			tab.style.background = "white";
		}
	}

	function updateText(textField, type, coverage, minimum) {
		if (coverage >= minimum) {
			textField.textContent = "# " + type + ": " + coverage;
			textField.style.color = "green";
		} else if ((coverage < minimum) && (coverage > 0)) {
			textField.textContent = "# " + type + ": " + coverage + " (at least " + minimum + " " + type + " required!)";
			textField.style.color = "orange";
		} else if (coverage === 0) {
			textField.textContent = "# " + type + ": " + coverage + " (at least " + minimum + " " + type + " required!)";
			textField.style.color = "red";
		} else {
			textField.textContent = "";
			textField.style.color = "black";
		}
	}

	global.conDecQualityCheck = new ConDecQualityCheck();
})(window);
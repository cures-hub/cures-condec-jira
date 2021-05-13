/*
 *	This view provides a check  of the quality of the decision knowledge of the jira issue.
 */

(function (global) {
	var issueLabel;
	var decisionLabel;

	var projectKey;
	var issueKey;

	var ConDecQualityCheck = function ConDecQualityCheck() {
		projectKey = conDecAPI.getProjectKey();
		issueKey = conDecAPI.getIssueKey();
	};

	ConDecQualityCheck.prototype.initView = function () {
		console.log("ConDecQualityChecking initView");

		conDecAPI.getFilterSettings(issueKey, "", function(filterSettings) {
			var minimumCoverage = filterSettings.minimumDecisionCoverage;

			conDecDoDCheckingAPI.getCoverageOfJiraIssue(projectKey, issueKey, function(result) {
				var numberOfIssues = result.Issue;
				var numberOfDecisions = result.Decision;

				updateView(numberOfIssues, numberOfDecisions, minimumCoverage);
			});
		});
	};

	function updateView(numberOfIssues, numberOfDecisions, minimumCoverage) {
		issueLabel = document.getElementById("quality-check-issue-label");
		decisionLabel = document.getElementById("quality-check-decision-label");

		issueLabel.innerHTML = fillInLabel("issues", numberOfIssues, minimumCoverage);
		decisionLabel.innerHTML = fillInLabel("decisions", numberOfDecisions, minimumCoverage);
	}

	function fillInLabel(type, coverage, minimum) {
		if (coverage >= minimum) {
			return "# " + type + ": " + coverage;
		} else if ((coverage < minimum) && (coverage > 0)) {
			return "# " + type + ": " + coverage + " (at least " + minimum + " " + type + " required!)";
		} else if (coverage === 0) {
			return "# " + type + ": " + coverage + " (at least " + minimum + " " + type + " required!)";
		}

		return "";
	}

	global.conDecQualityCheck = new ConDecQualityCheck();
})(window);
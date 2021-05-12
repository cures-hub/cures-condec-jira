/*
 *	This view provides a check  of the quality of the decision knowledge of the jira issue.
 */

(function (global) {
	var issueLabel;
	var decisionLabel;

	var projectKey;
	var issueKey;

	var numberOfIssues;
	var numberOfDecisions;
	var minimumCoverage;

	var ConDecQualityCheck = function ConDecQualityCheck() {
		projectKey = conDecAPI.getProjectKey();
		issueKey = conDecAPI.getIssueKey();

		numberOfIssues = 0;
		numberOfDecisions = 0;
		minimumCoverage = 1;
	};

	ConDecQualityCheck.prototype.initView = function () {
		console.log("ConDecQualityChecking initView");

		conDecDoDCheckingAPI.getCoverageOfJiraIssue(projectKey, issueKey, function(result) {
			numberOfIssues = result.Issue;
			numberOfDecisions = result.Decision;
		});

		conDecAPI.getFilterSettings(issueKey, "", function(filterSettings) {
			minimumCoverage = filterSettings.minimumDecisionCoverage;
		});

		updateView();
	};

	function updateView() {
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
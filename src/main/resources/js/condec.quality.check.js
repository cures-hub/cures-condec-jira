/*
 *	This view provides a check  of the quality of the decision knowledge of the jira issue.
 */
(function (global) {

	var projectKey;
	var issueKey;

	var numberOfIssues;
	var numberOfDecisions;
	var minimumNumberOfDecisions;

	var ConDecQualityChecking = function ConDecQualityChecking() {
		projectKey = conDecAPI.getProjectKey();
		issueKey = conDecAPI.getIssueKey();

		numberOfIssues = 0;
		numberOfDecisions = 0;
		minimumNumberOfDecisions = 0;
	};

	ConDecQualityChecking.prototype.initView = function () {
		console.log("ConDecQualityChecking initView");

		conDecAPI.getCoverageOfJiraIssue(projectKey, issueKey, function(result) {
			numberOfIssues = result.Issue;
			numberOfDecisions = result.Decision;
		});

		conDecAPI.getDefinitionOfDone(projectKey, function(definitionOfDone) {
			minimumNumberOfDecisions = definitionOfDone.minimumDecisionsWithinLinkDistance;
		});

		updateView();
	};

	function updateView() {
		var issueLabel = document.getElementById("quality-check-issue-label");
		var decisionLabel = document.getElementById("quality-check-decision-label");

		issueLabel.innerHTML = fillInLabel("issues", numberOfIssues, minimumNumberOfDecisions);
		decisionLabel.innerHTML = fillInLabel("decisions", numberOfDecisions, minimumNumberOfDecisions);
	}

	function fillInLabel(type, number, minimumNumber) {
		if (number >= minimumNumber) {
			return "# " + type + ": " + number;
		} else if ((number < minimumNumber) && (number > 0)) {
			return "# " + type + ": " + number + " (at least " + minimumNumber + " " + type + " required!";
		} else if (number === 0) {
			return "# " + type + ": " + number + " (at least " + minimumNumber + " " + type + " required!";
		}

		return "";
	}

	global.conDecQualityChecking = new ConDecQualityChecking();
})(window);
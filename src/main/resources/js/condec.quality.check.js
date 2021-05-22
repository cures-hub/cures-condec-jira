/*
 *	This view provides a check  of the quality of the decision knowledge of the jira issue.
 */

(function (global) {
	var qualityCheckTab;
	var minimumCoverageText;
	var linkDistanceText;
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
			var linkDistance = filterSettings.linkDistance;

			conDecDoDCheckingAPI.getCoverageOfJiraIssue(projectKey, issueKey, function(result) {
				var numberOfIssues = result.Issue;
				var numberOfDecisions = result.Decision;

				getHTMLNodes("menu-item-quality-check"
					, "condec-tab-minimum-coverage"
					, "condec-tab-link-distance"
					, "quality-check-issue-text"
					, "quality-check-decision-text");

				updateTab(qualityCheckTab, numberOfIssues, numberOfDecisions, minimumCoverage);
				updateLabel(minimumCoverageText, minimumCoverage);
				updateLabel(linkDistanceText, linkDistance);
				updateText(issueText, "issues", numberOfIssues, minimumCoverage);
				updateText(decisionText, "decisions", numberOfDecisions, minimumCoverage);
			});
		});
	}

	function updateLabel(label, text) {
		label.innerText = text;
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

	function getHTMLNodes(tabName, minimumCoverageName, linkDistanceName, issueName, decisionName) {
		qualityCheckTab = document.getElementById(tabName);
		minimumCoverageText = document.getElementById(minimumCoverageName);
		linkDistanceText = document.getElementById(linkDistanceName);
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
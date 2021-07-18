/*
 This module fills the box plots and pie charts used in the rationale coverage dashboard item.

 Requires
 * condec.requirements.dashboard.js

 Is referenced in HTML by
 * dashboard/rationaleCoverage.vm
 */

(function (global) {
	var ConDecRationaleCoverageDashboard = function() {
		console.log("ConDecRationaleCoverageDashboard constructor");
	};

	ConDecRationaleCoverageDashboard.prototype.init = function (filterSettings, issueType) {
		getMetrics(filterSettings, issueType);
	};

	function getMetrics(filterSettings, sourceKnowledgeTypes) {
		if (!JSON.parse(filterSettings).projectKey || !JSON.parse(filterSettings).projectKey.length) {
			return;
		}

		conDecDashboard.showDashboardSection("condec-dashboard-processing-", "rationale-coverage");
		document.getElementById("condec-dashboard-selected-project-rationale-coverage").innerText = JSON.parse(filterSettings).projectKey;

		url = conDecAPI.restPrefix + "/dashboard/rationaleCoverage.json?sourceKnowledgeTypes=" + sourceKnowledgeTypes;

		AJS.$.ajax({
			url: url,
			headers: { "Content-Type": "application/json; charset=utf-8", "Accept": "application/json"},
			type: "post",
			dataType: "json",
			data: filterSettings,
			async: true,
			success: conDecRationaleCoverageDashboard.processData,
			error: conDecRationaleCoverageDashboard.processDataBad
		});
	}

	ConDecRationaleCoverageDashboard.prototype.processDataBad = function (data) {
		conDecDashboard.showDashboardSection("condec-dashboard-contents-data-error-", "rationale-coverage");
	};

	ConDecRationaleCoverageDashboard.prototype.processData = function (data) {
		conDecDashboard.showDashboardSection("condec-dashboard-contents-container-", "rationale-coverage");
		renderData(data);
	};

	function renderData(calculator) {
		/*  init data for charts */
		var issuesPerSelectedJiraIssue = new Map();
		var decisionsPerSelectedJiraIssue = new Map();
		var issueDocumentedForSelectedJiraIssue = new Map();
		var decisionDocumentedForSelectedJiraIssue = new Map();

		/* set something for box plots in case no data will be added to them */
		issuesPerSelectedJiraIssue.set("none", 0);
		decisionsPerSelectedJiraIssue.set("none", 0);

		issueDocumentedForSelectedJiraIssue.set("no rationale elements", "");
		decisionDocumentedForSelectedJiraIssue.set("no code classes", "");

		/* form data for charts */
		issuesPerSelectedJiraIssue = calculator.issuesPerSelectedJiraIssue;
		decisionsPerSelectedJiraIssue = calculator.decisionsPerSelectedJiraIssue;
		issueDocumentedForSelectedJiraIssue = calculator.issueDocumentedForSelectedJiraIssue;
		decisionDocumentedForSelectedJiraIssue = calculator.decisionDocumentedForSelectedJiraIssue;

		/* define color palette */
		var colorPalette = ['#EE6666', '#FAC858', '#91CC75'];

		/* render box-plots */
		ConDecReqDash.initializeChartWithColorPalette("boxplot-IssuesPerJiraIssue",
			"", "# Issues per element", issuesPerSelectedJiraIssue,
			colorPalette);
		ConDecReqDash.initializeChartWithColorPalette("boxplot-DecisionsPerJiraIssue",
			"", "# Decisions per element", decisionsPerSelectedJiraIssue,
			colorPalette);
		/* render pie-charts */
		ConDecReqDash.initializeChartWithColorPalette("piechartRich-IssueDocumentedForSelectedJiraIssue",
			"", "For how many elements is an issue documented?", issueDocumentedForSelectedJiraIssue,
			colorPalette);
		ConDecReqDash.initializeChartWithColorPalette("piechartRich-DecisionDocumentedForSelectedJiraIssue",
			"", "For how many elements is a decision documented?", decisionDocumentedForSelectedJiraIssue,
			colorPalette);
	}

	global.conDecRationaleCoverageDashboard = new ConDecRationaleCoverageDashboard();
})(window);
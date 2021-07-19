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

	ConDecRationaleCoverageDashboard.prototype.getData = function (dashboardAPI, filterSettings, sourceKnowledgeTypes) {
		conDecDashboardAPI.getRationaleCoverage(filterSettings, sourceKnowledgeTypes, function (error, result) {
			conDecDashboard.processData(error, result, conDecRationaleCoverageDashboard,
				"rationale-coverage", dashboardAPI);
		});
	};

	ConDecRationaleCoverageDashboard.prototype.renderData = function (data) {
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
		issuesPerSelectedJiraIssue = data.issuesPerSelectedJiraIssue;
		decisionsPerSelectedJiraIssue = data.decisionsPerSelectedJiraIssue;
		issueDocumentedForSelectedJiraIssue = data.issueDocumentedForSelectedJiraIssue;
		decisionDocumentedForSelectedJiraIssue = data.decisionDocumentedForSelectedJiraIssue;

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
	};

	global.conDecRationaleCoverageDashboard = new ConDecRationaleCoverageDashboard();
})(window);
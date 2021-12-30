/*
 This module renders the dashboard and its configuration screen used in the rationale coverage dashboard item.

 Requires
 * condec.requirements.dashboard.js
 * condec.rationale.coverage.dashboard.js

 Is referenced in HTML by
 * dashboard/rationaleCoverage.vm
 */
define('dashboard/rationaleCoverage', [], function() {
	var dashboardAPI;

	var ConDecRationaleCoverageDashboardItem = function(API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCoverageDashboardItem.prototype.render = function(context, preferences) {
		conDecDashboard.initRender(this, "rationale-coverage",
			dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCoverageDashboardItem.prototype.renderEdit = function(context, preferences) {
		conDecDashboard.initConfiguration("rationale-coverage", dashboardAPI, preferences);
	};

	/**
	 * Gets the data to fill the dashboard plots by making an API-call.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the filterSettings used for the API-call
	 * @param sourceKnowledgeTypes the source knowledge types used for the API-call
	 */
	ConDecRationaleCoverageDashboardItem.prototype.getData = function(dashboardAPI, filterSettings, sourceKnowledgeTypes) {
		var conDecRationaleCoverageDashboardItem = this;
		conDecDashboardAPI.getRationaleCoverage(filterSettings, sourceKnowledgeTypes, function(error, result) {
			conDecDashboard.processData(error, result, conDecRationaleCoverageDashboardItem, "rationale-coverage", dashboardAPI);
		});
	};

	/**
	 * Render the dashboard plots.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param data the data returned from the API-call
	 */
	ConDecRationaleCoverageDashboardItem.prototype.renderData = function(data) {
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

	return ConDecRationaleCoverageDashboardItem;
});
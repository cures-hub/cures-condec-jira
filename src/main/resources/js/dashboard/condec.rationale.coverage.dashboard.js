/**
 * This module renders the dashboard and its configuration screen used in the rationale coverage dashboard item.
 *
 * Requires
 * condec.dashboard.js
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
		conDecDashboard.initDashboard(this, "rationale-coverage", dashboardAPI, preferences);
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
	 * Gets the metrics to fill the dashboard plots by making an API-call.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the filterSettings used for the API-call
	 * @param sourceKnowledgeTypes the source knowledge types used for the API-call
	 */
	ConDecRationaleCoverageDashboardItem.prototype.getData = function(dashboardAPI, filterSettings) {
		let self = this;
		conDecDashboardAPI.getRationaleCoverage(filterSettings, filterSettings["sourceKnowledgeTypes"], function(error, result) {
			conDecDashboard.processData(error, result, self, "rationale-coverage", dashboardAPI);
		});
	};

	/**
	 * Render the dashboard plots.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param metrics the metrics returned from the API-call
	 */
	ConDecRationaleCoverageDashboardItem.prototype.renderData = function(metrics) {
		/* define color palette */
		var colorPalette = ['#EE6666', '#FAC858', '#91CC75'];

		/* render box-plots */
		conDecDashboard.initializeChartWithColorPalette("boxplot-IssuesPerJiraIssue",
			"", "# Issues per element", metrics.issuesPerSelectedJiraIssue,
			colorPalette);
		conDecDashboard.initializeChartWithColorPalette("boxplot-DecisionsPerJiraIssue",
			"", "# Decisions per element", metrics.decisionsPerSelectedJiraIssue,
			colorPalette);
		/* render pie-charts */
		conDecDashboard.initializeChartWithColorPalette("piechartRich-IssueDocumentedForSelectedJiraIssue",
			"", "For how many elements is an issue documented?", metrics.issueDocumentedForSelectedJiraIssue,
			colorPalette);
		conDecDashboard.initializeChartWithColorPalette("piechartRich-DecisionDocumentedForSelectedJiraIssue",
			"", "For how many elements is a decision documented?", metrics.decisionDocumentedForSelectedJiraIssue,
			colorPalette);
	};

	return ConDecRationaleCoverageDashboardItem;
});
/**
 * This module renders the dashboard and its configuration screen used in the rationale completeness dashboard item.
 *
 * Requires
 * condec.dashboard.js
 */
define('dashboard/rationaleCompleteness', [], function() {
	var dashboardAPI;
	const viewId = "rationale-completeness";

	var ConDecRationaleCompletenessDashboardItem = function(API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.render = function(context, preferences) {
		conDecDashboard.initDashboard(this, viewId, dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.renderEdit = function(context, preferences) {
		conDecDashboard.initConfiguration(viewId, dashboardAPI, preferences);
	};

	/**
	 * Gets the metrics to fill the dashboard plots by making an API-call.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the filterSettings used for the API-call
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.getData = function(dashboardAPI, filterSettings) {
		let self = this;
		conDecDashboardAPI.getRationaleCompleteness(filterSettings, function(error, result) {
			conDecDashboard.processData(error, result, self, viewId, dashboardAPI);
		});
	};

	/**
	 * Render the dashboard plots.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param metrics the metrics returned from the API-call
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.renderData = function(metrics) {
		var colorPalette = ['#91CC75', '#EE6666'];

		conDecDashboard.createPieChartWithListOfElements(metrics.issuesSolvedByDecision, "piechartRich-IssuesSolvedByDecision",
			"How many issues (=decision problems) are solved by a decision?", viewId, colorPalette);
		conDecDashboard.createPieChartWithListOfElements(metrics.decisionsSolvingIssues, "piechartRich-DecisionsSolvingIssues",
			"For how many decisions is the issue (=decision problem) documented?", viewId, colorPalette);
		conDecDashboard.createPieChartWithListOfElements(metrics.proArgumentDocumentedForDecision,
			"piechartRich-ProArgumentDocumentedForDecision",
			"How many decisions have at least one pro argument documented?", viewId, colorPalette);
		conDecDashboard.createPieChartWithListOfElements(metrics.conArgumentDocumentedForDecision,
			"piechartRich-ConArgumentDocumentedForDecision",
			"How many decisions have at least one con argument documented?", viewId, colorPalette);
		conDecDashboard.createPieChartWithListOfElements(metrics.proArgumentDocumentedForAlternative,
			"piechartRich-ProArgumentDocumentedForAlternative",
			"How many alternatives have at least one pro argument documented?", viewId, colorPalette);
		conDecDashboard.createPieChartWithListOfElements(metrics.conArgumentDocumentedForAlternative,
			"piechartRich-ConArgumentDocumentedForAlternative",
			"How many alternatives have at least one con argument documented?", viewId, colorPalette);
	};

	return ConDecRationaleCompletenessDashboardItem;
});
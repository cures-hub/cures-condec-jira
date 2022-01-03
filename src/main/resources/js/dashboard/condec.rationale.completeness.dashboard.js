/**
 * This module renders the dashboard and its configuration screen used in the rationale completeness dashboard item.
 *
 * Requires
 * condec.dashboard.js
 */
define('dashboard/rationaleCompleteness', [], function() {
	var dashboardAPI;

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
		conDecDashboard.initDashboard(this, "rationale-completeness", dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.renderEdit = function(context, preferences) {
		conDecDashboard.initConfiguration("rationale-completeness", dashboardAPI, preferences);
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
			conDecDashboard.processData(error, result, self, "rationale-completeness", dashboardAPI);
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
		createPieChart(metrics.issuesSolvedByDecision,
			"piechartRich-IssuesSolvedByDecision",
			"How many issues (=decision problems) are solved by a decision?");
		createPieChart(metrics.decisionsSolvingIssues,
			"piechartRich-DecisionsSolvingIssues",
			"For how many decisions is the issue (=decision problem) documented?");
		createPieChart(metrics.proArgumentDocumentedForDecision,
			"piechartRich-ProArgumentDocumentedForDecision",
			"How many decisions have at least one pro argument documented?");
		createPieChart(metrics.conArgumentDocumentedForDecision,
			"piechartRich-ConArgumentDocumentedForDecision",
			"How many decisions have at least one con argument documented?");
		createPieChart(metrics.proArgumentDocumentedForAlternative,
			"piechartRich-ProArgumentDocumentedForAlternative",
			"How many alternatives have at least one pro argument documented?");
		createPieChart(metrics.conArgumentDocumentedForAlternative,
			"piechartRich-ConArgumentDocumentedForAlternative",
			"How many alternatives have at least one con argument documented?");
	};

	function createPieChart(metric, divId, title) {
		/* define color palette */
		var colorPalette = ['#EE6666', '#91CC75'];

		var keys = [
			metric.sourceElementType + " has " + metric.targetElementType,
			metric.sourceElementType + " has no " + metric.targetElementType,
		];

		var data = [
			{ "name": keys[0], "value": metric.completeElements.length, "elements": metric.completeElements },
			{ "name": keys[1], "value": metric.incompleteElements.length, "elements": metric.incompleteElements }
		];

		var pieChart = conDecDashboard.createPieChart(divId, title, keys, data, colorPalette);
	}

	return ConDecRationaleCompletenessDashboardItem;
});
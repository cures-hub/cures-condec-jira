/**
 * This module renders the dashboard and its configuration screen used in the general metrics dashboard item.
 *
 * Requires
 * condec.dashboard.js
 */
define('dashboard/generalMetrics', [], function() {
	var dashboardAPI;
	const viewId = "general-metrics";

	var ConDecGeneralMetricsDashboardItem = function(API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecGeneralMetricsDashboardItem.prototype.render = function(context, preferences) {
		conDecDashboard.initDashboard(this, viewId, dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecGeneralMetricsDashboardItem.prototype.renderEdit = function(context, preferences) {
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
	ConDecGeneralMetricsDashboardItem.prototype.getData = function(dashboardAPI, filterSettings) {
		delete filterSettings.definitionOfDone;
		let self = this;
		conDecDashboardAPI.getGeneralMetrics(filterSettings, function(error, result) {
			conDecDashboard.processData(error, result, self, viewId, dashboardAPI);
		});
	};

	/**
	 * Render the dashboard plots.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param generalMetrics the data returned from the API-call
	 */
	ConDecGeneralMetricsDashboardItem.prototype.renderData = function(generalMetrics) {
		var colorPalette = ['#EE6666', '#91CC75'];

		conDecDashboard.createBoxPlotWithListOfElements("boxplot-CommentsPerJiraIssue", "#Comments per Jira Issue",
			generalMetrics.numberOfCommentsMap, viewId);
		conDecDashboard.createBoxPlotWithListOfElements("boxplot-CommitsPerJiraIssue", "#Commits per Jira Issue",
			generalMetrics.numberOfCommitsMap, viewId);
		conDecDashboard.createPieChartWithListOfElements(generalMetrics.requirementsAndCodeFiles,
			"piechartRich-ReqCodeSummary", "#Requirements and Code Files", viewId);
		conDecDashboard.createPieChartWithListOfElements(generalMetrics.elementsFromDifferentOrigins,
			"piechartRich-DecSources", "#Rationale Elements per Origin", viewId);
		conDecDashboard.createSimplePieChart(generalMetrics.numberOfRelevantAndIrrelevantComments,
			"piechartInteger-RelevantSentences", "#Comments in Jira Issues relevant to Decision Knowledge");
		conDecDashboard.createPieChartWithListOfElements(generalMetrics.numberOfDecisionKnowledgeElements,
			"piechartRich-KnowledgeTypeDistribution", "#Decision Knowledge Elements", viewId);
		conDecDashboard.createPieChartWithListOfElements(generalMetrics.definitionOfDoneCheckResults,
			"piechartRich-DoDCheck", "Definition of Done Check", viewId, colorPalette);
	};

	return ConDecGeneralMetricsDashboardItem;
});
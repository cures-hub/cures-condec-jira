/**
 * This module renders the dashboard and its configuration screen used in the general metrics dashboard item.
 *
 * Requires
 * condec.dashboard.js
 */
define('dashboard/generalMetrics', [], function() {
	var dashboardAPI;

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
		conDecDashboard.initDashboard(this, "general-metrics", dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecGeneralMetricsDashboardItem.prototype.renderEdit = function(context, preferences) {
		conDecDashboard.initConfiguration("general-metrics", dashboardAPI, preferences);
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
			conDecDashboard.processData(error, result, self, "general-metrics", dashboardAPI);
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
		/* define color palette */
		var colorPalette = ['#EE6666', '#91CC75'];

		/* render box-plots */
		conDecDashboard.createBoxPlot("boxplot-CommentsPerJiraIssue", "#Comments per Jira Issue",
			generalMetrics.numberOfCommentsMap);
		conDecDashboard.createBoxPlot("boxplot-CommitsPerJiraIssue", "#Commits per Jira Issue",
			generalMetrics.numberOfCommitsMap);
		/* render pie-charts */
		createPieChart(generalMetrics.reqAndClassSummary,
			"piechartRich-ReqCodeSummary",
			"#Requirements and Code Classes");
		createPieChart(generalMetrics.elementsFromDifferentOrigins,
			"piechartRich-DecSources",
			"#Rationale Elements per Origin");
		createSimplePieChart(generalMetrics.numberOfRelevantComments,
			"piechartInteger-RelevantSentences",
			"Comments in Jira Issues relevant to Decision Knowledge");
		createPieChart(generalMetrics.distributionOfKnowledgeTypes,
			"piechartRich-KnowledgeTypeDistribution",
			"Distribution of Knowledge Types");
		createPieChart(generalMetrics.definitionOfDoneCheckResults,
			"piechartRich-DoDCheck",
			"Definition of Done Check", colorPalette);
	};

	function createSimplePieChart(metric, divId, title, colorPalette) {
		var data = [];

		for (const [category, number] of metric.entries()) {
			entry = { "name": category, "value": number }
			data.push(entry);
		}

		var pieChart = conDecDashboard.createPieChart(divId, title, Array.from(metric.keys()), data, colorPalette);
	}

	function createPieChart(metric, divId, title, colorPalette) {
		var data = [];

		for (const [category, elements] of metric.entries()) {
			entry = { "name": category, "value": elements.length, "elements": elements }
			data.push(entry);
		}

		console.log(metric.keys());

		var pieChart = conDecDashboard.createPieChart(divId, title, Array.from(metric.keys()), data, colorPalette);
	}

	return ConDecGeneralMetricsDashboardItem;
});
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
		preferences.definitionOfDone = {};
		conDecDashboard.initDashboard(this, "rationale-coverage", dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param filterSettings The user filterSettings saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCoverageDashboardItem.prototype.renderEdit = function(context, filterSettings) {
		conDecDashboard.initConfiguration("rationale-coverage", dashboardAPI, filterSettings);
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
		//definitionOfDone = filterSettings.definitionOfDone;
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
		console.log(metrics.decisionDocumentedForSelectedJiraIssue);

		/* define color palette */
		var colorPalette = ['#EE6666', '#FAC858', '#91CC75'];


		// render box-plots 
		conDecDashboard.initializeChartWithColorPalette("boxplot-IssuesPerJiraIssue",
			"", "# Issues per element", metrics.issueCoverageMetric.coverageMap,
			colorPalette);
		conDecDashboard.initializeChartWithColorPalette("boxplot-DecisionsPerJiraIssue",
			"", "# Decisions per element", metrics.decisionCoverageMetric.coverageMap,
			colorPalette);

		// render pie-charts
		createPieChart(metrics.issueCoverageMetric,
			"piechartRich-IssueDocumentedForSelectedJiraIssue",
			"For how many elements is an issue documented?");

		createPieChart(metrics.decisionCoverageMetric,
			"piechartRich-DecisionDocumentedForSelectedJiraIssue",
			"For how many elements is a decision documented?");
	};

	function createPieChart(metric, divId, title) {
		/* define color palette */
		var colorPalette = ['#91CC75', '#FAC858', '#EE6666'];



		// necessary because Java map is not recognized as a map in JavaScript
		var coverageMap = new Map(Object.entries(metric.coverageMap));

		var elementsWithNoCoverage = [];
		var elementsWithLowCoverage = [];
		var elementsWithHighCoverage = [];
		for (const [numberOfReachableElements, elements] of coverageMap.entries()) {
			if (numberOfReachableElements == 0) {
				elementsWithNoCoverage = elementsWithNoCoverage.concat(elements);
				console.log(elements);
			} else if (numberOfReachableElements < metric.minimumRequiredCoverage) {
				elementsWithLowCoverage = elementsWithLowCoverage.concat(elements);
			} else {
				elementsWithHighCoverage = elementsWithHighCoverage.concat(elements);
			}
		}

		var keys = [
			"More than or equal to " + metric.minimumRequiredCoverage + " " + metric.targetElementType + "s reachable",
			"Less than " + metric.minimumRequiredCoverage + " " + metric.targetElementType + "s reachable",
			"No " + metric.targetElementType + "s reachable"
		];

		var data = [
			{ "name": keys[0], "value": elementsWithHighCoverage.length, "elements": elementsWithHighCoverage },
			{ "name": keys[1], "value": elementsWithLowCoverage.length, "elements": elementsWithLowCoverage },
			{ "name": keys[2], "value": elementsWithNoCoverage.length, "elements": elementsWithNoCoverage }
		];

		var pieChart = conDecDashboard.createPieChart(divId, title, keys, data, colorPalette);
	}

	return ConDecRationaleCoverageDashboardItem;
});
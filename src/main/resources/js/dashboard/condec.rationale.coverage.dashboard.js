/**
 * This module renders the dashboard and its configuration screen used in the rationale coverage dashboard item.
 *
 * Requires
 * condec.dashboard.js
 */
define('dashboard/rationaleCoverage', [], function() {
	var dashboardAPI;
	const viewId = "rationale-coverage";

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
		conDecDashboard.initDashboard(this, viewId, dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param filterSettings The user filterSettings saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCoverageDashboardItem.prototype.renderEdit = function(context, filterSettings) {
		conDecDashboard.initConfiguration(viewId, dashboardAPI, filterSettings);
	};

	/**
	 * Gets the metrics to fill the dashboard plots by making an API-call.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the filterSettings used for the API-call including the rationaleCoveredKnowledgeTypes
	 */
	ConDecRationaleCoverageDashboardItem.prototype.getData = function(dashboardAPI, filterSettings) {
		let self = this;
		conDecDashboardAPI.getRationaleCoverage(filterSettings, function(error, result) {
			conDecDashboard.processData(error, result, self, viewId, dashboardAPI);
		});
	};

	/**
	 * Renders the dashboard plots.	
	 * external references: condec.dashboard.js
	 * @param metrics the metrics returned from the API-call
	 */
	ConDecRationaleCoverageDashboardItem.prototype.renderData = function(metrics) {
		conDecDashboard.createBoxPlotWithListOfElements("boxplot-IssuesPerJiraIssue",
			"#Issues per element", metrics.issueCoverageMetric, viewId);
		conDecDashboard.createBoxPlotWithListOfElements("boxplot-DecisionsPerJiraIssue",
			"#Decisions per element", metrics.decisionCoverageMetric, viewId);

		createPieChart(metrics.issueCoverageMetric, "piechartRich-IssueDocumentedForSelectedJiraIssue",
			"For how many elements is an issue documented?", "Issue", metrics.minimumRequiredCoverage);
		createPieChart(metrics.decisionCoverageMetric, "piechartRich-DecisionDocumentedForSelectedJiraIssue",
			"For how many elements is a decision documented?", "Decision", metrics.minimumRequiredCoverage);
	};

	function createPieChart(metric, divId, title, targetElementType, minimumRequiredCoverage) {
		var colorPalette = ['#91CC75', '#FAC858', '#EE6666'];

		var elementsWithNoCoverage = [];
		var elementsWithLowCoverage = [];
		var elementsWithHighCoverage = [];
		for (const [coverage, elements] of metric.entries()) {
			if (coverage == 0) {
				elementsWithNoCoverage = elementsWithNoCoverage.concat(elements);
			} else if (coverage < minimumRequiredCoverage) {
				elementsWithLowCoverage = elementsWithLowCoverage.concat(elements);
			} else {
				elementsWithHighCoverage = elementsWithHighCoverage.concat(elements);
			}
		}

		var coverageMap = new Map();
		coverageMap.set("More than or equal to " + minimumRequiredCoverage + " " + targetElementType + "s reachable",
			elementsWithHighCoverage);
		coverageMap.set("Less than " + minimumRequiredCoverage + " " + targetElementType + "s reachable",
			elementsWithLowCoverage);
		coverageMap.set("No " + targetElementType + "s reachable",
			elementsWithNoCoverage);

		var pieChart = conDecDashboard.createPieChartWithListOfElements(coverageMap, divId, title, viewId, colorPalette);
	}

	return ConDecRationaleCoverageDashboardItem;
});
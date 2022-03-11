/**
 * This module renders the dashboard and its configuration screen used in the decision groups dashboard item.
 *
 * Requires
 * condec.dashboard.js
 */
define('dashboard/decisionGroups', [], function() {
	var dashboardAPI;
	const viewId = "decision-groups";

	var ConDecDecisionGroupsDashboardItem = function(API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecDecisionGroupsDashboardItem.prototype.render = function(context, preferences) {
		conDecDashboard.initDashboard(this, viewId, dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param filterSettings The user filterSettings saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecDecisionGroupsDashboardItem.prototype.renderEdit = function(context, filterSettings) {
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
	ConDecDecisionGroupsDashboardItem.prototype.getData = function(dashboardAPI, filterSettings) {
		let self = this;
		conDecDashboardAPI.getDecisionGroupsMetrics(filterSettings, function(error, result) {
			conDecDashboard.processData(error, result, self, viewId, dashboardAPI);
		});
	};

	/**
	 * Renders the dashboard plots.	
	 * external references: condec.dashboard.js
	 * @param metrics the metrics returned from the API-call
	 */
	ConDecDecisionGroupsDashboardItem.prototype.renderData = function(metrics) {
		console.log("render data");
		console.log(metrics);

		plotDecisionLevels(metrics["groups"], metrics["coverage"]);
		plotDecisionGroups(metrics["groups"]);
		plotCoverage(metrics["coverage"]);
	};

	function plotDecisionLevels(groupsMap, coverageMap) {
		var decisionLevelsMap = new Map();
		decisionLevelsMap.set("High Level", groupsMap.get("High_Level"));
		decisionLevelsMap.set("Medium Level", groupsMap.get("Medium_Level"));
		decisionLevelsMap.set("Realization Level", groupsMap.get("Realization_Level"));
		if (coverageMap.get("0")) {
			decisionLevelsMap.set("No Level", coverageMap.get("0"));
		}
		conDecDashboard.createPieChartWithListOfElements(decisionLevelsMap,
			"piechart-decision-levels",
			"How many elements are there per decision level?", viewId);
	}

	function plotDecisionGroups(metric) {
		metric.delete("High_Level");
		metric.delete("Medium_Level");
		metric.delete("Realization_Level");
		metric.delete("NoGroup");
		conDecDashboard.createPieChartWithListOfElements(metric,
			"piechart-decision-groups",
			"How many elements are there per decision group?", viewId);
	}

	function plotCoverage(coverageMap) {
		coverageMap.delete("0"); // because we show respective elements in decision level plot
		var decisionGroupsCoverageMap = new Map();

		var defaultColorPalette = ["#91cc75", "#3ba272", "#73c0de", "#5470c6", "#9a60b4", "#ea7ccc"];
		var colorPalette = [];

		for (const [coverage, elements] of coverageMap) {
			var decisionGroupsCoverage = parseInt(coverage) - 1;
			decisionGroupsCoverageMap.set(decisionGroupsCoverage.toString(), elements);
			colorPalette.push(getColorForCoverage(decisionGroupsCoverage, defaultColorPalette));
		}

		conDecDashboard.createPieChartWithListOfElements(decisionGroupsCoverageMap, "piechart-decision-groups-coverage",
			"How many decision groups are assigned to the elements?", viewId, colorPalette);
	}

	function getColorForCoverage(coverage, defaultColorPalette) {
		switch (coverage) {
			case 0:
				return "#ee6666";
			case 1:
				return "#fc8452";
			case 2:
				return "#fac858";
		}
		return defaultColorPalette.shift();
	}

	return ConDecDecisionGroupsDashboardItem;
});
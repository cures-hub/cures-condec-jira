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
		preferences.definitionOfDone = {};
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
		conDecGroupingAPI.getDecisionGroupsMap(filterSettings, function(error, result) {
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

		plotDecisionLevels(metrics);
		createDecisionGroupsMap(metrics);
	};

	function plotDecisionLevels(metrics) {
		var decisionLevelsMap = new Map();
		decisionLevelsMap.set("High Level", metrics.get("High_Level"));
		decisionLevelsMap.set("Medium Level", metrics.get("Medium_Level"));
		decisionLevelsMap.set("Realization Level", metrics.get("Realization_Level"));
		conDecDashboard.createPieChartWithListOfElements(decisionLevelsMap,
			"piechart-decision-levels",
			"How many elements are there per decision level?", viewId);
	}
	
	function createDecisionGroupsMap(metrics) {
		metrics.delete("High_Level");
		metrics.delete("Medium_Level");
		metrics.delete("Realization_Level");
		conDecDashboard.createPieChartWithListOfElements(metrics,
			"piechart-decision-groups",
			"How many elements are there per decision group?", viewId);
	}

	return ConDecDecisionGroupsDashboardItem;
});
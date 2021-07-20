/*
 This module renders the dashboard and its configuration screen used in the general metrics dashboard item.

 Requires
 * condec.requirements.dashboard.js
 * condec.general.metrics.dashboard.js

 Is referenced in HTML by
 * dashboard/generalMetrics.vm
 */
define('dashboard/generalMetrics', [], function () {
	var dashboardAPI;

	var ConDecGeneralMetricsDashboardItem = function (API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecGeneralMetricsDashboardItem.prototype.render = function (context, preferences) {
		conDecDashboard.initRender(conDecGeneralMetricsDashboard, "general-metrics",
			dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecGeneralMetricsDashboardItem.prototype.renderEdit = function (context, preferences) {
		conDecDashboard.initConfiguration("general-metrics", dashboardAPI, preferences);
	};

	return ConDecGeneralMetricsDashboardItem;
});
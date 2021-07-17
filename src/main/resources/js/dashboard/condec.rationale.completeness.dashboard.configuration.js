/*
 This module renders the configuration screen used in the rationale completeness dashboard item.

 Requires
 * condec.requirements.dashboard.js
 * condec.rationale.completeness.dashboard.js

 Is referenced in HTML by
 * dashboard/rationaleCompleteness.vm
 */
define('dashboard/rationaleCompleteness', [], function () {
	var dashboardAPI;

	var ConDecRationaleCompletenessDashboardItem = function (API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.render = function (context, preferences) {
		conDecDashboard.initRender(conDecRationaleCompletenessDashboard, dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.renderEdit = function (context, preferences) {
		conDecDashboard.initConfiguration("rationale-completeness", dashboardAPI, preferences);
	};

	return ConDecRationaleCompletenessDashboardItem;
});
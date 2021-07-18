/*
 This module renders the dashboard and its configuration screen used in the rationale coverage dashboard item.

 Requires
 * condec.requirements.dashboard.js
 * condec.rationale.coverage.dashboard.js

 Is referenced in HTML by
 * dashboard/rationaleCoverage.vm
 */
define('dashboard/rationaleCoverage', [], function () {
	var dashboardAPI;

	var ConDecRationaleCoverageDashboardItem = function (API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCoverageDashboardItem.prototype.render = function (context, preferences) {
		conDecDashboard.initRender(conDecRationaleCoverageDashboard, "rationale-coverage",
			dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCoverageDashboardItem.prototype.renderEdit = function (context, preferences) {
		conDecDashboard.initConfiguration("rationale-coverage", dashboardAPI, preferences);
	};

	return ConDecRationaleCoverageDashboardItem;
});
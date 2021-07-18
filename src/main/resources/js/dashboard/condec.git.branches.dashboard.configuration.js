/*
 This module renders the dashboard and its configuration screen used in the feature branch dashboard item.

 Requires
 * condec.requirements.dashboard.js
 * condec.git.branches.dashboard.js

 Is referenced in HTML by
 * dashboard/featureBranches.vm
 */
define('dashboard/branches', [], function () {
	var dashboardAPI;

	var ConDecBranchesDashboardItem = function (API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecBranchesDashboardItem.prototype.render = function (context, preferences) {
		conDecDashboard.initRender(conDecBranchesDashboard, "branch", dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecBranchesDashboardItem.prototype.renderEdit = function (context, preferences) {
		conDecDashboard.initConfiguration("branch", dashboardAPI, preferences);
	};

	return ConDecBranchesDashboardItem;
});
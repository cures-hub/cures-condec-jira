/*
 This module renders the dashboard and its configuration screen used in the rationale completeness dashboard item.

 Requires
 * condec.requirements.dashboard.js
 * condec.rationale.completeness.dashboard.js

 Is referenced in HTML by
 * dashboard/rationaleCompleteness.vm
 */
define('dashboard/rationaleCompleteness', [], function() {
	var dashboardAPI;

	var ConDecRationaleCompletenessDashboardItem = function(API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.render = function(context, preferences) {
		conDecDashboard.initRender(this, "rationale-completeness", dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.renderEdit = function(context, preferences) {
		conDecDashboard.initConfiguration("rationale-completeness", dashboardAPI, preferences);
	};

	/**
	 * Gets the data to fill the dashboard plots by making an API-call.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the filterSettings used for the API-call
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.getData = function(dashboardAPI, filterSettings) {
		let self = this;
		conDecDashboardAPI.getRationaleCompleteness(filterSettings, function(error, result) {
			conDecDashboard.processData(error, result, self, "rationale-completeness", dashboardAPI);
		});
	};

	/**
	 * Render the dashboard plots.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param data the data returned from the API-call
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.renderData = function(data) {
		/*  init data for charts */
		var issuesSolvedByDecision = new Map();
		var decisionsSolvingIssues = new Map();
		var proArgumentDocumentedForDecision = new Map();
		var conArgumentDocumentedForAlternative = new Map();
		var conArgumentDocumentedForDecision = new Map();
		var proArgumentDocumentedForAlternative = new Map();

		/* set something in case no data will be added to them */
		issuesSolvedByDecision.set("none", "");
		decisionsSolvingIssues.set("none", "");
		proArgumentDocumentedForDecision.set("none", "");
		conArgumentDocumentedForAlternative.set("none", "");
		conArgumentDocumentedForDecision.set("none", "");
		proArgumentDocumentedForAlternative.set("none", "");

		/* form data for charts */
		issuesSolvedByDecision = data.issuesSolvedByDecision;
		decisionsSolvingIssues = data.decisionsSolvingIssues;
		proArgumentDocumentedForDecision = data.proArgumentDocumentedForDecision;
		conArgumentDocumentedForAlternative = data.conArgumentDocumentedForAlternative;
		conArgumentDocumentedForDecision = data.conArgumentDocumentedForDecision;
		proArgumentDocumentedForAlternative = data.proArgumentDocumentedForAlternative;

		/* define color palette */
		var colorPalette = ['#EE6666', '#91CC75'];

		/* render pie-charts */
		conDecDashboard.initializeChartWithColorPalette("piechartRich-IssuesSolvedByDecision",
			"", "How many issues (=decision problems) are solved by a decision?", issuesSolvedByDecision,
			colorPalette);
		conDecDashboard.initializeChartWithColorPalette("piechartRich-DecisionsSolvingIssues",
			"", "For how many decisions is the issue (=decision problem) documented?", decisionsSolvingIssues,
			colorPalette);
		conDecDashboard.initializeChartWithColorPalette("piechartRich-ProArgumentDocumentedForDecision",
			"", "How many decisions have at least one pro argument documented?", proArgumentDocumentedForDecision,
			colorPalette);
		conDecDashboard.initializeChartWithColorPalette("piechartRich-ConArgumentDocumentedForDecision",
			"", "How many decisions have at least one con argument documented?", conArgumentDocumentedForDecision,
			colorPalette);
		conDecDashboard.initializeChartWithColorPalette("piechartRich-ProArgumentDocumentedForAlternative",
			"", "How many alternatives have at least one pro argument documented?", proArgumentDocumentedForAlternative,
			colorPalette);
		conDecDashboard.initializeChartWithColorPalette("piechartRich-ConArgumentDocumentedForAlternative",
			"", "How many alternatives have at least one con argument documented?", conArgumentDocumentedForAlternative,
			colorPalette);
	};

	return ConDecRationaleCompletenessDashboardItem;
});
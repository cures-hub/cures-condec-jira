/*
 This module fills the box plots and pie charts used in the rationale completeness dashboard item.

 Requires
 * condec.requirements.dashboard.js

 Is referenced in HTML by
 * dashboard/rationaleCompleteness.vm
 */

(function (global) {

	var ConDecRationaleCompletenessDashboard = function () {
		console.log("ConDecRationaleCompletenessDashboard constructor");
	};

	/**
	 * Gets the data to fill the dashboard plots by making an API-call.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the filterSettings used for the API-call
	 */
	ConDecRationaleCompletenessDashboard.prototype.getData = function (dashboardAPI, filterSettings) {
		conDecDashboardAPI.getRationaleCompleteness(filterSettings, function (error, result) {
			conDecDashboard.processData(error, result, conDecRationaleCompletenessDashboard,
				"rationale-completeness", dashboardAPI);
		});
	};

	/**
	 * Render the dashboard plots.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param data the data returned from the API-call
	 */
	ConDecRationaleCompletenessDashboard.prototype.renderData = function (data) {
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
		ConDecReqDash.initializeChartWithColorPalette("piechartRich-IssuesSolvedByDecision",
			"", "How many issues (=decision problems) are solved by a decision?", issuesSolvedByDecision,
			colorPalette);
		ConDecReqDash.initializeChartWithColorPalette("piechartRich-DecisionsSolvingIssues",
			"", "For how many decisions is the issue (=decision problem) documented?", decisionsSolvingIssues,
			colorPalette);
		ConDecReqDash.initializeChartWithColorPalette("piechartRich-ProArgumentDocumentedForDecision",
			"", "How many decisions have at least one pro argument documented?", proArgumentDocumentedForDecision,
			colorPalette);
		ConDecReqDash.initializeChartWithColorPalette("piechartRich-ConArgumentDocumentedForDecision",
			"", "How many decisions have at least one con argument documented?", conArgumentDocumentedForDecision,
			colorPalette);
		ConDecReqDash.initializeChartWithColorPalette("piechartRich-ProArgumentDocumentedForAlternative",
			"", "How many alternatives have at least one pro argument documented?", proArgumentDocumentedForAlternative,
			colorPalette);
		ConDecReqDash.initializeChartWithColorPalette("piechartRich-ConArgumentDocumentedForAlternative",
			"", "How many alternatives have at least one con argument documented?", conArgumentDocumentedForAlternative,
			colorPalette);
	};

	global.conDecRationaleCompletenessDashboard = new ConDecRationaleCompletenessDashboard();
})(window);
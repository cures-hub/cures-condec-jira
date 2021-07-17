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

	ConDecRationaleCompletenessDashboard.prototype.init = function (filterSettings) {
		getMetrics(filterSettings);
	};

	function getMetrics(filterSettings) {
		if (!JSON.parse(filterSettings).projectKey || !JSON.parse(filterSettings).projectKey.length) {
			return;
		}

		conDecDashboard.showDashboardSection("condec-dashboard-processing-", "rationale-completeness");
		document.getElementById("condec-dashboard-selected-project-rationale-completeness").innerText = JSON.parse(filterSettings).projectKey;

		url = conDecAPI.restPrefix + "/dashboard/rationaleCompleteness.json";

		AJS.$.ajax({
			url: url,
			headers: { "Content-Type": "application/json; charset=utf-8", "Accept": "application/json"},
			type: "post",
			dataType: "json",
			data: filterSettings,
			async: true,
			success: conDecRationaleCompletenessDashboard.processData,
			error: conDecRationaleCompletenessDashboard.processDataBad
		});
	}

	ConDecRationaleCompletenessDashboard.prototype.processDataBad = function processDataBad(data) {
		conDecDashboard.showDashboardSection("condec-dashboard-contents-data-error-", "rationale-completeness");
	};

	ConDecRationaleCompletenessDashboard.prototype.processData = function processData(data) {
		conDecDashboard.showDashboardSection("condec-dashboard-contents-container-", "rationale-completeness");
		renderData(data);
	};

	function renderData(calculator) {
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
		issuesSolvedByDecision = calculator.issuesSolvedByDecision;
		decisionsSolvingIssues = calculator.decisionsSolvingIssues;
		proArgumentDocumentedForDecision = calculator.proArgumentDocumentedForDecision;
		conArgumentDocumentedForAlternative = calculator.conArgumentDocumentedForAlternative;
		conArgumentDocumentedForDecision = calculator.conArgumentDocumentedForDecision;
		proArgumentDocumentedForAlternative = calculator.proArgumentDocumentedForAlternative;

		/* render pie-charts */
		ConDecReqDash.initializeChart("piechartRich-IssuesSolvedByDecision",
			"", "How many issues (=decision problems) are solved by a decision?", issuesSolvedByDecision);
		ConDecReqDash.initializeChart("piechartRich-DecisionsSolvingIssues",
			"", "For how many decisions is the issue (=decision problem) documented?", decisionsSolvingIssues);
		ConDecReqDash.initializeChart("piechartRich-ProArgumentDocumentedForDecision",
			"", "How many decisions have at least one pro argument documented?", proArgumentDocumentedForDecision);
		ConDecReqDash.initializeChart("piechartRich-ConArgumentDocumentedForDecision",
			"", "How many decisions have at least one con argument documented?", conArgumentDocumentedForDecision);
		ConDecReqDash.initializeChart("piechartRich-ProArgumentDocumentedForAlternative",
			"", "How many alternatives have at least one pro argument documented?", proArgumentDocumentedForAlternative);
		ConDecReqDash.initializeChart("piechartRich-ConArgumentDocumentedForAlternative",
			"", "How many alternatives have at least one con argument documented?", conArgumentDocumentedForAlternative);
	}

	global.conDecRationaleCompletenessDashboard = new ConDecRationaleCompletenessDashboard();
})(window);
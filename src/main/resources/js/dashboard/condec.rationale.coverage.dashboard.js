/*
 This module fills the box plots and pie charts used in the rationale coverage dashboard item.

 Requires
 * condec.requirements.dashboard.js

 Is referenced in HTML by
 * dashboard/rationaleCoverage.vm
 */

(function (global) {
	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectNode;

	var ConDecRationaleCoverageDashboard = function() {
		console.log("ConDecRationaleCoverageDashboard constructor");
	};

	ConDecRationaleCoverageDashboard.prototype.init = function (filterSettings, issueType) {
		getHTMLNodes("condec-rationale-coverage-dashboard-configproject"
			, "condec-rationale-coverage-dashboard-contents-container"
			, "condec-rationale-coverage-dashboard-contents-data-error"
			, "condec-rationale-coverage-dashboard-no-project"
			, "condec-rationale-coverage-dashboard-processing"
			, "condec-dashboard-selected-project-rationale-coverage");

		getMetrics(filterSettings, issueType);
	};

	function getHTMLNodes(filterName, containerName, dataErrorName, noProjectName, processingName, projectName) {
		dashboardFilterNode = document.getElementById(filterName);
		dashboardContentNode = document.getElementById(containerName);
		dashboardDataErrorNode = document.getElementById(dataErrorName);
		dashboardNoContentsNode = document.getElementById(noProjectName);
		dashboardProcessingNode = document.getElementById(processingName);
		dashboardProjectNode = document.getElementById(projectName);
	}

	function showDashboardSection(node) {
		var hiddenClass = "hidden";
		dashboardFilterNode.classList.add(hiddenClass);
		dashboardContentNode.classList.add(hiddenClass);
		dashboardDataErrorNode.classList.add(hiddenClass);
		dashboardNoContentsNode.classList.add(hiddenClass);
		dashboardProcessingNode.classList.add(hiddenClass);
		node.classList.remove(hiddenClass);
	}

	function getMetrics(filterSettings, sourceKnowledgeTypes) {
		if (!JSON.parse(filterSettings).projectKey || !JSON.parse(filterSettings).projectKey.length || !JSON.parse(filterSettings).projectKey.length > 0) {
			return;
		}

		showDashboardSection(dashboardProcessingNode);
		var projectKey = JSON.parse(filterSettings).projectKey;
		dashboardProjectNode.innerText = projectKey;

		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		url = conDecAPI.restPrefix + "/dashboard/rationaleCoverage.json?sourceKnowledgeTypes=" + sourceKnowledgeTypes;

		console.log("Starting REST query.");
		AJS.$.ajax({
			url: url,
			headers: { "Content-Type": "application/json; charset=utf-8", "Accept": "application/json"},
			type: "post",
			dataType: "json",
			data: filterSettings,
			async: true,
			success: conDecRationaleCoverageDashboard.processData,
			error: conDecRationaleCoverageDashboard.processDataBad
		});
	}

	ConDecRationaleCoverageDashboard.prototype.processDataBad = function (data) {
		console.log(data.responseJSON.error);
		showDashboardSection(dashboardDataErrorNode);
	};

	ConDecRationaleCoverageDashboard.prototype.processData = function (data) {
		processXhrResponseData(data);
	};

	function processXhrResponseData(data) {
		doneWithXhrRequest();
		showDashboardSection(dashboardContentNode);
		renderData(data);
	}

	function doneWithXhrRequest() {
		dashboardProcessingNode.classList.remove("error");
		showDashboardSection(dashboardProcessingNode);
	}

	function renderData(calculator) {
		/*  init data for charts */
		var issuesPerSelectedJiraIssue = new Map();
		var decisionsPerSelectedJiraIssue = new Map();
		var issueDocumentedForSelectedJiraIssue = new Map();
		var decisionDocumentedForSelectedJiraIssue = new Map();

		/* set something for box plots in case no data will be added to them */
		issuesPerSelectedJiraIssue.set("none", 0);
		decisionsPerSelectedJiraIssue.set("none", 0);

		issueDocumentedForSelectedJiraIssue.set("no rationale elements", "");
		decisionDocumentedForSelectedJiraIssue.set("no code classes", "");

		/* form data for charts */
		issuesPerSelectedJiraIssue = calculator.issuesPerSelectedJiraIssue;
		decisionsPerSelectedJiraIssue = calculator.decisionsPerSelectedJiraIssue;
		issueDocumentedForSelectedJiraIssue = calculator.issueDocumentedForSelectedJiraIssue;
		decisionDocumentedForSelectedJiraIssue = calculator.decisionDocumentedForSelectedJiraIssue;

		/* define color palette */
		var colorpalette = ['#EE6666', '#FAC858', '#91CC75'];

		/* render box-plots */
		ConDecReqDash.initializeChartWithColorPalette("boxplot-IssuesPerJiraIssue",
			"", "# Issues per element", issuesPerSelectedJiraIssue,
			colorpalette);
		ConDecReqDash.initializeChartWithColorPalette("boxplot-DecisionsPerJiraIssue",
			"", "# Decisions per element", decisionsPerSelectedJiraIssue,
			colorpalette);
		/* render pie-charts */
		ConDecReqDash.initializeChartWithColorPalette("piechartRich-IssueDocumentedForSelectedJiraIssue",
			"", "For how many elements is an issue documented?", issueDocumentedForSelectedJiraIssue,
			colorpalette);
		ConDecReqDash.initializeChartWithColorPalette("piechartRich-DecisionDocumentedForSelectedJiraIssue",
			"", "For how many elements is a decision documented?", decisionDocumentedForSelectedJiraIssue,
			colorpalette);
	}

	global.conDecRationaleCoverageDashboard = new ConDecRationaleCoverageDashboard();
})(window);
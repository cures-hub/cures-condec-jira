/*
 This module fills the box plots and pie charts used in the general metrics dashboard item.

 Requires
 * condec.requirements.dashboard.js

 Is referenced in HTML by
 * dashboard/generalMetrics.vm
 */

(function (global) {
	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;

	var ConDecGeneralMetricsDashboard = function() {
		console.log("ConDecGeneralMetricsDashboard constructor");
	};

	ConDecGeneralMetricsDashboard.prototype.init = function init(filterSettings) {
		getHTMLNodes("condec-general-metrics-dashboard-configproject"
			, "condec-general-metrics-dashboard-contents-container"
			, "condec-general-metrics-dashboard-contents-data-error"
			, "condec-general-metrics-dashboard-no-project"
			, "condec-general-metrics-dashboard-processing");

		getMetrics(filterSettings);
	};

	function getHTMLNodes(filterName, containerName, dataErrorName, noProjectName, processingName) {
		dashboardFilterNode = document.getElementById(filterName);
		dashboardContentNode = document.getElementById(containerName);
		dashboardDataErrorNode = document.getElementById(dataErrorName);
		dashboardNoContentsNode = document.getElementById(noProjectName);
		dashboardProcessingNode = document.getElementById(processingName);
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

	function getMetrics(filterSettings) {
		if (!JSON.parse(filterSettings).projectKey || !JSON.parse(filterSettings).projectKey.length || !JSON.parse(filterSettings).projectKey.length > 0) {
			return;
		}
		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		showDashboardSection(dashboardProcessingNode);

		url = conDecAPI.restPrefix + "/dashboard/generalMetrics.json";

		console.log("Starting REST query.");
		AJS.$.ajax({
			url: url,
			headers: { "Content-Type": "application/json; charset=utf-8", "Accept": "application/json"},
			type: "post",
			dataType: "json",
			data: filterSettings,
			async: true,
			success: conDecGeneralMetricsDashboard.processData,
			error: conDecGeneralMetricsDashboard.processDataBad
		});
	}

	ConDecGeneralMetricsDashboard.prototype.processDataBad = function processDataBad(data) {
		console.log(data.responseJSON.error);
		showDashboardSection(dashboardDataErrorNode);
	};

	ConDecGeneralMetricsDashboard.prototype.processData = function processData(data) {
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

	function renderData(data) {
		/*  init data for charts */
		var commentsPerIssue = new Map();
		var commitsPerIssue = new Map();
		var reqCodeSummary = new Map();
		var decSources = new Map();
		var relevantSentences = new Map();
		var knowledgeTypeDistribution = new Map();

		/* set something in case no data will be added to them */
		commentsPerIssue.set("none", 0);
		commitsPerIssue.set("none", 0);

		reqCodeSummary.set("no code classes", "");
		decSources.set("no rationale elements", "");
		relevantSentences.set("no Jira issue", "");
		knowledgeTypeDistribution.set("no knowledge type", "");

		/* form data for charts */
		commentsPerIssue = data.numberOfCommentsPerJiraIssue;
		commitsPerIssue = data.numberOfCommitsPerJiraIssue;
		reqCodeSummary = data.requirementsAndCodeFiles;
		decSources = data.numberOfElementsPerDocumentationLocation;
		relevantSentences = data.numberOfRelevantComments;
		knowledgeTypeDistribution = data.distributionOfKnowledgeTypes;

		/* render box-plots */
		ConDecReqDash.initializeChart("boxplot-CommentsPerJiraIssue",
			"", "#Comments per Jira Issue", commentsPerIssue);
		ConDecReqDash.initializeChart("boxplot-CommitsPerJiraIssue",
			"", "#Commits per Jira Issue", commitsPerIssue);
		/* render pie-charts */
		ConDecReqDash.initializeChart("piechartInteger-ReqCodeSummary",
			"", "#Requirements and Code Classes", reqCodeSummary);
		ConDecReqDash.initializeChart("piechartRich-DecSources",
			"", "#Rationale Elements per Origin", decSources);
		ConDecReqDash.initializeChart("piechartInteger-RelevantSentences",
			"", "Comments in Jira Issues relevant to Decision Knowledge", relevantSentences);
		ConDecReqDash.initializeChart("piechartInteger-KnowledgeTypeDistribution",
			"", "Distribution of Knowledge Types", knowledgeTypeDistribution);
	}

	global.conDecGeneralMetricsDashboard = new ConDecGeneralMetricsDashboard();
})(window);
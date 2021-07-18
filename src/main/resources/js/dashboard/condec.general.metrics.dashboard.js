/*
 This module fills the box plots and pie charts used in the general metrics dashboard item.

 Requires
 * condec.requirements.dashboard.js

 Is referenced in HTML by
 * dashboard/generalMetrics.vm
 */

(function (global) {

	var ConDecGeneralMetricsDashboard = function() {
		console.log("ConDecGeneralMetricsDashboard constructor");
	};

	ConDecGeneralMetricsDashboard.prototype.getData = function (dashboardAPI, filterSettings) {
		conDecDashboardAPI.getGeneralMetrics(filterSettings, function (error, result) {
			conDecDashboard.processData(error, result, conDecGeneralMetricsDashboard,
				"general-metrics", dashboardAPI);
		});
	};

	ConDecGeneralMetricsDashboard.prototype.renderData = function (data) {
		/*  init data for charts */
		var commentsPerIssue = new Map();
		var commitsPerIssue = new Map();
		var reqCodeSummary = new Map();
		var decSources = new Map();
		var relevantSentences = new Map();
		var knowledgeTypeDistribution = new Map();
		var definitionOfDoneCheckResults = new Map();

		/* set something in case no data will be added to them */
		commentsPerIssue.set("none", 0);
		commitsPerIssue.set("none", 0);

		reqCodeSummary.set("no code classes", "");
		decSources.set("no rationale elements", "");
		relevantSentences.set("no Jira issue", "");
		knowledgeTypeDistribution.set("no knowledge type", "");
		definitionOfDoneCheckResults.set("no rationale elements", "");

		/* form data for charts */
		commentsPerIssue = data.numberOfCommentsPerIssue;
		commitsPerIssue = data.numberOfCommits;
		reqCodeSummary = data.reqAndClassSummary;
		decSources = data.elementsFromDifferentOrigins;
		relevantSentences = data.numberOfRelevantComments;
		knowledgeTypeDistribution = data.distributionOfKnowledgeTypes;
		definitionOfDoneCheckResults = data.definitionOfDoneCheckResults;

		/* define color palette */
		var colorPalette = ['#91CC75', '#EE6666'];

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
		ConDecReqDash.initializeChartWithColorPalette("piechartRich-DoDCheck",
			"", "Definition of Done Check", definitionOfDoneCheckResults, colorPalette);
	}

	global.conDecGeneralMetricsDashboard = new ConDecGeneralMetricsDashboard();
})(window);
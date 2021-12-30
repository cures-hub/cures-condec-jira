/**
 * This module renders the dashboard and its configuration screen used in the general metrics dashboard item.
 *
 * Requires
 * condec.dashboard.js
 */
define('dashboard/generalMetrics', [], function() {
	var dashboardAPI;

	var ConDecGeneralMetricsDashboardItem = function(API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecGeneralMetricsDashboardItem.prototype.render = function(context, preferences) {
		conDecDashboard.initRender(this, "general-metrics", dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecGeneralMetricsDashboardItem.prototype.renderEdit = function(context, preferences) {
		conDecDashboard.initConfiguration("general-metrics", dashboardAPI, preferences);
	};

	/**
	 * Gets the data to fill the dashboard plots by making an API-call.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the filterSettings used for the API-call
	 */
	ConDecGeneralMetricsDashboardItem.prototype.getData = function(dashboardAPI, filterSettings) {
		delete filterSettings.definitionOfDone;
		let self = this;
		conDecDashboardAPI.getGeneralMetrics(filterSettings, function(error, result) {
			conDecDashboard.processData(error, result, self, "general-metrics", dashboardAPI);
		});
	};

	/**
	 * Render the dashboard plots.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param data the data returned from the API-call
	 */
	ConDecGeneralMetricsDashboardItem.prototype.renderData = function(data) {
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
		conDecDashboard.initializeChart("boxplot-CommentsPerJiraIssue",
			"", "#Comments per Jira Issue", commentsPerIssue);
		conDecDashboard.initializeChart("boxplot-CommitsPerJiraIssue",
			"", "#Commits per Jira Issue", commitsPerIssue);
		/* render pie-charts */
		conDecDashboard.initializeChart("piechartRich-ReqCodeSummary",
			"", "#Requirements and Code Classes", reqCodeSummary);
		conDecDashboard.initializeChart("piechartRich-DecSources",
			"", "#Rationale Elements per Origin", decSources);
		conDecDashboard.initializeChart("piechartInteger-RelevantSentences",
			"", "Comments in Jira Issues relevant to Decision Knowledge", relevantSentences);
		conDecDashboard.initializeChart("piechartRich-KnowledgeTypeDistribution",
			"", "Distribution of Knowledge Types", knowledgeTypeDistribution);
		conDecDashboard.initializeChartWithColorPalette("piechartRich-DoDCheck",
			"", "Definition of Done Check", definitionOfDoneCheckResults, colorPalette);
	};

	return ConDecGeneralMetricsDashboardItem;
});
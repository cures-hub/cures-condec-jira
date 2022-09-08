/**
 * This module renders the dashboard and its configuration screen used in the general metrics dashboard item.
 *
 * Requires
 * condec.dashboard.js
 */
/* global conDecDashboard */
define("dashboard/generalMetrics", [], function() {
	var dashboardAPI;
	const viewId = "general-metrics";

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
		conDecDashboard.initDashboard(this, viewId, dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecGeneralMetricsDashboardItem.prototype.renderEdit = function(context, preferences) {
		conDecDashboard.initConfiguration(viewId, dashboardAPI, preferences);
	};

	/**
	 * Gets the metrics to fill the dashboard plots by making an API-call.
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
			conDecDashboard.processData(error, result, self, viewId, dashboardAPI);
		});
	};

	/**
	 * Renders the dashboard plots.
	 * external references: condec.dashboard.js
	 * @param generalMetrics the metrics returned from the API-call
	 */
	ConDecGeneralMetricsDashboardItem.prototype.renderData = function(generalMetrics) {
		var colorPalette = ["#EE6666", "#91CC75"];

		let commentsSum = 0;
		for (const [commentsCount, elements] of generalMetrics.numberOfCommentsPerJiraIssueMap) {
			commentsSum += parseInt(commentsCount, 10) * elements.length;
		}
		conDecDashboard.createBoxPlotWithListOfElements("boxplot-CommentsPerJiraIssue",
			`#Comments per Jira Issue\n Sum: ${commentsSum}`,
			generalMetrics.numberOfCommentsPerJiraIssueMap, viewId);

		let commitsSum = 0;
		for (const [commitsCount, elements] of generalMetrics.numberOfCommitsPerJiraIssueMap) {
			commitsSum += parseInt(commitsCount, 10) * elements.length;
		}
		conDecDashboard.createBoxPlotWithListOfElements("boxplot-CommitsPerJiraIssue",
			`#Commits per Jira Issue\n Transcribed into Comments\n Sum: ${commitsSum}`,
			generalMetrics.numberOfCommitsPerJiraIssueMap, viewId);

		let numberOfUnlinkedFiles = 0;
		if (generalMetrics.numberOfLinkedJiraIssuesForCodeMap.has("0")) {
			numberOfUnlinkedFiles = generalMetrics.numberOfLinkedJiraIssuesForCodeMap.get("0").length;
		}
		conDecDashboard.createBoxPlotWithListOfElements("boxplot-LinkedJiraIssuesPerCodeFile",
			`#Linked Jira Issues\n Per Code File\n #Unlinked Files: ${numberOfUnlinkedFiles}`,
			generalMetrics.numberOfLinkedJiraIssuesForCodeMap, viewId);

		let linesOfCodeSum = 0;
		for (const [linesOfCode, elements] of generalMetrics.linesOfCodeMap) {
			linesOfCodeSum += parseInt(linesOfCode, 10) * elements.length;
		}
		conDecDashboard.createBoxPlotWithListOfElements("boxplot-LinesOfCode",
			`#Lines\n Per Code File\n Sum: ${linesOfCodeSum}`,
			generalMetrics.linesOfCodeMap, viewId);

		conDecDashboard.createPieChartWithListOfElements(generalMetrics.requirementsAndCodeFilesMap,
			"piechartRich-ReqCodeSummary", "#Requirements and Code Files", viewId);
		conDecDashboard.createPieChartWithListOfElements(generalMetrics.originMap,
			"piechartRich-DecSources", "#Rationale Elements per Origin", viewId);
		conDecDashboard.createSimplePieChart(generalMetrics.numberOfRelevantAndIrrelevantCommentsMap,
			"piechartInteger-RelevantSentences", "#Comments in Jira Issues relevant to Decision Knowledge");
		createPieChartForNumberOfDecisionKnowledgeElements(generalMetrics.decisionKnowledgeTypeMap);
		conDecDashboard.createPieChartWithListOfElements(generalMetrics.definitionOfDoneCheckResultsMap,
			"piechartRich-DoDCheck", "Definition of Done Check", viewId, colorPalette);
	};

	function createPieChartForNumberOfDecisionKnowledgeElements(decisionKnowledgeElementsMap) {
		var defaultColorPalette = ["#3ba272", "#fc8452", "#9a60b4", "#ea7ccc"];
		var colorPaletteForDecisionKnowledge = [];

		for (const type of decisionKnowledgeElementsMap.keys()) {
			colorPaletteForDecisionKnowledge.push(getColorForKnowledgeType(type, defaultColorPalette));
		}

		conDecDashboard.createPieChartWithListOfElements(decisionKnowledgeElementsMap,
			"piechartRich-KnowledgeTypeDistribution", "#Decision Knowledge Elements", viewId, colorPaletteForDecisionKnowledge);
	}

	function getColorForKnowledgeType(type, defaultColorPalette) {
		switch (type) {
			case "Pro":
				return "#91cc75";
			case "Con":
				return "#ee6666";
			case "Issue":
				return "#fac858";
			case "Alternative":
				return "#73c0de";
			case "Decision":
				return "#5470c6";
		}
		return defaultColorPalette.shift();
	}

	return ConDecGeneralMetricsDashboardItem;
});
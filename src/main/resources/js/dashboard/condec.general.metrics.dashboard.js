/*
 This module fills the box plots and pie charts used in the general metrics dashboard item.

 Requires
 * js/condec.requirements.dashboard.js

 Is referenced in HTML by
 * generalMetricsDashboardItem.vm
 */

(function (global) {
	var ConDecGeneralMetricsDashboard = function ConDecGeneralMetricsDashboard() {
		console.log("ConDecGeneralMetricsDashboard constructor");
	};

	ConDecGeneralMetricsDashboard.prototype.init = function init(projectKey) {
		getHTMLNodes("condec-general-metrics-dashboard-contents-container"
			, "condec-general-metrics-dashboard-contents-data-error"
			, "condec-general-metrics-dashboard-no-project"
			, "condec-general-metrics-dashboard-processing"
			, "condec-general-metrics-dashboard-nogit-error");

		getMetrics(projectKey);
	};

	function getHTMLNodes(containerName, dataErrorName, noProjectName, processingName, noGitName) {
		dashboardContentNode = document.getElementById(containerName);
		dashboardDataErrorNode = document.getElementById(dataErrorName);
		dashboardNoContentsNode = document.getElementById(noProjectName);
		dashboardProcessingNode = document.getElementById(processingName);
		dashboardProjectWithoutGit = document.getElementById(noGitName);
	}

	function showDashboardSection(node) {
		var hiddenClass = "hidden";
		dashboardContentNode.classList.add(hiddenClass);
		dashboardDataErrorNode.classList.add(hiddenClass);
		dashboardNoContentsNode.classList.add(hiddenClass);
		dashboardProcessingNode.classList.add(hiddenClass);
		dashboardProjectWithoutGit.classList.add(hiddenClass);
		node.classList.remove(hiddenClass);
	}

	function getMetrics(projectKey) {
		if (!projectKey || !projectKey.length || !projectKey.length > 0) {
			return;
		}
		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		showDashboardSection(dashboardProcessingNode);
		url = conDecAPI.restPrefix + "/dashboard/getNumberOfCommentsPerJiraIssue.json?projectKey=" + projectKey;
		/* get cache or server data? */
		if (localStorage.getItem("condec.restCacheTTL")) {
			console.log("condec.restCacheTTL setting found");
			if (localStorage.getItem(url)) {
				var data = null;
				var now = Date.now();
				var cacheTTL = parseInt(localStorage.getItem("condec.restCacheTTL"));
				try {
					data = JSON.parse(localStorage.getItem(url));
				} catch (ex) {
					data = null;
				}
				if (data && cacheTTL) {
					if (now - data.timestamp < cacheTTL) {
						console.log(
							"Cache is within specified TTL, therefore getting data from local cache instead from server."
						);
						return processXhrResponseData(data);
					} else {
						console.log("Cache TTL expired, therefore starting  REST query.");
					}
				}
				if (!cacheTTL) {
					console.log(
						"Cache TTL is not a number, therefore starting  REST query."
					);
				}
			}
		} else {
			localStorage.setItem("condec.restCacheTTL", 1000 * 60 * 3); /*
																	 * init 3
																	 * minute
																	 * caching
																	 */
		}
		console.log("Starting  REST query.");
		AJS.$.ajax({
			url: url,
			type: "get",
			dataType: "json",
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
		data.timestamp = Date.now();
		localStorage.setItem(url, JSON.stringify(data, null, 1));
		processing = null;
		renderData(data);
	}

	function doneWithXhrRequest() {
		dashboardProcessingNode.classList.remove("error");
		showDashboardSection(dashboardProcessingNode);
	}

	function renderData(data) {
		showDashboardSection(dashboardContentNode);

		/*  init data for charts */
		var commentsPerIssue = new Map();

		/* set something for box plots in case no data will be added to them */
		commentsPerIssue.set("none", 0);

		/* form data for charts */
		commentsPerIssue = data;

		/* render box-plots */
		ConDecReqDash.initializeChart("boxplot-CommentsPerJiraIssue",
			"", "#Comments per Jira Issue", commentsPerIssue);
		ConDecReqDash.initializeChart("boxplot-CommitsPerJiraIssue",
			"", "#Commits per Jira Issue", "$numberOfCommitsPerJiraIssue");
		/* render pie-charts */
		ConDecReqDash.initializeChart("piechartInteger-ReqCodeSummary",
			"", "#Requirements and Code Classes", "$requirementsAndCodeFiles");
		ConDecReqDash.initializeChart("piechartRich-DecSources",
			"", "#Rationale Elements per Origin", "$numberOfElementsPerDocumentationLocation");
		ConDecReqDash.initializeChart("piechartInteger-RelevantSentences",
			"", "Comments in Jira Issues relevant to Decision Knowledge", "$numberOfRelevantComments");
		ConDecReqDash.initializeChart("piechartInteger-KnowledgeTypeDistribution",
			"", "Distribution of Knowledge Types", "$distributionOfKnowledgeTypes");
	}

	global.conDecGeneralMetricsDashboard = new ConDecGeneralMetricsDashboard();
})(window);
/*
 This module fills the box plots and pie charts used in the rationale coverage dashboard item.

 Requires
 * js/condec.requirements.dashboard.js

 Is referenced in HTML by
 * rationaleCoverageDashboardItem.vm
 */

(function (global) {
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var ConDecRationaleCoverageDashboard = function ConDecRationaleCoverageDashboard() {
		console.log("ConDecRationaleCoverageDashboard constructor");
	};

	ConDecRationaleCoverageDashboard.prototype.initProject = function init(projectKey) {
		document.getElementById("condec-dashboard-rationale-coverage-issuetype-selection").classList.remove("hidden");

		var issueTypeSelection = document.getElementById("condec-dashboard-rationale-coverage-issuetype-input");

		removeOptions(issueTypeSelection);

		getJiraIssueTypes(projectKey);
	};

	ConDecRationaleCoverageDashboard.prototype.init = function init(projectKey, issueType, linkDistance) {
		getHTMLNodes("condec-rationale-coverage-dashboard-contents-container"
			, "condec-rationale-coverage-dashboard-contents-data-error"
			, "condec-rationale-coverage-dashboard-no-project"
			, "condec-rationale-coverage-dashboard-processing"
			, "condec-rationale-coverage-dashboard-nogit-error");

		getMetrics(projectKey, issueType, linkDistance);
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

	function removeOptions(selectElement) {
		var i, L = selectElement.options.length - 1;
		for(i = L; i >= 0; i--) {
			selectElement.remove(i);
		}
	}

	function getJiraIssueTypes(projectKey) {
		if (!projectKey || !projectKey.length || !projectKey.length > 0) {
			return;
		}
		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		url = conDecAPI.restPrefix + "/dashboard/jiraIssueTypes.json?projectKey=" + projectKey;

		console.log("Starting REST query.");
		AJS.$.ajax({
			url: url,
			type: "get",
			dataType: "json",
			async: true,
			success: conDecRationaleCoverageDashboard.fillOptionsJiraIssueTypes,
			error: conDecRationaleCoverageDashboard.processDataBad
		});
	}

	ConDecRationaleCoverageDashboard.prototype.fillOptionsJiraIssueTypes = function fillOptionsJiraIssueTypes(data) {
		var jiraIssueTypes = getList(JSON.stringify(data));

		var jiraIssueTypeNode = document.getElementById("condec-dashboard-rationale-coverage-issuetype-input");

		var emptyIssueType = document.createElement('option');
		emptyIssueType.value = "";
		emptyIssueType.text = "";
		jiraIssueTypeNode.options.add(emptyIssueType);

		for (i = 0; i < jiraIssueTypes.length; i++) {
			var issueType = document.createElement('option');
			issueType.value = jiraIssueTypes[i];
			issueType.text = jiraIssueTypes[i];
			jiraIssueTypeNode.options.add(issueType);
		}
	};

	function getList(jsonString) {
		jsonString = jsonString.replace("\[", "").replace("\]", "");
		jsonString = jsonString.replaceAll("\"", "");

		return jsonString.split(",");
	}

	function getMetrics(projectKey, issueType, linkDistance) {
		if (!projectKey || !projectKey.length || !projectKey.length > 0) {
			return;
		}
		if (!issueType || !issueType.length || !issueType.length > 0) {
			return;
		}
		if (!linkDistance || !(linkDistance >= 0)) {
			return;
		}
		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		showDashboardSection(dashboardProcessingNode);

		var filterSettings = getFilterSettings(projectKey, linkDistance);

		url = conDecAPI.restPrefix + "/dashboard/rationaleCoverage.json?issueType=" + issueType;

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

	function getFilterSettings(projectKey, linkDistance) {
		var filterSettings = {};
		filterSettings.projectKey = projectKey;
		filterSettings.searchTerm = "";
		filterSettings.linkDistance = linkDistance;
		return JSON.stringify(filterSettings);
	}

	ConDecRationaleCoverageDashboard.prototype.processDataBad = function processDataBad(data) {
		console.log(data.responseJSON.error);
		showDashboardSection(dashboardDataErrorNode);
	};

	ConDecRationaleCoverageDashboard.prototype.processData = function processData(data) {
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

	function getMap(jsonString) {
		jsonString = jsonString.replace("\{", "").replace("\}", "");
		jsonString = jsonString.replaceAll("\"", "");

		var jsMap = new Map();
		var mapEntries = jsonString.split(",");
		for (i = 0; i < mapEntries.length; i++) {
			var mapEntry = mapEntries[i].split(":");
			jsMap.set(mapEntry[0], mapEntry[1]);
		}
		return jsMap;
	}

	function renderData(data) {
		var jsonStr = JSON.stringify(data);
		var json = JSON.parse(jsonStr);

		/*  init data for charts */
		var issuesPerJiraIssue = new Map();
		var decisionsPerJiraIssue = new Map();
		var decisionDocumentedForSelectedJiraIssue = new Map();
		var issueDocumentedForSelectedJiraIssue = new Map();

		/* set something for box plots in case no data will be added to them */
		issuesPerJiraIssue.set("none", 0);
		decisionsPerJiraIssue.set("none", 0);

		decisionDocumentedForSelectedJiraIssue.set("no code classes", "");
		issueDocumentedForSelectedJiraIssue.set("no rationale elements", "");

		/* form data for charts */
		issuesPerJiraIssue = getMap(JSON.stringify(json.issuesPerJiraIssue));
		decisionsPerJiraIssue = getMap(JSON.stringify(json.decisionsPerJiraIssue));
		decisionDocumentedForSelectedJiraIssue = getMap(JSON.stringify(json.decisionDocumentedForSelectedJiraIssue));
		issueDocumentedForSelectedJiraIssue = getMap(JSON.stringify(json.issueDocumentedForSelectedJiraIssue));

		/* render box-plots */
		ConDecReqDash.initializeChart("boxplot-IssuesPerJiraIssue",
			"", "#Decisions per Jira Issue", issuesPerJiraIssue);
		ConDecReqDash.initializeChart("boxplot-DecisionsPerJiraIssue",
			"", "#Issues per Jira Issue", decisionsPerJiraIssue);
		/* render pie-charts */
		ConDecReqDash.initializeChart("piechartRich-DecisionDocumentedForSelectedJiraIssue",
			"", "For how many selected issue type elements is an issue documented?", decisionDocumentedForSelectedJiraIssue);
		ConDecReqDash.initializeChart("piechartRich-IssueDocumentedForSelectedJiraIssue",
			"", "For how many selected issue type elements is a decision documented?", issueDocumentedForSelectedJiraIssue);
	}

	global.conDecRationaleCoverageDashboard = new ConDecRationaleCoverageDashboard();
})(window);
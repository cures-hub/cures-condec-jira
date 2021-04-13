/*
 This module fills the box plots and pie charts used in the rationale coverage dashboard item.

 Requires
 * js/condec.requirements.dashboard.js

 Is referenced in HTML by
 * rationaleCoverageDashboardItem.vm
 */

(function (global) {
	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var ConDecRationaleCoverageDashboard = function ConDecRationaleCoverageDashboard() {
		console.log("ConDecRationaleCoverageDashboard constructor");
	};

	ConDecRationaleCoverageDashboard.prototype.setJiraIssueTypes = function setJiraIssueTypes(projectKey) {
		var issueTypeSelection = document.getElementById("condec-dashboard-rationale-coverage-issuetype-input");

		removeOptions(issueTypeSelection);

		getJiraIssueTypes(projectKey);
	};

	ConDecRationaleCoverageDashboard.prototype.setKnowledgeTypes = function setKnowledgeTypes(projectkey) {
		var KnowledgeTypeSelection = document.getElementById("condec-dashboard-rationale-coverage-knowledgetypes-input");

		removeOptions(KnowledgeTypeSelection);

		getKnowledgeTypes(projectkey);
	};

	ConDecRationaleCoverageDashboard.prototype.setDocumentationLocations = function setDocumentationLocations() {
		var documentationLocationSelection = document.getElementById("condec-dashboard-rationale-coverage-documentationlocation-input");

		removeOptions(documentationLocationSelection);

		getDocumentationLocations();
	};

	ConDecRationaleCoverageDashboard.prototype.setKnowledgeStatus = function setKnowledgeStatus() {
		var KnowledgeStatusSelection = document.getElementById("condec-dashboard-rationale-coverage-knowledgestatus-input");

		removeOptions(KnowledgeStatusSelection);

		getKnowledgeStatus();
	};

	ConDecRationaleCoverageDashboard.prototype.setLinkTypes = function setLinkTypes() {
		var LinkTypeSelection = document.getElementById("condec-dashboard-rationale-coverage-linktypes-input");

		removeOptions(LinkTypeSelection);

		getLinkTypes();
	};

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
			async: false,
			success: conDecRationaleCoverageDashboard.fillOptionsJiraIssueTypes,
			error: conDecRationaleCoverageDashboard.processDataBad
		});
	}

	ConDecRationaleCoverageDashboard.prototype.fillOptionsJiraIssueTypes = function fillOptionsJiraIssueTypes(data) {
		var jiraIssueTypes = getList(JSON.stringify(data));

		var jiraIssueTypeNode = document.getElementById("condec-dashboard-rationale-coverage-issuetype-input");

		for (i = 0; i < jiraIssueTypes.length; i++) {
			var issueType = document.createElement('option');
			issueType.value = jiraIssueTypes[i];
			issueType.text = jiraIssueTypes[i];
			jiraIssueTypeNode.options.add(issueType);
		}
	};

	function getKnowledgeTypes(projectKey) {
		if (!projectKey || !projectKey.length || !projectKey.length > 0) {
			return;
		}
		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		url = conDecAPI.restPrefix + "/dashboard/knowledgeTypes.json?projectKey=" + projectKey;

		console.log("Starting REST query.");
		AJS.$.ajax({
			url: url,
			type: "get",
			dataType: "json",
			async: false,
			success: conDecRationaleCoverageDashboard.fillOptionsKnowledgeTypes,
			error: conDecRationaleCoverageDashboard.processDataBad
		});
	}

	ConDecRationaleCoverageDashboard.prototype.fillOptionsKnowledgeTypes = function fillOptionsKnowledgeTypes(data) {
		var knowledgeTypes = getList(JSON.stringify(data));

		var knowledgeTypeNode = document.getElementById("condec-dashboard-rationale-coverage-knowledgetypes-input");

		for (i = 0; i < knowledgeTypes.length; i++) {
			var knowledgeType = document.createElement('option');
			knowledgeType.value = knowledgeTypes[i];
			knowledgeType.text = knowledgeTypes[i];
			knowledgeTypeNode.options.add(knowledgeType);
		}
	};

	function getDocumentationLocations() {
		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		url = conDecAPI.restPrefix + "/dashboard/documentationLocations";

		console.log("Starting REST query.");
		AJS.$.ajax({
			url: url,
			type: "get",
			dataType: "json",
			async: false,
			success: conDecRationaleCoverageDashboard.fillOptionsDocumentationLocations,
			error: conDecRationaleCoverageDashboard.processDataBad
		});
	}

	ConDecRationaleCoverageDashboard.prototype.fillOptionsDocumentationLocations = function fillOptionsDocumentationLocations(data) {
		var documentationLocations = getList(JSON.stringify(data));

		var documentationLocationNode = document.getElementById("condec-dashboard-rationale-coverage-documentationlocation-input");

		for (i = 0; i < documentationLocations.length; i++) {
			var documentationLocation = document.createElement('option');
			documentationLocation.value = documentationLocations[i];
			documentationLocation.text = documentationLocations[i];
			documentationLocationNode.options.add(documentationLocation);
		}
	};

	function getKnowledgeStatus() {
		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		url = conDecAPI.restPrefix + "/dashboard/knowledgeStatus";

		console.log("Starting REST query.");
		AJS.$.ajax({
			url: url,
			type: "get",
			dataType: "json",
			async: false,
			success: conDecRationaleCoverageDashboard.fillOptionsKnowledgeStatus,
			error: conDecRationaleCoverageDashboard.processDataBad
		});
	}

	ConDecRationaleCoverageDashboard.prototype.fillOptionsKnowledgeStatus = function fillOptionsKnowledgeStatus(data) {
		var knowledgeStatuses = getList(JSON.stringify(data));

		var knowledgeStatusNode = document.getElementById("condec-dashboard-rationale-coverage-knowledgestatus-input");

		for (i = 0; i < knowledgeStatuses.length; i++) {
			var knowledgeStatus = document.createElement('option');
			knowledgeStatus.value = knowledgeStatuses[i];
			knowledgeStatus.text = knowledgeStatuses[i];
			knowledgeStatusNode.options.add(knowledgeStatus);
		}
	};

	function getLinkTypes() {
		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		url = conDecAPI.restPrefix + "/dashboard/linkTypes";

		console.log("Starting REST query.");
		AJS.$.ajax({
			url: url,
			type: "get",
			dataType: "json",
			async: false,
			success: conDecRationaleCoverageDashboard.fillOptionsLinkTypes,
			error: conDecRationaleCoverageDashboard.processDataBad
		});
	}

	ConDecRationaleCoverageDashboard.prototype.fillOptionsLinkTypes = function fillOptionsLinkTypes(data) {
		var linkTypes = getList(JSON.stringify(data));

		var linkTypesNode = document.getElementById("condec-dashboard-rationale-coverage-linktypes-input");

		for (i = 0; i < linkTypes.length; i++) {
			var linkType = document.createElement('option');
			linkType.value = linkTypes[i];
			linkType.text = linkTypes[i];
			linkTypesNode.options.add(linkType);
		}
	};

	function getList(jsonString) {
		jsonString = jsonString.replace("\[", "").replace("\]", "");
		jsonString = jsonString.replaceAll("\"", "");

		return jsonString.split(",");
	}

	ConDecRationaleCoverageDashboard.prototype.init = function init(filterSettings, issueType) {
		getHTMLNodes("condec-rationale-coverage-dashboard-configproject"
			, "condec-rationale-coverage-dashboard-contents-container"
			, "condec-rationale-coverage-dashboard-contents-data-error"
			, "condec-rationale-coverage-dashboard-no-project"
			, "condec-rationale-coverage-dashboard-processing"
			, "condec-rationale-coverage-dashboard-nogit-error");

		getMetrics(filterSettings, issueType);
	};

	function getHTMLNodes(filterName, containerName, dataErrorName, noProjectName, processingName, noGitName) {
		dashboardFilterNode = document.getElementById(filterName);
		dashboardContentNode = document.getElementById(containerName);
		dashboardDataErrorNode = document.getElementById(dataErrorName);
		dashboardNoContentsNode = document.getElementById(noProjectName);
		dashboardProcessingNode = document.getElementById(processingName);
		dashboardProjectWithoutGit = document.getElementById(noGitName);
	}

	function showDashboardSection(node) {
		var hiddenClass = "hidden";
		dashboardFilterNode.classList.add(hiddenClass);
		dashboardContentNode.classList.add(hiddenClass);
		dashboardDataErrorNode.classList.add(hiddenClass);
		dashboardNoContentsNode.classList.add(hiddenClass);
		dashboardProcessingNode.classList.add(hiddenClass);
		dashboardProjectWithoutGit.classList.add(hiddenClass);
		node.classList.remove(hiddenClass);
	}

	function getMetrics(filterSettings, issueType) {
		if (!JSON.parse(filterSettings).projectKey || !JSON.parse(filterSettings).projectKey.length || !JSON.parse(filterSettings).projectKey.length > 0) {
			return;
		}
		if (!issueType || !issueType.length || !issueType.length > 0) {
			return;
		}

		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		showDashboardSection(dashboardProcessingNode);

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
			"", "# Issues per Jira Issue", issuesPerJiraIssue);
		ConDecReqDash.initializeChart("boxplot-DecisionsPerJiraIssue",
			"", "# Decisions per Jira Issue", decisionsPerJiraIssue);
		/* render pie-charts */
		ConDecReqDash.initializeChart("piechartRich-DecisionDocumentedForSelectedJiraIssue",
			"", "For how many selected issue type elements is an issue documented?", decisionDocumentedForSelectedJiraIssue);
		ConDecReqDash.initializeChart("piechartRich-IssueDocumentedForSelectedJiraIssue",
			"", "For how many selected issue type elements is a decision documented?", issueDocumentedForSelectedJiraIssue);
	}

	global.conDecRationaleCoverageDashboard = new ConDecRationaleCoverageDashboard();
})(window);
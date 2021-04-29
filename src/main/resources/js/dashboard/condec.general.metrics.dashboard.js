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
	var dashboardProjectWithoutGit;

	var ConDecGeneralMetricsDashboard = function() {
		console.log("ConDecGeneralMetricsDashboard constructor");
	};

	ConDecGeneralMetricsDashboard.prototype.setKnowledgeTypes = function (projectKey) {
		var KnowledgeTypeSelection = document.getElementById("knowledgetype-multi-select-general-metrics");

		removeOptions(KnowledgeTypeSelection);
		this.fillOptionsKnowledgeTypes(projectKey);
	};

	ConDecGeneralMetricsDashboard.prototype.setDocumentationLocations = function () {
		var documentationLocationSelection = document.getElementById("documentationlocation-multi-select-general-metrics");

		removeOptions(documentationLocationSelection);
		this.fillOptionsDocumentationLocations();
	};

	ConDecGeneralMetricsDashboard.prototype.setKnowledgeStatus = function () {
		var KnowledgeStatusSelection = document.getElementById("knowledgestatus-multi-select-general-metrics");

		removeOptions(KnowledgeStatusSelection);
		this.fillOptionsKnowledgeStatus();
	};

	ConDecGeneralMetricsDashboard.prototype.setLinkTypes = function () {
		var LinkTypeSelection = document.getElementById("linktype-multi-select-general-metrics");

		removeOptions(LinkTypeSelection);
		this.fillOptionsLinkTypes();
	};

	function removeOptions(selectElement) {
		var i, L = selectElement.options.length - 1;
		for(i = L; i >= 0; i--) {
			selectElement.remove(i);
		}
	}

	ConDecGeneralMetricsDashboard.prototype.fillOptionsKnowledgeTypes = function (projectKey) {
		conDecAPI.projectKey = projectKey;
		var knowledgeTypes = conDecAPI.getKnowledgeTypes();

		var knowledgeTypeNode = document.getElementById("knowledgetype-multi-select-general-metrics");

		for (i = 0; i < knowledgeTypes.length; i++) {
			var knowledgeType = document.createElement('option');
			knowledgeType.value = knowledgeTypes[i];
			knowledgeType.text = knowledgeTypes[i];
			knowledgeTypeNode.options.add(knowledgeType);
		}
	};

	ConDecGeneralMetricsDashboard.prototype.fillOptionsDocumentationLocations = function() {
		var documentationLocations = conDecAPI.documentationLocations;
		
		var documentationLocationNode = document.getElementById("documentationlocation-multi-select-general-metrics");

		for (i = 0; i < documentationLocations.length; i++) {
			var documentationLocation = document.createElement('option');
			documentationLocation.value = documentationLocations[i];
			documentationLocation.text = documentationLocations[i];
			documentationLocationNode.options.add(documentationLocation);
		}
	};

	ConDecGeneralMetricsDashboard.prototype.fillOptionsKnowledgeStatus = function () {
		var knowledgeStatuses = conDecAPI.knowledgeStatus;

		var knowledgeStatusNode = document.getElementById("knowledgestatus-multi-select-general-metrics");

		for (i = 0; i < knowledgeStatuses.length; i++) {
			var knowledgeStatus = document.createElement('option');
			knowledgeStatus.value = knowledgeStatuses[i];
			knowledgeStatus.text = knowledgeStatuses[i];
			knowledgeStatusNode.options.add(knowledgeStatus);
		}
	};

	ConDecGeneralMetricsDashboard.prototype.fillOptionsLinkTypes = function() {
		var linkTypes = conDecAPI.getLinkTypes();
		console.log(linkTypes);

		var linkTypesNode = document.getElementById("linktype-multi-select-general-metrics");

		for (i = 0; i < linkTypes.length; i++) {
			var linkType = document.createElement('option');
			linkType.value = linkTypes[i];
			linkType.text = linkTypes[i];
			linkTypesNode.options.add(linkType);
		}
	};

	ConDecGeneralMetricsDashboard.prototype.init = function init(filterSettings) {
		getHTMLNodes("condec-general-metrics-dashboard-configproject"
			, "condec-general-metrics-dashboard-contents-container"
			, "condec-general-metrics-dashboard-contents-data-error"
			, "condec-general-metrics-dashboard-no-project"
			, "condec-general-metrics-dashboard-processing"
			, "condec-general-metrics-dashboard-nogit-error");

		getMetrics(filterSettings);
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
		commentsPerIssue = getMap(JSON.stringify(json.numberOfCommentsPerJiraIssue));
		commitsPerIssue = getMap(JSON.stringify(json.numberOfCommitsPerJiraIssue));
		reqCodeSummary = getMap(JSON.stringify(json.requirementsAndCodeFiles));
		decSources = getMap(JSON.stringify(json.numberOfElementsPerDocumentationLocation));
		relevantSentences = getMap(JSON.stringify(json.numberOfRelevantComments));
		knowledgeTypeDistribution = getMap(JSON.stringify(json.distributionOfKnowledgeTypes));

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
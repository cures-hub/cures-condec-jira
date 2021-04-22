/*
 This module fills the box plots used in the code coverage dashboard item.

 Requires
 * js/condec.requirements.dashboard.js

 Is referenced in HTML by
 * codeCoverageDashboardItem.vm
 */

 (function (global) {
	 var dashboardFilterNode;
	 var dashboardContentNode;
	 var dashboardDataErrorNode;
	 var dashboardNoContentsNode;
	 var dashboardProcessingNode;
	 var dashboardProjectWithoutGit;

	var ConDecCodeCoverageDashboard = function ConDecCodeCoverageDashboard() {
		console.log("ConDecCodeCoverageDashboard constructor");
	};

	 ConDecCodeCoverageDashboard.prototype.setKnowledgeTypes = function setKnowledgeTypes(projectkey) {
	 	var KnowledgeTypeSelection = document.getElementById("knowledgetype-multi-select-code-coverage");

	 	removeOptions(KnowledgeTypeSelection);

	 	getKnowledgeTypes(projectkey);
	 };

	 ConDecCodeCoverageDashboard.prototype.setDocumentationLocations = function setDocumentationLocations() {
	 	var documentationLocationSelection = document.getElementById("documentationlocation-multi-select-code-coverage");

	 	removeOptions(documentationLocationSelection);

	 	getDocumentationLocations();
	 };

	 ConDecCodeCoverageDashboard.prototype.setKnowledgeStatus = function setKnowledgeStatus() {
	 	var KnowledgeStatusSelection = document.getElementById("knowledgestatus-multi-select-code-coverage");

	 	removeOptions(KnowledgeStatusSelection);

	 	getKnowledgeStatus();
	 };

	 ConDecCodeCoverageDashboard.prototype.setLinkTypes = function setLinkTypes() {
	 	var LinkTypeSelection = document.getElementById("linktype-multi-select-code-coverage");

	 	removeOptions(LinkTypeSelection);

	 	getLinkTypes();
	 };

	 function removeOptions(selectElement) {
	 	var i, L = selectElement.options.length - 1;
	 	for(i = L; i >= 0; i--) {
	 		selectElement.remove(i);
	 	}
	 }

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
		 	success: conDecCodeCoverageDashboard.fillOptionsKnowledgeTypes,
		 	error: conDecCodeCoverageDashboard.processDataBad
	 	});
	 }

	 ConDecCodeCoverageDashboard.prototype.fillOptionsKnowledgeTypes = function fillOptionsKnowledgeTypes(data) {
	 	var knowledgeTypes = getList(JSON.stringify(data));

	 	var knowledgeTypeNode = document.getElementById("knowledgetype-multi-select-code-coverage");

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
			 success: conDecCodeCoverageDashboard.fillOptionsDocumentationLocations,
			 error: conDecCodeCoverageDashboard.processDataBad
		 });
	 }

	 ConDecCodeCoverageDashboard.prototype.fillOptionsDocumentationLocations = function fillOptionsDocumentationLocations(data) {
		 var documentationLocations = getList(JSON.stringify(data));

		 var documentationLocationNode = document.getElementById("documentationlocation-multi-select-code-coverage");

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
			 success: conDecCodeCoverageDashboard.fillOptionsKnowledgeStatus,
			 error: conDecCodeCoverageDashboard.processDataBad
		 });
	 }

	 ConDecCodeCoverageDashboard.prototype.fillOptionsKnowledgeStatus = function fillOptionsKnowledgeStatus(data) {
		 var knowledgeStatuses = getList(JSON.stringify(data));

		 var knowledgeStatusNode = document.getElementById("knowledgestatus-multi-select-code-coverage");

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
			 success: conDecCodeCoverageDashboard.fillOptionsLinkTypes,
			 error: conDecCodeCoverageDashboard.processDataBad
		 });
	 }

	 ConDecCodeCoverageDashboard.prototype.fillOptionsLinkTypes = function fillOptionsLinkTypes(data) {
		 var linkTypes = getList(JSON.stringify(data));

		 var linkTypesNode = document.getElementById("linktype-multi-select-code-coverage");

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

	ConDecCodeCoverageDashboard.prototype.init = function init(filterSettings) {
		getHTMLNodes("condec-code-coverage-dashboard-configproject"
			, "condec-code-coverage-dashboard-contents-container"
			, "condec-code-coverage-dashboard-contents-data-error"
			, "condec-code-coverage-dashboard-no-project"
			, "condec-code-coverage-dashboard-processing"
			, "condec-code-coverage-dashboard-nogit-error");

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

		url = conDecAPI.restPrefix + "/dashboard/codeCoverage.json";

		console.log("Starting REST query.");
		AJS.$.ajax({
			url: url,
			headers: { "Content-Type": "application/json; charset=utf-8", "Accept": "application/json"},
			type: "post",
			dataType: "json",
			data: filterSettings,
			async: true,
			success: conDecCodeCoverageDashboard.processData,
			error: conDecCodeCoverageDashboard.processDataBad
		});
	}

	ConDecCodeCoverageDashboard.prototype.processDataBad = function processDataBad(data) {
		console.log(data.responseJSON.error);
		showDashboardSection(dashboardDataErrorNode);
	};

	ConDecCodeCoverageDashboard.prototype.processData = function processData(data) {
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
		var jsonstr = JSON.stringify(data);
		var json = JSON.parse(jsonstr);

		/*  init data for charts */
		var issuesPerCodeFile = new Map();
		var decisionsPerCodeFile = new Map();
		var decisionDocumentedForCodeFile = new Map();
		var issueDocumentedForCodeFile = new Map();
        
		/* set something for box plots in case no data will be added to them */
		issuesPerCodeFile.set("none", 0);
		decisionsPerCodeFile.set("none", 0);

		decisionDocumentedForCodeFile.set("no code classes", "");
		issueDocumentedForCodeFile.set("no rationale elements", "");

		/* form data for charts */
		issuesPerCodeFile = getMap(JSON.stringify(json.issuesPerCodeFile));
		decisionsPerCodeFile = getMap(JSON.stringify(json.decisionsPerCodeFile));
		decisionDocumentedForCodeFile = getMap(JSON.stringify(json.decisionDocumentedForCodeFile));
		issueDocumentedForCodeFile = getMap(JSON.stringify(json.issueDocumentedForCodeFile));

        /* render box-plots */
		ConDecReqDash.initializeChart("boxplot-IssuesPerCodeFile",
			"", "# Issues per Code File", issuesPerCodeFile);
		ConDecReqDash.initializeChart("boxplot-DecisionsPerCodeFile",
			"", "# Decisions per Code File", decisionsPerCodeFile);
		/* render pie-charts */
		ConDecReqDash.initializeChart("piechartRich-DecisionDocumentedForCodeFile",
			"", "For how many code files is an issue documented?", decisionDocumentedForCodeFile);
		ConDecReqDash.initializeChart("piechartRich-IssueDocumentedForCodeFile",
			"", "For how many code files is a decision documented?", issueDocumentedForCodeFile);
	}

	global.conDecCodeCoverageDashboard = new ConDecCodeCoverageDashboard();
})(window);
/*
 This module fills the box plots and pie charts used in the rationale completeness dashboard item.

 Requires
 * js/condec.requirements.dashboard.js

 Is referenced in HTML by
 * rationaleCompletenessDashboardItem.vm
 */

(function (global) {
	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var ConDecRationaleCompletenessDashboard = function ConDecRationaleCompletenessDashboard() {
		console.log("ConDecRationaleCompletenessDashboard constructor");
	};

	ConDecRationaleCompletenessDashboard.prototype.setKnowledgeTypes = function setKnowledgeTypes(projectkey) {
		var KnowledgeTypeSelection = document.getElementById("knowledgetype-multi-select-rationale-completeness");

		removeOptions(KnowledgeTypeSelection);

		getKnowledgeTypes(projectkey);
	};

	ConDecRationaleCompletenessDashboard.prototype.setDocumentationLocations = function setDocumentationLocations() {
		var documentationLocationSelection = document.getElementById("documentationlocation-multi-select-rationale-completeness");

		removeOptions(documentationLocationSelection);

		getDocumentationLocations();
	};

	ConDecRationaleCompletenessDashboard.prototype.setKnowledgeStatus = function setKnowledgeStatus() {
		var KnowledgeStatusSelection = document.getElementById("knowledgestatus-multi-select-rationale-completeness");

		removeOptions(KnowledgeStatusSelection);

		getKnowledgeStatus();
	};

	ConDecRationaleCompletenessDashboard.prototype.setLinkTypes = function setLinkTypes() {
		var LinkTypeSelection = document.getElementById("linktype-multi-select-rationale-completeness");

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
			success: conDecRationaleCompletenessDashboard.fillOptionsKnowledgeTypes,
			error: conDecRationaleCompletenessDashboard.processDataBad
		});
	}

	ConDecRationaleCompletenessDashboard.prototype.fillOptionsKnowledgeTypes = function fillOptionsKnowledgeTypes(data) {
		var knowledgeTypes = getList(JSON.stringify(data));

		var knowledgeTypeNode = document.getElementById("knowledgetype-multi-select-rationale-completeness");

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
			success: conDecRationaleCompletenessDashboard.fillOptionsDocumentationLocations,
			error: conDecRationaleCompletenessDashboard.processDataBad
		});
	}

	ConDecRationaleCompletenessDashboard.prototype.fillOptionsDocumentationLocations = function fillOptionsDocumentationLocations(data) {
		var documentationLocations = getList(JSON.stringify(data));

		var documentationLocationNode = document.getElementById("documentationlocation-multi-select-rationale-completeness");

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
			success: conDecRationaleCompletenessDashboard.fillOptionsKnowledgeStatus,
			error: conDecRationaleCompletenessDashboard.processDataBad
		});
	}

	ConDecRationaleCompletenessDashboard.prototype.fillOptionsKnowledgeStatus = function fillOptionsKnowledgeStatus(data) {
		var knowledgeStatuses = getList(JSON.stringify(data));

		var knowledgeStatusNode = document.getElementById("knowledgestatus-multi-select-rationale-completeness");

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
			success: conDecRationaleCompletenessDashboard.fillOptionsLinkTypes,
			error: conDecRationaleCompletenessDashboard.processDataBad
		});
	}

	ConDecRationaleCompletenessDashboard.prototype.fillOptionsLinkTypes = function fillOptionsLinkTypes(data) {
		var linkTypes = getList(JSON.stringify(data));

		var linkTypesNode = document.getElementById("linktype-multi-select-rationale-completeness");

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

	ConDecRationaleCompletenessDashboard.prototype.init = function init(filterSettings) {
		getHTMLNodes("condec-rationale-completeness-dashboard-configproject"
			, "condec-rationale-completeness-dashboard-contents-container"
			, "condec-rationale-completeness-dashboard-contents-data-error"
			, "condec-rationale-completeness-dashboard-no-project"
			, "condec-rationale-completeness-dashboard-processing"
			, "condec-rationale-completeness-dashboard-nogit-error");

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

		url = conDecAPI.restPrefix + "/dashboard/rationaleCompleteness.json";

		console.log("Starting REST query.");
		AJS.$.ajax({
			url: url,
			headers: { "Content-Type": "application/json; charset=utf-8", "Accept": "application/json"},
			type: "post",
			dataType: "json",
			data: filterSettings,
			async: true,
			success: conDecRationaleCompletenessDashboard.processData,
			error: conDecRationaleCompletenessDashboard.processDataBad
		});
	}

	ConDecRationaleCompletenessDashboard.prototype.processDataBad = function processDataBad(data) {
		console.log(data.responseJSON.error);
		showDashboardSection(dashboardDataErrorNode);
	};

	ConDecRationaleCompletenessDashboard.prototype.processData = function processData(data) {
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
		var issuesSolvedByDecision = new Map();
		var decisionsSolvingIssues = new Map();
		var proArgumentDocumentedForDecision = new Map();
		var conArgumentDocumentedForAlternative = new Map();
		var conArgumentDocumentedForDecision = new Map();
		var proArgumentDocumentedForAlternative = new Map();

		/* set something in case no data will be added to them */
		issuesSolvedByDecision.set("none", "");
		decisionsSolvingIssues.set("none", "");
		proArgumentDocumentedForDecision.set("none", "");
		conArgumentDocumentedForAlternative.set("none", "");
		conArgumentDocumentedForDecision.set("none", "");
		proArgumentDocumentedForAlternative.set("none", "");

		/* form data for charts */
		issuesSolvedByDecision = getMap(JSON.stringify(json.issuesSolvedByDecision));
		decisionsSolvingIssues = getMap(JSON.stringify(json.decisionsSolvingIssues));
		proArgumentDocumentedForDecision = getMap(JSON.stringify(json.proArgumentDocumentedForDecision));
		conArgumentDocumentedForAlternative = getMap(JSON.stringify(json.conArgumentDocumentedForAlternative));
		conArgumentDocumentedForDecision = getMap(JSON.stringify(json.conArgumentDocumentedForDecision));
		proArgumentDocumentedForAlternative = getMap(JSON.stringify(json.proArgumentDocumentedForAlternative));

		/* render pie-charts */
		ConDecReqDash.initializeChart("piechartRich-IssuesSolvedByDecision",
			"", "How many issues (=decision problems) are solved by a decision?", issuesSolvedByDecision);
		ConDecReqDash.initializeChart("piechartRich-DecisionsSolvingIssues",
			"", "For how many decisions is the issue (=decision problem) documented?", decisionsSolvingIssues);
		ConDecReqDash.initializeChart("piechartRich-ProArgumentDocumentedForDecision",
			"", "How many decisions have at least one pro argument documented?", proArgumentDocumentedForDecision);
		ConDecReqDash.initializeChart("piechartRich-ConArgumentDocumentedForDecision",
			"", "How many decisions have at least one con argument documented?", conArgumentDocumentedForDecision);
		ConDecReqDash.initializeChart("piechartRich-ProArgumentDocumentedForAlternative",
			"", "How many alternatives have at least one pro argument documented?", proArgumentDocumentedForAlternative);
		ConDecReqDash.initializeChart("piechartRich-ConArgumentDocumentedForAlternative",
			"", "How many alternatives have at least one con argument documented?", conArgumentDocumentedForAlternative);
	}

	global.conDecRationaleCompletenessDashboard = new ConDecRationaleCompletenessDashboard();
})(window);
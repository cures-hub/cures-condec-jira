/*
 This module fills the box plots and pie charts used in the general metrics dashboard item.

 Requires
 * js/condec.requirements.dashboard.js

 Is referenced in HTML by
 * generalMetricsDashboardItem.vm
 */

(function (global) {
	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var ConDecGeneralMetricsDashboard = function ConDecGeneralMetricsDashboard() {
		console.log("ConDecGeneralMetricsDashboard constructor");
	};

	ConDecGeneralMetricsDashboard.prototype.setKnowledgeTypes = function setKnowledgeTypes(projectkey) {
		var KnowledgeTypeSelection = document.getElementById("condec-dashboard-general-metrics-knowledgetypes-input");

		removeOptions(KnowledgeTypeSelection);

		getKnowledgeTypes(projectkey);
	};

	ConDecGeneralMetricsDashboard.prototype.setDocumentationLocations = function setDocumentationLocations() {
		var documentationLocationSelection = document.getElementById("condec-dashboard-general-metrics-documentationlocation-input");

		removeOptions(documentationLocationSelection);

		getDocumentationLocations();
	};

	ConDecGeneralMetricsDashboard.prototype.setKnowledgeStatus = function setKnowledgeStatus() {
		var KnowledgeStatusSelection = document.getElementById("condec-dashboard-general-metrics-knowledgestatus-input");

		removeOptions(KnowledgeStatusSelection);

		getKnowledgeStatus();
	};

	ConDecGeneralMetricsDashboard.prototype.setLinkTypes = function setLinkTypes() {
		var LinkTypeSelection = document.getElementById("condec-dashboard-general-metrics-linktypes-input");

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
			success: conDecGeneralMetricsDashboard.fillOptionsKnowledgeTypes,
			error: conDecGeneralMetricsDashboard.processDataBad
		});
	}

	ConDecGeneralMetricsDashboard.prototype.fillOptionsKnowledgeTypes = function fillOptionsKnowledgeTypes(data) {
		var knowledgeTypes = getList(JSON.stringify(data));

		var knowledgeTypeNode = document.getElementById("condec-dashboard-general-metrics-knowledgetypes-input");

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
			success: conDecGeneralMetricsDashboard.fillOptionsDocumentationLocations,
			error: conDecGeneralMetricsDashboard.processDataBad
		});
	}

	ConDecGeneralMetricsDashboard.prototype.fillOptionsDocumentationLocations = function fillOptionsDocumentationLocations(data) {
		var documentationLocations = getList(JSON.stringify(data));

		var documentationLocationNode = document.getElementById("condec-dashboard-general-metrics-documentationlocation-input");

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
			success: conDecGeneralMetricsDashboard.fillOptionsKnowledgeStatus,
			error: conDecGeneralMetricsDashboard.processDataBad
		});
	}

	ConDecGeneralMetricsDashboard.prototype.fillOptionsKnowledgeStatus = function fillOptionsKnowledgeStatus(data) {
		var knowledgeStatuses = getList(JSON.stringify(data));

		var knowledgeStatusNode = document.getElementById("condec-dashboard-general-metrics-knowledgestatus-input");

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
			success: conDecGeneralMetricsDashboard.fillOptionsLinkTypes,
			error: conDecGeneralMetricsDashboard.processDataBad
		});
	}

	ConDecGeneralMetricsDashboard.prototype.fillOptionsLinkTypes = function fillOptionsLinkTypes(data) {
		var linkTypes = getList(JSON.stringify(data));

		var linkTypesNode = document.getElementById("condec-dashboard-general-metrics-linktypes-input");

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

define('dashboard/generalMetrics', [], function () {
	var dashboardAPI;

	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var ConDecGeneralMetricsDashboardItem = function (API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecGeneralMetricsDashboardItem.prototype.render = function (context, preferences) {
		$(document).ready(function() {
			var projectKey = preferences['projectKey'];
			var knowledgeTypes = preferences['knowledgeTypes'];
			var documentationLocations = preferences['documentationLocations'];
			var knowledgeStatus = preferences['knowledgeStatus'];
			var linkTypes = preferences['linkTypes'];
			var linkDistance = preferences['linkDistance'];
			var minDegree = preferences['minDegree'];
			var maxDegree = preferences['maxDegree'];
			var startDate = preferences['startDate'];
			var endDate = preferences['endDate'];
			var decisionKnowledgeShown = preferences['decisionKnowledgeShown'];
			var testCodeShown = preferences['testCodeShown'];
			var incompleteKnowledgeShown = preferences['incompleteKnowledgeShown'];

			var filterSettings = getFilterSettings(projectKey, knowledgeTypes, documentationLocations, knowledgeStatus, linkTypes,
				linkDistance, minDegree, maxDegree, startDate, endDate,
				decisionKnowledgeShown, testCodeShown, incompleteKnowledgeShown);

			conDecGeneralMetricsDashboard.init(filterSettings);
			dashboardAPI.resize();
		});
	};

	ConDecGeneralMetricsDashboardItem.prototype.renderEdit = function (context, preferences) {
		$(document).ready(function() {
			getHTMLNodes("condec-general-metrics-dashboard-configproject"
				, "condec-general-metrics-dashboard-contents-container"
				, "condec-general-metrics-dashboard-contents-data-error"
				, "condec-general-metrics-dashboard-no-project"
				, "condec-general-metrics-dashboard-processing"
				, "condec-general-metrics-dashboard-nogit-error");

			showDashboardSection(dashboardFilterNode);

			setPreferences(preferences);

			dashboardAPI.resize();

			function onSaveButton(event) {
				var preferences = getPreferences();

				var projectKey = preferences['projectKey'];
				var knowledgeTypes = preferences['knowledgeTypes'];
				var documentationLocations = preferences['documentationLocations'];
				var knowledgeStatus = preferences['knowledgeStatus'];
				var linkTypes = preferences['linkTypes'];
				var linkDistance = preferences['linkDistance'];
				var minDegree = preferences['minDegree'];
				var maxDegree = preferences['maxDegree'];
				var startDate = preferences['startDate'];
				var endDate = preferences['endDate'];
				var decisionKnowledgeShown = preferences['decisionKnowledgeShown'];
				var testCodeShown = preferences['testCodeShown'];
				var incompleteKnowledgeShown = preferences['incompleteKnowledgeShown'];

				var filterSettings = getFilterSettings(projectKey, knowledgeTypes, documentationLocations, knowledgeStatus, linkTypes,
					linkDistance, minDegree, maxDegree, startDate, endDate,
					decisionKnowledgeShown, testCodeShown, incompleteKnowledgeShown);

				if (projectKey) {
					dashboardAPI.savePreferences(preferences);
					conDecGeneralMetricsDashboard.init(filterSettings);
				}

				dashboardAPI.resize();
			}

			function onCancelButton(event) {
				dashboardAPI.closeEdit();
				dashboardAPI.resize();
			}

			function onSelectProject(event) {
				conDecGeneralMetricsDashboard.setKnowledgeTypes(preferences['projectKey']);
				conDecGeneralMetricsDashboard.setKnowledgeTypes(preferences['projectKey']);
				conDecGeneralMetricsDashboard.setDocumentationLocations();
				conDecGeneralMetricsDashboard.setKnowledgeStatus();
				conDecGeneralMetricsDashboard.setLinkTypes();
			}

			saveButton = document.getElementById("general-metrics-save-button");
			saveButton.addEventListener("click", onSaveButton);

			cancelButton = document.getElementById("general-metrics-cancel-button");
			cancelButton.addEventListener("click", onCancelButton);

			projectKeyNode = document.getElementById("condec-dashboard-general-metrics-project-selection");
			projectKeyNode.addEventListener("change", onSelectProject);
		});
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

	function getPreferences() {
		var preferences = {};

		var projectNode = document.getElementById("condec-dashboard-general-metrics-project-selection");
		preferences['projectKey'] = projectNode.value;

		var knowledgeTypesNode = document.getElementById("condec-dashboard-general-metrics-knowledgetypes-input");
		preferences['knowledgeTypes'] = getSelectValues(knowledgeTypesNode);

		var documentationLocationsNode = document.getElementById("condec-dashboard-general-metrics-documentationlocation-input");
		preferences['documentationLocations'] = getSelectValues(documentationLocationsNode);

		var knowledgeStatusNode = document.getElementById("condec-dashboard-general-metrics-knowledgestatus-input");
		preferences['knowledgeStatus'] = getSelectValues(knowledgeStatusNode);

		var linkTypesNode = document.getElementById("condec-dashboard-general-metrics-linktypes-input");
		preferences['linkTypes'] = getSelectValues(linkTypesNode);

		var linkDistanceNode = document.getElementById("condec-dashboard-general-metrics-linkdistance-input");
		preferences['linkDistance'] = linkDistanceNode.value;

		var minDegreeNode = document.getElementById("condec-dashboard-general-metrics-mindegree-input");
		preferences['minDegree'] = minDegreeNode.value;

		var maxDegreeNode = document.getElementById("condec-dashboard-general-metrics-maxdegree-input");
		preferences['maxDegree'] = maxDegreeNode.value;

		var startDateNode = document.getElementById("condec-dashboard-general-metrics-startdate-input");
		preferences['startDate'] = startDateNode.value;

		var endDateNode = document.getElementById("condec-dashboard-general-metrics-enddate-input");
		preferences['endDate'] = endDateNode.value;

		var decisionKnowledgeNode = document.getElementById("condec-dashboard-general-metrics-decisionknowledge-checkbox");
		preferences['decisionKnowledgeShown'] = decisionKnowledgeNode.checked;

		var testCodeNode = document.getElementById("condec-dashboard-general-metrics-testcode-checkbox");
		preferences['testCodeShown'] = testCodeNode.checked;

		var incompleteKnowledgeNode = document.getElementById("condec-dashboard-general-metrics-incompleteknowledge-checkbox");
		preferences['incompleteKnowledgeShown'] = incompleteKnowledgeNode.checked;

		return preferences;
	}

	function setPreferences(preferences) {
		if (preferences['projectKey']) {
			var projectNode = document.getElementById("condec-dashboard-general-metrics-project-selection");
			projectNode.value = preferences['projectKey'];
		}

		conDecGeneralMetricsDashboard.setKnowledgeTypes(preferences['projectKey']);

		if (preferences['knowledgeTypes']) {
			var KnowledgeTypesNode = document.getElementById("condec-dashboard-general-metrics-knowledgetypes-input");
			setSelectValues(KnowledgeTypesNode, preferences['knowledgeTypes']);
		}

		conDecGeneralMetricsDashboard.setDocumentationLocations();

		if (preferences['documentationLocations']) {
			var documentationLocationsNode = document.getElementById("condec-dashboard-general-metrics-documentationlocation-input");
			setSelectValues(documentationLocationsNode, preferences['documentationLocations']);
		}

		conDecGeneralMetricsDashboard.setKnowledgeStatus();

		if (preferences['knowledgeStatus']) {
			var knowledgeStatusNode = document.getElementById("condec-dashboard-general-metrics-knowledgestatus-input");
			setSelectValues(knowledgeStatusNode, preferences['knowledgeStatus']);
		}

		conDecGeneralMetricsDashboard.setLinkTypes();

		if (preferences['linkTypes']) {
			var linkTypesNode = document.getElementById("condec-dashboard-general-metrics-linktypes-input");
			setSelectValues(linkTypesNode, preferences['linkTypes']);
		}

		if (preferences['linkDistance']) {
			var linkDistanceNode = document.getElementById("condec-dashboard-general-metrics-linkdistance-input");
			linkDistanceNode.value = preferences['linkDistance'];
		}

		if (preferences['minDegree']) {
			var minDegreeNode = document.getElementById("condec-dashboard-general-metrics-mindegree-input");
			minDegreeNode.value = preferences['minDegree'];
		}

		if (preferences['maxDegree']) {
			var maxDegreeNode = document.getElementById("condec-dashboard-general-metrics-maxdegree-input");
			maxDegreeNode.value = preferences['maxDegree'];
		}

		if (preferences['startDate']) {
			var startDateNode = document.getElementById("condec-dashboard-general-metrics-startdate-input");
			startDateNode.value = preferences['startDate'];
		}

		if (preferences['endDate']) {
			var endDateNode = document.getElementById("condec-dashboard-general-metrics-enddate-input");
			endDateNode.value = preferences['endDate'];
		}

		if (preferences['decisionKnowledgeShown']) {
			var decisionKnowledgeNode = document.getElementById("condec-dashboard-general-metrics-decisionknowledge-checkbox");
			decisionKnowledgeNode.checked = preferences['decisionKnowledgeShown'];
		}

		if (preferences['testCodeShown']) {
			var testCodeNode = document.getElementById("condec-dashboard-general-metrics-testcode-checkbox");
			testCodeNode.checked = preferences['testCodeShown'];
		}

		if (preferences['incompleteKnowledgeShown']) {
			var incompleteKnowledgeNode = document.getElementById("condec-dashboard-general-metrics-incompleteknowledge-checkbox");
			incompleteKnowledgeNode.checked = preferences['incompleteKnowledgeShown'];
		}
	}

	function getFilterSettings(projectKey, knowledgeTypes, documentationLocations, knowledgeStatus, linkTypes,
							   linkDistance, minDegree, maxDegree, startDate, endDate,
							   decisionKnowledgeShown, testCodeShown, incompleteKnowledgeShown) {
		var filterSettings = {};

		filterSettings.projectKey = projectKey;
		filterSettings.searchTerm = "";

		var knowledgeTypesList = getList(knowledgeTypes)
		if (Array.isArray(knowledgeTypesList) && knowledgeTypesList.length) {
			filterSettings.knowledgeTypes = knowledgeTypesList;
		}

		var documentationLocationsList = getList(documentationLocations);
		if (Array.isArray(documentationLocationsList) && documentationLocationsList.length) {
			filterSettings.documentationLocations = documentationLocationsList;
		}

		var knowledgeStatusList = getList(knowledgeStatus);
		if (Array.isArray(knowledgeStatusList) && knowledgeStatusList.length) {
			filterSettings.status = knowledgeStatusList;
		}

		var linkTypesList = getList(linkTypes);
		if (Array.isArray(linkTypesList) && linkTypesList.length) {
			filterSettings.linkTypes = linkTypesList;
		}

		if (Number.isInteger(linkDistance)) {
			filterSettings.linkDistance = linkDistance;
		}
		if (Number.isInteger(minDegree)) {
			filterSettings.minDegree = minDegree;
		}
		if (Number.isInteger(maxDegree)) {
			filterSettings.maxDegree = maxDegree;
		}

		if (startDate) {
			filterSettings.startDate = new Date(startDate).getTime();
		}
		if (endDate) {
			filterSettings.endDate = new Date(endDate).getTime();
		}

		filterSettings.isOnlyDecisionKnowledgeShown = decisionKnowledgeShown;
		filterSettings.isTestCodeShown = testCodeShown;
		filterSettings.isIncompleteKnowledgeShown = incompleteKnowledgeShown;

		return JSON.stringify(filterSettings);
	}

	function getSelectValues(select) {
		var result = [];
		var options = select.options;

		for (var i=0; i<options.length; i++) {
			if (options[i].selected) {
				result.push(options[i].value);
			}
		}

		return result;
	}

	function setSelectValues(select, list) {
		var options = select.options;
		var values = list.split(",");

		for (var i=0; i<options.length; i++) {
			options[i].selected = false;
			for (var j=0; j<values.length; j++) {
				if (options[i].value === values[j]) {
					options[i].selected = true;
				}
			}
		}
	}

	function getList(jsonString) {
		if (jsonString === "") {
			return null;
		}

		if (Array.isArray(jsonString)) {
			return jsonString;
		}

		jsonString = jsonString.replace("\[", "").replace("\]", "");
		jsonString = jsonString.replaceAll("\"", "");

		return jsonString.split(",");
	}

	return ConDecGeneralMetricsDashboardItem;
});
/*
 This module render the configuration screen used in the rationale coverage dashboard item.

 Requires
 * condec.requirements.dashboard.js
 * condec.rationale.coverage.dashboard.js

 Is referenced in HTML by
 * dashboard/rationaleCoverage.vm
 */
define('dashboard/rationaleCoverage', [], function () {
	var dashboardAPI;

	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var ConDecRationaleCoverageDashboardItem = function (API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCoverageDashboardItem.prototype.render = function (context, preferences) {
		dashboardAPI.once("afterRender",
			function() {
				if (preferences['projectKey'] && preferences['issueType']) {
					createRender(preferences);
				}
			});
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCoverageDashboardItem.prototype.renderEdit = function (context, preferences) {
		dashboardAPI.once("afterRender",
			function() {
				createConfiguration(preferences);
			});
	};

	function createRender(preferences) {
		var projectKey = preferences['projectKey'];
		var issueType = preferences['issueType'];
		var knowledgeTypes;
		if (preferences['knowledgeTypes']) {
			knowledgeTypes = preferences['knowledgeTypes'];
		}
		var documentationLocations;
		if (preferences['documentationLocations']) {
			documentationLocations = preferences['documentationLocations'];
		}
		var knowledgeStatus;
		if (preferences['knowledgeStatus']) {
			knowledgeStatus = preferences['knowledgeStatus'];
		}
		var linkTypes;
		if (preferences['linkTypes']) {
			linkTypes = preferences['linkTypes'];
		}
		var linkDistance;
		if (preferences['linkDistance']) {
			linkDistance = preferences['linkDistance'];
		}
		var minimumDecisionCoverage;
		if (preferences['minimumDecisionCoverage']) {
			minimumDecisionCoverage = preferences['minimumDecisionCoverage'];
		}
		var minDegree;
		if (preferences['minDegree']) {
			minDegree = preferences['minDegree'];
		}
		var maxDegree;
		if (preferences['maxDegree']) {
			maxDegree = preferences['maxDegree'];
		}
		var startDate;
		if (preferences['startDate']) {
			startDate = preferences['startDate'];
		}
		var endDate;
		if (preferences['endDate']) {
			endDate = preferences['endDate'];
		}
		var decisionKnowledgeShown;
		if (preferences['decisionKnowledgeShown']) {
			decisionKnowledgeShown = preferences['decisionKnowledgeShown'];
		}
		var testCodeShown;
		if (preferences['testCodeShown']) {
			testCodeShown = preferences['testCodeShown'];
		}
		var incompleteKnowledgeShown;
		if (preferences['incompleteKnowledgeShown']) {
			incompleteKnowledgeShown = preferences['incompleteKnowledgeShown'];
		}
		var transitiveLinksShown;
		if (preferences['transitiveLinksShown']) {
			transitiveLinksShown = preferences['transitiveLinksShown'];
		}

		var filterSettings = getFilterSettings(projectKey, knowledgeTypes, documentationLocations, knowledgeStatus, linkTypes,
			linkDistance, minimumDecisionCoverage, minDegree, maxDegree, startDate, endDate,
			decisionKnowledgeShown, testCodeShown, incompleteKnowledgeShown, transitiveLinksShown);

		conDecRationaleCoverageDashboard.init(filterSettings, issueType);

		dashboardAPI.resize();
	}

	function createConfiguration(preferences) {
		getHTMLNodes("condec-rationale-coverage-dashboard-configproject"
			, "condec-rationale-coverage-dashboard-contents-container"
			, "condec-rationale-coverage-dashboard-contents-data-error"
			, "condec-rationale-coverage-dashboard-no-project"
			, "condec-rationale-coverage-dashboard-processing"
			, "condec-rationale-coverage-dashboard-nogit-error");

		showDashboardSection(dashboardFilterNode);

		setPreferences(preferences);

		createSaveButton();

		createCancelButton(preferences);

		createListener();

		dashboardAPI.resize();
	}

	function createSaveButton() {
		function onSaveButton(event) {
			var preferences = getPreferences();

			if (preferences['projectKey']) {
				dashboardAPI.savePreferences(preferences);
			}

			dashboardAPI.resize();
		}

		var saveButton = document.getElementById("save-button-rationale-coverage");
		saveButton.addEventListener("click", onSaveButton);
	}

	function createCancelButton(preferences) {
		function onCancelButton(event) {
			if (preferences['projectKey'] && preferences['issueType']) {
				dashboardAPI.closeEdit();
			}
		}

		var cancelButton = document.getElementById("cancel-button-rationale-coverage");
		cancelButton.addEventListener("click", onCancelButton);
	}

	function createListener() {
		function onSelectProject(event) {
			var projectNode = document.getElementById("project-dropdown-rationale-coverage");
			conDecRationaleCoverageDashboard.setJiraIssueTypes(projectNode.value);
			conDecRationaleCoverageDashboard.setKnowledgeTypes(projectNode.value);
			conDecRationaleCoverageDashboard.setDocumentationLocations();
			conDecRationaleCoverageDashboard.setKnowledgeStatus();
			conDecRationaleCoverageDashboard.setLinkTypes();
		}

		var projectKeyNode = document.getElementById("project-dropdown-rationale-coverage");
		projectKeyNode.addEventListener("change", onSelectProject);
	}

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

		var projectNode = document.getElementById("project-dropdown-rationale-coverage");
		preferences['projectKey'] = projectNode.value;

		var issueTypeNode = document.getElementById("issuetype-select-rationale-coverage");
		preferences['issueType'] = issueTypeNode.value;

		var knowledgeTypesNode = document.getElementById("knowledgetype-multi-select-rationale-coverage");
		preferences['knowledgeTypes'] = getSelectValues(knowledgeTypesNode);

		var documentationLocationsNode = document.getElementById("documentationlocation-multi-select-rationale-coverage");
		preferences['documentationLocations'] = getSelectValues(documentationLocationsNode);

		var knowledgeStatusNode = document.getElementById("knowledgestatus-multi-select-rationale-coverage");
		preferences['knowledgeStatus'] = getSelectValues(knowledgeStatusNode);

		var linkTypesNode = document.getElementById("linktype-multi-select-rationale-coverage");
		preferences['linkTypes'] = getSelectValues(linkTypesNode);

		var linkDistanceNode = document.getElementById("link-distance-input-rationale-coverage");
		preferences['linkDistance'] = linkDistanceNode.value;

		var minimumDecisionCoverageNode = document.getElementById("minimum-decision-coverage-input-rationale-coverage");
		preferences['minimumDecisionCoverage'] = minimumDecisionCoverageNode.value;

		var minDegreeNode = document.getElementById("min-degree-input-rationale-coverage");
		preferences['minDegree'] = minDegreeNode.value;

		var maxDegreeNode = document.getElementById("max-degree-input-rationale-coverage");
		preferences['maxDegree'] = maxDegreeNode.value;

		var startDateNode = document.getElementById("start-date-picker-rationale-coverage");
		preferences['startDate'] = startDateNode.value;

		var endDateNode = document.getElementById("end-date-picker-rationale-coverage");
		preferences['endDate'] = endDateNode.value;

		var decisionKnowledgeNode = document.getElementById("dashboard-checkbox-decisionknowledge-rationale-coverage");
		preferences['decisionKnowledgeShown'] = decisionKnowledgeNode.checked;

		var testCodeNode = document.getElementById("dashboard-checkbox-testcode-rationale-coverage");
		preferences['testCodeShown'] = testCodeNode.checked;

		var incompleteKnowledgeNode = document.getElementById("dashboard-checkbox-incompleteknowledge-rationale-coverage");
		preferences['incompleteKnowledgeShown'] = incompleteKnowledgeNode.checked;

		var transitiveLinksNode = document.getElementById("dashboard-checkbox-transitivelinks-rationale-coverage");
		preferences['transitiveLinksShown'] = transitiveLinksNode.checked;

		return preferences;
	}

	function setPreferences(preferences) {
		if (preferences['projectKey']) {
			var projectNode = document.getElementById("project-dropdown-rationale-coverage");
			projectNode.value = preferences['projectKey'];

			conDecRationaleCoverageDashboard.setJiraIssueTypes(preferences['projectKey']);
			conDecRationaleCoverageDashboard.setKnowledgeTypes(preferences['projectKey']);
		}

		if (preferences['issueType']) {
			var issueTypeNode = document.getElementById("issuetype-select-rationale-coverage");
			issueTypeNode.value = preferences['issueType'];
		}

		if (preferences['knowledgeTypes']) {
			var KnowledgeTypesNode = document.getElementById("knowledgetype-multi-select-rationale-coverage");
			setSelectValues(KnowledgeTypesNode, preferences['knowledgeTypes']);
		}

		conDecRationaleCoverageDashboard.setDocumentationLocations();

		if (preferences['documentationLocations']) {
			var documentationLocationsNode = document.getElementById("documentationlocation-multi-select-rationale-coverage");
			setSelectValues(documentationLocationsNode, preferences['documentationLocations']);
		}

		conDecRationaleCoverageDashboard.setKnowledgeStatus();

		if (preferences['knowledgeStatus']) {
			var knowledgeStatusNode = document.getElementById("knowledgestatus-multi-select-rationale-coverage");
			setSelectValues(knowledgeStatusNode, preferences['knowledgeStatus']);
		}

		conDecRationaleCoverageDashboard.setLinkTypes();

		if (preferences['linkTypes']) {
			var linkTypesNode = document.getElementById("linktype-multi-select-rationale-coverage");
			setSelectValues(linkTypesNode, preferences['linkTypes']);
		}

		if (preferences['linkDistance']) {
			var linkDistanceNode = document.getElementById("link-distance-input-rationale-coverage");
			linkDistanceNode.value = preferences['linkDistance'];
		}

		if (preferences['minimumDecisionCoverage']) {
			var minimumDecisionCoverageNode = document.getElementById("minimum-decision-coverage-input-rationale-coverage");
			minimumDecisionCoverageNode.value = preferences['minimumDecisionCoverage'];
		}

		if (preferences['minDegree']) {
			var minDegreeNode = document.getElementById("min-degree-input-rationale-coverage");
			minDegreeNode.value = preferences['minDegree'];
		}

		if (preferences['maxDegree']) {
			var maxDegreeNode = document.getElementById("max-degree-input-rationale-coverage");
			maxDegreeNode.value = preferences['maxDegree'];
		}

		if (preferences['startDate']) {
			var startDateNode = document.getElementById("start-date-picker-rationale-coverage");
			startDateNode.value = preferences['startDate'];
		}

		if (preferences['endDate']) {
			var endDateNode = document.getElementById("end-date-picker-rationale-coverage");
			endDateNode.value = preferences['endDate'];
		}

		if (preferences['decisionKnowledgeShown']) {
			var decisionKnowledgeNode = document.getElementById("dashboard-checkbox-decisionknowledge-rationale-coverage");
			decisionKnowledgeNode.checked = preferences['decisionKnowledgeShown'];
		}

		if (preferences['testCodeShown']) {
			var testCodeNode = document.getElementById("dashboard-checkbox-testcode-rationale-coverage");
			testCodeNode.checked = preferences['testCodeShown'];
		}

		if (preferences['incompleteKnowledgeShown']) {
			var incompleteKnowledgeNode = document.getElementById("dashboard-checkbox-incompleteknowledge-rationale-coverage");
			incompleteKnowledgeNode.checked = preferences['incompleteKnowledgeShown'];
		}

		if (preferences['transitiveLinksShown']) {
			var transitiveLinksNode = document.getElementById("dashboard-checkbox-transitivelinks-rationale-coverage");
			transitiveLinksNode.checked = preferences['transitiveLinksShown'];
		}
	}

	function getFilterSettings(projectKey, knowledgeTypes, documentationLocations, knowledgeStatus, linkTypes,
							   linkDistance, minimumDecisionCoverage, minDegree, maxDegree, startDate, endDate,
							   decisionKnowledgeShown, testCodeShown, incompleteKnowledgeShown, transitiveLinksShown) {
		var filterSettings = {};

		filterSettings.projectKey = projectKey;
		filterSettings.searchTerm = "";

		var knowledgeTypesList = getList(knowledgeTypes);
		if (knowledgeTypesList && Array.isArray(knowledgeTypesList) && knowledgeTypesList.length) {
			filterSettings.knowledgeTypes = knowledgeTypesList;
		}

		var documentationLocationsList = getList(documentationLocations);
		if (documentationLocationsList && Array.isArray(documentationLocationsList) && documentationLocationsList.length) {
			filterSettings.documentationLocations = documentationLocationsList;
		}

		var knowledgeStatusList = getList(knowledgeStatus);
		if (knowledgeStatusList && Array.isArray(knowledgeStatusList) && knowledgeStatusList.length) {
			filterSettings.status = knowledgeStatusList;
		}

		var linkTypesList = getList(linkTypes);
		if (linkTypesList && Array.isArray(linkTypesList) && linkTypesList.length) {
			filterSettings.linkTypes = linkTypesList;
		}

		if (linkDistance && Number.isInteger(linkDistance)) {
			filterSettings.linkDistance = linkDistance;
		}
		if (minimumDecisionCoverage && Number.isInteger(minimumDecisionCoverage)) {
			filterSettings.minimumDecisionCoverage = minimumDecisionCoverage;
		}
		if (minDegree && Number.isInteger(minDegree)) {
			filterSettings.minDegree = minDegree;
		}
		if (maxDegree && Number.isInteger(maxDegree)) {
			filterSettings.maxDegree = maxDegree;
		}

		if (startDate) {
			filterSettings.startDate = new Date(startDate).getTime();
		}
		if (endDate) {
			filterSettings.endDate = new Date(endDate).getTime();
		}

		if (decisionKnowledgeShown) {
			filterSettings.isOnlyDecisionKnowledgeShown = decisionKnowledgeShown;
		}
		if (testCodeShown) {
			filterSettings.isTestCodeShown = testCodeShown;
		}
		if (incompleteKnowledgeShown) {
			filterSettings.isIncompleteKnowledgeShown = incompleteKnowledgeShown;
		}
		if (transitiveLinksShown) {
			filterSettings.createTransitiveLinks = transitiveLinksShown;
		}

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
		if (!jsonString || (jsonString === "")) {
			return null;
		}

		if (Array.isArray(jsonString)) {
			return jsonString;
		}

		jsonString = jsonString.replace("\[", "").replace("\]", "");
		jsonString = jsonString.replaceAll("\"", "");

		return jsonString.split(",");
	}

	return ConDecRationaleCoverageDashboardItem;
});
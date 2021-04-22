/*
 This module render the configuration screen used in the code coverage dashboard item.

 Requires
 * js/condec.requirements.dashboard.js
 * js/condec.code.coverage.dashboard.js

 Is referenced in HTML by
 * codeCoverageDashboardItem.vm
 */
define('dashboard/codeCoverage', [], function () {
	var dashboardAPI;

	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var ConDecCodeCoverageDashboardItem = function (API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecCodeCoverageDashboardItem.prototype.render = function (context, preferences) {
		$(document).ready(function() {
			if (preferences['projectKey']) {
				if (checkElementsExist()) {
					createRender(preferences);
				}
				else {
					dashboardAPI.once("afterRender",
						function() {
							createRender(preferences);
						});
				}
			}
			else {
				self.renderEdit(context, preferences);
			}
		});
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecCodeCoverageDashboardItem.prototype.renderEdit = function (context, preferences) {
		$(document).ready(function() {
			if (checkElementsExist()) {
				createConfiguration(preferences);
			}
			else {
				dashboardAPI.once("afterRender",
					function() {
						createConfiguration(preferences);
					});
			}
		});
	};

	function createRender(preferences) {
		var projectKey = preferences['projectKey'];
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
			incompleteKnowledgeShown = preferences['transitiveLinksShown'];
		}

		var filterSettings = getFilterSettings(projectKey, knowledgeTypes, documentationLocations, knowledgeStatus, linkTypes,
			linkDistance, minDegree, maxDegree, startDate, endDate,
			decisionKnowledgeShown, testCodeShown, incompleteKnowledgeShown, transitiveLinksShown);

		conDecCodeCoverageDashboard.init(filterSettings);

		dashboardAPI.resize();
	}

	function createConfiguration(preferences) {
		getHTMLNodes("condec-code-coverage-dashboard-configproject"
			, "condec-code-coverage-dashboard-contents-container"
			, "condec-code-coverage-dashboard-contents-data-error"
			, "condec-code-coverage-dashboard-no-project"
			, "condec-code-coverage-dashboard-processing"
			, "condec-code-coverage-dashboard-nogit-error");

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

		var saveButton = document.getElementById("save-button-code-coverage");
		saveButton.addEventListener("click", onSaveButton);
	}

	function createCancelButton(preferences) {
		function onCancelButton(event) {
			if (preferences['projectKey']) {
				dashboardAPI.closeEdit();
			}
		}

		var cancelButton = document.getElementById("cancel-button-code-coverage");
		cancelButton.addEventListener("click", onCancelButton);
	}

	function createListener() {
		function onSelectProject(event) {
			var projectNode = document.getElementById("project-dropdown-code-coverage");
			conDecCodeCoverageDashboard.setKnowledgeTypes(projectNode.value);
			conDecCodeCoverageDashboard.setDocumentationLocations();
			conDecCodeCoverageDashboard.setKnowledgeStatus();
			conDecCodeCoverageDashboard.setLinkTypes();
		}

		var projectKeyNode = document.getElementById("project-dropdown-code-coverage");
		projectKeyNode.addEventListener("change", onSelectProject);
	}

	function checkElementsExist() {
		getHTMLNodes("condec-code-coverage-dashboard-configproject"
			, "condec-code-coverage-dashboard-contents-container"
			, "condec-code-coverage-dashboard-contents-data-error"
			, "condec-code-coverage-dashboard-no-project"
			, "condec-code-coverage-dashboard-processing"
			, "condec-code-coverage-dashboard-nogit-error");

		return !!(dashboardFilterNode && dashboardContentNode && dashboardDataErrorNode &&
			dashboardNoContentsNode && dashboardProcessingNode && dashboardProjectWithoutGit);
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

		var projectNode = document.getElementById("project-dropdown-code-coverage");
		preferences['projectKey'] = projectNode.value;

		var knowledgeTypesNode = document.getElementById("knowledgetype-multi-select-code-coverage");
		preferences['knowledgeTypes'] = getSelectValues(knowledgeTypesNode);

		var documentationLocationsNode = document.getElementById("documentationlocation-multi-select-code-coverage");
		preferences['documentationLocations'] = getSelectValues(documentationLocationsNode);

		var knowledgeStatusNode = document.getElementById("knowledgestatus-multi-select-code-coverage");
		preferences['knowledgeStatus'] = getSelectValues(knowledgeStatusNode);

		var linkTypesNode = document.getElementById("linktype-multi-select-code-coverage");
		preferences['linkTypes'] = getSelectValues(linkTypesNode);

		var linkDistanceNode = document.getElementById("link-distance-input-code-coverage");
		preferences['linkDistance'] = linkDistanceNode.value;

		var minDegreeNode = document.getElementById("min-degree-input-code-coverage");
		preferences['minDegree'] = minDegreeNode.value;

		var maxDegreeNode = document.getElementById("max-degree-input-code-coverage");
		preferences['maxDegree'] = maxDegreeNode.value;

		var startDateNode = document.getElementById("start-date-picker-code-coverage");
		preferences['startDate'] = startDateNode.value;

		var endDateNode = document.getElementById("end-date-picker-code-coverage");
		preferences['endDate'] = endDateNode.value;

		var decisionKnowledgeNode = document.getElementById("dashboard-checkbox-decisionknowledge-code-coverage");
		preferences['decisionKnowledgeShown'] = decisionKnowledgeNode.checked;

		var testCodeNode = document.getElementById("dashboard-checkbox-testcode-code-coverage");
		preferences['testCodeShown'] = testCodeNode.checked;

		var incompleteKnowledgeNode = document.getElementById("dashboard-checkbox-incompleteknowledge-code-coverage");
		preferences['incompleteKnowledgeShown'] = incompleteKnowledgeNode.checked;

		var transitiveLinksNode = document.getElementById("dashboard-checkbox-transitivelinks-code-coverage");
		preferences['transitiveLinksShown'] = transitiveLinksNode.checked;

		return preferences;
	}

	function setPreferences(preferences) {
		if (preferences['projectKey']) {
			var projectNode = document.getElementById("project-dropdown-code-coverage");
			projectNode.value = preferences['projectKey'];

			conDecCodeCoverageDashboard.setKnowledgeTypes(preferences['projectKey']);
		}

		if (preferences['knowledgeTypes']) {
			var KnowledgeTypesNode = document.getElementById("knowledgetype-multi-select-code-coverage");
			setSelectValues(KnowledgeTypesNode, preferences['knowledgeTypes']);
		}

		conDecCodeCoverageDashboard.setDocumentationLocations();

		if (preferences['documentationLocations']) {
			var documentationLocationsNode = document.getElementById("documentationlocation-multi-select-code-coverage");
			setSelectValues(documentationLocationsNode, preferences['documentationLocations']);
		}

		conDecCodeCoverageDashboard.setKnowledgeStatus();

		if (preferences['knowledgeStatus']) {
			var knowledgeStatusNode = document.getElementById("knowledgestatus-multi-select-code-coverage");
			setSelectValues(knowledgeStatusNode, preferences['knowledgeStatus']);
		}

		conDecCodeCoverageDashboard.setLinkTypes();

		if (preferences['linkTypes']) {
			var linkTypesNode = document.getElementById("linktype-multi-select-code-coverage");
			setSelectValues(linkTypesNode, preferences['linkTypes']);
		}

		if (preferences['linkDistance']) {
			var linkDistanceNode = document.getElementById("link-distance-input-code-coverage");
			linkDistanceNode.value = preferences['linkDistance'];
		}

		if (preferences['minDegree']) {
			var minDegreeNode = document.getElementById("min-degree-input-code-coverage");
			minDegreeNode.value = preferences['minDegree'];
		}

		if (preferences['maxDegree']) {
			var maxDegreeNode = document.getElementById("max-degree-input-code-coverage");
			maxDegreeNode.value = preferences['maxDegree'];
		}

		if (preferences['startDate']) {
			var startDateNode = document.getElementById("start-date-picker-code-coverage");
			startDateNode.value = preferences['startDate'];
		}

		if (preferences['endDate']) {
			var endDateNode = document.getElementById("end-date-picker-code-coverage");
			endDateNode.value = preferences['endDate'];
		}

		if (preferences['decisionKnowledgeShown']) {
			var decisionKnowledgeNode = document.getElementById("dashboard-checkbox-decisionknowledge-code-coverage");
			decisionKnowledgeNode.checked = preferences['decisionKnowledgeShown'];
		}

		if (preferences['testCodeShown']) {
			var testCodeNode = document.getElementById("dashboard-checkbox-testcode-code-coverage");
			testCodeNode.checked = preferences['testCodeShown'];
		}

		if (preferences['incompleteKnowledgeShown']) {
			var incompleteKnowledgeNode = document.getElementById("dashboard-checkbox-incompleteknowledge-code-coverage");
			incompleteKnowledgeNode.checked = preferences['incompleteKnowledgeShown'];
		}

		if (preferences['transitiveLinksShown']) {
			var transitiveLinksNode = document.getElementById("dashboard-checkbox-transitivelinks-code-coverage");
			transitiveLinksNode.checked = preferences['transitiveLinksShown'];
		}
	}

	function getFilterSettings(projectKey, knowledgeTypes, documentationLocations, knowledgeStatus, linkTypes,
							   linkDistance, minDegree, maxDegree, startDate, endDate,
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

	return ConDecCodeCoverageDashboardItem;
});
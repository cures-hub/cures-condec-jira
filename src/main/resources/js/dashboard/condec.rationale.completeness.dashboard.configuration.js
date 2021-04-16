/*
 This module render the configuration screen used in the rationale completeness dashboard item.

 Requires
 * js/condec.requirements.dashboard.js
 * js/condec.rationale.completeness.dashboard.js

 Is referenced in HTML by
 * rationaleCompletenessDashboardItem.vm
 */
define('dashboard/rationaleCompleteness', [], function () {
	var dashboardAPI;

	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var ConDecRationaleCompletenessDashboardItem = function (API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecRationaleCompletenessDashboardItem.prototype.render = function (context, preferences) {
		$(document).ready(function() {
			if (preferences['projectKey']) {
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

				var filterSettings = getFilterSettings(projectKey, knowledgeTypes, documentationLocations, knowledgeStatus, linkTypes,
					linkDistance, minDegree, maxDegree, startDate, endDate,
					decisionKnowledgeShown, testCodeShown, incompleteKnowledgeShown);

				conDecRationaleCompletenessDashboard.init(filterSettings);

				dashboardAPI.resize();
			}
			else {
				createConfiguration(context, preferences);
			}
		});
	};

	ConDecRationaleCompletenessDashboardItem.prototype.renderEdit = function (context, preferences) {
		$(document).ready(function() {
			if (preferences['once']) {
				createConfiguration(context, preferences);
			}
			else {
				preferences['once'] = true;
				dashboardAPI.savePreferences(preferences);
				window.location.reload();
			}
		});
	};

	function createConfiguration(context, preferences) {
		getHTMLNodes("condec-rationale-completeness-dashboard-configproject"
			, "condec-rationale-completeness-dashboard-contents-container"
			, "condec-rationale-completeness-dashboard-contents-data-error"
			, "condec-rationale-completeness-dashboard-no-project"
			, "condec-rationale-completeness-dashboard-processing"
			, "condec-rationale-completeness-dashboard-nogit-error");

		showDashboardSection(dashboardFilterNode);

		setPreferences(preferences);

		dashboardAPI.resize();

		createSaveButton();

		createCancelButton(preferences);

		createListener();
	}

	function createSaveButton() {
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
				conDecRationaleCompletenessDashboard.init(filterSettings);
			}

			dashboardAPI.resize();
		}

		var saveButton = document.getElementById("rationale-completeness-save-button");
		saveButton.addEventListener("click", onSaveButton);
	}

	function createCancelButton(preferences) {
		function onCancelButton(event) {
			if (preferences['projectKey']) {
				dashboardAPI.closeEdit();
			}
		}

		var cancelButton = document.getElementById("rationale-completeness-cancel-button");
		cancelButton.addEventListener("click", onCancelButton);
	}

	function createListener() {
		function onSelectProject(event) {
			var projectNode = document.getElementById("condec-dashboard-rationale-completeness-project-selection");
			conDecRationaleCompletenessDashboard.setKnowledgeTypes(projectNode.value);
			conDecRationaleCompletenessDashboard.setDocumentationLocations();
			conDecRationaleCompletenessDashboard.setKnowledgeStatus();
			conDecRationaleCompletenessDashboard.setLinkTypes();
		}

		var projectKeyNode = document.getElementById("condec-dashboard-rationale-completeness-project-selection");
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

		preferences['once'] = true;

		var projectNode = document.getElementById("condec-dashboard-rationale-completeness-project-selection");
		preferences['projectKey'] = projectNode.value;

		var knowledgeTypesNode = document.getElementById("condec-dashboard-rationale-completeness-knowledgetypes-input");
		preferences['knowledgeTypes'] = getSelectValues(knowledgeTypesNode);

		var documentationLocationsNode = document.getElementById("condec-dashboard-rationale-completeness-documentationlocation-input");
		preferences['documentationLocations'] = getSelectValues(documentationLocationsNode);

		var knowledgeStatusNode = document.getElementById("condec-dashboard-rationale-completeness-knowledgestatus-input");
		preferences['knowledgeStatus'] = getSelectValues(knowledgeStatusNode);

		var linkTypesNode = document.getElementById("condec-dashboard-rationale-completeness-linktypes-input");
		preferences['linkTypes'] = getSelectValues(linkTypesNode);

		var linkDistanceNode = document.getElementById("condec-dashboard-rationale-completeness-linkdistance-input");
		preferences['linkDistance'] = linkDistanceNode.value;

		var minDegreeNode = document.getElementById("condec-dashboard-rationale-completeness-mindegree-input");
		preferences['minDegree'] = minDegreeNode.value;

		var maxDegreeNode = document.getElementById("condec-dashboard-rationale-completeness-maxdegree-input");
		preferences['maxDegree'] = maxDegreeNode.value;

		var startDateNode = document.getElementById("condec-dashboard-rationale-completeness-startdate-input");
		preferences['startDate'] = startDateNode.value;

		var endDateNode = document.getElementById("condec-dashboard-rationale-completeness-enddate-input");
		preferences['endDate'] = endDateNode.value;

		var decisionKnowledgeNode = document.getElementById("condec-dashboard-rationale-completeness-decisionknowledge-checkbox");
		preferences['decisionKnowledgeShown'] = decisionKnowledgeNode.checked;

		var testCodeNode = document.getElementById("condec-dashboard-rationale-completeness-testcode-checkbox");
		preferences['testCodeShown'] = testCodeNode.checked;

		var incompleteKnowledgeNode = document.getElementById("condec-dashboard-rationale-completeness-incompleteknowledge-checkbox");
		preferences['incompleteKnowledgeShown'] = incompleteKnowledgeNode.checked;

		return preferences;
	}

	function setPreferences(preferences) {
		if (preferences['projectKey']) {
			var projectNode = document.getElementById("condec-dashboard-rationale-completeness-project-selection");
			projectNode.value = preferences['projectKey'];

			conDecRationaleCompletenessDashboard.setKnowledgeTypes(preferences['projectKey']);
		}

		if (preferences['knowledgeTypes']) {
			var KnowledgeTypesNode = document.getElementById("condec-dashboard-rationale-completeness-knowledgetypes-input");
			setSelectValues(KnowledgeTypesNode, preferences['knowledgeTypes']);
		}

		conDecRationaleCompletenessDashboard.setDocumentationLocations();

		if (preferences['documentationLocations']) {
			var documentationLocationsNode = document.getElementById("condec-dashboard-rationale-completeness-documentationlocation-input");
			setSelectValues(documentationLocationsNode, preferences['documentationLocations']);
		}

		conDecRationaleCompletenessDashboard.setKnowledgeStatus();

		if (preferences['knowledgeStatus']) {
			var knowledgeStatusNode = document.getElementById("condec-dashboard-rationale-completeness-knowledgestatus-input");
			setSelectValues(knowledgeStatusNode, preferences['knowledgeStatus']);
		}

		conDecRationaleCompletenessDashboard.setLinkTypes();

		if (preferences['linkTypes']) {
			var linkTypesNode = document.getElementById("condec-dashboard-rationale-completeness-linktypes-input");
			setSelectValues(linkTypesNode, preferences['linkTypes']);
		}

		if (preferences['linkDistance']) {
			var linkDistanceNode = document.getElementById("condec-dashboard-rationale-completeness-linkdistance-input");
			linkDistanceNode.value = preferences['linkDistance'];
		}

		if (preferences['minDegree']) {
			var minDegreeNode = document.getElementById("condec-dashboard-rationale-completeness-mindegree-input");
			minDegreeNode.value = preferences['minDegree'];
		}

		if (preferences['maxDegree']) {
			var maxDegreeNode = document.getElementById("condec-dashboard-rationale-completeness-maxdegree-input");
			maxDegreeNode.value = preferences['maxDegree'];
		}

		if (preferences['startDate']) {
			var startDateNode = document.getElementById("condec-dashboard-rationale-completeness-startdate-input");
			startDateNode.value = preferences['startDate'];
		}

		if (preferences['endDate']) {
			var endDateNode = document.getElementById("condec-dashboard-rationale-completeness-enddate-input");
			endDateNode.value = preferences['endDate'];
		}

		if (preferences['decisionKnowledgeShown']) {
			var decisionKnowledgeNode = document.getElementById("condec-dashboard-rationale-completeness-decisionknowledge-checkbox");
			decisionKnowledgeNode.checked = preferences['decisionKnowledgeShown'];
		}

		if (preferences['testCodeShown']) {
			var testCodeNode = document.getElementById("condec-dashboard-rationale-completeness-testcode-checkbox");
			testCodeNode.checked = preferences['testCodeShown'];
		}

		if (preferences['incompleteKnowledgeShown']) {
			var incompleteKnowledgeNode = document.getElementById("condec-dashboard-rationale-completeness-incompleteknowledge-checkbox");
			incompleteKnowledgeNode.checked = preferences['incompleteKnowledgeShown'];
		}
	}

	function getFilterSettings(projectKey, knowledgeTypes, documentationLocations, knowledgeStatus, linkTypes,
							   linkDistance, minDegree, maxDegree, startDate, endDate,
							   decisionKnowledgeShown, testCodeShown, incompleteKnowledgeShown) {
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

	return ConDecRationaleCompletenessDashboardItem;
});
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

		var knowledgeTypesList = getList(knowledgeTypes);
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
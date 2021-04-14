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

			conDecCodeCoverageDashboard.init(filterSettings);
			dashboardAPI.resize();
		});
	};

	ConDecCodeCoverageDashboardItem.prototype.renderEdit = function (context, preferences) {
		$(document).ready(function() {
			getHTMLNodes("condec-code-coverage-dashboard-configproject"
				, "condec-code-coverage-dashboard-contents-container"
				, "condec-code-coverage-dashboard-contents-data-error"
				, "condec-code-coverage-dashboard-no-project"
				, "condec-code-coverage-dashboard-processing"
				, "condec-code-coverage-dashboard-nogit-error");

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
					conDecCodeCoverageDashboard.init(filterSettings);
				}

				dashboardAPI.resize();
			}

			function onCancelButton(event) {
				dashboardAPI.closeEdit();
				dashboardAPI.resize();
			}

			function onSelectProject(event) {
				conDecCodeCoverageDashboard.setKnowledgeTypes(preferences['projectKey']);
				conDecCodeCoverageDashboard.setDocumentationLocations();
				conDecCodeCoverageDashboard.setKnowledgeStatus();
				conDecCodeCoverageDashboard.setLinkTypes();
			}

			saveButton = document.getElementById("code-coverage-save-button");
			saveButton.addEventListener("click", onSaveButton);

			cancelButton = document.getElementById("code-coverage-cancel-button");
			cancelButton.addEventListener("click", onCancelButton);

			projectKeyNode = document.getElementById("condec-dashboard-code-coverage-project-selection");
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

		var projectNode = document.getElementById("condec-dashboard-code-coverage-project-selection");
		preferences['projectKey'] = projectNode.value;

		var knowledgeTypesNode = document.getElementById("condec-dashboard-code-coverage-knowledgetypes-input");
		preferences['knowledgeTypes'] = getSelectValues(knowledgeTypesNode);

		var documentationLocationsNode = document.getElementById("condec-dashboard-code-coverage-documentationlocation-input");
		preferences['documentationLocations'] = getSelectValues(documentationLocationsNode);

		var knowledgeStatusNode = document.getElementById("condec-dashboard-code-coverage-knowledgestatus-input");
		preferences['knowledgeStatus'] = getSelectValues(knowledgeStatusNode);

		var linkTypesNode = document.getElementById("condec-dashboard-code-coverage-linktypes-input");
		preferences['linkTypes'] = getSelectValues(linkTypesNode);

		var linkDistanceNode = document.getElementById("condec-dashboard-code-coverage-linkdistance-input");
		preferences['linkDistance'] = linkDistanceNode.value;

		var minDegreeNode = document.getElementById("condec-dashboard-code-coverage-mindegree-input");
		preferences['minDegree'] = minDegreeNode.value;

		var maxDegreeNode = document.getElementById("condec-dashboard-code-coverage-maxdegree-input");
		preferences['maxDegree'] = maxDegreeNode.value;

		var startDateNode = document.getElementById("condec-dashboard-code-coverage-startdate-input");
		preferences['startDate'] = startDateNode.value;

		var endDateNode = document.getElementById("condec-dashboard-code-coverage-enddate-input");
		preferences['endDate'] = endDateNode.value;

		var decisionKnowledgeNode = document.getElementById("condec-dashboard-code-coverage-decisionknowledge-checkbox");
		preferences['decisionKnowledgeShown'] = decisionKnowledgeNode.checked;

		var testCodeNode = document.getElementById("condec-dashboard-code-coverage-testcode-checkbox");
		preferences['testCodeShown'] = testCodeNode.checked;

		var incompleteKnowledgeNode = document.getElementById("condec-dashboard-code-coverage-incompleteknowledge-checkbox");
		preferences['incompleteKnowledgeShown'] = incompleteKnowledgeNode.checked;

		return preferences;
	}

	function setPreferences(preferences) {
		if (preferences['projectKey']) {
			var projectNode = document.getElementById("condec-dashboard-code-coverage-project-selection");
			projectNode.value = preferences['projectKey'];
		}

		conDecCodeCoverageDashboard.setKnowledgeTypes(preferences['projectKey']);

		if (preferences['knowledgeTypes']) {
			var KnowledgeTypesNode = document.getElementById("condec-dashboard-code-coverage-knowledgetypes-input");
			setSelectValues(KnowledgeTypesNode, preferences['knowledgeTypes']);
		}

		conDecCodeCoverageDashboard.setDocumentationLocations();

		if (preferences['documentationLocations']) {
			var documentationLocationsNode = document.getElementById("condec-dashboard-code-coverage-documentationlocation-input");
			setSelectValues(documentationLocationsNode, preferences['documentationLocations']);
		}

		conDecCodeCoverageDashboard.setKnowledgeStatus();

		if (preferences['knowledgeStatus']) {
			var knowledgeStatusNode = document.getElementById("condec-dashboard-code-coverage-knowledgestatus-input");
			setSelectValues(knowledgeStatusNode, preferences['knowledgeStatus']);
		}

		conDecCodeCoverageDashboard.setLinkTypes();

		if (preferences['linkTypes']) {
			var linkTypesNode = document.getElementById("condec-dashboard-code-coverage-linktypes-input");
			setSelectValues(linkTypesNode, preferences['linkTypes']);
		}

		if (preferences['linkDistance']) {
			var linkDistanceNode = document.getElementById("condec-dashboard-code-coverage-linkdistance-input");
			linkDistanceNode.value = preferences['linkDistance'];
		}

		if (preferences['minDegree']) {
			var minDegreeNode = document.getElementById("condec-dashboard-code-coverage-mindegree-input");
			minDegreeNode.value = preferences['minDegree'];
		}

		if (preferences['maxDegree']) {
			var maxDegreeNode = document.getElementById("condec-dashboard-code-coverage-maxdegree-input");
			maxDegreeNode.value = preferences['maxDegree'];
		}

		if (preferences['startDate']) {
			var startDateNode = document.getElementById("condec-dashboard-code-coverage-startdate-input");
			startDateNode.value = preferences['startDate'];
		}

		if (preferences['endDate']) {
			var endDateNode = document.getElementById("condec-dashboard-code-coverage-enddate-input");
			endDateNode.value = preferences['endDate'];
		}

		if (preferences['decisionKnowledgeShown']) {
			var decisionKnowledgeNode = document.getElementById("condec-dashboard-code-coverage-decisionknowledge-checkbox");
			decisionKnowledgeNode.checked = preferences['decisionKnowledgeShown'];
		}

		if (preferences['testCodeShown']) {
			var testCodeNode = document.getElementById("condec-dashboard-code-coverage-testcode-checkbox");
			testCodeNode.checked = preferences['testCodeShown'];
		}

		if (preferences['incompleteKnowledgeShown']) {
			var incompleteKnowledgeNode = document.getElementById("condec-dashboard-code-coverage-incompleteknowledge-checkbox");
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

	return ConDecCodeCoverageDashboardItem;
});
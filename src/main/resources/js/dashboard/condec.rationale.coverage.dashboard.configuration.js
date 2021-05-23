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

	var dashboardFilterProjectNode;
	var dashboardFilterSourceKnowledgeTypesNode;
	var dashboardFilterKnowledgeTypesNode;
	var dashboardFilterDocumentationLocationsNode;
	var dashboardFilterKnowledgeStatusNode;
	var dashboardFilterLinkTypesNode;
	var dashboardFilterLinkDistanceNode;
	var dashboardFilterMinimumDecisionCoverageNode;
	var dashboardFilterMinDegreeNode;
	var dashboardFilterMaxDegreeNode;
	var dashboardFilterStartDateNode;
	var dashboardFilterEndDateNode;
	var dashboardFilterDecisionKnowledgeShownNode;
	var dashboardFilterTestCodeShownNode;
	var dashboardFilterIncompleteKnowledgeShownNode;
	var dashboardFilterTransitiveLinksShownNode;
	var dashboardFilterSaveButton;
	var dashboardFilterCancelButton;

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
				if (preferences['projectKey']) {
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
		var sourceKnowledgeTypes = "";
		if (preferences['sourceKnowledgeTypes']) {
			sourceKnowledgeTypes = preferences['sourceKnowledgeTypes'];
		}
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

		conDecRationaleCoverageDashboard.init(filterSettings, sourceKnowledgeTypes);

		dashboardAPI.resize();
	}

	function createConfiguration(preferences) {
		getHTMLNodes("condec-rationale-coverage-dashboard-configproject"
			, "condec-rationale-coverage-dashboard-contents-container"
			, "condec-rationale-coverage-dashboard-contents-data-error"
			, "condec-rationale-coverage-dashboard-no-project"
			, "condec-rationale-coverage-dashboard-processing"
			, "project-dropdown-rationale-coverage"
			, "source-knowledgetype-multi-select-rationale-coverage"
			, "knowledgetype-multi-select-rationale-coverage"
			, "documentationlocation-multi-select-rationale-coverage"
			, "knowledgestatus-multi-select-rationale-coverage"
			, "linktype-multi-select-rationale-coverage"
			, "link-distance-input-rationale-coverage"
			, "minimum-decision-coverage-input-rationale-coverage"
			, "min-degree-input-rationale-coverage"
			, "max-degree-input-rationale-coverage"
			, "start-date-picker-rationale-coverage"
			, "end-date-picker-rationale-coverage"
			, "is-decision-knowledge-only-input-rationale-coverage"
			, "is-incomplete-knowledge-input-rationale-coverage"
			, "is-test-code-input-rationale-coverage"
			, "is-transitive-links-input-rationale-coverage"
			, "save-button-rationale-coverage"
			, "cancel-button-rationale-coverage");

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

		dashboardFilterSaveButton.addEventListener("click", onSaveButton);
	}

	function createCancelButton(preferences) {
		function onCancelButton(event) {
			if (preferences['projectKey']) {
				dashboardAPI.closeEdit();
			}
		}

		dashboardFilterCancelButton.addEventListener("click", onCancelButton);
	}

	function createListener() {
		function onSelectProject(event) {
			setSourceKnowledgeTypes(dashboardFilterProjectNode.value);
			setKnowledgeTypes(dashboardFilterProjectNode.value);
			setDocumentationLocations();
			setKnowledgeStatus();
			setLinkTypes(dashboardFilterProjectNode.value);
			setDefaultLinkDistanceAndAndMinimumDecisionCoverage(dashboardFilterProjectNode.value);
		}

		dashboardFilterProjectNode.addEventListener("change", onSelectProject);
	}

	function getHTMLNodes(filterName, containerName, dataErrorName, noProjectName, processingName,
						  projectName, sourceKnowledgeTypesName, knowledgeTypesName, documentationLocationsName, knowledgeStatusName, linkTypesName,
						  linkDistanceName, minimumDecisionCoverageName, minDegreeName, maxDegreeName, startDateName, endDateName,
						  decisionKnowledgeShownName, testCodeShownName, incompleteKnowledgeShownName, transitiveLinkShownName,
						  saveButtonName, cancelButtonName) {
		dashboardFilterNode = document.getElementById(filterName);
		dashboardContentNode = document.getElementById(containerName);
		dashboardDataErrorNode = document.getElementById(dataErrorName);
		dashboardNoContentsNode = document.getElementById(noProjectName);
		dashboardProcessingNode = document.getElementById(processingName);

		dashboardFilterProjectNode = document.getElementById(projectName);
		dashboardFilterSourceKnowledgeTypesNode = document.getElementById(sourceKnowledgeTypesName);
		dashboardFilterKnowledgeTypesNode = document.getElementById(knowledgeTypesName);
		dashboardFilterDocumentationLocationsNode = document.getElementById(documentationLocationsName);
		dashboardFilterKnowledgeStatusNode = document.getElementById(knowledgeStatusName);
		dashboardFilterLinkTypesNode = document.getElementById(linkTypesName);
		dashboardFilterLinkDistanceNode = document.getElementById(linkDistanceName);
		dashboardFilterMinimumDecisionCoverageNode = document.getElementById(minimumDecisionCoverageName);
		dashboardFilterMinDegreeNode = document.getElementById(minDegreeName);
		dashboardFilterMaxDegreeNode = document.getElementById(maxDegreeName);
		dashboardFilterStartDateNode = document.getElementById(startDateName);
		dashboardFilterEndDateNode = document.getElementById(endDateName);
		dashboardFilterDecisionKnowledgeShownNode = document.getElementById(decisionKnowledgeShownName);
		dashboardFilterTestCodeShownNode = document.getElementById(testCodeShownName);
		dashboardFilterIncompleteKnowledgeShownNode = document.getElementById(incompleteKnowledgeShownName);
		dashboardFilterTransitiveLinksShownNode = document.getElementById(transitiveLinkShownName);
		dashboardFilterSaveButton = document.getElementById(saveButtonName);
		dashboardFilterCancelButton = document.getElementById(cancelButtonName);
	}

	function showDashboardSection(node) {
		var hiddenClass = "hidden";
		dashboardFilterNode.classList.add(hiddenClass);
		dashboardContentNode.classList.add(hiddenClass);
		dashboardDataErrorNode.classList.add(hiddenClass);
		dashboardNoContentsNode.classList.add(hiddenClass);
		dashboardProcessingNode.classList.add(hiddenClass);
		node.classList.remove(hiddenClass);
	}

	function getPreferences() {
		var preferences = {};

		preferences['projectKey'] = dashboardFilterProjectNode.value;
		preferences['sourceKnowledgeTypes'] = getSelectValues(dashboardFilterSourceKnowledgeTypesNode);
		preferences['knowledgeTypes'] = getSelectValues(dashboardFilterKnowledgeTypesNode);
		preferences['documentationLocations'] = getSelectValues(dashboardFilterDocumentationLocationsNode);
		preferences['knowledgeStatus'] = getSelectValues(dashboardFilterKnowledgeStatusNode);
		preferences['linkTypes'] = getSelectValues(dashboardFilterLinkTypesNode);
		preferences['linkDistance'] = dashboardFilterLinkDistanceNode.value;
		preferences['minimumDecisionCoverage'] = dashboardFilterMinimumDecisionCoverageNode.value;
		preferences['minDegree'] = dashboardFilterMinDegreeNode.value;
		preferences['maxDegree'] = dashboardFilterMaxDegreeNode.value;
		preferences['startDate'] = dashboardFilterStartDateNode.value;
		preferences['endDate'] = dashboardFilterEndDateNode.value;
		preferences['decisionKnowledgeShown'] = dashboardFilterDecisionKnowledgeShownNode.checked;
		preferences['testCodeShown'] = dashboardFilterTestCodeShownNode.checked;
		preferences['incompleteKnowledgeShown'] = dashboardFilterIncompleteKnowledgeShownNode.checked;
		preferences['transitiveLinksShown'] = dashboardFilterTransitiveLinksShownNode.checked;

		return preferences;
	}

	function setPreferences(preferences) {
		if (preferences['projectKey']) {
			dashboardFilterProjectNode.value = preferences['projectKey'];

			setSourceKnowledgeTypes(preferences['projectKey']);
			setKnowledgeTypes(preferences['projectKey']);
		}

		if (preferences['sourceKnowledgeTypes']) {
			setSelectValues(dashboardFilterSourceKnowledgeTypesNode, preferences['sourceKnowledgeTypes']);
		}

		if (preferences['knowledgeTypes']) {
			setSelectValues(dashboardFilterKnowledgeTypesNode, preferences['knowledgeTypes']);
		}

		setDocumentationLocations();

		if (preferences['documentationLocations']) {
			setSelectValues(dashboardFilterDocumentationLocationsNode, preferences['documentationLocations']);
		}

		setKnowledgeStatus();

		if (preferences['knowledgeStatus']) {
			setSelectValues(dashboardFilterKnowledgeStatusNode, preferences['knowledgeStatus']);
		}

		setLinkTypes();

		if (preferences['linkTypes']) {
			setSelectValues(dashboardFilterLinkTypesNode, preferences['linkTypes']);
		}

		if (preferences['linkDistance']) {
			dashboardFilterLinkDistanceNode.value = preferences['linkDistance'];
		}

		if (preferences['minimumDecisionCoverage']) {
			dashboardFilterMinimumDecisionCoverageNode.value = preferences['minimumDecisionCoverage'];
		}

		if (preferences['minDegree']) {
			dashboardFilterMinDegreeNode.value = preferences['minDegree'];
		}

		if (preferences['maxDegree']) {
			dashboardFilterMaxDegreeNode.value = preferences['maxDegree'];
		}

		if (preferences['startDate']) {
			dashboardFilterStartDateNode.value = preferences['startDate'];
		}

		if (preferences['endDate']) {
			dashboardFilterEndDateNode.value = preferences['endDate'];
		}

		if (preferences['decisionKnowledgeShown']) {
			dashboardFilterDecisionKnowledgeShownNode.checked = preferences['decisionKnowledgeShown'];
		}

		if (preferences['testCodeShown']) {
			dashboardFilterTestCodeShownNode.checked = preferences['testCodeShown'];
		}

		if (preferences['incompleteKnowledgeShown']) {
			dashboardFilterIncompleteKnowledgeShownNode.checked = preferences['incompleteKnowledgeShown'];
		}

		if (preferences['transitiveLinksShown']) {
			dashboardFilterTransitiveLinksShownNode.checked = preferences['transitiveLinksShown'];
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

		if (linkDistance) {
			filterSettings.linkDistance = linkDistance;
		}
		if (minimumDecisionCoverage) {
			filterSettings.minimumDecisionCoverage = minimumDecisionCoverage;
		}
		if (minDegree) {
			filterSettings.minDegree = minDegree;
		}
		if (maxDegree) {
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

	function setSourceKnowledgeTypes(projectKey) {
		removeOptions(dashboardFilterSourceKnowledgeTypesNode);

		conDecAPI.projectKey = projectKey;
		var sourceKnowledgeTypes = conDecAPI.getKnowledgeTypes();

		for (i = 0; i < sourceKnowledgeTypes.length; i++) {
			var sourceKnowledgeType = document.createElement('option');
			sourceKnowledgeType.value = sourceKnowledgeTypes[i];
			sourceKnowledgeType.text = sourceKnowledgeTypes[i];
			dashboardFilterSourceKnowledgeTypesNode.options.add(sourceKnowledgeType);
		}
	}

	function setKnowledgeTypes(projectKey) {
		removeOptions(dashboardFilterKnowledgeTypesNode);

		conDecAPI.projectKey = projectKey;
		var knowledgeTypes = conDecAPI.getKnowledgeTypes();

		for (i = 0; i < knowledgeTypes.length; i++) {
			var knowledgeType = document.createElement('option');
			knowledgeType.value = knowledgeTypes[i];
			knowledgeType.text = knowledgeTypes[i];
			dashboardFilterKnowledgeTypesNode.options.add(knowledgeType);
		}
	}

	function setDocumentationLocations() {
		removeOptions(dashboardFilterDocumentationLocationsNode);

		var documentationLocations = conDecAPI.documentationLocations;

		for (i = 0; i < documentationLocations.length; i++) {
			var documentationLocation = document.createElement('option');
			documentationLocation.value = documentationLocations[i];
			documentationLocation.text = documentationLocations[i];
			dashboardFilterDocumentationLocationsNode.options.add(documentationLocation);
		}
	}

	function setKnowledgeStatus() {
		removeOptions(dashboardFilterKnowledgeStatusNode);

		var knowledgeStatuses = conDecAPI.knowledgeStatus;

		for (i = 0; i < knowledgeStatuses.length; i++) {
			var knowledgeStatus = document.createElement('option');
			knowledgeStatus.value = knowledgeStatuses[i];
			knowledgeStatus.text = knowledgeStatuses[i];
			dashboardFilterKnowledgeStatusNode.options.add(knowledgeStatus);
		}
	}

	function setLinkTypes() {
		removeOptions(dashboardFilterLinkTypesNode);

		var linkTypes = conDecAPI.getLinkTypes();

		for (i = 0; i < linkTypes.length; i++) {
			var linkType = document.createElement('option');
			linkType.value = linkTypes[i];
			linkType.text = linkTypes[i];
			dashboardFilterLinkTypesNode.options.add(linkType);
		}
	}

	function setDefaultLinkDistanceAndAndMinimumDecisionCoverage(projectKey) {
		if (projectKey) {
			conDecAPI.getFilterSettings(projectKey, "", function (filterSettings) {
				dashboardFilterLinkDistanceNode.value = filterSettings.linkDistance;
				dashboardFilterMinimumDecisionCoverageNode.value = filterSettings.minimumDecisionCoverage;
			});
		}
	}

	function removeOptions(selectElement) {
		var i, L = selectElement.options.length - 1;
		for(i = L; i >= 0; i--) {
			selectElement.remove(i);
		}
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
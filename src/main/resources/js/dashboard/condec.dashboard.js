/*
 This module contains methods used to render the dashboards and their configuration screens.

 Is referenced by
 * condec.general.metrics.dashboard.js
 * condec.general.metrics.dashboard.configuration.js
 * condec.git.branches.dashboard.js
 * condec.git.branches.dashboard.configuration.js
 * condec.rationale.completeness.dashboard.js
 * condec.rationale.completeness.dashboard.configuration.js
 * condec.rationale.coverage.dashboard.js
 * condec.rationale.coverage.dashboard.configuration.js
 */
(function (global) {
	var ConDecDashboard = function ConDecDashboard() {
		console.log("ConDecDashboard constructor");
	};

	ConDecDashboard.prototype.initRender = function (dashboard, viewIdentifier, dashboardAPI, preferences) {
		dashboardAPI.once("afterRender",
			function() {
				if (preferences['projectKey']) {
					createRender(dashboard, viewIdentifier, dashboardAPI, preferences);
				}
			});
	}

	ConDecDashboard.prototype.initConfiguration = function (viewIdentifier, dashboardAPI, preferences) {
		dashboardAPI.once("afterRender",
			function() {
				createConfiguration(viewIdentifier, dashboardAPI, preferences);
			});
	}

	function createRender(dashboard, viewIdentifier, dashboardAPI, preferences) {
		var projectKey = preferences['projectKey'];
		var sourceKnowledgeTypes = "";
		if (preferences['sourceKnowledgeTypes']) {
			sourceKnowledgeTypes = preferences['sourceKnowledgeTypes'];
		}
		var minimumDecisionCoverage;
		if (preferences['minimumDecisionCoverage']) {
			minimumDecisionCoverage = preferences['minimumDecisionCoverage'];
		}
		var maximumLinkDistance;
		if (preferences['maximumLinkDistance']) {
			maximumLinkDistance = preferences['maximumLinkDistance'];
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
		var decisionGroups;
		if (preferences['decisionGroups']) {
			decisionGroups = preferences['decisionGroups'];
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
			transitiveLinksShown = preferences['transitiveLinksShown'];
		}

		var filterSettings = getFilterSettings(projectKey, minimumDecisionCoverage, maximumLinkDistance,
			knowledgeTypes, documentationLocations, knowledgeStatus, linkTypes, decisionGroups,
			linkDistance, minDegree, maxDegree, startDate, endDate,
			decisionKnowledgeShown, testCodeShown, incompleteKnowledgeShown, transitiveLinksShown);

		getData(dashboard, viewIdentifier, dashboardAPI, filterSettings, sourceKnowledgeTypes);

		dashboardAPI.resize();
	}

	function createConfiguration(viewIdentifier, dashboardAPI, preferences) {
		showDashboardSection("condec-dashboard-config-", viewIdentifier);

		setPreferences(preferences, viewIdentifier);

		createSaveButton(dashboardAPI, viewIdentifier);

		createCancelButton(preferences, dashboardAPI, viewIdentifier);

		createListener(viewIdentifier);

		dashboardAPI.resize();
	}

	function createSaveButton(dashboardAPI, viewIdentifier) {
		function onSaveButton(event) {
			var preferences = getPreferences(viewIdentifier);

			if (preferences['projectKey']) {
				dashboardAPI.savePreferences(preferences);
			}

			dashboardAPI.resize();
		}

		document.getElementById("save-button-" + viewIdentifier).addEventListener("click", onSaveButton);
	}

	function createCancelButton(preferences, dashboardAPI, viewIdentifier) {
		function onCancelButton(event) {
			if (preferences['projectKey']) {
				showDashboardSection("condec-dashboard-contents-container-", viewIdentifier);
			}

			dashboardAPI.resize();
		}

		document.getElementById("cancel-button-" + viewIdentifier).addEventListener("click", onCancelButton);
	}

	function createListener(viewIdentifier) {
		function onSelectProject(event) {
			setSourceKnowledgeTypes(document.getElementById("project-dropdown-" + viewIdentifier).value, viewIdentifier);
			setKnowledgeTypes(document.getElementById("project-dropdown-" + viewIdentifier).value, viewIdentifier);
			setDocumentationLocations(viewIdentifier);
			setKnowledgeStatus(viewIdentifier);
			setLinkTypes(viewIdentifier);
			setDecisionGroups(document.getElementById("project-dropdown-" + viewIdentifier).value, viewIdentifier)
			setDefaultMinimumDecisionCoverageAndMaximumLinkDistance(document.getElementById("project-dropdown-" + viewIdentifier).value, viewIdentifier);
		}

		document.getElementById("project-dropdown-" + viewIdentifier).addEventListener("change", onSelectProject);
	}

	function showDashboardSection(nodeName, viewIdentifier) {
		var hiddenClass = "hidden";
		document.getElementById("condec-dashboard-config-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById("condec-dashboard-contents-container-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById("condec-dashboard-contents-data-error-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById("condec-dashboard-no-project-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById("condec-dashboard-processing-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById(nodeName + viewIdentifier).classList.remove(hiddenClass);
	}

	function getData(dashboard, viewIdentifier, dashboardAPI, filterSettings, sourceKnowledgeTypes) {
		filterSettings = JSON.parse(filterSettings);
		if (!filterSettings.projectKey || !filterSettings.projectKey.length) {
			return;
		}

		showDashboardSection("condec-dashboard-processing-", viewIdentifier);
		document.getElementById("condec-dashboard-selected-project-rationale-coverage").innerText = filterSettings.projectKey;

		dashboard.getData(dashboardAPI, filterSettings, sourceKnowledgeTypes);
	}

	ConDecDashboard.prototype.processData = function (error, result, dashboard, viewIdentifier, dashboardAPI, filterSettings) {
		if (error) {
			showDashboardSection("condec-dashboard-contents-data-error-", viewIdentifier);
		} else {
			showDashboardSection("condec-dashboard-contents-container-", viewIdentifier);
			dashboard.renderData(result, filterSettings);
		}

		dashboardAPI.resize();
	}

	function getPreferences(viewIdentifier) {
		var preferences = {};

		var projectNode = document.getElementById("project-dropdown-" + viewIdentifier);
		if (projectNode) {
			preferences['projectKey'] = projectNode.value;
		}

		var sourceKnowledgeTypeNode = document.getElementById("source-knowledgetype-multi-select-" + viewIdentifier);
		if (sourceKnowledgeTypeNode) {
			preferences['sourceKnowledgeTypes'] = getSelectedValues(sourceKnowledgeTypeNode);
		}

		var minimumDecisionCoverageNode = document.getElementById("minimum-number-of-decisions-input-" + viewIdentifier);
		if (minimumDecisionCoverageNode) {
			preferences['minimumDecisionCoverage'] = minimumDecisionCoverageNode.value;
		}

		var maximumLinkDistanceNode = document.getElementById("link-distance-to-decision-number-input-" + viewIdentifier);
		if (maximumLinkDistanceNode) {
			preferences['maximumLinkDistance'] = maximumLinkDistanceNode.value;
		}

		var knowledgeTypeNode = document.getElementById("knowledgetype-multi-select-" + viewIdentifier);
		if (knowledgeTypeNode) {
			preferences['knowledgeTypes'] = getSelectedValues(knowledgeTypeNode);
		}

		var documentationLocationNode = document.getElementById("documentationlocation-multi-select-" + viewIdentifier);
		if (documentationLocationNode) {
			preferences['documentationLocations'] = getSelectedValues(documentationLocationNode);
		}

		var knowledgeStatusNode = document.getElementById("knowledgestatus-multi-select-" + viewIdentifier);
		if (knowledgeStatusNode) {
			preferences['knowledgeStatus'] = getSelectedValues(knowledgeStatusNode);
		}

		var linkTypeNode = document.getElementById("linktype-multi-select-" + viewIdentifier);
		if (linkTypeNode) {
			preferences['linkTypes'] = getSelectedValues(linkTypeNode);
		}

		var decisionGroupNode = document.getElementById("decisiongroup-multi-select-" + viewIdentifier);
		if (decisionGroupNode) {
			preferences['decisionGroups'] = getSelectedValues(decisionGroupNode);
		}

		var linkDistanceNode = document.getElementById("link-distance-input-" + viewIdentifier);
		if (linkDistanceNode) {
			preferences['linkDistance'] = linkDistanceNode.value;
		}

		var minDegreeNode = document.getElementById("min-degree-input-" + viewIdentifier);
		if (minDegreeNode) {
			preferences['minDegree'] = minDegreeNode.value;
		}

		var maxDegreeNode = document.getElementById("max-degree-input-" + viewIdentifier);
		if (maxDegreeNode) {
			preferences['maxDegree'] = maxDegreeNode.value;
		}

		var startDateNode = document.getElementById("start-date-picker-" + viewIdentifier);
		if (startDateNode) {
			preferences['startDate'] = startDateNode.value;
		}

		var endDateNode = document.getElementById("end-date-picker-" + viewIdentifier);
		if (endDateNode) {
			preferences['endDate'] = endDateNode.value;
		}

		var decisionKnowledgeShownNode = document.getElementById("is-decision-knowledge-only-input-" + viewIdentifier);
		if (decisionKnowledgeShownNode) {
			preferences['decisionKnowledgeShown'] = decisionKnowledgeShownNode.checked;
		}

		var testCodeShown = document.getElementById("is-test-code-input-" + viewIdentifier);
		if (testCodeShown) {
			preferences['testCodeShown'] = testCodeShown.checked;
		}

		var incompleteKnowledgeShown = document.getElementById("is-incomplete-knowledge-input-" + viewIdentifier);
		if (incompleteKnowledgeShown) {
			preferences['incompleteKnowledgeShown'] = incompleteKnowledgeShown.checked;
		}

		var transitiveLinksShown = document.getElementById("is-transitive-links-input-" + viewIdentifier);
		if (transitiveLinksShown) {
			preferences['transitiveLinksShown'] = transitiveLinksShown.checked;
		}

		return preferences;
	}

	function setPreferences(preferences, viewIdentifier) {
		var projectNode = document.getElementById("project-dropdown-" + viewIdentifier);
		if (preferences['projectKey']) {
			projectNode.value = preferences['projectKey'];

			setSourceKnowledgeTypes(preferences['projectKey'], viewIdentifier);
			setKnowledgeTypes(preferences['projectKey'], viewIdentifier);
			setDocumentationLocations(viewIdentifier);
			setKnowledgeStatus(viewIdentifier);
			setLinkTypes(viewIdentifier);
			setDecisionGroups(preferences['projectKey'], viewIdentifier);
		}

		var sourceKnowledgeTypeNode = document.getElementById("source-knowledgetype-multi-select-" + viewIdentifier);
		if (preferences['sourceKnowledgeTypes']) {
			setSelectedValues(sourceKnowledgeTypeNode, preferences['sourceKnowledgeTypes']);
		}

		var minimumDecisionCoverageNode = document.getElementById("minimum-number-of-decisions-input-" + viewIdentifier);
		if (preferences['minimumDecisionCoverage']) {
			minimumDecisionCoverageNode.value = preferences['minimumDecisionCoverage'];
		}

		var maximumLinkDistanceNode = document.getElementById("link-distance-to-decision-number-input-" + viewIdentifier);
		if (preferences['maximumLinkDistance']) {
			maximumLinkDistanceNode.value = preferences['maximumLinkDistance'];
		}

		var knowledgeTypeNode = document.getElementById("knowledgetype-multi-select-" + viewIdentifier);
		if (preferences['knowledgeTypes']) {
			setSelectedValues(knowledgeTypeNode, preferences['knowledgeTypes']);
		}

		var documentationLocationNode = document.getElementById("documentationlocation-multi-select-" + viewIdentifier);
		if (preferences['documentationLocations']) {
			setSelectedValues(documentationLocationNode, preferences['documentationLocations']);
		}

		var knowledgeStatusNode = document.getElementById("knowledgestatus-multi-select-" + viewIdentifier);
		if (preferences['knowledgeStatus']) {
			setSelectedValues(knowledgeStatusNode, preferences['knowledgeStatus']);
		}

		var linkTypeNode = document.getElementById("linktype-multi-select-" + viewIdentifier);
		if (preferences['linkTypes']) {
			setSelectedValues(linkTypeNode, preferences['linkTypes']);
		}

		var decisionGroupNode = document.getElementById("decisiongroup-multi-select-" + viewIdentifier);
		if (preferences['decisionGroups']) {
			setSelectedValues(decisionGroupNode, preferences['decisionGroups']);
		}

		var linkDistanceNode = document.getElementById("link-distance-input-" + viewIdentifier);
		if (preferences['linkDistance']) {
			linkDistanceNode.value = preferences['linkDistance'];
		}

		var minDegreeNode = document.getElementById("min-degree-input-" + viewIdentifier);
		if (preferences['minDegree']) {
			minDegreeNode.value = preferences['minDegree'];
		}

		var maxDegreeNode = document.getElementById("max-degree-input-" + viewIdentifier);
		if (preferences['maxDegree']) {
			maxDegreeNode.value = preferences['maxDegree'];
		}

		var startDateNode = document.getElementById("start-date-picker-" + viewIdentifier);
		if (preferences['startDate']) {
			startDateNode.value = preferences['startDate'];
		}

		var endDateNode = document.getElementById("end-date-picker-" + viewIdentifier);
		if (preferences['endDate']) {
			endDateNode.value = preferences['endDate'];
		}

		var decisionKnowledgeShownNode = document.getElementById("is-decision-knowledge-only-input-" + viewIdentifier);
		if (preferences['decisionKnowledgeShown']) {
			decisionKnowledgeShownNode.checked = preferences['decisionKnowledgeShown'];
		}

		var testCodeShown = document.getElementById("is-test-code-input-" + viewIdentifier);
		if (preferences['testCodeShown']) {
			testCodeShown.checked = preferences['testCodeShown'];
		}

		var incompleteKnowledgeShown = document.getElementById("is-incomplete-knowledge-input-" + viewIdentifier);
		if (preferences['incompleteKnowledgeShown']) {
			incompleteKnowledgeShown.checked = preferences['incompleteKnowledgeShown'];
		}

		var transitiveLinksShown = document.getElementById("is-transitive-links-input-" + viewIdentifier);
		if (preferences['transitiveLinksShown']) {
			transitiveLinksShown.checked = preferences['transitiveLinksShown'];
		}
	}

	function getFilterSettings(projectKey, minimumDecisionCoverage, maximumLinkDistance,
							   knowledgeTypes, documentationLocations, knowledgeStatus, linkTypes, decisionGroups,
							   linkDistance, minDegree, maxDegree, startDate, endDate,
							   decisionKnowledgeShown, testCodeShown, incompleteKnowledgeShown, transitiveLinksShown) {
		var filterSettings = {};

		filterSettings.projectKey = projectKey;
		filterSettings.searchTerm = "";
		filterSettings.definitionOfDone = {};

		if (minimumDecisionCoverage) {
			filterSettings.definitionOfDone.minimumDecisionsWithinLinkDistance = minimumDecisionCoverage;
		}

		if (maximumLinkDistance) {
			filterSettings.definitionOfDone.maximumLinkDistanceToDecisions = maximumLinkDistance;
		}

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

		var decisionGroupsList = getList(decisionGroups);
		if (decisionGroupsList && Array.isArray(decisionGroupsList) && decisionGroupsList.length) {
			filterSettings.groups = decisionGroupsList;
		}

		if (linkDistance) {
			filterSettings.linkDistance = linkDistance;
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

	function setSourceKnowledgeTypes(projectKey, viewIdentifier) {
		var sourceKnowledgeTypeSelection = document.getElementById("source-knowledgetype-multi-select-" + viewIdentifier);
		conDecAPI.projectKey = projectKey;
		var sourceKnowledgeTypes = conDecAPI.getKnowledgeTypes();
		setMultiSelection(sourceKnowledgeTypeSelection, sourceKnowledgeTypes);
	}

	function setKnowledgeTypes(projectKey, viewIdentifier) {
		var knowledgeTypeSelection = document.getElementById("knowledgetype-multi-select-" + viewIdentifier);
		conDecAPI.projectKey = projectKey;
		var knowledgeTypes = conDecAPI.getKnowledgeTypes();
		setMultiSelection(knowledgeTypeSelection, knowledgeTypes);
	}

	function setDocumentationLocations(viewIdentifier) {
		var documentationLocationSelection = document.getElementById("documentationlocation-multi-select-" + viewIdentifier);
		var documentationLocations = conDecAPI.documentationLocations;
		setMultiSelection(documentationLocationSelection, documentationLocations);
	}

	function setKnowledgeStatus(viewIdentifier) {
		var knowledgeStatusSelection = document.getElementById("knowledgestatus-multi-select-" + viewIdentifier);
		var knowledgeStatuses = conDecAPI.knowledgeStatus;
		setMultiSelection(knowledgeStatusSelection, knowledgeStatuses);
	}

	function setLinkTypes(viewIdentifier) {
		var linkTypeSelection = document.getElementById("linktype-multi-select-" + viewIdentifier);
		var linkTypes = conDecAPI.getLinkTypes();
		setMultiSelection(linkTypeSelection, linkTypes);
	}

	function setDecisionGroups(projectKey, viewIdentifier) {
		var decisionGroupSelection = document.getElementById("decisiongroup-multi-select-" + viewIdentifier);
		var decisionGroups = conDecAPI.getAllDecisionGroups();
		setMultiSelection(decisionGroupSelection, decisionGroups);
	}

	function setMultiSelection(selectionElement, options) {
		if (selectionElement) {
			removeOptions(selectionElement);

			for (var i = 0; i < options.length; i++) {
				var option = document.createElement('option');
				option.value = options[i];
				option.text = options[i];
				selectionElement.options.add(option);
			}
		}
	}

	function setDefaultMinimumDecisionCoverageAndMaximumLinkDistance(projectKey, viewIdentifier) {
		if (projectKey) {
			var minimumDecisionCoverageNode = document.getElementById("minimum-number-of-decisions-input-" + viewIdentifier);
			var maximumLinkDistanceNode = document.getElementById("link-distance-to-decision-number-input-" + viewIdentifier);
			if (minimumDecisionCoverageNode && maximumLinkDistanceNode) {
				conDecDoDCheckingAPI.getDefinitionOfDone(projectKey, (definitionOfDone) => {
					minimumDecisionCoverageNode.value = definitionOfDone.minimumDecisionsWithinLinkDistance;
					maximumLinkDistanceNode.value = definitionOfDone.maximumLinkDistanceToDecisions;
				});
			}
		}
	}

	function removeOptions(selectedElement) {
		for(var i = selectedElement.options.length - 1; i >= 0; i--) {
			selectedElement.remove(i);
		}
	}

	function getSelectedValues(selectedElement) {
		var result = [];
		var options = selectedElement.options;

		for (var i = 0; i < options.length; i++) {
			if (options[i].selected) {
				result.push(options[i].value);
			}
		}

		return result;
	}

	function setSelectedValues(selectedElement, list) {
		var options = selectedElement.options;
		var values = list.split(",");

		for (var i = 0; i < options.length; i++) {
			options[i].selected = false;
			for (var j = 0; j < values.length; j++) {
				if (options[i].value === values[j]) {
					options[i].selected = true;
				}
			}
		}
	}

	function getList(jsonString) {
		if (!jsonString || !jsonString.length) {
			return null;
		}

		if (Array.isArray(jsonString)) {
			return jsonString;
		}

		jsonString = jsonString.replace("\[", "").replace("\]", "");
		jsonString = jsonString.replaceAll("\"", "");

		return jsonString.split(",");
	}

	global.conDecDashboard = new ConDecDashboard();
})(window);
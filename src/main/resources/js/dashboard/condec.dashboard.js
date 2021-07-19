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
				if (preferences["projectKey"]) {
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
		var sourceKnowledgeTypes = "";
		if (preferences["sourceKnowledgeTypes"]) {
			sourceKnowledgeTypes = preferences["sourceKnowledgeTypes"];
		}
		var filterSettings = getFilterSettings(preferences);
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

			if (preferences["projectKey"]) {
				dashboardAPI.savePreferences(preferences);
			}

			dashboardAPI.resize();
		}

		document.getElementById("save-button-" + viewIdentifier).addEventListener("click", onSaveButton);
	}

	function createCancelButton(preferences, dashboardAPI, viewIdentifier) {
		function onCancelButton(event) {
			if (preferences["projectKey"]) {
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

		setPreferenceValue("projectKey", null, preferences, "project-dropdown-", viewIdentifier);
		setPreferenceValue("sourceKnowledgeTypes", "list", preferences, "source-knowledgetype-multi-select-", viewIdentifier);
		setPreferenceValue("minimumDecisionCoverage", null, preferences, "minimum-number-of-decisions-input-", viewIdentifier);
		setPreferenceValue("maximumLinkDistance", null, preferences, "link-distance-to-decision-number-input-", viewIdentifier);
		setPreferenceValue("knowledgeTypes", "list", preferences, "knowledgetype-multi-select-", viewIdentifier);
		setPreferenceValue("documentationLocations", "list", preferences, "documentationlocation-multi-select-", viewIdentifier);
		setPreferenceValue("knowledgeStatus", "list", preferences, "knowledgestatus-multi-select-", viewIdentifier);
		setPreferenceValue("linkTypes", "list", preferences, "linktype-multi-select-", viewIdentifier);
		setPreferenceValue("decisionGroups", "list", preferences, "decisiongroup-multi-select-", viewIdentifier);
		setPreferenceValue("linkDistance", null, preferences, "link-distance-input-", viewIdentifier);
		setPreferenceValue("minDegree", null, preferences, "min-degree-input-", viewIdentifier);
		setPreferenceValue("maxDegree", null, preferences, "max-degree-input-", viewIdentifier);
		setPreferenceValue("startDate", null, preferences, "start-date-picker-", viewIdentifier);
		setPreferenceValue("endDate", null, preferences, "end-date-picker-", viewIdentifier);
		setPreferenceValue("endDate", null, preferences, "end-date-picker-", viewIdentifier);
		setPreferenceValue("decisionKnowledgeShown", "flag", preferences, "is-decision-knowledge-only-input-", viewIdentifier);
		setPreferenceValue("testCodeShown", "flag", preferences, "is-test-code-input-", viewIdentifier);
		setPreferenceValue("incompleteKnowledgeShown", "flag", preferences, "is-incomplete-knowledge-input-", viewIdentifier);
		setPreferenceValue("transitiveLinksShown", "flag", preferences, "is-transitive-links-input-", viewIdentifier);

		return preferences;
	}

	function setPreferences(preferences, viewIdentifier) {
		setSourceKnowledgeTypes(preferences["projectKey"], viewIdentifier);
		setKnowledgeTypes(preferences["projectKey"], viewIdentifier);
		setDocumentationLocations(viewIdentifier);
		setKnowledgeStatus(viewIdentifier);
		setLinkTypes(viewIdentifier);
		setDecisionGroups(preferences["projectKey"], viewIdentifier);

		setPreference("projectKey", null, preferences, "project-dropdown-", viewIdentifier);
		setPreference("sourceKnowledgeTypes", "list", preferences, "source-knowledgetype-multi-select-", viewIdentifier);
		setPreference("minimumDecisionCoverage", null, preferences, "minimum-number-of-decisions-input-", viewIdentifier);
		setPreference("maximumLinkDistance", null, preferences, "link-distance-to-decision-number-input-", viewIdentifier);
		setPreference("knowledgeTypes", "list", preferences, "knowledgetype-multi-select-", viewIdentifier);
		setPreference("documentationLocations", "list", preferences, "documentationlocation-multi-select-", viewIdentifier);
		setPreference("knowledgeStatus", "list", preferences, "knowledgestatus-multi-select-", viewIdentifier);
		setPreference("linkTypes", "list", preferences, "linktype-multi-select-", viewIdentifier);
		setPreference("decisionGroups", "list", preferences, "decisiongroup-multi-select-", viewIdentifier);
		setPreference("linkDistance", null, preferences, "link-distance-input-", viewIdentifier);
		setPreference("minDegree", null, preferences, "min-degree-input-", viewIdentifier);
		setPreference("maxDegree", null, preferences, "max-degree-input-", viewIdentifier);
		setPreference("startDate", null, preferences, "start-date-picker-", viewIdentifier);
		setPreference("endDate", null, preferences, "end-date-picker-", viewIdentifier);
		setPreference("decisionKnowledgeShown", "flag", preferences, "is-decision-knowledge-only-input-", viewIdentifier);
		setPreference("testCodeShown", "flag", preferences, "is-test-code-input-", viewIdentifier);
		setPreference("incompleteKnowledgeShown", "flag", preferences, "is-test-code-input-", viewIdentifier);
		setPreference("transitiveLinksShown", "flag", preferences, "is-transitive-links-input-", viewIdentifier);
	}

	function getFilterSettings(preferences) {
		var filterSettings = {};

		filterSettings.searchTerm = "";
		filterSettings.definitionOfDone = {};

		setFilterSetting("projectKey", "projectKey", null, filterSettings, preferences);
		setFilterSetting("minimumDecisionsWithinLinkDistance", "minimumDecisionCoverage", "DoD", filterSettings, preferences);
		setFilterSetting("maximumLinkDistanceToDecisions", "maximumLinkDistance", "DoD", filterSettings, preferences);
		setFilterSetting("knowledgeTypes", "knowledgeTypes", "list", filterSettings, preferences);
		setFilterSetting("documentationLocations", "documentationLocations", "list", filterSettings, preferences);
		setFilterSetting("status", "knowledgeStatus", "list", filterSettings, preferences);
		setFilterSetting("linkTypes", "linkTypes", "list", filterSettings, preferences);
		setFilterSetting("groups", "decisionGroups", "list", filterSettings, preferences);
		setFilterSetting("linkDistance", "linkDistance", null, filterSettings, preferences);
		setFilterSetting("minDegree", "minDegree", null, filterSettings, preferences);
		setFilterSetting("maxDegree", "maxDegree", null, filterSettings, preferences);
		setFilterSetting("startDate", "startDate", "date", filterSettings, preferences);
		setFilterSetting("endDate", "endDate", "date", filterSettings, preferences);
		setFilterSetting("isOnlyDecisionKnowledgeShown", "decisionKnowledgeShown", "list", filterSettings, preferences);
		setFilterSetting("isTestCodeShown", "testCodeShown", "list", filterSettings, preferences);
		setFilterSetting("isIncompleteKnowledgeShown", "incompleteKnowledgeShown", "list", filterSettings, preferences);
		setFilterSetting("createTransitiveLinks", "transitiveLinksShown", "list", filterSettings, preferences);

		return filterSettings;
	}

	function setPreferenceValue(key, type, preferences, nodeId, viewIdentifier) {
		var node = document.getElementById(nodeId + viewIdentifier);
		if (node) {
			if (type === "flag") {
				preferences[key] = node.checked;
			} else if (type === "list") {
				preferences[key] = getSelectedValues(node);
			} else {
				preferences[key] = node.value;
			}
		}
	}

	function setPreference(key, type, preferences, nodeId, viewIdentifier) {
		var node = document.getElementById(nodeId + viewIdentifier);
		if (preferences[key]) {
			if (type === "flag") {
				node.checked = preferences[key];
			} else if (type === "list") {
				setSelectedValues(node, preferences[key])
			} else {
				node.value = preferences[key];
			}
		}
	}

	function setFilterSetting(filterSettingKey, preferencesKey, type, filterSettings, preferences) {
		if (type === "list") {
			var list = getList(preferences[preferencesKey]);
			if (list && Array.isArray(list) && list.length) {
				filterSettings[filterSettingKey] = list;
			}
		} else if (type === "date") {
			if (preferences[preferencesKey]) {
				filterSettings[filterSettingKey] = new Date(preferences[preferencesKey]).getTime();
			}
		} else if (type === "DoD") {
			if (preferences[preferencesKey]) {
				filterSettings["definitionOfDone"][filterSettingKey] = preferences[preferencesKey];
			}
		} else {
			if (preferences[preferencesKey]) {
				filterSettings[filterSettingKey] = preferences[preferencesKey];
			}
		}
	}

	function setSourceKnowledgeTypes(projectKey, viewIdentifier) {
		if (projectKey) {
			var sourceKnowledgeTypeSelection = document.getElementById("source-knowledgetype-multi-select-" + viewIdentifier);
			conDecAPI.projectKey = projectKey;
			var sourceKnowledgeTypes = conDecAPI.getKnowledgeTypes();
			setMultiSelection(sourceKnowledgeTypeSelection, sourceKnowledgeTypes);
		}
	}

	function setKnowledgeTypes(projectKey, viewIdentifier) {
		if (projectKey) {
			var knowledgeTypeSelection = document.getElementById("knowledgetype-multi-select-" + viewIdentifier);
			conDecAPI.projectKey = projectKey;
			var knowledgeTypes = conDecAPI.getKnowledgeTypes();
			setMultiSelection(knowledgeTypeSelection, knowledgeTypes);
		}
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
		if (projectKey) {
			var decisionGroupSelection = document.getElementById("decisiongroup-multi-select-" + viewIdentifier);
			var decisionGroups = conDecAPI.getAllDecisionGroups();
			setMultiSelection(decisionGroupSelection, decisionGroups);
		}
	}

	function setMultiSelection(selectionElement, options) {
		if (selectionElement) {
			removeOptions(selectionElement);

			for (var i = 0; i < options.length; i++) {
				var option = document.createElement("option");
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
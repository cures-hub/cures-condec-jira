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
	};

	ConDecDashboard.prototype.initConfiguration = function (viewIdentifier, dashboardAPI, preferences) {
		dashboardAPI.once("afterRender",
			function() {
				createConfiguration(viewIdentifier, dashboardAPI, preferences);
			});
	};

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

		createSaveButton(dashboardAPI, viewIdentifier);
		createCancelButton(preferences, dashboardAPI, viewIdentifier);
		createListener(viewIdentifier);
		setPreferences(preferences, viewIdentifier);

		dashboardAPI.resize();
	}

	function createSaveButton(dashboardAPI, viewIdentifier) {
		function onSaveButton() {
			var preferences = getPreferences(viewIdentifier);

			if (preferences["projectKey"]) {
				dashboardAPI.savePreferences(preferences);
			}

			dashboardAPI.resize();
		}

		clearListener("save-button-", viewIdentifier);

		document.getElementById("save-button-" + viewIdentifier).addEventListener("click", onSaveButton);
	}

	function createCancelButton(preferences, dashboardAPI, viewIdentifier) {
		function onCancelButton() {
			if (preferences["projectKey"]) {
				showDashboardSection("condec-dashboard-contents-container-", viewIdentifier);
			}

			dashboardAPI.resize();
		}

		clearListener("cancel-button-", viewIdentifier);

		document.getElementById("cancel-button-" + viewIdentifier).addEventListener("click", onCancelButton);
	}

	function createListener(viewIdentifier) {
		function onSelectProject() {
			var projectKey = document.getElementById("project-dropdown-" + viewIdentifier).value;
			if (projectKey) {
				conDecAPI.projectKey = projectKey;
				conDecFiltering.fillDropdownMenus(viewIdentifier);
				conDecFiltering.fillMinimumCoverageAndMaximumLinkDistance(viewIdentifier, projectKey);
			}
		}

		clearListener("project-dropdown-", viewIdentifier);

		document.getElementById("project-dropdown-" + viewIdentifier).addEventListener("change", onSelectProject);
	}

	function clearListener(elementId, viewIdentifier) {
		var element = document.getElementById(elementId + viewIdentifier);
		element.replaceWith(element.cloneNode(true));
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
		document.getElementById("condec-dashboard-selected-project-" + viewIdentifier).innerText = filterSettings.projectKey;

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
	};

	function getPreferences(viewIdentifier) {
		var preferences = {};

		setPreferenceValue("projectKey", null, preferences, "project-dropdown-", viewIdentifier);
		setPreferenceValue("sourceKnowledgeTypes", "list", preferences, "source-knowledge-type-dropdown-", viewIdentifier);
		setPreferenceValue("minimumDecisionCoverage", null, preferences, "minimum-number-of-decisions-input-", viewIdentifier);
		setPreferenceValue("maximumLinkDistance", null, preferences, "link-distance-to-decision-number-input-", viewIdentifier);
		setPreferenceValue("searchTerm", null, preferences, "search-input-", viewIdentifier);
		setPreferenceValue("knowledgeTypes", "list", preferences, "knowledge-type-dropdown-", viewIdentifier);
		setPreferenceValue("knowledgeStatus", "list", preferences, "status-dropdown-", viewIdentifier);
		setPreferenceValue("documentationLocations", "list", preferences, "documentation-location-dropdown-", viewIdentifier);
		setPreferenceValue("linkTypes", "list", preferences, "link-type-dropdown-", viewIdentifier);
		setPreferenceValue("decisionGroups", "list", preferences, "decision-group-dropdown-", viewIdentifier);
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

	function setPreferenceValue(key, type, preferences, nodeId, viewIdentifier) {
		var node = document.getElementById(nodeId + viewIdentifier);
		if (node) {
			if (type === "flag") {
				preferences[key] = node.checked;
			} else if (type === "list") {
				preferences[key] = conDecFiltering.getSelectedItems(nodeId + viewIdentifier);
			} else {
				preferences[key] = node.value;
			}
		}
	}

	function setPreferences(preferences, viewIdentifier) {
		var projectKey = preferences["projectKey"];
		if (projectKey) {
			conDecAPI.projectKey = projectKey;
		}
		setPreference("projectKey", null, preferences, null, "project-dropdown-", viewIdentifier);
		setPreference("sourceKnowledgeTypes", "list", preferences, conDecAPI.getKnowledgeTypes(), "source-knowledge-type-dropdown-", viewIdentifier);
		setPreference("minimumDecisionCoverage", null, preferences, null, "minimum-number-of-decisions-input-", viewIdentifier);
		setPreference("maximumLinkDistance", null, preferences, null, "link-distance-to-decision-number-input-", viewIdentifier);
		setPreference("searchTerm", null, preferences, null, "search-input-", viewIdentifier);
		setPreference("knowledgeTypes", "list", preferences, conDecAPI.getKnowledgeTypes(), "knowledge-type-dropdown-", viewIdentifier);
		setPreference("knowledgeStatus", "list", preferences, conDecAPI.knowledgeStatus, "status-dropdown-", viewIdentifier);
		setPreference("documentationLocations", "list", preferences, conDecAPI.documentationLocations, "documentation-location-dropdown-", viewIdentifier);
		setPreference("linkTypes", "list", preferences, conDecAPI.getLinkTypes(), "link-type-dropdown-", viewIdentifier);
		setPreference("decisionGroups", "list", preferences, conDecAPI.getAllDecisionGroups(), "decision-group-dropdown-", viewIdentifier);
		setPreference("linkDistance", null, preferences, null, "link-distance-input-", viewIdentifier);
		setPreference("minDegree", null, preferences, null, "min-degree-input-", viewIdentifier);
		setPreference("maxDegree", null, preferences, null, "max-degree-input-", viewIdentifier);
		setPreference("startDate", null, preferences, null, "start-date-picker-", viewIdentifier);
		setPreference("endDate", null, preferences, null, "end-date-picker-", viewIdentifier);
		setPreference("decisionKnowledgeShown", "flag", preferences, null, "is-decision-knowledge-only-input-", viewIdentifier);
		setPreference("testCodeShown", "flag", preferences, null, "is-test-code-input-", viewIdentifier);
		setPreference("incompleteKnowledgeShown", "flag", preferences, null, "is-test-code-input-", viewIdentifier);
		setPreference("transitiveLinksShown", "flag", preferences, null, "is-transitive-links-input-", viewIdentifier);
	}

	function setPreference(key, type, preferences, items, nodeId, viewIdentifier) {
		var node = document.getElementById(nodeId + viewIdentifier);
		if (type === "flag") {
			if (preferences[key]) {
				node.checked = preferences[key];
			}
		} else if (type === "list") {
			if (preferences["projectKey"]) {
				conDecAPI.projectKey = preferences["projectKey"];
				conDecFiltering.initDropdown(nodeId + viewIdentifier, items, preferences[key]);
			}
		} else {
			if (preferences[key]) {
				node.value = preferences[key];
			}
		}
	}

	function getFilterSettings(preferences) {
		var filterSettings = {};
		filterSettings.definitionOfDone = {};

		setFilterSetting("projectKey", "projectKey", null, filterSettings, preferences);
		setFilterSetting("minimumDecisionsWithinLinkDistance", "minimumDecisionCoverage", "DoD", filterSettings, preferences);
		setFilterSetting("maximumLinkDistanceToDecisions", "maximumLinkDistance", "DoD", filterSettings, preferences);
		setFilterSetting("searchTerm", "searchTerm", null, filterSettings, preferences);
		setFilterSetting("knowledgeTypes", "knowledgeTypes", "list", filterSettings, preferences);
		setFilterSetting("status", "knowledgeStatus", "list", filterSettings, preferences);
		setFilterSetting("documentationLocations", "documentationLocations", "list", filterSettings, preferences);
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
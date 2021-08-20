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

	/**
	 * Initializes a dashboard.
	 * Automatically called when a dashboard renders its content or
	 * when the dashboard loads with set preferences.
	 * 
	 * external references: condec.general.metrics.dashboard.configuration.js
	 * condec.git.branches.dashboard.configuration.js, 
	 * condec.rationale.completeness.dashboard.configuration.js, 
	 * condec.rationale.coverage.dashboard.configuration.js
	 * 
	 * @param dashboard reference to the current dashboard
	 * @param viewIdentifier identifies the html elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param preferences the options set in the dashboard configuration
	 */
	ConDecDashboard.prototype.initRender = function (dashboard, viewIdentifier, dashboardAPI, preferences) {
		dashboardAPI.once("afterRender",
			function() {
				if (preferences["projectKey"]) {
					createRender(dashboard, viewIdentifier, dashboardAPI, preferences);
				}
			});
	};

	/**
	 * Initializes a dashboard configuration screen. 
	 * Automatically when the dashboard edit function is selected or
	 * when the dashboard loads without set preferences.
	 * 
	 * external references: condec.general.metrics.dashboard.configuration.js
	 * condec.git.branches.dashboard.configuration.js,
	 * condec.rationale.completeness.dashboard.configuration.js,
	 * condec.rationale.coverage.dashboard.configuration.js
	 * 
	 * @param viewIdentifier identifies the html elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param preferences the options set in the dashboard configuration
	 */
	ConDecDashboard.prototype.initConfiguration = function (viewIdentifier, dashboardAPI, preferences) {
		dashboardAPI.once("afterRender",
			function() {
				createConfiguration(viewIdentifier, dashboardAPI, preferences);
			});
	};

	/**
	 * Gets the filterSettings from the preferences and gets the data
	 * from the server.
	 *
	 * @param dashboard reference to the current dashboard
	 * @param viewIdentifier identifies the html elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param preferences the options set in the dashboard configuration
	 */
	function createRender(dashboard, viewIdentifier, dashboardAPI, preferences) {
		dashboardAPI.showLoadingBar();

		var sourceKnowledgeTypes = "";
		if (preferences["sourceKnowledgeTypes"]) {
			sourceKnowledgeTypes = preferences["sourceKnowledgeTypes"];
		}
		var filterSettings = getFilterSettings(preferences);
		getData(dashboard, viewIdentifier, dashboardAPI, filterSettings, sourceKnowledgeTypes);

		dashboardAPI.resize();
	}

	/**
	 * Gets the data to fill the dashboard plots by calling the getData()-method
	 * of the specified dashboard.
	 *
	 * @param dashboard reference to the current dashboard
	 * @param viewIdentifier identifies the html elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the filterSettings used for the REST-call
	 * @param sourceKnowledgeTypes the sourceKnowledgeTypes used for the REST-call
	 *                             (optional, only used for the RationaleCoverageDashboard)
	 */
	function getData(dashboard, viewIdentifier, dashboardAPI, filterSettings, sourceKnowledgeTypes) {
		if (!filterSettings.projectKey || !filterSettings.projectKey.length) {
			return;
		}

		document.getElementById("condec-dashboard-selected-project-" + viewIdentifier).innerText = filterSettings.projectKey;

		dashboard.getData(dashboardAPI, filterSettings, sourceKnowledgeTypes);
	}

	/**
	 * Process the data that was returned from a REST-call.
	 *
	 * external references: condec.general.metrics.dashboard.configuration.js
	 * condec.git.branches.dashboard.configuration.js,
	 * condec.rationale.completeness.dashboard.configuration.js,
	 * condec.rationale.coverage.dashboard.configuration.js
	 *
	 * @param error the error message returned in the REST-call
	 *              null if no error occurred
	 * @param result the result of the REST-call
	 * @param dashboard reference to the current dashboard
	 * @param viewIdentifier identifies the html elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the filterSettings used in the REST-call
	 */
	ConDecDashboard.prototype.processData = function (error, result, dashboard, viewIdentifier, dashboardAPI, filterSettings) {
		dashboardAPI.hideLoadingBar();
		if (error) {
			showDashboardSection("condec-dashboard-contents-data-error-", viewIdentifier);
		} else {
			showDashboardSection("condec-dashboard-contents-container-", viewIdentifier);
			dashboard.renderData(result, filterSettings);
		}

		dashboardAPI.resize();
	};

	/**
	 * Sets up the dashboard configuration screen and fills it 
	 * from the preferences.
	 *
	 * @param viewIdentifier identifies the html elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param preferences the options set in the dashboard configuration
	 */
	function createConfiguration(viewIdentifier, dashboardAPI, preferences) {
		showDashboardSection("condec-dashboard-config-", viewIdentifier);

		createSaveButton(dashboardAPI, viewIdentifier);
		createCancelButton(preferences, dashboardAPI, viewIdentifier);
		createListener(viewIdentifier);
		setPreferences(preferences, viewIdentifier);

		dashboardAPI.resize();
	}

	/**
	 * Creates the save button and adds a listener to it.
	 * When the save button is pressed and the projectKey is set
	 * the dashboard renders its content.
	 *
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param viewIdentifier identifies the html elements of the dashboard
	 */
	function createSaveButton(dashboardAPI, viewIdentifier) {
		function onSaveButton() {
			var preferences = getPreferences(viewIdentifier);

			if (preferences["projectKey"]) {
				dashboardAPI.savePreferences(preferences);
			}

			dashboardAPI.resize();
		}

		clearListener("save-button-" + viewIdentifier);

		document.getElementById("save-button-" + viewIdentifier).addEventListener("click", onSaveButton);
	}

	/**
	 * Creates the save button and adds a listener to it.
	 * When the cancel button is pressed and projectKey is set
	 * it opens the already rendered content view without
	 * recalculating it
	 *
	 * @param preferences the options set in the dashboard configuration
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param viewIdentifier identifies the html elements of the dashboard
	 */
	function createCancelButton(preferences, dashboardAPI, viewIdentifier) {
		function onCancelButton() {
			if (preferences["projectKey"]) {
				showDashboardSection("condec-dashboard-contents-container-", viewIdentifier);
			}

			dashboardAPI.resize();
		}

		clearListener("cancel-button-" + viewIdentifier);

		document.getElementById("cancel-button-" + viewIdentifier).addEventListener("click", onCancelButton);
	}

	/**
	 * Creates a listener and adds it to the projectKey-dropdown.
	 * If a project is selected, further filter elements can be filled.
	 *
	 * @param viewIdentifier identifies the html elements of the dashboard
	 */
	function createListener(viewIdentifier) {
		function onSelectProject() {
			var projectKey = document.getElementById("project-dropdown-" + viewIdentifier).value;
			if (projectKey) {
				conDecAPI.projectKey = projectKey;
				conDecFiltering.fillDropdownMenus(viewIdentifier);
				conDecFiltering.fillMinimumCoverageAndMaximumLinkDistance(viewIdentifier, projectKey);
			}
		}

		clearListener("project-dropdown-" + viewIdentifier);

		document.getElementById("project-dropdown-" + viewIdentifier).addEventListener("change", onSelectProject);
	}

	/**
	 * Clears all listeners from a html element.
	 * Used to prevent multiple listeners doing the same thing
	 * being added to an element every time the edit view is opened.
	 *
	 * @param elementId the id of the element from which the listeners 
	 *                  should be cleared
	 */
	function clearListener(elementId) {
		var element = document.getElementById(elementId);
		if (element) {
			element.replaceWith(element.cloneNode(true));
		}
	}

	/**
	 * Shown the specified html element.
	 * All other html elements are hidden.
	 *
	 * @param elementId the id of the element (without viewIdentifier) 
	 *                  that should be shown
	 * @param viewIdentifier identifies the html elements of the dashboard
	 */
	function showDashboardSection(elementId, viewIdentifier) {
		var hiddenClass = "hidden";
		document.getElementById("condec-dashboard-config-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById("condec-dashboard-contents-container-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById("condec-dashboard-contents-data-error-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById("condec-dashboard-no-project-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById(elementId + viewIdentifier).classList.remove(hiddenClass);
	}

	/**
	 * Return the preferences set in the html filter elements.
	 * 
	 * @param viewIdentifier identifies the html elements of the dashboard
	 */
	function getPreferences(viewIdentifier) {
		var preferences = {};

		setPreferenceValue("projectKey", null, preferences, "project-dropdown-" + viewIdentifier);
		setPreferenceValue("sourceKnowledgeTypes", "list", preferences, "source-knowledge-type-dropdown-" + viewIdentifier);
		setPreferenceValue("minimumDecisionCoverage", null, preferences, "minimum-number-of-decisions-input-" + viewIdentifier);
		setPreferenceValue("maximumLinkDistance", null, preferences, "link-distance-to-decision-number-input-" + viewIdentifier);
		setPreferenceValue("searchTerm", null, preferences, "search-input-" + viewIdentifier);
		setPreferenceValue("knowledgeTypes", "list", preferences, "knowledge-type-dropdown-" + viewIdentifier);
		setPreferenceValue("knowledgeStatus", "list", preferences, "status-dropdown-" + viewIdentifier);
		setPreferenceValue("documentationLocations", "list", preferences, "documentation-location-dropdown-" + viewIdentifier);
		setPreferenceValue("linkTypes", "list", preferences, "link-type-dropdown-" + viewIdentifier);
		setPreferenceValue("decisionGroups", "list", preferences, "decision-group-dropdown-" + viewIdentifier);
		setPreferenceValue("linkDistance", null, preferences, "link-distance-input-" + viewIdentifier);
		setPreferenceValue("minDegree", null, preferences, "min-degree-input-" + viewIdentifier);
		setPreferenceValue("maxDegree", null, preferences, "max-degree-input-" + viewIdentifier);
		setPreferenceValue("startDate", null, preferences, "start-date-picker-" + viewIdentifier);
		setPreferenceValue("endDate", null, preferences, "end-date-picker-" + viewIdentifier);
		setPreferenceValue("endDate", null, preferences, "end-date-picker-" + viewIdentifier);
		setPreferenceValue("decisionKnowledgeShown", "flag", preferences, "is-decision-knowledge-only-input-" + viewIdentifier);
		setPreferenceValue("testCodeShown", "flag", preferences, "is-test-code-input-" + viewIdentifier);
		setPreferenceValue("incompleteKnowledgeShown", "flag", preferences, "is-incomplete-knowledge-input-" + viewIdentifier);
		setPreferenceValue("transitiveLinksShown", "flag", preferences, "is-transitive-links-input-" + viewIdentifier);

		return preferences;
	}

	/**
	 * Read the option set in a html filter element and 
	 * set it in the preferences.
	 *
	 * @param key the key under which the options should be saved 
	 *            in the preferences
	 * @param type the type of the options read out, 
	 *             so it can be saved in the right way
	 *             possible options are "flag" and "list"
	 *             if null just save the value as is
	 * @param preferences the preferences the options should be saved in
	 * @param elementId the id of the filter element that should be read
	 */
	function setPreferenceValue(key, type, preferences, elementId) {
		var node = document.getElementById(elementId);
		if (node) {
			if (type === "flag") {
				preferences[key] = node.checked;
			} else if (type === "list") {
				preferences[key] = conDecFiltering.getSelectedItems(elementId);
			} else {
				preferences[key] = node.value;
			}
		}
	}

	/**
	 * Set the options of the html filter elements from the preferences.
	 *
	 * @param preferences stores the filter options
	 * @param viewIdentifier identifies the html elements of the dashboard
	 */
	function setPreferences(preferences, viewIdentifier) {
		var projectKey = preferences["projectKey"];
		if (projectKey) {
			conDecAPI.projectKey = projectKey;
		}
		setPreference("projectKey", null, preferences, null, "project-dropdown-" + viewIdentifier);
		setPreference("sourceKnowledgeTypes", "list", preferences, conDecAPI.getKnowledgeTypes(), "source-knowledge-type-dropdown-" + viewIdentifier);
		setPreference("minimumDecisionCoverage", null, preferences, null, "minimum-number-of-decisions-input-" + viewIdentifier);
		setPreference("maximumLinkDistance", null, preferences, null, "link-distance-to-decision-number-input-" + viewIdentifier);
		setPreference("searchTerm", null, preferences, null, "search-input-" + viewIdentifier);
		setPreference("knowledgeTypes", "list", preferences, conDecAPI.getKnowledgeTypes(), "knowledge-type-dropdown-" + viewIdentifier);
		setPreference("knowledgeStatus", "list", preferences, conDecAPI.knowledgeStatus, "status-dropdown-" + viewIdentifier);
		setPreference("documentationLocations", "list", preferences, conDecAPI.documentationLocations, "documentation-location-dropdown-" + viewIdentifier);
		setPreference("linkTypes", "list", preferences, conDecAPI.getLinkTypes(), "link-type-dropdown-" + viewIdentifier);
		setPreference("decisionGroups", "list", preferences, conDecAPI.getAllDecisionGroups(), "decision-group-dropdown-" + viewIdentifier);
		setPreference("linkDistance", null, preferences, null, "link-distance-input-" + viewIdentifier);
		setPreference("minDegree", null, preferences, null, "min-degree-input-" + viewIdentifier);
		setPreference("maxDegree", null, preferences, null, "max-degree-input-" + viewIdentifier);
		setPreference("startDate", null, preferences, null, "start-date-picker-" + viewIdentifier);
		setPreference("endDate", null, preferences, null, "end-date-picker-" + viewIdentifier);
		setPreference("decisionKnowledgeShown", "flag", preferences, null, "is-decision-knowledge-only-input-" + viewIdentifier);
		setPreference("testCodeShown", "flag", preferences, null, "is-test-code-input-" + viewIdentifier);
		setPreference("incompleteKnowledgeShown", "flag", preferences, null, "is-test-code-input-" + viewIdentifier);
		setPreference("transitiveLinksShown", "flag", preferences, null, "is-transitive-links-input-" + viewIdentifier);
	}

	/**
	 * Read the option set in the preferences and
	 * set it in the html filter elements.
	 *
	 * @param key the key under which the options are saved
	 *            in the preferences
	 * @param type the type of the options saved,
	 *             so it can be set in the right way
	 *             possible options are "flag" and "list"
	 *             if null just save the value as is
	 * @param preferences the preferences the options are saved in
	 * @param items the list of items that a dropdown menu should contain
	 *              only necessary if type is "list" else can be null
	 * @param elementId the id of the filter element that should be set
	 */
	function setPreference(key, type, preferences, items, elementId) {
		var node = document.getElementById(elementId);
		if (type === "flag") {
			if (preferences[key]) {
				node.checked = preferences[key];
			}
		} else if (type === "list") {
			if (preferences["projectKey"]) {
				conDecAPI.projectKey = preferences["projectKey"];
				conDecFiltering.initDropdown(elementId, items, preferences[key]);
			}
		} else {
			if (preferences[key]) {
				node.value = preferences[key];
			}
		}
	}

	/**
	 * Converts the options stored in the preferences for persistence
	 * into filterSettings used for REST-calls.
	 * Necessary because the the data persisting in the preferences
	 * must is saved slightly different than in the filterSettings
	 * for some data types (like lists, dates or the DoD).
	 *
	 * @param preferences stores the filter options
	 */
	function getFilterSettings(preferences) {
		var filterSettings = {};
		filterSettings.definitionOfDone = {};
		filterSettings.searchTerm = "";

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

	/**
	 * Read the option set in the preferences and
	 * set it in the filterSettings.
	 *
	 * @param filterSettingKey the key under which the options should be saved
	 *                         in the filterSettings
	 * @param preferencesKey the key under which the options are saved
	 *                         in the preferences
	 * @param type the type of the options converted,
	 *             so it can be set in the right way
	 *             possible options are "list", "date" and "DoD"
	 *             if null just save the value as is
	 * @param filterSettings the filterSettings the options should
	 *                       be saved in
	 * @param preferences the preferences the options are saved in
	 *              only necessary if type is "list" else can be null
	 */
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

	/**
	 * Convert a string to a list.
	 * Necessary because lists persists in the preferences
	 * as a string.
	 *
	 * @param string the string to be converted
	 */
	function getList(string) {
		if (!string || !string.length) {
			return null;
		}

		if (Array.isArray(string)) {
			return string;
		}

		string = string.replace("\[", "").replace("\]", "");
		string = string.replaceAll("\"", "");

		return string.split(",");
	}

	global.conDecDashboard = new ConDecDashboard();
})(window);
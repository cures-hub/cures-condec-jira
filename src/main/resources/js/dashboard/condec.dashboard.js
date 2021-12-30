/**
 This module contains methods used to render the dashboards and their configuration screens.

 Is referenced by
 * condec.general.metrics.dashboard.js
 * condec.git.branches.dashboard.js
 * condec.rationale.completeness.dashboard.js
 * condec.rationale.coverage.dashboard.js
 */
(function(global) {
	var ConDecDashboard = function ConDecDashboard() {
		console.log("ConDecDashboard constructor");
	};

	/**
	 * Initializes a dashboard.
	 * Automatically called when a dashboard renders its content or
	 * when the dashboard loads with set filterSettings.
	 * 
	 * external references: condec.general.metrics.dashboard.configuration.js
	 * condec.git.branches.dashboard.configuration.js, 
	 * condec.rationale.completeness.dashboard.configuration.js, 
	 * condec.rationale.coverage.dashboard.configuration.js
	 * 
	 * @param dashboard reference to the current dashboard
	 * @param viewIdentifier identifies the html elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the options set in the dashboard configuration
	 */
	ConDecDashboard.prototype.initRender = function(dashboard, viewIdentifier, dashboardAPI, filterSettings) {
		dashboardAPI.once("afterRender",
			function() {
				// the following splitting is necessary because dashboardAPI.savePreferences(filterSettings)
				// saves lists as strings and cannot save objects such as definitionOfDone
				filterSettings.knowledgeTypes = filterSettings["knowledgeTypes"].split(',');
				filterSettings.linkTypes = filterSettings["linkTypes"].split(',');
				filterSettings.status = filterSettings["status"].split(',');
				filterSettings.documentationLocations = filterSettings["documentationLocations"].split(',');
				if (filterSettings.groups.length > 0) {
					filterSettings.groups = filterSettings["groups"].split(',');
				} else {
					filterSettings.groups = [];
				}
				//filterSettings.sourceKnowledgeTypes = filterSettings["sourceKnowledgeTypes"].split(',');
				filterSettings.changeImpactAnalysisConfig = {};
				filterSettings.definitionOfDone = {
					"minimumDecisionsWithinLinkDistance": filterSettings.minimumDecisionsWithinLinkDistance,
					"maximumLinkDistanceToDecisions": filterSettings.maximumLinkDistanceToDecisions
				};
				if (filterSettings["projectKey"]) {
					createRender(dashboard, viewIdentifier, dashboardAPI, filterSettings);
				}
			});
	};

	/**
	 * Initializes a dashboard configuration screen. 
	 * Automatically when the dashboard edit function is selected or
	 * when the dashboard loads without set filterSettings.
	 * 
	 * external references: condec.general.metrics.dashboard.configuration.js
	 * condec.git.branches.dashboard.configuration.js,
	 * condec.rationale.completeness.dashboard.configuration.js,
	 * condec.rationale.coverage.dashboard.configuration.js
	 * 
	 * @param viewIdentifier identifies the html elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the options set in the dashboard configuration
	 */
	ConDecDashboard.prototype.initConfiguration = function(viewIdentifier, dashboardAPI, filterSettings) {
		dashboardAPI.once("afterRender",
			function() {
				createConfiguration(viewIdentifier, dashboardAPI, filterSettings);
			});
	};

	/**
	 * Gets the filterSettings from the filterSettings and gets the data
	 * from the server.
	 *
	 * @param dashboard reference to the current dashboard
	 * @param viewIdentifier identifies the html elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the options set in the dashboard configuration
	 */
	function createRender(dashboard, viewIdentifier, dashboardAPI, filterSettings) {
		dashboardAPI.showLoadingBar();

		var sourceKnowledgeTypes = "";
		if (filterSettings["sourceKnowledgeTypes"]) {
			sourceKnowledgeTypes = filterSettings["sourceKnowledgeTypes"];
		}
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
	ConDecDashboard.prototype.processData = function(error, result, dashboard, viewIdentifier, dashboardAPI, filterSettings) {
		dashboardAPI.hideLoadingBar();
		if (error) {
			showDashboardSection("condec-dashboard-contents-data-error-", viewIdentifier);
			console.log(error);
		} else {
			showDashboardSection("condec-dashboard-contents-container-", viewIdentifier);
			dashboard.renderData(result, filterSettings);
		}

		dashboardAPI.resize();
	};

	/**
	 * Sets up the dashboard configuration screen and fills it from the filterSettings.
	 *
	 * @param viewIdentifier identifies the html elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the options set in the dashboard configuration
	 */
	function createConfiguration(viewIdentifier, dashboardAPI, filterSettings) {
		showDashboardSection("condec-dashboard-config-", viewIdentifier);

		createSaveButton(dashboardAPI, viewIdentifier);
		createCancelButton(filterSettings, dashboardAPI, viewIdentifier);
		createListener(viewIdentifier);
		setPreferences(filterSettings, viewIdentifier);

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
			var filterSettings = conDecFiltering.getFilterSettings(viewIdentifier);

			if (filterSettings["projectKey"]) {
				console.log("save");
				if (filterSettings["definitionOfDone"]) {
					// necessary since savePreferences cannot store objects
					Object.assign(filterSettings, filterSettings.definitionOfDone);
				}
				console.log(filterSettings);
				dashboardAPI.savePreferences(filterSettings);
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
	 * @param filterSettings the options set in the dashboard configuration
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param viewIdentifier identifies the html elements of the dashboard
	 */
	function createCancelButton(filterSettings, dashboardAPI, viewIdentifier) {
		function onCancelButton() {
			if (filterSettings["projectKey"]) {
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
	 * Set the options of the html filter elements from the preferences.
	 *
	 * @param preferences stores the filter options
	 * @param viewIdentifier identifies the html elements of the dashboard
	 */
	function setPreferences(filterSettings, viewIdentifier) {
		var projectKey = filterSettings["projectKey"];
		if (projectKey) {
			conDecAPI.projectKey = projectKey;
		}
		conDecFiltering.fillFilterElementsFromSettings(viewIdentifier, filterSettings);
	}

	global.conDecDashboard = new ConDecDashboard();
})(window);
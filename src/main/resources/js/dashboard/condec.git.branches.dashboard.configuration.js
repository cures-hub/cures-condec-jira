/*
 This module render the configuration screen used in the feature branch dashboard item.

 Requires
 * condec.requirements.dashboard.js
 * condec.git.branches.dashboard.js

 Is referenced in HTML by
 * dashboard/featureBranches.vm
 */
define('dashboard/branches', [], function () {
	var dashboardAPI;

	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var dashboardFilterProjectNode;
	var dashboardFilterSaveButton;
	var dashboardFilterCancelButton;

	var ConDecBranchesDashboardItem = function (API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecBranchesDashboardItem.prototype.render = function (context, preferences) {
		dashboardAPI.once("afterRender",
			function() {
				createRender(preferences);
			});
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecBranchesDashboardItem.prototype.renderEdit = function (context, preferences) {
		dashboardAPI.once("afterRender",
			function() {
				createConfiguration(preferences);
			});
	};

	function createRender(preferences) {
		var projectKey = preferences['projectKey'];

		var filterSettings = getFilterSettings(projectKey);

		conDecBranchesDashboard.init(filterSettings);

		dashboardAPI.resize();
	}

	function createConfiguration(preferences) {
		getHTMLNodes("condec-branch-dashboard-configproject"
			, "condec-branches-dashboard-contents-container"
			, "condec-branches-dashboard-contents-data-error"
			, "condec-branches-dashboard-no-project"
			, "condec-branches-dashboard-processing"
			, "condec-branches-dashboard-nogit-error"
			, "project-dropdown-branch"
			, "save-button-branch"
			, "cancel-button-branch");

		showDashboardSection(dashboardFilterNode);

		setPreferences(preferences);

		createSaveButton();

		createCancelButton(preferences);

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

	function getHTMLNodes(filterName, containerName, dataErrorName, noProjectName, processingName, noGitName,
						  projectName, saveButtonName, cancelButtonName) {
		dashboardFilterNode = document.getElementById(filterName);
		dashboardContentNode = document.getElementById(containerName);
		dashboardDataErrorNode = document.getElementById(dataErrorName);
		dashboardNoContentsNode = document.getElementById(noProjectName);
		dashboardProcessingNode = document.getElementById(processingName);
		dashboardProjectWithoutGit = document.getElementById(noGitName);

		dashboardFilterProjectNode = document.getElementById(projectName);
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
		dashboardProjectWithoutGit.classList.add(hiddenClass);
		node.classList.remove(hiddenClass);
	}

	function getPreferences() {
		var preferences = {};

		preferences['projectKey'] = dashboardFilterProjectNode.value;

		return preferences;
	}

	function setPreferences(preferences) {
		if (preferences['projectKey']) {
			dashboardFilterProjectNode.value = preferences['projectKey'];
		}
	}

	function getFilterSettings(projectKey) {
		var filterSettings = {};

		filterSettings.projectKey = projectKey;
		filterSettings.searchTerm = "";

		return JSON.stringify(filterSettings);
	}

	return ConDecBranchesDashboardItem;
});
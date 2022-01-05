/**
 * This module renders the dashboard and its configuration screen used in the feature branch dashboard item.
 *
 * Requires
 * condec.dashboard.js
 */
define('dashboard/branches', [], function() {
	var dashboardAPI;
	const viewId = "branch";

	var ConDecBranchesDashboardItem = function(API) {
		dashboardAPI = API;
	};

	/**
	 * Called to render the view for a fully configured dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecBranchesDashboardItem.prototype.render = function(context, preferences) {
		conDecDashboard.initDashboard(this, viewId, dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecBranchesDashboardItem.prototype.renderEdit = function(context, preferences) {
		conDecDashboard.initConfiguration(viewId, dashboardAPI, preferences);
	};

	/**
	 * Gets the data to fill the dashboard plots by making an API-call.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the filterSettings used for the API-call
	 */
	ConDecBranchesDashboardItem.prototype.getData = function(dashboardAPI, filterSettings) {
		let self = this;
		conDecDashboardAPI.getBranchMetrics(filterSettings, function(error, branchMetrics) {
			conDecDashboard.processData(error, branchMetrics, self, viewId, dashboardAPI);
		});
	};

	/**
	 * Render the dashboard plots.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param data the data returned from the API-call
	 * @param filterSettings the filterSettings used in the API-call
	 */
	ConDecBranchesDashboardItem.prototype.renderData = function(branchMetrics) {
		createPieChartWithListOfBranches(branchMetrics.branchStatusMap, "piechartRich-QualityStatusForBranches",
				"How many branches document rationale well?");
		createPieChartWithListOfBranches(branchMetrics.qualityProblemMap, "piechartRich-ProblemTypesInBranches",
				"Which documentation mistakes are most common?");
		createPieChartWithListOfBranches(branchMetrics.jiraIssueMap, "piechartRich-BranchesPerIssue",
				"How many branches do Jira tasks have?");
		
		/* render box-plots */
		createBoxPlot("boxplot-IssuesPerBranch", "#Issues in branches", branchMetrics.numberOfIssuesMap);
		createBoxPlot("boxplot-DecisionsPerBranch", "#Decisions in branches", branchMetrics.numberOfDecisionsMap);
		createBoxPlot("boxplot-AlternativesPerBranch", "#Alternatives in branches", branchMetrics.numberOfAlternativesMap);
		createBoxPlot("boxplot-ProsPerBranch", "#Pro-arguments in branches", branchMetrics.numberOfProsMap);
		createBoxPlot("boxplot-ConsPerBranch", "#Con-arguments in branches", branchMetrics.numberOfConsMap);
	};

	function createBoxPlot(divId, title, dataMap) {
		var boxplot = conDecDashboard.createBoxPlot(divId, title, dataMap);
		boxplot.on('click', function(param) {
			if (typeof param.seriesIndex != 'undefined') {
				var dialogContent = conDecDashboard.initDialog(viewId);
				console.log(param);
				var selectedValues = param.data;
				console.log(selectedValues);
				var selectedBranches = [];
				for (value of selectedValues) {
					if (dataMap.has(value)) {
						selectedBranches = selectedBranches.concat(dataMap.get(value));
					}
				}
				for (branch of new Set(selectedBranches)) {
					var link = createLinkToGitView(branch);
					dialogContent.appendChild(link);
				}
			}
		});
	}

	function createPieChartWithListOfBranches(metric, divId, title, colorPalette) {
		var pieChart = conDecDashboard.createPieChartWithList(metric, divId, title, colorPalette);
		pieChart.on('click', function(param) {
			if (typeof param.seriesIndex != 'undefined' && param.data.list) {
				var dialogContent = conDecDashboard.initDialog(viewId);
				for (branch of param.data.list) {
					var link = createLinkToGitView(branch);
					dialogContent.appendChild(link);
				}
			}
		});
	}

	function createLinkToGitView(branch) {
		var link = document.createElement("a");
		link.classList = "navigationLink";
		link.innerText = branch.name;
		link.addEventListener('click', function() {
			var newWindow = window.open(AJS.contextPath() + '/projects/' + conDecAPI.projectKey + '?selectedItem=decision-knowledge-page#menu-item-git',
				'_blank');
			var script = document.createElement('script');
			script.innerHTML = 'AJS.tabs.change(AJS.$("a[href=#git-tab]"))';
			newWindow.onload = function() {
				this.document.body.appendChild(script);
			};
		});
		return link;
	}

	return ConDecBranchesDashboardItem;
});
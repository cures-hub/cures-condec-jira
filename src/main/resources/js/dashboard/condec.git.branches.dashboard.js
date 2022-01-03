/**
 * This module renders the dashboard and its configuration screen used in the feature branch dashboard item.
 *
 * Requires
 * condec.dashboard.js
 */
define('dashboard/branches', [], function() {
	var dashboardAPI;
	var issueBranchKeyRegex = null;

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
		conDecDashboard.initDashboard(this, "branch", dashboardAPI, preferences);
	};

	/**
	 * Called to render the edit view for a dashboard item.
	 *
	 * @param context The surrounding <div/> context that this items should render into.
	 * @param preferences The user preferences saved for this dashboard item (e.g. filter id, number of results...)
	 */
	ConDecBranchesDashboardItem.prototype.renderEdit = function(context, preferences) {
		conDecDashboard.initConfiguration("branch", dashboardAPI, preferences);
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
		conDecBranchesDashboardItem = this;
		conDecAPI.projectKey = filterSettings.projectKey;
		conDecGitAPI.getDiffForProject(filterSettings.projectKey)
			.then(branches => {
				conDecDashboard.processData(null, branches, conDecBranchesDashboardItem, "branch",
					dashboardAPI, filterSettings);
			}).catch(error => {
				conDecDashboard.processData(error, null, conDecBranchesDashboardItem, "branch",
					dashboardAPI, filterSettings);
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
	ConDecBranchesDashboardItem.prototype.renderData = function(branches, filterSettings) {
		/*
		 * Match branch names either: starting with issue key followed by dot OR
		 * exactly the issue key
		 */
		issueBranchKeyRegex = RegExp("origin/(" + filterSettings.projectKey
			+ "-\\d+)\\.|origin/(" + filterSettings.projectKey + "-\\d+)$", "i");

		for (branch of branches) {
			branch.status = getBranchStatus(branch);
			branch.numIssues = countElementType("Issue", branch);
			branch.numDecisions = countElementType("Decision", branch);
			branch.numAlternatives = countElementType("Alternative", branch);
			branch.numPros = countElementType("Pro", branch);
			branch.numCons = countElementType("Con", branch);
		}
		/* sort lexicographically */
		branches = sortBranches(branches);
		/* render charts and plots */
		renderChartsAndPlots(branches);
	};

	function renderChartsAndPlots(branches) {
		/*  init data for charts */
		var statusesForBranchesData = new Map();
		statusesForBranchesData.set("Incorrect", []);
		statusesForBranchesData.set("Good", []);
		statusesForBranchesData.set("No rationale", []);
		for (branch of branches) {
			var statusOfBranch = branch.status;
			addValueToMap(statusesForBranchesData, statusOfBranch, branch);
		}

		var problemTypesOccurrence = new Map();
		for (branch of branches) {
			for (problem of branch.qualityProblems) {
				addValueToMap(problemTypesOccurrence, problem.explanation, branch);
			}
		}

		var branchesPerIssue = new Map();
		for (branch of branches) {
			var issueMatch = branch.name.match(issueBranchKeyRegex);
			var key = "";

			if (!issueMatch || (!issueMatch[1] && !issueMatch[2])) {
				key = "no Jira task";
			} else {
				if (issueMatch[1]) {
					key = issueMatch[1];
				}
				if (issueMatch[2]) {
					key = issueMatch[2];
				}
			}

			addValueToMap(branchesPerIssue, key, branch);
		}

		var issuesInBranches = new Map();
		var decisionsInBranches = new Map();
		var alternativesInBranches = new Map();
		var prosInBranches = new Map();
		var consInBranches = new Map();

		for (branch of branches) {
			addValueToMap(issuesInBranches, branch.numIssues, branch);
			addValueToMap(decisionsInBranches, branch.numDecisions, branch);
			addValueToMap(alternativesInBranches, branch.numAlternatives, branch);
			addValueToMap(prosInBranches, branch.numPros, branch);
			addValueToMap(consInBranches, branch.numCons, branch);
		}

		/* render pie-charts */
		createPieChart(statusesForBranchesData, "piechartRich-QualityStatusForBranches",
			"How many branches document rationale well?");
		createPieChart(problemTypesOccurrence, "piechartRich-ProblemTypesInBranches",
			"Which documentation mistakes are most common?");
		createPieChart(branchesPerIssue, "piechartRich-BranchesPerIssue",
			"How many branches do Jira tasks have?");

		/* render box-plots */
		createBoxPlot("boxplot-IssuesPerBranch", "#Issues in branches", issuesInBranches);
		createBoxPlot("boxplot-DecisionsPerBranch", "#Decisions in branches", decisionsInBranches);
		createBoxPlot("boxplot-AlternativesPerBranch", "#Alternatives in branches", alternativesInBranches);
		createBoxPlot("boxplot-ProsPerBranch", "#Pro-arguments in branches", prosInBranches);
		createBoxPlot("boxplot-ConsPerBranch", "#Con-arguments in branches", consInBranches);
	}

	function createBoxPlot(divId, title, dataMap) {
		var boxplot = conDecDashboard.createBoxPlot(divId, title, dataMap);
		boxplot.on('click', function(param) {
			if (typeof param.seriesIndex != 'undefined') {
				var navigationDialog = document.getElementById("navigate-dialog");
				AJS.dialog2(navigationDialog).show();

				var dialogContent = document.getElementById("navigate-dialog-content");
				dialogContent.innerHTML = "";
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

	function addValueToMap(map, key, value) {
		if (!map.has(key)) {
			map.set(key, []);
		}
		map.get(key).push(value);
		return map;
	}

	function countElementType(targetType, branch) {
		if (!targetType || !branch) {
			return 0;
		}
		var allElements = branch.codeElements.concat(branch.commitElements);
		var filtered = allElements.filter(function(e) {
			return e.type.toLowerCase() === targetType.toLowerCase();
		});
		return filtered.length;
	}

	/* lex sorting */
	function sortBranches(branches) {
		return branches.sort(function(a, b) {
			return a.name.localeCompare(b.name);
		});
	}

	function getBranchStatus(branch) {
		if (branch.commitElements.length + branch.codeElements.length === 0) {
			return "No rationale";
		} else if (branch.qualityProblems.length === 0) {
			return "Good";
		}
		return "Incorrect";
	}

	function createPieChart(metric, divId, title, colorPalette) {
		var data = [];

		for (const [category, branches] of metric.entries()) {
			entry = { "name": category, "value": branches.length, "branches": branches }
			data.push(entry);
		}

		console.log(metric.keys());

		var pieChart = conDecDashboard.createPieChart(divId, title, Array.from(metric.keys()), data, colorPalette);
		pieChart.on('click', function(param) {
			if (typeof param.seriesIndex != 'undefined' && param.data.branches) {
				var navigationDialog = document.getElementById("navigate-dialog");
				AJS.dialog2(navigationDialog).show();

				var dialogContent = document.getElementById("navigate-dialog-content");
				dialogContent.innerHTML = "";
				for (branch of param.data.branches) {
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
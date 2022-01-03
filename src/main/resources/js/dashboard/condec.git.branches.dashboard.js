/**
 * This module renders the dashboard and its configuration screen used in the feature branch dashboard item.
 *
 * Requires
 * condec.dashboard.js
 */
define('dashboard/branches', [], function() {
	var dashboardAPI;
	var issueBranchKeyRx = null;
	var branchesQuality = [];

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
		issueBranchKeyRx = RegExp("origin/(" + filterSettings.projectKey
			+ "-\\d+)\\.|origin/(" + filterSettings.projectKey + "-\\d+)$", "i");

		branchesQuality = [];

		for (branch of branches) {
			branch.status = getBranchStatus(branch);
			branch.numIssues = countElementType("Issue", branch);
			branch.numDecisions = countElementType("Decision", branch);
			branch.numAlternatives = countElementType("Alternative", branch);
			branch.numPros = countElementType("Pro", branch);
			branch.numCons = countElementType("Con", branch);
			branchesQuality.push(branch);
		}
		/* sort lexicographically */
		branchesQuality = sortBranches(branchesQuality);
		/* render charts and plots */
		renderChartsAndPlots();
	};

	function renderChartsAndPlots() {
		const BRANCHES_SEPARATOR_TOKEN = " ";

		function branchesPerJiraIssueReducer(accumulator, currentBranch) {
			var nameOfBranch = currentBranch.name;
			var issueMatch = nameOfBranch.match(issueBranchKeyRx);
			var accumulatorField = "";
			var nextValue = nameOfBranch;

			if (!issueMatch || (!issueMatch[1] && !issueMatch[2])) {
				accumulatorField = "no Jira task";
			} else {
				if (issueMatch[1]) {
					accumulatorField = issueMatch[1];
				}
				if (issueMatch[2]) {
					accumulatorField = issueMatch[2];
				}
			}

			if (accumulator.has(accumulatorField)) {
				nextValue = accumulator.get(accumulatorField) + BRANCHES_SEPARATOR_TOKEN + nameOfBranch;
			}

			accumulator.set(accumulatorField, nextValue);
			return accumulator;
		}


		function numberIssuesInBranchesReducer(accumulator, currentBranch) {
			accumulator.set(currentBranch.name, currentBranch.numIssues);
			accumulator.delete("none");
			return accumulator;
		}

		function numberDecisionsInBranchesReducer(accumulator, currentBranch) {
			accumulator.set(currentBranch.name, currentBranch.numDecisions);
			accumulator.delete("none");
			return accumulator;
		}

		function numberAlternativesInBranchesReducer(accumulator, currentBranch) {
			accumulator.set(currentBranch.name, currentBranch.numAlternatives);
			accumulator.delete("none");
			return accumulator;
		}

		function numberProsInBranchesReducer(accumulator, currentBranch) {
			accumulator.set(currentBranch.name, currentBranch.numPros);
			accumulator.delete("none");
			return accumulator;
		}

		function numberConInBranchesReducer(accumulator, currentBranch) {
			accumulator.set(currentBranch.name, currentBranch.numCons);
			accumulator.delete("none");
			return accumulator;
		}

		function sortByBranchNumberDescending(unsortedMap) {
			var keys = Array.from(unsortedMap.keys());
			var keyVal = [];
			for (var i = 0; i < keys.length; i++) {
				var count = 0;
				if (unsortedMap.get(keys[i]).length > 0) {
					count = unsortedMap.get(keys[i]).split(" ").length;
				}
				keyVal.push([keys[i], count]);
			}
			var sortedKeyByVal = keyVal.sort(function(a, b) {
				return b[1] - a[1];
			});
			var sortedMap = new Map();
			for (var j = 0; j < sortedKeyByVal.length; j++) {
				var mapKey = sortedKeyByVal[j][0];
				sortedMap.set(mapKey, unsortedMap.get(mapKey));
			}
			return sortedMap;
		}

		/*  init data for charts */
		var statusesForBranchesData = new Map();
		statusesForBranchesData.set("Incorrect", []);
		statusesForBranchesData.set("Good", []);
		statusesForBranchesData.set("No rationale", []);
		for (branch of branchesQuality) {
			var statusOfBranch = branch.status;
			if (statusesForBranchesData.has(statusOfBranch)) {
				var previousBranchesInStatus = statusesForBranchesData.get(statusOfBranch);
				previousBranchesInStatus.push(branch);
				statusesForBranchesData.set(statusOfBranch, previousBranchesInStatus);
			}
		}

		var problemTypesOccurrence = new Map();
		for (branch of branchesQuality) {
			for (problem of branch.qualityProblems) {
				console.log(problem);
				if (!problemTypesOccurrence.has(problem.explanation)) {
					problemTypesOccurrence.set(problem.explanation, []);
				}
				var branchesWithProblem = problemTypesOccurrence.get(problem.explanation);
				branchesWithProblem.push(branch);
				problemTypesOccurrence.set(problem.explanation, branchesWithProblem);
			}
		}

		var branchesPerIssue = new Map();
		var issuesInBranches = new Map();
		var decisionsInBranches = new Map();
		var alternativesInBranches = new Map();
		var prosInBranches = new Map();
		var consInBranches = new Map();

		/* set something in case no data will be added to them */
		issuesInBranches.set("none", 0);
		decisionsInBranches.set("none", 0);
		alternativesInBranches.set("none", 0);
		prosInBranches.set("none", 0);
		consInBranches.set("none", 0);

		branchesPerIssue.set("no Jira task", "");

		/* form data for charts */
		branchesQuality.reduce(branchesPerJiraIssueReducer, branchesPerIssue);
		branchesQuality.reduce(numberIssuesInBranchesReducer, issuesInBranches);
		branchesQuality.reduce(numberDecisionsInBranchesReducer, decisionsInBranches);
		branchesQuality.reduce(numberAlternativesInBranchesReducer, alternativesInBranches);
		branchesQuality.reduce(numberProsInBranchesReducer, prosInBranches);
		branchesQuality.reduce(numberConInBranchesReducer, consInBranches);



		/* sort some data by number of branches */
		//var sortedProblemTypesOccurrence = sortByBranchNumberDescending(problemTypesOccurrence);
		var sortedBranchesPerIssue = sortByBranchNumberDescending(branchesPerIssue);

		/* render pie-charts */
		/* "Quality status" */
		console.log(statusesForBranchesData);
		createPieChart(statusesForBranchesData, "piechartRich-QualityStatusForBranches",
			"How many branches document rationale well?");
		createPieChart(problemTypesOccurrence, "piechartRich-ProblemTypesInBranches",
			"Which documentation mistakes are most common?");

		conDecDashboard.initializeChartForBranchSource("piechartRich-BranchesPerIssue",
			"", "How many branches do Jira tasks have?", sortedBranchesPerIssue);
		/* render box-plots */
		conDecDashboard.initializeChartForBranchSource("boxplot-IssuesPerBranch",
			"", "Issues number in branches", issuesInBranches);
		conDecDashboard.initializeChartForBranchSource("boxplot-DecisionsPerBranch",
			"", "Decisions number in branches", decisionsInBranches);
		conDecDashboard.initializeChartForBranchSource("boxplot-AlternativesPerBranch",
			"", "Alternatives number in branches", alternativesInBranches);
		conDecDashboard.initializeChartForBranchSource("boxplot-ProsPerBranch",
			"", "Pro arguments number in branches", prosInBranches);
		conDecDashboard.initializeChartForBranchSource("boxplot-ConsPerBranch",
			"", "Con arguments number in branches", consInBranches);
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
					dialogContent.appendChild(link);
				}
			}
		});
	}

	return ConDecBranchesDashboardItem;
});
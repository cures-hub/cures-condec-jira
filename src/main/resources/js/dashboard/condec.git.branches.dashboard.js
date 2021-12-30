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

	/*  bad problem explanations */
	var ARGUMENT_WITHOUT_PARENT_ELEMENT = "Argument without parent alternative";
	var ALTERNATIVE_DECISION_WITHOUT_PARENT_ELEMENT =
		"Alternative without parent problem";
	var ISSUE_WITH_MANY_DECISIONS = "Issue has too many decisions";
	var DECISION_WITHOUT_PRO_ARGUMENTS = "Decision does not have a pro argument";

	/*  not that bad problem explanations */
	var ALTERNATIVE_DECISION_WITHOUT_ARGUMENTS =
		"Alternative does not have any arguments";
	var DECISION_ARGUMENTS_MAYBE_WORSE_THAN_ALTERNATIVE =
		"Decision's arguments seem weaker than in one of sibling alternatives";
	var ISSUE_WITHOUT_DECISIONS = "Issue does not have a valid decision!";
	var ISSUE_WITHOUT_ALTERNATIVES = "Issue does not have any alternatives yet";

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
			var branchQuality = {};
			branchQuality.name = branch.name;
			branchQuality.status = getBranchStatus(branch);
			branchQuality.problems = branch.qualityProblems;
			branchQuality.numIssues = countElementType("Issue", branch);
			branchQuality.numDecisions = countElementType("Decision", branch);
			branchQuality.numAlternatives = countElementType("Alternative", branch);
			branchQuality.numPros = countElementType("Pro", branch);
			branchQuality.numCons = countElementType("Con", branch);
			branchesQuality.push(branchQuality);
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

		function statusWithBranchesReducer(accumulator, currentBranch) {
			var statusOfBranch = currentBranch.status;
			var nameOfBranch = currentBranch.name;
			if (accumulator.has(statusOfBranch)) {
				var previousBranchesInStatus = accumulator.get(statusOfBranch);
				if (previousBranchesInStatus.length < 1) {
					accumulator.set(statusOfBranch, nameOfBranch);
				} else {
					var newValue = previousBranchesInStatus + BRANCHES_SEPARATOR_TOKEN + nameOfBranch;
					accumulator.set(statusOfBranch, newValue);
				}
			} else {
				accumulator.set(statusOfBranch, nameOfBranch);
			}

			return accumulator;
		}

		function problemsWithBranchesReducer(accumulator, currentBranch) {
			var problems = currentBranch.problems;
			console.log(problems);
			var nameOfBranch = currentBranch.name;

			for (problem of problems) {
				var newValue = accumulator.get(problem.explanation)
					+ BRANCHES_SEPARATOR_TOKEN
					+ nameOfBranch;
				accumulator.set(problem.explanation, newValue);
			}
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
		statusesForBranchesData.set("Incorrect", "");
		statusesForBranchesData.set("Good", "");
		statusesForBranchesData.set("No rationale", "");
		var problemTypesOccurrence = getEmptyMapForProblemTypes("");

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
		branchesQuality.reduce(statusWithBranchesReducer, statusesForBranchesData);
		branchesQuality.reduce(problemsWithBranchesReducer, problemTypesOccurrence);
		branchesQuality.reduce(branchesPerJiraIssueReducer, branchesPerIssue);
		branchesQuality.reduce(numberIssuesInBranchesReducer, issuesInBranches);
		branchesQuality.reduce(numberDecisionsInBranchesReducer, decisionsInBranches);
		branchesQuality.reduce(numberAlternativesInBranchesReducer, alternativesInBranches);
		branchesQuality.reduce(numberProsInBranchesReducer, prosInBranches);
		branchesQuality.reduce(numberConInBranchesReducer, consInBranches);

		/* sort some data by number of branches */
		var sortedProblemTypesOccurrence = sortByBranchNumberDescending(problemTypesOccurrence);
		var sortedBranchesPerIssue = sortByBranchNumberDescending(branchesPerIssue);

		/* render pie-charts */
		conDecDashboard.initializeChartForBranchSource("piechartRich-QualityStatusForBranches",
			"", "How many branches document rationale well?", statusesForBranchesData); /* "Quality status" */
		conDecDashboard.initializeChartForBranchSource("piechartRich-ProblemTypesInBranches",
			"", "Which documentation mistakes are most common?", sortedProblemTypesOccurrence); /*"Total quality problems" */
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

	function getEmptyMapForProblemTypes(initValue) {
		var allProblems = new Map();

		allProblems.set(ARGUMENT_WITHOUT_PARENT_ELEMENT, initValue);
		allProblems.set(ALTERNATIVE_DECISION_WITHOUT_PARENT_ELEMENT, initValue);
		allProblems.set(ISSUE_WITH_MANY_DECISIONS, initValue);
		allProblems.set(DECISION_WITHOUT_PRO_ARGUMENTS, initValue);
		allProblems.set(ALTERNATIVE_DECISION_WITHOUT_ARGUMENTS, initValue);
		allProblems.set(DECISION_ARGUMENTS_MAYBE_WORSE_THAN_ALTERNATIVE, initValue);
		allProblems.set(ISSUE_WITHOUT_DECISIONS, initValue);
		allProblems.set(ISSUE_WITHOUT_ALTERNATIVES, initValue);

		return allProblems;
	}

	function getBranchStatus(branch) {
		if (branch.commitElements.length + branch.codeElements.length === 0) {
			return "No rationale";
		} else if (branch.qualityProblems.length === 0) {
			return "Good";
		}
		return "Incorrect";
	}

	return ConDecBranchesDashboardItem;
});
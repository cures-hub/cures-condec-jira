/*
 This module fills the box plots and pie charts used in the feature task branch dashboard item.

 Requires
 * condec.requirements.dashboard.js

 Is referenced in HTML by
 * dashboard/featureBranches.vm
 */

(function (global) {
	var issueBranchKeyRx = null;
	var branchesQuality = [];

	var ConDecBranchesDashboard = function ConDecBranchesDashboard() {
		console.log("ConDecBranchesDashboard constructor");
	};

	/**
	 * Gets the data to fill the dashboard plots by making an API-call.
	 *
	 * external references: condec.dashboard.js
	 *
	 * @param dashboardAPI used to call methods of the Jira dashboard api
	 * @param filterSettings the filterSettings used for the API-call
	 */
	ConDecBranchesDashboard.prototype.getData = function (dashboardAPI, filterSettings) {
		conDecDashboardAPI.getElementsFromBranchesOfJiraIssue(filterSettings.projectKey, function (error, result) {
			conDecDashboard.processData(error, result, conDecBranchesDashboard, "branch",
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
	ConDecBranchesDashboard.prototype.renderData = function (data, filterSettings) {
		/*
		 * Match branch names either: starting with issue key followed by dot OR
		 * exactly the issue key
		 */
		issueBranchKeyRx = RegExp("origin/(" + filterSettings.projectKey
			+ "-\\d+)\\.|origin/(" + filterSettings.projectKey + "-\\d+)$", "i");

		branchesQuality = [];

		var branches = data.branches;
		for (var branchIdx = 0; branchIdx < branches.length; branchIdx++) {
			var lastBranch = conDecLinkBranchCandidates.extractPositions(branches[branchIdx]);

			/* these elements are sorted by commit age and occurrence in message */
			var lastBranchElementsFromMessages = lastBranch.commitElements;
			var lastBranchElementsFromFiles = lastBranch.codeElements;

			var lastBranchRelevantElementsSortedWithPosition =
				lastBranchElementsFromMessages.concat(lastBranchElementsFromFiles);

			/* assess relations between rationale and their problems */
			conDecLinkBranchCandidates.init(
				lastBranchRelevantElementsSortedWithPosition,
				lastBranch.branchName,
				branchIdx,
				'');

			var branchQuality = {};
			branchQuality.name = lastBranch.branchName;
			branchQuality.status = conDecLinkBranchCandidates.getBranchStatus();
			branchQuality.problems = conDecLinkBranchCandidates.getProblemNamesObserved();
			branchQuality.numIssues = countElementType("Issue", lastBranch);
			branchQuality.numDecisions = countElementType("Decision", lastBranch);
			branchQuality.numAlternatives = countElementType("Alternative", lastBranch);
			branchQuality.numPros = countElementType("Pro", lastBranch);
			branchQuality.numCons = countElementType("Con", lastBranch);
			branchesQuality.push(branchQuality);
		}
		/* sort lexicographically */
		branchesQuality = sortBranches(branchesQuality);
		/* render charts and plots */
		renderChartsAndPlots();
	}

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

			var it = problems.keys();
			var result = it.next();

			while (!result.done) {
				var key = result.value;
				if (problems.get(key) > 0) {
					/* already has a branch name */
					if (accumulator.get(key).length > 1) {
						var newValue = accumulator.get(key)
							+ BRANCHES_SEPARATOR_TOKEN
							+ nameOfBranch;
						accumulator.set(key, newValue);
					} else {
						accumulator.set(key, nameOfBranch);
					}
				}
				result = it.next();
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
			var sortedKeyByVal = keyVal.sort(function (a, b) {
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
		var statusesForBranchesData = conDecLinkBranchCandidates.getEmptyMapForStatuses("");
		var problemTypesOccurrence = conDecLinkBranchCandidates.getEmptyMapForProblemTypes("");

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
		ConDecReqDash.initializeChartForBranchSource("piechartRich-QualityStatusForBranches",
			"", "How many branches document rationale well?", statusesForBranchesData); /* "Quality status" */
		ConDecReqDash.initializeChartForBranchSource("piechartRich-ProblemTypesInBranches",
			"", "Which documentation mistakes are most common?", sortedProblemTypesOccurrence); /*"Total quality problems" */
		ConDecReqDash.initializeChartForBranchSource("piechartRich-BranchesPerIssue",
			"", "How many branches do Jira tasks have?", sortedBranchesPerIssue);
		/* render box-plots */
		ConDecReqDash.initializeChartForBranchSource("boxplot-IssuesPerBranch",
			"", "Issues number in branches", issuesInBranches);
		ConDecReqDash.initializeChartForBranchSource("boxplot-DecisionsPerBranch",
			"", "Decisions number in branches", decisionsInBranches);
		ConDecReqDash.initializeChartForBranchSource("boxplot-AlternativesPerBranch",
			"", "Alternatives number in branches", alternativesInBranches);
		ConDecReqDash.initializeChartForBranchSource("boxplot-ProsPerBranch",
			"", "Pro arguments number in branches", prosInBranches);
		ConDecReqDash.initializeChartForBranchSource("boxplot-ConsPerBranch",
			"", "Con arguments number in branches", consInBranches);
	}

	function countElementType(targetType, branch) {
		if (!targetType || !branch) {
			return 0;
		}
		var allElements = branch.codeElements.concat(branch.commitElements);
		var filtered = allElements.filter(function (e) {
			return e.type.toLowerCase() === targetType.toLowerCase();
		});
		return filtered.length;
	}

	/* lex sorting */
	function sortBranches(branches) {
		return branches.sort(function (a, b) {
			return a.name.localeCompare(b.name);
		});
	}

	global.conDecBranchesDashboard = new ConDecBranchesDashboard();
})(window);
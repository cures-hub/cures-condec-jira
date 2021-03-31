/*
 This module fills the box plots and pie charts used in the feature task branch dashboard item.

 Requires
 * js/condec.requirements.dashboard.js

 Is referenced in HTML by
 * featureBranchesDashboardItem.vm
 */

/* DEV vars to be removed: */
var ConDecDevBranchesQuality = [];
var ConDecDevBranches = [];

(function (global) {
	var processing = null;
	var issueBranchKeyRx = null;

	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var branchesQuality = [];

	var ConDecBranchesDashboard = function ConDecBranchesDashboard() {
		console.log("ConDecBranchesDashboard constructor");
	};

	ConDecBranchesDashboard.prototype.init = function init(filterSettings) {
		/*
		 * Match branch names either: starting with issue key followed by dot OR
		 * exactly the issue key
		 */
		issueBranchKeyRx = RegExp("origin/(" + JSON.parse(filterSettings).projectKey
			+ "-\\d+)\\.|origin/(" + JSON.parse(filterSettings).projectKey + "-\\d+)$", "i");

		getHTMLNodes("condec-branch-dashboard-configproject"
			, "condec-branches-dashboard-contents-container"
			, "condec-branches-dashboard-contents-data-error"
			, "condec-branches-dashboard-no-project"
			, "condec-branches-dashboard-processing"
			, "condec-branches-dashboard-nogit-error");

		branchesQuality = [];
		getBranches(filterSettings);
	};

	function getHTMLNodes(filterName, containerName, dataErrorName, noProjectName, processingName, noGitName) {
		dashboardFilterNode = document.getElementById(filterName);
		dashboardContentNode = document.getElementById(containerName);
		dashboardDataErrorNode = document.getElementById(dataErrorName);
		dashboardNoContentsNode = document.getElementById(noProjectName);
		dashboardProcessingNode = document.getElementById(processingName);
		dashboardProjectWithoutGit = document.getElementById(noGitName);
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

	function getBranches(filterSettings) {
		var projectKey = JSON.parse(filterSettings).projectKey;

		if (!projectKey || !projectKey.length || !projectKey.length > 0) {
			return;
		}
		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		processing = projectKey;
		showDashboardSection(dashboardProcessingNode);
		url = conDecAPI.restPrefix + "/view/elementsFromBranchesOfProject.json?projectKey=" + projectKey;

		console.log("Starting REST query.");
		AJS.$.ajax({
			url: url,
			type: "get",
			dataType: "json",
			async: true,
			success: conDecBranchesDashboard.processData,
			error: conDecBranchesDashboard.processDataBad
		});
	}

	ConDecBranchesDashboard.prototype.processDataBad = function processDataBad(data) {
		console.log(data.responseJSON.error);
		showDashboardSection(dashboardDataErrorNode);
	};

	ConDecBranchesDashboard.prototype.processData = function processData(data) {
		processXhrResponseData(data);
	};

	function processXhrResponseData(data) {
		doneWithXhrRequest();
		showDashboardSection(dashboardContentNode);
		processing = null;
		processBranches(data);
	}

	function doneWithXhrRequest() {
		dashboardProcessingNode.classList.remove("error");
		showDashboardSection(dashboardProcessingNode);
	}

	function countElementType(targetType, branch) {
		if (!targetType || !branch || !branch.elements || !branch.elements.length) {
			return 0;
		}
		var filtered = branch.elements.filter(function (e) {
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

	function processBranches(data) {
		var branches = data.branches;
		ConDecDevBranches = branches; /*
										 * remember in global scope for
										 * development/debugging
										 */
		for (branchIdx = 0; branchIdx < branches.length; branchIdx++) {
			var lastBranch = conDecLinkBranchCandidates.extractPositions(branches[branchIdx]);

			/* these elements are sorted by commit age and occurrence in message */
			var lastBranchElementsFromMessages =
				lastBranch.elements.filter(function (e) {
					return e.key.sourceTypeCommitMessage;
				});

			/* these elements are not sorted, we want only B(final) files. */
			var lastBranchElementsFromFilesButNotSorted =
				lastBranch.elements.filter(function (e) {
					return e.key.codeFileB;
				});

			/* sort file elements */
			var lastBranchElementsFromFiles =
				conDecLinkBranchCandidates.sortRationaleDiffOfFiles(lastBranchElementsFromFilesButNotSorted);

			var lastBranchRelevantElementsSortedWithPosition =
				lastBranchElementsFromMessages.concat(lastBranchElementsFromFiles);

			/* assess relations between rationale and their problems */
			conDecLinkBranchCandidates.init(
				lastBranchRelevantElementsSortedWithPosition,
				lastBranch.branchName,
				branchIdx,
				'');

			branchQuality = {};
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
		renderData();
	}

	function renderData() {
		BRANCHES_SEPARATOR_TOKEN = " ";

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
		var problemTypesOccurrance = conDecLinkBranchCandidates.getEmptyMapForProblemTypes("");

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
		branchesQuality.reduce(problemsWithBranchesReducer, problemTypesOccurrance);
		branchesQuality.reduce(branchesPerJiraIssueReducer, branchesPerIssue);
		branchesQuality.reduce(numberIssuesInBranchesReducer, issuesInBranches);
		branchesQuality.reduce(numberDecisionsInBranchesReducer, decisionsInBranches);
		branchesQuality.reduce(numberAlternativesInBranchesReducer, alternativesInBranches);
		branchesQuality.reduce(numberProsInBranchesReducer, prosInBranches);
		branchesQuality.reduce(numberConInBranchesReducer, consInBranches);

		/* sort some data by number of branches */
		var sortedProblemTypesOccurrance = sortByBranchNumberDescending(problemTypesOccurrance);
		var sortedBranchesPerIssue = sortByBranchNumberDescending(branchesPerIssue);

		/* render pie-charts */
		ConDecReqDash.initializeChartForBranchSource("piechartRich-QualityStatusForBranches",
			"", "How many branches document rationale well?", statusesForBranchesData); /* "Quality status" */
		ConDecReqDash.initializeChartForBranchSource("piechartRich-ProblemTypesInBranches",
			"", "Which documentation mistakes are most common?", sortedProblemTypesOccurrance); /*"Total quality problems" */
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

		/* remember in global scope for development/debugging */
		ConDecDevBranchesQuality = {branchesQuality: branchesQuality};
		ConDecDevBranchesQuality.getTitleByName = function (branchNameShortened) {
			for (var i = 0; i < this.branchesQuality.length; i++) {
				if (this.branchesQuality[i].name.endsWith(branchNameShortened)) {
					return this.getTitle(i);
				}
			}
			return this.getTitle(-1);
		};
		ConDecDevBranchesQuality.getTitle = function (idx) {
			if (!this.branchesQuality[idx]) {
				return "";
			}
			var branch = this.branchesQuality[idx];
			var buffer = " status: " + branch.status
				+ "\n" + " numAlternatives: " + branch.numAlternatives
				+ "\n" + " numCons: " + branch.numCons
				+ "\n" + " numDecisions: " + branch.numDecisions
				+ "\n" + " numIssues: " + branch.numIssues
				+ "\n" + " numPros: " + branch.numIssues;

			return buffer;
		};

	}

	global.conDecBranchesDashboard = new ConDecBranchesDashboard();
})(window);

define('dashboard/branches', [], function () {
	var dashboardAPI;

	var dashboardFilterNode;
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

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
		$(document).ready(function() {
			var projectKey = preferences['projectKey'];

			var filterSettings = getFilterSettings(projectKey);

			conDecBranchesDashboard.init(filterSettings);
			dashboardAPI.resize();
		});
	};

	ConDecBranchesDashboardItem.prototype.renderEdit = function (context, preferences) {
		$(document).ready(function() {
			getHTMLNodes("condec-branch-dashboard-configproject"
				, "condec-branches-dashboard-contents-container"
				, "condec-branches-dashboard-contents-data-error"
				, "condec-branches-dashboard-no-project"
				, "condec-branches-dashboard-processing"
				, "condec-branches-dashboard-nogit-error");

			showDashboardSection(dashboardFilterNode);

			setPreferences(preferences);

			dashboardAPI.resize();

			function onSaveButton(event) {
				var preferences = getPreferences();

				var projectKey = preferences['projectKey'];

				var filterSettings = getFilterSettings(projectKey);

				if (projectKey) {
					dashboardAPI.savePreferences(preferences);
					conDecBranchesDashboard.init(filterSettings);
				}

				dashboardAPI.resize();
			}

			function onCancelButton(event) {
				dashboardAPI.closeEdit();
				dashboardAPI.resize();
			}

			saveButton = document.getElementById("branch-save-button");
			saveButton.addEventListener("click", onSaveButton);

			cancelButton = document.getElementById("branch-cancel-button");
			cancelButton.addEventListener("click", onCancelButton);
		});
	};

	function getHTMLNodes(filterName, containerName, dataErrorName, noProjectName, processingName, noGitName) {
		dashboardFilterNode = document.getElementById(filterName);
		dashboardContentNode = document.getElementById(containerName);
		dashboardDataErrorNode = document.getElementById(dataErrorName);
		dashboardNoContentsNode = document.getElementById(noProjectName);
		dashboardProcessingNode = document.getElementById(processingName);
		dashboardProjectWithoutGit = document.getElementById(noGitName);
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

		var projectNode = document.getElementById("condec-dashboard-branch-project-selection");
		preferences['projectKey'] = projectNode.value;

		return preferences;
	}

	function setPreferences(preferences) {
		if (preferences['projectKey']) {
			var projectNode = document.getElementById("condec-dashboard-branch-project-selection");
			projectNode.value = preferences['projectKey'];
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
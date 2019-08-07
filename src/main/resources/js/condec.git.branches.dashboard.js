/*
 This module fills the box plots and pie charts used in the feature task branch dashboard item.

 Requires
 * js/condec.report.js

 Is referenced in HTML by
 * featureBranchesDashboardItem.vm
 */

/* DEV vars to be removed: */
var ConDecDevBranchesQuality = [];
var ConDecDevBranches = [];

(function(global) {
    var dashboards = {};    /* TODO: object muss support more than one dashboard.*/

    var dashboardUID;
    var processing = null;
    var projectKey = null;
    var issueKeyRx = null;

    var dashboardContentNode;
    var dashboardDataErrorNode;
    var dashboardFatalErrorNode;
    var dashboardNoContentsNode;
    var dashboardProcessingNode;
    var dashboardProjectWithoutGit;

    var branchesQuality = [];

	var ConDecBranchesDashboard = function ConDecBranchesDashboard() {
		console.log("ConDecBranchesDashboard constructor");
	};

    ConDecBranchesDashboard.prototype.init = function init(_projectKey, _dashboardUID, _gituri) {
        console.log("received for project: "+ _projectKey +" UID:"+ _dashboardUID);
        projectKey = _projectKey;
        issueKeyRx = RegExp("origin/(" + _projectKey + "-\\d+)\\.","i");
        dashboardUID = _dashboardUID;

        getHTMLNodes( "condec-branches-dashboard-contents-container"+dashboardUID
        , "condec-branches-dashboard-contents-data-error"+dashboardUID
        , "condec-branches-dashboard-no-project"+dashboardUID
        , "condec-branches-dashboard-processing"+dashboardUID
        , "condec-branches-dashboard-nogit-error"+dashboardUID);

        branchesQuality = [];

        if (!_gituri || _gituri.length<1) {
            showDashboardSection(dashboardProjectWithoutGit);
        }
        else {
            getBranches(projectKey);
        }
    };

    function getHTMLNodes(containerName, dataErrorName, noProjectName, processingName, noGitName) {
        dashboardContentNode   = document.getElementById(containerName);
        dashboardDataErrorNode = document.getElementById(dataErrorName);
        dashboardNoContentsNode = document.getElementById(noProjectName);
        dashboardProcessingNode = document.getElementById(processingName);
        dashboardProjectWithoutGit = document.getElementById(noGitName);
    }
    function showDashboardSection(node) {
        var hiddenClass = "hidden";
        dashboardContentNode.classList.add(hiddenClass);
        dashboardDataErrorNode.classList.add(hiddenClass);
        dashboardNoContentsNode.classList.add(hiddenClass);
        dashboardProcessingNode.classList.add(hiddenClass);
        dashboardProjectWithoutGit.classList.add(hiddenClass);
        node.classList.remove(hiddenClass);
    }

    function getBranches(projectKey){
        if (!projectKey || !projectKey.length || !projectKey.length>0) {
            return;
        }
        /* on XHR HTTP failure codes the code aborts instead of processing with processDataBad() !?
        if (processing) {
            return warnStillProcessing();
        }
        */
        processing = projectKey;
        showDashboardSection(dashboardProcessingNode);

        url = AJS.contextPath()
          + "/rest/decisions/latest/view/elementsFromBranchesOfProject.json?projectKey="
          + projectKey;

        /*  get cache or server data? */
        if (localStorage.getItem("condec.restCacheTTL")) {
          console.log("condec.restCacheTTL setting found");
          if (localStorage.getItem(url)) {
            var data = null;
            var now = Date.now();
            var cacheTTL = parseInt(localStorage.getItem("condec.restCacheTTL"));
            try {
              data = JSON.parse(localStorage.getItem(url));
            } catch (ex) {
              data = null;
            }
            if (data && cacheTTL) {
              if (now - data.timestamp < cacheTTL) {
                console.log(
                  "Cache is within specified TTL, therefore getting data from local cache instead from server."
                );
                return processXhrResponseData(data);
              } else {
                console.log("Cache TTL expired, therefore starting  REST query.");
              }
            }
            if (!cacheTTL) {
              console.log(
                "Cache TTL is not a number, therefore starting  REST query."
              );
            }
          }
        }
        else {
          localStorage.setItem("condec.restCacheTTL",1000*60*3); /* init 3 minute caching */
        }

        console.log("Starting  REST query.");
        AJS.$.ajax({
          url: url,
          type: "get",
          dataType: "json",
          async: true,
          success: conDecBranchesDashboard.processData,
          error: conDecBranchesDashboard.processDataBad
        });
    }

    ConDecBranchesDashboard.prototype.processDataBad = function processDataBad(data,uid) {
        showDashboardSection(dashboardDataErrorNode);
        doneWithXhrRequest();
    };

    ConDecBranchesDashboard.prototype.processData = function processData(data,uid) {
        processXhrResponseData(data);
    };

    function processXhrResponseData(data) {
        doneWithXhrRequest();
        showDashboardSection(dashboardContentNode);
        data.timestamp = Date.now();
        localStorage.setItem(url, JSON.stringify(data, null, 1));
        processing = null;
        processBranches(data);
    }

    function doneWithXhrRequest() {
        dashboardProcessingNode.classList.remove("error");
        showDashboardSection(dashboardProcessingNode);
    }

    function warnStillProcessing() {
        dashboardProcessingNode.classList.add("error");
        console.warn("Still processing request for: "+processing);
    }

    function countElementType(targetType, branch) {
        if (!targetType || !branch || !branch.elements || !branch.elements.length) {
            return 0;
        }
        var filtered = branch.elements.filter(function(e){
            return e.type.toLowerCase() === targetType.toLowerCase();
        });
        return filtered.length;
    }

    /* lex sorting */
    function sortBranches(branches) {
        return branches.sort(function(a,b){return a.name.localeCompare(b.name); });
    }

    function processBranches(data){
        var branches = data.branches;
        ConDecDevBranches = branches; /*  remember in global scope for development/debugging */
        for (branchIdx = 0; branchIdx < branches.length; branchIdx++) {
            var lastBranch = conDecLinkBranchCandidates.extractPositions(branches[branchIdx]);

            /*  these elements are sorted by commit age and occurrence in message */
            var lastBranchElementsFromMessages =
            lastBranch.elements.filter(function(e){return e.key.sourceTypeCommitMessage;});

            /*  these elements are not sorted, we want only B(final) files. */
            var lastBranchElementsFromFilesButNotSorted =
            lastBranch.elements.filter(function(e){return e.key.codeFileB;});

            /*  sort file elements */
            var lastBranchElementsFromFiles =
            conDecLinkBranchCandidates.sortRationaleDiffOfFiles(lastBranchElementsFromFilesButNotSorted);

            var lastBranchRelevantElementsSortedWithPosition =
            lastBranchElementsFromMessages.concat(lastBranchElementsFromFiles);

            /*  assess relations between rationale and their problems */
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
        /*  sort lexicographically */
        branchesQuality = sortBranches(branchesQuality);
        /*  render charts and plots */
        renderData();
    }

    function renderData(){
        function branchesPerJiraIssueReducer(accumulator, currentBranch) {
            var nameOfBranch = currentBranch.name;
            var issueMatch = nameOfBranch.match(issueKeyRx)
            var accumulatorField = "";
            var nextValue = nameOfBranch;

            if (!issueMatch || !issueMatch[1]) {
                accumulatorField = "no Jira task";
            }
            else {
                accumulatorField = issueMatch[1];
            }

            if (accumulator.has(accumulatorField)) {
                nextValue = accumulator.get(accumulatorField)+";"+ nameOfBranch;
            }

            accumulator.set(accumulatorField, nextValue);
            return accumulator;
        }

        function statusWithBranchesReducer(accumulator, currentBranch) {
            BRANCHES_SEPARATOR_TOKEN = ";";
            var  statusOfBranch = currentBranch.status;
            var  nameOfBranch = currentBranch.name;
            if (accumulator.has(statusOfBranch)) {
                var previousBranchesInStatus = accumulator.get(statusOfBranch);
                if (previousBranchesInStatus.length<1) {
                    accumulator.set(statusOfBranch, nameOfBranch);
                }
                else {
                    var newValue = previousBranchesInStatus + BRANCHES_SEPARATOR_TOKEN + nameOfBranch;
                    accumulator.set(statusOfBranch, newValue);
                }
            }
            else {
                accumulator.set(statusOfBranch, nameOfBranch);
            }

            return accumulator;
        }

        function problemsWithBranchesReducer(accumulator, currentBranch) {
            BRANCHES_SEPARATOR_TOKEN = ";";
            var  problems = currentBranch.problems;
            var  nameOfBranch = currentBranch.name;

            var it = problems.keys();
            var result = it.next();

            while (!result.done) {
                var key = result.value;
                if (problems.get(key)>0) {
                    /*  already has a bran chname */
                    if (accumulator.get(key).length>1) {
                        var newValue = accumulator.get(key)
                                     + BRANCHES_SEPARATOR_TOKEN
                                     + nameOfBranch;
                        accumulator.set(key, newValue);
                    }
                    else {
                        accumulator.set(key,nameOfBranch);
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

        /*  init data for charts */
        var statusesForBranchesData = conDecLinkBranchCandidates.getEmptyMapForStatuses("");
        var problemTypesOccurrance = conDecLinkBranchCandidates.getEmptyMapForProblemTypes("");

        var branchesPerIssue = new Map();
        var issuesInBranches = new Map();
        var decisionsInBranches = new Map();
        var alternativesInBranches = new Map();
        var prosInBranches = new Map();
        var consInBranches = new Map();

        /*  set something for box plots in case no data will be added to them */
        issuesInBranches.set("none",0);
        decisionsInBranches.set("none",0);
        alternativesInBranches.set("none",0);
        prosInBranches.set("none",0);
        consInBranches.set("none",0);

        branchesPerIssue.set("no Jira task","");

        /*  form data for charts */
        branchesQuality.reduce(statusWithBranchesReducer, statusesForBranchesData);
        branchesQuality.reduce(problemsWithBranchesReducer, problemTypesOccurrance);
        branchesQuality.reduce(branchesPerJiraIssueReducer, branchesPerIssue);
        branchesQuality.reduce(numberIssuesInBranchesReducer, issuesInBranches);
        branchesQuality.reduce(numberDecisionsInBranchesReducer, decisionsInBranches);
        branchesQuality.reduce(numberAlternativesInBranchesReducer, alternativesInBranches);
        branchesQuality.reduce(numberProsInBranchesReducer, prosInBranches);
        branchesQuality.reduce(numberConInBranchesReducer, consInBranches);

        /* render pie-charts */
        conDecReport.initializeChartForBranchSource('piechartRich-QualityStatusForBranches'+dashboardUID,
         '', 'How many branches document rationale well?',statusesForBranchesData); /* 'Quality status' */
        conDecReport.initializeChartForBranchSource('piechartRich-ProblemTypesInBranches'+dashboardUID,
         '', 'Which documentation mistakes are most common?',problemTypesOccurrance); /*'Total quality problems' */
        conDecReport.initializeChartForBranchSource('piechartRich-BranchesPerIssue'+dashboardUID,
         '', 'How many branches do Jira tasks have?',branchesPerIssue);
        /* render box-plots */
        conDecReport.initializeChartForBranchSource('boxplot-IssuesPerBranch'+dashboardUID,
         '', 'Issues number in branches',issuesInBranches);
        conDecReport.initializeChartForBranchSource('boxplot-DecisionsPerBranch'+dashboardUID,
         '', 'Decisions number in branches',decisionsInBranches);
        conDecReport.initializeChartForBranchSource('boxplot-AlternativesPerBranch'+dashboardUID,
         '', 'Alternatives number in branches',alternativesInBranches);
        conDecReport.initializeChartForBranchSource('boxplot-ProsPerBranch'+dashboardUID,
         '', 'Pro arguments number in branches',prosInBranches);
        conDecReport.initializeChartForBranchSource('boxplot-ConsPerBranch'+dashboardUID,
         '', 'Con arguments number in branches',consInBranches);

        /* remember in global scope for development/debugging */
        ConDecDevBranchesQuality = branchesQuality;

    }

	global.conDecBranchesDashboard  = new ConDecBranchesDashboard ();
})(window);


/*
 This module fills the box plots and pie charts used in the feature task branch dashboard item.

 Requires
 * js/condec.report.js

 Is referenced in HTML by
 * featureBranchesDashboardItem.vm
 */

// DEV vars to be removed:
var CONDEC_branchesQuality = [];
var CONDEC_branches = [];

(function(global) {
    var dashboards = {};    /* TODO: object muss support more than one dashboard.*/

    var dashboardUID;
    var processing = null;
    var projectKey = null;

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
        projectKey = _projectKey
        dashboardUID = _dashboardUID

        getHTMLNodes( "condec-branches-dashboard-contents-container"+dashboardUID
        , "condec-branches-dashboard-contents-data-error"+dashboardUID
        , "condec-branches-dashboard-no-project"+dashboardUID
        , "condec-branches-dashboard-processing"+dashboardUID
        , "condec-branches-dashboard-nogit-error"+dashboardUID);

        branchesQuality = [];

        if (!_gituri || _gituri.length<1) {
            showDashboardSection(dashboardProjectWithoutGit)
        }
        else {
            getBranches(projectKey)
        }

    }

    function getHTMLNodes(containerName, dataErrorName, noProjectName, processingName, noGitName) {
        if (!dashboardContentNode) {
            dashboardContentNode   = document.getElementById(containerName)
            dashboardDataErrorNode = document.getElementById(dataErrorName)
            dashboardNoContentsNode = document.getElementById(noProjectName)
            dashboardProcessingNode = document.getElementById(processingName)
            dashboardProjectWithoutGit = document.getElementById(noGitName)
        }
    }
    function showDashboardSection(node) {
        var hiddenClass = "hidden";
        dashboardContentNode.classList.add(hiddenClass);
        dashboardDataErrorNode.classList.add(hiddenClass);
        dashboardNoContentsNode.classList.add(hiddenClass);
        dashboardProcessingNode.classList.add(hiddenClass);
        dashboardProjectWithoutGit.classList.add(hiddenClass);
        node.classList.remove(hiddenClass)
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
          + projectKey

        // get cache or server data?
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
        console.log("Starting  REST query.")
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
        showDashboardSection(dashboardDataErrorNode);
        doneWithXhrRequest();
    }

    ConDecBranchesDashboard.prototype.processData = function processData(data) {
        processXhrResponseData(data);
    }

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
        showDashboardSection(dashboardProcessingNode)
    }

    function warnStillProcessing() {
        dashboardProcessingNode.classList.add("error");
        console.warn("Still processing request for: "+processing);
    }

    function countElementType(type, branch) {
        if (!type || !branch || !branch.elemnts || !branch.elements.length) {
            return 0;
        }
        return branch.elements.filter(function(e){
            return e.type.toLowerCase() === type.toLowerCase();
        }).lenght;
    }

    /* lex sorting */
    function sortBranches(branches) {
        return branches.sort(function(a,b){return a.name.localeCompare(b.name); });
    }

    function processBranches(data){
        var branches = data.branches;
        CONDEC_branches = branches;
        for (branchIdx = 0; branchIdx < branches.length; branchIdx++) {
            var lastBranch = conDecLinkBranchCandidates.extractPositions(branches[branchIdx]);

            // these elements are sorted by commit age and occurrence in message
            var lastBranchElementsFromMessages =
            lastBranch.elements.filter(function(e){return e.key.sourceTypeCommitMessage;});

            // these elements are not sorted, we want only B(final) files.
            var lastBranchElementsFromFiles_BUT_NotSorted =
            lastBranch.elements.filter(function(e){return e.key.codeFileB;})

            // sort file elements
            var lastBranchElementsFromFiles =
            conDecLinkBranchCandidates.sortRationaleDiffOfFiles(lastBranchElementsFromFiles_BUT_NotSorted);

            var lastBranchRelevantElementsSortedWithPosition =
            lastBranchElementsFromMessages.concat(lastBranchElementsFromFiles);

            // assess relations between rationale and their problems
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
        // sort lexicographically
        branchesQuality = sortBranches(branchesQuality);
        // render charts and plots
        renderData();
    }

    function renderData(){
        function statusWithBranchesReducer(accumulator, currentBranch) {
            BRANCHES_SEPARATOR_TOKEN = ";"
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
            BRANCHES_SEPARATOR_TOKEN = ";"
            var  problems = currentBranch.problems;
            var  nameOfBranch = currentBranch.name;

            var it = problems.keys();
            var result = it.next();

            while (!result.done) {
                var key = result.value;
                if (problems.get(key)>0) {
                    // already has a bran chname
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

        // form data for charts
        statusesForBranchesData = conDecLinkBranchCandidates.getEmptyMapForStatuses("");
        problemTypesOccurrance = conDecLinkBranchCandidates.getEmptyMapForProblemTypes("");

        branchesQuality.reduce(statusWithBranchesReducer, statusesForBranchesData);
        branchesQuality.reduce(problemsWithBranchesReducer, problemTypesOccurrance);

        // render charts
        conDecReport.initializeChartForBranchSource('piechartRich-QualityStatusForBranches'+dashboardUID,
         'Quality status', 'How many branches document rationale well?',statusesForBranchesData);
        conDecReport.initializeChartForBranchSource('piechartRich-ProblemTypesInBranches'+dashboardUID,
         'Total problems distribution', 'which documentation mistakes are most common?',problemTypesOccurrance);

        // remember in global scope
        CONDEC_branchesQuality = branchesQuality;

    }

	global.conDecBranchesDashboard  = new ConDecBranchesDashboard ();
})(window);


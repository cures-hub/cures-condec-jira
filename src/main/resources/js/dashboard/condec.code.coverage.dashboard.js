/*
 This module fills the box plots used in the code coverage dashboard item.

 Requires
 * js/condec.requirements.dashboard.js

 Is referenced in HTML by
 * codeCoverageDashboardItem.vm
 */

 (function (global) {
	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var ConDecCodeCoverageDashboard = function ConDecCodeCoverageDashboard() {
		console.log("ConDecCodeCoverageDashboard constructor");
	};

	ConDecCodeCoverageDashboard.prototype.init = function init(projectKey, linkDistance) {
		document.getElementById("condec-dashboard-code-coverage-linkdistance-selection").classList.remove("hidden");
		getHTMLNodes("condec-code-coverage-dashboard-contents-container"
			, "condec-code-coverage-dashboard-contents-data-error"
			, "condec-code-coverage-dashboard-no-project"
			, "condec-code-coverage-dashboard-processing"
			, "condec-code-coverage-dashboard-nogit-error");

		getMetrics(projectKey, linkDistance);
	};

	function getHTMLNodes(containerName, dataErrorName, noProjectName, processingName, noGitName) {
		dashboardContentNode = document.getElementById(containerName);
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

	function getMetrics(projectKey, linkDistance) {
		if (!projectKey || !projectKey.length || !projectKey.length > 0) {
			return;
		}
		if (!linkDistance || !(linkDistance >= 0)) {
			return;
		}
		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		showDashboardSection(dashboardProcessingNode);
		url = conDecAPI.restPrefix + "/dashboard/codeCoverage.json?projectKey=" + projectKey
			+ "&linkDistance=" + linkDistance;

		console.log("Starting REST query.");
		AJS.$.ajax({
			url: url,
			type: "get",
			dataType: "json",
			async: true,
			success: conDecCodeCoverageDashboard.processData,
			error: conDecCodeCoverageDashboard.processDataBad
		});
	}

	ConDecCodeCoverageDashboard.prototype.processDataBad = function processDataBad(data) {
		try {
            console.log(data.responseJSON.error);
        } catch (e) {
            // nothing here…
        }
		showDashboardSection(dashboardDataErrorNode);
	};

	ConDecCodeCoverageDashboard.prototype.processData = function processData(data) {
		processXhrResponseData(data);
	};

	function processXhrResponseData(data) {
		doneWithXhrRequest();
		showDashboardSection(dashboardContentNode);
		renderData(data);
	}

	function doneWithXhrRequest() {
		dashboardProcessingNode.classList.remove("error");
		showDashboardSection(dashboardProcessingNode);
	}

	function getMap(jsonString) {
        jsonString = jsonString.replace("\{", "").replace("\}", "");
		jsonString = jsonString.replaceAll("\"", "");
        
		var jsMap = new Map();
		var mapEntries = jsonString.split(",");
        
		for (i = 0; i < mapEntries.length; i++) {
            var mapEntry = mapEntries[i].split(":");
			jsMap.set(mapEntry[0], mapEntry[1]);
		}
		return jsMap;
	}

    function renderData(data) {
		var jsonstr = JSON.stringify(data);
		var json = JSON.parse(jsonstr);

		/*  init data for charts */
		var issuesPerCodeFile = new Map();
		var decisionsPerCodeFile = new Map();
		var decisionDocumentedForCodeFile = new Map();
		var issueDocumentedForCodeFile = new Map();
        
		/* set something for box plots in case no data will be added to them */
		issuesPerCodeFile.set("none", 0);
		decisionsPerCodeFile.set("none", 0);

		decisionDocumentedForCodeFile.set("no code classes", "");
		issueDocumentedForCodeFile.set("no rationale elements", "");

		/* form data for charts */
		issuesPerCodeFile = getMap(JSON.stringify(json.issuesPerCodeFile));
		decisionsPerCodeFile = getMap(JSON.stringify(json.decisionsPerCodeFile));
		decisionDocumentedForCodeFile = getMap(JSON.stringify(json.decisionDocumentedForCodeFile));
		issueDocumentedForCodeFile = getMap(JSON.stringify(json.issueDocumentedForCodeFile));

        /* render box-plots */
		ConDecReqDash.initializeChart("boxplot-IssuesPerCodeFile",
			"", "# Issues per Code File", issuesPerCodeFile);
		ConDecReqDash.initializeChart("boxplot-DecisionsPerCodeFile",
			"", "# Decisions per Code File", decisionsPerCodeFile);
		/* render pie-charts */
		ConDecReqDash.initializeChart("piechartRich-DecisionDocumentedForCodeFile",
			"", "For how many code files is an issue documented?", decisionDocumentedForCodeFile);
		ConDecReqDash.initializeChart("piechartRich-IssueDocumentedForCodeFile",
			"", "For how many code files is a decision documented?", issueDocumentedForCodeFile);
	}

	global.conDecCodeCoverageDashboard = new ConDecCodeCoverageDashboard();
})(window);
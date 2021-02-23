/*
 This module fills the box plots and pie charts used in the rationale completeness dashboard item.

 Requires
 * js/condec.requirements.dashboard.js

 Is referenced in HTML by
 * rationaleCompletenessDashboardItem.vm
 */

(function (global) {
	var processing = null;

	var dashboardContentNode;
	var dashboardDataErrorNode;
	var dashboardNoContentsNode;
	var dashboardProcessingNode;
	var dashboardProjectWithoutGit;

	var ConDecRationaleCompletenessDashboard = function ConDecRationaleCompletenessDashboard() {
		console.log("ConDecRationaleCompletenessDashboard constructor");
	};

	ConDecRationaleCompletenessDashboard.prototype.init = function init(projectKey) {
		getHTMLNodes("condec-rationale-completeness-dashboard-contents-container"
			, "condec-rationale-completeness-dashboard-contents-data-error"
			, "condec-rationale-completeness-dashboard-no-project"
			, "condec-rationale-completeness-dashboard-processing"
			, "condec-rationale-completeness-dashboard-nogit-error");

		getMetrics(projectKey);
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

	function getMetrics(projectKey) {
		if (!projectKey || !projectKey.length || !projectKey.length > 0) {
			return;
		}
		/*
		 * on XHR HTTP failure codes the code aborts instead of processing with
		 * processDataBad() !? if (processing) { return warnStillProcessing(); }
		 */
		showDashboardSection(dashboardProcessingNode);
		url = conDecAPI.restPrefix + "/dashboard/rationaleCompleteness.json?projectKey=" + projectKey;
		/* get cache or server data? */
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
		} else {
			localStorage.setItem("condec.restCacheTTL", 1000 * 60 * 3); /*
																	 * init 3
																	 * minute
																	 * caching
																	 */
		}
		console.log("Starting  REST query.");
		AJS.$.ajax({
			url: url,
			type: "get",
			dataType: "json",
			async: true,
			success: conDecRationaleCompletenessDashboard.processData,
			error: conDecRationaleCompletenessDashboard.processDataBad
		});
	}

	ConDecRationaleCompletenessDashboard.prototype.processDataBad = function processDataBad(data) {
		console.log(data.responseJSON.error);
		showDashboardSection(dashboardDataErrorNode);
	};

	ConDecRationaleCompletenessDashboard.prototype.processData = function processData(data) {
		processXhrResponseData(data);
	};

	function processXhrResponseData(data) {
		doneWithXhrRequest();
		showDashboardSection(dashboardContentNode);
		data.timestamp = Date.now();
		localStorage.setItem(url, JSON.stringify(data, null, 1));
		processing = null;
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
		var issuesSolvedByDecision = new Map();
		var decisionsSolvingIssues = new Map();
		var proArgumentDocumentedForDecision = new Map();
		var conArgumentDocumentedForAlternative = new Map();
		var conArgumentDocumentedForDecision = new Map();
		var proArgumentDocumentedForAlternative = new Map();

		/* set something in case no data will be added to them */
		issuesSolvedByDecision.set("none", "");
		decisionsSolvingIssues.set("none", "");
		proArgumentDocumentedForDecision.set("none", "");
		conArgumentDocumentedForAlternative.set("none", "");
		conArgumentDocumentedForDecision.set("none", "");
		proArgumentDocumentedForAlternative.set("none", "");

		/* form data for charts */
		issuesSolvedByDecision = getMap(JSON.stringify(json.issuesSolvedByDecision));
		decisionsSolvingIssues = getMap(JSON.stringify(json.decisionsSolvingIssues));
		proArgumentDocumentedForDecision = getMap(JSON.stringify(json.proArgumentDocumentedForDecision));
		conArgumentDocumentedForAlternative = getMap(JSON.stringify(json.conArgumentDocumentedForAlternative));
		conArgumentDocumentedForDecision = getMap(JSON.stringify(json.conArgumentDocumentedForDecision));
		proArgumentDocumentedForAlternative = getMap(JSON.stringify(json.proArgumentDocumentedForAlternative));

		/* render pie-charts */
		ConDecReqDash.initializeChart("piechartRich-IssuesSolvedByDecision",
			"", "How many issues (=decision problems) are solved by a decision?", issuesSolvedByDecision);
		ConDecReqDash.initializeChart("piechartRich-DecisionsSolvingIssues",
			"", "For how many decisions is the issue (=decision problem) documented?", decisionsSolvingIssues);
		ConDecReqDash.initializeChart("piechartRich-ProArgumentDocumentedForDecision",
			"", "How many decisions have at least one pro argument documented?", proArgumentDocumentedForDecision);
		ConDecReqDash.initializeChart("piechartRich-ConArgumentDocumentedForDecision",
			"", "How many decisions have at least one con argument documented?", conArgumentDocumentedForDecision);
		ConDecReqDash.initializeChart("piechartRich-ProArgumentDocumentedForAlternative",
			"", "How many alternatives have at least one pro argument documented?", proArgumentDocumentedForAlternative);
		ConDecReqDash.initializeChart("piechartRich-ConArgumentDocumentedForAlternative",
			"", "How many alternatives have at least one con argument documented?", conArgumentDocumentedForAlternative);
	}

	global.conDecRationaleCompletenessDashboard = new ConDecRationaleCompletenessDashboard();
})(window);
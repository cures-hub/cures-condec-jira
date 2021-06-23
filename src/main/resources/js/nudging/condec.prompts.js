(function(global) {

	const ConDecPrompt = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/nudging";
		jQuery(document).ajaxComplete(function(event, request, settings) {
			if (settings.url.includes("WorkflowUIDispatcher.jspa")) {
				// just-in-time prompts when status changes
				const issueKey = conDecAPI.getIssueKey();
				// Create unified prompt
				document.getElementById("unified-prompt-header").innerHTML = "Before you close " + issueKey + "...";
				conDecPrompt.promptNonValidatedElements();
				conDecPrompt.promptDefinitionOfDoneChecking();
				conDecPrompt.promptLinkSuggestion();

				AJS.dialog2("#unified-prompt").show();
			}
		});
	};

	ConDecPrompt.prototype.promptLinkSuggestion = function() {
		const issueId = JIRA.Issue.getIssueId();
		const projectKey = conDecAPI.projectKey;
		if (issueId === null || issueId === undefined) {
			return;
		}
		// TODO: check on the approval necessity and when the check gets turned off
		// conDecLinkRecommendationAPI.doesElementNeedApproval(projectKey, issueId, "i")
		// 	.then((isApprovalNeeded) => {
		// 		if (!isApprovalNeeded) {
		// 			return;
		// 		}
		Promise.all([conDecLinkRecommendationAPI.getDuplicateKnowledgeElement(projectKey, issueId, "i"),
			conDecLinkRecommendationAPI.getRelatedKnowledgeElements(projectKey, issueId, "i")]) // TODO: could add list of the elements here
			.then((values) => {
				let numDuplicates = (values[0].length);
				let numRelated = (values[1].length);
				console.log(numDuplicates)
				if (numDuplicates + numRelated > 0) {
					document.getElementById("link-recommendation-prompt-num-link-recommendations").innerHTML = numRelated;
					document.getElementById("link-recommendation-prompt-num-duplicate-recommendations").innerHTML = numDuplicates;
				}
			});

	}

	ConDecPrompt.prototype.promptDefinitionOfDoneChecking = function() {
		var projectKey = conDecAPI.getProjectKey();
		if (projectKey === null || projectKey === undefined) {
			return;
		}

		var issueKey = conDecAPI.getIssueKey();
		if (issueKey === null || issueKey === undefined) {
			return;
		}

		var filterSettings = {
			"projectKey": projectKey,
			"selectedElement": issueKey,
		}
		conDecAPI.getFilterSettings(projectKey, "", settings => {

			document.getElementById("condec-prompt-minimum-coverage").innerHTML = settings.minimumDecisionCoverage;
			document.getElementById("condec-prompt-link-distance").innerHTML = settings.linkDistance;
		});

		conDecDoDCheckingAPI.getCoverageOfJiraIssue(projectKey, issueKey, (coverage) => {
			document.getElementById("condec-prompt-issue-coverage").innerHTML = coverage["Issue"];
			document.getElementById("condec-prompt-decision-coverage").innerHTML = coverage["Decision"];
		});


		document.getElementById("definition-of-done-checking-prompt-jira-issue-key").innerHTML = conDecAPI.getIssueKey();


	}

	ConDecPrompt.prototype.promptNonValidatedElements = function () {
		const issueKey = conDecAPI.getIssueKey();
		if (issueKey === null || issueKey === undefined) {
			return;
		}

		conDecTextClassificationAPI.getNonValidatedElements(conDecAPI.projectKey, issueKey)
			.then(response => {
				if (response["nonValidatedElements"].length === 0) {
					return;
				}
				const nonValidatedElements = response["nonValidatedElements"]

				document.getElementById("num-non-validated-elements").innerHTML = response["nonValidatedElements"].length


				let tableContents = "";
				nonValidatedElements.forEach(recommendation => {
					let tableRow = "<tr>";
					tableRow += "<td> " + recommendation.summary + "</td>";
					tableRow += "<td>" + recommendation.type + "</td>";
					tableRow += "</tr>";
					tableContents += tableRow;

				});
				document.getElementById("non-validated-table-body").innerHTML = tableContents;
				document.getElementById("non-validated-elements-validate-button").onclick = function () {
					conDecTextClassificationAPI.validateAllElements(conDecAPI.projectKey, issueKey);
				};

			})
	}


	global.conDecPrompt = new ConDecPrompt();
})(window);
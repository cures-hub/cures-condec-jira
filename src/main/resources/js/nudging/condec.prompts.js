(function(global) {

	const ConDecPrompt = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/nudging";
		jQuery(document).ajaxComplete(function(event, request, settings) {
			if (settings.url.includes("WorkflowUIDispatcher.jspa")) {
				// just-in-time prompts when status changes
				conDecPrompt.promptLinkSuggestion();
				conDecPrompt.promptDefinitionOfDoneChecking();
			}
		});
	};

	ConDecPrompt.prototype.promptLinkSuggestion = function() {
		var issueId = JIRA.Issue.getIssueId();
		var projectKey = conDecAPI.projectKey;
		if (issueId === null || issueId === undefined) {
			return;
		}
		conDecLinkRecommendationAPI.doesElementNeedApproval(projectKey, issueId, "i")
			.then((isApprovalNeeded) => {
				if (!isApprovalNeeded) {
					return;
				}
				Promise.all([conDecLinkRecommendationAPI.getDuplicateKnowledgeElement(projectKey, issueId, "i"),
				conDecLinkRecommendationAPI.getRelatedKnowledgeElements(projectKey, issueId, "i")]).then(
					(values) => {
						let numDuplicates = (values[0].length);
						let numRelated = (values[1].length);
						if (numDuplicates + numRelated > 0) {
							document.getElementById("link-recommendation-prompt-jira-issue-key").innerHTML = conDecAPI.getIssueKey();
							document.getElementById("link-recommendation-prompt-num-link-recommendations").innerHTML = numRelated;
							document.getElementById("link-recommendation-prompt-num-duplicate-recommendations").innerHTML = numDuplicates;
							var flag = AJS.flag({
								body: document.getElementById("link-recommendation-prompt").outerHTML,
								title: "Related Knowledge Elements Detected!"
							});
							document.getElementById("link-recommendation-prompt-button").onclick = function() {
								conDecLinkRecommendationAPI.approveInconsistencies(issueId);
								flag.close();
							};
						}
					});
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

		var FilterSettings = {
			"projectKey": projectKey,
			"selectedElement": issueKey,
		}

		conDecDoDCheckingAPI.doesElementNeedCompletenessApproval(FilterSettings)
			.then(isDoDViolated => {
				if (!isDoDViolated) {
					return;
				}
				document.getElementById("definition-of-done-checking-prompt-jira-issue-key").innerHTML = conDecAPI.getIssueKey();
				var flag = AJS.flag({
					body: document.getElementById("definition-of-done-checking-prompt").outerHTML,
					title: "Definition of Done Violated!",
					type: "warning"

				});
				document.getElementById("definition-of-done-checking-prompt-button").onclick = function() {
					flag.close();
				};
			});
	}

	global.conDecPrompt = new ConDecPrompt();
})(window);
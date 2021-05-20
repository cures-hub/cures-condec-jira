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
								conDecLinkRecommendationAPI.approveInconsistencies();
								flag.close();
							};
						}
					});
			});
	}

	ConDecPrompt.prototype.promptDefinitionOfDoneChecking = function() {
		var issueKey = conDecAPI.getIssueKey();
		if (issueKey === null || issueKey === undefined) {
			return;
		}
		var filterSettings = {
			"projectKey": conDecAPI.projectKey,
			"selectedElement": issueKey
		}
		// TODO Show exact DoD criteria that are violated
		conDecDoDCheckingAPI.doesElementNeedCompletenessApproval(filterSettings)
			.then(isDoDViolated => {
				if (!isDoDViolated) {
					return;
				}
				// TODO Add velocity template and move HTML code there
				conDecPrompt.dodCheckingPrompt = showWarning("Incomplete decision knowledge!",
					'Issue <strong>'
					+ issueKey
					+ '</strong> contains some incomplete documented decision knowledge. <br/>'
					+ '<ul class="aui-nav-actions-list">'
					+ '<li>'
					+ '<button id="completeness-check-dialog-submit-button" '
					+ 'onclick="conDecPrompt.dodCheckingPrompt.close()" class="aui-button aui-button-link">'
					+ 'Confirm'
					+ '</button>'
					+ '</li>'
					+ '</ul>');
			});
	}

	function showWarning(title, message) {
		return AJS.flag({
			type: "warning",
			close: "manual",
			title: title,
			body: message
		});
	};

	global.conDecPrompt = new ConDecPrompt();
})(window);
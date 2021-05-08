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
		conDecLinkSuggestionAPI.doesElementNeedApproval(projectKey, issueId, "i")
			.then((isApprovalNeeded) => {
				if (!isApprovalNeeded) {
					return;
				}
				Promise.all([conDecLinkSuggestionAPI.getDuplicateKnowledgeElement(projectKey, issueId, "i"),
				conDecLinkSuggestionAPI.getRelatedKnowledgeElements(projectKey, issueId, "i")]).then(
					(values) => {
						let numDuplicates = (values[0].duplicates.length);
						let numRelated = (values[1].relatedIssues.length);
						if (numDuplicates + numRelated > 0) {
							conDecLinkSuggestionAPI.consistencyCheckFlag = showWarning(
								'Unlinked related knowledge elements detected!',
								'Issue <strong>'
								+ conDecAPI.getIssueKey()
								+ '</strong> contains some detected inconsistencies. <br/>'
								+ '<ul>'
								+ '<li> ' + numRelated + ' possibly related issues </li>'
								+ '<li> ' + numDuplicates + ' possible duplicates </li>'
								+ '</ul>'
								+ '<ul class="aui-nav-actions-list">'
								+ '<li>'
								+ '<button id="consistency-check-dialog-submit-button" '
								+ 'onclick="conDecLinkSuggestionAPI.approveInconsistencies()" class="aui-button aui-button-link">'
								+ 'I approve the consistency of this knowledge element!'
								+ '</button>'
								+ '</li>'
								+ '</ul>'
							);
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
		conDecAPI.doesElementNeedCompletenessApproval(filterSettings)
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
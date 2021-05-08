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
		let that = consistencyAPI;
		this.issueId = JIRA.Issue.getIssueId();
		this.projectKey = conDecAPI.projectKey;
		if (that.issueId !== null && that.issueId !== undefined) {
			consistencyAPI.doesElementNeedApproval(that.projectKey, that.issueId, "i")
				.then((response) => {
					if (response.needsApproval) {
						Promise.all([consistencyAPI.getDuplicateKnowledgeElement(that.projectKey, that.issueId, "i"),
						consistencyAPI.getRelatedKnowledgeElements(that.projectKey, that.issueId, "i")]).then(
							(values) => {
								let numDuplicates = (values[0].duplicates.length);
								let numRelated = (values[1].relatedIssues.length);
								if (numDuplicates + numRelated > 0) {
									that.consistencyCheckFlag = AJS.flag({
										type: 'warning',
										title: 'Possible inconsistencies detected!',
										close: 'manual',
										body: 'Issue <strong>'
											+ conDecAPI.getIssueKey()
											+ '</strong> contains some detected inconsistencies. <br/>'
											+ '<ul>'
											+ '<li> ' + numRelated + ' possibly related issues </li>'
											+ '<li> ' + numDuplicates + ' possible duplicates </li>'
											+ '</ul>'
											+ '<ul class="aui-nav-actions-list">'
											+ '<li>'
											+ '<button id="consistency-check-dialog-submit-button" '
											+ 'onclick="consistencyAPI.approveInconsistencies()" class="aui-button aui-button-link">'
											+ 'I approve the consistency of this knowledge element!'
											+ '</button>'
											+ '</li>'
											+ '</ul>'
									});
								}

							});

					}
				});
		}
	}

	ConDecPrompt.prototype.promptDefinitionOfDoneChecking = function() {
		let that = this;
		this.issueKey = conDecAPI.getIssueKey();
		if (that.issueKey !== null && that.issueKey !== undefined) {
			var filterSettings = {
				"projectKey": conDecAPI.projectKey,
				"selectedElement": this.issueKey
			}
			conDecAPI.doesElementNeedCompletenessApproval(filterSettings)
				.then((response) => {
					if (response.needsCompletenessApproval) {
						Promise.all([]).then(
							() => {
								that.consistencyCheckFlag = showWarning("Imcomplete decision knowledge!",
									'Issue <strong>'
									+ this.issueKey
									+ '</strong> contains some incomplete documented decision knowledge. <br/>'
									+ '<ul class="aui-nav-actions-list">'
									+ '<li>'
									+ '<button id="completeness-check-dialog-submit-button" '
									+ 'onclick="consistencyAPI.consistencyCheckFlag.close()" class="aui-button aui-button-link">'
									+ 'Confirm'
									+ '</button>'
									+ '</li>'
									+ '</ul>');
							});
					}
				});
		}
	}

	var showWarning = function(title, message) {
		return AJS.flag({
			type: "warning",
			close: "manual",
			title: title,
			body: message
		});
	};

	global.conDecPrompt = new ConDecPrompt();
})(window);
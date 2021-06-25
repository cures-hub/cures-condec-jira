(function(global) {

	const ConDecPrompt = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/nudging";
		jQuery(document).ajaxComplete(function(event, request, settings) {
			if (settings.url.includes("WorkflowUIDispatcher.jspa")) {
				// just-in-time prompts when status changes
				var params = new URLSearchParams(settings.url.replaceAll("?", "&"));
				var id = params.get("id");
				var actionId = params.get("action");
				conDecNudgingAPI.isPromptEventActivated("DOD_CHECKING", id, actionId).then((isActivated) => {
					if (isActivated) {
						conDecPrompt.promptDefinitionOfDoneChecking();
					}
				});
				conDecNudgingAPI.isPromptEventActivated("LINK_RECOMMENDATION", id, actionId).then((isActivated) => {
					if (isActivated) {
						conDecPrompt.promptLinkSuggestion();
					}
				});
				conDecNudgingAPI.isPromptEventActivated("TEXT_CLASSIFICATION", id, actionId).then((isActivated) => {
					if (isActivated) {
						conDecPrompt.promptNonValidatedElements();
					}
				});
			}
		});
	};

	ConDecPrompt.prototype.promptLinkSuggestion = function() {
		var issueId = JIRA.Issue.getIssueId();
		var projectKey = conDecAPI.projectKey;
		if (issueId === null || issueId === undefined) {
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
						flag.close();
					};
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

		conDecDoDCheckingAPI.getFailedDefinitionOfDoneCriteria(filterSettings, function(result) {
			if (!result || !result.length) {
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
		})
	}

	ConDecPrompt.prototype.promptNonValidatedElements = function() {
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

				document.getElementById("non-validated-elements-prompt-jira-issue-key").innerHTML = issueKey;
				document.getElementById("num-non-validated-elements").innerHTML = response["nonValidatedElements"].length

				const flag = AJS.flag({
					body: document.getElementById("non-validated-elements-prompt").outerHTML,
					title: "Non-validated elements found!",
					type: "warning"
				})
				document.getElementById("non-validated-elements-validate-button").onclick = function() {
					conDecTextClassificationAPI.validateAllElements(conDecAPI.projectKey, issueKey);
					flag.close();

				};
				document.getElementById("non-validated-elements-ignore-button").onclick = function() {
					flag.close();
				};


				let tableContents = "";
				nonValidatedElements.forEach(recommendation => {
					let tableRow = "<tr>";
					tableRow += "<td> " + recommendation.summary + "</td>";
					tableRow += "<td>" + recommendation.type + "</td>";
					tableRow += "</tr>";
					tableContents += tableRow;

				});
				document.getElementById("non-validated-table-body").innerHTML = tableContents;
			})
	}

	global.conDecPrompt = new ConDecPrompt();
})(window);
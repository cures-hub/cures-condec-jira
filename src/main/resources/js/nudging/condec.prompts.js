(function (global) {

	const ConDecPrompt = function () {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/nudging";
		jQuery(document).ajaxComplete(function (event, request, settings) {
			if (settings.url.includes("WorkflowUIDispatcher.jspa")) {
				const issueKey = conDecAPI.getIssueKey();
				// Create unified prompt
				document.getElementById("unified-prompt-header").innerHTML = "Before you close " + issueKey + "...";

				const unifiedPromptElement = document.getElementById("unified-prompt");
				document.getElementById("warning-dialog-fix-elements").onclick = function () {
					AJS.dialog2(unifiedPromptElement).hide();
				}// TODO change this so it actually stops the event from continuing

				document.getElementById("warning-dialog-continue").onclick = function () {
					AJS.dialog2(unifiedPromptElement).hide();
				}


				// just-in-time prompts when status changes
				const params = new URLSearchParams(settings.url.replaceAll("?", "&"));
				const id = params.get("id");
				const actionId = params.get("action");
				let atLeastOnePrompt = false; // if no prompts are activated, don't show the dialog at all
				Promise.all([
					conDecNudgingAPI.isPromptEventActivated("DOD_CHECKING", id, actionId),
					conDecNudgingAPI.isPromptEventActivated("LINK_RECOMMENDATION", id, actionId),
					conDecNudgingAPI.isPromptEventActivated("TEXT_CLASSIFICATION", id, actionId),
					conDecNudgingAPI.isPromptEventActivated("DECISION_GUIDANCE", id, actionId)
				])
					.then(([isDoDCheckActivated, isLinkRecommendationActivated, isTextClassificationActivated, isDecisionGuidanceActivated]) => {


						if (isDoDCheckActivated
							|| isLinkRecommendationActivated
							|| isTextClassificationActivated
							|| isDecisionGuidanceActivated) {
							AJS.dialog2(unifiedPromptElement).show()
						}

						if (isDoDCheckActivated) {
								conDecPrompt.promptDefinitionOfDoneChecking();
								$(document).ready(function () {
									document.getElementById("definition-of-done-prompt").style.display = "block";
								})
							}

							if (isLinkRecommendationActivated) {
								conDecPrompt.promptLinkSuggestion();
								$(document).ready(function () {
									document.getElementById("link-recommendation-prompt").style.display = "block";
								})
							}
							if (isTextClassificationActivated) {
								conDecPrompt.promptNonValidatedElements();
								$(document).ready(function () {
									document.getElementById("non-validated-elements-prompt").style.display = "block";
								})
							}
							if (isDecisionGuidanceActivated) {
								atLeastOnePrompt = true;
								conDecPrompt.promptDecisionGuidance()
								$(document).ready(function () {
									document.getElementById("decision-guidance-prompt").style.display = "block";
								});
							}
						}
					)


			}
		});
	}


	ConDecPrompt.prototype.promptLinkSuggestion = function () {
		const issueId = JIRA.Issue.getIssueId();
		const projectKey = conDecAPI.projectKey;
		if (issueId === null || issueId === undefined) {
			return;
		}

		Promise.all([conDecLinkRecommendationAPI.getDuplicateKnowledgeElement(projectKey, issueId, "i"),
			conDecLinkRecommendationAPI.getRelatedKnowledgeElements(projectKey, issueId, "i")]) // TODO: could add list of the elements here
			.then((values) => {
				let numDuplicates = (values[0].length);
				let numRelated = (values[1].length);
				if (numDuplicates + numRelated > 0) {
					document.getElementById("link-recommendation-prompt-num-link-recommendations").innerHTML = numRelated;
					document.getElementById("link-recommendation-prompt-num-duplicate-recommendations").innerHTML = numDuplicates;
				}
			});
	}

	ConDecPrompt.prototype.promptDefinitionOfDoneChecking = function () {
		const projectKey = conDecAPI.getProjectKey();
		if (projectKey === null || projectKey === undefined) {
			return;
		}

		const issueKey = conDecAPI.getIssueKey();
		if (issueKey === null || issueKey === undefined) {
			return;
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
	};
	ConDecPrompt.prototype.promptDecisionGuidance = function () {
		const issueKey = conDecAPI.getIssueKey();
		if (issueKey === null || issueKey === undefined) {
			return;
		}
		const projectKey = conDecAPI.getProjectKey();
		conDecDecisionGuidanceAPI.getRecommendations(projectKey, issueKey, (recommendationsMap, error) => {
			if (error === null || error === undefined) {
				document.getElementById("num-decision-problems").innerHTML = Object.keys(recommendationsMap).length.toString()
				Object.keys(recommendationsMap).forEach((id) => {
					conDecAPI.getDecisionKnowledgeElement(id, 's', (decisionProblem) => {
						let tableRow = "<tr>";
						tableRow += "<td>" + decisionProblem.summary + "</td>";
						tableRow += "<td>" + recommendationsMap[id].length + "</td>";
						tableRow += "</tr>";
						document.getElementById("decision-problems-table-body").innerHTML += tableRow;
					});
				});
			} else {
				console.log("Error in making decision guidance prompt table was: ", error);
			}
		});
	};

	global.conDecPrompt = new ConDecPrompt();
})
(window);
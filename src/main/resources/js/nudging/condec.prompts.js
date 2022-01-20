/**
 * This module is responsible to show a just-in-time prompt when a developer changes the status of a Jira issue.
 * The just-in-time prompt covers recommendations regarding "smart features" for rationale management. 
 */
(function(global) {

	/**
	 * @issue How can we trigger a just-in-time prompt when the developer changes the status of a Jira issue?
	 * @decision We listen for two events (WorkflowUIDispatcher and AjaxIssueEditAction) using jQuery(document).ajaxComplete!
	 * @pro These events somehow reload the HTML elements. After these events have finished, the HTML elements can be found 
	 * even if REST calls for the recommendation generation take quite long.
	 * @alternative Use only the WorkflowUIDispatcher event and jQuery(document).ready to wait for the page to be loaded.
	 * @con Does not work: When the REST calls finish, the HTML elements cannot be found.
	 */
	const ConDecPrompt = function() {
		jQuery(document).ajaxComplete(function(event, request, settings) {
			if (settings.url.includes("WorkflowUIDispatcher.jspa")) {
				const params = new URLSearchParams(settings.url.replaceAll("?", "&"));
				const id = params.get("id");
				const actionId = params.get("action");

				jQuery(document).ajaxComplete(function(event, request, settings) {
					if (settings.url.includes("AjaxIssueEditAction")) {

						const jiraIssueKey = conDecAPI.getIssueKey();
						const projectKey = conDecAPI.getProjectKey();
						document.getElementById("unified-prompt-header").innerHTML = "Recommendations for " + jiraIssueKey;
						const promptDialog = document.getElementById("unified-prompt");
						document.getElementById("warning-dialog-continue").onclick = function() {
							AJS.dialog2(promptDialog).hide();
						}

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
									AJS.dialog2(promptDialog).show();
								}
								if (isDoDCheckActivated) {
									conDecPrompt.promptDefinitionOfDoneChecking(projectKey, jiraIssueKey);
									document.getElementById("definition-of-done-checking-prompt").style.display = "block";
									document.getElementById("go-to-quality-check-tab").onclick =
										() => openDetailView("quality-check-tab", promptDialog);
								}
								if (isLinkRecommendationActivated) {
									conDecPrompt.promptLinkSuggestion(projectKey);
									document.getElementById("link-recommendation-prompt").style.display = "block";
									document.getElementById("go-to-link-recomendation-tab").onclick =
										() => openDetailView("link-recommendation-tab", promptDialog);
								}
								if (isTextClassificationActivated) {
									conDecPrompt.promptNonValidatedElements(projectKey, jiraIssueKey);
									document.getElementById("non-validated-elements-prompt").style.display = "block";
									document.getElementById("go-to-classification-tab").onclick =
										() => openDetailView("text-classification-tab", promptDialog);
								}
								if (isDecisionGuidanceActivated) {
									conDecPrompt.promptDecisionGuidance(projectKey);
									document.getElementById("decision-guidance-prompt").style.display = "block";
									document.getElementById("go-to-decision-guidance-tab").onclick =
										() => openDetailView("decision-guidance-tab", promptDialog);
								}
							});
					}
				});
			}
		});
	};

	function openDetailView(tabId, promptDialog) {
		AJS.tabs.change(jQuery("a[href='#" + tabId + "']"));
		AJS.dialog2(promptDialog).hide();
	}

	ConDecPrompt.prototype.promptLinkSuggestion = function(projectKey) {
		const issueId = JIRA.Issue.getIssueId();

		Promise.resolve(conDecLinkRecommendationAPI.getLinkRecommendations(projectKey, issueId, "i"))
			.then(recommendations => {
				let numRecommendations = conDecRecommendation.getNumberOfNonDiscardedRecommendations(recommendations);
				if (numRecommendations > 0) {
					document.getElementById("link-recommendation-prompt-num-link-recommendations").innerText = numRecommendations;
				}
				conDecNudgingAPI.decideAmbientFeedbackForTab(numRecommendations, "menu-item-link-recommendation");
				conDecNudgingAPI.decideCheckIcon(numRecommendations, "link-recommendation-check-icon");
				document.getElementById("link-recommendation-spinner").style.display = "none";
			});
	}

	ConDecPrompt.prototype.promptDefinitionOfDoneChecking = function(projectKey, jiraIssueKey) {
		var filterSettings = {
			"projectKey": projectKey,
			"selectedElement": jiraIssueKey,
		};

		conDecDoDCheckingAPI.getQualityProblems(filterSettings, (qualityProblems) => {
			document.getElementById("dod-spinner").style.display = "none";
			var summary = document.getElementById("definition-of-done-checking-summary");
			if (!qualityProblems.length) {
				summary.innerHTML = "Great work! No <b>violations of the definition of done</b> were found.";
			} else {
				summary.innerHTML = "Please improve the knowledge documentation. The following <b>violations of the definition of done</b> were found:";
				var problemExplanation = document.getElementById("definition-of-done-checking-results");
				problemExplanation.innerHTML = "";
				qualityProblems.forEach(function(problem) {
					problemExplanation.insertAdjacentHTML("beforeend", "<li>" + problem.explanation + "</li>");
				});
			}
			conDecNudgingAPI.decideCheckIcon(qualityProblems.length, "definition-of-done-check-icon");
		});
	}

	ConDecPrompt.prototype.promptNonValidatedElements = function(projectKey, jiraIssueKey) {
		conDecTextClassificationAPI.getNonValidatedElements(projectKey, jiraIssueKey)
			.then(nonValidatedElements => {
				document.getElementById("num-non-validated-elements").innerHTML = nonValidatedElements.length;
				if (nonValidatedElements.length === 0) {
					document.getElementById("non-validated-table-body").innerHTML = "<i>All elements have been validated!</i>";
				} else {
					let tableContents = "";
					nonValidatedElements.forEach(recommendation => {
						let tableRow = "<tr>";
						tableRow += "<td> " + recommendation.summary + "</td>";
						tableRow += "<td>" + recommendation.type + "</td>";
						tableRow += "</tr>";
						tableContents += tableRow;
					});
					document.getElementById("non-validated-table-body").innerHTML = tableContents;
				}
				conDecNudgingAPI.decideAmbientFeedbackForTab(nonValidatedElements.length, "menu-item-text-classification");
				conDecNudgingAPI.decideCheckIcon(nonValidatedElements.length, "text-classification-check-icon");
				document.getElementById("non-validated-spinner").style.display = "none";
			});
	};

	ConDecPrompt.prototype.promptDecisionGuidance = function(projectKey) {
		conDecAPI.getDecisionProblems({}, decisionProblems => {
			var recommendationPromises = [];
			for (decisionProblem of decisionProblems) {
				decisionProblem.projectKey = projectKey;
				recommendationPromises.push(conDecDecisionGuidanceAPI.getRecommendations(decisionProblem, ""));
			}
			Promise.all(recommendationPromises)
				.then(recommendationsForAllProblems => {
					var totalNumberOfRecommendations = 0;
					document.getElementById("decision-problems-table-body").innerHTML = "";
					for (i = 0; i < decisionProblems.length; i++) {
						var numberOfRecommendations = recommendationsForAllProblems[i].length;
						let tableRow = "<tr>";
						tableRow += "<td>" + decisionProblems[i].summary + "</td>";
						tableRow += "<td>" + numberOfRecommendations + "</td>";
						tableRow += "</tr>";
						document.getElementById("decision-problems-table-body").innerHTML += tableRow;
						totalNumberOfRecommendations += numberOfRecommendations;
					}

					document.getElementById("num-decision-problems").innerHTML = decisionProblems.length;
					document.getElementById("num-recommendations").innerHTML = totalNumberOfRecommendations;
					conDecNudgingAPI.decideAmbientFeedbackForTab(totalNumberOfRecommendations, "menu-item-decision-guidance");
					conDecNudgingAPI.decideCheckIcon(totalNumberOfRecommendations, "decision-guidance-check-icon");
					document.getElementById("decision-guidance-spinner").style.display = "none";
				});
		});
	}

	global.conDecPrompt = new ConDecPrompt();
})(window);
(function(global) {

	const ConDecPrompt = function() {
		jQuery(document).ajaxComplete(function(event, request, settings) {
			if (settings.url.includes("WorkflowUIDispatcher.jspa")) {
				const issueKey = conDecAPI.getIssueKey();
				document.getElementById("unified-prompt-header").innerHTML = "Recommendations for " + issueKey;

				const promptDialog = document.getElementById("unified-prompt");

				document.getElementById("warning-dialog-continue").onclick = function() {
					AJS.dialog2(promptDialog).hide();
				}

				// just-in-time prompts when status changes
				const params = new URLSearchParams(settings.url.replaceAll("?", "&"));
				const id = params.get("id");
				const actionId = params.get("action");
				Promise.all([
					conDecNudgingAPI.isPromptEventActivated("DOD_CHECKING", id, actionId),
					conDecNudgingAPI.isPromptEventActivated("LINK_RECOMMENDATION", id, actionId),
					conDecNudgingAPI.isPromptEventActivated("TEXT_CLASSIFICATION", id, actionId),
					conDecNudgingAPI.isPromptEventActivated("DECISION_GUIDANCE", id, actionId)
				])
					.then(([isDoDCheckActivated, isLinkRecommendationActivated, isTextClassificationActivated, isDecisionGuidanceActivated]) => {
						/**
						 * @issue The page is reloaded and the ambient feedback is removed again on the
						 * link recommendation menu item. How can we prevent this?
						 * @alternative Use jQuery(document).ready to wait for the page to be loaded.
						 * @con Does not work, the link recommendation menu item coloring is removed.
						 */
						if (isDoDCheckActivated
							|| isLinkRecommendationActivated
							|| isTextClassificationActivated
							|| isDecisionGuidanceActivated) {
							AJS.dialog2(promptDialog).show()
						}
						if (isDoDCheckActivated) {
							conDecPrompt.promptDefinitionOfDoneChecking();
							document.getElementById("definition-of-done-checking-prompt").style.display = "block";
							document.getElementById("go-to-quality-check-tab").onclick = () => {
								AJS.tabs.change(jQuery('a[href="#quality-check-tab"]'));
								window.open("#quality-check-tab", "blank");
							}
						}
						if (isLinkRecommendationActivated) {
							conDecPrompt.promptLinkSuggestion();
							document.getElementById("link-recommendation-prompt").style.display = "block";

							document.getElementById("go-to-link-recomendation-tab").onclick = () => {
								AJS.tabs.change(jQuery('a[href="#link-recommendation-tab"]'));
								window.open("#link-recommendation-tab", "blank");
							}
						}
						if (isTextClassificationActivated) {
							conDecPrompt.promptNonValidatedElements();
							document.getElementById("non-validated-elements-prompt").style.display = "block";
							document.getElementById("go-to-classification-tab").onclick =
								() => {
									AJS.tabs.change(jQuery('a[href="#text-classification-tab"]'));
									window.open("#text-classification-tab", "blank");

								}
						}
						if (isDecisionGuidanceActivated) {
							conDecPrompt.promptDecisionGuidance();
							document.getElementById("decision-guidance-prompt").style.display = "block";
							document.getElementById("go-to-decision-guidance-tab").onclick =
								() => {
									AJS.tabs.change(jQuery('a[href="#decision-guidance-tab"]'));
									window.open("#decision-guidance-tab", "blank");
								}
						}
					});
			}
		});
	};

	ConDecPrompt.prototype.promptLinkSuggestion = function() {
		const issueId = JIRA.Issue.getIssueId();
		const projectKey = conDecAPI.projectKey;
		if (issueId === null || issueId === undefined) {
			return;
		}

		Promise.all([conDecLinkRecommendationAPI.getDuplicateKnowledgeElement(projectKey, issueId, "i"),
		conDecLinkRecommendationAPI.getRelatedKnowledgeElements(projectKey, issueId, "i")])
			.then((recommendations) => {
				let numDuplicates = conDecRecommendation.getNumberOfNonDiscardedRecommendations(recommendations[0]);
				let numLinkRecommendations = conDecRecommendation.getNumberOfNonDiscardedRecommendations(recommendations[1]);
				if (numDuplicates + numLinkRecommendations > 0) {
					document.getElementById("link-recommendation-prompt-num-link-recommendations").innerText = numLinkRecommendations;
					document.getElementById("link-recommendation-prompt-num-duplicate-recommendations").innerText = numDuplicates;
					conDecNudgingAPI.decideAmbientFeedbackForTab(numDuplicates + numLinkRecommendations, "menu-item-link-recommendation");
				}
				document.getElementById("link-recommendation-spinner").style.display = "none";
			});
	}

	ConDecPrompt.prototype.promptDefinitionOfDoneChecking = function() {
		const projectKey = conDecAPI.getProjectKey();
		if (projectKey === null || projectKey === undefined) {
			return;
		}

		const issueKey = conDecAPI.getIssueKey();
		if (issueKey === null || issueKey === undefined) {
			return;
		}

		var filterSettings = {
			"projectKey": projectKey,
			"selectedElement": issueKey,
		};

		conDecDoDCheckingAPI.getDefinitionOfDone(projectKey, (definitionOfDone) => {
			document.getElementById("condec-prompt-minimum-coverage").innerHTML = definitionOfDone.minimumDecisionsWithinLinkDistance;
			document.getElementById("condec-prompt-link-distance").innerHTML = definitionOfDone.maximumLinkDistanceToDecisions;
		});

		conDecDoDCheckingAPI.getCoverageOfJiraIssue(filterSettings, (coverage) => {
			document.getElementById("condec-prompt-decision-coverage").innerHTML = coverage;
			document.getElementById("dod-spinner").style.display = "none";
		});

		document.getElementById("definition-of-done-checking-prompt-jira-project-key").innerHTML = projectKey;
	}

	ConDecPrompt.prototype.promptNonValidatedElements = function() {
		const issueKey = conDecAPI.getIssueKey();
		if (issueKey === null || issueKey === undefined) {
			return;
		}
		conDecTextClassificationAPI.getNonValidatedElements(conDecAPI.projectKey, issueKey)
			.then(response => {
				const nonValidatedElements = response["nonValidatedElements"]
				document.getElementById("num-non-validated-elements").innerHTML = nonValidatedElements.length;
				conDecNudgingAPI.decideAmbientFeedbackForTab(nonValidatedElements.length, "text-classification-tab");

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
				document.getElementById("non-validated-spinner").style.display = "none";
			})
	};

	ConDecPrompt.prototype.promptDecisionGuidance = function() {
		const issueKey = conDecAPI.getIssueKey();
		if (issueKey === null || issueKey === undefined) {
			return;
		}
		const projectKey = conDecAPI.getProjectKey();

		conDecAPI.getDecisionProblems({}, decisionProblems => {
			// Get an array containing promises that resolve to the numbers of elements in each decision problem.
			const recommendationsNumsPromises = decisionProblems.map(decisionProblem => {
				const filterSettings = {
					"projectKey": projectKey,
					"selectedElementObject": decisionProblem
				};
				return conDecDecisionGuidanceAPI.getRecommendations(filterSettings)
					.then(res => res.length)
					.catch(error => {
						console.log("Error in making decision guidance prompt table was: ", error)
						document.getElementById("decision-problems-table-body").innerHTML = "<i>An error occurred!</i>"
					});
			});
			// Wait for all promises to resolve, then continue adding the numbers of recommendations to the table
			Promise.all(recommendationsNumsPromises)
				.then(numbersOfRecommendations => {
					var totalNumberOfRecommendations = 0;
					decisionProblems.forEach((decisionProblem, index) => {
						let tableRow = "<tr>";
						tableRow += "<td>" + decisionProblem.summary + "</td>";
						tableRow += "<td>" + numbersOfRecommendations[index] + "</td>";
						tableRow += "</tr>";
						document.getElementById("decision-problems-table-body").innerHTML += tableRow;
						totalNumberOfRecommendations += numbersOfRecommendations[index];
					});

					document.getElementById("num-decision-problems").innerHTML = decisionProblems.length;
					document.getElementById("num-recommendations").innerHTML = totalNumberOfRecommendations;
					conDecNudgingAPI.decideAmbientFeedbackForTab(totalNumberOfRecommendations, "menu-item-decision-guidance");
				});
		});
		document.getElementById("decision-guidance-spinner").style.display = "none";
	}

	global.conDecPrompt = new ConDecPrompt();
})
	(window);
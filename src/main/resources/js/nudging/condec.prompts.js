(function(global) {

	const ConDecPrompt = function() {
		/**
		 * @issue The page is reloaded and the ambient feedback is removed again on the
		 * link recommendation menu item. How can we prevent this?
		 * @alternative Use jQuery(document).ready to wait for the page to be loaded.
		 * @con Does not work, the link recommendation menu item coloring is removed.
		 */
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

						// just-in-time prompts when status changes
						Promise.all([
							conDecNudgingAPI.isPromptEventActivated("DOD_CHECKING", id, actionId),
							conDecNudgingAPI.isPromptEventActivated("LINK_RECOMMENDATION", id, actionId),
							conDecNudgingAPI.isPromptEventActivated("TEXT_CLASSIFICATION", id, actionId),
							conDecNudgingAPI.isPromptEventActivated("DECISION_GUIDANCE", id, actionId)
						])
							.then(([isDoDCheckActivated, isLinkRecommendationActivated,	isTextClassificationActivated, isDecisionGuidanceActivated]) => {
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

	ConDecPrompt.prototype.promptDefinitionOfDoneChecking = function(projectKey, jiraIssueKey) {
		var filterSettings = {
			"projectKey": projectKey,
			"selectedElement": jiraIssueKey,
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

	ConDecPrompt.prototype.promptNonValidatedElements = function(projectKey, jiraIssueKey) {
		conDecTextClassificationAPI.getNonValidatedElements(projectKey, jiraIssueKey)
			.then(response => {
				const nonValidatedElements = response["nonValidatedElements"];
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
					document.getElementById("decision-guidance-spinner").style.display = "none";
				});
		});
	}

	global.conDecPrompt = new ConDecPrompt();
})(window);
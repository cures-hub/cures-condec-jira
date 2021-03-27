/**
 * This module implements the communication with the ConDec Java REST API for solution option recommendation (decision guidance).
 *
 * Is required by: conDecDecisionGuidance
 *
 * Is referenced in HTML by settingsForAllProjects.vm
 * settingsForSingleProject.vm
 */
(function(global) {

	var ConDecDecisionGuidanceAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/decisionguidance";
	};

	/*
	 * external references: jiraIssueModule.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.resetRecommendationForKnowledgeElement = function(jiraIssueId, callback) {
		generalApi.postJSON(this.restPrefix + "/resetRecommendationsForKnowledgeElement.json", jiraIssueId,
			function(error, numberOfElements) {
				if (error === null) {
					conDecAPI.showFlag("success", numberOfElements + " decision knowledge elements in the text were found and linked in the knowledge graph.");
					callback();
				}
			});
	};

	/*
	 * external references: condec.decision.guidance
	 */
	ConDecDecisionGuidanceAPI.prototype.getRecommendation = function(projectKey, keyword, issueID, documentationLocation, callback) {
		generalApi.getJSON(this.restPrefix + "/getRecommendation.json?projectKey=" + projectKey + "&keyword=" + keyword + "&issueID=" + issueID + "&documentationLocation=" + documentationLocation,
			function(error, results) {
				callback(results, error);
			});
	};

	/*
	 * external references: condec.decision.guidance
	 */
	ConDecDecisionGuidanceAPI.prototype.getRecommendationEvaluation = function(projectKey, keyword, issueID, knowledgeSources, kResults, documentationLocation, callback) {
		generalApi.getJSON(this.restPrefix + "/getRecommendationEvaluation.json?projectKey=" + projectKey + "&keyword=" + keyword + "&issueID=" + issueID + "&knowledgeSource=" + knowledgeSources + "&kResults=" + kResults + "&documentationLocation=" + documentationLocation,
			function(error, results) {
				callback(results, error);
			});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.setMaxNumberRecommendations = function(projectKey, maxNumberRecommendations) {
		generalApi.postJSON(this.restPrefix + "/setMaxNumberRecommendations.json?projectKey=" + projectKey + "&maxNumberRecommendations=" + maxNumberRecommendations, null, function(
			error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "Maximum number of results are updated to: " + maxNumberRecommendations);
			}
		});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.setSimilarityThreshold = function(projectKey, threshold) {
		generalApi.postJSON(this.restPrefix + "/setSimilarityThreshold.json?projectKey=" + projectKey + "&threshold=" + threshold, null, function(
			error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "The similarity threshold is updated to: " + threshold);
			}
		});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.setIrrelevantWords = function(projectKey, words) {
		generalApi.postJSON(this.restPrefix + "/setIrrelevantWords.json?projectKey=" + projectKey + "&words=" + words, null, function(
			error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "The irrelevant words are updated!");
			}
		});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.setRDFKnowledgeSource = function(projectKey, rdfSource) {
		generalApi.postJSON(this.restPrefix + "/setRDFKnowledgeSource.json?projectKey=" + projectKey, rdfSource, function(
			error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "The Knowledgesource is successfully created. <b>Please refresh the page.</b> ");
			}
		});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.setAddRecommendationDirectly = function(projectKey, addRecommendationDirectly) {
		generalApi.postJSON(this.restPrefix + "/setAddRecommendationDirectly.json?projectKey=" + projectKey + "&addRecommendationDirectly=" + addRecommendationDirectly, null,
			function(error, results) {
				conDecAPI.showFlag("success", "Recommendation settings successfully changed");
			});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.setRecommendationInput = function(projectKey, recommendationInput, isActivated) {
		generalApi.postJSON(this.restPrefix + "/setRecommendationInput.json?projectKey=" + projectKey + "&recommendationInput=" + recommendationInput + "&isActivated=" + isActivated, null,
			function(error, results) {
				if (error) {
					conDecAPI.showFlag("error", error);
				} else if (isActivated) {
					conDecAPI.showFlag("success", "Recommendation input has been successfully <b>activated</b>");
				} else {
					conDecAPI.showFlag("success", "Recommendation input has been successfully <b>deactivated</b>");
				}
			});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.setKnowledgeSourceActivated = function(projectKey, knowledgeSourceName, isActivated) {
		generalApi.postJSON(this.restPrefix + "/setKnowledgeSourceActivated.json?projectKey=" + projectKey + "&knowledgeSourceName=" + knowledgeSourceName + "&isActivated=" + isActivated, null, function(
			error, response) {
			if (error === null) {
				if (isActivated)
					conDecAPI.showFlag("success", "The knowledge source " + knowledgeSourceName + " is activated.");
				else {
					conDecAPI.showFlag("success", "The knowledge source " + knowledgeSourceName + " is deactivated.");
				}
			}
		});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.setProjectSource = function(projectKey, projectSourceKey, isActivated) {
		generalApi.postJSON(this.restPrefix + "/setProjectSource.json?projectKey=" + projectKey + "&projectSourceKey=" + projectSourceKey + "&isActivated=" + isActivated, null, function(
			error, response) {
			if (error === null) {
				if (isActivated) {
					conDecAPI.showFlag("success", "The project  <b>" + projectSourceKey + "</b> is now  <b>activated</b> as a knowledge source!!");
				}
				else {
					conDecAPI.showFlag("success", "The project <b>" + projectSourceKey + "</b> is now <b>deactivated</b> as a knowledge source!!");
				}
			}
		});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.deleteKnowledgeSource = function(projectKey, knowledgeSourceName) {
		generalApi.postJSON(this.restPrefix + "/deleteKnowledgeSource.json?projectKey=" + projectKey + "&knowledgeSourceName=" + knowledgeSourceName, null, function(
			error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "The knowledge source " + knowledgeSourceName + " was successfully deleted.");
			}
		});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.updateKnowledgeSource = function(projectKey, knowledgeSourceName, knowledgeSource) {
		generalApi.postJSON(this.restPrefix + "/updateKnowledgeSource.json?projectKey=" + projectKey + "&knowledgeSourceName=" + knowledgeSourceName, knowledgeSource, function(
			error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "The knowledge source was successfully updated.");
			}
		});
	};

	global.conDecDecisionGuidanceAPI = new ConDecDecisionGuidanceAPI();
})(window);
/**
 * This module implements the communication with the ConDec Java REST API for solution option recommendation (decision guidance).
 *
 * Is required by: conDecDecisionGuidance
 *
 * Is referenced in HTML by
 * settings/decisionguidance/*
 * tabs/decisiontable/*
 * jiraIssueModule.vm
 */
(function (global) {

	var ConDecDecisionGuidanceAPI = function () {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/decisionguidance";
	};

	/*
	 * external references: condec.decision.guidance
	 */
	ConDecDecisionGuidanceAPI.prototype.getRecommendations = function (projectKey, keyword, issueId, documentationLocation, callback) {
		generalApi.getJSON(this.restPrefix + "/recommendations.json?projectKey=" + projectKey + "&keyword=" + keyword + "&issueId=" + issueId + "&documentationLocation=" + documentationLocation,
			function (error, recommendations) {
				recommendations.sort(function (recommendation1, recommendation2) {
					return recommendation2.score.value - recommendation1.score.value;
				});
				callback(recommendations, error);
			});
	};

	/*
	 * external references: condec.decision.guidance
	 */
	ConDecDecisionGuidanceAPI.prototype.getRecommendationEvaluation = function (projectKey, keyword, issueId, knowledgeSources, kResults, documentationLocation, callback) {
		generalApi.getJSON(this.restPrefix + "/recommendationEvaluation.json?projectKey=" + projectKey + "&keyword=" + keyword + "&issueId=" + issueId + "&knowledgeSource=" + knowledgeSources + "&kResults=" + kResults + "&documentationLocation=" + documentationLocation,
			function (error, results) {
				callback(results, error);
			});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.setMaxNumberOfRecommendations = function(projectKey, maxNumberOfRecommendations) {
		generalApi.postJSON(this.restPrefix + "/setMaxNumberOfRecommendations.json?projectKey=" + projectKey + "&maxNumberOfRecommendations=" + maxNumberOfRecommendations, null, function(
			error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "Maximum number of results are updated to: " + maxNumberOfRecommendations);
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
	ConDecDecisionGuidanceAPI.prototype.createRDFKnowledgeSource = function(projectKey, rdfSource) {
		generalApi.postJSON(this.restPrefix + "/createRDFKnowledgeSource.json?projectKey=" + projectKey, rdfSource,
			function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The knowledge source is successfully created. <b>Please refresh the page.</b> ");
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
	ConDecDecisionGuidanceAPI.prototype.setKnowledgeSourceActivated = function(projectKey, knowledgeSourceName, isActivated) {
		generalApi.postJSON(this.restPrefix + "/setKnowledgeSourceActivated.json?projectKey=" + projectKey + "&knowledgeSourceName=" + knowledgeSourceName + "&isActivated=" + isActivated, null, function(
			error, response) {
			if (error === null) {
				if (isActivated) {
					conDecAPI.showFlag("success", "The knowledge source " + knowledgeSourceName + " is activated.");
				} else {
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
					conDecAPI.showFlag("success", "The project <b>" + projectSourceKey + "</b> is now <b>activated</b> as a knowledge source.");
				} else {
					conDecAPI.showFlag("success", "The project <b>" + projectSourceKey + "</b> is now <b>deactivated</b> as a knowledge source.");
				}
			}
		});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.deleteKnowledgeSource = function(projectKey, knowledgeSourceName, callback) {
		generalApi.postJSON(this.restPrefix + "/deleteKnowledgeSource.json?projectKey=" + projectKey + "&knowledgeSourceName=" + knowledgeSourceName, null, function(
			error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "The knowledge source " + knowledgeSourceName + " was successfully deleted.");
				callback();
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

	/*
	 * external references: jiraIssueModule.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.removeRecommendationForKnowledgeElement = function(jiraIssueId, callback) {
		generalApi.postJSON(this.restPrefix + "/removeRecommendationsForKnowledgeElement.json", jiraIssueId,
			function(error, numberOfElements) {
				if (error === null) {
					conDecAPI.showFlag("success", numberOfElements + " decision knowledge elements in the text were found and linked in the knowledge graph.");
					callback();
				}
			});
	};

	global.conDecDecisionGuidanceAPI = new ConDecDecisionGuidanceAPI();
})(window);
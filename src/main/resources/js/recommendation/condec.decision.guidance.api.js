/**
 * This module implements the communication with the ConDec Java REST API for solution option recommendation (decision guidance).
 *
 * Is required by: conDecDecisionGuidance
 *
 * Is referenced in HTML by
 * settings/decisionguidance/*
 * tabs/recommendation/*
 * jiraIssueModule.vm
 */
(function(global) {

	var ConDecDecisionGuidanceAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/decision-guidance";
		this.recommendationsPerProblem = new Map();
	};

	/*
	 * external references: condec.decision.guidance
	 */
	ConDecDecisionGuidanceAPI.prototype.getRecommendations = function(decisionProblem, keywords) {
		var filterSettings = {
			projectKey: conDecAPI.getProjectKey(),
			selectedElementObject: decisionProblem,
			searchTerm: keywords
		}
		if (this.recommendationsPerProblem.has(decisionProblem.id)) {
			return this.recommendationsPerProblem.get(decisionProblem.id);
		}
		return generalApi.postJSONReturnPromise(this.restPrefix + "/recommendations.json", filterSettings)
			.then(recommendations => {
				recommendations = recommendations.sort((a, b) => b.score.value - a.score.value);
				conDecDecisionGuidanceAPI.recommendationsPerProblem.set(filterSettings.selectedElementObject.id, recommendations);
				return recommendations;
			});
	};

	/*
	 * external references: condec.decision.guidance
	 */
	ConDecDecisionGuidanceAPI.prototype.getRecommendationEvaluation = function(projectKey, keyword, issueId, knowledgeSources, kResults, documentationLocation, callback) {
		generalApi.getJSON(this.restPrefix + "/recommendationEvaluation.json?projectKey=" + projectKey + "&keyword=" + keyword + "&issueId=" + issueId + "&knowledgeSource=" + knowledgeSources + "&kResults=" + kResults + "&documentationLocation=" + documentationLocation,
			function(error, results) {
				callback(results, error);
			});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.setMaxNumberOfRecommendations = function(projectKey, maxNumberOfRecommendations) {
		generalApi.postJSON(this.restPrefix + "/configuration/" + projectKey + "/max-recommendations", maxNumberOfRecommendations,
			function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "Maximum number of results are updated to: " + maxNumberOfRecommendations);
				}
			});
	};

	/*
	 * external references: settings/decisionguidance/decisionGuidance.vm
	 */
	ConDecDecisionGuidanceAPI.prototype.setSimilarityThreshold = function(projectKey, threshold) {
		generalApi.postJSON(this.restPrefix + "/configuration/" + projectKey + "/similarity-threshold", threshold,
			function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The similarity threshold is updated to: " + threshold);
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
		generalApi.postJSON(this.restPrefix + "/configuration/" + projectKey + "/add-recommendations-directly", addRecommendationDirectly,
			function(error, results) {
				if (error === null) {
					conDecAPI.showFlag("success", "The recommendation settings were successfully changed.");
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
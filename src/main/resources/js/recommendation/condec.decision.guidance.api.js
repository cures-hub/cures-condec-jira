/**
 * Implements the communication with the ConDec Java REST API for solution option recommendation
 * (decision guidance).
 *
 * Is required by: conDecDecisionGuidance, conDecPrompt
 *
 * Is referenced in HTML by
 * settings/decisionguidance/*
 */
/* global AJS, conDecAPI, generalApi, conDecDecisionGuidanceAPI */
(function(global) {
	var ConDecDecisionGuidanceAPI = function() {
		this.restPrefix = `${AJS.contextPath()}/rest/condec/latest/decision-guidance`;
		this.recommendationsPerProblem = new Map(); // for caching recommendations
	};

	/**
	 * external references: condec.decision.guidance, condec.prompts
	 */
	ConDecDecisionGuidanceAPI.prototype.getRecommendations = function(decisionProblem, keywords) {
		const filterSettings = {
			"projectKey": conDecAPI.getProjectKey(),
			"selectedElementObject": decisionProblem,
			"searchTerm": keywords,
			"isCacheCleared": document.getElementById("clear-decision-guidance-cache-input").checked,
			"decisionGuidanceConfig": {
				"similarityThreshold": 0.5,
				"maxNumberOfRecommendations": 3,
			},
		};
		if (!filterSettings.isCacheCleared && this.recommendationsPerProblem.has(decisionProblem.id)) {
			return this.recommendationsPerProblem.get(decisionProblem.id);
		}
		return generalApi.postJSONReturnPromise(`${this.restPrefix}/recommendations`,
			filterSettings, conDecAPI.projectKey)
			.then((recommendations) => {
				recommendations = recommendations.sort((a, b) => b.score.value - a.score.value);
				conDecDecisionGuidanceAPI.recommendationsPerProblem.set(
					filterSettings.selectedElementObject.id, recommendations);
				return recommendations;
			});
	};

	/**
	 * Get all discarded recommendations for a decision problem from the database and delete all
	 * cached recommendations to allow editing their status
	 *
	 * @param decisionProblem
	 * @returns {Promise<unknown>|unknown}
	 */
	ConDecDecisionGuidanceAPI.prototype.getDiscardedRecommendations = function(decisionProblem) {
		const filterSettings = {
			"projectKey": conDecAPI.getProjectKey(),
			"selectedElementObject": decisionProblem,
		};
		conDecDecisionGuidanceAPI.recommendationsPerProblem.delete(filterSettings.selectedElementObject.id);
		return generalApi.postJSONReturnPromise(`${this.restPrefix}/discarded-recommendations`,
			filterSettings, conDecAPI.projectKey)
			.then((recommendations) => {
				recommendations = recommendations.sort((a, b) => b.score.value - a.score.value);
				return recommendations;
			});
	};

	/**
	 * external references: condec.decision.guidance
	 */
	ConDecDecisionGuidanceAPI.prototype.getRecommendationEvaluation =
		function(projectKey, keyword, issueId, knowledgeSources, kResults, documentationLocation,
			callback) {
			generalApi.getJSON(`${this.restPrefix}/evaluation/${projectKey}?keyword=${keyword}` +
				`&issueId=${issueId}&knowledgeSource=${knowledgeSources}&kResults=${kResults}` +
				`&documentationLocation=${documentationLocation}`,
				(error, results) => {
					callback(results, error);
				});
		};

	/**
	 * external references: condec.decision.guidance
	 */
	ConDecDecisionGuidanceAPI.prototype.getDecisionGuidanceConfig = function() {
		return generalApi.getJSONReturnPromise(`${this.restPrefix}/configuration/${conDecAPI.projectKey}`);
	};

	/**
	 * external references: settings/decisionguidance/
	 */
	ConDecDecisionGuidanceAPI.prototype.setMaxNumberOfRecommendations =
		function(projectKey, maxRecommendations) {
			generalApi.postJSONReturnPromise(`${this.restPrefix}/configuration/${projectKey}` +
				"/max-recommendations", maxRecommendations)
				.then(() => conDecAPI.showFlag("success", "Maximum number of " +
					"recommendations was successfully updated!"));
		};

	/**
	 * external references: settings/decisionguidance/
	 */
	ConDecDecisionGuidanceAPI.prototype.setSimilarityThreshold = function(projectKey, threshold) {
		generalApi.postJSON(`${this.restPrefix}/configuration/${projectKey}` +
			`/similarity-threshold${threshold}`,
			(error, response) => {
				if (error === null) {
					conDecAPI.showFlag("success",
						`The similarity threshold is updated to: ${threshold}`);
				}
			});
	};

	/**
	 * external references: settings/decisionguidance/
	 */
	ConDecDecisionGuidanceAPI.prototype.createRDFKnowledgeSource = function(projectKey, rdfSource) {
		generalApi.postJSON(`${this.restPrefix}/configuration/${projectKey}` +
			"/create/rdf-source", rdfSource,
			(error, response) => {
				if (error === null) {
					conDecAPI.showFlag("success",
						"The knowledge source is successfully created. " +
						"<b>Please refresh the page.</b> ");
				}
			});
	};

	/**
	 * external references: settings/decisionguidance/
	 */
	ConDecDecisionGuidanceAPI.prototype.setAddRecommendationDirectly =
		function(projectKey, addRecommendationDirectly) {
			generalApi.postJSON(`${this.restPrefix}/configuration/${projectKey}` +
				"/add-recommendations-directly", addRecommendationDirectly,
				(error, results) => {
					if (error === null) {
						conDecAPI.showFlag("success",
							"The recommendation settings were successfully changed.");
					}
				});
		};

	/**
	 * external references: settings/decisionguidance/
	 */
	ConDecDecisionGuidanceAPI.prototype.setRDFKnowledgeSourceActivated =
		function(projectKey, knowledgeSourceName, isActivated) {
			generalApi.postJSON(`${this.restPrefix}/configuration/${projectKey}` +
				`/activate/rdf-source/${knowledgeSourceName}`, isActivated,
				(error, response) => {
					if (error === null) {
						const message = `The knowledge source ${knowledgeSourceName} is `;
						if (isActivated) {
							conDecAPI.showFlag("success", `${message}activated.`);
						} else {
							conDecAPI.showFlag("success", `${message}deactivated.`);
						}
					}
				});
		};

	/**
	 * external references: settings/decisionguidance/
	 */
	ConDecDecisionGuidanceAPI.prototype.setProjectSource =
		function(projectKey, projectSourceKey, isActivated) {
			generalApi.postJSON(`${this.restPrefix}/configuration/${projectKey}` +
				`/activate/project-source/${projectSourceKey}`, isActivated,
				(error, response) => {
					if (error === null) {
						const message = `The project <b>${projectSourceKey}</b> is now `;
						if (isActivated) {
							conDecAPI.showFlag("success", `${message}activated.`);
						} else {
							conDecAPI.showFlag("success", `${message}deactivated.`);
						}
					}
				});
		};

	/**
	 * external references: settings/decisionguidance/
	 */
	ConDecDecisionGuidanceAPI.prototype.deleteRDFKnowledgeSource =
		function(projectKey, knowledgeSourceName, callback) {
			generalApi.deleteJSON(`${this.restPrefix}/configuration/${projectKey}` +
				`/rdf-source/${knowledgeSourceName}`, null,
				(error, response) => {
					if (error === null) {
						conDecAPI.showFlag("success", "The knowledge source " +
							`${knowledgeSourceName} was successfully deleted.`);
						callback();
					}
				});
		};

	/**
	 * external references: settings/decisionguidance/
	 */
	ConDecDecisionGuidanceAPI.prototype.updateRDFKnowledgeSource =
		function(projectKey, knowledgeSourceName, knowledgeSource) {
			generalApi.postJSON(`${this.restPrefix}/configuration/${projectKey}` +
				`/update/rdf-source/${knowledgeSourceName}`, knowledgeSource,
				(error, response) => {
					if (error === null) {
						conDecAPI.showFlag("success",
							"The knowledge source was successfully updated.");
					}
				});
		};

	/**
	 * Discard a decision guidance recommendation, i.e. send a request to store it as discarded.
	 *
	 * @param recommendation The recommendation to be stored as discarded
	 * @returns {Promise | Promise<unknown>} Promise corresponding to discard request
	 */
	ConDecDecisionGuidanceAPI.prototype.discardRecommendation = function(recommendation) {
		recommendation.isDiscarded = true;
		recommendation.arguments = [];  // Ignore arguments when discarding
		return generalApi.postJSONReturnPromise(`${this.restPrefix}` +
			`/discard/${conDecAPI.projectKey}`, recommendation);
	};

	/**
	 * Undo discard of a decision guidance recommendation, i.e. send a request to remove a
	 * recommendation from the stored discarded recommendations.
	 *
	 * @param recommendation The recommendation to be no longer stored as discarded
	 * @returns {Promise | Promise<unknown>} Promise corresponding to the undo-discard request
	 */
	ConDecDecisionGuidanceAPI.prototype.undoDiscardRecommendation = function(recommendation) {
		recommendation.isDiscarded = false;
		recommendation.arguments = [];  // Arguments of discarded recommendations are ignored as
		//                                 they are not stored
		return generalApi.postJSONReturnPromise(`${this.restPrefix}` +
			`/undo-discard/${conDecAPI.projectKey}`, recommendation);
	};

	global.conDecDecisionGuidanceAPI = new ConDecDecisionGuidanceAPI();
})(window);

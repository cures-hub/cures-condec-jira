/**
 * Implements the communication with the ConDec Java REST API for link recommendation and duplicate recognition.
 *
 * Is required by: conDecLinkRecommendation, conDecPrompt
 *
 * Is referenced in HTML by
 * settings/linkrecommendation/*
 */
(function(global) {

	const ConDecLinkRecommendationAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/link-recommendation";
		this.currentLinkRecommendations = new Map(); // for caching recommendations
	};

	/**
	 * external usage: condec.link.recommendation.js, condec.prompts.js
	 */
	ConDecLinkRecommendationAPI.prototype.getLinkRecommendations = function(filterSettings) {
		var elementId = filterSettings.selectedElementObject.id;
		if (!filterSettings.isCacheCleared && this.currentLinkRecommendations.has(elementId)) {
			return this.currentLinkRecommendations.get(elementId);
		}
		return generalApi.postJSONReturnPromise(this.restPrefix + "/recommendations", filterSettings)
			.then(recommendations => {
				recommendations = recommendations.sort((a, b) => b.score.value - a.score.value);
				conDecLinkRecommendationAPI.currentLinkRecommendations.set(elementId, recommendations);
				return recommendations;
			});
	};

	/**
	 * external usage: condec.link.recommendation.js
	 */
	ConDecLinkRecommendationAPI.prototype.discardRecommendation = function(projectKey, recommendation) {
		recommendation.isDiscarded = true;
		recommendation.projectKey = projectKey;
		return generalApi.postJSONReturnPromise(this.restPrefix + "/discard", recommendation);
	};

	/**
	 * external usage: condec.link.recommendation.js
	 */
	ConDecLinkRecommendationAPI.prototype.undoDiscardRecommendation = function(projectKey, recommendation) {
		recommendation.isDiscarded = false;
		recommendation.projectKey = projectKey;
		return generalApi.postJSONReturnPromise(this.restPrefix + "/undo-discard", recommendation);
	};

	/**
	 * external usage: condec.link.recommendation.js
	 */
	ConDecLinkRecommendationAPI.prototype.getLinkRecommendationConfig = function() {
		return generalApi.getJSONReturnPromise(this.restPrefix + "/configuration/" + conDecAPI.projectKey);
	};

	/**
	 * external references: settings/linkrecommendation/
	 */
	ConDecLinkRecommendationAPI.prototype.setThreshold = function(projectKey, threshold) {
		generalApi.postJSONReturnPromise(this.restPrefix + "/configuration/" + projectKey + "/threshold", threshold)
			.then(() => conDecAPI.showFlag("success", "Minimum probability was successfully updated!"));
	};

	/**
	 * external references: settings/linkrecommendation/
	 */
	ConDecLinkRecommendationAPI.prototype.setMaxRecommendations = function(projectKey, maxRecommendations) {
		generalApi.postJSONReturnPromise(this.restPrefix + "/configuration/" + projectKey + "/max-recommendations", maxRecommendations)
			.then(() => conDecAPI.showFlag("success", "Maximum number of recommendations was successfully updated!"));
	};

	/**
	 * external references: settings/linkrecommendation/
	 */
	ConDecLinkRecommendationAPI.prototype.saveRules = function(projectKey, linkRecommendationRules) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/configuration/" + projectKey + "/rules", linkRecommendationRules)
			.then(() => conDecAPI.showFlag("success", "Link recommendation rules were successfully updated!"));
	};

	global.conDecLinkRecommendationAPI = new ConDecLinkRecommendationAPI();
})(window);
(function(global) {

	const ConDecLinkRecommendationAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/linkrecommendation";
		this.currentLinkRecommendations = new Map();
		this.currentLinkDuplicates = new Map();
	};

	ConDecLinkRecommendationAPI.prototype.setMinimumLinkSuggestionProbability = function(projectKey, minLinkSuggestionProbability) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/setMinimumLinkSuggestionProbability.json?
			projectKey=${projectKey}&minLinkSuggestionProbability=${minLinkSuggestionProbability}`, null)
			.then(() => conDecAPI.showFlag("success", "Minimum probability was successfully updated!"));
	}

	ConDecLinkRecommendationAPI.prototype.getRelatedKnowledgeElements = function(projectKey, elementId, elementLocation) {
		if (this.currentLinkRecommendations.has(elementId)) {
			return this.currentLinkRecommendations.get(elementId);
		}
		return generalApi.getJSONReturnPromise(
			`${this.restPrefix}/getRelatedKnowledgeElements.json
				?projectKey=${projectKey}
				&elementId=${elementId}
				&elementLocation=${elementLocation}`)
			.then(recommendations => {
				recommendations = recommendations.sort((a, b) => b.score.value - a.score.value);
				conDecLinkRecommendationAPI.currentLinkRecommendations.set(elementId, recommendations);
				return recommendations;
			});
	};

	ConDecLinkRecommendationAPI.prototype.discardRecommendation = function(projectKey, recommendation) {
		recommendation.isDiscarded = true;
		return generalApi.postJSONReturnPromise(this.restPrefix + `/discardRecommendation.json
				?projectKey=${projectKey}`, recommendation);
	};

	ConDecLinkRecommendationAPI.prototype.undoDiscardRecommendation = function(projectKey, recommendation) {
		recommendation.isDiscarded = false;
		return generalApi.postJSONReturnPromise(this.restPrefix + `/undoDiscardRecommendation.json
				?projectKey=${projectKey}`, recommendation);
	};

	global.conDecLinkRecommendationAPI = new ConDecLinkRecommendationAPI();
})(window);
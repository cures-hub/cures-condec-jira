(function(global) {

	const ConDecLinkRecommendationAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/linkrecommendation";
		this.projectKey = conDecAPI.getProjectKey();
	};

	ConDecLinkRecommendationAPI.prototype.setMinimumDuplicateLength = function(projectKey, fragmentLength) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/setMinimumDuplicateLength.json?
			projectKey=${projectKey}&fragmentLength=${fragmentLength}`, null)
			.then(conDecAPI.showFlag("success", "Minimum length was successfully updated!"));
	}

	ConDecLinkRecommendationAPI.prototype.setMinimumLinkSuggestionProbability = function(projectKey, minLinkSuggestionProbability) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/setMinimumLinkSuggestionProbability.json?
			projectKey=${projectKey}&minLinkSuggestionProbability=${minLinkSuggestionProbability}`, null)
			.then(conDecAPI.showFlag("success", "Minimum probability was successfully updated!"));
	}

	ConDecLinkRecommendationAPI.prototype.getRelatedKnowledgeElements = function(projectKey, elementId, elementLocation) {
		return generalApi.getJSONReturnPromise(
			`${this.restPrefix}/getRelatedKnowledgeElements.json
				?projectKey=${projectKey}
				&elementId=${elementId}
				&elementLocation=${elementLocation}`);
	};

	ConDecLinkRecommendationAPI.prototype.discardRecommendation = function(projectKey, recommendation) {
		recommendation["@type"] = recommendation.recommendationType;
		return generalApi.postJSONReturnPromise(this.restPrefix + `/discardRecommendation.json
				?projectKey=${projectKey}`, recommendation);
	};

	ConDecLinkRecommendationAPI.prototype.getDuplicateKnowledgeElement = function(projectKey, elementId, location) {
		return generalApi.getJSONReturnPromise(
			`${this.restPrefix}/getDuplicateKnowledgeElement.json
				?projectKey=${projectKey}
				&elementId=${elementId}
				&location=${location}`
		);
	};

	global.conDecLinkRecommendationAPI = new ConDecLinkRecommendationAPI();
}
)(window);
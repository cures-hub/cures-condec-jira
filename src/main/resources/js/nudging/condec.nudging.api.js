(function(global) {

	const ConDecNudgingAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/nudging";
	};

	ConDecNudgingAPI.prototype.activatePromptEventForDefinitionOfDoneChecking = function(projectKey, eventKey, isActivated) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/activatePromptEventForDefinitionOfDoneChecking.json?
			projectKey=${projectKey}&eventKey=${eventKey}&isActivated=${isActivated}`, null)
			.then(conDecAPI.showFlag("success",
				"Activation of prompting event for DoD checking was successfully changed to " + isActivated));
	}

	ConDecNudgingAPI.prototype.activatePromptEventForLinkSuggestion = function (projectKey, eventKey, isActivated) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/activatePromptEventForLinkSuggestion.json?
			projectKey=${projectKey}&eventKey=${eventKey}&isActivated=${isActivated}`, null)
			.then(conDecAPI.showFlag("success",
				"Activation of prompting event for link suggestion was successfully changed to " + isActivated));
	}
	ConDecNudgingAPI.prototype.activatePromptEventForNonValidatedElementsChecking = function (projectKey, eventKey, isActivated) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/activatePromptEventForNonValidatedElementsChecking.json?
			projectKey=${projectKey}&eventKey=${eventKey}&isActivated=${isActivated}`, null)
			.then(conDecAPI.showFlag("success",
				"Activation of prompting event for non validated elements was successfully changed to " + isActivated));
	}
	global.conDecNudgingAPI = new ConDecNudgingAPI();
})(window);
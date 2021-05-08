(function(global) {

	const NudgingAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/nudging";
	};

	NudgingAPI.prototype.activatePromptEventForDefinitionOfDoneChecking = function(projectKey, eventKey, isActivated) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/activatePromptEventForDefinitionOfDoneChecking.json?
			projectKey=${projectKey}&eventKey=${eventKey}&isActivated=${isActivated}`, null)
			.then(conDecAPI.showFlag("success", "Activation of prompting event was successfully changed!"));
	}

	NudgingAPI.prototype.activatePromptEventForLinkSuggestion = function(projectKey, eventKey, isActivated) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/activatePromptEventForLinkSuggestion.json?
			projectKey=${projectKey}&eventKey=${eventKey}&isActivated=${isActivated}`, null)
			.then(conDecAPI.showFlag("success", "Activation of prompting event was successfully changed!"));
	}

	global.nudgingAPI = new NudgingAPI();
})(window);
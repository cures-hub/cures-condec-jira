(function(global) {

	const NudgingAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/nudging";
		jQuery(document).ajaxComplete(function (event, request, settings) {
			if (settings.url.includes("WorkflowUIDispatcher.jspa")) {
				// just-in-time prompts when status changes
				consistencyAPI.displayConsistencyCheck();
				consistencyAPI.displayCompletenessCheck();				
			}
		});
	};

	NudgingAPI.prototype.activatePromptEventForDefinitionOfDoneChecking = function(projectKey, eventKey, isActivated) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/activatePromptEventForDefinitionOfDoneChecking.json?
			projectKey=${projectKey}&eventKey=${eventKey}&isActivated=${isActivated}`, null)
			.then(conDecAPI.showFlag("success",
				"Activation of prompting event for DoD checking was successfully changed!"));
	}

	NudgingAPI.prototype.activatePromptEventForLinkSuggestion = function(projectKey, eventKey, isActivated) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/activatePromptEventForLinkSuggestion.json?
			projectKey=${projectKey}&eventKey=${eventKey}&isActivated=${isActivated}`, null)
			.then(conDecAPI.showFlag("success",
				"Activation of prompting event for link suggestion was successfully changed!"));
	}

	global.nudgingAPI = new NudgingAPI();
})(window);
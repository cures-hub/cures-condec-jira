(function(global) {

	const ConfigAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/config";
	};

	ConfigAPI.prototype.setActivationStatusOfQualityEvent = function(projectKey, eventKey, isActivated) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/activatePromptEventForDefinitionOfDoneChecking.json?
			projectKey=${projectKey}&eventKey=${eventKey}&isActivated=${isActivated}`, null)
			.then(conDecAPI.showFlag("success", "Activation of prompting event was successfully changed!"));
	}

	global.configAPI = new ConfigAPI();
})(window);
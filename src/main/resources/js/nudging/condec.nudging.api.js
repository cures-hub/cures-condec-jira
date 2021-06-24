(function(global) {

	const ConDecNudgingAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/nudging";
	};

	ConDecNudgingAPI.prototype.isPromptEventActivated = function(feature, jiraIssueId, actionId) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/isPromptEventActivated.json?
			feature=${feature}&jiraIssueId=${jiraIssueId}&actionId=${actionId}`, null)
			.then((isActivated) => {conDecAPI.showFlag("success",
				"Event is activated " + isActivated)});
	}

	ConDecNudgingAPI.prototype.activatePromptEvent = function(projectKey, feature, eventKey, isActivated) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/activatePromptEvent.json?
			projectKey=${projectKey}&feature=${feature}&eventKey=${eventKey}&isActivated=${isActivated}`, null)
			.then(conDecAPI.showFlag("success",
				"Activation of prompting event was successfully changed to " + isActivated));
	}

	global.conDecNudgingAPI = new ConDecNudgingAPI();
})(window);
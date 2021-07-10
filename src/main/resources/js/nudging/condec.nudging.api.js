(function(global) {

	const ConDecNudgingAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/nudging";
	};

	ConDecNudgingAPI.prototype.isPromptEventActivated = function(feature, jiraIssueId, actionId) {
		return generalApi.postJSONReturnPromise(this.restPrefix + `/isPromptEventActivated.json?
			feature=${feature}&jiraIssueId=${jiraIssueId}&actionId=${actionId}`, null);
	}

	ConDecNudgingAPI.prototype.activatePromptEvent = function(projectKey, feature, eventKey, isActivated) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/activatePromptEvent.json?
			projectKey=${projectKey}&feature=${feature}&eventKey=${eventKey}&isActivated=${isActivated}`, null)
			.then(() => conDecAPI.showFlag("success",
				"Activation of prompting event was successfully changed to " + isActivated));
	}

	ConDecNudgingAPI.prototype.decideAmbientFeedbackForTab = function(numberOfSmartFeatureTodos, nameOfTab) {
		var tab = document.getElementById(nameOfTab);
		if (numberOfSmartFeatureTodos <= 0) {
			this.setAmbientFeedback(tab, "condec-fine");
		} else if (numberOfSmartFeatureTodos < 25) {
			this.setAmbientFeedback(tab, "condec-warning");
		} else {
			this.setAmbientFeedback(tab, "condec-error");
		}
	};

	ConDecNudgingAPI.prototype.setAmbientFeedback = function(element, tag) {
		if (element === null) {
			return;
		}
		element.classList.remove("condec-default");
		element.classList.remove("condec-error");
		element.classList.remove("condec-warning");
		element.classList.remove("condec-fine");
		element.classList.add(tag);
	};

	global.conDecNudgingAPI = new ConDecNudgingAPI();
})(window);
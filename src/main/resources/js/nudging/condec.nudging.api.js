(function(global) {

	const ConDecNudgingAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/nudging";
	};

	ConDecNudgingAPI.prototype.isPromptEventActivated = function(feature, jiraIssueId, actionId) {
		return generalApi.postJSONReturnPromise(this.restPrefix + `/isPromptEventActivated.json?
			feature=${feature}&jiraIssueId=${jiraIssueId}&actionId=${actionId}`, null);
	};

	ConDecNudgingAPI.prototype.activatePromptEvent = function(projectKey, feature, eventKey, isActivated) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/activatePromptEvent.json?
			projectKey=${projectKey}&feature=${feature}&eventKey=${eventKey}&isActivated=${isActivated}`, null)
			.then(() => conDecAPI.showFlag("success",
				"Activation of prompting event was successfully changed to " + isActivated));
	};

	/**
	 * Sets a CSS class on a HTML element (e.g. menu item) according to the number of developer actions required.
	 * @param numberOfSmartFeatureTodos number of developer actions required.
	 * @param elementId of a menu item or other HTML element.
	 */
	ConDecNudgingAPI.prototype.decideAmbientFeedbackForTab = function(numberOfSmartFeatureTodos, elementId) {
		var element = document.getElementById(elementId);
		if (numberOfSmartFeatureTodos <= 0) {
			this.setAmbientFeedback(element, "condec-fine");
		} else if (numberOfSmartFeatureTodos < 25) {
			this.setAmbientFeedback(element, "condec-warning");
		} else {
			this.setAmbientFeedback(element, "condec-error");
		}
	};

	/**
	 * Sets the CSS class on a HTML element (e.g. menu item).
	 * @param element menu item or other HTML element.
	 * @param cssClass to be set on the HTML element.
	 */
	ConDecNudgingAPI.prototype.setAmbientFeedback = function(element, cssClass) {
		if (element === null) {
			return;
		}
		element.classList.remove("condec-default");
		element.classList.remove("condec-error");
		element.classList.remove("condec-warning");
		element.classList.remove("condec-fine");
		element.classList.add(cssClass);
	};

	global.conDecNudgingAPI = new ConDecNudgingAPI();
})(window);
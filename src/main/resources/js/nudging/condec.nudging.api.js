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
	 * 
	 * @issue When to add ambient feedback on menu items?
	 * @alternative We add ambient feedback on menu items when the page is loaded.
	 * @pro The developers are directly confronted with the feedback and are better nudged to use ConDec's 
	 * 		smart features and improve the documentation quality.
	 * @con Costs a lot of computation effort when loading the page.
	 * @alternative The ambient feedback is set when the just-in-time prompts are opened and when the developer 
	 * 				navigates to a smart feature view!
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
	
	ConDecNudgingAPI.prototype.decideCheckIcon = function(numberOfSmartFeatureTodos, elementId) {
		var element = document.getElementById(elementId);
		if (numberOfSmartFeatureTodos <= 0) {
			element.style.visibility = "visible";
			AJS.$(element).tooltip({gravity: 'w'});
		} else {
			element.style.visibility = "hidden";
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
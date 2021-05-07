/**
 * This object provides methods for the settings page for the completeness.
 * This object is used in the following files:
 * rationaleBacklogSettings.vm:
 * 		completenessEventSettings.vm
 */
(function (global) {

	let ConDecJiraIssueCompletenessModule = function () {
		this.isInitialized = false;
		this.projectKey = conDecAPI.getProjectKey();
	};

	ConDecJiraIssueCompletenessModule.prototype.isInitialized = function() {
		return this.isInitialized;
	}

	ConDecJiraIssueCompletenessModule.prototype.init = function init() {
		//Check event toggles
		this.isDoneActivatedToggle = document.getElementById("isCompletenessDoneActivated-toggle");
		this.isClosedActivatedToggle = document.getElementById("isCompletenessClosedActivated-toggle");
		conDecJiraIssueQualityModule.initToggles(this.isClosedActivatedToggle, this.projectKey, "completeness-closed");
		conDecJiraIssueQualityModule.initToggles(this.isDoneActivatedToggle, this.projectKey, "completeness-done");
		// add listeners
		this.isDoneActivatedToggle.addEventListener("change", conDecJiraIssueQualityModule .callSetActivationStatusOfQualityEvent(this.isDoneActivatedToggle, "completeness-done"));
		this.isClosedActivatedToggle.addEventListener("change", conDecJiraIssueQualityModule .callSetActivationStatusOfQualityEvent(this.isClosedActivatedToggle, "completeness-closed"));
		this.isInitialized = true;
	}

	global.conDecJiraIssueCompletenessModule = new ConDecJiraIssueCompletenessModule();
})(window);
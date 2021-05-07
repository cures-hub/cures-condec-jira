/**
 * This object provides methods for the settings page for the consistency.
 * This object is used in the following files:
 * consistencySettings.vm:
 * 		consistencyLinkSettings.vm,
 * 		consistencyDuplicationSettings.vm,
 * 		consistencyEventSettings.vm
 */
(function (global) {

	let ConDecJiraIssueConsistencyModule = function() {
		this.isInitialized = false;
		this.projectKey = conDecAPI.getProjectKey();
	};

	ConDecJiraIssueConsistencyModule.prototype.isInitialized = function() {
		return this.isInitialized;
	}

	/**
	 * Initializes the buttons and toggles by loading the currently set values and registering listeners.
	 */
	ConDecJiraIssueConsistencyModule.prototype.init = function() {
		//Check event toggles
		this.isDoneActivatedToggle = document.getElementById("isConsistencyDoneActivated-toggle");
		this.isClosedActivatedToggle = document.getElementById("isConsistencyClosedActivated-toggle");
		conDecJiraIssueQualityModule.initToggles(this.isClosedActivatedToggle, this.projectKey, "consistency-closed");
		conDecJiraIssueQualityModule.initToggles(this.isDoneActivatedToggle, this.projectKey, "consistency-done");
		// add listeners
		this.isDoneActivatedToggle.addEventListener("change", conDecJiraIssueQualityModule.callSetActivationStatusOfQualityEvent(this.isDoneActivatedToggle, "consistency-done"));
		this.isClosedActivatedToggle.addEventListener("change", conDecJiraIssueQualityModule.callSetActivationStatusOfQualityEvent(this.isClosedActivatedToggle, "consistency-closed"));
		this.isInitialized = true;
	}

	global.conDecJiraIssueConsistencyModule = new ConDecJiraIssueConsistencyModule();
})(window);
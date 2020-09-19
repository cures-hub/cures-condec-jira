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

		//Link suggestion score settings
		this.saveMinProbabilityLinkBtn = document.getElementById("save-min-probability-link-btn");
		this.minProbabilityInput = document.getElementById("min-probability-link");
		this.saveMinProbabilityLinkBtn.addEventListener('click', event => this.saveMinProbabilityLink(event));

		//Duplication detection settings
		this.saveMinLengthDuplicateBtn = document.getElementById("save-min-length-duplicate-btn");
		this.minLengthDuplicateInput = document.getElementById("min-length-duplicate");
		this.saveMinLengthDuplicateBtn.addEventListener('click', event => this.saveMinLengthDuplicate(event));

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


	ConDecJiraIssueConsistencyModule.prototype.saveMinLengthDuplicate = function (event) {
		event.preventDefault();
		event.stopPropagation();
		window.onbeforeunload = null;
		if (!this.saveMinLengthDuplicateBtn.isBusy()) {
			this.saveMinLengthDuplicateBtn.setAttribute('aria-disabled', 'true');
			this.saveMinLengthDuplicateBtn.busy();

			configAPI.setMinimumDuplicateLength(conDecAPI.getProjectKey(), this.minLengthDuplicateInput.value)
				.then(() => showSuccessFlag())
				.catch(() => conDecAPI.showFlag("error", "An error occurred while updating the configuration!"))
				.finally(() => {
					this.saveMinLengthDuplicateBtn.setAttribute('aria-disabled', 'false');
					this.saveMinLengthDuplicateBtn.idle();
				})

		}
	}


	ConDecJiraIssueConsistencyModule.prototype.saveMinProbabilityLink = function(event) {
		event.preventDefault();
		event.stopPropagation();
		window.onbeforeunload = null;
		if (!this.saveMinProbabilityLinkBtn.isBusy()) {
			this.saveMinProbabilityLinkBtn.setAttribute('aria-disabled', 'true');
			this.saveMinProbabilityLinkBtn.busy();
			configAPI.setMinimumLinkSuggestionProbability(conDecAPI.getProjectKey(), this.minProbabilityInput.value)
				.then(() => showSuccessFlag())
				.catch(() => conDecAPI.showFlag("error", "An error occurred while updating the configuration!"))
				.finally(() => {
					this.saveMinProbabilityLinkBtn.setAttribute('aria-disabled', 'false');
					this.saveMinProbabilityLinkBtn.idle();
				})
		}

	}


	global.conDecJiraIssueConsistencyModule = new ConDecJiraIssueConsistencyModule();
})(window);
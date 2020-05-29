/**
 * This object provides methods for the settings page for the consistency.
 * This object is used in the following files:
 * consistencySettings.vm:
 * 		consistencyLinkSettings.vm,
 * 		consistencyDuplicationSettings.vm,
 * 		consistencyEventSettings.vm
 */
(function (global) {

	let ConDecJiraIssueConsistencyModule = function ConDecJiraIssueConsistencyModule() {
		this.isInitialized = false;
		this.projectKey = conDecAPI.getProjectKey();
	};

	ConDecJiraIssueConsistencyModule.prototype.isInitialized = function isInitialized() {
		return this.isInitialized;
	}

	/**
	 * Initializes the buttons and toggles by loading the currently set values and registering listeners.
	 */
	ConDecJiraIssueConsistencyModule.prototype.init = function init() {

		//Link suggestion score settings
		this.saveMinProbabilityLinkBtn = document.getElementById("save-min-probability-link-btn");
		this.minProbabilityInput = document.getElementById("min-probability-link");
		this.saveMinProbabilityLinkBtn.addEventListener('click', event => this.saveMinProbabilityLink(event));

		//Duplication detection settings
		this.saveMinLengthDuplicateBtn = document.getElementById("save-min-length-duplicate-btn");
		this.minLengthDuplicateInput = document.getElementById("min-length-duplicate");
		this.saveMinLengthDuplicateBtn.addEventListener('click', event => this.saveMinLengthDuplicate(event));

		//Check event toggles
		this.isDoneActivatedToggle = document.getElementById("isDoneActivated-toggle");
		this.isClosedActivatedToggle = document.getElementById("isClosedActivated-toggle");
		initToggles(this.isClosedActivatedToggle, this.projectKey, "closed");
		initToggles(this.isDoneActivatedToggle, this.projectKey, "done");
		// add listeners
		this.isDoneActivatedToggle.addEventListener("change", this.callSetActivationStatusOfConsistencyEvent(this.isDoneActivatedToggle, "done"));
		this.isClosedActivatedToggle.addEventListener("change", this.callSetActivationStatusOfConsistencyEvent(this.isClosedActivatedToggle, "closed"));
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


	ConDecJiraIssueConsistencyModule.prototype.saveMinProbabilityLink = function saveMinProbabilityLink(event) {
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

	ConDecJiraIssueConsistencyModule.prototype.callSetActivationStatusOfConsistencyEvent = function callSetActivationStatusOfConsistencyEvent(toggle, eventKey) {
		return () => {
			toggle.busy = true;
			configAPI.setActivationStatusOfConsistencyEvent(this.projectKey, eventKey, toggle.checked)
				.then(() => showSuccessFlag())
				.catch((error) => {
					conDecAPI.showFlag("error", "Could not activate consistency event. " + error);
					toggle.checked = !toggle.checked;
				})
				.finally(() => toggle.busy = false);
		}
	}

	/**
	 * FUNCTIONS!
	 */

	function showSuccessFlag() {
		conDecAPI.showFlag("success", "Configuration successfully updated!");
	}

	//show current toggle status
	function initToggles(toggleElement, projectKey, eventKey) {
		toggleElement.busy = true;
		toggle.disabled = true;

		configAPI.getActivationStatusOfConsistencyEvent(projectKey, eventKey)
			.then(response => {
				toggleElement.checked = response.isActivated;
				showSuccessFlag();
			})
			.catch((error) =>
				conDecAPI.showFlag("error", "Could not load activation status of consistency event. " + error)
			)
			.finally(() => {
					toggleElement.busy = false;
					toggle.disabled = false;
				}
			);
	}

	global.conDecJiraIssueConsistencyModule = new ConDecJiraIssueConsistencyModule();
})(window);
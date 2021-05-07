/**
 * This object provides methods for the settings page for the consistency.
 * This object is used in the following files:
 * consistencySettings.vm:
 * 		consistencyLinkSettings.vm,
 * 		consistencyDuplicationSettings.vm,
 * 		consistencyEventSettings.vm
 */
(function (global) {

	let ConDecJiraIssueQualityModule = function () {
		this.projectKey = conDecAPI.getProjectKey();
	};

	ConDecJiraIssueQualityModule.prototype.callSetActivationStatusOfQualityEvent = function(toggle, eventKey) {
		return () => {
			toggle.busy = true;
			configAPI.setActivationStatusOfQualityEvent(this.projectKey, eventKey, toggle.checked)
				.then(() => this.showSuccessFlag())
				.catch((error) => {
					conDecAPI.showFlag("error", "Could not activate quality event. " + error);
					toggle.checked = !toggle.checked;
				})
				.finally(() => toggle.busy = false);
		}
	}

	ConDecJiraIssueQualityModule.prototype.showSuccessFlag = function() {
		conDecAPI.showFlag("success", "Configuration successfully updated!");
	}

	global.conDecJiraIssueQualityModule = new ConDecJiraIssueQualityModule();
})(window);
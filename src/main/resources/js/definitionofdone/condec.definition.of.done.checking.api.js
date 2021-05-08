(function(global) {

	const ConDecDoDCheckingAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/dodchecking";
	};

	/*
	 * external references: settings/definitionofdone/...
	 */
	ConDecDoDCheckingAPI.prototype.setDefinitionOfDone = function(projectKey, definitionOfDone) {
		generalApi.postJSON(this.restPrefix + "/setDefinitionOfDone.json?projectKey=" + projectKey, definitionOfDone, function(
			error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "The definition of done is updated.");
			}
		});
	};

	/*
	 * external references: nudging/condec.prompts.js
	 */
	ConDecDoDCheckingAPI.prototype.doesElementNeedCompletenessApproval = function(filterSettings) {
		return generalApi.postJSONReturnPromise(
			`${this.restPrefix}/doesElementNeedCompletenessApproval.json`, filterSettings
		);
	};

	global.conDecDoDCheckingAPI = new ConDecDoDCheckingAPI();
})(window);
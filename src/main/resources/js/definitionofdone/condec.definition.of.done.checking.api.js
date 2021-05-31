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
	 * external references: nudging/condec.prompts.js,
	 * condec.quality.check.js
	 */
	ConDecDoDCheckingAPI.prototype.getFailedDefinitionOfDoneCriteria = function(filterSettings, callback) {
		generalApi.postJSON(this.restPrefix + '/getFailedDefinitionOfDoneCriteria.json', filterSettings, function(
			error, result) {
			if (error === null) {
				callback(result);
			}
		});
	};

	/*
	 * external references: condec.quality.check.js
	 */
	ConDecDoDCheckingAPI.prototype.getCoverageOfJiraIssue = function(projectKey, issueKey, callback) {
		generalApi.getJSON(this.restPrefix + `/getCoverageOfJiraIssue.json?projectKey=${projectKey}&issueKey=${issueKey}`,
			function(error, result) {
				if (error === null) {
					callback(result);
				}
			});
	};

	global.conDecDoDCheckingAPI = new ConDecDoDCheckingAPI();
})(window);
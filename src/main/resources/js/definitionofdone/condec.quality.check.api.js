/**
 * Implements the communication with the ConDec Java REST API for definition of done (DoD) checking and configuration.
 */
(function(global) {

	const ConDecDoDCheckingAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/quality-checking";
	};

	/**
	 * external references: settings/definitionofdone/...
	 */
	ConDecDoDCheckingAPI.prototype.setDefinitionOfDone = function(projectKey, definitionOfDone) {
		generalApi.postJSON(this.restPrefix + "/configuration/" + projectKey + "/definition-of-done", definitionOfDone,
			function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The definition of done is updated.");
				}
			});
	};

	/**
	 * external references: settings/definitionofdone/...
	 * nudging/condec.prompts.js, condec.quality.check.js
	 */
	ConDecDoDCheckingAPI.prototype.getDefinitionOfDone = function(projectKey, callback) {
		conDecAPI.getFilterSettings(projectKey, "", (filterSettings) => {
			callback(filterSettings["definitionOfDone"]);
		});
	};

	/**
	 * external references: nudging/condec.prompts.js,
	 * condec.quality.check.js
	 */
	ConDecDoDCheckingAPI.prototype.getQualityProblems = function(filterSettings, callback) {
		generalApi.postJSON(this.restPrefix + '/quality-check-results', filterSettings, function(
			error, result) {
			if (error === null) {
				callback(result);
			}
		});
	};

	/**
	 * external references: condec.quality.check.js,
	 * nudging/condec.prompts.js,
	 */
	ConDecDoDCheckingAPI.prototype.getCoverageOfJiraIssue = function(filterSettings, callback) {
		generalApi.postJSON(this.restPrefix + '/getCoverageOfJiraIssue.json', filterSettings, function(
			error, result) {
			if (error === null) {
				callback(result);
			}
		});
	};

	global.conDecDoDCheckingAPI = new ConDecDoDCheckingAPI();
})(window);
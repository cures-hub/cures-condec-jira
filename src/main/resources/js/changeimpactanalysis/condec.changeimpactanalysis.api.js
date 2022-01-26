(function(global) {

    var ConDecChangeImpactAnalysisAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/change-impact-analysis";
	};

    /*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecChangeImpactAnalysisAPI.prototype.setChangeImpactAnalysisConfiguration = function(projectKey, ciaConfig) {
		generalApi.postJSON(this.restPrefix + "/setChangeImpactAnalysisConfiguration?projectKey="
			+ projectKey, ciaConfig, function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The change impact analysis configuration is updated.");
				}
			});
	};

	/*
	 * external references: condec.filtering
	 */
	ConDecChangeImpactAnalysisAPI.prototype.getChangeImpactAnalysisConfiguration = function(projectKey, callback) {
		generalApi.getJSON(this.restPrefix + "/getChangeImpactAnalysisConfiguration?projectKey="
			+ projectKey, callback);
	};
	
    global.conDecChangeImpactAnalysisAPI = new ConDecChangeImpactAnalysisAPI();
})(window);

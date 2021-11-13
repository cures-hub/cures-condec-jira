(function(global) {

	const ConDecDashboardAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/dashboard";
	};

	/*
	 * external references: dashboard/condec.general.metrics.dashboard.js
	 */
	ConDecDashboardAPI.prototype.getGeneralMetrics = function(filterSettings, callback) {
		generalApi.postJSON(this.restPrefix + "/generalMetrics", filterSettings,
			function(error, result) {
				callback(error, result);
			});
	};

	/*
	 * external references: dashboard/condec.rationale.completeness.dashboard.js
	 */
	ConDecDashboardAPI.prototype.getRationaleCompleteness = function(filterSettings, callback) {
		generalApi.postJSON(this.restPrefix + "/rationaleCompleteness", filterSettings,
			function(error, result) {
			callback(error, result);
		});
	};

	/*
	 * external references: dashboard/condec.rationale.coverage.dashboard.js
	 */
	ConDecDashboardAPI.prototype.getRationaleCoverage = function(filterSettings, sourceKnowledgeTypes, callback) {
		generalApi.postJSON(this.restPrefix + "/rationaleCoverage?sourceKnowledgeTypes=" +
			sourceKnowledgeTypes, filterSettings, function(error, result) {
			callback(error, result);
		});
	};

	global.conDecDashboardAPI = new ConDecDashboardAPI();
})(window);
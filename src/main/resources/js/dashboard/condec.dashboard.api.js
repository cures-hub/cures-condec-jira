/**
 * This module implements the communication with the ConDec Java REST API for dashboard metrics.
 */
(function(global) {

	const ConDecDashboardAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/dashboard";
	};

	/**
	 * external references: dashboard/condec.general.metrics.dashboard.js
	 */
	ConDecDashboardAPI.prototype.getGeneralMetrics = function(filterSettings, callback) {
		generalApi.postJSON(this.restPrefix + "/general-metrics", filterSettings,
			function(error, generalMetrics) {
				// necessary because Java map is not recognized as a map in JavaScript
				generalMetrics.numberOfCommentsMap = new Map(Object.entries(generalMetrics.numberOfCommentsMap));
				generalMetrics.numberOfCommitsMap = new Map(Object.entries(generalMetrics.numberOfCommitsMap));
				generalMetrics.distributionOfKnowledgeTypes = new Map(Object.entries(generalMetrics.distributionOfKnowledgeTypes));
				generalMetrics.elementsFromDifferentOrigins = new Map(Object.entries(generalMetrics.elementsFromDifferentOrigins));
				generalMetrics.definitionOfDoneCheckResults = new Map(Object.entries(generalMetrics.definitionOfDoneCheckResults));
				generalMetrics.reqAndClassSummary = new Map(Object.entries(generalMetrics.reqAndClassSummary));	
				generalMetrics.numberOfRelevantComments = new Map(Object.entries(generalMetrics.numberOfRelevantComments));			
				callback(error, generalMetrics);
			});
	};

	/**
	 * external references: dashboard/condec.rationale.completeness.dashboard.js
	 */
	ConDecDashboardAPI.prototype.getRationaleCompleteness = function(filterSettings, callback) {
		generalApi.postJSON(this.restPrefix + "/rationale-completeness", filterSettings,
			function(error, result) {
				callback(error, result);
			});
	};

	/**
	 * external references: dashboard/condec.rationale.coverage.dashboard.js
	 */
	ConDecDashboardAPI.prototype.getRationaleCoverage = function(filterSettings, callback) {
		generalApi.postJSON(this.restPrefix + "/rationale-coverage", filterSettings, function(error, result) {
			callback(error, result);
		});
	};

	global.conDecDashboardAPI = new ConDecDashboardAPI();
})(window);
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
				conDecDashboardAPI.convertJavaMapToJavaScriptMap(generalMetrics);
				callback(error, generalMetrics);
			});
	};

	/**
	 * external references: dashboard/condec.rationale.completeness.dashboard.js
	 */
	ConDecDashboardAPI.prototype.getRationaleCompleteness = function(filterSettings, callback) {
		generalApi.postJSON(this.restPrefix + "/rationale-completeness", filterSettings,
			function(error, rationaleCompletenessMetrics) {
				conDecDashboardAPI.convertJavaMapToJavaScriptMap(rationaleCompletenessMetrics);
				callback(error, rationaleCompletenessMetrics);
			});
	};

	/**
	 * external references: dashboard/condec.rationale.coverage.dashboard.js
	 */
	ConDecDashboardAPI.prototype.getRationaleCoverage = function(filterSettings, callback) {
		generalApi.postJSON(this.restPrefix + "/rationale-coverage", filterSettings, function(error, rationaleCoverageMetrics) {
			conDecDashboardAPI.convertJavaMapToJavaScriptMap(rationaleCoverageMetrics);
			rationaleCoverageMetrics.minimumRequiredCoverage = filterSettings.definitionOfDone.minimumDecisionsWithinLinkDistance;
			callback(error, rationaleCoverageMetrics);
		});
	};

	/**
	 * external references: dashboard/condec.git.branches.dashboard.js
	 */
	ConDecDashboardAPI.prototype.getBranchMetrics = function(filterSettings, callback) {
		generalApi.postJSON(this.restPrefix + "/git", filterSettings, function(error, branchMetrics) {
			conDecDashboardAPI.convertJavaMapToJavaScriptMap(branchMetrics);
			callback(error, branchMetrics);
		});
	};

	/**
	 * external references: condec.decision.grouping.api.js
	 * Necessary because Java map is not recognized as a map in JavaScript.
	 */
	ConDecDashboardAPI.prototype.convertJavaMapToJavaScriptMap = function(metrics) {
		for ([metricName, metricMap] of Object.entries(metrics)) {
			metrics[metricName] = new Map(Object.entries(metricMap));
		}
		return metrics;
	};

	global.conDecDashboardAPI = new ConDecDashboardAPI();
})(window);
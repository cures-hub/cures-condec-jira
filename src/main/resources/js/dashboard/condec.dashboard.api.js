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
				convertJavaMapToJavaScriptMap(generalMetrics);
				callback(error, generalMetrics);
			});
	};
	
	/**
	 * Necessary because Java map is not recognized as a map in JavaScript.
	 */
	function convertJavaMapToJavaScriptMap(metrics) {
		for ([metricName, metricMap] of Object.entries(metrics)) {
			metrics[metricName] = new Map(Object.entries(metricMap));
		}
		return metrics;
	}

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
/**
 * This module implements the communication with the ConDec Java REST API and
 * the Jira API for the dashboard items.
 */
(function (global) {

	var conDecDashboard = function () {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest";
	};

	conDecDashboard.prototype.getGeneralMetrics = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/dashboard/getGeneralMetrics.json?projectKey="
			+ projectKey)
	};

	global.conDecDashboard = new conDecDashboard();
})(window);
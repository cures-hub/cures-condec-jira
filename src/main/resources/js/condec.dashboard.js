/**
 * This module implements the communication with the ConDec Java REST API and
 * the Jira API for the dashboard items.
 */
(function (global) {

	var conDecDashboard = function () {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest";
	};

	conDecDashboard.prototype.getGeneralMetrics = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/dashboard/generalMetrics.json?projectKey="
			+ projectKey)
	};

	conDecDashboard.prototype.getRationaleCompleteness = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/dashboard/rationaleCompleteness.json?projectKey="
			+ projectKey);
	};

	conDecDashboard.prototype.getRationaleCoverage = function (projectKey, issueType, linkDistance) {
		generalApi.getJSON(this.restPrefix + "/dashboard/rationaleCOverage.json?projectKey="
			+ projectKey + "&issueType=" + issueType + "&linkDistance=" + linkDistance);
	};

	conDecDashboard.prototype.getJiraIssueTypes = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/dashboard/jiraIssueTypes.json?projectKey="
			+ projectKey);
	};

	global.conDecDashboard = new conDecDashboard();
})(window);
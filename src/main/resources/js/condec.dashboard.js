/**
 * This module implements the communication with the ConDec Java REST API and
 * the Jira API for the dashboard items.
 */
(function (global) {

	var conDecDashboard = function () {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest";
	};

	conDecDashboard.prototype.getElementsFromAllBranchesOfProject = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/view/elementsFromBranchesOfProject.json?projectKey="
			+ projectKey)
	};

	conDecDashboard.prototype.getElementsOfFeatureBranchForJiraIssue = function (issueKey) {
		generalApi.getJSON(this.restPrefix + "/view/elementsFromBranchesOfJiraIssue.json?issueKey="
			+ issueKey)
	};

	conDecDashboard.prototype.getNumberOfCommentsPerJiraIssue = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/dashboard/numberOfCommentsPerJiraIssue.json?projectKey="
			+ projectKey)
	};

	conDecDashboard.prototype.getNumberOfCommitsPerJiraIssue = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/dashboard/numberOfCommitsPerJiraIssue.json?projectKey="
			+ projectKey)
	};

	conDecDashboard.prototype.getDistributionOfKnowledgeTypes = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/dashboard/distributionOfKnowledgeTypes.json?projectKey="
			+ projectKey)
	};

	conDecDashboard.prototype.getRequirementsAndCodeFiles = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/dashboard/requirementsAndCodeFiles.json?projectKey="
			+ projectKey)
	};

	conDecDashboard.prototype.getNumberOfElementsPerDocumentationLocation = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/dashboard/numberOfElementsPerDocumentationLocation.json?projectKey="
			+ projectKey)
	};

	conDecDashboard.prototype.getNumberOfRelevantComments = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/dashboard/numberOfRelevantComments.json?projectKey="
			+ projectKey)
	};

	conDecDashboard.prototype.getGeneralMetrics = function (projectKey) {
		generalApi.getJSON(this.restPrefix + "/dashboard/getGeneralMetrics.json?projectKey="
			+ projectKey)
	};

	global.conDecDashboard = new conDecDashboard();
})(window);
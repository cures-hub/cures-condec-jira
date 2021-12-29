/**
 * This module implements the communication with the ConDec Java REST API for git configuration and connection.
 *
 * Requires: conDecAPI
 * Required by: conDecDialog, dashboard/condec.git.branches.dashboard.js
 *
 * Is referenced in HTML by settings/git/...
 */
(function(global) {

	var ConDecGitAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/git";
	};

	ConDecGitAPI.prototype.setKnowledgeExtractedFromGit = function(isKnowledgeExtractedFromGit, projectKey) {
		generalApi.postJSON(this.restPrefix + "/activate/" + projectKey,
			isKnowledgeExtractedFromGit, function(error, response) {
				if (error === null) {
					if (isKnowledgeExtractedFromGit) {
						conDecAPI.showFlag("success", "Git connection for this project is activated.");
					} else {
						conDecAPI.showFlag("success", "Git connection for this project is deactivated.");
					}
				}
			});
	};

	ConDecGitAPI.prototype.setPostFeatureBranchCommits = function(checked, projectKey) {
		generalApi.postJSON(this.restPrefix + "/configuration/" + projectKey + "/post-feature-branch-commits",
			checked, function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "Post feature branch commits for this project has been set to " + checked
						+ ".");
				}
			});
	};

	ConDecGitAPI.prototype.setPostDefaultBranchCommits = function(checked, projectKey) {
		generalApi.postJSON(this.restPrefix + "/configuration/" + projectKey + "/post-default-branch-commits",
			checked, function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "Post default branch commits for this project has been set to " + checked
						+ ".");
				}
			});
	};

	ConDecGitAPI.prototype.setGitRepositoryConfigurations = function(projectKey, gitRepositoryConfigurations) {
		generalApi.postJSON(this.restPrefix + "/configuration/" + projectKey + "/repositories", 
				gitRepositoryConfigurations, function(error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "The git URIs and credentials for this project have been set.");
			}
		});
	};

	ConDecGitAPI.prototype.setCodeFileEndings = function(projectKey, codeFileEndings) {
		generalApi.postJSON(this.restPrefix + "/configuration/" + projectKey + "/file-endings",
			codeFileEndings, function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The code file endings for this project have been set.");
				}
			});
	};

	ConDecGitAPI.prototype.deleteGitRepos = function(projectKey) {
		generalApi.deleteJSON(this.restPrefix + "/" + projectKey, null,
			function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The git repos for this project were deleted.");
				}
			});
	};

	/**
	 * external references: condec.dialog
	 */
	ConDecGitAPI.prototype.getSummarizedCode = function(id, documentationLocation, probability) {
		var filterSettings = {
			"projectKey": conDecAPI.projectKey,
			"selectedElementObject": {
				"id": id,
				"documentationLocation": documentationLocation
			}
		};
		return generalApi.postJSONReturnTextPromise(this.restPrefix
			+ "/summary?minProbabilityOfCorrectness=" + probability, filterSettings);
	};

	/**
	 * external references: dashboard/condec.git.branches.dashboard.js
	 */
	ConDecGitAPI.prototype.getDiffForProject = function(projectKey) {
		return generalApi.getJSONReturnPromise(this.restPrefix + "/diff/project?projectKey=" + projectKey);
	};

	/**
	 * external references: condec.git.js
	 */
	ConDecGitAPI.prototype.getBranches = function() {
		var jiraIssueKey = conDecAPI.getIssueKey();
		if (jiraIssueKey !== undefined && jiraIssueKey !== null) {
			return this.getDiffForJiraIssue(jiraIssueKey);
		}
		return this.getDiffForProject(conDecAPI.projectKey);
	};

	/**
	 * external references: none, only local usage in getBranches
	 */
	ConDecGitAPI.prototype.getDiffForJiraIssue = function(jiraIssueKey) {
		return generalApi.getJSONReturnPromise(
			`${this.restPrefix}/diff/jira-issue?jiraIssueKey=${jiraIssueKey}`);
	};

	global.conDecGitAPI = new ConDecGitAPI();
})(window);
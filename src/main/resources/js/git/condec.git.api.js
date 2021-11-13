/**
 * This module implements the communication with the ConDec Java REST API for git configuration and conntection.
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
		generalApi.postJSON(this.restPrefix + "/setKnowledgeExtractedFromGit?projectKey="
			+ projectKey + "&isKnowledgeExtractedFromGit=" + isKnowledgeExtractedFromGit, null, function(error,
				response) {
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
		generalApi.postJSON(this.restPrefix + "/setPostFeatureBranchCommits?projectKey="
			+ projectKey + "&isPostFeatureBranchCommits=" + checked, null, function(error,
				response) {
			if (error === null) {
				conDecAPI.showFlag("success", "Post feature branch commits for this project has been set to " + checked
					+ ".");
			}
		});
	};

	ConDecGitAPI.prototype.setPostDefaultBranchCommits = function(checked, projectKey) {
		generalApi.postJSON(this.restPrefix + "/setPostDefaultBranchCommits?projectKey="
			+ projectKey + "&isPostDefaultBranchCommits=" + checked, null, function(error,
				response) {
			if (error === null) {
				conDecAPI.showFlag("success", "Post default branch commits for this project has been set to " + checked
					+ ".");
			}
		});
	};

	ConDecGitAPI.prototype.setGitRepositoryConfigurations = function(projectKey, gitRepositoryConfigurations) {
		generalApi.postJSON(this.restPrefix + "/setGitRepositoryConfigurations?projectKey="
			+ projectKey, gitRepositoryConfigurations, function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The git URIs and credentials for this project have been set.");
				}
			});
	};

	ConDecGitAPI.prototype.setCodeFileEndings = function(projectKey, codeFileEndings) {
		generalApi.postJSON(this.restPrefix + "/setCodeFileEndings?projectKey="
			+ projectKey, codeFileEndings, function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The code file endings for this project have been set.");
				}
			});
	};

	ConDecGitAPI.prototype.deleteGitRepos = function(projectKey) {
		generalApi.postJSON(this.restPrefix + "/deleteGitRepos?projectKey=" + projectKey, null,
			function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The git repos for this project were deleted.");
				}
			});
	};
	
	/*
	 * external references: condec.dialog
	 */
	ConDecGitAPI.prototype.getSummarizedCode = function(id, documentationLocation, probability, callback) {
		generalApi.getText(this.restPrefix + "/getSummarizedCode?projectKey=" + conDecAPI.projectKey
			+ "&id=" + id + "&documentationLocation=" + documentationLocation + "&probability=" + probability,
			function(error, summary) {
				if (error === null) {
					callback(summary);
				}
			});
	};
	
	ConDecGitAPI.prototype.getElementsFromBranchesOfJiraIssue = function(jiraIssueKey) {
		return generalApi.getJSONReturnPromise(
			`${this.restPrefix}/elementsFromBranchesOfJiraIssue?issueKey=${jiraIssueKey}`);
	};
	
	/*
	 * external references: dashboard/condec.git.branches.dashboard.js
	 */
	ConDecGitAPI.prototype.getElementsFromBranchesOfProject = function(projectKey, callback) {
		generalApi.getJSON(conDecGitAPI.restPrefix + "/elementsFromBranchesOfProject?projectKey=" + projectKey,
			function(error, result) {
				callback(error, result);
			});
	};

	global.conDecGitAPI = new ConDecGitAPI();
})(window);
/**
 * This module implements the communication with the ConDec Java REST API and
 * the Jira API important to create, show, and manage release notes.
 * 
 * Requires: conDecAPI, generalAPI
 * 
 * Is required by: conDecReleaseNotesPage
 * 
 * Is referenced in HTML by
 * templates/settings/releaseNotesSettings.vm
 */
(function(global) {

	var projectKey = null;

	var ConDecReleaseNotesAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/releasenotes";
		projectKey = conDecAPI.getProjectKey();
	};

	/**
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.getReleaseNotesConfiguration = function() {
		return generalApi.getJSONReturnPromise(this.restPrefix + "/configuration/" + projectKey);
	};

	/**
	 * external references: template/settings/releaseNotesSettings.vm
	 */
	ConDecReleaseNotesAPI.prototype.saveReleaseNotesConfiguration = function(projectKey, releaseNotesConfig) {
		generalApi.postJSON(this.restPrefix + "/configuration/" + projectKey, releaseNotesConfig,
			function(error) {
				if (error === null) {
					conDecAPI.showFlag("success", "The release notes configuration for this project was saved.");
				}
			});
	};

	/**
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.getReleaseNotesById = function(id) {
		return generalApi.getJSONReturnPromise(this.restPrefix + "/" + id);
	};

	/**
	 * external references: condec.release.notes.page
	 */
	ConDecReleaseNotesAPI.prototype.getReleaseNotes = function(searchTerm) {
		return generalApi.getJSONReturnPromise(this.restPrefix + "?projectKey="
			+ projectKey + "&searchTerm=" + searchTerm);
	};

	/**
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.proposeElements = function(relaseNotesConfiguration) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/propose-elements/" + projectKey,
			relaseNotesConfiguration);
	};

	/**
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.createReleaseNotesContent = function(releaseNotes) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/create-content", releaseNotes);
	};

	/**
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.createReleaseNotes = function(releaseNotes) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/create", releaseNotes);
	};

	/**
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.updateReleaseNotes = function(releaseNotes) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/update", releaseNotes);
	};

	/**
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.deleteReleaseNotes = function(id) {
		return generalApi.deleteJSONReturnPromise(this.restPrefix + "/" + id, null);
	};

	/**
	 * external references: condec.release.notes.dialog
	 * 
	 * @return all Jira issue types for a project.
	 */
	ConDecReleaseNotesAPI.prototype.getIssueTypes = function() {
		var issueTypeUrl = "/rest/api/2/issue/createmeta?expand=projects.issuetypes&projectKeys=" + projectKey;
		return generalApi.getJSONReturnPromise(AJS.contextPath() + issueTypeUrl).then(function(result) {
			if (result && result.projects && result.projects.length) {
				var correctIssueTypes = result.projects.filter(function(project) {
					return project.key === projectKey;
				});
				correctIssueTypes = correctIssueTypes[0].issuetypes;
				if (correctIssueTypes && correctIssueTypes.length) {
					return correctIssueTypes;
				} else {
					conDecAPI.showFlag("error", "No Jira issue types could be found for this project.");
				}
			} else {
				conDecAPI.showFlag("error", "No Jira projects were found.");
			}
		});
	};

	/**
	 * external references: condec.release.notes.dialog
	 * 
	 * @return releases for a Jira project.
	 */
	ConDecReleaseNotesAPI.prototype.getReleases = function() {
		var issueTypeUrl = "/rest/projects/latest/project/" + projectKey + "/release/allversions";
		return generalApi.getJSONReturnPromise(AJS.contextPath() + issueTypeUrl).catch(function(err) {
			conDecAPI.showFlag("info", "Releases could not be loaded. " + err);
		});
	};

	/**
	 * external references: condec.release.notes.dialog
	 * 
	 * @return sprints for the Jira project. Finds the board(s) first, then the sprints for the board(s).
	 */
	ConDecReleaseNotesAPI.prototype.getSprintsByProject = function() {
		return new Promise(function(resolve, reject) {
			var boardUrl = "/rest/agile/latest/board?projectKeyOrId=" + projectKey;
			var boardPromise = generalApi.getJSONReturnPromise(AJS.contextPath() + boardUrl);
			boardPromise.then(function(boards) {
				if (boards && boards.values && boards.values.length) {
					var sprintPromises = boards.values.map(function(board) {
						var sprintUrl = "/rest/agile/latest/board/" + board.id + "/sprint";
						return generalApi.getJSONReturnPromise(AJS.contextPath() + sprintUrl);
					});
					Promise.all(sprintPromises)
						.then(function(sprints) {
							resolve(sprints);
						}).catch(function(err) {
							reject(err);
						})
				} else {
					reject("No boards could be found, so the sprints could also not be loaded");
				}
			}).catch(function(err) {
				reject(err);
			});
		});
	};

	global.conDecReleaseNotesAPI = new ConDecReleaseNotesAPI();
})(window);
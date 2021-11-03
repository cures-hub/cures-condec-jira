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

	ConDecReleaseNotesAPI.prototype.getIssueTypes = function() {
		// first we need the boards then we can get the sprints for each
		// board
		return new Promise(function(resolve, reject) {
			var issueTypeUrl = "/rest/api/2/issue/createmeta?expand=projects.issuetypes";
			var issuePromise = generalApi.getJSONReturnPromise(AJS.contextPath() + issueTypeUrl);
			issuePromise.then(function(result) {
				if (result && result.projects && result.projects.length) {
					var correctIssueTypes = result.projects.filter(function(project) {
						return project.key === projectKey;
					});
					correctIssueTypes = correctIssueTypes[0].issuetypes;
					if (correctIssueTypes && correctIssueTypes.length) {
						resolve(correctIssueTypes);
					} else {
						reject("No issue types could be found for this project");
					}
				} else {
					reject("No Projects were found.");
				}

			}).catch(function(err) {
				reject(err);
			})
		})
	};

	ConDecReleaseNotesAPI.prototype.getReleases = function() {
		// first we need the boards then we can get the Sprints for each
		// board
		return new Promise(function(resolve, reject) {
			var issueTypeUrl = "/rest/projects/latest/project/" + projectKey + "/release/allversions";
			var issuePromise = generalApi.getJSONReturnPromise(AJS.contextPath() + issueTypeUrl);
			issuePromise.then(function(result) {
				if (result && result.length) {
					resolve(result);
				} else {
					reject("No releases were found");
				}
			}).catch(function(err) {
				reject(err);
			})
		})
	};

	ConDecReleaseNotesAPI.prototype.getProjectWideSelectedIssueTypes = function() {
		return new Promise(function(resolve, reject) {
			var preSelectedIssueUrl = conDecAPI.restPrefix + "/config/releaseNoteMapping?projectKey=" + projectKey;
			var issuePromise = generalApi.getJSONReturnPromise(preSelectedIssueUrl);
			issuePromise.then(function(result) {
				if (result) {
					resolve(result);
				} else {
					reject();
				}
			}).catch(function(err) {
				reject(err);
			})
		})
	};

	/*
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.getSprintsByProject = function() {
		// first we need the boards then we can get the Sprints for each
		// board
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
			})
		})
	};

	/*
	 * external references: settingsForSingleProject.vm
	 * TODO Pass ReleaseNotesConfig
	 */
	ConDecReleaseNotesAPI.prototype.setReleaseNoteMapping = function(releaseNoteCategory, projectKey, selectedIssueTypes) {
		generalApi.postJSON(conDecAPI.restPrefix + "/config/setReleaseNoteMapping?projectKey=" + projectKey + "&releaseNoteCategory=" + releaseNoteCategory, selectedIssueTypes, function(
			error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "The associated Jira issue types for the category: " + releaseNoteCategory + " were changed for this project.");
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
		return generalApi.postJSONReturnPromise(this.restPrefix + "/propose-elements?projectKey="
			+ projectKey, relaseNotesConfiguration);
	};

	/**
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.postProposedKeys = function(proposedKeys) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/postProposedKeys?projectKey="
			+ projectKey, proposedKeys);
	};

	/**
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.createReleaseNotes = function(releaseNotes) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/create",
			releaseNotes);
	};

	/**
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.updateReleaseNotes = function(releaseNotes) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/update",
			releaseNotes);
	};

	/**
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.deleteReleaseNotes = function(id) {
		return generalApi.deleteJSONReturnPromise(this.restPrefix + "/delete?"
			+ "id=" + id, null);
	};

	global.conDecReleaseNotesAPI = new ConDecReleaseNotesAPI();
})(window);
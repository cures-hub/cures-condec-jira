/**
 * This module implements the communication with the ConDec Java REST API and
 * the Jira API important to create, show, and manage release notes.
 * 
 * Requires: conDecAPI, generalAPI
 * 
 * Is required by: conDecReleaseNotesPage
 * 
 * Is referenced in HTML by
 * templates/releaseNotesSettings.vm
 */
(function (global) {

	var projectKey = null;

	var ConDecReleaseNotesAPI = function () {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest";
		projectKey = conDecAPI.getProjectKey();
	};

	ConDecReleaseNotesAPI.prototype.getIssueTypes = function () {
		// first we need the boards then we can get the sprints for each
		// board
		return new Promise(function (resolve, reject) {
			var issueTypeUrl = "/rest/api/2/issue/createmeta?expand=projects.issuetypes";
			var issuePromise = generalApi.getJSONReturnPromise(AJS.contextPath() + issueTypeUrl);
			issuePromise.then(function (result) {
				if (result && result.projects && result.projects.length) {
					var correctIssueTypes = result.projects.filter(function (project) {
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

			}).catch(function (err) {
				reject(err);
			})
		})
	};

	ConDecReleaseNotesAPI.prototype.getReleases = function () {
		// first we need the boards then we can get the Sprints for each
		// board
		return new Promise(function (resolve, reject) {
			var issueTypeUrl = "/rest/projects/latest/project/" + projectKey + "/release/allversions";
			var issuePromise = generalApi.getJSONReturnPromise(AJS.contextPath() + issueTypeUrl);
			issuePromise.then(function (result) {
				if (result && result.length) {
					resolve(result);
				} else {
					reject("No releases were found");
				}
			}).catch(function (err) {
				reject(err);
			})
		})
	};

	ConDecReleaseNotesAPI.prototype.getProjectWideSelectedIssueTypes = function () {
		return new Promise(function (resolve, reject) {
			var preSelectedIssueUrl = conDecReleaseNotesAPI.restPrefix + "/config/releaseNoteMapping.json?projectKey=" + projectKey;
			var issuePromise = generalApi.getJSONReturnPromise(preSelectedIssueUrl);
			issuePromise.then(function (result) {
				if (result) {
					resolve(result);
				} else {
					reject();
				}
			}).catch(function (err) {
				reject(err);
			})
		})
	};

	/*
	 * external references: condec.dialog
	 */
	ConDecReleaseNotesAPI.prototype.getSprintsByProject = function () {
		// first we need the boards then we can get the Sprints for each
		// board
		return new Promise(function (resolve, reject) {
			var boardUrl = "/rest/agile/latest/board?projectKeyOrId=" + projectKey;
			var boardPromise = generalApi.getJSONReturnPromise(AJS.contextPath() + boardUrl);
			boardPromise.then(function (boards) {
				if (boards && boards.values && boards.values.length) {
					var sprintPromises = boards.values.map(function (board) {
						var sprintUrl = "/rest/agile/latest/board/" + board.id + "/sprint";
						return generalApi.getJSONReturnPromise(AJS.contextPath() + sprintUrl);
					});
					Promise.all(sprintPromises)
					.then(function (sprints) {
						resolve(sprints);
					}).catch(function (err) {
						reject(err);
					})
				} else {
					reject("No boards could be found, so the sprints could also not be loaded");
				}
			}).catch(function (err) {
				reject(err);
			})
		})
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecReleaseNotesAPI.prototype.setReleaseNoteMapping = function (releaseNoteCategory, projectKey, selectedIssueTypes) {
		generalApi.postJSON(this.restPrefix + "/config/setReleaseNoteMapping.json?projectKey=" + projectKey + "&releaseNoteCategory=" + releaseNoteCategory, selectedIssueTypes, function (
				error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "The associated Jira issue types for the category: " + releaseNoteCategory + " were changed for this project.");
			}
		});
	};

	/*
	 * external references: condec.release.notes.page
	 */
	ConDecReleaseNotesAPI.prototype.getReleaseNotes = function (callback) {
		var projectKey = getProjectKey();
		generalApi.getJSON(this.restPrefix + "/release-note/getReleaseNotes.json?projectKey="
				+ projectKey, function (error, elements) {
			if (error === null) {
				callback(elements);
			}
		});
	};

	ConDecReleaseNotesAPI.prototype.getReleaseNotesById = function (id) {
		return generalApi.getJSONReturnPromise(this.restPrefix + "/release-note/getReleaseNote.json?projectKey="
				+ projectKey + "&id=" + id);

	};
	
	ConDecReleaseNotesAPI.prototype.getAllReleaseNotes = function (query) {
		return generalApi.getJSONReturnPromise(this.restPrefix + "/release-note/getAllReleaseNotes.json?projectKey="
				+ projectKey + "&query=" + query);
	};
	
	/*
	 * external references: condec.release.notes.page
	 */
	ConDecReleaseNotesAPI.prototype.getReleaseNotes = function (callback) {
		var projectKey = getProjectKey();
		generalApi.getJSON(this.restPrefix + "/release-note/getReleaseNotes.json?projectKey="
				+ projectKey, function (error, elements) {
			if (error === null) {
				callback(elements);
			}
		});
	};
	
	/*
	 * external references: condec.release.notes.dialog
	 */
	ConDecReleaseNotesAPI.prototype.getProposedIssues = function (releaseNoteConfiguration) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/release-note/getProposedIssues.json?projectKey="
				+ projectKey, releaseNoteConfiguration);
	};

	ConDecReleaseNotesAPI.prototype.postProposedKeys = function (proposedKeys) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/release-note/postProposedKeys.json?projectKey="
				+ projectKey, proposedKeys);
	};
	
	ConDecReleaseNotesAPI.prototype.createReleaseNote = function (content) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/release-note/createReleaseNote.json?projectKey="
				+ projectKey, content);
	};
	
	ConDecReleaseNotesAPI.prototype.updateReleaseNote = function (releaseNote) {
		return generalApi.postJSONReturnPromise(this.restPrefix + "/release-note/updateReleaseNote.json?projectKey="
				+ projectKey, releaseNote)
	};
	
	ConDecReleaseNotesAPI.prototype.deleteReleaseNote = function (id) {
		return generalApi.deleteJSONReturnPromise(this.restPrefix + "/release-note/deleteReleaseNote.json?projectKey="
				+ projectKey + "&id=" + id, null);
	};

	global.conDecReleaseNotesAPI = new ConDecReleaseNotesAPI();
})(window);
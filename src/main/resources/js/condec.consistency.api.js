(function (global) {

		const ConsistencyAPI = function ConsistencyAPI() {
			this.restPrefix = AJS.contextPath() + "/rest/condec/latest/consistency";
			this.projectKey = conDecAPI.getProjectKey();
			this.consistencyCheckFlag = undefined;
			let that = this;
			global.addEventListener("DOMContentLoaded", () => {
				that.displayConsistencyCheck();
			});



		};

		ConsistencyAPI.prototype.getRelatedKnowledgeElements = function (projectKey, elementId, elementLocation) {
			return generalApi.getJSONReturnPromise(
				`${this.restPrefix}/getRelatedKnowledgeElements.json
				?projectKey=${projectKey}
				&elementId=${elementId}
				&elementLocation=${elementLocation}`);
		};


		ConsistencyAPI.prototype.discardLinkSuggestion = function
			(projectKey, originElementId, originElementLocation, targetElementId, targetElementLocation) {
			return generalApi.postJSONReturnPromise(
				`${this.restPrefix}/discardLinkSuggestion.json
				?projectKey=${projectKey}
				&originElementId=${originElementId}
				&originElementLocation=${originElementLocation}
				&targetElementId=${targetElementId}
				&targetElementLocation=${targetElementLocation}`
			);
		};

		ConsistencyAPI.prototype.discardDuplicateSuggestion = function
			(projectKey, originElementId, originElementLocation, targetElementId, targetElementLocation) {
			return generalApi.postJSONReturnPromise(
				`${this.restPrefix}/discardDuplicate.json
				?projectKey=${projectKey}
				&originElementId=${originElementId}
				&originElementLocation=${originElementLocation}
				&targetElementId=${targetElementId}
				&targetElementLocation=${targetElementLocation}`
			);
		};

		ConsistencyAPI.prototype.getDuplicateKnowledgeElement = function (projectKey, elementId, location) {
			return generalApi.getJSONReturnPromise(
				`${this.restPrefix}/getDuplicateKnowledgeElement.json
				?projectKey=${projectKey}
				&elementId=${elementId}
				&location=${location}`
			);
		};

		ConsistencyAPI.prototype.doesElementNeedApproval = function (projectKey, elementId, elementLocation) {
			return generalApi.getJSONReturnPromise(
				`${this.restPrefix}/doesElementNeedApproval.json
				?projectKey=${projectKey}
				&elementId=${elementId}
				&elementLocation=${elementLocation}`
			);
		};

		ConsistencyAPI.prototype.doesElementNeedCompletenessApproval = function (filterSettings) {
			return generalApi.postJSONReturnPromise(
				`${this.restPrefix}/doesElementNeedCompletenessApproval.json`, filterSettings
			);
		};

		ConsistencyAPI.prototype.approveCheck = function (projectKey, elementId, elementLocation, user) {
			return generalApi.postJSONReturnPromise(
				`${this.restPrefix}/approveCheck.json
				?projectKey=${projectKey}
				&elementId=${elementId}
				&elementLocation=${elementLocation}
				&user=${user}`
			);
		};

		ConsistencyAPI.prototype.approveInconsistencies = function () {
			consistencyAPI.approveCheck(this.projectKey, this.issueId, "i", JIRA.Users.LoggedInUser.userName());
			this.consistencyCheckFlag.close();
		}

		ConsistencyAPI.prototype.displayConsistencyCheck = function () {
			let that = this;
			this.issueId = JIRA.Issue.getIssueId();

			console.log("displayConsistencyCheck: " + this.issueId);

			if (that.issueId !== null && that.issueId !== undefined) {
				this.doesElementNeedApproval(that.projectKey, that.issueId, "i")
					.then((response) => {
						if (response.needsApproval) {
							Promise.all([this.getDuplicateKnowledgeElement(that.projectKey, that.issueId, "i"),
								consistencyAPI.getRelatedKnowledgeElements(that.projectKey, that.issueId, "i")]).then(
								(values) => {
									let numDuplicates = (values[0].duplicates.length);
									let numRelated = (values[1].relatedIssues.length);
									if (numDuplicates + numRelated > 0) {
										that.consistencyCheckFlag = AJS.flag({
											type: 'warning',
											title: 'Possible inconsistencies detected!',
											close: 'manual',
											body: 'Issue <strong>'
												+ conDecAPI.getIssueKey()
												+ '</strong> contains some detected inconsistencies. <br/>'
												+ '<ul>'
												+ '<li> ' + numRelated + ' possibly related issues </li>'
												+ '<li> ' + numDuplicates + ' possible duplicates </li>'
												+ '</ul>'
												+ '<ul class="aui-nav-actions-list">'
												+ '<li>'
												+ '<button id="consistency-check-dialog-submit-button" '
												+ 'onclick="consistencyAPI.approveInconsistencies()" class="aui-button aui-button-link">'
												+ 'I approve the consistency of this knowledge element!'
												+ '</button>'
												+ '</li>'
												+ '</ul>'
										});
									}

								});

						}
					});
			}
		}

		ConsistencyAPI.prototype.displayCompletenessCheck = function () {
			let that = this;
			this.issueKey = conDecAPI.getIssueKey();

			console.log("displayCompletenessCheck: " + this.issueKey);

			if (that.issueKey !== null && that.issueKey !== undefined) {
				var filterSettings = {
					"projectKey" : this.projectKey,
					"selectedElement" : this.issueKey
				}
				this.doesElementNeedCompletenessApproval(filterSettings)
					.then((response) => {
						if (response.needsCompletenessApproval) {
							Promise.all([]).then(
								() => {
										that.consistencyCheckFlag = AJS.flag({
											type: 'warning',
											title: 'Imcomplete decision knowledge!',
											close: 'manual',
											body: 'Issue <strong>'
												+ this.issueKey
												+ '</strong> contains some incomplete documented decision knowledge. <br/>'
												+ '<ul class="aui-nav-actions-list">'
												+ '<li>'
												+ '<button id="completeness-check-dialog-submit-button" '
												+ 'onclick="consistencyAPI.consistencyCheckFlag.close()" class="aui-button aui-button-link">'
												+ 'Confirm'
												+ '</button>'
												+ '</li>'
												+ '</ul>'
										});
								});
						}
					});
			}
		}

		ConsistencyAPI.prototype.confirmIncompleteMessage = function () {
			this.consistencyCheckFlag.close();
		}

		// export ConsistencyAPI
		global.consistencyAPI = new ConsistencyAPI();
	}
)(window);

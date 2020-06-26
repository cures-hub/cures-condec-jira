(function (global) {

		const ConsistencyAPI = function ConsistencyAPI() {
			this.restPrefix = AJS.contextPath() + "/rest/condec/latest/consistency";
			this.consistencyCheckFlag = undefined;
			this.displayConsistencyCheck();
		};

		ConsistencyAPI.prototype.getRelatedIssues = function (issueKey) {
			return generalApi.getJSONReturnPromise(this.restPrefix + "/getRelatedIssues.json?issueKey=" + issueKey);
		};


		ConsistencyAPI.prototype.discardLinkSuggestion = function discardLinkSuggestion(baseIssueKey, otherIssueKey, projectKey) {
			return generalApi.postJSONReturnPromise(
				`${this.restPrefix}/discardLinkSuggestion.json?projectKey=${projectKey}
				&originIssueKey=${baseIssueKey}&targetIssueKey=${otherIssueKey}`
			);
		};

		ConsistencyAPI.prototype.discardDuplicateSuggestion = function discardDuplicateSuggestion(baseIssueKey, otherIssueKey, projectKey) {
			return generalApi.postJSONReturnPromise(
				`${this.restPrefix}/discardDuplicate.json?projectKey=${projectKey}
				&originIssueKey=${baseIssueKey}&targetIssueKey=${otherIssueKey}`
			);
		};

		ConsistencyAPI.prototype.getDuplicatesForIssue = function getDuplicatesForIssue(issueKey) {
			return generalApi.getJSONReturnPromise(
				`${this.restPrefix}/getDuplicatesForIssue.json?issueKey=${issueKey}`
			);
		};

		ConsistencyAPI.prototype.doesIssueNeedApproval = function doesIssueNeedApproval(issueKey) {
			return generalApi.getJSONReturnPromise(
				`${this.restPrefix}/doesIssueNeedApproval.json?issueKey=${issueKey}`
			);
		};

		ConsistencyAPI.prototype.approveCheck = function approveCheck(issueKey, user) {
			return generalApi.postJSONReturnPromise(
				`${this.restPrefix}/approveCheck.json?issueKey=${issueKey}&user=${user}`
			);
		};

		ConsistencyAPI.prototype.approveInconsistencies = function () {
			consistencyAPI.approveCheck(conDecAPI.getIssueKey(), JIRA.Users.LoggedInUser.userName());
			this.consistencyCheckFlag.close();
		}

		ConsistencyAPI.prototype.displayConsistencyCheck = function () {
			let self = this;
			this.doesIssueNeedApproval(conDecAPI.getIssueKey())
				.then((response) => {
				if (response.needsApproval) {
					Promise.all([this.getDuplicatesForIssue(conDecAPI.getIssueKey()),
						consistencyAPI.getRelatedIssues(conDecAPI.getIssueKey())]).then(
						(values) => {
							let numDuplicates = (values[0].duplicates.length);
							let numRelated = (values[1].relatedIssues.length);
							if (numDuplicates + numRelated > 0) {
								self.consistencyCheckFlag = AJS.flag({
									type: 'warning',
									title: 'Possible inconsistencies detected!',
									body: 'Issue <strong>' + conDecAPI.getIssueKey() + '</strong> contains some detected inconsistencies. <br/>'
										+ '<ul>'
										+ '<li> ' + numRelated + ' possibly related issues </li>'
										+ '<li> ' + numDuplicates + ' possible duplicates </li>'
										+ '</ul>'
										+ '<ul class="aui-nav-actions-list">'
										+ '<li><button id="consistency-check-dialog-submit-button" onclick="consistencyAPI.approveInconsistencies()" class="aui-button aui-button-link">Approve</button></li>'
										+ '</ul>'
								});
							}

						});

				}
			});

		}

		// export ConsistencyAPI
		global.consistencyAPI = new ConsistencyAPI();
	}
)(window);

(function (global) {

		const ConsistencyAPI = function ConsistencyAPI() {
			this.restPrefix = AJS.contextPath() + "/rest/condec/latest/consistency";

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

		// export ConsistencyAPI
		global.consistencyAPI = new ConsistencyAPI();
	}
)(window);

(function (global) {

		const ConsistencyAPI = function ConsistencyAPI() {
			this.restPrefix = AJS.contextPath() + "/rest/condec/latest";

		};

		ConsistencyAPI.prototype.getRelatedIssues = function (issueKey) {
			return generalApi.getJSONReturnPromise(this.restPrefix + "/consistency/getRelatedIssues.json?issueKey=" + issueKey);
		};


		ConsistencyAPI.prototype.discardLinkSuggestion = function discardLinkSuggestion(baseIssueKey, otherIssueKey, projectKey) {
			return generalApi.postJSONReturnPromise(
				`${this.restPrefix}/consistency/discardLinkSuggestion.json?projectKey=${projectKey}
				&originIssueKey=${baseIssueKey}&targetIssueKey=${otherIssueKey}`
			);
		};

		ConsistencyAPI.prototype.discardDuplicateSuggestion = function discardDuplicateSuggestion(baseIssueKey, otherIssueKey, projectKey) {
			return generalApi.postJSONReturnPromise(
				`${this.restPrefix}/consistency/discardDuplicate.json?projectKey=${projectKey}
				&originIssueKey=${baseIssueKey}&targetIssueKey=${otherIssueKey}`
			);
		};

		ConsistencyAPI.prototype.getDuplicatesForIssue = function getDuplicatesForIssue(issueKey) {
			return generalApi.getJSONReturnPromise(
				`${this.restPrefix}/consistency/getDuplicatesForIssue.json?issueKey=${issueKey}`
			);
		};

		// export ConsistencyAPI
		global.consistencyAPI = new ConsistencyAPI();
	}
)(window);

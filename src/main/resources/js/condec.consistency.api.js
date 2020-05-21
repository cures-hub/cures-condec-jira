(function (global) {

		const ConsistencyAPI = function ConsistencyAPI() {
			this.restPrefix = AJS.contextPath() + "/rest/condec/latest";

		};

		ConsistencyAPI.prototype.loadRelatedIssues = function (issueKey) {
			return generalApi.getJSONReturnPromise(this.restPrefix + "/consistency/getRelatedIssues.json?issueKey=" + issueKey);
		};


		ConsistencyAPI.prototype.discardLinkSuggestion = function discardLinkSuggestion(baseIssueKey, otherIssueKey, projectKey) {
			return generalApi.postJSONReturnPromise(
				`${this.restPrefix}/consistency/discardLinkSuggestion.json?projectKey=${projectKey}
				&originIssueKey=${baseIssueKey}&targetIssueKey=${otherIssueKey}`
			);
		};

		ConsistencyAPI.prototype.discardDuplicateSuggestion = function discardLinkSuggestion(baseIssueKey, otherIssueKey, projectKey) {
			return generalApi.postJSONReturnPromise(
				`${this.restPrefix}/consistency/discardDuplicate.json?projectKey=${projectKey}
				&originIssueKey=${baseIssueKey}&targetIssueKey=${otherIssueKey}`
			);
		};

		ConsistencyAPI.prototype.getDuplicatesForIssue = function discardLinkSuggestion(issueKey) {
			return generalApi.getJSONReturnPromise(
				`${this.restPrefix}/consistency/getDuplicatesForIssue.json?issueKey=${issueKey}`
			);
		};

		// export ConsistencyAPI
		global.consistencyAPI = new ConsistencyAPI();
	}
)(window);

(function(global) {

	const ConsistencyAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/consistency";
		this.projectKey = conDecAPI.getProjectKey();
	};

	ConsistencyAPI.prototype.setMinimumDuplicateLength = function(projectKey, fragmentLength) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/setMinimumDuplicateLength.json?
			projectKey=${projectKey}&fragmentLength=${fragmentLength}`, null)
			.then(conDecAPI.showFlag("success", "Minimum length was successfully updated!"));
	}

	ConsistencyAPI.prototype.setMinimumLinkSuggestionProbability = function(projectKey, minLinkSuggestionProbability) {
		generalApi.postJSONReturnPromise(this.restPrefix + `/setMinimumLinkSuggestionProbability.json?
			projectKey=${projectKey}&minLinkSuggestionProbability=${minLinkSuggestionProbability}`, null)
			.then(conDecAPI.showFlag("success", "Minimum probability was successfully updated!"));
	}

	ConsistencyAPI.prototype.getRelatedKnowledgeElements = function(projectKey, elementId, elementLocation) {
		return generalApi.getJSONReturnPromise(
			`${this.restPrefix}/getRelatedKnowledgeElements.json
				?projectKey=${projectKey}
				&elementId=${elementId}
				&elementLocation=${elementLocation}`);
	};

	ConsistencyAPI.prototype.discardLinkSuggestion = function(projectKey, originElementId, originElementLocation, targetElementId, targetElementLocation) {
		return generalApi.postJSONReturnPromise(
			`${this.restPrefix}/discardLinkSuggestion.json
				?projectKey=${projectKey}
				&originElementId=${originElementId}
				&originElementLocation=${originElementLocation}
				&targetElementId=${targetElementId}
				&targetElementLocation=${targetElementLocation}`
		);
	};

	ConsistencyAPI.prototype.discardDuplicateSuggestion = function(projectKey, originElementId, originElementLocation, targetElementId, targetElementLocation) {
		return generalApi.postJSONReturnPromise(
			`${this.restPrefix}/discardDuplicate.json
				?projectKey=${projectKey}
				&originElementId=${originElementId}
				&originElementLocation=${originElementLocation}
				&targetElementId=${targetElementId}
				&targetElementLocation=${targetElementLocation}`
		);
	};

	ConsistencyAPI.prototype.getDuplicateKnowledgeElement = function(projectKey, elementId, location) {
		return generalApi.getJSONReturnPromise(
			`${this.restPrefix}/getDuplicateKnowledgeElement.json
				?projectKey=${projectKey}
				&elementId=${elementId}
				&location=${location}`
		);
	};

	ConsistencyAPI.prototype.doesElementNeedApproval = function(projectKey, elementId, elementLocation) {
		return generalApi.getJSONReturnPromise(
			`${this.restPrefix}/doesElementNeedApproval.json
				?projectKey=${projectKey}
				&elementId=${elementId}
				&elementLocation=${elementLocation}`
		);
	};

	ConsistencyAPI.prototype.approveCheck = function(projectKey, elementId, elementLocation, user) {
		return generalApi.postJSONReturnPromise(
			`${this.restPrefix}/approveCheck.json
				?projectKey=${projectKey}
				&elementId=${elementId}
				&elementLocation=${elementLocation}
				&user=${user}`
		);
	};

	ConsistencyAPI.prototype.approveInconsistencies = function() {
		consistencyAPI.approveCheck(this.projectKey, this.issueId, "i", JIRA.Users.LoggedInUser.userName());
		this.consistencyCheckFlag.close();
	}
	
	ConsistencyAPI.prototype.confirmIncompleteMessage = function() {
		this.consistencyCheckFlag.close();
	}

	global.consistencyAPI = new ConsistencyAPI();
}
)(window);

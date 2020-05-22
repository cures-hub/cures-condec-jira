(function (global) {

		const ConfigAPI = function ConfigAPI() {
			this.restPrefix = AJS.contextPath() + "/rest/condec/latest/config";

		};

		ConfigAPI.prototype.setMinimumDuplicateLength = function(projectKey, minDuplicateLength){
			return generalApi.postJSONReturnPromise(this.restPrefix + `/setMinimumDuplicateLength.json?
			projectKey=${projectKey}&minDuplicateLength=${minDuplicateLength}`, null);
		}

		ConfigAPI.prototype.setMinimumLinkSuggestionProbability = function(projectKey, minLinkSuggestionProbability){
			return generalApi.postJSONReturnPromise(this.restPrefix + `/setMinimumLinkSuggestionProbability.json?
			projectKey=${projectKey}&minLinkSuggestionProbability=${minLinkSuggestionProbability}`, null);
		}

		ConfigAPI.prototype.setActivationStatusOfConsistencyEvent = function(projectKey, eventKey, isActivated){
			return generalApi.postJSONReturnPromise(this.restPrefix + `/activateConsistencyEvent.json?
			projectKey=${projectKey}&eventKey=${eventKey}&isActivated=${isActivated}`, null);
		}

		ConfigAPI.prototype.getActivationStatusOfConsistencyEvent = function(projectKey, eventKey){
			return generalApi.getJSONReturnPromise(this.restPrefix + `/isConsistencyEventActivated?
			projectKey=${projectKey}&eventKey=${eventKey}`, null);
		}

		ConfigAPI.prototype.setActivationStatusOfAllConsistencyEvents = function(projectKey, isActivated){
			return generalApi.postJSONReturnPromise(this.restPrefix + "/setConsistencyActivated.json?projectKey="
				+ projectKey + "&isConsistencyActivated=" + isActivated, null);
		}


		// export ConsistencyAPI
		global.configAPI = new ConfigAPI();
	}
)(window);

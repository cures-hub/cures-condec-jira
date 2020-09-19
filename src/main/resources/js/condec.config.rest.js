(function (global) {

		const ConfigAPI = function ConfigAPI() {
			this.restPrefix = AJS.contextPath() + "/rest/condec/latest/config";

		};

		ConfigAPI.prototype.setMinimumDuplicateLength = function(projectKey, fragmentLength){
			return generalApi.postJSONReturnPromise(this.restPrefix + `/setMinimumDuplicateLength.json?
			projectKey=${projectKey}&fragmentLength=${fragmentLength}`, null);
		}

		ConfigAPI.prototype.setMinimumLinkSuggestionProbability = function(projectKey, minLinkSuggestionProbability){
			return generalApi.postJSONReturnPromise(this.restPrefix + `/setMinimumLinkSuggestionProbability.json?
			projectKey=${projectKey}&minLinkSuggestionProbability=${minLinkSuggestionProbability}`, null);
		}

		ConfigAPI.prototype.getConsistencyEventTriggerNames = function(){
			return generalApi.getJSONReturnPromise(this.restPrefix + `/getConsistencyEventTriggerNames`, null);
		}

		ConfigAPI.prototype.setActivationStatusOfAllConsistencyEvents = function(projectKey, isActivated){
			return generalApi.postJSONReturnPromise(this.restPrefix + "/setConsistencyActivated.json?projectKey="
				+ projectKey + "&isConsistencyActivated=" + isActivated, null);
		}

		ConfigAPI.prototype.getActivationStatusOfQualityEvent = function(projectKey, eventKey){
			return generalApi.getJSONReturnPromise(this.restPrefix + `/isQualityEventActivated?
				projectKey=${projectKey}&eventKey=${eventKey}`, null);
		}

		ConfigAPI.prototype.setActivationStatusOfQualityEvent = function(projectKey, eventKey, isActivated){
			return generalApi.postJSONReturnPromise(this.restPrefix + `/activateQualityEvent.json?
				projectKey=${projectKey}&eventKey=${eventKey}&isActivated=${isActivated}`, null);
		}

		global.configAPI = new ConfigAPI();
})(window);
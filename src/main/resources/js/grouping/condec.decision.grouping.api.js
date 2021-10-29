/**
 * This module implements the communication with the ConDec Java REST API for
 * mangement of decision groups and levels.
 *
 * Is required by: conDecDialog conDecFiltering conDecGroups
 *
 * Is referenced in HTML by none
 */
(function(global) {

	var projectKey = null;

	var ConDecGroupingAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/grouping";
		this.decisionGroups = [];
	};

	/**
	 * external references: condec.dialog, condec.decision.grouping.dialogs
	 */
	ConDecGroupingAPI.prototype.assignDecisionGroup = function(level, existingGroups, addgroup, elementId, documentationLocation, callback) {
		var element = {
			"id": elementId,
			"documentationLocation": documentationLocation,
			"projectKey": conDecAPI.projectKey
		};
		generalApi.postJSON(this.restPrefix + "/assignDecisionGroup.json?level=" + level
			+ "&existingGroups=" + existingGroups
			+ "&addGroup=" + addgroup, element, function(error) {
				if (error === null) {
					conDecAPI.showFlag("Success", "Decision groups/levels have been assigned.");
					callback(elementId);
				}
			});
	};

	/**
	 * external references: condec.filtering
	 */
	ConDecGroupingAPI.prototype.getAllDecisionGroups = function() {
		if (conDecAPI.projectKey === undefined || conDecAPI.projectKey === null || conDecAPI.projectKey.length === 0) {
			return this.decisionGroups;
		}
		if (this.decisionGroups === undefined || this.decisionGroups.length === 0) {
			this.decisionGroups = generalApi.getResponseAsReturnValue(conDecGroupingAPI.restPrefix
				+ "/getAllDecisionGroups.json?projectKey=" + conDecAPI.projectKey);
		}
		return this.decisionGroups;
	};

	/**
	 * external references: condec.dialog, condec.decision.grouping.dialogs
	 */
	ConDecGroupingAPI.prototype.getDecisionGroups = function(id, documentationLocation, callback) {
		var element = {
			"id": id,
			"documentationLocation": documentationLocation,
			"projectKey": conDecAPI.projectKey
		};
		generalApi.postJSON(this.restPrefix + "/getDecisionGroups.json", element,
			function(error, decisionGroups) {
				if (error === null) {
					callback(decisionGroups);
				}
			});
	};

	/**
	 * external references: condec.decision.grouping.dialogs
	 */
	ConDecGroupingAPI.prototype.renameDecisionGroup = function(oldName, newName, callback) {
		generalApi.getJSON(this.restPrefix
			+ "/renameDecisionGroup.json?projectKey=" + conDecAPI.projectKey
			+ "&oldName=" + oldName + "&newName=" + newName, function() {
				var index = conDecGroupingAPI.decisionGroups.indexOf(oldName);
				conDecGroupingAPI.decisionGroups[index] = newName;
				callback();
			});
	};

	/**
	 * external references: condec.decision.grouping.dialogs
	 */
	ConDecGroupingAPI.prototype.deleteDecisionGroup = function(groupName, callback) {
		generalApi.getJSON(this.restPrefix
			+ "/deleteDecisionGroup.json?projectKey=" + conDecAPI.projectKey
			+ "&groupName=" + groupName, function() {
				var index = conDecGroupingAPI.decisionGroups.indexOf(groupName);
				conDecGroupingAPI.decisionGroups.splice(index, 1);
				callback();
			});
	};

	global.conDecGroupingAPI = new ConDecGroupingAPI();
})(window);
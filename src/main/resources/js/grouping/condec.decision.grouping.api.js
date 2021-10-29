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
	ConDecGroupingAPI.prototype.assignDecisionGroup = function(level, existingGroups, addgroup, sourceId, documentationLocation, callback) {
		var newElement = {};
		generalApi.postJSON(this.restPrefix + "/assignDecisionGroup.json?sourceId="
			+ sourceId + "&documentationLocation="
			+ documentationLocation + "&projectKey="
			+ projectKey + "&level="
			+ level + "&existingGroups="
			+ existingGroups + "&addGroup="
			+ addgroup, newElement, function(error, newElement) {
				if (error === null) {
					conDecAPI.showFlag("Success", "Decision groups/levels have been assigned.");
					callback(sourceId);
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
	ConDecGroupingAPI.prototype.getDecisionGroups = function(id, location, callback) {
		// TODO Change to POST method and post knowledgeElement
		generalApi.getJSON(this.restPrefix + "/getDecisionGroups.json?elementId=" + id
			+ "&location=" + location + "&projectKey=" + projectKey, function(error, decisionGroups) {
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
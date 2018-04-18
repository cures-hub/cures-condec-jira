function getJSON(url, callback) {
	var xhr = new XMLHttpRequest();
	xhr.open("GET", url, true);
	xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
	xhr.responseType = "json";
	xhr.onload = function() {
		var status = xhr.status;
		if (status === 200) {
			callback(null, xhr.response);
		} else {
			callback(status);
		}
	};
	xhr.send();
}

function postJSON(url, data, callback) {
	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
	xhr.setRequestHeader("Accept", "application/json");
	xhr.responseType = "json";
	xhr.onload = function() {
		var status = xhr.status;
		if (status === 200) {
			callback(null, xhr.response);
		} else {
			callback(status);
		}
	};
	xhr.send(JSON.stringify(data));
}

function putJSON(url, data, callback) {
	var xhr = new XMLHttpRequest();
	xhr.open("PUT", url, true);
	xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
	xhr.setRequestHeader("Accept", "application/json");
	xhr.responseType = "json";
	xhr.onload = function() {
		var status = xhr.status;
		if (status === 200) {
			callback(null, xhr.response);
		} else {
			callback(status);
		}
	};
	xhr.send(JSON.stringify(data));
}

function deleteJSON(url, data, callback) {
	var xhr = new XMLHttpRequest();
	xhr.open("DELETE", url, true);
	xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
	xhr.setRequestHeader("Accept", "application/json");
	xhr.responseType = "json";
	xhr.onload = function() {
		var status = xhr.status;
		if (status == 200) {
			callback(null, xhr.response);
		} else {
			callback(status);
		}
	};
	xhr.send(JSON.stringify(data));
}

function getDecisionKnowledgeElement(id, projectKey, callback) {
	getJSON(
			AJS.contextPath() + "/rest/decisions/latest/decisions/getDecisionKnowledgeElement.json?projectKey="
					+ projectKey + '&id=' + id,
			function(error, decisionKnowledgeElement) {
				if (error == null) {
					callback(decisionKnowledgeElement);
				} else {
					showFlag("error",
							"An error occured when receiving the decision knowledge element for the given id and project key.");
				}
			});
}

function getUnlinkedDecisionComponents(id, projectKey, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getUnlinkedDecisionComponents.json?projectKey="
			+ projectKey + '&id=' + id, function(error, unlinkedDecisionComponents) {
		if (error == null) {
			callback(unlinkedDecisionComponents);
		} else {
			showFlag("error",
					"An error occured when receiving the unlinked decision components for the selected element.");
		}
	});
}

function createDecisionKnowledgeElement(summary, description, type, callback) {
	if (summary !== "") {
		var jsondata = {
			"projectKey" : getProjectKey(),
			"summary" : summary,
			"type" : type,
			"description" : description
		};
		postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/createDecisionKnowledgeElement.json", jsondata,
				function(error, decisionKnowledgeElement) {
					if (error == null) {
						showFlag("success", type + " has been created.");
						callback(decisionKnowledgeElement.id);
					} else {
						showFlag("error", type + " has not been created. Error Code: " + error);
					}
				});
	}
}

function editDecisionKnowledgeElement(id, summary, description, callback) {
	var jsondata = {
		"id" : id,
		"summary" : summary,
		"projectKey" : getProjectKey(),
		"description" : description
	};
	postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/updateDecisionKnowledgeElement.json", jsondata,
			function(error, decisionKnowledgeElement) {
				if (error == null) {
					showFlag("success", "Decision knowledge element has been updated.");
					callback(decisionKnowledgeElement);
				} else {
					showFlag("error", "Decision knowledge element was not updated. Error Code: " + error);
				}
			});
}

function deleteDecisionKnowledgeElement(id, callback) {
	var jsondata = {
		"id" : id,
		"projectKey" : getProjectKey()
	};
	deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/deleteDecisionKnowledgeElement.json", jsondata,
			function(error, decisionKnowledgeElement) {
				if (error == null) {
					showFlag("success", "Decision knowledge element has been deleted.");
					callback();
				} else {
					showFlag("error", "Decision knowledge element was not deleted. Error Code: " + error);
				}
			});
}

function createLink(parentId, childId, linkType, callback) {
	var jsondata = {
		"linkType" : linkType,
		"ingoingId" : childId,
		"outgoingId" : parentId
	};
	putJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/createLink.json?projectKey=" + getProjectKey(),
			jsondata, function(error, link) {
				if (error == null) {
					showFlag("success", "Link has been created.");
					callback(link);
				} else {
					showFlag("error", "Link could not be created.");
				}
			});
}

function deleteLink(parentId, childId, linkType, callback) {
	var jsondata = {
		"linkType" : linkType,
		"ingoingId" : childId,
		"outgoingId" : parentId
	};
	deleteJSON(AJS.contextPath() + "/rest/decisions/latest/deleteLink.json?projectKey=" + getProjectKey(), jsondata,
			function(error, link) {
				if (error == null) {
					showFlag("success", "Link has been deleted.");
					callback();
				} else {
					showFlag("error", "Link could not be deleted.");
				}
			});
}

function getProjectKey() {
	var pathname = window.location.pathname;
	var stringArray = pathname.split("/");
	return stringArray[stringArray.length - 1];
}

function getTreantUrl(projectKey, key, depthOfTree){
    return AJS.contextPath() + "/rest/treantsrest/latest/treant.json?projectKey="
    + projectKey + "&elementKey=" + key + "&depthOfTree=" + depthOfTree;
}

function getTreeView(projectKey){
    return AJS.contextPath() + "/rest/treeviewerrest/latest/treeviewer.json?projectKey="
    + projectKey;
}

function showFlag(type, message) {
	AJS.flag({
		type : type,
		close : "auto",
		title : type.charAt(0).toUpperCase() + type.slice(1),
		body : message
	});
}
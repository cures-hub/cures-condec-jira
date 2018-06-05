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

function getDecisionKnowledgeElement(id, callback) {
	getJSON(
			AJS.contextPath() + "/rest/decisions/latest/decisions/getDecisionKnowledgeElement.json?projectKey="
					+ getProjectKey() + '&id=' + id,
			function(error, decisionKnowledgeElement) {
				if (error == null) {
					callback(decisionKnowledgeElement);
				} else {
					showFlag("error",
							"An error occured when receiving the decision knowledge element for the given id and project key.");
				}
			});
}

function getLinkedDecisionComponents(id, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getLinkedDecisionComponents.json?projectKey="
			+ getProjectKey() + '&id=' + id, function(error, linkedDecisionComponents) {
		if (error == null) {
			callback(linkedDecisionComponents);
		} else {
			showFlag("error",
					"An error occured when receiving the linked decision components for the selected element.");
		}
	});
}

function getUnlinkedDecisionComponents(id, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getUnlinkedDecisionComponents.json?projectKey="
			+ getProjectKey() + '&id=' + id, function(error, unlinkedDecisionComponents) {
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

function editDecisionKnowledgeElement(id, summary, description, type, callback) {
	var jsondata = {
		"id" : id,
		"summary" : summary,
		"type" : type,
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
	deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/deleteLink.json?projectKey=" + getProjectKey(), jsondata,
		function(error, link) {
            if (error == null) {
                showFlag("success", "Link has been deleted.");
                callback();
            } else {
                showFlag("error", "Link could not be deleted.");
            }

		});
}

function getTreant(key, depthOfTree, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getTreant.json?projectKey=" + getProjectKey() + "&elementKey="
			+ key + "&depthOfTree=" + depthOfTree, function(error, treant) {
		if (error == null) {
			callback(treant);
		} else {
			showFlag("error", "Treant data could not be received. Error-Code: " + error);
		}
	});
}

function getTreeViewer(callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getTreeViewer.json?projectKey=" + getProjectKey(), function(
			error, core) {
		if (error == null) {
			callback(core)
		} else {
			showFlag("error", "Tree viewer data could not be received. Error-Code: " + error);
		}
	});
}

function setActivated(isActivated, projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setActivated.json?projectKey=" + projectKey + "&isActivated=" + isActivated,
		function(error, response) {
			if (error == null) {
				showFlag("success", "Plug-in activation for project has been changed.");
			} else {
				showFlag("error", "Plug-in activation for project has not been changed.");
			}
	});
}

function setIssueStrategy(isIssueStrategy, projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setIssueStrategy.json?projectKey=" + projectKey + "&isIssueStrategy="
			+ isIssueStrategy, function(error, response) {
		if (error == null) {
			showFlag("success", "Strategy has been selected.");
		} else {
			showFlag("error", "Strategy could not be selected.");
		}
	});
}

function isIssueStrategy(projectKey,callback) {
    getJSON(AJS.contextPath() + "/rest/decisions/latest/config/isIssueStrategy.json?projectKey=" + getProjectKey(), function(
        error, isIssueBoolean) {
        if (error == null) {
            callback(isIssueBoolean)
        } else {
            showFlag("error", "Strategy for the Project could not be received. Error-Code: " + error);
        }
    });
}

function showFlag(type, message) {
	AJS.flag({
		type : type,
		close : "auto",
		title : type.charAt(0).toUpperCase() + type.slice(1),
		body : message
	});
}
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

function getResponseAsReturnValue(url) {
	var xhr = new XMLHttpRequest();
	xhr.open("GET", url, false);
	xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
	xhr.send();
	return JSON.parse(xhr.response);
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
		if (status === 200) {
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
					+ getProjectKey() + "&id=" + id,
			function(error, decisionKnowledgeElement) {
				if (error === null) {
					callback(decisionKnowledgeElement);
				} else {
					showFlag("error",
							"An error occured when receiving the decision knowledge element for the given id and project key.");
				}
			});
}

function getLinkedDecisionComponents(id, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getLinkedDecisionComponents.json?projectKey="
			+ getProjectKey() + "&id=" + id, function(error, linkedDecisionComponents) {
		if (error === null) {
			callback(linkedDecisionComponents);
		} else {
			showFlag("error",
					"An error occured when receiving the linked decision components for the selected element.");
		}
	});
}

function getUnlinkedDecisionComponents(id, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getUnlinkedDecisionComponents.json?projectKey="
			+ getProjectKey() + "&id=" + id, function(error, unlinkedDecisionComponents) {
		if (error === null) {
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
					if (error === null) {
						showFlag("success", type + " has been created.");
						callback(decisionKnowledgeElement.id);
					} else {
						showFlag("error", type + " has not been created. Error Code: " + error);
					}
				});
	}
}

function updateDecisionKnowledgeElement(id, summary, description, type, callback) {
	var jsondata = {
		"id" : id,
		"summary" : summary,
		"type" : type,
		"projectKey" : getProjectKey(),
		"description" : description
	};
	postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/updateDecisionKnowledgeElement.json", jsondata,
			function(error, decisionKnowledgeElement) {
				if (error === null) {
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
				if (error === null) {
					showFlag("success", "Decision knowledge element has been deleted.");
					callback();
				} else {
					showFlag("error", "Decision knowledge element was not deleted. Error Code: " + error);
				}
			});
}

function linkElements(parentId, childId, linkType, callback) {
	var jsondata = {
		"linkType" : linkType,
		"idOfSourceElement" : childId,
		"idOfDestinationElement" : parentId
	};
	postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/createLink.json?projectKey=" + getProjectKey(),
			jsondata, function(error, link) {
				if (error === null) {
					showFlag("success", "Link has been created.");
					callback(link);
				} else {
					showFlag("error", "Link could not be created.");
				}
			});
}

function deleteLink(parentId, childId, callback) {
	var jsondata = {
		"idOfSourceElement" : childId,
		"idOfDestinationElement" : parentId
	};
	deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/deleteLink.json?projectKey=" + getProjectKey(),
			jsondata, function(error, link) {
				if (error === null) {
					showFlag("success", "Link has been deleted.");
					callback();
				} else {
					showFlag("error", "Link could not be deleted.");
				}

			});
}

function getTreant(elementKey, depthOfTree, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getTreant.json?&elementKey=" + elementKey
			+ "&depthOfTree=" + depthOfTree, function(error, treant) {
		if (error === null) {
			callback(treant);
		} else {
			showFlag("error", "Treant data could not be received. Error-Code: " + error);
		}
	});
}

function getTreeViewer(rootElementType, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getTreeViewer.json?projectKey=" + getProjectKey()
			+ "&rootElementType=" + rootElementType, function(error, core) {
		if (error === null) {
			callback(core);
		} else {
			showFlag("error", "Tree viewer data could not be received. Error-Code: " + error);
		}
	});
}

function setActivated(isActivated, projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setActivated.json?projectKey=" + projectKey
			+ "&isActivated=" + isActivated, function(error, response) {
		if (error === null) {
			showFlag("success", "Plug-in activation for the project has been changed.");
		} else {
			showFlag("error", "Plug-in activation for the project has not been changed.");
		}
	});
}

function setIssueStrategy(isIssueStrategy, projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setIssueStrategy.json?projectKey=" + projectKey
			+ "&isIssueStrategy=" + isIssueStrategy, function(error, response) {
		if (error === null) {
			showFlag("success", "Strategy has been selected.");
		} else {
			showFlag("error", "Strategy could not be selected.");
		}
	});
}

function isIssueStrategy(projectKey, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/config/isIssueStrategy.json?projectKey=" + getProjectKey(),
			function(error, isIssueBoolean) {
				if (error === null) {
					callback(isIssueBoolean);
				} else {
					showFlag("error", "Persistence strategy for the project could not be received. Error-Code: "
							+ error);
				}
			});
}

function setKnowledgeExtractedFromGit(isKnowledgeExtractedFromGit, projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setKnowledgeExtractedFromGit.json?projectKey="
			+ projectKey + "&isKnowledgeExtractedFromGit=" + isKnowledgeExtractedFromGit, function(error, response) {
		if (error === null) {
			showFlag("success", "Git connection for this project has been changed.");
		} else {
			showFlag("error", "Git connection for this project could not be configured.");
		}
	});
}

function isKnowledgeExtractedFromGit(projectKey, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/config/isKnowledgeExtractedFromGit.json?projectKey="
			+ projectKey, function(error, isKnowledgeExtractedFromGit) {
		if (error === null) {
			callback(isKnowledgeExtractedFromGit);
		} else {
			showFlag("error", "It could not be received whether decision knowledge is extracted from git. Error-Code: "
					+ error);
		}
	});
}

function setKnowledgeExtractedFromIssues(isKnowledgeExtractedFromIssues, projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setKnowledgeExtractedFromIssues.json?projectKey="
			+ projectKey + "&isKnowledgeExtractedFromIssues=" + isKnowledgeExtractedFromIssues, function(error,
			response) {
		if (error === null) {
			showFlag("success", "Extraction from issue comments for this project has been changed.");
		} else {
			showFlag("error", "Extraction from issue comments for this project could not be configured.");
		}
	});
}

function isKnowledgeExtractedFromIssues(projectKey, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/config/isKnowledgeExtractedFromIssues.json?projectKey="
			+ projectKey, function(error, isKnowledgeExtractedFromIssues) {
		if (error === null) {
			callback(isKnowledgeExtractedFromIssues);
		} else {
			showFlag("error",
					"It could not be received whether decision knowledge is extracted from issue comments. Error-Code: "
							+ error);
		}
	});
}

function setKnowledgeTypeEnabled(isKnowledgeTypeEnabled, knowledgeType, projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setKnowledgeTypeEnabled.json?projectKey=" + projectKey
			+ "&knowledgeType=" + knowledgeType + "&isKnowledgeTypeEnabled=" + isKnowledgeTypeEnabled, function(error,
			response) {
		if (error === null) {
			showFlag("success", "The activation of " + knowledgeType + " for this project has been changed.");
		} else {
			showFlag("error", "The activation of " + knowledgeType + " for this project could not be changed.");
		}
	});
}

function isKnowledgeTypeEnabled(knowledgeType, projectKey, toggle, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/config/isKnowledgeTypeEnabled.json?knowledgeType="
			+ knowledgeType + "&projectKey=" + projectKey, function(error, isKnowledgeTypeEnabled) {
		if (error === null) {
			callback(isKnowledgeTypeEnabled, toggle);
		} else {
			showFlag("error", "It could not be received whether the knowledge type is enabled. Error-Code: " + error);
		}
	});
}

function getKnowledgeTypes(projectKey) {
	var knowledgeTypes = getResponseAsReturnValue(AJS.contextPath()
			+ "/rest/decisions/latest/config/getKnowledgeTypes.json?projectKey=" + projectKey);
	if (knowledgeTypes !== null) {
		return knowledgeTypes;
	} else {
		showFlag("error", "The knowledge types could not be received. Error-Code: " + error);
	}
}

function getDefaultKnowledgeTypes(projectKey) {
	var defaultKnowledgeTypes = getResponseAsReturnValue(AJS.contextPath()
			+ "/rest/decisions/latest/config/getDefaultKnowledgeTypes.json?projectKey=" + projectKey);
	if (defaultKnowledgeTypes !== null) {
		return defaultKnowledgeTypes;
	} else {
		showFlag("error", "The default knowledge types could not be received. Error-Code: " + error);
	}
}

function getCommits(elementKey, callback) {
	getJSON(AJS.contextPath() + "/rest/gitplugin/latest/issues/" + elementKey + "/commits",
			function(error, commitData) {
				if (error === null) {
					callback(commitData.commits);
				} else {
					showFlag("error", "Commits for this element could not be received. Error-Code: " + error);
					callback();
				}
			});
}

function getCommitsAsReturnValue(elementKey) {
	var commitData = getResponseAsReturnValue(AJS.contextPath() + "/rest/gitplugin/latest/issues/" + elementKey
			+ "/commits");
	return commitData.commits;
}
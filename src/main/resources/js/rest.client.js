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

function postWithResponseAsReturnValue(url) {
	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, false);
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

function getLinkedElements(id, callback) {
	getJSON(
			AJS.contextPath() + "/rest/decisions/latest/decisions/getLinkedElements.json?projectKey=" + getProjectKey()
					+ "&id=" + id,
			function(error, linkedElements) {
				if (error === null) {
					callback(linkedElements);
				} else {
					showFlag("error",
							"An error occured when receiving the linked decision knowledge elements for the selected element.");
				}
			});
}

function getUnlinkedElements(id, callback) {
	getJSON(
			AJS.contextPath() + "/rest/decisions/latest/decisions/getUnlinkedElements.json?projectKey="
					+ getProjectKey() + "&id=" + id,
			function(error, unlinkedElements) {
				if (error === null) {
					callback(unlinkedElements);
				} else {
					showFlag("error",
							"An error occured when receiving the unlinked decision knowledge elements for the selected element.");
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
			function(error, isDeleted) {
				if (error === null) {
					showFlag("success", "Decision knowledge element has been deleted.");
					callback();
				} else {
					showFlag("error", "Decision knowledge element was not deleted. Error Code: " + error);
				}
			});
}

function linkElements(idOfDestinationElement, idOfSourceElement, linkType, callback) {
	var jsondata = {
		"type" : linkType,
		"idOfSourceElement" : idOfSourceElement,
		"idOfDestinationElement" : idOfDestinationElement
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

function deleteLink(idOfDestinationElement, idOfSourceElement, callback) {
	var jsondata = {
		"idOfSourceElement" : idOfSourceElement,
		"idOfDestinationElement" : idOfDestinationElement
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

function deleteSentenceLink(idOfDestinationElement, idOfSourceElement, callback) {
	var jsondata = {
		"idOfSourceElement" : idOfSourceElement,
		"idOfDestinationElement" : idOfDestinationElement
	};
	deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/deleteLinkBetweenSentences.json?projectKey="
			+ getProjectKey(), jsondata, function(error, link) {
		if (error === null) {
			showFlag("success", "Link has been deleted.");
			callback();
		} else {
			showFlag("error", "Link could not be deleted.");
		}

	});
}

function setSentenceIrrelevant(id, callback) {
	var jsondata = {
		"id" : id,
		"summary" : "",
		"type" : "",
		"projectKey" : "",
		"description" : ""
	};
	postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/setSentenceIrrelevant.json", jsondata, function(
			error) {
		if (error === null) {
			showFlag("success", "Decision knowledge element has been updated.");
			callback();
		} else {
			showFlag("error", "Decision knowledge element was not updated. Error Code: " + error);
		}
	});
}

function changeKnowledgeTypeOfSentence(id, type, callback) {
	var jsondata = {
		"id" : id,
		"type" : type
	};
	var argument = type;
	if (type.includes("Pro") || type.includes("Con")) {
		argument = type;
	}
	postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/changeKnowledgeTypeOfSentence.json?projectKey="
			+ getProjectKey() + "&argument=" + argument, jsondata, function(error, link) {
		if (error === null) {
			showFlag("success", "Knowledge type has been changed.");
			callback(link);
		} else {
			showFlag("error", "Knowledge type could not be changed.");
		}
	});
}

function editSentenceBody(id, body, type, callback) {
	var jsondata = {
		"id" : id,
		"summary" : "",
		"type" : type,
		"projectKey" : getProjectKey(),
		"description" : body
	};
	postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/editSentenceBody.json?argument=" + type, jsondata,
			function(error, id, type) {
				if (error === null) {
					showFlag("success", "Decision knowledge element has been updated.");
					callback(id, type);
				} else {
					showFlag("error", "Decision knowledge element was not updated. Error Code: " + error);
				}
			});
}

function deleteGenericLink(targetId, sourceId, targetType, sourceType, callback, showError) {
	var jsondata = {
		"idOfSourceElement" : sourceType + sourceId,
		"idOfDestinationElement" : targetType + targetId
	};
	deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/deleteGenericLink.json?projectKey="
			+ getProjectKey(), jsondata, function(error, link) {
		if (error === null) {
			showFlag("success", "Link has been deleted.");
			callback();
		} else if (showError) {
			showFlag("error", "Link could not be deleted.");
		}

	});
}

function linkGenericElements(targetId, sourceId, targetType, sourceType, callback) {
	var jsondata = {
		"type" : "contain",
		"idOfSourceElement" : sourceType + sourceId,
		"idOfDestinationElement" : targetType + targetId
	};
	postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/createGenericLink.json?projectKey="
			+ getProjectKey(), jsondata, function(error, link) {
		if (error === null) {
			showFlag("success", "Link has been created.");
			callback(link);
		} else {
			showFlag("error", "Link could not be created.");
		}
	});
}
//
// function getTreant(elementKey, depthOfTree, callback) {
// 	getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getTreant.json?&elementKey=" + elementKey
// 			+ "&depthOfTree=" + depthOfTree, function(error, treant) {
// 		if (error === null) {
// 			callback(treant);
// 		} else {
// 			showFlag("error", "Treant data could not be received. Error-Code: " + error);
// 		}
// 	});
// }

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

function getTreant(elementKey, depthOfTree, searchTerm, callback) {
    getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getTreant.json?&elementKey=" + elementKey
        + "&depthOfTree=" + depthOfTree + "&searchTerm=" + searchTerm, function(error, treant) {
        if (error === null) {
            callback(treant);
        } else {
            showFlag("error", "Filtered Treant data could not be received. Error-Code: " + error);
        }
    });
}

function getTreeViewerWithoutRootElement(showRelevant, callback) {
	var issueId = AJS.$("meta[name='ajs-issue-key']").attr("content");
	if (issueId === undefined) {
		issueId = getIssueKey();
	}
	getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getTreeViewer2.json?issueKey=" + issueId
			+ "&showRelevant=" + showRelevant, function(error, core) {
		if (error === null) {
			callback(core);
		} else {
			showFlag("error", "Tree viewer data could not be received. Error-Code: " + error);
		}
	});
}

function setActivated(isActivated, projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setActivated.json?projectKey=" + projectKey
			+ "&isActivated=" + isActivated, null, function(error, response) {
		if (error === null) {
			showFlag("success", "Plug-in activation for the project has been set to " + isActivated + ".");
		} else {
			showFlag("error", "Plug-in activation for the project has not been changed.");
		}
	});
}

function setIssueStrategy(isIssueStrategy, projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setIssueStrategy.json?projectKey=" + projectKey
			+ "&isIssueStrategy=" + isIssueStrategy, null, function(error, response) {
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
			+ projectKey + "&isKnowledgeExtractedFromGit=" + isKnowledgeExtractedFromGit, null,
			function(error, response) {
				if (error === null) {
					showFlag("success", "Git connection for this project has been set to "
							+ isKnowledgeExtractedFromGit + ".");
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
			+ projectKey + "&isKnowledgeExtractedFromIssues=" + isKnowledgeExtractedFromIssues, null, function(error,
			response) {
		if (error === null) {
			showFlag("success", "Extraction from issue comments for this project has been set to "
					+ isKnowledgeExtractedFromIssues + ".");
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
			+ "&knowledgeType=" + knowledgeType + "&isKnowledgeTypeEnabled=" + isKnowledgeTypeEnabled, null, function(
			error, response) {
		if (error === null) {
			showFlag("success", "The activation of the " + knowledgeType
					+ " knowledge type for this project has been set to " + isKnowledgeTypeEnabled + ".");
		} else {
			showFlag("error", "The activation of the " + knowledgeType
					+ " knowledge type for this project could not be changed.");
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

function setGitAddress(projectKey, gitAddress) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setGitAddress.json?projectKey=" + projectKey
			+ "&gitAddress=" + gitAddress, null, function(error, response) {
		if (error === null) {
			showFlag("success", "The git address  " + gitAddress + " for this project has been set.");
		} else {
			showFlag("error", "The git address  " + gitAddress + " for this project could not be set.");
		}
	});
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

function setWebhookData(projectKey, webhookUrl, webhookSecret) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setWebhookData.json?projectKey=" + projectKey
			+ "&webhookUrl=" + webhookUrl + "&webhookSecret=" + webhookSecret, null, function(error, response) {
		if (error === null) {
			showFlag("success", "The webhook for this project has been set.");
		} else {
			showFlag("error", "The webhook for this project has not been set.");
		}
	});
}

function setWebhookEnabled(isActivated, projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setWebhookEnabled.json?projectKey=" + projectKey
			+ "&isActivated=" + isActivated, null, function(error, response) {
		if (error === null) {
			showFlag("success", "The webhook activation for this project has been changed.");
		} else {
			showFlag("error", "The webhook activation for this project could not be changed.");
		}
	});
}

function getProjectIssueTypes(projectKey, callback) {
	getJSON(AJS.contextPath() + "/rest/decisions/latest/config/getProjectIssueTypes.json?projectKey=" + projectKey,
			function(error, issueTypes) {
				if (error === null) {
					callback(issueTypes);
				} else {
					showFlag("error", "Issue types of project could not be received. Error-Code: " + error);
				}
			});
}

function setWebhookType(webhookType, projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setWebhookType.json?projectKey=" + projectKey
			+ "&webhookType=" + webhookType, null, function(error, response) {
		if (error === null) {
			showFlag("success", "The webhook root element type was changed for this project.");
		} else {
			showFlag("error", "The webhook root element type could not been changed for this project.");
		}
	});
}

function getCommitsAsReturnValue(elementKey) {
	var commitData = getResponseAsReturnValue(AJS.contextPath() + "/rest/gitplugin/latest/issues/" + elementKey
			+ "/commits");
	return commitData.commits;
}

function clearSentenceDatabase(projectKey) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/clearSentenceDatabase.json?projectKey=" + projectKey,
			null, function(error, response) {
				if (error === null) {
					showFlag("success", "The Sentence database has been cleared.");
				} else {
					showFlag("error", "The Sentence database has not been cleared.");
				}
			});
}

function classifyWholeProject(projectKey) {
	var isSucceeded = postWithResponseAsReturnValue(AJS.contextPath()
			+ "/rest/decisions/latest/config/classifyWholeProject.json?projectKey=" + projectKey);
	if (isSucceeded) {
		showFlag("success", "The whole project has been classified.");
		return 1.0;
	}
	showFlag("error", "The classification process failed.");
	return 0.0;
}

function setIconParsing(projectKey, isActivated) {
	postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setIconParsing.json?projectKey=" + projectKey
			+ "&isActivatedString=" + isActivated, null, function(error, response) {
		if (error === null) {
			showFlag("success", "Plug-in activation for the project has been set to " + isActivated + ".");
		} else {
			showFlag("error", "Plug-in activation for the project has not been changed.");
		}
	});
}

function isIconParsing(projectKey, callback) {
    getJSON(
        AJS.contextPath() + "/rest/decisions/latest/config/isIconParsing.json?projectKey=" + getProjectKey(),
        function (error, isIconParsingBoolean) {
            if (error === null) {
                callback(isIconParsingBoolean);
            } else {
                showFlag("error", "Icon boolean value for the project could not be received. Error-Code: " + error);
            }
        });
}
function getElementsByQuery(sQuery, callback) {
    getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getQuery.json?&projectKey=" + getProjectKey()
        +"&URISearch="+sQuery, function (error, json) {
    	console.log("getElement",sQuery)
    	console.log("getElement error",error)
    	console.log("getElement json",json)
        if (error === null) {
            callback(json);
        } else {
            showFlag("error", "Filtered Treant data could not be received. Error-Code: " + error);
        }
    });
}
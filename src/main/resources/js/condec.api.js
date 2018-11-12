/*
 This module implements communication with the ConDec Java REST API and the JIRA API
 
 As known on 11. November 2018 this module:
 
 Requires
 * nothing
    
 Is required by
 * view.*  
  
 Is referenced in HTML by
 * settingsForAllProjects.vm and settingsForSingleProject.vm
 */
(function(global) {
	var ConDecAPI = function ConDecAPI() {
	    var projectKey  = getProjectKey();
        this.knowledgeTypes = ConDecAPI.prototype.getKnowledgeTypes(projectKey);
        this.extendedKnowledgeTypes = ConDecAPI.prototype.getExtendedKnowledgeTypes();

	};

	/*
	 * external references: view.context.menu, view.decision.knowledge.page,
	 * view.issue.module ..
	 */
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

	ConDecAPI.prototype.getDecisionKnowledgeElement = getDecisionKnowledgeElement;

	/*
	 * external references: view.issue.module ..
	 */
	ConDecAPI.prototype.getLinkedElements = function getLinkedElements(id, callback) {
		getJSON(
				AJS.contextPath() + "/rest/decisions/latest/decisions/getLinkedElements.json?projectKey="
						+ getProjectKey() + "&id=" + id,
				function(error, linkedElements) {
					if (error === null) {
						callback(linkedElements);
					} else {
						showFlag("error",
								"An error occured when receiving the linked decision knowledge elements for the selected element.");
					}
				});
	};

	/*
	 * external references: view.context.menu ..
	 */
	ConDecAPI.prototype.getUnlinkedElements = function getUnlinkedElements(id, callback) {
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
	};

	/*
	 * external references: view.issue.module, view.decision.knowledge.page ..
	 */
	function createDecisionKnowledgeElement(summary, description, type, callback) {
		if (summary !== "") {
			var jsondata = {
				"projectKey" : getProjectKey(),
				"summary" : summary,
				"type" : type,
				"description" : description
			};
			postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/createDecisionKnowledgeElement.json",
					jsondata, function(error, decisionKnowledgeElement) {
						if (error === null) {
							showFlag("success", type + " has been created.");
							callback(decisionKnowledgeElement.id);
						} else {
							showFlag("error", type + " has not been created. Error Code: " + error);
						}
					});
		}
	}

	ConDecAPI.prototype.createDecisionKnowledgeElement = createDecisionKnowledgeElement;

	/*
	 * external references: none
	 */
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

	ConDecAPI.prototype.updateDecisionKnowledgeElement = updateDecisionKnowledgeElement;

	/*
	 * external references: view.context.menu ..
	 */
	ConDecAPI.prototype.deleteDecisionKnowledgeElement = function deleteDecisionKnowledgeElement(id, callback) {
		var jsondata = {
			"id" : id,
			"projectKey" : getProjectKey()
		};
		deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/deleteDecisionKnowledgeElement.json",
				jsondata, function(error, isDeleted) {
					if (error === null) {
						showFlag("success", "Decision knowledge element has been deleted.");
						callback();
					} else {
						showFlag("error", "Decision knowledge element was not deleted. Error Code: " + error);
					}
				});
	};

	/*
	 * external references: none
	 */
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

	ConDecAPI.prototype.linkElements = linkElements;

	/*
	 * external references: view.context.menu, view.decision.knowledge.page,
	 * view.issue.module, view.treant, view.tree.viewer ..
	 */
	function deleteLink(idOfDestinationElement, idOfSourceElement, callback) {
		var jsondata = {
			"idOfSourceElement" : idOfSourceElement,
			"idOfDestinationElement" : idOfDestinationElement
		};
		deleteJSON(
				AJS.contextPath() + "/rest/decisions/latest/decisions/deleteLink.json?projectKey=" + getProjectKey(),
				jsondata, function(error, link) {
					if (error === null) {
						showFlag("success", "Link has been deleted.");
						callback();
					} else {
						showFlag("error", "Link could not be deleted.");
					}
				});
	}

	ConDecAPI.prototype.deleteLink = deleteLink;

	ConDecAPI.prototype.createLinkToExistingElement = function createLinkToExistingElement(idOfExistingElement,
			idOfNewElement, knowledgeTypeOfChild) {
		switchLinkTypes(knowledgeTypeOfChild, idOfExistingElement, idOfNewElement, function(linkType,
				idOfExistingElement, idOfNewElement) {
			linkElements(idOfExistingElement, idOfNewElement, linkType, function() {
				conDecObservable.notify();
			});
		});
	};

	function switchLinkTypes(type, idOfExistingElement, idOfNewElement, linkTypeFunction) {
		console.log("conDecAPI switchLinkTypes");
		switch (type) {
		case "Pro-argument":
			linkTypeFunction("support", idOfExistingElement, idOfNewElement);
			break;
		case "Con-argument":
			linkTypeFunction("attack", idOfExistingElement, idOfNewElement);
			break;
		default:
			linkTypeFunction("contain", idOfNewElement, idOfExistingElement);
		}
	}

	ConDecAPI.prototype.updateDecisionKnowledgeElementAsChild = function updateDecisionKnowledgeElementAsChild(childId,
			summary, description, type) {
		var simpleType = getSimpleType(type);
		getDecisionKnowledgeElement(childId, function(decisionKnowledgeElement) {
			updateDecisionKnowledgeElement(childId, summary, description, simpleType, function() {
				if (decisionKnowledgeElement.type !== type) {
					var parentId = findParentId(childId);
					switchLinkTypes(type, parentId, childId, function(linkType, parentId, childId) {
						deleteLink(parentId, childId, function() {
							linkElements(parentId, childId, linkType, function() {
								conDecObservable.notify();
							});
						});
					});
				} else {
					conDecObservable.notify();
				}
			});
		});
	};

	function getSimpleType(type) {
		var simpleType = type;
		if (type === "Pro-argument" || type === "Con-argument") {
			simpleType = "Argument";
		}
		return simpleType;
	}

	ConDecAPI.prototype.createDecisionKnowledgeElementAsChild = function createDecisionKnowledgeElementAsChild(summary,
			description, type, idOfExistingElement) {
		console.log("conDecAPI createDecisionKnowledgeElementAsChild");
		var simpleType = getSimpleType(type);
		createDecisionKnowledgeElement(summary, description, simpleType, function(idOfNewElement) {
			switchLinkTypes(type, idOfExistingElement, idOfNewElement, function(linkType, idOfExistingElement,
					idOfNewElement) {
				linkElements(idOfExistingElement, idOfNewElement, linkType, function() {
					conDecObservable.notify();
				});
			});
		});
	};

	/*
	 * external references: none, not even used locally! //TODO: delete
	 * function?
	 */
	ConDecAPI.prototype.deleteSentenceLink = function deleteSentenceLink(idOfDestinationElement, idOfSourceElement,
			callback) {
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
	};

	/*
	 * external references: view.context.menu
	 */
	ConDecAPI.prototype.setSentenceIrrelevant = function setSentenceIrrelevant(id, callback) {
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
	};

	/*
	 * external references: view.context.menu
	 */
	ConDecAPI.prototype.changeKnowledgeTypeOfSentence = function changeKnowledgeTypeOfSentence(id, type, callback) {
		var jsondata = {
			"id" : id,
			"type" : type
		};
		var argument = "";// Important to be empty!
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
	};

	/*
	 * external references: view.context.menu ..
	 */
	ConDecAPI.prototype.editSentenceBody = function editSentenceBody(id, body, type, callback) {
		var jsondata = {
			"id" : id,
			"summary" : "",
			"type" : type,
			"projectKey" : getProjectKey(),
			"description" : body
		};
		postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/editSentenceBody.json?argument=" + type,
				jsondata, function(error, id, type) {
					if (error === null) {
						showFlag("success", "Decision knowledge element has been updated.");
						callback(id, type);
					} else {
						showFlag("error", "Decision knowledge element was not updated. Error Code: " + error);
					}
				});
	};

	/*
	 * external references: view.context.menu ..
	 */
	ConDecAPI.prototype.createIssueFromSentence = function createIssueFromSentence(id, callback) {
		var jsondata = {
			"id" : id,
			"summary" : "",
			"type" : "",
			"projectKey" : "",
			"description" : ""
		};
		postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/createIssueFromSentence.json", jsondata,
				function(error, id, type) {
					if (error === null) {
						showFlag("success", "JIRA Issue has been created");
						callback(id, type);
					} else {
						showFlag("error", "JIRA Issue has not been created. Error Code: " + error);
					}
				});
	};

	/*
	 * external references: view.context.menu, view.treant, view.tree.viewer ..
	 */
	ConDecAPI.prototype.deleteGenericLink = function deleteGenericLink(targetId, sourceId, targetType, sourceType,
			callback, showError) {
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
	};

	/*
	 * external references: view.treant, view.tree.viewer ..
	 */
	ConDecAPI.prototype.linkGenericElements = function linkGenericElements(targetId, sourceId, targetType, sourceType,
			callback) {
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
	};

	/*
	 * external references: view.tab.panel, view.tree.viewer ..
	 */
	ConDecAPI.prototype.getTreeViewer = function getTreeViewer(rootElementType, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getTreeViewer.json?projectKey=" + getProjectKey()
				+ "&rootElementType=" + rootElementType, function(error, core) {
			if (error === null) {
				callback(core);
			} else {
				showFlag("error", "Tree viewer data could not be received. Error-Code: " + error);
			}
		});
	};

	/*
	 * external references: view.treant ..
	 */
	ConDecAPI.prototype.getTreant = function getTreant(elementKey, depthOfTree, searchTerm, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getTreant.json?&elementKey=" + elementKey
				+ "&depthOfTree=" + depthOfTree + "&searchTerm=" + searchTerm, function(error, treant) {
			if (error === null) {
				callback(treant);
			} else {
				showFlag("error", "Filtered Treant data could not be received. Error-Code: " + error);
			}
		});
	};

	/*
	 * external references: view.tab.panel ..
	 */
	ConDecAPI.prototype.getTreeViewerWithoutRootElement = function getTreeViewerWithoutRootElement(showRelevant,
			callback) {
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
	};

	/*
	 * external references: settingsForSingleProject.vm,
	 * settingsForAllProjects.vm ..
	 */
	ConDecAPI.prototype.setActivated = function setActivated(isActivated, projectKey) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setActivated.json?projectKey=" + projectKey
				+ "&isActivated=" + isActivated, null, function(error, response) {
			if (error === null) {
				showFlag("success", "Plug-in activation for the project has been set to " + isActivated + ".");
			} else {
				showFlag("error", "Plug-in activation for the project has not been changed.");
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm,
	 * settingsForAllProjects.vm ..
	 */
	ConDecAPI.prototype.setIssueStrategy = function setIssueStrategy(isIssueStrategy, projectKey) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setIssueStrategy.json?projectKey=" + projectKey
				+ "&isIssueStrategy=" + isIssueStrategy, null, function(error, response) {
			if (error === null) {
				showFlag("success", "Strategy has been selected.");
			} else {
				showFlag("error", "Strategy could not be selected.");
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm,
	 * settingsForAllProjects.vm, view.context.menu ..
	 */
	ConDecAPI.prototype.isIssueStrategy = function isIssueStrategy(projectKey, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/config/isIssueStrategy.json?projectKey=" + getProjectKey(),
				function(error, isIssueStrategyBoolean) {
					if (error === null) {
						callback(isIssueStrategyBoolean);
					} else {
						showFlag("error", "Persistence strategy for the project could not be received. Error-Code: "
								+ error);
					}
				});
	};

	/*
	 * external references: settingsForSingleProject.vm,
	 * settingsForAllProjects.vm ..
	 */
	ConDecAPI.prototype.setKnowledgeExtractedFromGit = function setKnowledgeExtractedFromGit(
			isKnowledgeExtractedFromGit, projectKey) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setKnowledgeExtractedFromGit.json?projectKey="
				+ projectKey + "&isKnowledgeExtractedFromGit=" + isKnowledgeExtractedFromGit, null, function(error,
				response) {
			if (error === null) {
				showFlag("success", "Git connection for this project has been set to " + isKnowledgeExtractedFromGit
						+ ".");
			} else {
				showFlag("error", "Git connection for this project could not be configured.");
			}
		});
	};

	/*
	 * external references: view.treant ..
	 */
	ConDecAPI.prototype.isKnowledgeExtractedFromGit = function isKnowledgeExtractedFromGit(projectKey, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/config/isKnowledgeExtractedFromGit.json?projectKey="
				+ projectKey, function(error, isKnowledgeExtractedFromGit) {
			if (error === null) {
				callback(isKnowledgeExtractedFromGit);
			} else {
				showFlag("error",
						"It could not be received whether decision knowledge is extracted from git. Error-Code: "
								+ error);
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm,
	 * settingsForAllProjects.vm ..
	 */
	ConDecAPI.prototype.setKnowledgeExtractedFromIssues = function setKnowledgeExtractedFromIssues(
			isKnowledgeExtractedFromIssues, projectKey) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setKnowledgeExtractedFromIssues.json?projectKey="
				+ projectKey + "&isKnowledgeExtractedFromIssues=" + isKnowledgeExtractedFromIssues, null, function(
				error, response) {
			if (error === null) {
				showFlag("success", "Extraction from issue comments for this project has been set to "
						+ isKnowledgeExtractedFromIssues + ".");
			} else {
				showFlag("error", "Extraction from issue comments for this project could not be configured.");
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm ..
	 */
	ConDecAPI.prototype.setUseClassiferForIssueComments = function setUseClassiferForIssueComments(
			isClassifierUsedForIssues, projectKey) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setUseClassiferForIssueComments.json?projectKey="
				+ projectKey + "&isClassifierUsedForIssues=" + isClassifierUsedForIssues, null, function(error,
				response) {
			if (error === null) {
				showFlag("success", "Usage of classification for Decision Knowledge in Issue Comments has been set to "
						+ isClassifierUsedForIssues + ".");
			} else {
				showFlag("error",
						"Usage of classification for Decision Knowledge in Issue Comments could not be configured.");
			}
		});
	};

	/*
	 * external references: none, not even used locally, parameter naming ..
	 */
	ConDecAPI.prototype.isKnowledgeExtractedFromIssues = function isKnowledgeExtractedFromIssues(projectKey, callback) {
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
	};

	/*
	 * external references: settingsForSingleProject.vm ..
	 */
	ConDecAPI.prototype.setKnowledgeTypeEnabled = function setKnowledgeTypeEnabled(isKnowledgeTypeEnabled,
			knowledgeType, projectKey) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setKnowledgeTypeEnabled.json?projectKey="
				+ projectKey + "&knowledgeType=" + knowledgeType + "&isKnowledgeTypeEnabled=" + isKnowledgeTypeEnabled,
				null, function(error, response) {
					if (error === null) {
						showFlag("success", "The activation of the " + knowledgeType
								+ " knowledge type for this project has been set to " + isKnowledgeTypeEnabled + ".");
					} else {
						showFlag("error", "The activation of the " + knowledgeType
								+ " knowledge type for this project could not be changed.");
					}
				});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.isKnowledgeTypeEnabled = function isKnowledgeTypeEnabled(knowledgeType, projectKey, toggle,
			callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/config/isKnowledgeTypeEnabled.json?knowledgeType="
				+ knowledgeType + "&projectKey=" + projectKey, function(error, isKnowledgeTypeEnabled) {
			if (error === null) {
				callback(isKnowledgeTypeEnabled, toggle);
			} else {
				showFlag("error", "It could not be received whether the knowledge type is enabled. Error-Code: "
						+ error);
			}
		});
	};

	/*
	 * external references: context.menu, Default knowledge types are
	 * "Alternative", "Argument", "Decision", and "Issue".
	 */
	ConDecAPI.prototype.getDefaultKnowledgeTypes = function getDefaultKnowledgeTypes(projectKey) {
		var defaultKnowledgeTypes = getResponseAsReturnValue(AJS.contextPath()
				+ "/rest/decisions/latest/config/getDefaultKnowledgeTypes.json?projectKey=" + projectKey);
		if (defaultKnowledgeTypes !== null) {
			return defaultKnowledgeTypes;
		} else {
			showFlag("error", "The default knowledge types could not be received. Error-Code: " + error);
		}
	};

	/*
	 * external references: context.menu, Knowledge types are a subset of
	 * "Alternative", "Argument", "Assessment", "Assumption", "Claim",
	 * "Constraint", "Context", "Decision", "Goal", "Implication", "Issue",
	 * "Problem", and "Solution".
	 */
	function getKnowledgeTypes(projectKey) {
		var knowledgeTypes = getResponseAsReturnValue(AJS.contextPath()
				+ "/rest/decisions/latest/config/getKnowledgeTypes.json?projectKey=" + projectKey);
		if (knowledgeTypes !== null) {
			return knowledgeTypes;
		} else {
			showFlag("error", "The knowledge types could not be received. Error-Code: " + error);
		}
	}

	ConDecAPI.prototype.getKnowledgeTypes = getKnowledgeTypes;

	/*
	 * external references: context.menu, Replaces argument with pro-argument
	 * and con-argument in knowledge types array.
	 */
	ConDecAPI.prototype.getExtendedKnowledgeTypes = function getExtendedKnowledgeTypes() {
	    this.extendedKnowledgeTypes = getKnowledgeTypes(getProjectKey());
		this.extendedKnowledgeTypes = this.extendedKnowledgeTypes.filter(function(value) {
			return value.toLowerCase() !== "argument";
		});
		this.extendedKnowledgeTypes.push("Pro-argument");
		this.extendedKnowledgeTypes.push("Con-argument");
		return this.extendedKnowledgeTypes;
	};

	/*
	 * external references: none, not even used locally! //TODO: delete
	 * function?
	 */
	ConDecAPI.prototype.setGitAddress = function setGitAddress(projectKey, gitAddress) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setGitAddress.json?projectKey=" + projectKey
				+ "&gitAddress=" + gitAddress, null, function(error, response) {
			if (error === null) {
				showFlag("success", "The git address  " + gitAddress + " for this project has been set.");
			} else {
				showFlag("error", "The git address  " + gitAddress + " for this project could not be set.");
			}
		});
	};

	/*
	 * external references: view.treant ..
	 */
	ConDecAPI.prototype.getCommits = function getCommits(elementKey, callback) {
		getJSON(AJS.contextPath() + "/rest/gitplugin/latest/issues/" + elementKey + "/commits", function(error,
				commitData) {
			if (error === null) {
				callback(commitData.commits);
			} else {
				showFlag("error", "Commits for this element could not be received. Error-Code: " + error);
				callback();
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm ..
	 */
	ConDecAPI.prototype.setWebhookData = function setWebhookData(projectKey, webhookUrl, webhookSecret) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setWebhookData.json?projectKey=" + projectKey
				+ "&webhookUrl=" + webhookUrl + "&webhookSecret=" + webhookSecret, null, function(error, response) {
			if (error === null) {
				showFlag("success", "The webhook for this project has been set.");
			} else {
				showFlag("error", "The webhook for this project has not been set.");
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setWebhookEnabled = function setWebhookEnabled(isActivated, projectKey) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setWebhookEnabled.json?projectKey=" + projectKey
				+ "&isActivated=" + isActivated, null, function(error, response) {
			if (error === null) {
				showFlag("success", "The webhook activation for this project has been changed.");
			} else {
				showFlag("error", "The webhook activation for this project could not be changed.");
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm ..
	 */
	ConDecAPI.prototype.setWebhookType = function setWebhookType(webhookType, projectKey, isWebhookTypeEnabled) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setWebhookType.json?projectKey=" + projectKey
				+ "&webhookType=" + webhookType + "&isWebhookTypeEnabled=" + isWebhookTypeEnabled, null, function(
				error, response) {
			if (error === null) {
				showFlag("success", "The webhook root element type was changed for this project.");
			} else {
				showFlag("error", "The webhook root element type could not been changed for this project.");
			}
		});
	};

	/*
	 * external references: none, not even used locally! //TODO: delete
	 * function?
	 */
	ConDecAPI.prototype.getCommitsAsReturnValue = function getCommitsAsReturnValue(elementKey) {
		var commitData = getResponseAsReturnValue(AJS.contextPath() + "/rest/gitplugin/latest/issues/" + elementKey
				+ "/commits");
		return commitData.commits;
	};

	/*
	 * external references: settingsForSingleProject.vm ..
	 */
	ConDecAPI.prototype.clearSentenceDatabase = function clearSentenceDatabase(projectKey) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/clearSentenceDatabase.json?projectKey="
				+ projectKey, null, function(error, response) {
			if (error === null) {
				showFlag("success", "The Sentence database has been cleared.");
			} else {
				showFlag("error", "The Sentence database has not been cleared.");
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm ..
	 */
	ConDecAPI.prototype.classifyWholeProject = function classifyWholeProject(projectKey) {
		var isSucceeded = postWithResponseAsReturnValue(AJS.contextPath()
				+ "/rest/decisions/latest/config/classifyWholeProject.json?projectKey=" + projectKey);
		if (isSucceeded) {
			showFlag("success", "The whole project has been classified.");
			return 1.0;
		}
		showFlag("error", "The classification process failed.");
		return 0.0;
	};

	/*
	 * external references: settingsForSingleProject.vm ..
	 */
	ConDecAPI.prototype.setIconParsing = function setIconParsing(projectKey, isActivated) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setIconParsing.json?projectKey=" + projectKey
				+ "&isActivatedString=" + isActivated, null, function(error, response) {
			if (error === null) {
				showFlag("success", "Using icons to tag issue comments has been set to " + isActivated + ".");
			} else {
				showFlag("error", "It could not be received wether icons can be used to tag issue comments.");
			}
		});
	};

	/*
	 * external references: settingsForAllProjects.vm ..
	 */
	ConDecAPI.prototype.getRequestToken = function getRequestToken(projectKey, baseURL, privateKey, consumerKey,
			callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/config/getRequestToken.json?projectKey=" + projectKey
				+ "&baseURL=" + baseURL + "&privateKey=" + privateKey + "&consumerKey=" + consumerKey, function(error,
				result) {
			if (error === null) {
				callback(result);
			} else {
				showFlag("error", "Request token could not be received. Error-Code: " + error);
			}
		});
	};

	/*
	 * external references: settingsForAllProjects.vm ..
	 */
	ConDecAPI.prototype.getAccessToken = function getAccessToken(projectKey, baseURL, privateKey, consumerKey,
			requestToken, secret, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/config/getAccessToken.json?projectKey=" + projectKey
				+ "&baseURL=" + baseURL + "&privateKey=" + privateKey + "&consumerKey=" + consumerKey
				+ "&requestToken=" + requestToken + "&secret=" + secret, function(error, result) {
			if (error === null) {
				callback(result);
			} else {
				showFlag("error", "Access token could not be received. Error-Code: " + error);
			}
		});
	};

	/*
	 * external references: view.issue.module ..
	 */
	ConDecAPI.prototype.getElementsByQuery = function getElementsByQuery(query, callback) {
		var projectKey = getProjectKey() || "";
		postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getAllElementsMatchingQuery.json?projectKey="
				+ projectKey + "&query=" + query, null, function(error, result) {
			if (error === null) {
				callback(result);
			} else {
				showFlag("error", "Elements for given query could not be received." + error);
			}
		});
	};

	/*
	 * external references: view.issue.module ..
	 */
	ConDecAPI.prototype.getLinkedElementsByQuery = function getLinkedElementsByQuery(query, elementKey, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getAllElementsLinkedToElement.json?elementKey="
				+ elementKey + "&URISearch=" + query, function(error, result) {
			if (error === null) {
				callback(result);
			} else {
				showFlag("error", "Linked elements for given query could not be received." + error);
			}
		});
	};

	/*
	 * external references: none, only within this closure.
	 */
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

	/*
	 * external references: none, only within this closure.
	 */
	function getResponseAsReturnValue(url) {
		var xhr = new XMLHttpRequest();
		xhr.open("GET", url, false);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.send();
		return JSON.parse(xhr.response);
	}

	/*
	 * external references: none, only within this closure.
	 */
	function postWithResponseAsReturnValue(url) {
		var xhr = new XMLHttpRequest();
		xhr.open("POST", url, false);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.send();
		return JSON.parse(xhr.response);
	}

	/*
	 * external references: none, only within this closure.
	 */
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

	/*
	 * external references: none, only within this closure.
	 */
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

	/*
	 * external references: none, only within this closure.
	 */
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

	function getIssueKey() {
		console.log("conDecAPI getIssueKey");
		var issueKey = JIRA.Issue.getIssueKey();
		if (issueKey === null) {
			issueKey = AJS.Meta.get("issue-key");
		}
		return issueKey;
	}
	
	ConDecAPI.prototype.getIssueKey = getIssueKey;

	function getProjectKey() {
		var projectKey;
		try {
			projectKey = JIRA.API.Projects.getCurrentProjectKey();
		} catch (error) {
			console.log(error);
		}
		if (projectKey === undefined) {
			try {
				var issueKey = getIssueKey();
				projectKey = issueKey.split("-")[0];
			} catch (error) {
				console.log(error);
			}
		}
		return projectKey;
	}

	ConDecAPI.prototype.getProjectKey = getProjectKey;

	/*
	 * external references: view.context.menu.js
	 */
	ConDecAPI.prototype.getProjectId = function getProjectId() {
		var projectId;
		try {
			projectId = JIRA.API.Projects.getCurrentProjectId();
		} catch (error) {
			console.log(error);
		}
		return projectId;
	};

	/*
	 * external references: none, only within this closure.
	 */
	function showFlag(type, message) {
		AJS.flag({
			type : type,
			close : "auto",
			title : type.charAt(0).toUpperCase() + type.slice(1),
			body : message
		});
	}

	// export ConDecAPI
	global.conDecAPI = new ConDecAPI();
})(window);
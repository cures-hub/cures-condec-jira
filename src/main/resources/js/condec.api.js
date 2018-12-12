/*
 This module implements the communication with the ConDec Java REST API and the JIRA API.

 Requires
 * conDecTreant.findParentId
    
 Is required by
 * conDecContextMenu
 * conDecDialog
 * conDecIssueModule
 * conDecTreant
 * conDecTreeViewer
 * conDecKnowledgePage
 * conDecTabPanel
  
 Is referenced in HTML by
 * settingsForAllProjects.vm 
 * settingsForSingleProject.vm
 */
(function(global) {

	var projectKey = null;

	var ConDecAPI = function ConDecAPI() {
		projectKey = getProjectKey();
		this.knowledgeTypes = getKnowledgeTypes(projectKey);
		this.extendedKnowledgeTypes = getExtendedKnowledgeTypes(this.knowledgeTypes);
	};

	ConDecAPI.prototype.checkIfProjectKeyIsValid = function checkIfProjectKeyIsValid() {
		if (projectKey === null || projectKey === undefined) {
			/*
			 * Some dependencies were missing when the closure object was first
			 * instantiated. Instantiates the object again.
			 */
			global.conDecAPI = new ConDecAPI();
		}
	};

	/*
	 * external references: condec.context.menu, condec.dialog,
	 * view.condec.knowledge.page, condec.jira.issue.module
	 */
	ConDecAPI.prototype.getDecisionKnowledgeElement = function getDecisionKnowledgeElement(id, callback) {
		getJSON(
				AJS.contextPath() + "/rest/decisions/latest/decisions/getDecisionKnowledgeElement.json?projectKey="
						+ projectKey + "&id=" + id,
				function(error, decisionKnowledgeElement) {
					if (error === null) {
						callback(decisionKnowledgeElement);
					} else {
						showFlag("error",
								"An error occured when receiving the decision knowledge element for the given id and project key.");
					}
				});
	};

	/*
	 * external references: condec.jira.issue.module
	 */
	ConDecAPI.prototype.getLinkedElements = function getLinkedElements(id, callback) {
		getJSON(
				AJS.contextPath() + "/rest/decisions/latest/decisions/getLinkedElements.json?projectKey=" + projectKey
						+ "&id=" + id,
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
	 * external references: condec.dialog
	 */
	ConDecAPI.prototype.getUnlinkedElements = function getUnlinkedElements(id, callback) {
		getJSON(
				AJS.contextPath() + "/rest/decisions/latest/decisions/getUnlinkedElements.json?projectKey="
						+ projectKey + "&id=" + id,
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
	 * external references: view.condec.knowledge.page, condec.dialog
	 */
	ConDecAPI.prototype.createDecisionKnowledgeElement = function createDecisionKnowledgeElementAsChild(summary,
			description, type, documentationLocation, idOfExistingElement, documentationLocationOfExistingElement,
			callback) {
		console.log("conDecAPI createDecisionKnowledgeElement");
		var newElement = {
			"summary" : summary,
			"type" : type,
			"projectKey" : projectKey,
			"description" : description,
			"documentationLocation" : documentationLocation,
		};

		postJSON(AJS.contextPath()
				+ "/rest/decisions/latest/decisions/createDecisionKnowledgeElement.json?idOfExistingElement="
				+ idOfExistingElement + "&documentationLocationOfExistingElement="
				+ documentationLocationOfExistingElement, newElement, function(error, newElement) {
			if (error === null) {
				showFlag("success", type + " and link have been created.");
				callback(newElement.id);
			} else {
				showFlag("error", type + " and link have not been created. Error Code: " + error);
			}
		});
	};

	/*
	 * internal references: updateDecisionKnowledgeElementAsChild
	 */
	function updateDecisionKnowledgeElement(id, summary, description, type, documentationLocation, callback) {
		var element = {
			"id" : id,
			"summary" : summary,
			"type" : type,
			"projectKey" : projectKey,
			"description" : description,
			"documentationLocation" : documentationLocation
		};
		postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/updateDecisionKnowledgeElement.json", element,
				function(error, decisionKnowledgeElement) {
					if (error === null) {
						showFlag("success", "Decision knowledge element has been updated.");
						callback(decisionKnowledgeElement);
					} else {
						showFlag("error", "Decision knowledge element was not updated. Error Code: " + error);
					}
				});
	}

	/*
	 * external references: condec.dialog
	 */
	ConDecAPI.prototype.deleteDecisionKnowledgeElement = function deleteDecisionKnowledgeElement(id, callback) {
		var element = {
			"id" : id,
			"projectKey" : projectKey
		};
		deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/deleteDecisionKnowledgeElement.json", element,
				function(error, isDeleted) {
					if (error === null) {
						showFlag("success", "Decision knowledge element has been deleted.");
						callback();
					} else {
						showFlag("error", "Decision knowledge element was not deleted. Error Code: " + error);
					}
				});
	};

	/*
	 * external references: condec.treant, condec.tree.viewer
	 */
	ConDecAPI.prototype.linkElements = function linkElements(linkType, idOfDestinationElement, idOfSourceElement,
			documentationLocationOfDestinationElement, documentationLocationOfSourceElement, callback) {
		console.log("conDecAPI linkElements");
		var link = {
			"type" : linkType,
			"idOfSourceElement" : idOfSourceElement,
			"idOfDestinationElement" : idOfDestinationElement,
			"documentationLocationOfSourceElement" : documentationLocationOfSourceElement,
			"documentationLocationOfDestinationElement" : documentationLocationOfDestinationElement
		};
		postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/createLink.json?projectKey=" + projectKey, link,
				function(error, link) {
					if (error === null) {
						showFlag("success", "Link has been created.");
						callback(link);
					} else {
						showFlag("error", "Link could not be created.");
					}
				});
	};

	/*
	 * external references: condec.context.menu, condec.dialog, condec.treant,
	 * condec.tree.viewer
	 */
	ConDecAPI.prototype.deleteLink = function deleteLink(idOfDestinationElement, idOfSourceElement,
			documentationLocationOfDestinationElement, documentationLocationOfSourceElement, callback, showError) {
		var link = {
			"idOfSourceElement" : idOfSourceElement,
			"idOfDestinationElement" : idOfDestinationElement,
			"documentationLocationOfSourceElement" : documentationLocationOfSourceElement,
			"documentationLocationOfDestinationElement" : documentationLocationOfDestinationElement
		};
		deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/deleteLink.json?projectKey=" + projectKey,
				link, function(error, link) {
					if (error === null) {
						showFlag("success", "Link has been deleted.");
						callback();
					} else if (showError) {
						showFlag("error", "Link could not be deleted.");
					}
				});
	};

	/*
	 * external references: condec.dialog
	 */
	ConDecAPI.prototype.createLinkToExistingElement = function createLinkToExistingElement(idOfExistingElement,
			idOfNewElement, knowledgeTypeOfChild) {
		switchLinkTypes(knowledgeTypeOfChild, idOfExistingElement, idOfNewElement, "i", "i", (function(linkType,
				idOfExistingElement, idOfNewElement) {
			this.linkElements(linkType, idOfExistingElement, idOfNewElement, "i", "i", function() {
				conDecObservable.notify();
			});
		}).bind(this));
	};

	function switchLinkTypes(type, idOfExistingElement, idOfNewElement, documentationLocationOfExistingElement,
			documentationLocationOfNewElement, linkTypeFunction) {
		console.log("conDecAPI switchLinkTypes");
		switch (type) {
		case "Pro-argument":
			linkTypeFunction("support", idOfExistingElement, idOfNewElement, documentationLocationOfExistingElement,
					documentationLocationOfNewElement);
			break;
		case "Con-argument":
			linkTypeFunction("attack", idOfExistingElement, idOfNewElement, documentationLocationOfExistingElement,
					documentationLocationOfNewElement);
			break;
		default:
			linkTypeFunction("contain", idOfNewElement, idOfExistingElement, documentationLocationOfNewElement,
					documentationLocationOfExistingElement);
		}
	}

	/*
	 * external references: condec.dialog TODO: This is currently not working if
	 * a JIRA issue is child of a sentence element
	 */
	ConDecAPI.prototype.updateDecisionKnowledgeElementAsChild = function updateDecisionKnowledgeElementAsChild(childId,
			summary, description, type) {
		// var simpleType = getSimpleType(type);
		this.getDecisionKnowledgeElement(childId, (function(childElement) {
			updateDecisionKnowledgeElement(childId, summary, description, type, "i", (function() {
				if (childElement.type !== type) {
					var parentId = conDecTreant.findParentId(childId);
					// @issue What if parent is a sentence object? Get
					// documentation location of parent.
					switchLinkTypes(type, parentId, childId, "i", childElement.documentationLocation, (function(
							linkType, parentId, childId) {
						this.deleteLink(parentId, childId, "i", childElement.documentationLocation, (function() {
							this.linkElements(linkType, parentId, childId, "i", childElement.documentationLocation,
									function() {
										conDecObservable.notify();
									});
						}).bind(this));
					}).bind(this));
				} else {
					conDecObservable.notify();
				}
			}).bind(this));
		}).bind(this));
	};

	/*
	 * external references: condec.context.menu
	 */
	ConDecAPI.prototype.setSentenceIrrelevant = function setSentenceIrrelevant(id, callback) {
		var jsondata = {
			"id" : id,
			"documentationLocation" : "s"
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
	 * external references: condec.context.menu
	 */
	ConDecAPI.prototype.getSentenceElement = function getSentenceElement(id, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getSentenceElement.json?id=" + id, function(
				error, sentenceElement) {
			if (error === null) {
				callback(sentenceElement);
			} else {
				showFlag("error", "The Element data could not be fetched");
			}
		});
	};

	/*
	 * external references: condec.context.menu
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
				+ projectKey + "&argument=" + argument, jsondata, function(error, link) {
			if (error === null) {
				showFlag("success", "Knowledge type has been changed.");
				callback(link);
			} else {
				showFlag("error", "Knowledge type could not be changed.");
			}
		});
	};

	/*
	 * external references: condec.context.menu
	 */
	ConDecAPI.prototype.deleteSentenceObject2 = function deleteSentenceObject2(id, callback) {
		deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/deleteSentenceObject2.json?id=" + id, null,
				function(error) {
					if (error === null) {
						showFlag("success", "Knowledge element has been deleted.");
						callback();
					} else {
						showFlag("error", "Knowledge element could not be deleted.");
					}
				});
	};

	/*
	 * external references: condec.context.menu ..
	 */
	ConDecAPI.prototype.editSentenceBody = function editSentenceBody(id, body, type, callback) {
		var jsondata = {
			"id" : id,
			"summary" : "",
			"type" : type,
			"projectKey" : projectKey,
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
	 * external references: condec.context.menu
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
						callback();
					} else {
						showFlag("error", "JIRA Issue has not been created. Error Code: " + error);
					}
				});
	};

	/*
	 * external references: condec.tree.viewer
	 */
	ConDecAPI.prototype.getTreeViewer = function getTreeViewer(rootElementType, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getTreeViewer.json?projectKey=" + projectKey
				+ "&rootElementType=" + rootElementType, function(error, core) {
			if (error === null) {
				callback(core);
			} else {
				showFlag("error", "Tree viewer data could not be received. Error-Code: " + error);
			}
		});
	};

	/*
	 * external references: condec.treant
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
	 * external references: view.condec.tab.panel
	 */
	ConDecAPI.prototype.getTreeViewerWithoutRootElement = function getTreeViewerWithoutRootElement(showRelevant,
			callback) {
		var issueId = AJS.$("meta[name='ajs-issue-key']").attr("content");
		if (issueId === undefined) {
			issueId = this.getIssueKey();
		}
		getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getTreeViewer2.json?issueKey=" + issueId
				+ "&showRelevant=" + showRelevant.toString(), function(error, core) {
			if (error === null) {
				callback(core);
			} else {
				showFlag("error", "Tree viewer data could not be received. Error-Code: " + error);
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm,
	 * settingsForAllProjects.vm
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
	 * settingsForAllProjects.vm
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
	 * external references: condec.dialog
	 */
	ConDecAPI.prototype.isIssueStrategy = function isIssueStrategy(callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/config/isIssueStrategy.json?projectKey=" + projectKey,
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
	 * settingsForAllProjects.vm
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
	 * external references: settingsForSingleProject.vm,
	 * settingsForAllProjects.vm
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
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setUseClassifierForIssueComments = function setUseClassifierForIssueComments(
			isClassifierUsedForIssues, projectKey) {
		postJSON(
				AJS.contextPath() + "/rest/decisions/latest/config/setUseClassifierForIssueComments.json?projectKey="
						+ projectKey + "&isClassifierUsedForIssues=" + isClassifierUsedForIssues,
				null,
				function(error, response) {
					if (error === null) {
						showFlag("success",
								"Usage of classification for Decision Knowledge in JIRA Issue Comments has been set to "
										+ isClassifierUsedForIssues + ".");
					} else {
						showFlag("error",
								"Usage of classification for Decision Knowledge in JIRA Issue Comments could not be configured.");
					}
				});
	};

	/*
	 * external references: settingsForSingleProject.vm
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
	 * Knowledge types are a subset of "Alternative", "Argument", "Assessment",
	 * "Assumption", "Claim", "Constraint", "Context", "Decision", "Goal",
	 * "Implication", "Issue", "Problem", and "Solution".
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

	/*
	 * Replaces argument with pro-argument and con-argument in knowledge types
	 * array.
	 */
	function getExtendedKnowledgeTypes(knowledgeTypes) {
		var extendedKnowledgeTypes = knowledgeTypes.filter(function(value) {
			return value.toLowerCase() !== "argument";
		});
		extendedKnowledgeTypes.push("Pro-argument");
		extendedKnowledgeTypes.push("Con-argument");
		return extendedKnowledgeTypes;
	}

	/*
	 * external references: settingsForSingleProject.vm
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
	 * external references: settingsForSingleProject.vm
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
	 * external references: settingsForSingleProject.vm
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
	 * external references: settingsForSingleProject.vm
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
	 * external references: settingsForSingleProject.vm
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
	 * external references: settingsForAllProjects.vm
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
	 * external references: settingsForAllProjects.vm
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
	 * external references: condec.jira.issue.module
	 */
	ConDecAPI.prototype.getElementsByQuery = function getElementsByQuery(query, callback) {
		var projectKey = projectKey || "";
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
	 * external references: condec.jira.issue.module
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

	/*
	 * external references: condec.jira.issue.module
	 */
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
		console.log("conDecAPI getProjectKey");
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

	function showFlag(type, message) {
		AJS.flag({
			type : type,
			close : "auto",
			title : type.charAt(0).toUpperCase() + type.slice(1),
			body : message
		});
	}

	ConDecAPI.prototype.openJiraIssue = function openJiraIssue(nodeId) {
		console.log("conDecAPI openJiraIssue");

		this.getDecisionKnowledgeElement(nodeId, function(decisionKnowledgeElement) {
			var baseUrl = AJS.params.baseURL;
			var key = decisionKnowledgeElement.key;
			global.open(baseUrl + "/browse/" + key, '_self');
		});
	};

	// export ConDecAPI
	global.conDecAPI = new ConDecAPI();
})(window);
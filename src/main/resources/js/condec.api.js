/**
 * This module implements the communication with the ConDec Java REST API and
 * the Jira API.
 *
 * Requires: conDecTreant.findParentElement
 *
 * Is required by: conDecContextMenu conDecDialog conDecEvolutionPage
 * conDecTreant conDecTreeViewer conDecJiraIssueModule conDecKnowledgePage
 * conDecTabPanel conDecVis
 *
 * Is referenced in HTML by settingsForAllProjects.vm
 * settingsForSingleProject.vm
 */
(function(global) {

	var projectKey = null;

	var ConDecAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest";
		projectKey = getProjectKey();
		this.projectKey = projectKey;

		/**
		 * @issue How to store settings retrieved from the backend such as
		 *        knowledge types?
		 * @decision The settings that do not change are stored as global
		 *           attributes (arrays)! The global arrays are filled when the
		 *           getter method is called but only once, if they are empty!
		 * @pro This avoids redundant REST calls and improves performance.
		 * @alternative Do not use global attributes but make REST calls in
		 *              every getter-call!
		 * @con REST calls would be redundant, which leads to longer loading
		 *      times.
		 */
		this.knowledgeTypes = [];
		this.extendedKnowledgeTypes = [];
		this.propagationRules = [];

		this.decisionStatus = ["decided", "challenged", "rejected"];
		this.alternativeStatus = ["idea", "discarded", "recommended"];
		this.issueStatus = ["resolved", "unresolved"];
		this.knowledgeStatus = this.decisionStatus.concat(this.issueStatus).concat(this.alternativeStatus).concat("undefined");
		this.rationaleBacklogItemStatus = ["challenged", "unresolved", "incomplete"];

		// Todo handle origin
		this.documentationLocations = ["JiraIssues", "JiraIssueText", "Code", "PullRequest"];

		this.linkTypes = [];

		this.decisionGroups = [];
	};

	/**
	 * @issue How can we access global attributes of closure objects, e.g.
	 *        extendedKnowlegdeTypes?
	 * @alternative Do not use getters in Javascript but directly call e.g.
	 *              "conDecAPI.extendedKnowlegdeTypes"!
	 * @pro Less code.
	 * @con The global attribute might not be initialized. The initialisation
	 *      cannot be done via direct calls.
	 * @decision Use getters in Javascript and e.g. call
	 *           "conDecAPI.getExtendedKnowledgeTypes()"!
	 * @pro If the global attribute is not initialized, this can be done with
	 *      the getter function.
	 */
	ConDecAPI.prototype.getExtendedKnowledgeTypes = function() {
		if (this.extendedKnowledgeTypes === undefined || this.extendedKnowledgeTypes.length === 0) {
			this.extendedKnowledgeTypes = generalApi.getResponseAsReturnValue(conDecAPI.restPrefix
				+ "/config/getDecisionKnowledgeTypes.json?projectKey=" + conDecAPI.projectKey);
			this.extendedKnowledgeTypes = createExtendedKnowledgeTypes(this.extendedKnowledgeTypes);
		}
		return this.extendedKnowledgeTypes;
	};

	/**
	 * Replaces argument with pro-argument and con-argument in knowledge types
	 * array.
	 */
	function createExtendedKnowledgeTypes(knowledgeTypes) {
		var extendedKnowledgeTypes = knowledgeTypes.filter(function(value) {
			return value.toLowerCase() !== "argument";
		});
		extendedKnowledgeTypes.push("Pro-argument");
		extendedKnowledgeTypes.push("Con-argument");
		return extendedKnowledgeTypes;
	}

	ConDecAPI.prototype.checkIfProjectKeyIsValid = function() {
		if (projectKey === null || projectKey === undefined) {
			/**
			 * Some dependencies were missing when the closure object was first
			 * instantiated. Instantiates the object again.
			 */
			global.conDecAPI = new ConDecAPI();
		}
	};

	/*
	 * external references: condec.context.menu, condec.dialog,
	 * condec.knowledge.page
	 */
	ConDecAPI.prototype.getDecisionKnowledgeElement = function(id, documentationLocation, callback) {
		generalApi.getJSON(this.restPrefix + "/knowledge/getDecisionKnowledgeElement.json?projectKey="
			+ projectKey + "&id=" + id + "&documentationLocation=" + documentationLocation, function(error,
				decisionKnowledgeElement) {
			if (error === null) {
				callback(decisionKnowledgeElement);
			}
		});
	};

	/*
	 * external references: condec.dialog
	 */
	ConDecAPI.prototype.getUnlinkedElements = function(id, documentationLocation, callback) {
		generalApi.getJSON(this.restPrefix + "/knowledge/getUnlinkedElements.json?projectKey="
			+ projectKey + "&id=" + id + "&documentationLocation=" + documentationLocation, function(error, unlinkedElements) {
				if (error === null) {
					callback(unlinkedElements);
				}
			});
	};

	/*
	 * Creates a new decision knowledge element. If the element should be
	 * unlinked the idOfExistingElement must be 0 and the
	 * documentationLocationOfExistingElement must be null
	 *
	 * external references: condec.knowledge.page, condec.dialog
	 */
	ConDecAPI.prototype.createDecisionKnowledgeElement = function(summary, description, type, documentationLocation, idOfExistingElement, documentationLocationOfExistingElement, callback) {
		var newElement = {
			"summary": summary,
			"type": type,
			"projectKey": projectKey,
			"description": description,
			"documentationLocation": documentationLocation
		};
		generalApi.postJSON(this.restPrefix + "/knowledge/createDecisionKnowledgeElement.json?idOfExistingElement="
			+ idOfExistingElement + "&documentationLocationOfExistingElement="
			+ documentationLocationOfExistingElement, newElement, function(error, newElement) {
				if (error === null) {
					showFlag("success", type + " and link have been created.");
					callback(newElement.id);
				}
			});
	};

	ConDecAPI.prototype.assignDecisionGroup = function(level, existingGroups, addgroup, sourceId, documentationLocation, callback) {
		var newElement = {};
		generalApi.postJSON(this.restPrefix + "/knowledge/assignDecisionGroup.json?sourceId="
			+ sourceId + "&documentationLocation="
			+ documentationLocation + "&projectKey="
			+ projectKey + "&level="
			+ level + "&existingGroups="
			+ existingGroups + "&addGroup="
			+ addgroup, newElement, function(error, newElement) {
				if (error === null) {
					showFlag("Success", "Group Assignments have been created.");
					callback(sourceId);
				}
			});
	};

	/*
	 * external references: condec.dialog
	 */
	ConDecAPI.prototype.updateDecisionKnowledgeElement = function(id, summary, description, type, documentationLocation, status, callback) {
		var element = {
			"id": id,
			"summary": summary,
			"type": type,
			"projectKey": projectKey,
			"description": description,
			"documentationLocation": documentationLocation,
			"status": status
		};
		var parentElement = conDecTreant.findParentElement(id);
		generalApi.postJSON(this.restPrefix + "/knowledge/updateDecisionKnowledgeElement.json?idOfParentElement="
			+ parentElement["id"] + "&documentationLocationOfParentElement="
			+ parentElement["documentationLocation"], element, function(error, response) {
				if (error === null) {
					showFlag("success", "Decision knowledge element has been updated.");
					callback();
				}
			});
	};

	/**
	 * Updates the knowledge type of the element.
	 *
	 * external references: condec.context.menu
	 */
	ConDecAPI.prototype.changeKnowledgeType = function(id, type, documentationLocation, callback) {
		this.getDecisionKnowledgeElement(id, documentationLocation, function(element) {
			conDecAPI.updateDecisionKnowledgeElement(id, element.summary, element.description, type,
				documentationLocation, null, callback);
		});
	};

	/*
	 * external references: condec.dialog
	 */
	ConDecAPI.prototype.deleteDecisionKnowledgeElement = function(id, documentationLocation, callback) {
		var element = {
			"id": id,
			"projectKey": projectKey,
			"documentationLocation": documentationLocation
		};
		generalApi.deleteJSON(this.restPrefix + "/knowledge/deleteDecisionKnowledgeElement.json", element,
			function(error, isDeleted) {
				if (error === null) {
					showFlag("success", "Decision knowledge element has been deleted.");
					callback();
				}
			});
	};

	/*
	 * external references: condec.dialog, condec.treant, condec.tree.viewer, condec.decision.table
	 */
	ConDecAPI.prototype.createLink = function(idOfParent, idOfChild, documentationLocationOfParent, documentationLocationOfChild, linkType, callback) {
		generalApi.postJSON(this.restPrefix + "/knowledge/createLink.json?projectKey=" + projectKey
			+ "&idOfParent=" + idOfParent + "&documentationLocationOfParent=" + documentationLocationOfParent + "&idOfChild=" + idOfChild
			+ "&documentationLocationOfChild=" + documentationLocationOfChild + "&linkTypeName=" + linkType, null, function(error, link) {
				if (error === null) {
					showFlag("success", "Link has been created.");
					callback(link);
				}
			});
	};

	/*
	 * external references: condec.dialog, condec.treant, condec.tree.viewer
	 */
	ConDecAPI.prototype.deleteLink = function(idOfDestinationElement, idOfSourceElement,
		documentationLocationOfDestinationElement, documentationLocationOfSourceElement, callback) {
		var link = {
			"idOfSourceElement": idOfSourceElement,
			"idOfDestinationElement": idOfDestinationElement,
			"documentationLocationOfSourceElement": documentationLocationOfSourceElement,
			"documentationLocationOfDestinationElement": documentationLocationOfDestinationElement,
			"projectKey": projectKey
		};
		generalApi.deleteJSON(this.restPrefix + "/knowledge/deleteLink.json?projectKey=" + projectKey,
			link, function(error, link) {
				if (error === null) {
					showFlag("success", "Link has been deleted.");
					callback(link);
				}
			});
	};

	/**
	 * Updates the knowledge status of the element. The summary and description are set null
	 * to indicate that only the knowledge status is updated.
	 *
	 * external references: condec.dialog
	 */
	ConDecAPI.prototype.setStatus = function(id, documentationLocation, type, status, callback) {
		this.updateDecisionKnowledgeElement(id, null, null, type, documentationLocation, status, callback);
	};

	/*
	 * external references: condec.export, condec.decision.table
	 */
	ConDecAPI.prototype.getKnowledgeElements = function(filterSettings, callback) {
		filterSettings["projectKey"] = projectKey;
		generalApi.postJSON(this.restPrefix + "/knowledge/knowledgeElements.json", filterSettings, function(error, elements) {
			if (error === null) {
				callback(elements);
			}
		});
	};

	/*
	 * external references: condec.context.menu
	 */
	ConDecAPI.prototype.setSentenceIrrelevant = function(id, callback) {
		var jsondata = {
			"id": id,
			"documentationLocation": "s",
			"projectKey": projectKey
		};
		generalApi.postJSON(this.restPrefix + "/knowledge/setSentenceIrrelevant.json", jsondata, function(
			error) {
			if (error === null) {
				showFlag("success", "Decision knowledge element has been updated.");
				callback();
			}
		});
	};

	/*
	 * external references: jiraIssueModule.vm
	 */
	ConDecAPI.prototype.resetDecisionKnowledgeFromText = function(jiraIssueId, callback) {
		generalApi.postJSON(this.restPrefix + "/knowledge/resetDecisionKnowledgeFromText.json", jiraIssueId,
			function(error, numberOfElements) {
				if (error === null) {
					showFlag("success", numberOfElements + " decision knowledge elements in the text were found and linked in the knowledge graph.");
					callback();
				}
			});
	};

	/*
	 * external references: condec.tree.viewer
	 */
	ConDecAPI.prototype.getTreeViewer = function(filterSettings, callback) {
		filterSettings["projectKey"] = projectKey;
		generalApi.postJSON(this.restPrefix + "/view/getTreeViewer.json", filterSettings, function(error, core) {
			if (error === null) {
				callback(core);
			}
		});
	};

	/*
	 * external references: condec.treant
	 */
	ConDecAPI.prototype.getTreant = function(filterSettings, callback) {
		filterSettings["projectKey"] = projectKey;
		generalApi.postJSON(this.restPrefix + "/view/getTreant.json", filterSettings, function(error, treant) {
			if (error === null) {
				callback(treant);
			}
		});
	};

	/*
	 * external references: condec.dialog
	 */
	ConDecAPI.prototype.getSummarizedCode = function(id, documentationLocation, probability, callback) {
		generalApi.getText(this.restPrefix + "/knowledge/getSummarizedCode?projectKey=" + projectKey
			+ "&id=" + id + "&documentationLocation=" + documentationLocation + "&probability=" + probability,
			function(error, summary) {
				if (error === null) {
					callback(summary);
				}
			});
	};

	/*
	 * external references: condec.vis, condec.relationship.page
	 */
	ConDecAPI.prototype.getVis = function(filterSettings, callback) {
		filterSettings["projectKey"] = projectKey;
		generalApi.postJSON(this.restPrefix + "/view/getVis.json", filterSettings, function(error, vis) {
			if (error === null) {
				callback(vis);
			}
		});
	};

	/**
	 * If the search term is a Jira query in JQL, this function provides the
	 * filter settings matching the JQL. Otherwise it provides the default
	 * filter settings (e.g. link distance 3, all knowledge types, all link
	 * types, ...).
	 *
	 * external reference: currently not used, used to be used in
	 * Jira issue view to fill the HTML filter elements
	 */
	ConDecAPI.prototype.getFilterSettings = function(elementKey, searchTerm, callback) {
		generalApi.getJSON(this.restPrefix + "/view/getFilterSettings.json?elementKey=" + elementKey
			+ "&searchTerm=" + searchTerm, function(error, filterSettings) {
				if (error === null) {
					callback(filterSettings);
				}
			});
	};

	/**
	 * @param isPlacedAtCreationDate elements will be placed at their creation date.
	 * @param isPlacedAtUpdatingDate elements will be placed at the date of their last update.
	 *
	 * If both isPlacedAtCreationDate and isPlacedAtUpdatingDate are true, a bar connecting
	 * both dates is shown in chronology view.
	 *
	 * external references: condec.evolution.page
	 */
	ConDecAPI.prototype.getEvolutionData = function(filterSettings, isPlacedAtCreationDate, isPlacedAtUpdatingDate, callback) {
		filterSettings["projectKey"] = projectKey;
		generalApi.postJSON(this.restPrefix + "/view/getEvolutionData.json?isPlacedAtCreationDate=" + isPlacedAtCreationDate
			+ "&isPlacedAtUpdatingDate=" + isPlacedAtUpdatingDate, filterSettings, function(
				error, evolutionData) {
			if (error === null) {
				callback(evolutionData);
			}
		});
	};

	/*
	 * external references: condec.matrix
	 */
	ConDecAPI.prototype.getMatrix = function(filterSettings, callback) {
		filterSettings["projectKey"] = projectKey;
		generalApi.postJSON(this.restPrefix + "/view/getMatrix.json", filterSettings, function(error, matrix) {
			if (error == null) {
				callback(matrix);
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm,
	 * settingsForAllProjects.vm
	 */
	ConDecAPI.prototype.setActivated = function(isActivated, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/setActivated.json?projectKey=" + projectKey
			+ "&isActivated=" + isActivated, null, function(error, response) {
				if (error === null) {
					showFlag("success", "Plug-in activation for the project has been set to " + isActivated + ".");
				}
			});
	};

	/*
	 * external references: condec.text.editor.extension
	 */
	ConDecAPI.prototype.isActivated = function(callback) {
		generalApi.getJSON(this.restPrefix + "/config/isActivated.json?projectKey=" + projectKey,
			function(error, isActivatedBoolean) {
				if (error === null) {
					callback(isActivatedBoolean);
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm,
	 * settingsForAllProjects.vm
	 */
	ConDecAPI.prototype.setIssueStrategy = function(isIssueStrategy, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/setIssueStrategy.json?projectKey=" + projectKey
			+ "&isIssueStrategy=" + isIssueStrategy, null, function(error, response) {
				if (error === null) {
					showFlag("success", "Strategy has been selected.");
				}
			});
	};

	/*
	 * external references: condec.dialog, condec.context.menu
	 */
	ConDecAPI.prototype.isIssueStrategy = function(callback) {
		generalApi.getJSON(this.restPrefix + "/config/isIssueStrategy.json?projectKey=" + projectKey,
			function(error, isIssueStrategyBoolean) {
				if (error === null) {
					callback(isIssueStrategyBoolean);
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm,
	 * settingsForAllProjects.vm
	 */
	ConDecAPI.prototype.setKnowledgeExtractedFromGit = function(isKnowledgeExtractedFromGit, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/setKnowledgeExtractedFromGit.json?projectKey="
			+ projectKey + "&isKnowledgeExtractedFromGit=" + isKnowledgeExtractedFromGit, null, function(error,
				response) {
			if (error === null) {
				if (isKnowledgeExtractedFromGit) {
					showFlag("success", "Git connection for this project is activated.");
				} else {
					showFlag("success", "Git connection for this project is deactivated.");
				}
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setPostFeatureBranchCommits = function(checked, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/setPostFeatureBranchCommits.json?projectKey="
			+ projectKey + "&newSetting=" + checked, null, function(error,
				response) {
			if (error === null) {
				showFlag("success", "Post Feature Branch Commits for this project has been set to " + checked
					+ ".");
				return checked;
			} else {
				return !checked;
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setPostSquashedCommits = function(checked, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/setPostSquashedCommits.json?projectKey="
			+ projectKey + "&newSetting=" + checked, null, function(error,
				response) {
			if (error === null) {
				showFlag("success", "Post Squashed Commits for this project has been set to " + checked
					+ ".");
				return checked;
			} else {
				return !checked;
			}
		});
	};

	/*
	 * external references: gitSettings.vm
	 */
	ConDecAPI.prototype.setGitRepositoryConfigurations = function(projectKey, gitRepositoryConfigurations) {
		generalApi.postJSON(this.restPrefix + "/config/setGitRepositoryConfigurations.json?projectKey="
			+ projectKey, gitRepositoryConfigurations, function(error, response) {
				if (error === null) {
					showFlag("success", "The git URIs and credentials for this project have been set.");
				}
			});
	};

	/*
	 * external references: gitSettings.vm
	 */
	ConDecAPI.prototype.setCodeFileEndings = function(projectKey, codeFileEndings) {
		generalApi.postJSON(this.restPrefix + "/config/setCodeFileEndings.json?projectKey="
			+ projectKey, codeFileEndings, function(error, response) {
				if (error === null) {
					showFlag("success", "The code file endings for this project have been set.");
				}
			});
	};

	/*
	 * external references: gitSettings.vm
	 */
	ConDecAPI.prototype.deleteGitRepos = function(projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/deleteGitRepos.json?projectKey=" + projectKey, null,
			function(error, response) {
				if (error === null) {
					showFlag("success", "The git repos for this project were deleted.");
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setKnowledgeTypeEnabled = function(isKnowledgeTypeEnabled, knowledgeType, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/setKnowledgeTypeEnabled.json?projectKey="
			+ projectKey + "&knowledgeType=" + knowledgeType + "&isKnowledgeTypeEnabled=" + isKnowledgeTypeEnabled,
			null, function(error, response) {
				if (error === null) {
					showFlag("success", "The activation of the " + knowledgeType
						+ " knowledge type for this project has been set to " + isKnowledgeTypeEnabled + ".");
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.isKnowledgeTypeEnabled = function(knowledgeType, projectKey, toggle, callback) {
		generalApi.getJSON(this.restPrefix + "/config/isKnowledgeTypeEnabled.json?knowledgeType="
			+ knowledgeType + "&projectKey=" + projectKey, function(error, isKnowledgeTypeEnabled) {
				if (error === null) {
					callback(isKnowledgeTypeEnabled, toggle);
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setLinkTypeEnabled = function(isLinkTypeEnabled, linkType, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/setLinkTypeEnabled.json?projectKey="
			+ projectKey + "&linkType=" + linkType + "&isLinkTypeEnabled=" + isLinkTypeEnabled,
			null, function(error, response) {
				if (error === null) {
					showFlag("success", "The activation of the " + linkType
						+ " link type for this project has been set to " + isLinkTypeEnabled + ".");
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.isLinkTypeEnabled = function(linkType, projectKey, toggle, callback) {
		generalApi.getJSON(this.restPrefix + "/config/isLinkTypeEnabled.json?linkType="
			+ linkType + "&projectKey=" + projectKey, function(error, isLinkTypeEnabled) {
				if (error === null) {
					callback(isLinkTypeEnabled, toggle);
				}
			});
	};

	/*
	 * Knowledge types are a subset of "Alternative", "Argument", "Assessment",
	 * "Assumption", "Claim", "Constraint", "Context", "Decision", "Goal",
	 * "Implication", "Issue", "Problem", and "Solution".
	 */
	ConDecAPI.prototype.getKnowledgeTypes = function() {
		if (this.knowledgeTypes === undefined || this.knowledgeTypes.length === 0) {
			this.knowledgeTypes = generalApi.getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getKnowledgeTypes.json?projectKey=" + conDecAPI.projectKey);
		}
		return this.knowledgeTypes;
	};

	/*
	 * external references: condec.dialog, condec.filtering
	 */
	ConDecAPI.prototype.getLinkTypes = function() {
		if (this.linkTypes === undefined || this.linkTypes.length === 0) {
			this.linkTypes = generalApi.getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getLinkTypes.json?projectKey=" + conDecAPI.projectKey);
		}
		return this.linkTypes;
	};

	/*
	 * external references: condec.dialog, condec.filtering
	 */
	ConDecAPI.prototype.getAllLinkTypes = function() {
		if (this.linkTypes === undefined || this.linkTypes.length === 0) {
			this.linkTypes = generalApi.getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getAllLinkTypes.json?projectKey=" + conDecAPI.projectKey);
		}
		return this.linkTypes;
	};

	/*
	 * external references: condec.filtering
	 */
	ConDecAPI.prototype.getAllDecisionGroups = function() {
		if (this.decisionGroups === undefined || this.decisionGroups.length === 0) {
			this.decisionGroups = decisionGroups = generalApi.getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getAllDecisionGroups.json?projectKey=" + conDecAPI.projectKey);
		}
		return this.decisionGroups;
	};

	/*
	 * external references: condec.decision.table
	 */
	ConDecAPI.prototype.getDecisionTable = function(filterSettings, callback) {
		filterSettings["projectKey"] = projectKey;
		generalApi.postJSON(this.restPrefix + "/view/decisionTable.json", filterSettings,
			function(error, issues) {
				if (error === null) {
					callback(issues);
				}
			});
	};

	/*
	 * external references: condec.decision.table
	 */
	ConDecAPI.prototype.getDecisionTableCriteria = function(callback) {
		generalApi.getJSON(this.restPrefix + `/view/decisionTableCriteria.json?projectKey=${projectKey}`,
			function(error, query) {
				if (error === null) {
					callback(query);
				}
			});
	};

	/*
	 * external reference: rationalModelSettings.vm
	 */
	ConDecAPI.prototype.setDecisionTableCriteriaQuery = function(projectKey, query) {
		if (query.length === 0) {
			return showFlag("error", "Query length must not be empty.");
		}
		generalApi.postJSON(this.restPrefix + `/config/setDecisionTableCriteriaQuery.json?projectKey=${projectKey}&query=${query}`,
			null, function(error, numberOfCriteria) {
				if (error === null) {
					var message = "Query was saved. ";
					if (numberOfCriteria === 0) {
						message += "However, the query might be wrong because no criteria were found."
					} else {
						message += "Enabled " + numberOfCriteria + " criteria for the decision table."
					}
					showFlag("success", message);
				}
			});
	};

	/*
	 * external references: consistencySettings.vm
	 */
	ConDecAPI.prototype.setConsistencyActivated = function(isConsistencyActivated, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/setConsistencyActivated.json?projectKey="
			+ projectKey + "&isConsistencyActivated=" + isConsistencyActivated, null, function(error,
				response) {
			if (error === null) {
				showFlag("success",
					"Usage of the consistency module of the ConDec plugin has been set to "
					+ isConsistencyActivated + ".");
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setWebhookData = function(projectKey, webhookUrl, webhookSecret) {
		generalApi.postJSON(this.restPrefix + "/config/setWebhookData.json?projectKey=" + projectKey
			+ "&webhookUrl=" + webhookUrl + "&webhookSecret=" + webhookSecret, null, function(error, response) {
				if (error === null) {
					showFlag("success", "The webhook for this project has been set.");
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setWebhookEnabled = function(isActivated, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/setWebhookEnabled.json?projectKey=" + projectKey
			+ "&isActivated=" + isActivated, null, function(error, response) {
				if (error === null) {
					showFlag("success", "The webhook activation for this project has been changed.");
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setWebhookType = function(webhookType, projectKey, isWebhookTypeEnabled) {
		generalApi.postJSON(this.restPrefix + "/config/setWebhookType.json?projectKey=" + projectKey
			+ "&webhookType=" + webhookType + "&isWebhookTypeEnabled=" + isWebhookTypeEnabled, null, function(
				error, response) {
			if (error === null) {
				showFlag("success", "The webhook root element type was changed for this project.");
			}
		});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.sendTestPost = function(projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/sendTestPost.json?projectKey="
			+ projectKey, null, function(error, response) {
				if (error === null) {
					showFlag("success", "The webhook test was send.");
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.cleanDatabases = function(projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/cleanDatabases.json?projectKey="
			+ projectKey, null, function(error, response) {
				if (error === null) {
					showFlag("success", "The databases have been cleaned.");
				}
			});
	};

	ConDecAPI.prototype.getDecisionGroups = function(id, location, callback) {
		// TODO Change to POST method and post knowledgeElement
		generalApi.getJSON(this.restPrefix + "/config/getDecisionGroups.json?elementId=" + id
			+ "&location=" + location + "&projectKey=" + projectKey, function(error, decisionGroups) {
				if (error === null) {
					callback(decisionGroups);
				}
			});
	};

	ConDecAPI.prototype.renameDecisionGroup = function(oldName, newName, callback) {
		generalApi.getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/renameDecisionGroup.json?projectKey=" + projectKey
			+ "&oldName=" + oldName + "&newName=" + newName);
		callback();
	};

	/*
	 * external references: templates/settings/classificationSettings.vm
	 */
	ConDecAPI.prototype.setTextClassifierEnabled = function(isTextClassifierEnabled, projectKey) {
		generalApi.postJSON(this.restPrefix + "/classification/setTextClassifierEnabled.json?projectKey="
			+ projectKey + "&isTextClassifierEnabled=" + isTextClassifierEnabled, null, function(error,
				response) {
			if (error === null) {
				showFlag("success",
					"Activation of text classification for decision knowledge in Jira issue description and comments was set to "
					+ isTextClassifierEnabled + ".");
			}
		});
	};

	/*
	 * external references: templates/settings/classificationSettings.vm
	 */
	ConDecAPI.prototype.classifyText = function(text, projectKey, callback) {
		generalApi.postJSON(this.restPrefix + "/classification/classifyText?projectKey="
			+ projectKey + "&text=" + text, null, function(error, response) {
				if (error === null) {
					callback(response.classificationResult);
				} else {
					callback("Error! Please check if the classifier is trained.");
				}
			});
	};

	/*
	 * external references: templates/settings/classificationSettings.vm
	 */
	ConDecAPI.prototype.classifyWholeProject = function(projectKey, animatedElement) {
		animatedElement.classList.add("aui-progress-indicator-value");
		generalApi.postJSON(this.restPrefix + "/classification/classifyWholeProject.json?projectKey=" + projectKey,
			null,
			function(error, response) {
				animatedElement.classList.remove("aui-progress-indicator-value");
				if (error === null) {
					showFlag("success", "The whole project has been classified.");
				}
			});
	};

	/*
	 * external references: templates/settings/classificationSettings.vm
	 */
	ConDecAPI.prototype.trainClassifier = function(projectKey, trainingFileName, binaryClassifierType, fineGrainedClassifierType, animatedElement) {
		animatedElement.classList.add("aui-progress-indicator-value");
		generalApi.postJSON(this.restPrefix + "/classification/trainClassifier.json?projectKey=" + projectKey + "&trainingFileName="
			+ trainingFileName + "&binaryClassifierType=" + binaryClassifierType + "&fineGrainedClassifierType=" + fineGrainedClassifierType,
			null,
			function(error, response) {
				animatedElement.classList.remove("aui-progress-indicator-value");
				if (error === null) {
					showFlag("success", "The classifier was successfully retrained.");
				}
			});
	};

	/*
	 * external references: templates/settings/classificationSettings.vm
	 */
	ConDecAPI.prototype.useTrainedClassifier = function(projectKey, trainedClassifier, isOnlineLearningActivated) {
		generalApi.postJSON(this.restPrefix + "/classification/useTrainedClassifier.json?projectKey=" + projectKey +
			"&trainedClassifier=" + trainedClassifier + "&isOnlineLearningActivated=" + isOnlineLearningActivated,
			null,
			function(error, response) {
				if (error === null) {
					showFlag("success", "The trained classifier was successfully set for the project.");
				}
			});
	};

	/*
	 * external references: templates/settings/classificationSettings.vm
	 */
	ConDecAPI.prototype.evaluateTextClassifier = function(projectKey, trainingFileName, numberOfFolds,
		binaryClassifierType, fineGrainedClassifierType, animatedElement, callback) {
		animatedElement.classList.add("aui-progress-indicator-value");
		generalApi.postJSON(this.restPrefix + "/classification/evaluateTextClassifier.json?projectKey=" + projectKey + "&trainingFileName="
			+ trainingFileName + "&numberOfFolds=" + numberOfFolds
			+ "&binaryClassifierType=" + binaryClassifierType + "&fineGrainedClassifierType=" + fineGrainedClassifierType,
			null,
			function(error, response) {
				animatedElement.classList.remove("aui-progress-indicator-value");
				if (error === null) {
					showFlag("success", "The evaluation results file was successfully created.");
					callback(response["content"]);
				}
			});
	};

	/*
	 * external references: templates/settings/classificationSettings.vm
	 */
	ConDecAPI.prototype.saveTrainingFile = function(projectKey, callback) {
		generalApi.postJSON(this.restPrefix + "/classification/saveTrainingFile.json?projectKey=" + projectKey, null,
			function(error, response) {
				if (error === null) {
					showFlag("success", "The training file was successfully created and saved in "
						+ response["trainingFile"] + ".");
					callback(response["content"]);
				}
			});
	};

	/*
	 * external references: settings/definitionofdone/...
	 */
	ConDecAPI.prototype.setDefinitionOfDone = function(projectKey, definitionOfDone) {
		generalApi.postJSON(this.restPrefix + "/dodchecking/setDefinitionOfDone.json?projectKey=" + projectKey, definitionOfDone, function(
			error, response) {
			if (error === null) {
				showFlag("success", "The definition of done is updated.");
			}
		});
	};
	
	/*
	 * external references: nudging/condec.prompts.js
	 */
	ConDecAPI.prototype.doesElementNeedCompletenessApproval = function(filterSettings) {
		return generalApi.postJSONReturnPromise(
			`${this.restPrefix}/dodchecking/doesElementNeedCompletenessApproval.json`, filterSettings
		);
	};

	ConDecAPI.prototype.getPropagationRules = function() {
		if (this.propagationRules === undefined || this.propagationRules.length === 0) {
			this.propagationRules = generalApi.getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getPropagationRules.json?projectKey=" + conDecAPI.projectKey);
		}
		return this.propagationRules;
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setCiaSettings = function(projectKey, ciaSettings) {
		generalApi.postJSON(this.restPrefix + "/config/setCiaSettings.json?projectKey="
			+ projectKey, ciaSettings, function(error, response) {
				if (error === null) {
					showFlag("success", "The Cia Settings are updated.");
				}
			});
	};

	ConDecAPI.prototype.getCiaSettings = function(projectKey, callback) {
		generalApi.getJSON(this.restPrefix + "/config/getCiaSettings.json?projectKey=" + projectKey, callback);
	};

	function getIssueKey() {
		var issueKey = null;
		if (JIRA && JIRA.Issue && JIRA.Issue.getIssueKey) {
			issueKey = JIRA.Issue.getIssueKey();
		}
		if (issueKey === undefined || !issueKey) {
			// console.log("conDecAPI could not getIssueKey using object
			// JIRA!");
			if (AJS && AJS.Meta && AJS.Meta.get) {
				issueKey = AJS.Meta.get("issue-key");
			}
		}
		if (issueKey === undefined || !issueKey) {
			// console.log("conDecAPI could not getIssueKey using object
			// AJS!");
			var chunks = document.location.pathname.split("/");
			if (chunks.length > 0) {
				var lastChunk = chunks[chunks.length - 1];
				if (lastChunk.includes("-")) {
					issueKey = lastChunk;
				}
			}
		}
		// console.log("conDecAPI getIssueKey: " + issueKey);
		return issueKey;
	}

	/*
	 * external references: condec.context.menu
	 */
	ConDecAPI.prototype.openJiraIssue = function(elementId, documentationLocation) {
		let newTab = window.open();
		this.getDecisionKnowledgeElement(elementId, documentationLocation, function(decisionKnowledgeElement) {
			newTab.location.href = decisionKnowledgeElement.url;
		});
	};

	/*
	 * external references: condec.export,
	 * condec.gitdiffviewer, relatedIssuesTab.vm
	 */
	ConDecAPI.prototype.getIssueKey = getIssueKey;

	function getProjectKey() {
		// console.log("conDecAPI getProjectKey");
		var projectKey;
		try {
			projectKey = JIRA.API.Projects.getCurrentProjectKey();
		} catch (error) {
			// console.log(error);
		}
		if (projectKey === undefined) {
			try {
				var issueKey = getIssueKey();
				projectKey = issueKey.split("-")[0];
			} catch (error) {
				// console.log(error);
			}
		}
		return projectKey;
	}

	/*
	 * external references: relatedIssuesTab.vm
	 */
	ConDecAPI.prototype.getProjectKey = getProjectKey;

	var showFlag = function(type, message, status) {
		if (status === null || status === undefined) {
			status = "";
		}
		AJS.flag({
			type: type,
			close: "auto",
			title: type.charAt(0).toUpperCase() + type.slice(1) + " " + status,
			body: message
		});
	};

	ConDecAPI.prototype.showFlag = showFlag;

	// export ConDecAPI
	global.conDecAPI = new ConDecAPI();
})(window);

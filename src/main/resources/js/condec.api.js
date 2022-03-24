/**
 * This module implements the communication with the ConDec Java REST API for general knowledge management 
 * and configuration as well as with the Jira API.
 *
 * Requires: conDecTreant.findParentElement
 *
 * Is required by: conDecContextMenu conDecDialog conDecEvolutionPage
 * conDecTreant conDecTreeViewer conDecKnowledgePage conDecVis
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
		this.rationaleBacklogItemStatus = ["challenged", "unresolved", "DoD violated"];

		this.documentationLocations = ["JiraIssues", "JiraIssueText", "Code", "PullRequest"];

		this.linkTypes = [];
		this.allLinkTypes = [];
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

	ConDecAPI.prototype.getKnowledgeTypesWithoutDecisionKnowledge = function() {
		var decisionKnowledgeTypes = ["Issue", "Decision", "Alternative", "Argument", "Goal"];
		return this.getKnowledgeTypes().filter(function(value, index, arr) {
			return !decisionKnowledgeTypes.includes(value);
		});
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

	/**
	 * external references: condec.context.menu, condec.dialog, condec.vis, 
	 * condec.export, condec.decision.guidance
	 */
	ConDecAPI.prototype.getKnowledgeElement = function(id, documentationLocation, callback) {
		generalApi.getJSON(this.restPrefix + "/knowledge/knowledgeElement?projectKey="
			+ projectKey + "&id=" + id + "&documentationLocation=" + documentationLocation, function(error,
				knowledgeElement) {
			if (error === null) {
				knowledgeElement.projectKey = projectKey;
				callback(knowledgeElement);
			}
		});
	};

	/**
	 * external references: condec.dialog
	 */
	ConDecAPI.prototype.getUnlinkedElements = function(id, documentationLocation, callback) {
		generalApi.getJSON(this.restPrefix + "/knowledge/getUnlinkedElements?projectKey="
			+ projectKey + "&id=" + id + "&documentationLocation=" + documentationLocation, function(error, unlinkedElements) {
				if (error === null) {
					callback(unlinkedElements);
				}
			});
	};

	/**
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
		generalApi.postJSON(this.restPrefix + "/knowledge/element/" + idOfExistingElement + "/"
			+ documentationLocationOfExistingElement, newElement, function(error, newElement) {
				if (error === null) {
					showFlag("success", type + " and link have been created.");
					callback(newElement.id);
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
		generalApi.postJSON(this.restPrefix + "/knowledge/updateDecisionKnowledgeElement?idOfParentElement="
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
		this.getKnowledgeElement(id, documentationLocation, function(element) {
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
		generalApi.deleteJSON(this.restPrefix + "/knowledge/deleteDecisionKnowledgeElement", element,
			function(error, isDeleted) {
				if (error === null) {
					showFlag("success", "Decision knowledge element has been deleted.");
					callback();
				}
			});
	};

	/**
	 * external references: condec.dialog, condec.treant, condec.tree.viewer, condec.decision.table
	 */
	ConDecAPI.prototype.createLink = function(idOfParent, idOfChild, documentationLocationOfParent, documentationLocationOfChild, linkType, callback) {
		generalApi.postJSON(this.restPrefix + "/knowledge/link/" + projectKey
			+ "?idOfParent=" + idOfParent + "&documentationLocationOfParent=" + documentationLocationOfParent + "&idOfChild=" + idOfChild
			+ "&documentationLocationOfChild=" + documentationLocationOfChild + "&linkTypeName=" + linkType, null, function(error, link) {
				if (error === null) {
					showFlag("success", "Link has been created.");
					callback(link);
				}
			});
	};

	/**
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
		generalApi.deleteJSON(this.restPrefix + "/knowledge/link/" + projectKey, link, function(error, link) {
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

	/**
	 * external references: condec.export, condec.decision.table
	 */
	ConDecAPI.prototype.getKnowledgeElements = function(filterSettings, callback) {
		filterSettings["projectKey"] = projectKey;
		generalApi.postJSON(this.restPrefix + "/knowledge/knowledgeElements", filterSettings, function(error, elements) {
			if (error === null) {
				callback(elements, filterSettings);
			}
		});
	};

	/*
	 * external references: condec.decision.table, condec.decision.guidance, condec.prompts
	 */
	ConDecAPI.prototype.getDecisionProblems = function(filterSettings, callback) {
		var jiraIssueKey = this.getIssueKey();
		if (filterSettings === null || filterSettings === undefined) {
			filterSettings = {};
		}
		filterSettings.knowledgeTypes = ["Issue", "Problem", "Goal"];
		filterSettings.selectedElement = jiraIssueKey !== undefined ? jiraIssueKey : "";
		filterSettings.projectKey = projectKey;
		conDecAPI.getKnowledgeElements(filterSettings, (elements) => {
			const decisionProblems = elements.filter((element) => element.id !== JIRA.Issue.getIssueId()
				|| filterSettings.knowledgeTypes.includes(element.type))
			callback(decisionProblems);
		});
	};

	/*
	 * external references: condec.context.menu
	 */
	ConDecAPI.prototype.createJiraIssueFromSentence = function(id, callback) {
		var jsondata = {
			"id": id,
			"projectKey": projectKey,
			"documentationLocation": "s"
		};
		generalApi.postJSON(this.restPrefix + "/knowledge/createJiraIssueFromSentence", jsondata,
			function(error, jiraIssue) {
				if (error === null) {
					showFlag("success", "Jira issue with key " + jiraIssue.key + " has been created.");
					callback();
				}
			});
	};

	/*
	 * external references: jiraIssueModule.vm
	 */
	ConDecAPI.prototype.resetDecisionKnowledgeFromText = function(jiraIssueId, callback) {
		generalApi.postJSON(this.restPrefix + "/knowledge/resetDecisionKnowledgeFromText", jiraIssueId,
			function(error, numberOfElements) {
				if (error === null) {
					showFlag("success", numberOfElements + " decision knowledge elements in the text were found and linked in the knowledge graph.");
					callback();
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm,
	 * settingsForAllProjects.vm
	 */
	ConDecAPI.prototype.setActivated = function(isActivated, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/" + projectKey + "/activate", isActivated,
			function(error, response) {
				if (error === null) {
					showFlag("success", "Plug-in activation for the project has been set to " + isActivated + ".");
				}
			});
	};

	/*
	 * external references: condec.text.editor.extension
	 */
	ConDecAPI.prototype.isActivated = function(callback) {
		generalApi.getJSON(this.restPrefix + "/config/isActivated?projectKey=" + projectKey,
			function(error, isActivatedBoolean) {
				if (error === null) {
					callback(isActivatedBoolean);
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setJiraIssueDocumentationLocationActivated = function(isActivated, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/" + projectKey + "/activate-jira-issue-documentation", isActivated,
			function(error, response) {
				if (error === null) {
					showFlag("success", "Documentation of rationale in entire Jira has been set to " + isActivated);
				}
			});
	};

	/*
	 * external references: condec.dialog, condec.context.menu
	 */
	ConDecAPI.prototype.isJiraIssueDocumentationLocationActivated = function(callback) {
		generalApi.getJSON(this.restPrefix + "/config/isJiraIssueDocumentationLocationActivated?projectKey=" + projectKey,
			function(error, isActivated) {
				if (error === null) {
					callback(isActivated);
				}
			});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setKnowledgeTypeEnabled = function(isKnowledgeTypeEnabled, knowledgeType, projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/setKnowledgeTypeEnabled?projectKey="
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
		generalApi.getJSON(this.restPrefix + "/config/isKnowledgeTypeEnabled?knowledgeType="
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
		generalApi.postJSON(this.restPrefix + "/config/setLinkTypeEnabled?projectKey="
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
		generalApi.getJSON(this.restPrefix + "/config/isLinkTypeEnabled?linkType="
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
		if (conDecAPI.projectKey === undefined || conDecAPI.projectKey === null || conDecAPI.projectKey.length === 0) {
			return this.knowledgeTypes;
		}
		if (this.knowledgeTypes === undefined || this.knowledgeTypes.length === 0) {
			this.knowledgeTypes = generalApi.getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getKnowledgeTypes.json?projectKey=" + conDecAPI.projectKey);
		}
		return this.knowledgeTypes;
	};

	/*
	 * external references: condec.dialog, condec.filtering
	 */
	ConDecAPI.prototype.getLinkTypes = function() {
		if (conDecAPI.projectKey === undefined || conDecAPI.projectKey === null || conDecAPI.projectKey.length === 0) {
			return this.linkTypes;
		}
		if (this.linkTypes === undefined || this.linkTypes.length === 0) {
			this.linkTypes = generalApi.getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getLinkTypes.json?projectKey=" + conDecAPI.projectKey);
		}
		return this.linkTypes;
	};

	/*
	 * external references: condec.dialog, condec.filtering
	 */
	ConDecAPI.prototype.getAllLinkTypes = function() {
		if (conDecAPI.projectKey === undefined || conDecAPI.projectKey === null || conDecAPI.projectKey.length === 0) {
			return this.allLinkTypes;
		}
		if (this.allLinkTypes === undefined || this.allLinkTypes.length === 0) {
			this.allLinkTypes = generalApi.getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getAllLinkTypes.json?projectKey=" + conDecAPI.projectKey);
		}
		return this.allLinkTypes;
	};

	/*
	 * external reference: rationalModelSettings.vm
	 */
	ConDecAPI.prototype.setDecisionTableCriteriaQuery = function(projectKey, query) {
		if (query.length === 0) {
			return showFlag("error", "Query length must not be empty.");
		}
		generalApi.postJSON(this.restPrefix + `/config/setDecisionTableCriteriaQuery?projectKey=${projectKey}&query=${query}`,
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
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.cleanDatabases = function(projectKey) {
		generalApi.postJSON(this.restPrefix + "/config/cleanDatabases?projectKey="
			+ projectKey, null, function(error, response) {
				if (error === null) {
					showFlag("success", "The databases have been cleaned.");
				}
			});
	};

	/**
	 * external references: condec.context.menu
	 */
	ConDecAPI.prototype.openJiraIssue = function(elementId, documentationLocation) {
		let newTab = window.open();
		this.getKnowledgeElement(elementId, documentationLocation, function(decisionKnowledgeElement) {
			newTab.location.href = decisionKnowledgeElement.url;
		});
	};

	/**
	 * external references: condec.dashboard, condec.decision.grouping
	 */
	ConDecAPI.prototype.createLinkToElement = function(element) {
		var link = document.createElement("a");
		link.classList = "navigationLink";
		link.innerText = element.type + ": " + element.summary;
		link.title = element.key;
		link.href = decodeURIComponent(element.url);
		link.target = "_blank";
		return link;
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

	/**
	 * external references: condec.git.api, condec.quality.check, condec.prompts
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

	/**
	 * external references: condec.quality.check
	 */
	ConDecAPI.prototype.getProjectKey = getProjectKey;

	function showFlag(type, message, status) {
		if (status === null || status === undefined) {
			status = "";
		}
		AJS.flag({
			type: type,
			close: "auto",
			title: type.charAt(0).toUpperCase() + type.slice(1) + " " + status,
			body: message
		});
	}

	ConDecAPI.prototype.showFlag = showFlag;

	// export ConDecAPI
	global.conDecAPI = new ConDecAPI();
})(window);
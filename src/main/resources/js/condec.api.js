/*
 This module implements the communication with the ConDec Java REST API and the JIRA API.

 Requires
 * conDecTreant.findParentElement
    
 Is required by
 * conDecContextMenu
 * conDecDialog
 * conDecTreant
 * conDecTreeViewer
 * conDecJiraIssueModule
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
     * condec.knowledge.page, condec.jira.issue.module
     */
    ConDecAPI.prototype.getDecisionKnowledgeElement = function getDecisionKnowledgeElement(id, documentationLocation,
                                                                                           callback) {
        getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getDecisionKnowledgeElement.json?projectKey="
            + projectKey + "&id=" + id + "&documentationLocation=" + documentationLocation, function(error,
                                                                                                     decisionKnowledgeElement) {
            if (error === null) {
                callback(decisionKnowledgeElement);
            }
        });
    };

	/*
	 * external references: none
	 */
	ConDecAPI.prototype.getAdjacentElements = function getAdjacentElements(id, documentationLocation, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getAdjacentElements.json?projectKey="
				+ projectKey + "&id=" + id + "&documentationLocation=" + documentationLocation, function(error,
				adjacentElements) {
			if (error === null) {
				callback(adjacentElements);
			}
		});
	};

    /*
     * external references: condec.dialog
     */
    ConDecAPI.prototype.getUnlinkedElements = function getUnlinkedElements(id, documentationLocation, callback) {
        getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getUnlinkedElements.json?projectKey="
            + projectKey + "&id=" + id + "&documentationLocation=" + documentationLocation, function(error,
                                                                                                     unlinkedElements) {
            if (error === null) {
                callback(unlinkedElements);
            }
        });
    };

    /*
     * external references: condec.knowledge.page, condec.dialog
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
            }
        });
    };

    /*
     * external references: condec.dialog
     */
    ConDecAPI.prototype.updateDecisionKnowledgeElement = function updateDecisionKnowledgeElement(id, summary,
                                                                                                 description, type, documentationLocation, callback) {
        var element = {
            "id" : id,
            "summary" : summary,
            "type" : type,
            "projectKey" : projectKey,
            "description" : description,
            "documentationLocation" : documentationLocation
        };
        var parentElement = conDecTreant.findParentElement(id);
        postJSON(AJS.contextPath()
            + "/rest/decisions/latest/decisions/updateDecisionKnowledgeElement.json?idOfParentElement="
            + parentElement["id"] + "&documentationLocationOfParentElement="
            + parentElement["documentationLocation"], element, function(error, response) {
            if (error === null) {
                showFlag("success", "Decision knowledge element has been updated.");
                callback();
            }
        });
    };

    /*
     * external references: condec.context.menu, condec.dialog
     */
    ConDecAPI.prototype.changeKnowledgeType = function changeKnowledgeType(id, type, documentationLocation, callback) {
        this.updateDecisionKnowledgeElement(id, null, null, type, documentationLocation, callback);
    };

    /*
     * external references: condec.dialog
     */
    ConDecAPI.prototype.deleteDecisionKnowledgeElement = function deleteDecisionKnowledgeElement(id,
                                                                                                 documentationLocation, callback) {
        var element = {
            "id" : id,
            "projectKey" : projectKey,
            "documentationLocation" : documentationLocation
        };
        deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/deleteDecisionKnowledgeElement.json", element,
            function(error, isDeleted) {
                if (error === null) {
                    showFlag("success", "Decision knowledge element has been deleted.");
                    callback();
                }
            });
    };

    /*
     * external references: condec.dialog, condec.treant, condec.tree.viewer
     */
    ConDecAPI.prototype.createLink = function createLink(knowledgeTypeOfChild, idOfParent, idOfChild,
                                                         documentationLocationOfParent, documentationLocationOfChild, callback) {
        postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/createLink.json?projectKey=" + projectKey
            + "&knowledgeTypeOfChild=" + knowledgeTypeOfChild + "&idOfParent=" + idOfParent
            + "&documentationLocationOfParent=" + documentationLocationOfParent + "&idOfChild=" + idOfChild
            + "&documentationLocationOfChild=" + documentationLocationOfChild, null, function(error, link) {
            if (error === null) {
                showFlag("success", "Link has been created.");
                callback(link);
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
                }
            });
    };

	/*
	 * external references: condec.jira.issue.module
	 */
	ConDecAPI.prototype.getElementsByQuery = function getElementsByQuery(query, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getElements.json?allTrees=false&projectKey="
				+ projectKey + "&query=" + query, function(error, elements) {
			if (error === null) {
				callback(elements);
			}
		});
	};

	/*
	 * external references: condec.jira.issue.module
	 */
	ConDecAPI.prototype.getLinkedElementsByQuery = function getLinkedElementsByQuery(query, elementKey,
			documentationLocation, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getElements.json?allTrees=false&projectKey="
				+ projectKey + "&elementKey=" + elementKey + "&query=" + query, function(error, elements) {
			if (error === null) {
				callback(elements);
			}
		});
	};

	/*
	 * external references: condec.jira.issue.module
	 */
	ConDecAPI.prototype.getAllElementsByQueryAndLinked = function getAllElementsByQueryAndLinked(query, callback) {
		getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/getElements.json?allTrees=true&projectKey="
				+ projectKey + "&query=" + query, function(error, elements) {
			if (error === null) {
				callback(elements);
			}
		});
	};

    /*
     * external references: condec.context.menu
     */
    ConDecAPI.prototype.setSentenceIrrelevant = function setSentenceIrrelevant(id, callback) {
        var jsondata = {
            "id" : id,
            "documentationLocation" : "s",
            "projectKey" : projectKey
        };
        postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/setSentenceIrrelevant.json", jsondata, function(
            error) {
            if (error === null) {
                showFlag("success", "Decision knowledge element has been updated.");
                callback();
            }
        });
    };

    /*
     * external references: condec.context.menu
     */
    ConDecAPI.prototype.createIssueFromSentence = function createIssueFromSentence(id, callback) {
        var jsondata = {
            "id" : id,
            "projectKey" : projectKey
        };
        postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/createIssueFromSentence.json", jsondata,
            function(error, id, type) {
                if (error === null) {
                    showFlag("success", "JIRA Issue has been created");
                    callback();
                }
            });
    };

    /*
     * external references: condec.jira.issue.module
     */
    ConDecAPI.prototype.getSummarizedCode = function getSummarizedCode(id, documentationLocation, callback) {
        getText(AJS.contextPath() + "/rest/decisions/latest/decisions/getSummarizedCode?projectKey=" + projectKey
            + "&id=" + id + "&documentationLocation=" + documentationLocation, function(error, summary) {
            if (error === null) {
                callback(summary);
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
            }
        });
    };

    /*
     * external references: condec.vis
     */

    ConDecAPI.prototype.getVis = function getVis(elementKey,searchTerm, callback) {
        getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getVis.json?elementKey=" + elementKey
            + "&searchTerm=" + searchTerm, function(error,vis) {
            if (error === null) {
                callback(vis);
            }
        });
    };

    ConDecAPI.prototype.getFilterData = function getFilterData(elementKey, searchTerm, callback) {
        getJSON(AJS.contextPath() + "/rest/decisions/latest/view/getFilterData.json?elementKey=" + elementKey
        + "&searchTerm=" + searchTerm, function(error, filterData) {
            if (error === null) {
                callback(filterData);
            }
        })
    };

    /*
     * external references: condec.tab.panel
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
            }
        });
    };

    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.setGitUri = function setGitUri(projectKey, gitUri) {
        postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setGitUri.json?projectKey=" + projectKey
            + "&gitUri=" + gitUri, null, function(error, response) {
            if (error === null) {
                showFlag("success", "The git URI  " + gitUri + " for this project has been set.");
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
            }
        });
    };


    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.setUseClassifierForIssueComments = function setUseClassifierForIssueComments(
        isClassifierUsedForIssues, projectKey) {
        postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setUseClassifierForIssueComments.json?projectKey="
            + projectKey + "&isClassifierUsedForIssues=" + isClassifierUsedForIssues, null, function(error,
                                                                                                     response) {
            if (error === null) {
                showFlag("success",
                    "Usage of classification for Decision Knowledge in JIRA Issue Comments has been set to "
                    + isClassifierUsedForIssues + ".");
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
        }
    }

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.classifyWholeProject = function classifyWholeProject(projectKey) {
		var response = postWithResponseAsReturnValue(AJS.contextPath()
				+ "/rest/decisions/latest/config/classifyWholeProject.json?projectKey=" + projectKey);
		if (response["isSucceeded"]) {
			showFlag("success", "The whole project has been classified.");
			return 1.0;
		}
		showFlag("error", "The classification process failed.");
		return 0.0;
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.trainClassifier = function trainClassifier(projectKey, arffFileName) {
		var response = postWithResponseAsReturnValue(AJS.contextPath()
				+ "/rest/decisions/latest/config/trainClassifier.json?projectKey=" + projectKey + "&arffFileName="
				+ arffFileName);
		console.log(response);
		if (response["isSucceeded"]) {
			showFlag("success", "The classifier was successfully retrained.");
			return 1.0;
		}
		showFlag("error", "Training of the classifier failed.");
		return 0.0;
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.saveArffFile = function saveArffFile(projectKey, callback) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/saveArffFile.json?projectKey=" + projectKey, null,
				function(error, response) {
					if (error === null) {
						showFlag("success", "The ARFF file was successfully created and saved in "
								+ response["arffFile"] + ".");
						console.log(response["content"]);
						callback(response["content"]);
					}
				});
	};

	/*
	 * external references: settingsForSingleProject.vm
	 */
	ConDecAPI.prototype.setIconParsing = function setIconParsing(projectKey, isActivated) {
		postJSON(AJS.contextPath() + "/rest/decisions/latest/config/setIconParsing.json?projectKey=" + projectKey
				+ "&isActivatedString=" + isActivated, null, function(error, response) {
			if (error === null) {
				showFlag("success", "Using icons to tag issue comments has been set to " + isActivated + ".");
			}
		});
	};

	/*
	 * external references: condec.context.menu
	 */
	ConDecAPI.prototype.openJiraIssue = function openJiraIssue(elementId, documentationLocation) {
		this.getDecisionKnowledgeElement(elementId, documentationLocation, function(decisionKnowledgeElement) {
			global.open(decisionKnowledgeElement.url, '_self');
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
            }
        });
    };

    /*
     * external references: condec.context.menu
     */
    ConDecAPI.prototype.openJiraIssue = function openJiraIssue(elementId, documentationLocation) {
        this.getDecisionKnowledgeElement(elementId, documentationLocation, function(decisionKnowledgeElement) {
            global.open(decisionKnowledgeElement.url, '_self');
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
                showFlag("error", xhr.response.error, status);
                callback(status);
            }
        };
        xhr.send();
    }

    function getText(url, callback) {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", url, true);
        xhr.setRequestHeader("Content-type", "plain/text");
        xhr.onload = function() {
            var status = xhr.status;
            if (status === 200) {
                callback(null, xhr.response);
            } else {
                showFlag("error", xhr.response.error, status);
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
                showFlag("error", xhr.response.error, status);
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
                showFlag("error", xhr.response.error, status);
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
                showFlag("error", xhr.response.error, status);
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

    function showFlag(type, message, status) {
        if (status === null || status === undefined) {
            status = "";
        }
        AJS.flag({
            type : type,
            close : "auto",
            title : type.charAt(0).toUpperCase() + type.slice(1) + " " + status,
            body : message
        });
    }

    // export ConDecAPI
    global.conDecAPI = new ConDecAPI();
})(window);
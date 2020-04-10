/*
 This module implements the communication with the ConDec Java REST API and the JIRA API.

 Requires
 * conDecTreant.findParentElement
    
 Is required by
 * conDecContextMenu
 * conDecDialog
 * conDecEvolutionPage
 * conDecTreant
 * conDecTreeViewer
 * conDecJiraIssueModule
 * conDecKnowledgePage
 * conDecTabPanel
 * conDecVis
  
 Is referenced in HTML by
 * settingsForAllProjects.vm 
 * settingsForSingleProject.vm
 */
(function (global) {

    var projectKey = null;

    var ConDecAPI = function ConDecAPI() {
        this.restPrefix = AJS.contextPath() + "/rest/condec/latest";

        projectKey = getProjectKey();
        this.knowledgeTypes = getKnowledgeTypes(projectKey);
        this.extendedKnowledgeTypes = getExtendedKnowledgeTypes(this.knowledgeTypes);

        this.optionStatus = ["idea", "discarded", "decided", "rejected", "undefined"];
        this.issueStatus = ["resolved", "unresolved"];
        this.knowledgeStatus = this.optionStatus.concat(this.issueStatus);

        this.linkTypes = getLinkTypes(projectKey);
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
        getJSON(this.restPrefix + "/knowledge/getDecisionKnowledgeElement.json?projectKey="
            + projectKey + "&id=" + id + "&documentationLocation=" + documentationLocation, function (error,
                                                                                                      decisionKnowledgeElement) {
            if (error === null) {
                callback(decisionKnowledgeElement);
            }
        });
    };

    /*
     * external references: condec.dialog
     */
    ConDecAPI.prototype.getUnlinkedElements = function getUnlinkedElements(id, documentationLocation, callback) {
        getJSON(this.restPrefix + "/knowledge/getUnlinkedElements.json?projectKey="
            + projectKey + "&id=" + id + "&documentationLocation=" + documentationLocation, function (error,
                                                                                                      unlinkedElements) {
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
    ConDecAPI.prototype.createDecisionKnowledgeElement = function createDecisionKnowledgeElement(summary, description,
                                                                                                 type, documentationLocation, idOfExistingElement, documentationLocationOfExistingElement, callback) {
        var newElement = {
            "summary": summary,
            "type": type,
            "projectKey": projectKey,
            "description": description,
            "documentationLocation": documentationLocation
        };
        console.log(newElement);
        postJSON(this.restPrefix + "/knowledge/createDecisionKnowledgeElement.json?idOfExistingElement="
            + idOfExistingElement + "&documentationLocationOfExistingElement="
            + documentationLocationOfExistingElement, newElement, function (error, newElement) {
            if (error === null) {
                showFlag("success", type + " and link have been created.");
                callback(newElement.id);
            }
        });
    };


    ConDecAPI.prototype.assignDecisionGroup = function assignDecisionGroup(level, existingGroups,
                                                                           addgroup, sourceId, documentationLocation, callback) {
        var newElement = {};
        var projectKey = getProjectKey();
        postJSON(this.restPrefix + "/knowledge/assignDecisionGroup.json?sourceId="
            + sourceId + "&documentationLocation="
            + documentationLocation + "&projectKey="
            + projectKey + "&level="
            + level + "&existingGroups="
            + existingGroups + "&addGroup="
            + addgroup, newElement, function (error, newElement) {
            if (error === null) {
                showFlag("Success", "Group Assignments have been created.");
                callback(sourceId);
            }
        });
    };

    /*
     * external references: condec.dialog
     */
    ConDecAPI.prototype.updateDecisionKnowledgeElement = function updateDecisionKnowledgeElement(id, summary,
                                                                                                 description, type, documentationLocation, status, callback) {
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
        postJSON(this.restPrefix + "/knowledge/updateDecisionKnowledgeElement.json?idOfParentElement="
            + parentElement["id"] + "&documentationLocationOfParentElement="
            + parentElement["documentationLocation"], element, function (error, response) {
            if (error === null) {
                showFlag("success", "Decision knowledge element has been updated.");
                callback();
            }
        });
    };
    /*
     * external references: condec.dialog
     */
    ConDecAPI.prototype.getSprintsByProject = function getSprintsByProject() {
        // first we need the boards then we can get the Sprints for each board
        return new Promise(function (resolve, reject) {
            var boardUrl = "/rest/agile/latest/board?projectKeyOrId=" + projectKey;
            var boardPromise = getJSONReturnPromise(AJS.contextPath() + boardUrl);
            boardPromise.then(function (boards) {
                if (boards && boards.values && boards.values.length) {
                    var sprintPromises = boards.values.map(function (board) {
                        var sprintUrl = "/rest/agile/latest/board/" + board.id + "/sprint";
                        return getJSONReturnPromise(AJS.contextPath() + sprintUrl);
                    });
                    Promise.all(sprintPromises)
                        .then(function (sprints) {
                            resolve(sprints);
                        }).catch(function (err) {
                        reject(err);
                    })
                } else {
                    reject("No Boards could be found, so the sprints could also not be loaded");
                }
            }).catch(function (err) {
                reject(err);
            })
        })
    };
    ConDecAPI.prototype.getIssueTypes = function getIssueTypes() {
        // first we need the boards then we can get the Sprints for each board
        return new Promise(function (resolve, reject) {
            var issueTypeUrl = "/rest/api/2/issue/createmeta?expand=projects.issuetypes";
            var issuePromise = getJSONReturnPromise(AJS.contextPath() + issueTypeUrl);
            issuePromise.then(function (result) {
                if (result && result.projects && result.projects.length) {
                    var correctIssueTypes = result.projects.filter(function (project) {
                        return project.key === projectKey;
                    });
                    correctIssueTypes = correctIssueTypes[0].issuetypes;
                    if (correctIssueTypes && correctIssueTypes.length) {
                        resolve(correctIssueTypes);
                    } else {
                        reject("No issue-types could be found for this project");
                    }
                } else {
                    reject("No Projects were found.");
                }

            }).catch(function (err) {
                reject(err);
            })
        })
    };
    ConDecAPI.prototype.getReleases = function getReleases() {
        // first we need the boards then we can get the Sprints for each board
        return new Promise(function (resolve, reject) {
            var issueTypeUrl = "/rest/projects/latest/project/" + projectKey + "/release/allversions";
            var issuePromise = getJSONReturnPromise(AJS.contextPath() + issueTypeUrl);
            issuePromise.then(function (result) {
                if (result && result.length) {
                    resolve(result);
                } else {
                    reject("No Releases were found");
                }
            }).catch(function (err) {
                reject(err);
            })
        })
    };

    ConDecAPI.prototype.getProjectWideSelectedIssueTypes = function getProjectWideSelectedIssueTypes() {
        return new Promise(function (resolve, reject) {
            var preSelectedIssueUrl = AJS.contextPath() + "/rest/condec/latest/config/getReleaseNoteMapping.json?projectKey=" + projectKey;
            var issuePromise = getJSONReturnPromise(preSelectedIssueUrl);
            issuePromise.then(function (result) {
                if (result) {
                    resolve(result);
                } else {
                    reject();
                }
            }).catch(function (err) {
                reject(err);
            })
        })
    };

    /*
     * external references: condec.context.menu, condec.dialog
     */
    ConDecAPI.prototype.changeKnowledgeType = function changeKnowledgeType(id, type, documentationLocation, callback) {
        this.updateDecisionKnowledgeElement(id, null, null, type, documentationLocation, null, callback);
    };

    /*
     * external references: condec.dialog
     */
    ConDecAPI.prototype.deleteDecisionKnowledgeElement = function deleteDecisionKnowledgeElement(id,
                                                                                                 documentationLocation, callback) {
        var element = {
            "id": id,
            "projectKey": projectKey,
            "documentationLocation": documentationLocation
        };
        deleteJSON(this.restPrefix + "/knowledge/deleteDecisionKnowledgeElement.json", element,
            function (error, isDeleted) {
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
                                                         documentationLocationOfParent, documentationLocationOfChild, linkType, callback) {
        postJSON(this.restPrefix + "/knowledge/createLink.json?projectKey=" + projectKey + "&knowledgeTypeOfChild=" + knowledgeTypeOfChild
            + "&idOfParent=" + idOfParent + "&documentationLocationOfParent=" + documentationLocationOfParent + "&idOfChild=" + idOfChild
            + "&documentationLocationOfChild=" + documentationLocationOfChild + "&linkTypeName=" + linkType, null, function (error, link) {
            if (error === null) {
                showFlag("success", "Link has been created.");
                callback(link);
            }
        });
    };

    /*
     * external references: condec.dialog, condec.treant, condec.tree.viewer
     */
    ConDecAPI.prototype.deleteLink = function deleteLink(idOfDestinationElement, idOfSourceElement,
                                                         documentationLocationOfDestinationElement, documentationLocationOfSourceElement, callback) {
        var link = {
            "idOfSourceElement": idOfSourceElement,
            "idOfDestinationElement": idOfDestinationElement,
            "documentationLocationOfSourceElement": documentationLocationOfSourceElement,
            "documentationLocationOfDestinationElement": documentationLocationOfDestinationElement
        };
        deleteJSON(this.restPrefix + "/knowledge/deleteLink.json?projectKey=" + projectKey,
            link, function (error, link) {
                if (error === null) {
                    showFlag("success", "Link has been deleted.");
                    callback(link);
                }
            });
    };

    /*
     * external references: condec.dialog
     */
    ConDecAPI.prototype.setStatus = function setStatus(id, documentationLocation, type, status, callback) {
        this.updateDecisionKnowledgeElement(id, null, null, type, documentationLocation, status, callback);
    };

    /*
     * external references: condec.export
     */
    ConDecAPI.prototype.getElements = function getElements(query, callback) {
        getJSON(this.restPrefix + "/knowledge/getElements.json?projectKey="
            + projectKey + "&query=" + query, function (error, elements) {
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
            "id": id,
            "documentationLocation": "s",
            "projectKey": projectKey
        };
        postJSON(this.restPrefix + "/knowledge/setSentenceIrrelevant.json", jsondata, function (
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
            "id": id,
            "projectKey": projectKey
        };
        postJSON(this.restPrefix + "/knowledge/createIssueFromSentence.json", jsondata,
            function (error, id, type) {
                if (error === null) {
                    showFlag("success", "JIRA Issue has been created");
                    callback();
                }
            });
    };

    /*
     * external references: condec.jira.issue.module
     */
    ConDecAPI.prototype.getSummarizedCode = function getSummarizedCode(id, documentationLocation, probability, callback) {
        // console.log(probability);
        getText(this.restPrefix + "/knowledge/getSummarizedCode?projectKey=" + projectKey
            + "&id=" + id + "&documentationLocation=" + documentationLocation + "&probability=" + probability,
            function (error, summary) {
                if (error === null) {
                    callback(summary);
                }
            });
    };

    /*
     * external references: condec.tree.viewer
     */
    ConDecAPI.prototype.getTreeViewer = function getTreeViewer(rootElementType, callback) {
        getJSON(this.restPrefix + "/view/getTreeViewer.json?projectKey=" + projectKey
            + "&rootElementType=" + rootElementType, function (error, core) {
            if (error === null) {
                callback(core);
            }
        });
    };

    /*
     * external references: condec.treant
     */
    ConDecAPI.prototype.getTreant = function getTreant(elementKey, depthOfTree, searchTerm, callback) {
        getJSON(this.restPrefix + "/view/getTreant.json?&elementKey=" + elementKey
            + "&depthOfTree=" + depthOfTree + "&searchTerm=" + searchTerm, function (error, treant) {
            if (error === null) {
                callback(treant);
            }
        });
    };

    ConDecAPI.prototype.getClassTreant = function getClassTreant(elementKey, depthOfTree, searchTerm, checkboxflag,
                                                                 isIssueView, minLinkNumber, maxLinkNumber, callback) {
        getJSON(this.restPrefix + "/view/getClassTreant.json?&elementKey=" + elementKey
            + "&depthOfTree=" + depthOfTree + "&searchTerm=" + searchTerm + "&checkboxflag=" + checkboxflag
            + "&isIssueView=" + isIssueView + "&minLinkNumber=" + minLinkNumber + "&maxLinkNumber=" + maxLinkNumber, function (error, treant) {
            if (error === null) {
                callback(treant);
            }
        });
    };

    /*
     * external references: condec.vis
     */
    ConDecAPI.prototype.getVis = function getVis(elementKey, searchTerm, callback) {
        this.getVisFiltered(elementKey, null, null, -1, -1, null, null, callback);
    };

    /*
     * external references: condec.vis
     */
    ConDecAPI.prototype.getVisFiltered = function getVisFiltered(elementKey, searchTerm, selectedJiraIssueTypes,
                                                                 createdAfter, createdBefore, linkTypes, documentationLocations, callback) {
        var filterSettings = {
            "projectKey": projectKey,
            "searchString": searchTerm,
            "createdEarliest": createdBefore,
            "createdLatest": createdAfter,
            "documentationLocations": documentationLocations,
            "selectedJiraIssueTypes": selectedJiraIssueTypes,
            "selectedStatus": this.extendedStatus,
            "selectedLinkTypes": linkTypes
        };
        postJSON(this.restPrefix + "/view/getVis.json?elementKey=" + elementKey,
            filterSettings, function (error, vis) {
                if (error === null) {
                    callback(vis);
                }
            });
    };

    /*
     * external reference: condec.evolution.page.js
     */
    ConDecAPI.prototype.getCompareVis = function getCompareVis(created, closed, searchString, knowledgeTypes, status, callback) {
        var filterSettings = {
            "projectKey": projectKey,
            "searchString": searchString,
            "createdEarliest": created,
            "createdLatest": closed,
            "documentationLocations": null,
            "selectedJiraIssueTypes": knowledgeTypes,
            "selectedStatus": status
        };
        postJSON(this.restPrefix + "/view/getCompareVis.json", filterSettings, function (error,
                                                                                         vis) {
            if (error === null) {
                vis.nodes.sort(function (a, b) {
                    if (a.id > b.id) {
                        return 1;
                    }
                    if (a.id < b.id) {
                        return -1;
                    }
                    return 0;
                });
                callback(vis);
            }
        });
    };
    /*
     * external reference: condec.jira.issue.module
     */
    ConDecAPI.prototype.getFilterSettings = function getFilterSettings(elementKey, searchTerm, callback) {
        getJSON(this.restPrefix + "/view/getFilterSettings.json?elementKey=" + elementKey
            + "&searchTerm=" + searchTerm, function (error, filterSettings) {
            if (error === null) {
                callback(filterSettings);
            }
        });
    };

    /*
     * external references: condec.tab.panel
     */
    ConDecAPI.prototype.getTreeViewerForSingleElement = function getTreeViewerForSingleElement(jiraIssueKey, selectedKnowledgeTypes, callback) {
        var filterSettings = {
            "projectKey": projectKey,
            "selectedJiraIssueTypes": selectedKnowledgeTypes
        };
        postJSON(this.restPrefix + "/view/getTreeViewerForSingleElement.json?jiraIssueKey=" + jiraIssueKey, filterSettings, function (error, core) {
            if (error === null) {
                callback(core);
            }
        });
    };

    /*
     * external references: condec.evolution.page
     */
    ConDecAPI.prototype.getEvolutionData = function getEvolutionData(searchString, created, closed, issueTypes, issueStatus, decGroups,
                                                                     callback) {

        var filterSettings = {
            "projectKey": projectKey,
            "searchString": searchString,
            "createdEarliest": created,
            "createdLatest": closed,
            "documentationLocations": null,
            "selectedJiraIssueTypes": issueTypes,
            "selectedStatus": issueStatus,
            "selectedDecGroups": decGroups
        };
        postJSON(this.restPrefix + "/view/getEvolutionData.json", filterSettings, function (
            error, evolutionData) {
            if (error === null) {
                callback(evolutionData);
            }
        });
    };

    /*
     * external references: condec.relationshipMatrix.page
     */
    ConDecAPI.prototype.getDecisionMatrix = function getDecisionMatrix(callback) {
        getJSON(this.restPrefix + "/view/getDecisionMatrix.json?projectKey=" + projectKey, function (error, matrix) {
            if (error == null) {
                callback(matrix);
            }
        });
    };

    /*
     * external references: condec.relationship.page
     */
    ConDecAPI.prototype.getDecisionGraph = function getDecisionGraph(callback) {
        this.getDecisionGraphFiltered(null, "", [], callback);
    };

    /*
     * external references: condec.relationship.page
     */
    ConDecAPI.prototype.getDecisionGraphFiltered = function getDecisionGraphFiltered(selectedLinkTypes, searchString, decGroups, callback) {


        var filterSettings = {
            "projectKey": projectKey,
            "searchString": searchString,
            "createdEarliest": -1,
            "createdLatest": -1,
            "documentationLocations": null,
            "selectedJiraIssueTypes": ["Decision"],
            "selectedStatus": null,
            "selectedLinkTypes": selectedLinkTypes,
            "selectedDecGroups": decGroups
        };

        postJSON(this.restPrefix + "/view/getDecisionGraph.json?", filterSettings, function (error, graph) {
            if (error == null) {
                callback(graph);
            }
        });
    };

    /*
     * external references: settingsForSingleProject.vm,
     * settingsForAllProjects.vm
     */
    ConDecAPI.prototype.setActivated = function setActivated(isActivated, projectKey) {
        postJSON(this.restPrefix + "/config/setActivated.json?projectKey=" + projectKey
            + "&isActivated=" + isActivated, null, function (error, response) {
            if (error === null) {
                showFlag("success", "Plug-in activation for the project has been set to " + isActivated + ".");
            }
        });
    };

    /*
     * external references: condec.text.editor.extension
     */
    ConDecAPI.prototype.isActivated = function isActivated(callback) {
        getJSON(this.restPrefix + "/config/isActivated.json?projectKey=" + projectKey,
            function (error, isActivatedBoolean) {
                if (error === null) {
                    callback(isActivatedBoolean);
                }
            });
    };

    /*
     * external references: settingsForSingleProject.vm,
     * settingsForAllProjects.vm
     */
    ConDecAPI.prototype.setIssueStrategy = function setIssueStrategy(isIssueStrategy, projectKey) {
        postJSON(this.restPrefix + "/config/setIssueStrategy.json?projectKey=" + projectKey
            + "&isIssueStrategy=" + isIssueStrategy, null, function (error, response) {
            if (error === null) {
                showFlag("success", "Strategy has been selected.");
            }
        });
    };

    /*
     * external references: condec.dialog, condec.context.menu
     */
    ConDecAPI.prototype.isIssueStrategy = function isIssueStrategy(callback) {
        getJSON(this.restPrefix + "/config/isIssueStrategy.json?projectKey=" + projectKey,
            function (error, isIssueStrategyBoolean) {
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
        postJSON(this.restPrefix + "/config/setKnowledgeExtractedFromGit.json?projectKey="
            + projectKey + "&isKnowledgeExtractedFromGit=" + isKnowledgeExtractedFromGit, null, function (error,
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
    ConDecAPI.prototype.setPostFeatureBranchCommits = function (checked, projectKey) {
        postJSON(this.restPrefix + "/config/setPostFeatureBranchCommits.json?projectKey="
            + projectKey + "&newSetting=" + checked, null, function (error,
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
    ConDecAPI.prototype.setPostSquashedCommits = function (checked, projectKey) {
        postJSON(this.restPrefix + "/config/setPostSquashedCommits.json?projectKey="
            + projectKey + "&newSetting=" + checked, null, function (error,
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
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.setGitUris = function setGitUris(projectKey, gitUris, defaultBranches) {
        postJSON(this.restPrefix + "/config/setGitUris.json?projectKey=" + projectKey
            + "&gitUris=" + gitUris + "&defaultBranches=" + defaultBranches, null, function (error, response) {
            if (error === null) {
                showFlag("success", "The git URIs for this project have been set.");
            }
        });
    };

    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.setKnowledgeTypeEnabled = function setKnowledgeTypeEnabled(isKnowledgeTypeEnabled,
                                                                                   knowledgeType, projectKey) {
        postJSON(this.restPrefix + "/config/setKnowledgeTypeEnabled.json?projectKey="
            + projectKey + "&knowledgeType=" + knowledgeType + "&isKnowledgeTypeEnabled=" + isKnowledgeTypeEnabled,
            null, function (error, response) {
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
        getJSON(this.restPrefix + "/config/isKnowledgeTypeEnabled.json?knowledgeType="
            + knowledgeType + "&projectKey=" + projectKey, function (error, isKnowledgeTypeEnabled) {
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
        var knowledgeTypes = getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getKnowledgeTypes.json?projectKey=" + projectKey);
        return knowledgeTypes;
    }

    function getLinkTypes(projectKey) {
        var linkTypes = getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getLinkTypes.json?projectKey=" + projectKey);
        if (linkTypes !== null) {
            var linkTypeArray = [];
            for (var link in linkTypes) {
                linkTypeArray.push(link);
            }
            return linkTypeArray;
        }
    }

    ConDecAPI.prototype.getLinkTypes = function getLinkTypes(callback) {
        var projectKey = getProjectKey();
        getJSON(this.restPrefix + "/config/getLinkTypes.json?projectKey=" + projectKey, function (error, linkTypes) {
            if (error === null) {
                callback(linkTypes);
            }
        });
    };

    ConDecAPI.prototype.getDecisionGroups = function getDecisionGroups(id, location, inputExistingGroupsField, selectLevelField, callback) {
        var projectKey = getProjectKey();
        var decisionGroups = getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getDecisionGroups.json?elementId=" + id
            + "&location=" + location + "&projectKey=" + projectKey);
        callback(selectLevelField, inputExistingGroupsField, decisionGroups);
    };

    ConDecAPI.prototype.fillDecisionGroupSelect = function fillDecisionGroupSelect(elementId) {
        var selectGroupField = document.getElementById(elementId);
        getAllDecisionGroups(selectGroupField, function (selectGroupField, groups) {
            if (!(groups === null) && groups.length > 0) {
                selectGroupField.innerHTML = "";
                selectGroupField.insertAdjacentHTML("beforeend", "<option value='High_Level'>High_Level</option>"
                    + "<option value='Medium_Level'>Medium_Level</option>"
                    + "<option value='Realization_Level'>Realization_Level</option>");
                for (var i = 0; i < groups.length; i++) {
                    if (groups[i] !== "High_Level" && groups[i] !== "Medium_Level" && groups[i] !== "Realization_Level") {
                        selectGroupField.insertAdjacentHTML("beforeend", "<option value='" + groups[i] + "'>" + groups[i] + "</option>");
                    }
                }
            } else {
                selectGroupField.innerHTML = "";
            }
        });
    };

    function getAllDecisionGroups(selectGroupField, callback) {
        var projectKey = getProjectKey();
        var decisionGroups = getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getAllDecisionGroups.json?projectKey=" + projectKey);
        callback(selectGroupField, decisionGroups);
    };

    ConDecAPI.prototype.getDecisionGroupTable = function getDecisionGroupTable(callback) {
        var decisionGroups = getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getAllDecisionGroups.json?projectKey=" + projectKey);
        callback(decisionGroups, projectKey);
    }

    ConDecAPI.prototype.renameDecisionGroup = function renameDecisionGroup(oldName, newName, callback) {
        getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/renameDecisionGroup.json?projectKey=" + projectKey
            + "&oldName=" + oldName + "&newName=" + newName);
        callback();
    }

    ConDecAPI.prototype.deleteDecisionGroup = function deleteDecisionGroup(groupName, callback) {
        getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/deleteDecisionGroup.json?projectKey=" + projectKey
            + "&groupName=" + groupName);
        callback();
    }

    /*
     * Replaces argument with pro-argument and con-argument in knowledge types
     * array.
     */
    function getExtendedKnowledgeTypes(knowledgeTypes) {
        var extendedKnowledgeTypes = knowledgeTypes.filter(function (value) {
            return value.toLowerCase() !== "argument";
        });
        extendedKnowledgeTypes.push("Pro-argument");
        extendedKnowledgeTypes.push("Con-argument");
        return extendedKnowledgeTypes;
    }

    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.setUseClassifierForIssueComments = function setUseClassifierForIssueComments(
        isClassifierUsedForIssues, projectKey) {
        postJSON(this.restPrefix + "/config/setUseClassifierForIssueComments.json?projectKey="
            + projectKey + "&isClassifierUsedForIssues=" + isClassifierUsedForIssues, null, function (error,
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
    ConDecAPI.prototype.setWebhookData = function setWebhookData(projectKey, webhookUrl, webhookSecret) {
        postJSON(this.restPrefix + "/config/setWebhookData.json?projectKey=" + projectKey
            + "&webhookUrl=" + webhookUrl + "&webhookSecret=" + webhookSecret, null, function (error, response) {
            if (error === null) {
                showFlag("success", "The webhook for this project has been set.");
            }
        });
    };

    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.setWebhookEnabled = function setWebhookEnabled(isActivated, projectKey) {
        postJSON(this.restPrefix + "/config/setWebhookEnabled.json?projectKey=" + projectKey
            + "&isActivated=" + isActivated, null, function (error, response) {
            if (error === null) {
                showFlag("success", "The webhook activation for this project has been changed.");
            }
        });
    };

    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.setWebhookType = function setWebhookType(webhookType, projectKey, isWebhookTypeEnabled) {
        postJSON(this.restPrefix + "/config/setWebhookType.json?projectKey=" + projectKey
            + "&webhookType=" + webhookType + "&isWebhookTypeEnabled=" + isWebhookTypeEnabled, null, function (
            error, response) {
            if (error === null) {
                showFlag("success", "The webhook root element type was changed for this project.");
            }
        });
    };

    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.setReleaseNoteMapping = function setReleaseNoteMapping(releaseNoteCategory, projectKey, selectedIssueTypes) {
        postJSON(this.restPrefix + "/config/setReleaseNoteMapping.json?projectKey=" + projectKey + "&releaseNoteCategory=" + releaseNoteCategory, selectedIssueTypes, function (
            error, response) {
            if (error === null) {
                showFlag("success", "The associated Jira issue types for the category: " + releaseNoteCategory + " were changed for this project.");
            }
        });
    };

    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.cleanDatabases = function cleanDatabases(projectKey) {
        postJSON(this.restPrefix + "/config/cleanDatabases.json?projectKey="
            + projectKey, null, function (error, response) {
            if (error === null) {
                showFlag("success", "The databases have been cleaned.");
            }
        });
    };

    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.setUseClassifierForIssueComments = function setUseClassifierForIssueComments(
        isClassifierUsedForIssues, projectKey) {
        postJSON(this.restPrefix + "/config/setUseClassifierForIssueComments.json?projectKey="
            + projectKey + "&isClassifierUsedForIssues=" + isClassifierUsedForIssues, null, function (error,
                                                                                                      response) {
            if (error === null) {
                showFlag("success",
                    "Usage of classification for Decision Knowledge in JIRA Issue Comments has been set to "
                    + isClassifierUsedForIssues + ".");
            }
        });
    };

    /*
     * external references: classificationSettings.vm
     */
    ConDecAPI.prototype.testClassifierWithText = function testClassifierWithText(
        text, projectKey, resultDomElement) {
        postJSON(this.restPrefix + "/config/testClassifierWithText?projectKey="
            + projectKey + "&text=" + text, null, function (error, response) {
            if (error === null) {
                resultDomElement.innerText = response.content;
            } else {
                resultDomElement.innerText = "Error! Please check if the classifier is trained.";
            }
        });
    };


    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.classifyWholeProject = function classifyWholeProject(projectKey, animatedElement) {
        animatedElement.classList.add("aui-progress-indicator-value");
        postJSON(this.restPrefix + "/config/classifyWholeProject.json?projectKey=" + projectKey,
            null,
            function (error, response) {
                animatedElement.classList.remove("aui-progress-indicator-value");

                if (error === null) {
                    showFlag("success", "The whole project has been classified.");
                } else {
                    showFlag("error", "The classification process failed.");
                }
            });

    };

    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.trainClassifier = function trainClassifier(projectKey, arffFileName, animatedElement) {
        animatedElement.classList.add("aui-progress-indicator-value");

        postJSON(this.restPrefix + "/config/trainClassifier.json?projectKey=" + projectKey + "&arffFileName="
            + arffFileName,
            null,
            function (error, response) {
                animatedElement.classList.remove("aui-progress-indicator-value");
                if (error === null) {
                    showFlag("success", "The classifier was successfully retrained.");
                } else {
                    showFlag("error", "Training of the classifier failed.");
                }
            });
    };


    ConDecAPI.prototype.evaluateModel = function evaluateModel(projectKey, animatedElement, callback) {
        // console.log("ConDecAPI.prototype.evaluateModel");
        animatedElement.classList.add("aui-progress-indicator-value");
        postJSON(this.restPrefix + "/config/evaluateModel.json?projectKey=" + projectKey,
            null,
            function (error, response) {
                animatedElement.classList.remove("aui-progress-indicator-value");
                if (error === null) {
                    showFlag("success", "The evaluation results file was successfully created.");
                    // //console.log(response["content"]);
                    callback(response["content"]);
                }
            });
    };
    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.saveArffFile = function saveArffFile(projectKey, useOnlyValidatedData, callback) {
        postJSON(this.restPrefix + "/config/saveArffFile.json?projectKey=" + projectKey + "&useOnlyValidatedData=" + useOnlyValidatedData, null,
            function (error, response) {
                if (error === null) {
                    showFlag("success", "The ARFF file was successfully created and saved in "
                        + response["arffFile"] + ".");
                    // console.log(response["content"]);
                    callback(response["content"]);
                }
            });
    };

    /*
     * external references: settingsForSingleProject.vm
     */
    ConDecAPI.prototype.setIconParsing = function setIconParsing(projectKey, isActivated) {
        postJSON(this.restPrefix + "/config/setIconParsing.json?projectKey=" + projectKey
            + "&isActivatedString=" + isActivated, null, function (error, response) {
            if (error === null) {
                showFlag("success", "Using icons to tag issue comments has been set to " + isActivated + ".");
            }
        });
    };

    /*
     * external references: condec.context.menu
     */
    ConDecAPI.prototype.openJiraIssue = function openJiraIssue(elementId, documentationLocation) {
        this.getDecisionKnowledgeElement(elementId, documentationLocation, function (decisionKnowledgeElement) {
            global.open(decisionKnowledgeElement.url, '_self');
        });
    };
    /*
     * external references: condec.release.note.page
     */
    ConDecAPI.prototype.getReleaseNotes = function getReleaseNotes(callback) {
        var projectKey = getProjectKey();
        getJSON(this.restPrefix + "/release-note/getReleaseNotes.json?projectKey="
            + projectKey, function (error, elements) {
            if (error === null) {
                callback(elements);
            }
        });
    };
    /*
     * external references: condec.dialog
     */
    ConDecAPI.prototype.getProposedIssues = function getReleaseNotes(releaseNoteConfiguration) {
        return postJSONReturnPromise(this.restPrefix + "/release-note/getProposedIssues.json?projectKey="
            + projectKey, releaseNoteConfiguration);
    };

    ConDecAPI.prototype.postProposedKeys = function postProposedKeys(proposedKeys) {
        return postJSONReturnPromise(this.restPrefix + "/release-note/postProposedKeys.json?projectKey="
            + projectKey, proposedKeys);
    };
    ConDecAPI.prototype.createReleaseNote = function createReleaseNote(content) {
        return postJSONReturnPromise(this.restPrefix + "/release-note/createReleaseNote.json?projectKey="
            + projectKey, content);
    };
    ConDecAPI.prototype.updateReleaseNote = function updateReleaseNote(releaseNote) {
        return postJSONReturnPromise(this.restPrefix + "/release-note/updateReleaseNote.json?projectKey="
            + projectKey, releaseNote)
    };
    ConDecAPI.prototype.deleteReleaseNote = function deleteReleaseNote(id) {
        return deleteJSONReturnPromise(this.restPrefix + "/release-note/deleteReleaseNote.json?projectKey="
            + projectKey + "&id=" + id, null);
    };


    ConDecAPI.prototype.getReleaseNotesById = function getReleaseNotesById(id) {
        return getJSONReturnPromise(this.restPrefix + "/release-note/getReleaseNote.json?projectKey="
            + projectKey + "&id=" + id);

    };
    ConDecAPI.prototype.getAllReleaseNotes = function getAllReleaseNotes(query) {
        return getJSONReturnPromise(this.restPrefix + "/release-note/getAllReleaseNotes.json?projectKey="
            + projectKey + "&query=" + query);
    };

    function getResponseAsReturnValue(url) {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", url, false);
        xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
        xhr.send();
        return JSON.parse(xhr.response);
    }

    // @Deprecated
    function _postWithResponseAsReturnValue(url) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", url, false);
        xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
        xhr.send();
        return JSON.parse(xhr.response);
    }

    function getJSON(url, callback) {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", url, true);
        xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
        xhr.setRequestHeader("Accept", "application/json");
        xhr.responseType = "json";
        xhr.onload = function () {
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

    function getJSONReturnPromise(url) {
        return new Promise(function (resolve, reject) {
            getJSON(url, function (err, result) {
                if (err === null) {
                    resolve(result);
                } else {
                    reject(err);
                }
            });
        });

    }

    function postJSONReturnPromise(url, data) {
        return new Promise(function (resolve, reject) {
            postJSON(url, data, function (err, result) {
                if (err === null) {
                    resolve(result);
                } else {
                    reject(err);
                }
            })
        })
    }

    function deleteJSONReturnPromise(url, data) {
        return new Promise(function (resolve, reject) {
            deleteJSON(url, data, function (err, result) {
                if (err === null) {
                    resolve(result);
                } else {
                    reject(err);
                }
            })
        })
    }


    function getText(url, callback) {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", url, true);
        xhr.setRequestHeader("Content-type", "plain/text");
        xhr.onload = function () {
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

    function postJSON(url, data, callback) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
        xhr.setRequestHeader("Accept", "application/json");
        xhr.responseType = "json";
        xhr.onload = function () {
            var status = xhr.status;
            if (status === 200) {
                callback(null, xhr.response);
            } else {
                showFlag("error", xhr.response.error || "Unknown Error!", status);
                callback(status, xhr.response);
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
        xhr.onload = function () {
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
        xhr.onload = function () {
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
            // console.log("conDecAPI could not getIssueKey using object AJS!");
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
     * external references: condec.jira.issue.module, condec.export,
     * condec.gitdiffviewer
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

    // export ConDecAPI
    global.conDecAPI = new ConDecAPI();
})(window);
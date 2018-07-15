/*
 * Knowledge types are a subset of "Alternative", "Argument", "Assessment",
 * "Assumption", "Claim", "Constraint", "Context", "Decision",
 * "Goal", "Implication", "Issue", "Problem", and "Solution".
 */
var knowledgeTypes = getKnowledgeTypes(getProjectKey());
/*
 * Default knowledge types are "Alternative", "Argument", "Decision", and
 * "Issue".
 */
var defaultKnowledgeTypes = getDefaultKnowledgeTypes(getProjectKey());
var extendedKnowledgeTypes = replaceArgumentWithLinkTypes(knowledgeTypes);

function replaceArgumentWithLinkTypes(knowledgeTypes) {
	var extendedKnowledgeTypes = getKnowledgeTypes(getProjectKey());
	remove(extendedKnowledgeTypes, "Argument");
	extendedKnowledgeTypes.push("Pro");
	extendedKnowledgeTypes.push("Contra");
	return extendedKnowledgeTypes;
}

function createDecisionKnowledgeElementAsChild(summary, description, type, parentId) {
	switch (type) {
	case "Pro":
		createDecisionKnowledgeElement(summary, description, "Argument", function(childId) {
			linkElements(childId, parentId, "support", function() {
				updateView();
			});
		});
		break;
	case "Contra":
		createDecisionKnowledgeElement(summary, description, "Argument", function(childId) {
			linkElements(childId, parentId, "attack", function() {
				updateView();
			});
		});
		break;
	default:
		createDecisionKnowledgeElement(summary, description, type, function(childId) {
			linkElements(parentId, childId, "contain", function() {
				updateView();
			});
		});
	}
}

function updateDecisionKnowledgeElementAsChild(childId, summary, description, type) {
	switch (type) {
	case "Pro":
		updateDecisionKnowledgeElement(childId, summary, description, "Argument", function() {
			var parentId = findParentId(childId);
			deleteLink(parentId, childId, function() {
				linkElements(childId, parentId, "support", function() {
					updateView();
				});
			});
		});
		break;
	case "Contra":
		updateDecisionKnowledgeElement(childId, summary, description, "Argument", function() {
			var parentId = findParentId(childId);
			deleteLink(parentId, childId, function() {
				linkElements(childId, parentId, "attack", function() {
					updateView();
				});
			});
		});
		break;
	default:
		updateDecisionKnowledgeElement(childId, summary, description, type, function() {
			getDecisionKnowledgeElement(childId, function(decisionKnowledgeElement) {
				if (decisionKnowledgeElement.type !== type) {
					var parentId = findParentId(childId);
					deleteLink(parentId, childId, function() {
						linkElements(parentId, childId, "contain", function() {
							updateView();
						});
					});
				} else {
					updateView();
				}
			});
		});
		break;
	}
}

function createLinkToExistingElement(parentId, childId, knowledgeTypeOfChild) {
	switch (knowledgeTypeOfChild) {
	case "Pro":
		linkElements(childId, parentId, "support", function() {
			updateView();
		});
		break;
	case "Contra":
		linkElements(childId, parentId, "attack", function() {
			updateView();
		});
		break;
	default:
		linkElements(parentId, childId, "contain", function() {
			updateView();
		});
	}
}

function getProjectKey() {
	return JIRA.API.Projects.getCurrentProjectKey();
}

function getProjectId() {
	return JIRA.API.Projects.getCurrentProjectId();
}

function getIssueKey() {
	var issueKey = JIRA.Issue.getIssueKey();
	if (issueKey === null) {
		issueKey = AJS.Meta.get("issue-key");
	}
	return issueKey;
}

function showFlag(type, message) {
	AJS.flag({
		type : type,
		close : "auto",
		title : type.charAt(0).toUpperCase() + type.slice(1),
		body : message
	});
}

function remove(array, item) {
	for (var i = array.length; i--;) {
		if (array[i] === item) {
			array.splice(i, 1);
		}
	}
}
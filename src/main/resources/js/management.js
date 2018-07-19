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
	extendedKnowledgeTypes.push("Pro-argument");
	extendedKnowledgeTypes.push("Con-argument");
	return extendedKnowledgeTypes;
}

function createLinkToExistingElement(idOfDestinationElement, idOfSourceElement, knowledgeTypeOfChild) {
	switchLinkTypes(knowledgeTypeOfChild, idOfDestinationElement, idOfSourceElement, function(linkType,
			idOfDestinationElement, idOfSourceElement) {
		linkElements(idOfDestinationElement, idOfSourceElement, linkType, function() {
			updateView();
		});
	});
}

function switchLinkTypes(type, idOfDestinationElement, idOfSourceElement, linkTypeFunction) {
	switch (type) {
	case "Pro-argument":
		linkTypeFunction("support", idOfSourceElement, idOfDestinationElement);
		break;
	case "Con-argument":
		linkTypeFunction("attack", idOfSourceElement, idOfDestinationElement);
		break;
	default:
		linkTypeFunction("contain", idOfDestinationElement, idOfSourceElement);
	}
}

function updateDecisionKnowledgeElementAsChild(childId, summary, description, type) {
	var simpleType = getSimpleType(type);
	updateDecisionKnowledgeElement(childId, summary, description, simpleType, function() {
		getDecisionKnowledgeElement(childId, function(decisionKnowledgeElement) {
			if (decisionKnowledgeElement.type !== type) {
				var parentId = findParentId(childId);
				switchLinkTypes(type, parentId, childId, function(linkType, parentId, childId) {
					deleteLink(parentId, childId, function() {
						linkElements(parentId, childId, linkType, function() {
							updateView();
						});
					});
				});
			} else {
				updateView();
			}
		});
	});
}

function getSimpleType(type) {
	var simpleType = type;
	if (type === "Pro-argument" || type === "Con-argument") {
		simpleType = "Argument";
	}
	return simpleType;
}

function createDecisionKnowledgeElementAsChild(summary, description, type, idOfDestinationElement) {
	var simpleType = getSimpleType(type);
	createDecisionKnowledgeElement(summary, description, simpleType, function(idOfSourceElement) {
		switchLinkTypes(type, idOfDestinationElement, idOfSourceElement, function(linkType, idOfDestinationElement,
				idOfSourceElement) {
			linkElements(idOfDestinationElement, idOfSourceElement, linkType, function() {
				updateView();
			});
		});
	});
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
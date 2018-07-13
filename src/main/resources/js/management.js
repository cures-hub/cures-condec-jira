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
	extendedKnowledgeTypes.push("Pro Argument");
	extendedKnowledgeTypes.push("Contra Argument");
	extendedKnowledgeTypes.push("Comment");
	return extendedKnowledgeTypes;
}

function createDecisionKnowledgeElementAsChild(summary, description, type, parentId) {
	switch (type) {
	case "Pro Argument":
		createDecisionKnowledgeElement(summary, description, "Argument", function(childId) {
			linkElements(childId, parentId, "support", function() {
//				updateView(childId);
			});
		});
		break;
	case "Contra Argument":
		createDecisionKnowledgeElement(summary, description, "Argument", function(childId) {
			linkElements(childId, parentId, "attack", function() {
//				updateView(childId);
			});
		});
		break;
	case "Comment":
		createDecisionKnowledgeElement(summary, description, "Argument", function(childId) {
			linkElements(childId, parentId, "comment", function() {
//				updateView(childId);
			});
		});
		break;
	default:
		createDecisionKnowledgeElement(summary, description, type, function(childId) {
			linkElements(parentId, childId, "contain", function(link) {
				console.log(link);
//				updateView(childId);
			});
		});
	}
}

function editDecisionKnowledgeElementAsChild(summary, description, type, childId) {
	switch (type) {
	case "Pro Argument":
		editDecisionKnowledgeElement(childId, summary, description, "Argument", function() {
			getLinkedDecisionComponents(childId, function(parentElement) {
				for (var counter = 0; counter < parentElement.length; counter++) {
					var parentId = parentElement[counter].id;
					editLink(childId, parentId, "support", function() {
						updateView(parentId);
					});
				}
			});
		});
		break;
	case "Contra Argument":
		editDecisionKnowledgeElement(childId, summary, description, "Argument", function() {
			getLinkedDecisionComponents(childId, function(parentElement) {
				for (var counter = 0; counter < parentElement.length; counter++) {
					var parentId = parentElement[counter].id;
					editLink(childId, parentId, "attack", function() {
						updateView(parentId);
					});
				}
			});
		});
		break;
	case "Comment":
		editDecisionKnowledgeElement(childId, summary, description, "Argument", function() {
			getLinkedDecisionComponents(childId, function(parentElement) {
				for (var counter = 0; counter < parentElement.length; counter++) {
					var parentId = parentElement[counter].id;
					editLink(childId, parentId, "comment", function() {
						updateView(parentId);
					});
				}
			});
		});
		break;
	default:
		editDecisionKnowledgeElement(childId, summary, description, type, function() {
			getLinkedDecisionComponents(childId, function(parentElement) {
				for (var counter = 0; counter < parentElement.length; counter++) {
					var parentId = parentElement[counter].id;
					editLink(parentId, childId, "contain", function() {
						updateView(parentId);
					});
				}
			});
		});
	}
}

function createLinkToExistingElement(parentId, childId) {
	getDecisionKnowledgeElement(childId, function(decisionKnowledgeElement) {
		var type = decisionKnowledgeElement.type;
		switch (type) {
		case "Pro Argument":
			linkElements(childId, parentId, "support", function() {
				updateView(parentId);
			});
			break;
		case "Contra Argument":
			linkElements(childId, parentId, "attack", function() {
				updateView(parentId);
			});
			break;
		case "Comment":
			linkElements(childId, parentId, "comment", function() {
				updateView(parentId);
			});
			break;
		default:
			linkElements(parentId, childId, "contain", function() {
				updateView(parentId);
			});
		}
	});
}

function deleteLinkToExistingElement(parentId, childId) {
	getDecisionKnowledgeElement(childId, function(decisionKnowledgeElement) {
		var type = decisionKnowledgeElement.type;
		switch (type) {
		case "Pro Argument":
			deleteLink(childId, parentId, "support", function() {
				updateView(childId);
			});
			break;
		case "Contra Argument":
			deleteLink(childId, parentId, "attack", function() {
				updateView(childId);
			});
			break;
		case "Comment":
			deleteLink(childId, parentId, "comment", function() {
				updateView(childId);
			});
			break;
		default:
			deleteLink(parentId, childId, "contain", function() {
				updateView(childId);
			});
		}
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
	if (issueKey == null) {
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

function clearInner(node) {
	while (node.hasChildNodes()) {
		clear(node.firstChild);
	}
}

function clear(node) {
	while (node.hasChildNodes()) {
		clear(node.firstChild);
	}
	node.parentNode.removeChild(node);
}
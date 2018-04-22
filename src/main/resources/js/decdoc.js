function getProjectKey() {
	var pathname = window.location.pathname;
	var stringArray = pathname.split("/");
	return stringArray[stringArray.length - 1];
}

function createDecisionKnowledgeElementAsChild(summary, description, type, parentId) {
	// TODO: Enable to show arguments. They are currently not shown due to
	// an inward-outward link problem.
	switch (type) {
	case "Pro Argument":
		createDecisionKnowledgeElement(summary, description, "Argument", function(childId) {
			createLink(childId, parentId, "support", function() {
				buildTreeViewer(getProjectKey(), childId);
			});
		});
		break;
	case "Contra Argument":
		createDecisionKnowledgeElement(summary, description, "Argument", function(childId) {
			createLink(childId, parentId, "attack", function() {
				buildTreeViewer(getProjectKey(), childId);
			});
		});
		break;
	case "Comment":
		createDecisionKnowledgeElement(summary, description, "Argument", function(childId) {
			createLink(childId, parentId, "comment", function() {
				buildTreeViewer(getProjectKey(), childId);
			});
		});
		break;
	default:
		createDecisionKnowledgeElement(summary, description, type, function(childId) {
			createLink(parentId, childId, "contain", function() {
				buildTreeViewer(getProjectKey(), childId);
			});
		});
	}
}

function editDecisionKnowledgeElementAsChild(summary, description, type, parentId) {
	// TODO: Links need to be updated when the type is changed to an argument
	// (or from an argument to a different type), first we need a getLink method
	switch (type) {
	case "Pro Argument":
		editDecisionKnowledgeElement(parentId, summary, description, "Argument", function() {
			buildTreeViewer(getProjectKey(), parentId);
		});
		break;
	case "Contra Argument":
		editDecisionKnowledgeElement(parentId, summary, description, "Argument", function() {
			buildTreeViewer(getProjectKey(), parentId);
		});
		break;
	case "Comment":
		editDecisionKnowledgeElement(parentId, summary, description, "Argument", function() {
			buildTreeViewer(getProjectKey(), parentId);
		});
		break;
	default:
		editDecisionKnowledgeElement(parentId, summary, description, type, function() {
			buildTreeViewer(getProjectKey(), parentId);
		});
	}
}

function createLinkToExistingElement(parentId, childId) {
	getDecisionKnowledgeElement(childId, getProjectKey(), function(decisionKnowledgeElement) {
		var type = decisionKnowledgeElement.type;
		switch (type) {
		case "Pro Argument":
			createLink(childId, parentId, "support", function() {
				buildTreeViewer(getProjectKey(), childId);
			});
			break;
		case "Contra Argument":
			createLink(childId, parentId, "attack", function() {
				buildTreeViewer(getProjectKey(), childId);
			});
			break;
		case "Comment":
			createLink(childId, parentId, "comment", function() {
				buildTreeViewer(getProjectKey(), childId);
			});
			break;
		default:
			createLink(parentId, childId, "contain", function() {
				buildTreeViewer(getProjectKey(), childId);
			});
		}
	});
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

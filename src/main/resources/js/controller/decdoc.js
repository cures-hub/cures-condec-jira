function getProjectKey() {
	var pathname = window.location.pathname;
	var stringArray = pathname.split("/");
	return stringArray[stringArray.length - 1];
}

function createDecisionKnowledgeElementAsChild(summary, description, type, parentId) {
	switch (type) {
	case "Pro Argument":
		createDecisionKnowledgeElement(summary, description, "Argument", function(childId) {
			createLink(childId, parentId, "support", function() {
				updateView(childId);
			});
		});
		break;
	case "Contra Argument":
		createDecisionKnowledgeElement(summary, description, "Argument", function(childId) {
			createLink(childId, parentId, "attack", function() {
				updateView(childId);
			});
		});
		break;
	case "Comment":
		createDecisionKnowledgeElement(summary, description, "Argument", function(childId) {
			createLink(childId, parentId, "comment", function() {
				updateView(childId);
			});
		});
		break;
	default:
		createDecisionKnowledgeElement(summary, description, type, function(childId) {
			createLink(parentId, childId, "contain", function() {
				updateView(childId);
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
            getLinkedDecisionComponents(parentId, function(childElements) {
                for (var counter = 0; counter < childElements.length; counter++) {
                    deleteLinkToExistingElement(childElements[counter].id, parentId);
                    createLink(parentId, childElements[counter].id,  "support", function() {
                        updateView(parentId);
                    });
                }
            });
		});
		break;
	case "Contra Argument":
		editDecisionKnowledgeElement(parentId, summary, description, "Argument", function() {
            getLinkedDecisionComponents(parentId, function(childElements) {
                for (var counter = 0; counter < childElements.length; counter++) {
                    deleteLinkToExistingElement(childElements[counter].id, parentId);
                    createLink(parentId, childElements[counter].id,  "attack", function() {
                        updateView(parentId);
                    });
                }
            });
		});
		break;
	case "Comment":
		editDecisionKnowledgeElement(parentId, summary, description, "Argument", function() {
            getLinkedDecisionComponents(parentId, function(childElements) {
                for (var counter = 0; counter < childElements.length; counter++) {
                    deleteLinkToExistingElement(childElements[counter].id, parentId);
                    createLink(parentId, childElements[counter].id,  "comment", function() {
                        updateView(parentId);
                    });
                }
            });
		});
		break;
	default:
		editDecisionKnowledgeElement(parentId, summary, description, type, function() {
            getLinkedDecisionComponents(parentId, function(childElements) {
                for (var counter = 0; counter < childElements.length; counter++) {
                    deleteLinkToExistingElement(childElements[counter].id, parentId);
                    createLink(parentId, childElements[counter].id, "contain", function() {
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
			createLink(childId, parentId, "support", function() {
				updateView(parentId);
			});
			break;
		case "Contra Argument":
			createLink(childId, parentId, "attack", function() {
				updateView(parentId);
			});
			break;
		case "Comment":
			createLink(childId, parentId, "comment", function() {
				updateView(parentId);
			});
			break;
		default:
			createLink(parentId, childId, "contain", function() {
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

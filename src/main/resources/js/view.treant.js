// Global variable for DnD
var dragId;
var oldParentId;
var treantTree;

function buildTreant(decisionKnowledgeElement) {
	var depthOfTree = document.getElementById("depth-of-tree-input").value;
	getTreant(decisionKnowledgeElement.key, depthOfTree, function(treant) {
		document.getElementById("treant-container").innerHTML = "";
		treantTree = new Treant(treant);
		createContextMenuForTreantNodes();
		addDragAndDropSupportForTreant();
	});
}

function createContextMenuForTreantNodes() {
	$(function() {
		$.contextMenu({
			selector : ".decision, .rationale, .context, .problem, .solution, .support, .attack",
			items : contextMenuActions
		});
	});
}

function addDragAndDropSupportForTreant() {
	var treantNodes = document.getElementsByClassName("node");
	var i;
	for (i = 0; i < treantNodes.length; i++) {
		treantNodes[i].draggable = true;
		treantNodes[i].addEventListener("dragstart", function(event) {
			drag(event);
		});
		treantNodes[i].addEventListener("drop", function(event) {
			drop(event, this);
		});
	}
	var nodeDesc = document.getElementsByClassName("node-desc");
	for (i = 0; i < nodeDesc.length; i++) {
		nodeDesc[i].addEventListener("dragover", allowDrop, false);
	}
}

function drag(event) {
	dragId = event.target.id;
	event.dataTransfer.setData("text", dragId);
	oldParentId = findParentId(dragId);
}

function findParentId(elementId) {
	var nodes = treantTree.tree.nodeDB.db;
	var i;
	for (i = 0; i < nodes.length; i++) {
		if (nodes[i].nodeHTMLid == elementId) {
			var parentNode = treantTree.tree.getNodeDb().get(nodes[i].parentId);
			var parentId = parentNode.nodeHTMLid;
			return parentId;
		}
	}
}

function drop(event, target) {
	event.preventDefault();
	var parentId = target.id;
	var childId = dragId;
	deleteLinkToExistingElement(oldParentId, childId);
	createLinkToExistingElement(parentId, childId);
}

function allowDrop(event) {
	event.preventDefault();
}
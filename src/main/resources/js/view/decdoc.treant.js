// Global variable for DnD
var dragId;
var treant;

function buildTreant(decisionKnowledgeElement) {
	var depthOfTree = document.getElementById("depthOfTreeInput").value;
	getTreant(decisionKnowledgeElement.key, depthOfTree, function(treant) {
		document.getElementById("treant-container").innerHTML = "";
		treant = new Treant(treant);
		createContextMenuForTreantNodes();
		addDragAndDropSupportForTreant();
	});
}

function createContextMenuForTreantNodes() {
	$(function() {
		$.contextMenu({
			selector : ".decision, .rationale, .context, .problem, .solution",
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
	console.log("Child Id: " + dragId);
	//var node = treant.tree.getNodeDb().get(dragId);
}

function drop(event, target) {
	event.preventDefault();
	var parentId = target.id;
	var childId = dragId;
	//deleteLinkToExistingElement(oldParentId, nodeId);
	createLinkToExistingElement(parentId, childId);
}

function allowDrop(event) {
	event.preventDefault();
}
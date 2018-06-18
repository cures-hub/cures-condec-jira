// Global variable for DnD
var dragId;
var oldParentId;
var treantTree;

function buildTreant(decisionKnowledgeElement) {
	var depthOfTree = document.getElementById("depthOfTreeInput").value;
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
	findOldParentNode(dragId);
	console.log(dragId +" "+ oldParentId);
}

function drop(event, target) {
	event.preventDefault();
	var parentId = target.id;
	var childId = dragId;
	deleteLinkToExistingElement(oldParentId, childId);
	createLinkToExistingElement(parentId, childId);
}

function findOldParentNode(dragId) {
	var nodes = treantTree.tree.nodeDB.db;
    var i;
    for (i = 0; i < nodes.length; i++) {
	if(nodes[i].nodeHTMLid == dragId){
		var parentElement = treantTree.tree.getNodeDb().get(nodes[i].parentId);
		oldParentId = parentElement.nodeHTMLid;
		}
    }
}

function allowDrop(event) {
	event.preventDefault();
}
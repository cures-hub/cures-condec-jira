// Global variable for DnD
var dragId;

function buildTreant(decisionKnowledgeElement) {
	var depthOfTree = document.getElementById("depthOfTreeInput").value;
	getTreant(decisionKnowledgeElement.key, depthOfTree, function(treant) {
		document.getElementById("treant-container").innerHTML = "";
		new Treant(treant);
		createContextMenuForTreantNodes();
		addingDragAndDropSupport();
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

function addingDragAndDropSupport() {
	var nodeDivs = document.getElementsByClassName("node");
	var i;
	for (i = 0; i < nodeDivs.length; i++) {
		nodeDivs[i].draggable = true;
		nodeDivs[i].addEventListener('dragstart', drag, false);
		nodeDivs[i].addEventListener('drop', function(event) {
			drop(event, this);
		});
	}
	var nodeDesc = document.getElementsByClassName("node-desc");
	for (i = 0; i < nodeDesc.length; i++) {
		nodeDesc[i].addEventListener('dragover', allowDrop, false);
	}
}

function drag(event) {
	event.dataTransfer.setData("text", event.target.id);
	dragId = event.target.id;
}

function drop(event, target) {
	event.preventDefault();
	var parentId = target.id;
	var childId = dragId;
	createLinkToExistingElement(parentId, childId);
}

function allowDrop(event) {
	event.preventDefault();
}
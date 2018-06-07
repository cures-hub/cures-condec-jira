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
	for(i=0; i<nodeDivs.length; i++) {
        nodeDivs[i].draggable = true;
        nodeDivs[i].classList.add("drop");
        nodeDivs[i].addEventListener('dragstart', drag, false);
        nodeDivs[i].addEventListener('drop',drop, false);
        nodeDivs[i].addEventListener('dragover', allowDrop, false);
    }
}

function drag(event) {
    event.dataTransfer.setData("text", event.target.id);
}

function drop(event){
	event.preventDefault();
	console.log("Drop");
}

function  allowDrop(event) {
	event.preventDefault();
}



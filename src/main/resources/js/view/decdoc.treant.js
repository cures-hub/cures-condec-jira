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
	for(i=0; i<nodeDivs.length; i++){
		nodeDivs[i].draggable=true;
		nodeDivs[i].ondragend="drag(event)";
	}
}
//TODO Getting this function called instead of the ondragend default function
function drag(event) {
	event.preventDefault();
	console.log(event);

}
function buildTreant(projectKey, node) {
	var depthOfTree = document.getElementById("depthOfTreeInput").value;
	var treantUrl = getTreantUrl(projectKey, node.key, depthOfTree);
	getJSON(
			treantUrl,
			function(error, treant) {
				if (error != null) {
					document.getElementById("treant-container").innerHTML = "Treant data could not be received. Error-Code: "
							+ error;
				} else {
					document.getElementById("treant-container").innerHTML = "";
					new Treant(treant);
					var modal = document.getElementById('ContextMenuModal');
					// add click-handler for elements in modal to close modal window
					var elementsWithCloseFunction = document.getElementsByClassName("modal-close");
					for (var counter = 0; counter < elementsWithCloseFunction.length; counter++) {
						elementsWithCloseFunction[counter].onclick = function() {
							closeModal();
						}
					}
					// close modal window if user clicks anywhere outside of the modal
					window.onclick = function(event) {
						if (event.target == modal) {
							closeModal();
						}
					};
					createContextMenuForTreeNodes(projectKey);
				}
			});
}

function createContextMenuForTreeNodes(projectKey) {
	$(function() {
		$.contextMenu({
			selector : '.decision, .rationale, .context, .problem, .solution',
			items : contextMenuActions
		});
	});
}
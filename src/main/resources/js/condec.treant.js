(function(global) {

	// closure locals variable for DnD
	var dragId;
	var oldParentId;
	var treantTree;

	var draggedElement;

	var ConDecTreant = function ConDecTreant() {
	};

	ConDecTreant.prototype.buildTreant = function buildTreant(elementKey, isInteractive, searchTerm) {
		console.log("view.treant.js buildTreant");
		var depthOfTree = getDepthOfTree();
		conDecAPI.getTreant(elementKey, depthOfTree, searchTerm, function(treeStructure) {
			document.getElementById("treant-container").innerHTML = "";
			treantTree = new Treant(treeStructure);
			if (isInteractive !== undefined && isInteractive) {
				addContextMenuToTreant();
				addDragAndDropSupportForTreant();
				addTooltip();
			}
		});
	};

	function getDepthOfTree() {
		console.log("view.treant.js getDepthOfTree");
		var depthOfTreeInput = document.getElementById("depth-of-tree-input");
		var depthOfTree = 4;
		if (depthOfTreeInput !== null) {
			depthOfTree = depthOfTreeInput.value;
		}
		return depthOfTree;
	}

	function addDragAndDropSupportForTreant() {
		console.log("view.treant.js addDragAndDropSupportForTreant");
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

	function getCurrentRootElement() {
		console.log("view.treant.js getCurrentRootElement");
		if (treantTree) {
			return treantTree.tree.initJsonConfig.graph.rootElement;
		}
	}

	function drag(event) {
		dragId = event.target.id;
		draggedElement = event.target;
		event.dataTransfer.setData("text", dragId);
		oldParentId = findParentElement(dragId)["id"];
	}

	function findParentElement(elementId) {
		try {
			var nodes = treantTree.tree.nodeDB.db;
			var i;
			for (i = 0; i < nodes.length; i++) {
				// necessary to have ==, not ===
				if (nodes[i].nodeHTMLid == elementId) {
					var parentNode = treantTree.tree.getNodeDb().get(nodes[i].parentId);
					return {
						id : parentNode.nodeHTMLid,
						documentationLocation : parentNode.text.documentationLocation
					};
				}
			}
		} catch (error) {
			return {
				id : 0,
				documentationLocation : ""
			};
		}
	}

	/*
	 * external references: condec.api
	 */
	ConDecTreant.prototype.findParentElement = findParentElement;

	function drop(event, target) {
		event.preventDefault();
		var parentId = target.id;
		var childId = dragId;

		var sourceType = extractTypeFromHTMLElement(draggedElement);
		var oldParentType = extractTypeFromHTMLId(findParentElement(draggedElement.id)["id"]);
		var newParentType = extractTypeFromHTMLElement(target);

		conDecAPI.deleteLink(oldParentId, childId, oldParentType, sourceType, function() {
			conDecAPI.linkElements("contain", target.id, draggedElement.id, newParentType, sourceType, function() {
				conDecObservable.notify();
			});
		});
	}

	function allowDrop(event) {
		event.preventDefault();
	}

	function addTooltip() {
		console.log("view.treant.js addTooltip");
		var nodes = treantTree.tree.nodeDB.db;
		for (i = 0; i < nodes.length; i++) {
			AJS.$("#" + nodes[i].id).tooltip();
		}
	}

	function addContextMenuToTreant() {
		console.log("view.treant.js addContextMenuToTreant");
		var treantNodes = document.getElementsByClassName("node");
		var i;
		for (i = 0; i < treantNodes.length; i++) {
			treantNodes[i].addEventListener('contextmenu', function(event) {
				event.preventDefault();

				var left = conDecAPI.getLeftPosition(event, "treant-container");
				var top = conDecAPI.getTopPosition(event, "treant-container");
				
				if (this.getElementsByClassName("node-desc")[0].innerHTML.includes(":")) {
					conDecContextMenu.createContextMenuForSentences(left, top, this.id);
				} else {
					conDecContextMenu.createContextMenu(left, top, this.id);
				}
			});
		}
	}

	// differentiate between issue elements and sentence elements
	// If you have to add commits here: add a commit class to your commit
	// objects in
	// the method "createcontextMenuForTreant"
	function extractTypeFromHTMLElement(element) {
		console.log("view.treant.js extractTypeFromHTMLElement");
		// Sentences have the node desc shape "ProjectId-IssueID:SentenceID"
		if (element.getElementsByClassName("node-desc").length === 0) {
			return "i";
		}
		if (element.getElementsByClassName("node-desc")[0].innerHTML.includes(":")) {
			return "s";
		} else {
			return "i";
		}
	}

	function extractTypeFromHTMLId(id) {
		console.log("view.treant.js extractTypeFromHTMLId");
		var element = document.getElementById(id);
		console.log(id);
		return extractTypeFromHTMLElement(element);
	}

	// export ConDecTreant
	global.conDecTreant = new ConDecTreant();
})(window);
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
		oldParentId = findParentId(dragId);
	}

	function findParentId(elementId) {
		var nodes = treantTree.tree.nodeDB.db;
		var i;
		for (i = 0; i < nodes.length; i++) {
			//necessary to have ==, not ===
			if (nodes[i].nodeHTMLid == elementId) {
				var parentNode = treantTree.tree.getNodeDb().get(nodes[i].parentId);
				var parentId = parentNode.nodeHTMLid;
				return parentId;
			}
		}
	}

	ConDecTreant.prototype.findParentId = findParentId;

	function drop(event, target) {
		event.preventDefault();
		var parentId = target.id;
		var childId = dragId;
	//	sentenceElementIsDropped(target, parentId, childId);
		if (sentenceElementIsDropped(target, parentId, childId)) {
		conDecAPI.deleteLink(oldParentId, childId, function() {
			conDecAPI.createLinkToExistingElement(parentId, childId);
		});
	}
	}

	function sentenceElementIsDropped(target, parentId, childId) {
		console.log("view.treant.js sentenceElementIsDropped");
		var sourceType = extractTypeFromHTMLElement(draggedElement);
		var oldParentType = extractTypeFromHTMLId(findParentId(draggedElement.id));
		var newParentType = extractTypeFromHTMLElement(target);
		// selected element is an issue, dropped element is an sentence
		if (newParentType === "s" && oldParentType === "i") {
			console.log("case 1")
			conDecAPI.deleteLink("i" + oldParentId, "s" + childId, function() {
				conDecAPI.linkGenericElements(target.id, draggedElement.id, newParentType, sourceType, function() {
					conDecObservable.notify();
				});
			});
			return false;
		} else // selected element is an issue, parent element is a sentence
		if (sourceType === "i" && newParentType === "i" && oldParentType === "s") {
			console.log("case 2")
			conDecAPI.deleteGenericLink(findParentId(draggedElement.id), draggedElement.id, oldParentType, sourceType,
					function() {
						conDecAPI.createLinkToExistingElement(parentId, childId);
					});
			return false;
		} else if (sourceType != "i" && oldParentType != "i") {
			console.log("case 3")
			conDecAPI.deleteGenericLink(findParentId(draggedElement.id), draggedElement.id, oldParentType, sourceType,
					function() {
						conDecAPI.linkGenericElements(target.id, draggedElement.id, newParentType, sourceType,
								function() {
									conDecObservable.notify();
								});
					});
			return false;
		}
		return true;
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
				// TODO Find correct position in issue module
				var left = event.pageX;
				var top = event.pageY;

				console.log(left);
				console.log(top);

				console.log(this.id);
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
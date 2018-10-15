// Global variable for DnD
var dragId;
var oldParentId;
var treantTree;

var draggedElement;

// function buildTreant(elementKey, isInteractive) {
// 	console.log("view.treant.js buildTreant");
// 	var depthOfTree = getDepthOfTree();
// 	getTreant(elementKey, depthOfTree, function(treeStructure) {
// 		document.getElementById("treant-container").innerHTML = "";
//
// 		isKnowledgeExtractedFromGit(getProjectKey(), function(isKnowledgeExtractedFromGit) {
// 			if (isKnowledgeExtractedFromGit) {
// 				getCommits(elementKey,
// 						function(commits) {
// 							if (commits.length > 0) {
// 								treeStructure.nodeStructure.children = addCommits(commits,
// 										treeStructure.nodeStructure.children);
// 							}
// 							// console.log(treeStructure);
// 							createTreant(treeStructure, isInteractive);
// 						});
// 			} else {
// 				createTreant(treeStructure, isInteractive);
// 			}
// 		});
// 	});
// }

function buildTreant(elementKey, isInteractive, searchTerm) {
    var depthOfTree = getDepthOfTree();
    getTreant(elementKey, depthOfTree, searchTerm, function(treeStructure) {
        document.getElementById("treant-container").innerHTML = "";

        isKnowledgeExtractedFromGit(getProjectKey(), function(isKnowledgeExtractedFromGit) {
            if (isKnowledgeExtractedFromGit) {
                getCommits(elementKey,
                    function(commits) {
                        if (commits.length > 0) {
                            treeStructure.nodeStructure.children = addCommits(commits,
                                treeStructure.nodeStructure.children);
                        }
                        // console.log(treeStructure);
                        createTreant(treeStructure, isInteractive);
                    });
            } else {
                createTreant(treeStructure, isInteractive);
            }
        });
    });
}


function getDepthOfTree() {
	console.log("view.treant.js getDepthOfTree");
	var depthOfTreeInput = document.getElementById("depth-of-tree-input");
	var depthOfTree = 4;
	if (depthOfTreeInput !== null) {
		depthOfTree = depthOfTreeInput.value;
	}
	return depthOfTree;
}

function createTreant(treeStructure, isInteractive) {
	console.log("view.treant.js createTreant");
	treantTree = new Treant(treeStructure);
	if (isInteractive !== undefined && isInteractive) {
		createContextMenuForTreantNodesThatAreSentence();
		createContextMenuForTreantNodes();
		addDragAndDropSupportForTreant();
		addTooltip();
	}
}

function createContextMenuForTreantNodes() {
	jQueryConDec(function() {
		jQueryConDec.contextMenu({
			selector : ".decision, .rationale, .context, .problem, .solution, .pro, .contra, .other",
			items : contextMenuActionsTreant
		});
	});
}

function createContextMenuForTreantNodesThatAreSentence() {
	var nodes = document.getElementsByClassName("node");
	for (var i = nodes.length - 1; i >= 0; i--) {
		if (nodes[i].getElementsByClassName("node-desc")[0].innerHTML.includes(":")) {
			nodes[i].classList.add("sentence");
		}
	}
	jQueryConDec(function() {
		jQueryConDec.contextMenu({
			selector : ".sentence.node",
			items : contextMenuActionsForSentencesInTreant
		});
	});
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
	if (sentenceElementIsDropped(target, parentId, childId)) {
		deleteLink(oldParentId, childId, function() {
			createLinkToExistingElement(parentId, childId);
		});
	}
}

function sentenceElementIsDropped(target, parentId, childId) {
	console.log("view.treant.js sentenceElementIsDropped");
	var sourceType = extractTypeFromHTMLElement(draggedElement);
	var oldParentType = extractTypeFromHTMLId(findParentId(draggedElement.id));
	var newParentType = extractTypeFromHTMLElement(target);
	// selected element is a sentence, dropped element is an issue
	if (draggedElement.classList.contains("sentence") && !target.classList.contains("sentence")) {
		deleteGenericLink(findParentId(draggedElement.id), draggedElement.id, oldParentType, sourceType, function() {
			linkGenericElements(target.id, draggedElement.id, newParentType, sourceType, function() {
				updateView();
			});
		});
	} else // selected element is an issue, dropped element is an sentence
	if (target.classList.contains("sentence") && !draggedElement.classList.contains("sentence")) {
		deleteLink(oldParentId, childId, function() {
			linkGenericElements(target.id, draggedElement.id, newParentType, sourceType, function() {
				updateView();
			});
		});
	} else // selected element is a sentence, dropped element is an sentence
	if (target.classList.contains("sentence") && draggedElement.classList.contains("sentence")) {
		deleteGenericLink(findParentId(draggedElement.id), draggedElement.id, oldParentType, sourceType, function() {
			linkGenericElements(target.id, draggedElement.id, newParentType, sourceType, function() {
				updateView();
			});
		});
	} else // selected element is an issue, parent element is a sentence
	if (!draggedElement.classList.contains("sentence")
			&& document.getElementById(findParentId(draggedElement.id)).classList.contains("sentence")) {
		deleteGenericLink(findParentId(draggedElement.id), draggedElement.id, oldParentType, sourceType, function() {
			createLinkToExistingElement(parentId, childId);
		});
	} else {// usual link between issue and issue
		return true;
	}
	return false;
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

function addCommits(commits, elementArray) {
	console.log("view.treant.js addCommits");
	commits.forEach(function(commit) {
		var message = commit.message;

		var splitMessage = message.split("@");
		splitMessage.shift();

		var decision;
		var element;
		for ( var i in splitMessage) {
			var split = splitMessage[i].split(" ");
			message = splitMessage[i].substr(splitMessage[i].indexOf(" ") + 1);
			switch (split[0]) {
			case "Decision:":
				decision = {
					children : [],
					HTMLclass : 'decision',
					innerHTML : "<p class=\"node-title\">Decision:</p><p class=\"node-name\">" + message
							+ "</p><a href=\"#\" id=\"" + commit.commitId + "\">More Information</a>"
				};
				$(document).on('click', '#' + commit.commitId + '', function() {
					openCommitDetails(commit);
				});
				elementArray.push(decision);
				break;
			default:
				element = {
					children : [],
					HTMLclass : 'decision',
					text : {
						title : split[0],
						name : message
					}
				};
				elementArray.push(element);
				break;
			}
		}
	});
	return elementArray;
}

function openCommitDetails(commit) {
	console.log("view.treant.js openCommitDetails");
	var url = AJS.contextPath() + "/secure/bbb.gp.gitviewer.Commit.jspa?repoId=" + commit.repository.id + "&commitId="
			+ commit.commitId;
	window.open(url);
}

// differentiate between issue elements and sentence elements
// If you have to add commits here: add a commit class to your commit objects in
// the method "createcontextMenuForTreant"
function extractTypeFromHTMLElement(element) {
	console.log("view.treant.js extractTypeFromHTMLElement");
	if (element.classList.contains("sentence")) {
		return "s";
	}
	if (!element.classList.contains("sentence")) {
		return "i";
	}
}

function extractTypeFromHTMLId(id) {
	console.log("view.treant.js extractTypeFromHTMLId");
	var element = document.getElementById(id);
	return extractTypeFromHTMLElement(element);
}
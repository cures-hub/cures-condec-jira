// Global variable for DnD
var dragId;
var oldParentId;
var treantTree;

function buildTreant(elementKey) {
	var depthOfTreeInput = document.getElementById("depth-of-tree-input");
	var depthOfTree = 4;
	if (depthOfTreeInput !== null) {
		depthOfTree = depthOfTreeInput.value;
	}
	getTreant(elementKey, depthOfTree, function(treeStructure) {
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
							createTreant(treeStructure);
						});
			} else {
				createTreant(treeStructure);
			}
		});
	});
}

function createTreant(treeStructure) {
	treantTree = new Treant(treeStructure);
	createContextMenuForTreantNodes();
	addDragAndDropSupportForTreant();
}

function createContextMenuForTreantNodes() {
	$(function() {
		$.contextMenu({
			selector : ".decision, .rationale, .context, .problem, .solution, .pro, .contra, .other",
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

function getCurrentRootElement() {
	if (treantTree) {
		return treantTree.tree.initJsonConfig.graph.rootElement;
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
	deleteLink(oldParentId, childId, function() {
		createLinkToExistingElement(parentId, childId);
	});
}

function allowDrop(event) {
	event.preventDefault();
}

function addCommits(commits, elementArray) {
	commits.forEach(function(commit) {
		var message = commit.message;

		var splitMessage = message.split("@");
		splitMessage.shift();

		var decision;
		var element;
		for (var i in splitMessage) {
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
	var url = AJS.contextPath() + "/secure/bbb.gp.gitviewer.Commit.jspa?repoId=" + commit.repository.id + "&commitId="
			+ commit.commitId;
	window.open(url);
}
(function(global) {

	// closure locals variable for DnD
	var draggedElement;
	var oldParentElement;
	var treantTree;

	var treantId = "treant-container";

	var ConDecTreant = function() {
	};
	
	/**
	 * Creates a view with only the treant and filter elements. 
	 * The treant is shown as part of other views as well, but these views call "buildTreant".
	 */
	ConDecTreant.prototype.initView = function () {
		// Fill HTML elements for filter criteria
		conDecFiltering.fillFilterElements("treant");
		
		// Add event listeners to HTML elements for filtering
		conDecFiltering.addOnChangeEventToFilterElements("treant", conDecTreant.updateView);
		var isTestCodeShownInput = document.getElementById("show-test-elements-input");
		isTestCodeShownInput.addEventListener("click", function() {conDecTreant.updateView();});

		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);
		
		// Fill view
		this.updateView();
	};
	
	ConDecTreant.prototype.updateView = function () {
		console.log("ConDecTreant updateView");
		var issueKey = conDecAPI.getIssueKey();		
		var filterSettings = conDecFiltering.getFilterSettings("treant");
		filterSettings["selectedElement"] = issueKey;
		var isTestCodeShown = document.getElementById("show-test-elements-input").checked;
		filterSettings["isTestCodeShown"] = isTestCodeShown;
		conDecTreant.buildTreant(filterSettings, true);
	};

	/*
	 * external references: condec.knowledge.page, condec.rationale.backlog
	 */
	ConDecTreant.prototype.buildTreant = function (filterSettings, isInteractive, treantIdParam) {
		console.log("conDecTreant buildTreant");		
		conDecAPI.getTreant(filterSettings, function(treeStructure) {
			if (treantIdParam !== undefined) {
				treantId = treantIdParam;
				treeStructure.chart.container = "#" + treantIdParam;
			}
			document.getElementById(treantId).innerHTML = "";
			treantTree = new Treant(treeStructure);
			if (isInteractive !== undefined && isInteractive) {
				addDragAndDropSupportForTreant();
				addContextMenuToTreant();
				addTooltip();
			}
			changeColorForNodes();
		});
	};

	function changeColorForNodes() {
		var redStatus = new Array("discarded", "rejected", "unresolved", "challenged");
		var treantNodes = document.getElementsByClassName("node");
		for (var i = 0; i < treantNodes.length; i++) {
			var node = treantNodes[i];
			var status = node.data.treenode.text.status;
			if (redStatus.includes(status.toLowerCase())) {
				for (var j = 1; j < node.childNodes.length - 1; j++) {
					node.childNodes[j].style.color = "gray";
				}
			}
		}
	}

	function addDragAndDropSupportForTreant() {
		console.log("conDecTreant addDragAndDropSupportForTreant");
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
		console.log("conDecTreant getCurrentRootElement");
		if (treantTree) {
			return treantTree.tree.initJsonConfig.graph.rootElement;
		}
	}

	function drag(event) {
		draggedElement = event.target;
		oldParentElement = findParentElement(event.target.id);
		event.dataTransfer.setData("text", event.target.id);
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
		}
		return {
		    id : 0,
		    documentationLocation : ""
		};
	}

	/*
	 * external references: condec.api
	 */
	ConDecTreant.prototype.findParentElement = findParentElement;

	function drop(event, target) {
		event.preventDefault();
		var childId = draggedElement.id;

		var documentationLocationOfChild = extractDocumentationLocationFromHTMLElement(draggedElement);
		var documentationLocationOfOldParent = extractDocumentationLocationFromHTMLId(findParentElement(childId)["id"]);
		var documentationLocationOfNewParent = extractDocumentationLocationFromHTMLElement(target);
		if (documentationLocationOfNewParent !== "c" && documentationLocationOfChild !== "c") {
			conDecAPI.deleteLink(oldParentElement.id, childId, documentationLocationOfOldParent,
			        documentationLocationOfChild, function() {
				        conDecAPI.createLink(target.id, draggedElement.id, documentationLocationOfNewParent,
				                documentationLocationOfChild, null, function() {
					                conDecObservable.notify();
				                });
			        });
		} else {
			alert("Can't Link a Knowledge Element to a Code Class");
		}

	}

	function allowDrop(event) {
		event.preventDefault();
	}

	function addTooltip() {
		console.log("conDecTreant addTooltip");
		var nodes = treantTree.tree.nodeDB.db;
		for (i = 0; i < nodes.length; i++) {
			AJS.$("#" + nodes[i].id).tooltip();
		}
		if (document.getElementById("show-elements-input")) {
			if (localStorage.getItem("checkbox") == "false") {
				document.getElementById("show-elements-input").checked = false;
			} else {
				document.getElementById("show-elements-input").checked = true;
			}
		}
	}

	function addContextMenuToTreant() {
		console.log("conDecTreant addContextMenuToTreant");
		var treantNodes = document.getElementsByClassName("node");
		var i;
		for (i = 0; i < treantNodes.length; i++) {
			treantNodes[i].addEventListener('contextmenu', function(event) {
				event.preventDefault();
				if (this.getElementsByClassName("node-desc")[0].innerHTML.includes(":")) {
					conDecContextMenu.createContextMenu(this.id, "s", event, treantId);
				} else if (this.getElementsByClassName("node-documentationLocation")[0].innerHTML.includes("c")) {
					conDecContextMenu.createContextMenu(this.id, "c", event, treantId);
				} else if (this.id) {
					conDecContextMenu.createContextMenu(this.id, "i", event, treantId);
				}
			});
		}
		addContextMenuToCommentTabPanel();
	}

	function addContextMenuToCommentTabPanel() {
		console.log("conDecTreant addContextMenuToCommentTabPanel");
		// ids are set in AbstractKnowledgeClassificationMacro Java class
		var comments = document.querySelectorAll('[id^="commentnode-"]');
		if (comments) {
			for (i = 0; i < comments.length; i++) {
				comments[i].addEventListener('contextmenu', function(event) {
					event.preventDefault();
					conDecContextMenu.createContextMenu(this.id.split("-")[1], "s", event, "issue-container");
				});
			}
		}
	}

	// differentiate between issue elements and sentence elements
	// If you have to add commits here: add a commit class to your commit
	// objects in
	// the method "createcontextMenuForTreant"
	function extractDocumentationLocationFromHTMLElement(element) {
		console.log("conDecTreant extractDocumentationLocationFromHTMLElement");
		// Sentences have the node desc shape "ProjectId-IssueID:SentenceID"
		if ((element.getElementsByClassName("node-documentationLocation")[0].innerHTML.includes("c"))) {
			return "c";
		}
		if (element.getElementsByClassName("node-desc").length === 0) {
			return "i";
		}
		if (element.getElementsByClassName("node-desc")[0].innerHTML.includes(":")) {
			return "s";
		} else {
			return "i";
		}
	}

	function extractDocumentationLocationFromHTMLId(id) {
		console.log("conDecTreant extractDocumentationLocationFromHTMLId");
		var element = document.getElementById(id);
		console.log(id);
		return extractDocumentationLocationFromHTMLElement(element);
	}

	// export ConDecTreant
	global.conDecTreant = new ConDecTreant();
})(window);
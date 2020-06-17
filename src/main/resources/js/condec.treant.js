(function (global) {

    // closure locals variable for DnD
    var draggedElement;
    var oldParentElement;
    var treantTree;

    var treantid = "treant-container";
    var depthId = "depth-of-tree-input";


    var ConDecTreant = function ConDecTreant() {
    };

    /*
     * external references: condec.jira.issue.module.js, condec.knowledge.page.js
     */
    ConDecTreant.prototype.buildTreant = function buildTreant(elementKey, isInteractive, searchTerm, showOtherJiraIssues) {
        console.log("conDecTreant buildTreant");
        treantid = "treant-container";
        var depthOfTree = getDepthOfTree();
        conDecAPI.getTreant(elementKey, depthOfTree, searchTerm, showOtherJiraIssues, function (treeStructure) {
            document.getElementById(treantid).innerHTML = "";
            treantTree = new Treant(treeStructure);
            if (isInteractive !== undefined && isInteractive) {
                addDragAndDropSupportForTreant();
                addContextMenuToTreant();
                addTooltip();
            }
            changeColorForNodes();
        });
    };

    ConDecTreant.prototype.buildClassTreant = function buildClassTreant(elementKey, isInteractive, searchTerm, isIssueView) {
        console.log("conDecTreant buildClassTreant");
        var depthOfTree = getDepthOfTree();
        treantid = "treant-container-class";
        depthId = "depth-of-tree-input-code";
        var checkboxflag = false;
        var minLinkNumber = 1;
        var maxLinkNumber = 100;
        if (!isIssueView) {
            checkboxflag = document.getElementById("show-without-elements-input").checked;
        } else {
            minLinkNumber = document.getElementById("min-number-linked-issues-input").value;
            maxLinkNumber = document.getElementById("max-number-linked-issues-input").value;
            checkboxflag = document.getElementById("show-test-elements-input").checked;
            searchTerm = document.getElementById("search-code-class-input").value;
        }
        conDecAPI.getClassTreant(elementKey, depthOfTree, searchTerm, checkboxflag, isIssueView, minLinkNumber, maxLinkNumber, function (treeStructure) {
            document.getElementById(treantid).innerHTML = "";
            treantTree = new Treant(treeStructure);
            if (isInteractive !== undefined && isInteractive) {
                addDragAndDropSupportForTreant();
                addContextMenuToTreant();
                addTooltip();
            }
            changeColorForNodes();
        });
    };

    function getDepthOfTree() {
        console.log("conDecTreant getDepthOfTree");
        var depthOfTreeInput = document.getElementById(depthId);
        var depthOfTree = 4;
        if (depthOfTreeInput !== null) {
            depthOfTree = depthOfTreeInput.value;
            if (depthId === "depth-of-tree-input-code") {
                depthOfTree = depthOfTree - 1;
            }
        }
        return depthOfTree;
    }

    function changeColorForNodes() {
        var redStatus = new Array("discarded", "rejected", "unresolved");
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
            treantNodes[i].addEventListener("dragstart", function (event) {
                drag(event);
            });
            treantNodes[i].addEventListener("drop", function (event) {
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
                        id: parentNode.nodeHTMLid,
                        documentationLocation: parentNode.text.documentationLocation
                    };
                }
            }
        } catch (error) {
        }
        return {
            id: 0,
            documentationLocation: ""
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
                documentationLocationOfChild, function () {
                    conDecAPI.createLink(null, target.id, draggedElement.id, documentationLocationOfNewParent,
                        documentationLocationOfChild, null, function () {
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
            treantNodes[i].addEventListener('contextmenu', function (event) {
                event.preventDefault();
                if (this.getElementsByClassName("node-desc")[0].innerHTML.includes(":")) {
                    conDecContextMenu.createContextMenu(this.id, "s", event, treantid);
                } else if (this.getElementsByClassName("node-documentationLocation")[0].innerHTML.includes("c")) {
                    conDecContextMenu.createContextMenu(this.id, "c", event, treantid);
                } else if (this.id) {
                    conDecContextMenu.createContextMenu(this.id, "i", event, treantid);
                }
            });
        }
        addContextMenuToCommentTabPanel();
    }

    addContextMenuToCommentTabPanel = function addContextMenuToCommentTabPanel() {
        console.log("conDecTreant addContextMenuToCommentTabPanel");
        // ids are set in AbstractKnowledgeClassificationMacro Java class
        var comments = document.querySelectorAll('[id^="commentnode-"]');
        if (comments) {
            for (i = 0; i < comments.length; i++) {
                comments[i].addEventListener('contextmenu', function (event) {
                    event.preventDefault();
                    conDecContextMenu.createContextMenu(this.id.split("-")[1], "s", event, "issue-container");
                });
            }
        }
    };

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
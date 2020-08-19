(function (global) {

    var ConDecTreeViewer = function () {
    };

    /**
     * called by view.decision.knowledge.page.js
     */
    ConDecTreeViewer.prototype.buildTreeViewer = function buildTreeViewer(filterSettings, treeIdParam, searchInputId) {
        console.log("conDecTreeViewer buildTreeViewer");
        this.resetTreeViewer(treeIdParam);
        conDecAPI.getTreeViewer(filterSettings, function (core) {
            jQueryConDec(treeIdParam).jstree({
                "core": core,
                "plugins": ["dnd", "wholerow", "sort", "search", "state"],
                "search": {
                    "show_only_matches": true
                }
            });
            $(searchInputId).keyup(function () {
                var searchString = $(this).val();
                jQueryConDec(treeIdParam).jstree(true).search(searchString);
            });
        });
        this.addDragAndDropSupportForTreeViewer(treeIdParam);
        this.addContextMenuToTreeViewer(null, treeIdParam);
    };


    ConDecTreeViewer.prototype.addContextMenuToTreeViewer = function (container, treeIdParam) {
        console.log("conDecTreeViewer addContextMenuToTreeViewer");
        jQueryConDec(treeIdParam).on("contextmenu.jstree", function (event) {
            event.preventDefault();

            var nodeId = event.target.parentNode.id;
            var node = getTreeViewerNodeById(nodeId, "#jstree");
            var id = node.data.id;

            if (event.target.parentNode.classList.contains("sentence")) {
                conDecContextMenu.createContextMenu(id, "s", event, container);
            } else if (treeIdParam !== "#code-class-tree") {
            	conDecContextMenu.createContextMenu(id, "i", event, container);
            }
        });
    }

    /**
     * called by condec.tab.panel.js and locally
     */
    ConDecTreeViewer.prototype.resetTreeViewer = function resetTreeViewer(treeIdParam) {
        console.log("conDecTreeViewer resetTreeViewer");
        var treeViewer = jQueryConDec(treeIdParam).jstree(true);
        if (treeViewer) {
            treeViewer.destroy();
        }
    }

    /**
     * local usage only
     */
    function getTreeViewerNodeById(nodeId, treeId) {
        console.log("conDecTreeViewer getTreeViewerNodeById(nodeId)");
        if (nodeId === "#") {
            return nodeId;
        }
        return jQueryConDec(treeId).jstree(true).get_node(nodeId);
    }

    /**
     * called by view.decision.knowledge.js
     */
    ConDecTreeViewer.prototype.selectNodeInTreeViewer = function (nodeId, treeIdParam) {
        console.log("conDecTreeViewer selectNodeInTreeViewer");
        jQueryConDec(document).ready(function () {
            var treeViewer = jQueryConDec(treeIdParam).jstree(true);
            if (treeViewer) {
                treeViewer.deselect_all(true);
                treeViewer.select_node(nodeId);
            }
        });
    };

    ConDecTreeViewer.prototype.filterNodesByGroup = function (selectedGroup, treeId) {
        console.log("conDecTreeViewer filterNodesByGroup");
        jQueryConDec(treeId).on("state_ready.jstree", function () {
            var treeViewer = jQueryConDec(treeId).jstree(true);
            if (treeViewer) {
                var jsonNodes = treeViewer.get_json('#', {flat: true});
                $.each(jsonNodes, function (i, val) {
                    var matches = 0;
                    var kElement = $(val).attr("data");
                    var elementGroups = $(kElement).attr("groups");
                    var treeNode = document.getElementById($(val).attr("id"));
                    $(treeNode).hide();
                    for (var j = 0; j < elementGroups.length; j++) {
                        for (var x = 0; x < selectedGroup.length; x++) {
                            if (elementGroups[j] === selectedGroup[x]) {
                                matches++;
                            }
                        }
                        if (matches === selectedGroup.length) {
                            $(treeNode).show();
                        }
                    }
                });
            }
        });
    };

    ConDecTreeViewer.prototype.minMaxFilter = function (treeId) {
        console.log("conDecTreeViewer filterNodesByGroup");
        jQueryConDec(treeId).on("state_ready.jstree", function () {
            var treeViewer = jQueryConDec(treeId).jstree(true);
            if (treeViewer) {
                minLinkNumber = document.getElementById("min-number-linked-issues-input").value;
                maxLinkNumber = document.getElementById("max-number-linked-issues-input").value;
                var jsonNodes = treeViewer.get_json('#', {flat: true});
                $.each(jsonNodes, function (i, val) {
                    var treeNode = document.getElementById($(val).attr("id"));
                    if (treeNode.style.display !== "none") {
                        var kElement = $(val).attr("data");
                        var description = $(kElement).attr("description");
                        var number = description.split(";").length - 1;
                        if (number < minLinkNumber || number > maxLinkNumber) {
                            $(treeNode).hide();
                        }
                    }
                });
            }
        });
    };
    /**
     * called by view.tab.panel.js locally
     */
    ConDecTreeViewer.prototype.addDragAndDropSupportForTreeViewer = function (treeIdParam) {
        console.log("conDecTreeViewer addDragAndDropSupportForTreeViewer");
        jQueryConDec(treeIdParam).on(
            'move_node.jstree',
            function (object, nodeInContext) {
                var node = nodeInContext.node;
                var parentNode = getTreeViewerNodeById(nodeInContext.parent, "#jstree");
                var oldParentNode = getTreeViewerNodeById(nodeInContext.old_parent, "#jstree");
                var nodeId = node.data.id;

                var sourceType = (node.li_attr['class'] === "sentence") ? "s" : "i";
                var oldParentType = (oldParentNode.li_attr['class'] === "sentence") ? "s" : "i";
                var newParentType = (parentNode.li_attr['class'] === "sentence") ? "s" : "i";

                if (oldParentNode === "#" && parentNode !== "#") {
                    conDecAPI.createLink(null, parentNode.data.id, nodeId, newParentType, sourceType, null, function () {
                        conDecObservable.notify();
                    });
                }
                if (parentNode === "#" && oldParentNode !== "#") {
                    conDecAPI.deleteLink(oldParentNode.data.id, nodeId, oldParentType, sourceType, function () {
                        conDecObservable.notify();
                    });
                }
                if (parentNode !== '#' && oldParentNode !== '#') {
                    conDecAPI.deleteLink(oldParentNode.data.id, nodeId, oldParentType, sourceType, function () {
                        conDecAPI.createLink(null, parentNode.data.id, nodeId, newParentType, sourceType, null,
                            function () {
                                conDecObservable.notify();
                            });
                    });
                }
            });
    };

    // export ConDecTreeViewer
    global.conDecTreeViewer = new ConDecTreeViewer();
})(window);
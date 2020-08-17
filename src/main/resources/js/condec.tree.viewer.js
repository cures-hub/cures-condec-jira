(function (global) {

    var jstreeId = "#jstree";

    var ConDecTreeViewer = function () {
    };

    /**
     * called by view.decision.knowledge.page.js
     */
    ConDecTreeViewer.prototype.buildTreeViewer = function buildTreeViewer() {
        console.log("conDecTreeViewer buildTreeViewer");
        jstreeId = "#jstree";
        this.resetTreeViewer();
        var rootElementType = $("select[name='select-root-element-type']").val();
        conDecAPI.getTreeViewer(rootElementType, function (core) {
            jQueryConDec("#jstree").jstree({
                "core": core,
                "plugins": ["dnd", "wholerow", "sort", "search", "state"],
                "search": {
                    "show_only_matches": true
                }
            });
            $("#jstree-search-input").keyup(function () {
                var searchString = $(this).val();
                jQueryConDec("#jstree").jstree(true).search(searchString);
            });
        });
        this.addDragAndDropSupportForTreeViewer();
        this.addContextMenuToTreeViewer(null);
    };

    /**
     * called by condec.code.class.page.js
     */
    ConDecTreeViewer.prototype.buildClassTreeViewer = function buildClassTreeViewer() {
        console.log("conDecTreeViewer buildClassTreeViewer");
        jstreeId = "#code-class-tree";
        this.resetTreeViewer();
        var rootElementType = "codeClass";
        conDecAPI.getTreeViewer(rootElementType, function (core) {
            jQueryConDec("#code-class-tree").jstree({
                "core": core,
                "plugins": ["dnd", "wholerow", "sort", "search", "state"],
                "search": {
                    "show_only_matches": true
                }
            });
            $("#jstree-search-input-code").keyup(function () {
                var searchString = $(this).val();
                jQueryConDec("#code-class-tree").jstree(true).search(searchString);
            });
        });
    };

	/**
	 * called by view.decision.rationaleBacklog.page.js
	 */
	ConDecTreeViewer.prototype.buildRationaleBacklogTreeViewer = function buildRationaleBacklogTreeViewer() {
		console.log("conDecTreeViewer buildRationaleBacklogTreeViewer");
		jstreeId = "#rationale-backlog-tree";
		this.resetTreeViewer();
		var rootElementTypes = ["Issue", "Alternative", "Decision", "Pro", "Con"];
		conDecAPI.getTreeViewerForRationaleBacklog(rootElementTypes, function (core) {
			jQueryConDec("#rationale-backlog-tree").jstree({
				"core": core,
				"plugins": ["dnd", "wholerow", "sort", "search", "state"],
				"search": {
					"show_only_matches": true
				}
			});
			$("#text-search-input-rb").keyup(function () {
				var searchString = $(this).val();
				jQueryConDec("#rationale-backlog-tree").jstree(true).search(searchString);
			});
		});
		this.addDragAndDropSupportForTreeViewer();
		this.addContextMenuToTreeViewer(null);
	};

    ConDecTreeViewer.prototype.addContextMenuToTreeViewer = function (container) {
        console.log("conDecTreeViewer addContextMenuToTreeViewer");
        jQueryConDec(jstreeId).on("contextmenu.jstree", function (event) {
            event.preventDefault();

            var nodeId = event.target.parentNode.id;
            var node = getTreeViewerNodeById(nodeId, "#jstree");
            var id = node.data.id;

            if (event.target.parentNode.classList.contains("sentence")) {
                conDecContextMenu.createContextMenu(id, "s", event, container);
            } else {
                if (jstreeId !== "#code-class-tree") {
                    conDecContextMenu.createContextMenu(id, "i", event, container);
                }

            }
        });
    }

    /**
     * called by condec.tab.panel.js and locally
     */
    ConDecTreeViewer.prototype.resetTreeViewer = function resetTreeViewer() {
        console.log("conDecTreeViewer resetTreeViewer");
        var treeViewer = jQueryConDec(jstreeId).jstree(true);
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
    ConDecTreeViewer.prototype.selectNodeInTreeViewer = function (nodeId) {
        console.log("conDecTreeViewer selectNodeInTreeViewer");
        jQueryConDec(document).ready(function () {
            var treeViewer = jQueryConDec(jstreeId).jstree(true);
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

	// ConDecTreeViewer.prototype.filterNodesByStatus = function filterNodesByStatus(selectedStatus, treeId) {
	// 	console.log("conDecTreeViewer filterNodesByStatus");
	// 	jQueryConDec(treeId).on("state_ready.jstree", function() {
	// 		var treeViewer = jQueryConDec(treeId).jstree(true);
	// 		if (treeViewer) {
	// 			var jsonNodes = treeViewer.get_json('#', {
	// 				flat : true
	// 			});
	// 			$.each(jsonNodes, function(i, val) {
	// 				var matches = 0;
	// 				var kElement = $(val).attr("data");
	// 				var elementStatus = $(kElement).attr("status");
	// 				if (!selectedStatus.includes(elementStatus)) {
	// 					var treeNode = document.getElementById($(val).attr("id"));
	// 					$(treeNode).hide();
	// 				}
	// 			});
	// 		}
	// 	});
	// };

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
    ConDecTreeViewer.prototype.addDragAndDropSupportForTreeViewer = function () {
        console.log("conDecTreeViewer addDragAndDropSupportForTreeViewer");
        jQueryConDec(jstreeId).on(
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
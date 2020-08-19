(function(global) {

	var ConDecTreeViewer = function() {
	};

	/**
	 * called by condec.knowledge.page.js, condec.rationale.backlog.js, condec.code.class.page.js
	 */
	ConDecTreeViewer.prototype.buildTreeViewer = function (filterSettings, treeId, searchInputId, container) {
		console.log("conDecTreeViewer buildTreeViewer");
		resetTreeViewer(treeId);
		conDecAPI.getTreeViewer(filterSettings, function (core) {
			jQueryConDec(treeId).jstree({
			    "core" : core,
			    "plugins" : [ "dnd", "wholerow", "sort", "search", "state" ],
			    "search" : {
				    "show_only_matches" : true
			    }
			});
			$(searchInputId).keyup (function () {
				var searchString = $(this).val();
				jQueryConDec(treeId).jstree(true).search(searchString);
			});
		});
		addDragAndDropSupportForTreeViewer(treeId);
		addContextMenuToTreeViewer(container, treeId);
	};
	
	function resetTreeViewer (treeId) {
		console.log("conDecTreeViewer resetTreeViewer");
		var treeViewer = jQueryConDec(treeId).jstree(true);
		if (treeViewer) {
			treeViewer.destroy();
		}
	}

	function addDragAndDropSupportForTreeViewer (treeId) {
		console.log("conDecTreeViewer addDragAndDropSupportForTreeViewer");
		jQueryConDec(treeId).on('move_node.jstree', function(object, nodeInContext) {
			var node = nodeInContext.node;
			var parentNode = getTreeViewerNodeById(nodeInContext.parent, "#jstree");
			var oldParentNode = getTreeViewerNodeById(nodeInContext.old_parent, "#jstree");
			var nodeId = node.data.id;

			var sourceType = (node.li_attr['class'] === "sentence") ? "s" : "i";
			var oldParentType = (oldParentNode.li_attr['class'] === "sentence") ? "s" : "i";
			var newParentType = (parentNode.li_attr['class'] === "sentence") ? "s" : "i";

			if (oldParentNode === "#" && parentNode !== "#") {
				conDecAPI.createLink(null, parentNode.data.id, nodeId, newParentType, sourceType, null, function() {
					conDecObservable.notify();
				});
			}
			if (parentNode === "#" && oldParentNode !== "#") {
				conDecAPI.deleteLink(oldParentNode.data.id, nodeId, oldParentType, sourceType, function() {
					conDecObservable.notify();
				});
			}
			if (parentNode !== '#' && oldParentNode !== '#') {
				conDecAPI.deleteLink(oldParentNode.data.id, nodeId, oldParentType, sourceType, function() {
					conDecAPI.createLink(null, parentNode.data.id, nodeId, newParentType, sourceType, null, function() {
						conDecObservable.notify();
					});
				});
			}
		});
	}

	function addContextMenuToTreeViewer (container, treeId) {
		if (treeId === "#code-class-tree") {
			/*
			 * @issue Should it be possible to change code classes using the context menu?
			 */
			return;
		}
		console.log("conDecTreeViewer addContextMenuToTreeViewer");
		jQueryConDec(treeId).on("contextmenu.jstree", function(event) {
			event.preventDefault();

			var nodeId = event.target.parentNode.id;
			var node = getTreeViewerNodeById(nodeId, "#jstree");
			var id = node.data.id;

			if (event.target.parentNode.classList.contains("sentence")) {
				conDecContextMenu.createContextMenu(id, "s", event, container);
			} else {
				conDecContextMenu.createContextMenu(id, "i", event, container);
			}
		});
	}

	function getTreeViewerNodeById (nodeId, treeId) {
		console.log("conDecTreeViewer getTreeViewerNodeById(nodeId)");
		if (nodeId === "#") {
			return nodeId;
		}
		return jQueryConDec(treeId).jstree(true).get_node(nodeId);
	}

	/**
	 * called by condec.knowledge.page.js, condec.rationale.backlog.js
	 */
	ConDecTreeViewer.prototype.selectNodeInTreeViewer = function (nodeId, treeId) {
		console.log("conDecTreeViewer selectNodeInTreeViewer");
		jQueryConDec(document).ready(function() {
			var treeViewer = jQueryConDec(treeId).jstree(true);
			if (treeViewer) {
				treeViewer.deselect_all(true);
				treeViewer.select_node(nodeId);
			}
		});
	};

	// TODO Replace by backend filtering?
	ConDecTreeViewer.prototype.minMaxFilter = function (treeId) {
		console.log("conDecTreeViewer filterNodesByGroup");
		jQueryConDec(treeId).on("state_ready.jstree", function() {
			var treeViewer = jQueryConDec(treeId).jstree(true);
			if (treeViewer) {
				minLinkNumber = document.getElementById("min-number-linked-issues-input").value;
				maxLinkNumber = document.getElementById("max-number-linked-issues-input").value;
				var jsonNodes = treeViewer.get_json('#', {
					flat : true
				});
				$.each(jsonNodes, function(i, val) {
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
	
	// export ConDecTreeViewer
	global.conDecTreeViewer = new ConDecTreeViewer();
})(window);
(function(global) {

	var ConDecTreeViewer = function ConDecTreeViewer() {
	};

	/**
	 * called by view.decision.knowledge.page.js
	 */
	ConDecTreeViewer.prototype.buildTreeViewer = function buildTreeViewer() {
		console.log("view.tree.viewer.js buildTreeViewer");
		this.resetTreeViewer();
		var rootElementType = $("select[name='select-root-element-type']").val();
		conDecAPI.getTreeViewer(rootElementType, function(core) {
			jQueryConDec("#jstree").jstree({
				"core" : core,
				"plugins" : [ "dnd", "wholerow", "sort", "search", "state" ],
				"search" : {
					"show_only_matches" : true
				}
			});
			$("#jstree-search-input").keyup(function() {
				var searchString = $(this).val();
				jQueryConDec("#jstree").jstree(true).search(searchString);
			});
		});
		this.addDragAndDropSupportForTreeViewer();
		this.addContextMenuToTreeViewer();
	};

	ConDecTreeViewer.prototype.addContextMenuToTreeViewer = function addContextMenuToTreeViewer() {
		console.log("view.tree.viewer.js addContextMenuToTreeViewer");
		jQueryConDec("#jstree").on("contextmenu.jstree", function(event) {
			event.preventDefault();
			var left = event.pageX;
			var top = event.pageY;
			if (event.target.parentNode.classList.contains("sentence")) {
				conDecContextMenu.createContextMenuForSentences(left, top, event.target.parentNode.id);
			} else {
				conDecContextMenu.createContextMenu(left, top, event.target.parentNode.id);
			}
		});
	}

	/**
	 * called by view.context.menu.js view.condec.tab.panel.js locally
	 */
	ConDecTreeViewer.prototype.resetTreeViewer = function resetTreeViewer() {
		console.log("view.tree.viewer.js resetTreeViewer");
		var treeViewer = jQueryConDec("#jstree").jstree(true);
		if (treeViewer) {
			treeViewer.destroy();
		}
	}
	/**
	 * local usage only
	 */
	function getTreeViewerNodeById(nodeId) {
		console.log("view.tree.viewer.js getTreeViewerNodeById(nodeId)");
		if (nodeId === "#") {
			return nodeId;
		}
		return jQueryConDec("#jstree").jstree(true).get_node(nodeId);
	}
	/**
	 * called by view.decision.knowledge.js
	 */
	ConDecTreeViewer.prototype.selectNodeInTreeViewer = function selectNodeInTreeViewer(nodeId) {
		console.log("view.tree.viewer.js selectNodeInTreeViewer");
		jQueryConDec(document).ready(function() {
			var treeViewer = jQueryConDec("#jstree").jstree(true);
			if (treeViewer) {
				treeViewer.deselect_all(true);
				treeViewer.select_node(nodeId);
			}
		});
	};

	/**
	 * called by view.tab.panel.js locally
	 */
	ConDecTreeViewer.prototype.addDragAndDropSupportForTreeViewer = function addDragAndDropSupportForTreeViewer() {
		console.log("view.tree.viewer.js addDragAndDropSupportForTreeViewer");
		jQueryConDec("#jstree").on('move_node.jstree', function(object, nodeInContext) {
			var node = nodeInContext.node;
			var parentNode = getTreeViewerNodeById(nodeInContext.parent);
			var oldParentNode = getTreeViewerNodeById(nodeInContext.old_parent);
			var nodeId = node.data.id;

			var sourceType = (node.li_attr['class'] === "sentence") ? "s" : "i";
			var oldParentType = (oldParentNode.li_attr['class'] === "sentence") ? "s" : "i";
			var newParentType = (parentNode.li_attr['class'] === "sentence") ? "s" : "i";

			if (oldParentNode === "#" && parentNode !== "#") {
				conDecAPI.linkGenericElements(parentNode.data.id, nodeId, newParentType, sourceType, function() {
					conDecObservable.notify();
				});
			}
			if (parentNode === "#" && oldParentNode !== "#") {
				conDecAPI.deleteGenericLink(oldParentNode.data.id, nodeId, oldParentType, sourceType, function() {
					conDecObservable.notify();
				});
			}
			if (parentNode !== '#' && oldParentNode !== '#') {
				conDecAPI.deleteGenericLink(oldParentNode.data.id, nodeId, oldParentType, sourceType, function() {
					conDecAPI.linkGenericElements(parentNode.data.id, nodeId, newParentType, sourceType, function() {
						conDecObservable.notify();
					});
				});
			}
		});
	}

	// export ConDecTreeViewer
	global.conDecTreeViewer = new ConDecTreeViewer();
})(window);
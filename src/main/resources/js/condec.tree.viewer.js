(function(global) {

	var ConDecTreeViewer = function ConDecTreeViewer() {
	};

	/**
	 * called by view.decision.knowledge.page.js
	 */
	ConDecTreeViewer.prototype.buildTreeViewer = function buildTreeViewer() {
		console.log("conDecTreeViewer buildTreeViewer");
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
		this.addContextMenuToTreeViewer(null);
	};

	ConDecTreeViewer.prototype.addContextMenuToTreeViewer = function addContextMenuToTreeViewer(container) {
		console.log("conDecTreeViewer addContextMenuToTreeViewer");
		jQueryConDec("#jstree").on("contextmenu.jstree", function(event) {
			event.preventDefault();

			var nodeId = event.target.parentNode.id;
			var node = getTreeViewerNodeById(nodeId);
			var id = node.data.id;

			if (event.target.parentNode.classList.contains("sentence")) {
				conDecContextMenu.createContextMenu(id, "s", event, container);
			} else {
				conDecContextMenu.createContextMenu(id, "i", event, container);
			}
		});
	}

	/**
	 * called by condec.tab.panel.js and locally
	 */
	ConDecTreeViewer.prototype.resetTreeViewer = function resetTreeViewer() {
		console.log("conDecTreeViewer resetTreeViewer");
		var treeViewer = jQueryConDec("#jstree").jstree(true);
		if (treeViewer) {
			treeViewer.destroy();
		}
	}
	
	/**
	 * local usage only
	 */
	function getTreeViewerNodeById(nodeId) {
		console.log("conDecTreeViewer getTreeViewerNodeById(nodeId)");
		if (nodeId === "#") {
			return nodeId;
		}
		return jQueryConDec("#jstree").jstree(true).get_node(nodeId);
	}
	
	/**
	 * called by view.decision.knowledge.js
	 */
	ConDecTreeViewer.prototype.selectNodeInTreeViewer = function selectNodeInTreeViewer(nodeId) {
		console.log("conDecTreeViewer selectNodeInTreeViewer");
		jQueryConDec(document).ready(function() {
			var treeViewer = jQueryConDec("#jstree").jstree(true);
			if (treeViewer) {
				treeViewer.deselect_all(true);
				treeViewer.select_node(nodeId);
			}
		});
	};

	ConDecTreeViewer.prototype.filterNodesByGroup = function filterNodesByGroup(selectedGroup) {
		console.log("conDecTreeViewer filterNodesByGroup");
		jQueryConDec("#jstree").on("loaded.jstree",function() {
			var treeViewer = jQueryConDec("#jstree").jstree(true);
			if (treeViewer) {
				var jsonNodes = treeViewer.get_json('#', { flat: true});
				$.each(jsonNodes, function (i, val) {
						var kElement = $(val).attr("data");
						var elementGroups = $(kElement).attr("groups");
						var treeNode = document.getElementById($(val).attr("id"));
						$(treeNode).hide();
					    for(var j=0; j<elementGroups.length; j++){
					    	if(elementGroups[j] === selectedGroup){
					    		$(treeNode).show();
					    	}
					    }
				});
			}
		});
	};

	/**
	 * called by view.tab.panel.js locally
	 */
	ConDecTreeViewer.prototype.addDragAndDropSupportForTreeViewer = function addDragAndDropSupportForTreeViewer() {
		console.log("conDecTreeViewer addDragAndDropSupportForTreeViewer");
		jQueryConDec("#jstree").on(
				'move_node.jstree',
				function(object, nodeInContext) {
					var node = nodeInContext.node;
					var parentNode = getTreeViewerNodeById(nodeInContext.parent);
					var oldParentNode = getTreeViewerNodeById(nodeInContext.old_parent);
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
							conDecAPI.createLink(null, parentNode.data.id, nodeId, newParentType, sourceType, null,
									function() {
										conDecObservable.notify();
									});
						});
					}
				});
	};

	// export ConDecTreeViewer
	global.conDecTreeViewer = new ConDecTreeViewer();
})(window);
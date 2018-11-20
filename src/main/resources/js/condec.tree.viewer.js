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
	ConDecTreeViewer.prototype.addDragAndDropSupportForTreeViewer =  function addDragAndDropSupportForTreeViewer() {
		console.log("view.tree.viewer.js addDragAndDropSupportForTreeViewer");
		jQueryConDec("#jstree").on(
				'move_node.jstree',
				function(object, nodeInContext) {
					var node = nodeInContext.node;
					var parentNode = getTreeViewerNodeById(nodeInContext.parent);
					var oldParentNode = getTreeViewerNodeById(nodeInContext.old_parent);
					var nodeId = node.data.id;

					console.log(!node.li_attr['class'] === "sentence");
					if (node.li_attr['class'] === "issue" && parentNode.li_attr['class'] === "issue"
							&& oldParentNode.li_attr['class'] === "issue") {

						if (oldParentNode === "#" && parentNode !== "#") {

							conDecAPI.createLinkToExistingElement(parentNode.data.id, nodeId);
						}
						if (parentNode === "#" && oldParentNode !== "#") {

							conDecAPI.deleteLink(oldParentNode.data.id, nodeId, function() {
								conDecObservable.notify();
							});
						}
						if (parentNode !== '#' && oldParentNode !== '#') {

							conDecAPI.deleteLink(oldParentNode.data.id, nodeId, function() {
								conDecAPI.createLinkToExistingElement(parentNode.data.id, nodeId);
							});
						}
					} else {

						var targetType = (parentNode.li_attr['class'] === "sentence") ? "s" : "i";

						if (oldParentNode === "#" && parentNode !== "#") {

							conDecAPI.linkGenericElements(parentNode.data.id, nodeId, targetType, "s", function() {
								// conDecObservable.notify();
							});
						}
						if (parentNode === "#" && oldParentNode !== "#") {

							targetTypeOld = (oldParentNode.li_attr['class'] === "sentence") ? "s" : "i";
							conDecAPI.deleteGenericLink(oldParentNode.data.id, nodeId, targetTypeOld, "s", function() {
								//conDecObservable.notify();
							});
						}
						if (parentNode !== '#' && oldParentNode !== '#') {

							targetTypeOld = (oldParentNode.li_attr['class'] === "sentence") ? "s" : "i";
							var nodeType = (node.li_attr['class'] === "sentence") ? "s" : "i";
							if (nodeType === "i" && targetTypeOld === "i") {
								conDecAPI.deleteLink(oldParentNode.data.id, nodeId, function() {
									conDecAPI.linkGenericElements(parentNode.data.id, nodeId, targetType, nodeType,
											function() {
										//		conDecObservable.notify();
											});
								});
							} else {
								conDecAPI.deleteGenericLink(oldParentNode.data.id, nodeId, targetTypeOld, nodeType,
										function() {
											conDecAPI.linkGenericElements(parentNode.data.id, nodeId, targetType,
													nodeType, function() {
													//	conDecObservable.notify();
													});
										});
							}
						}
					}

				});
	}

	// export ConDecTreeViewer
	global.conDecTreeViewer = new ConDecTreeViewer();
})(window);
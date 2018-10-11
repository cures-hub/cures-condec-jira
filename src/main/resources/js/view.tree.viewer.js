function buildTreeViewer() {
	console.log("view.tree.viewer.js buildTreeViewer");
	resetTreeViewer();
	var rootElementType = $("select[name='select-root-element-type']").val();
	getTreeViewer(rootElementType, function(core) {
		jQueryConDec("#jstree").jstree({
			"core" : core,
			"plugins" : [ "dnd", "contextmenu", "wholerow", "sort", "search", "state" ],
			"search" : {
				"show_only_matches" : true
			},
			"contextmenu" : {
				"items" : customMenu
			}
		});
		$("#jstree-search-input").keyup(function() {
			var searchString = $(this).val();
			jQueryConDec("#jstree").jstree(true).search(searchString);
		});
	});
	addDragAndDropSupportForTreeViewer();
}

function customMenu(node) {
	console.log("view.tree.viewer.js customMenu(node)");
	if (node.li_attr['class'] == "sentence") {
		return contextMenuActionsForSentences;
	} else {
		return contextMenuActions;
	}
}

function resetTreeViewer() {
	console.log("view.tree.viewer.js resetTreeViewer");
	var treeViewer = jQueryConDec("#jstree").jstree(true);
	if (treeViewer) {
		treeViewer.destroy();
	}
}

function getTreeViewerNodeById(nodeId) {
	console.log("view.tree.viewer.js getTreeViewerNodeById(nodeId)");
	if (nodeId === "#") {
		return nodeId;
	}
	return jQueryConDec("#jstree").jstree(true).get_node(nodeId);
}

function selectNodeInTreeViewer(nodeId) {
	console.log("view.tree.viewer.js selectNodeInTreeViewer");
	jQueryConDec("#jstree").on("ready.jstree", function() {
		var treeViewer = jQueryConDec("#jstree").jstree(true);
		if (treeViewer) {
			treeViewer.select_node(nodeId);
		}
	});
}

function addDragAndDropSupportForTreeViewer() {
	console.log("view.tree.viewer.js addDragAndDropSupportForTreeViewer");
	jQueryConDec("#jstree").on('move_node.jstree', function(object, nodeInContext) {
		var node = nodeInContext.node;
		var parentNode = getTreeViewerNodeById(nodeInContext.parent);
		var oldParentNode = getTreeViewerNodeById(nodeInContext.old_parent);
		var nodeId = node.data.id;

		if (!node.li_attr['class'] == "sentence") {
			if (oldParentNode === "#" && parentNode !== "#") {
				createLinkToExistingElement(parentNode.data.id, nodeId);
			}
			if (parentNode === "#" && oldParentNode !== "#") {
				deleteLink(oldParentNode.data.id, nodeId, function() {
					updateView();
				});
			}
			if (parentNode !== '#' && oldParentNode !== '#') {
				deleteLink(oldParentNode.data.id, nodeId, function() {
					createLinkToExistingElement(parentNode.data.id, nodeId);
				});
			}
		} else {
			var targetType = (parentNode.li_attr['class'] == "sentence") ? "s" : "i";

			if (oldParentNode === "#" && parentNode !== "#") {
				linkGenericElements(parentNode.data.id, nodeId, targetType, "s", function() {
					refreshTreeViewer()
				});
			}
			if (parentNode === "#" && oldParentNode !== "#") {
				var targetTypeOld = (oldParentNode.li_attr['class'] == "sentence") ? "s" : "i";
				deleteGenericLink(oldParentNode.data.id, nodeId, targetTypeOld, "s", function() {
					refreshTreeViewer()
				});
			}
			if (parentNode !== '#' && oldParentNode !== '#') {
				var targetTypeOld = (oldParentNode.li_attr['class'] == "sentence") ? "s" : "i";
				var nodeType = (node.li_attr['class'] == "sentence") ? "s" : "i";
				if (nodeType == "i" && targetTypeOld == "i") {
					linkGenericElements(parentNode.data.id, nodeId, targetType, nodeType, function() {
						refreshTreeViewer()
					});
				} else {
					deleteGenericLink(oldParentNode.data.id, nodeId, targetTypeOld, nodeType, function() {
						linkGenericElements(parentNode.data.id, nodeId, targetType, nodeType, function() {
							refreshTreeViewer()
						});
					});
				}
			}
		}

	});
}
/*
 This module does:
 * show a tree of decision knowledge elements and other knowledge elements (requirements, tasks, bug reports, ...)
 * enable to filter the tree of knowledge

 Requires
 * conDecAPI
 * conDecObservable
 
 Is referenced by
 * conDecRationaleBacklog
 * conDecKnowledgePage
 
 Is referenced in HTML by
 * jstree.vm 
 */
(function(global) {

	var ConDecTreeViewer = function() {
	};
	
	/**
	 * Creates a view with only the jstree tree viewer and filter elements. 
	 * The jstree tree viewer is shown as part of other views as well, but these views call "buildTreeViewer".
	 */
	ConDecTreeViewer.prototype.initView = function () {
		// Fill HTML elements for filter criteria
		conDecFiltering.fillFilterElements("jstree");
		
		// Add on click listeners to filter button
		conDecFiltering.addOnClickEventToFilterButton("jstree", conDecTreeViewer.updateViewForFilterSettings);
        conDecFiltering.addOnClickEventToChangeImpactButton("jstree", conDecTreeViewer.updateViewForFilterSettings);

		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);

		// Fill view
		this.updateView();		
	};
	
	ConDecTreeViewer.prototype.updateView = function () {
		console.log("ConDecTreeViewer updateView");
		var filterSettings = conDecFiltering.getFilterSettings("jstree");
        conDecTreeViewer.updateViewForFilterSettings(filterSettings);
    };
    
    ConDecTreeViewer.prototype.updateViewForFilterSettings = function (filterSettings) {
		conDecTreeViewer.buildTreeViewer(filterSettings, "#jstree", "#search-input-jstree", "jstree");
		jQuery("#jstree").on("loaded.jstree", function() {
			jQuery("#jstree").jstree("open_all");
		});
		jQuery("#jstree").on("select_node.jstree", function(error, tree) {
			var node = tree.node.data;
			conDecFiltering.setSelectedElement("jstree", node.key);
		});

		// Add tooltip if Change Impact Analysis is being conducted
		if (filterSettings.areChangeImpactsHighlighted) {
			conDecCIATooltip.addJSTreeTooltip();
		}
	};

	/**
	 * called by condec.knowledge.page.js, condec.rationale.backlog.js
	 */
	ConDecTreeViewer.prototype.buildTreeViewer = function (filterSettings, treeId, searchInputId, container) {
		console.log("conDecTreeViewer buildTreeViewer");
		resetTreeViewer(treeId);
		conDecAPI.getTreeViewer(filterSettings, function (core) {
			jQuery(treeId).jstree({
			    "core" : core,
			    "plugins" : [ "dnd", "wholerow", "sort", "search", "state" ],
			    "search" : {
				    "show_only_matches" : true
			    }
			});
			$(searchInputId).keyup (function () {
				var searchString = $(this).val();
				jQuery(treeId).jstree(true).search(searchString);
			});
		});
		addDragAndDropSupportForTreeViewer(treeId);
		addContextMenuToTreeViewer(container, treeId);
	};
	
	function resetTreeViewer (treeId) {
		console.log("conDecTreeViewer resetTreeViewer");
		var treeViewer = jQuery(treeId).jstree(true);
		if (treeViewer) {
			treeViewer.destroy();
		}
	}

	function addDragAndDropSupportForTreeViewer (treeId) {
		console.log("conDecTreeViewer addDragAndDropSupportForTreeViewer");
		jQuery(treeId).on('move_node.jstree', function(object, nodeInContext) {
			var node = nodeInContext.node;
			var parentNode = getTreeViewerNodeById(nodeInContext.parent, treeId);
			var oldParentNode = getTreeViewerNodeById(nodeInContext.old_parent, treeId);
			var nodeId = node.data.id;

			var childLocation = node.data.documentationLocation;
			var oldParentLocation = oldParentNode.data.documentationLocation;
			var newParentLocation = parentNode.data.documentationLocation;

			if (oldParentNode === "#" && parentNode !== "#") {
				conDecAPI.createLink(parentNode.data.id, nodeId, newParentLocation, childLocation, null, function() {
					conDecObservable.notify();
				});
			} else if (parentNode === "#" && oldParentNode !== "#") {
				conDecAPI.deleteLink(oldParentNode.data.id, nodeId, oldParentLocation, childLocation, function() {
					conDecObservable.notify();
				});
			} else if (parentNode !== '#' && oldParentNode !== '#') {
				conDecAPI.deleteLink(oldParentNode.data.id, nodeId, oldParentLocation, childLocation, function() {
					conDecAPI.createLink(parentNode.data.id, nodeId, newParentLocation, childLocation, null, function() {
						conDecObservable.notify();
					});
				});
			}
		});
	}

	function addContextMenuToTreeViewer (container, treeId) {
		console.log("conDecTreeViewer addContextMenuToTreeViewer");
		jQuery(treeId).on("contextmenu.jstree", function(event) {
			event.preventDefault();

			var nodeId = event.target.parentNode.id;
			var node = getTreeViewerNodeById(nodeId, treeId);
			var element = node.data;

			var parentNode = getTreeViewerNodeById(node.parent, treeId);	
			var parentElement = parentNode.data;

			if (parentElement === undefined) {
				if (jQuery(treeId).jstree(true).is_parent(node) === true) {
					childNode = getTreeViewerNodeById(node.children[0], treeId);
					conDecContextMenu.createContextMenu(element.id, element.documentationLocation, event, container,
						childNode.data.id, childNode.data.documentationLocation);
				} else {
					conDecContextMenu.createContextMenu(element.id, element.documentationLocation, event, container);
				}
			} else {
				conDecContextMenu.createContextMenu(element.id, element.documentationLocation, event, container, 
					parentElement.id, parentElement.documentationLocation);
			}			
		});
	}

	function getTreeViewerNodeById (nodeId, treeId) {
		console.log("conDecTreeViewer getTreeViewerNodeById(nodeId)");
		if (nodeId === "#") {
			return nodeId;
		}
		return jQuery(treeId).jstree(true).get_node(nodeId);
	}

	/**
	 * called by condec.knowledge.page.js, condec.rationale.backlog.js
	 */
	ConDecTreeViewer.prototype.selectNodeInTreeViewer = function (nodeId, treeId) {
		console.log("conDecTreeViewer selectNodeInTreeViewer");
		jQuery(document).ready(function() {
			var treeViewer = jQuery(treeId).jstree(true);
			if (treeViewer) {
				treeViewer.deselect_all(true);
				treeViewer.select_node(nodeId);
			}
		});
	};
	
	global.conDecTreeViewer = new ConDecTreeViewer();
})(window);

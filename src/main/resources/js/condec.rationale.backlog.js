/*
 *	This view provides a backlog for decision knowledge.
 */
(function (global) {
	/* private vars */
	var conDecObservable = null;
	var conDecAPI = null;
	var conDecDialog = null;
	var conDecContextMenu = null;
	var treeViewer = null;
	var treant = null;

	var ConDecRationaleBacklog = function ConDecRationaleBacklog() {
	};

	ConDecRationaleBacklog.prototype.init = function (_conDecAPI, _conDecObservable, _conDecDialog, _conDecContextMenu,
														  _treant, _treeViewer) {
		console.log("conDecRationaleBacklog init");

		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
			&& isConDecDialogType(_conDecDialog) && isConDecContextMenuType(_conDecContextMenu)
			&& isConDecTreantType(_treant) && isConDecTreeViewerType(_treeViewer)) {

			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;
			conDecDialog = _conDecDialog;
			conDecContextMenu = _conDecContextMenu;
			treant = _treant;
			treeViewer = _treeViewer;

			// Register/subscribe this view as an observer
			conDecObservable.subscribe(this);

			return true;
		}
		return false;
	};


	ConDecRationaleBacklog.prototype.fetchAndRender = function () {
		initializeRationaleBacklog(conDecAPI, treant, treeViewer);
	};

	ConDecRationaleBacklog.prototype.updateView = function () {
		updateView(null, treant, treeViewer);
	};

	function initializeRationaleBacklog(conDecAPI, treant, treeViewer) {
		console.log("conDecRationaleBacklog initializeRationaleBacklog");

		conDecFiltering.addEventListenerToLinkDistanceInput("link-distance-input-rb", function() {
			updateView(null, treant, treeViewer);
		});

		conDecFiltering.initDropdown("knowledge-type-dropdown-rb", conDecAPI.getKnowledgeTypes());
		conDecFiltering.initDropdown("status-dropdown-rb", conDecAPI.rationaleBacklogItemStatus);
		var statusDropdown = document.getElementById("status-dropdown-rb");
		statusDropdown.addEventListener("change", function (e) {
			conDecRationaleBacklog.updateView();
		});
		conDecAPI.fillDecisionGroupSelect("select2-decision-group-rb");

		updateView(null, treant, treeViewer);
	}



	function updateView(nodeId, treant, treeViewer) {
		var knowledgeTypes = ["Issue", "Alternative", "Decision", "Pro", "Con"];
		var issueStatus = conDecAPI.issueStatus[1];
		var filterSettings = {
			"jiraIssueTypes": knowledgeTypes,
			"linkDistance": 0,
			"status" : [issueStatus]
		};
		treeViewer.buildTreeViewer(filterSettings, "#rationale-backlog-tree", "#text-search-input-rb", "rationale-backlog-tree");
		// if (nodeId === undefined) {
		// 	var rootElement = treant.getCurrentRootElement();
		// 	if (rootElement) {
		// 		treeViewer.selectNodeInTreeViewer(rootElement.id);
		// 	}
		// } else {
		// 	treeViewer.selectNodeInTreeViewer(nodeId);
		// }
		// jQueryConDec("#rationale-backlog-tree").on("select_node.jstree", function (error, tree) {
		// 	var node = tree.node.data;
		// 	var linkDistance = document.getElementById("link-distance-input-rb").value;
		// 	treant.buildRationaleBacklogTreant(node.key, true, "", linkDistance);
		// });
		// var selectedGroupsObj = $('#select2-code-decision-group-rb').select2('data');
		// var selectedGroups = [];
		// for (var i = 0; i <= selectedGroupsObj.length; i++) {
		// 	if (selectedGroupsObj[i]) {
		// 		selectedGroups[i] = selectedGroupsObj[i].text;
		// 	}
		// }
		// if (!selectedGroups === undefined || selectedGroups.length > 0) {
		// 	treeViewer.filterNodesByGroup(selectedGroups, "#rationale-backlog-tree");
		// }
		var selectedStatus = conDecFiltering.getSelectedItems("status-dropdown-rb");
		if (selectedStatus !== undefined && selectedStatus.length < conDecAPI.knowledgeStatus.length) {
			//treeViewer.filterNodesByStatus(selectedStatus, "#rationale-backlog-tree");
		}

	}



	/*
	* Init Helpers
	*/
	function isConDecAPIType(conDecAPI) {
		if (!(conDecAPI !== undefined && conDecAPI.getDecisionKnowledgeElement !== undefined && typeof conDecAPI.getDecisionKnowledgeElement === 'function')) {
			console.warn("ConDecCodeClassPage: invalid ConDecAPI object received.");
			return false;
		}
		return true;
	}

	function isConDecObservableType(conDecObservable) {
		if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
			console.warn("ConDecCodeClassPage: invalid ConDecObservable object received.");
			return false;
		}
		return true;
	}

	function isConDecDialogType(conDecDialog) {
		if (!(conDecDialog !== undefined && conDecDialog.showCreateDialog !== undefined && typeof conDecDialog.showCreateDialog === 'function')) {
			console.warn("ConDecCodeClassPage: invalid conDecDialog object received.");
			return false;
		}
		return true;
	}

	function isConDecContextMenuType(conDecContextMenu) {
		if (!(conDecContextMenu !== undefined && conDecContextMenu.createContextMenu !== undefined && typeof conDecContextMenu.createContextMenu === 'function')) {
			console.warn("ConDecCodeClassPage: invalid conDecContextMenu object received.");
			return false;
		}
		return true;
	}

	function isConDecTreantType(conDecTreant) {
		if (!(conDecTreant !== undefined && conDecTreant.buildTreant !== undefined && typeof conDecTreant.buildTreant === 'function')) {
			console.warn("ConDecCodeClassPage: invalid conDecTreant object received.");
			return false;
		}
		return true;
	}

	function isConDecTreeViewerType(treeViewer) {
		if (!(treeViewer !== undefined && treeViewer.selectNodeInTreeViewer !== undefined && typeof treeViewer.selectNodeInTreeViewer === 'function')) {
			console.warn("ConDecCodeClassPage: invalid treeViewer object received.");
			return false;
		}
		return true;
	}

	// export ConDecRationaleBacklog
	global.conDecRationaleBacklog = new ConDecRationaleBacklog();
})(window);
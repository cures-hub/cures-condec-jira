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

	var ConDecRationaleBacklog = function() {
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

		var knowledgeTypeDropdown = conDecFiltering.initDropdown("knowledge-type-dropdown-rb", conDecAPI.getKnowledgeTypes(), ["Alternative", "Decision", "Issue"]);
		knowledgeTypeDropdown.addEventListener("change", function() {
			conDecRationaleBacklog.updateView();
		});

		var statusDropdown = conDecFiltering.initDropdown("status-dropdown-rb", conDecAPI.rationaleBacklogItemStatus);
		statusDropdown.addEventListener("change", function () {
			conDecRationaleBacklog.updateView();
		});
		conDecAPI.fillDecisionGroupSelect("select2-decision-group-rb");

		var date = new Date();
		var endDatePicker = document.getElementById("end-date-picker-rb");
		endDatePicker.value = date.toISOString().substr(0, 10);
		date.setDate(date.getDate() - 7);
		endDatePicker.addEventListener("change", function () {
			conDecRationaleBacklog.updateView();
		});
		var startDatePicker =document.getElementById("start-date-picker-rb");
		startDatePicker.value = date.toISOString().substr(0, 10);
		startDatePicker.addEventListener("change", function () {
			conDecRationaleBacklog.updateView();
		});

		updateView(null, treant, treeViewer);
	}



	function updateView(nodeId, treant, treeViewer) {
		var knowledgeTypes = conDecFiltering.getSelectedItems("knowledge-type-dropdown-rb");
		var selectedStatus = conDecFiltering.getSelectedItems("status-dropdown-rb");
		var indexOfIncomplete = selectedStatus.indexOf("incomplete");
		var showIncomplete = false;
		if (indexOfIncomplete !== -1) {
			if (selectedStatus.length === 1) {
				selectedStatus = this.conDecAPI.knowledgeStatus;
			} else {
				selectedStatus.splice(indexOfIncomplete, 1);
			}
			showIncomplete = true;
		}
		var selectedGroups = conDecFiltering.getSelectedGroups("select2-decision-group-rb");
		var startDateString = document.getElementById("start-date-picker-rb").value;
		var startDateLong = new Date(startDateString).getTime();
		var endDateString = document.getElementById("end-date-picker-rb").value;
		var endDateLong = new Date(endDateString).getTime();
		var filterSettings = {
			"knowledgeTypes": knowledgeTypes,
			"linkDistance": 0,
			"status" : selectedStatus,
			"groups" : selectedGroups,
			"startDate" : startDateLong,
			"endDate" : endDateLong,
			"isIncompleteKnowledgeShown" : showIncomplete,
		};
		treeViewer.buildTreeViewer(filterSettings, "#rationale-backlog-tree", "#text-search-input-rb", "rationale-backlog-tree");
		if (nodeId === undefined) {
			var rootElement = treant.getCurrentRootElement();
			if (rootElement) {
				treeViewer.selectNodeInTreeViewer(rootElement.id, "#rationale-backlog-tree");
			}
		} else {
			treeViewer.selectNodeInTreeViewer(nodeId, "#rationale-backlog-tree");
		}
		jQueryConDec("#rationale-backlog-tree").on("select_node.jstree", function(error, tree) {
			var node = tree.node.data;
			filterSettings["knowledgeTypes"] = null;
			filterSettings["selectedElement"] = node.key;
			treant.buildTreant(filterSettings, true, "treant-rationale-backlog");
		});
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
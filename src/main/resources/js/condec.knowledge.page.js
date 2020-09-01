/*
 This view provides trees of knowledge elements.
 
 Requires
 * conDecAPI
 * conDecObservable
 * conDecContextMenu
 * conDecTreant
 * conDecTreeViewer

 Is referenced in HTML by
 * overview.vm
 */
(function(global) {
	/* private vars */
	var conDecObservable = null;
	var conDecAPI = null;
	var conDecDialog = null;
	var conDecContextMenu = null;
	var treant = null;
	var treeViewer = null;

	var ConDecKnowledgePage = function() {
	};

	ConDecKnowledgePage.prototype.init = function(_conDecAPI, _conDecObservable, _conDecDialog, _conDecContextMenu,
	        _treant, _treeViewer) {
		console.log("conDecKnowledgePage init");

		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
		        && isConDecDialogType(_conDecDialog) && isConDecContextMenuType(_conDecContextMenu)
		        && isConDecTreanType(_treant) && isConDecTreeViewerType(_treeViewer)) {

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

	ConDecKnowledgePage.prototype.fetchAndRender = function() {
		initializeDecisionKnowledgePage(conDecAPI, treant, treeViewer);
	};

	ConDecKnowledgePage.prototype.updateView = function() {
		updateView(null, treant, treeViewer);
	};

	function initializeDecisionKnowledgePage(conDecAPI, treant, treeViewer) {
		console.log("conDecKnowledgePage initializeDecisionKnowledgePage");
		var knowledgeTypes = conDecAPI.getKnowledgeTypes();
		for (var index = 0; index < knowledgeTypes.length; index++) {
			var isSelected = "";
			if (knowledgeTypes[index] === "Issue") {
				isSelected = "selected ";
			}
			jQueryConDec("select[name='knowledge-type-dropdown-overview']")[0].insertAdjacentHTML("beforeend", "<option "
			        + isSelected + " value='" + knowledgeTypes[index] + "'>" + knowledgeTypes[index] + "</option>");
		}

		var createElementButton = document.getElementById("create-element-button");
		var elementInputField = document.getElementById("element-input-field");
		conDecAPI.isIssueStrategy(function(isEnabled) {
			if (isEnabled) {
				createElementButton.addEventListener("click", function() {
					var summary = elementInputField.value;
					var type = jQueryConDec("select[name='knowledge-type-dropdown-overview']").val();
					elementInputField.value = "";
					conDecAPI.createDecisionKnowledgeElement(summary, "", type, "i", 0, null, function(id) {
						updateView(id, treant, treeViewer);
					});
				});
			} else {
				createElementButton.style.display = "none";
				elementInputField.style.display = "none";
			}
		});

		conDecFiltering.initDropdown("status-dropdown-overview", conDecAPI.knowledgeStatus);
		conDecAPI.fillDecisionGroupSelect("select2-decision-group-overview");
		conDecFiltering.addOnChangeEventToFilterElements("overview", conDecKnowledgePage.updateView, false);

		updateView(null, treant, treeViewer);
	}

	function updateView(nodeId, treant, treeViewer) {
		var filterSettings = conDecFiltering.getFilterSettings("overview");
		var knowledgeType = jQueryConDec("select[name='knowledge-type-dropdown-overview']").val();
		filterSettings["knowledgeTypes"] = [ knowledgeType ];
		filterSettings["linkDistance"] = 0; // so that jstree tree viewer only shows a list of elements
		treeViewer.buildTreeViewer(filterSettings, "#jstree", "#search-input-overview", "jstree");
		if (nodeId === undefined) {
			var rootElement = treant.getCurrentRootElement();
			if (rootElement) {
				treeViewer.selectNodeInTreeViewer(rootElement.id, "#jstree");
			}
		} else {
			treeViewer.selectNodeInTreeViewer(nodeId, "#jstree");
		}
		jQueryConDec("#jstree").on("select_node.jstree", function(error, tree) {
			var node = tree.node.data;
			var linkDistance = document.getElementById("link-distance-input-overview").value;
			filterSettings["linkDistance"] = linkDistance;
    		filterSettings["knowledgeTypes"] = null;
    		filterSettings["selectedElement"] = node.key;
	        treant.buildTreant(filterSettings, true);
		});
	}

	/*
	 * Init Helpers
	 */
	function isConDecAPIType(conDecAPI) {
		if (!(conDecAPI !== undefined && conDecAPI.getDecisionKnowledgeElement !== undefined && typeof conDecAPI.getDecisionKnowledgeElement === 'function')) {
			console.warn("ConDecKnowledgePage: invalid ConDecAPI object received.");
			return false;
		}
		return true;
	}

	function isConDecObservableType(conDecObservable) {
		if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
			console.warn("ConDecKnowledgePage: invalid ConDecObservable object received.");
			return false;
		}
		return true;
	}

	function isConDecDialogType(conDecDialog) {
		if (!(conDecDialog !== undefined && conDecDialog.showCreateDialog !== undefined && typeof conDecDialog.showCreateDialog === 'function')) {
			console.warn("ConDecKnowledgePage: invalid conDecDialog object received.");
			return false;
		}
		return true;
	}

	function isConDecContextMenuType(conDecContextMenu) {
		if (!(conDecContextMenu !== undefined && conDecContextMenu.createContextMenu !== undefined && typeof conDecContextMenu.createContextMenu === 'function')) {
			console.warn("ConDecKnowledgePage: invalid conDecContextMenu object received.");
			return false;
		}
		return true;
	}

	function isConDecTreanType(conDecTreant) {
		if (!(conDecTreant !== undefined && conDecTreant.buildTreant !== undefined && typeof conDecTreant.buildTreant === 'function')) {
			console.warn("ConDecKnowledgePage: invalid conDecTreant object received.");
			return false;
		}
		return true;
	}

	function isConDecTreeViewerType(treeViewer) {
		if (!(treeViewer !== undefined && treeViewer.selectNodeInTreeViewer !== undefined && typeof treeViewer.selectNodeInTreeViewer === 'function')) {
			console.warn("ConDecKnowledgePage: invalid treeViewer object received.");
			return false;
		}
		return true;
	}

	// export ConDecKnowledgePage
	global.conDecKnowledgePage = new ConDecKnowledgePage();
})(window);
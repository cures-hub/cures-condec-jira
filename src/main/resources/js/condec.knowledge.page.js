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
			// conDecObservable.subscribe(this);
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
			jQueryConDec("select[name='select-root-element-type']")[0].insertAdjacentHTML("beforeend", "<option "
			        + isSelected + " value='" + knowledgeTypes[index] + "'>" + knowledgeTypes[index] + "</option>");
		}

		var createElementButton = document.getElementById("create-element-button");
		var elementInputField = document.getElementById("element-input-field");
		conDecAPI.isIssueStrategy(function(isEnabled) {
			if (isEnabled) {
				createElementButton.addEventListener("click", function() {
					var summary = elementInputField.value;
					var type = jQueryConDec("select[name='select-root-element-type']").val();
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

		// @issue Should filters change all views or only the current view?
		// @decision Filters are only applied in the current view using updateView()!
		// @alternative We update all views using conDecObservable.notify()!
		// @pro The user could reuse the filter settings, which is more useable.
		// @con This would need more computation and decreases performance.
		conDecFiltering.addEventListenerToLinkDistanceInput("link-distance-input", conDecKnowledgePage.updateView);

		conDecAPI.fillDecisionGroupSelect("select2-decision-group");
		$("#select2-decision-group").on("change.select2", conDecKnowledgePage.updateView);

		var statusDropdown = conDecFiltering.initDropdown("status-dropdown-overview", conDecAPI.knowledgeStatus);
		statusDropdown.addEventListener("change", conDecKnowledgePage.updateView);

		var isOnlyDecisionKnowledgeShownInput = document.getElementById("is-decision-knowledge-only-input");
		isOnlyDecisionKnowledgeShownInput.addEventListener("change", conDecKnowledgePage.updateView);
		
		var minLinkNumberInput = document.getElementById("min-number-linked-issues-input");
		minLinkNumberInput.addEventListener("change", conDecKnowledgePage.updateView);

		var maxLinkNumberInput = document.getElementById("max-number-linked-issues-input");
		minLinkNumberInput.addEventListener("change", conDecKnowledgePage.updateView);

		updateView(null, treant, treeViewer);
	}

	function updateView(nodeId, treant, treeViewer) {
		var knowledgeType = jQueryConDec("select[name='select-root-element-type']").val();
		var selectedStatus = conDecFiltering.getSelectedItems("status-dropdown-overview");
		var knowledgeTypes = [ knowledgeType ];
		var selectedGroups = conDecFiltering.getSelectedGroups("select2-decision-group");
        var minLinkNumber = document.getElementById("min-number-linked-issues-input").value;
		var maxLinkNumber = document.getElementById("max-number-linked-issues-input").value;
		var filterSettings = {
			"knowledgeTypes" : knowledgeTypes,
			"status" : selectedStatus,
			"linkDistance" : 0,
			"groups" : selectedGroups,
			"minDegree" : minLinkNumber,
			"maxDegree" : maxLinkNumber
		};
		treeViewer.buildTreeViewer(filterSettings, "#jstree", "#jstree-search-input", "jstree");
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
			var isOnlyDecisionKnowledgeShown = document.getElementById("is-decision-knowledge-only-input").checked;
			var linkDistance = document.getElementById("link-distance-input").value;
			filterSettings["linkDistance"] = linkDistance;
    		filterSettings["isOnlyDecisionKnowledgeShown"] = isOnlyDecisionKnowledgeShown;
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
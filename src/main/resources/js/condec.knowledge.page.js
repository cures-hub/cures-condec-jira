/*
 This view provides trees of decision knowledge.
 
 Requires
 * conDecAPI
 * conDecObservable
 * conDecContextMenu
 * conDecTreant
 * conDecTreeViewer

 Is referenced in HTML by
 * decisionKnowledgePage.vm
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
		        && isConDecTreantType(_treant) && isConDecTreeViewerType(_treeViewer)) {

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

		conDecFiltering.addEventListenerToLinkDistanceInput("link-distance-input", function() {
			updateView(null, treant, treeViewer);
		});

		conDecAPI.fillDecisionGroupSelect("select2-decision-group");
		$("#select2-decision-group").on("change.select2", function(e) {
			// @issue Should filters change all views or only the current view?
			// @decision Filters are only applied in the current view using updateView()!
			// @alternative We update all views using conDecObservable.notify()!
			// @pro The user could reuse the filter settings, which is more useable.
			// @con This would need more computation and decreases performance.
			conDecKnowledgePage.updateView();
		});

		var statusDropdown = conDecFiltering.initDropdown("status-dropdown-overview", conDecAPI.knowledgeStatus);
		statusDropdown.addEventListener("change", function(e) {
			conDecKnowledgePage.updateView();
		});

		var isOnlyDecisionKnowledgeShownInput = document.getElementById("is-decision-knowledge-only-input");
		isOnlyDecisionKnowledgeShownInput.addEventListener("change", function(e) {
			conDecKnowledgePage.updateView();
		});

		updateView(null, treant, treeViewer);
	}

	function updateView(nodeId, treant, treeViewer) {
		var knowledgeType = jQueryConDec("select[name='select-root-element-type']").val();
		var selectedStatus = conDecFiltering.getSelectedItems("status-dropdown-overview");
		var knowledgeTypes = [ knowledgeType ];
		var selectedGroups = conDecFiltering.getSelectedGroups("select2-decision-group");
		var filterSettings = {
		    "jiraIssueTypes" : knowledgeTypes,
		    "linkDistance" : 0,
		    "status" : selectedStatus,
		    "groups" : selectedGroups
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
			treant.buildTreant(node.key, true, "", isOnlyDecisionKnowledgeShown, linkDistance);
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

	function isConDecTreantType(conDecTreant) {
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
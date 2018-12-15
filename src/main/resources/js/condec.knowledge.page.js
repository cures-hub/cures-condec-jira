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
	var i18n = null;
	var conDecObservable = null;
	var conDecAPI = null;
	var conDecDialog = null;
	var conDecContextMenu = null;
	var treant = null;
	var treeViewer = null;

	var ConDecKnowledgePage = function ConDecKnowledgePage() {
	};

	ConDecKnowledgePage.prototype.init = function(_conDecAPI, _conDecObservable, _conDecDialog, _conDecContextMenu,
			_treant, _treeViewer, _i18n) {
		console.log("conDecKnowledgePage init");

		// TODO: Add i18n support and check i18n
		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
				&& isConDecDialogType(_conDecDialog) && isConDecContextMenuType(_conDecContextMenu)
				&& isConDecTreantType(_treant) && isConDecTreeViewerType(_treeViewer)) {
			conDecAPI = _conDecAPI;

			conDecObservable = _conDecObservable;
			conDecDialog = _conDecDialog;
			conDecContextMenu = _conDecContextMenu;
			treant = _treant;
			treeViewer = _treeViewer;
			i18n = _i18n;
			
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

		var knowledgeTypes = conDecAPI.knowledgeTypes;
		for (var index = 0; index < knowledgeTypes.length; index++) {
			var isSelected = "";
			if (knowledgeTypes[index] === "Decision") {
				isSelected = "selected ";
			}
			jQueryConDec("select[name='select-root-element-type']")[0].insertAdjacentHTML("beforeend", "<option "
					+ isSelected + " value='" + knowledgeTypes[index] + "'>" + knowledgeTypes[index] + "</option>");
		}

		var createElementButton = document.getElementById("create-element-button");
		var elementInputField = document.getElementById("element-input-field");
		createElementButton.addEventListener("click", function() {
			var summary = elementInputField.value;
			var type = jQueryConDec("select[name='select-root-element-type']").val();
			elementInputField.value = "";
			conDecAPI.createDecisionKnowledgeElement(summary, "", type, "", 0, null, function(id) {
				updateView(id, treant, treeViewer);
			});
		});

		var depthOfTreeInput = document.getElementById("depth-of-tree-input");
		depthOfTreeInput.addEventListener("input", function() {
			var depthOfTreeWarningLabel = document.getElementById("depth-of-tree-warning");
			if (this.value > 0) {
				depthOfTreeWarningLabel.style.visibility = "hidden";
				updateView(null, treant, treeViewer);
			} else {
				depthOfTreeWarningLabel.style.visibility = "visible";
			}
		});

		updateView(null, treant, treeViewer);
	}

	function updateView(nodeId, treant, treeViewer) {
		console.log("conDecKnowledgePage updateView");
		treeViewer.buildTreeViewer();
		if (nodeId === undefined) {
			var rootElement = treant.getCurrentRootElement();
			if (rootElement) {
				treeViewer.selectNodeInTreeViewer(rootElement.id);
			}
		} else {
			treeViewer.selectNodeInTreeViewer(nodeId);
		}
		jQueryConDec("#jstree").on("select_node.jstree", function(error, tree) {
			var node = tree.node.data;
			treant.buildTreant(node.key, true, "");
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
/*
	This view provides a backlog for decision knowledge.
 */


(function (global) {
	/* private vars */
	var conDecAPI = null;
	var treeViewer = null;
	var treant = null;

	var ConDecRationaleBacklogPage = function () {
	};

	ConDecRationaleBacklogPage.prototype.init= function (_conDecAPI, _treant, _treeViewer) {
		console.log("conDecRationaleBacklogPage init");

		if (isConDecAPIType(_conDecAPI) && isConDecTreantType(_treant)
			&& isConDecTreeViewerType(_treeViewer)) {
			conDecAPI = _conDecAPI;
			treant = _treant;
			treeViewer = _treeViewer;
			return true;
		}
		return false;
	}


	ConDecRationaleBacklogPage.prototype.fetchAndRender = function () {
		initializeRationaleBacklogPage();
	}

	function initializeRationaleBacklogPage() {
		console.log("conDecRationaleBacklogPage initializeRationaleBacklogPage");
		conDecFiltering.initDropdown("knowledge-type-dropdown-rb", conDecAPI.getKnowledgeTypes());
		conDecFiltering.initDropdown("status-dropdown-rb", conDecAPI.rationaleBacklogItemStatus);
		conDecAPI.fillDecisionGroupSelect("select2-decision-group-rb");
		showTreeViewer(null, treeViewer)
	}

	function showTreeViewer(nodeID, treeViewer) {
		treeViewer.buildTreeViewer();
		if (nodeId === undefined) {
			var rootElement = treant.getCurrentRootElement();
			if (rootElement) {
				treeViewer.selectNodeInTreeViewer(rootElement.id);
			}
		} else {
			treeViewer.selectNodeInTreeViewer(nodeId);
		}
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

	function isConDecTreeViewerType(treeViewer) {
		if (!(treeViewer !== undefined && treeViewer.selectNodeInTreeViewer !== undefined && typeof treeViewer.selectNodeInTreeViewer === 'function')) {
			console.warn("ConDecKnowledgePage: invalid treeViewer object received.");
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

	global.conDecRationaleBacklogPage = new ConDecRationaleBacklogPage();
})(window);
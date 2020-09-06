/*
 *	This view provides a backlog for decision knowledge.
 */
(function (global) {

	var ConDecRationaleBacklog = function() {
	};

	ConDecRationaleBacklog.prototype.initView = function () {
		console.log("conDecRationaleBacklog initView");
			
		// Fill filter elements
		conDecFiltering.initDropdown("knowledge-type-dropdown-rationale-backlog", conDecAPI.getKnowledgeTypes(), ["Alternative", "Decision", "Issue"]);
		conDecFiltering.initDropdown("status-dropdown-rationale-backlog", conDecAPI.rationaleBacklogItemStatus);		
		conDecFiltering.fillDecisionGroupSelect("select2-decision-group-rationale-backlog");
		conDecFiltering.fillDatePickers("rationale-backlog", 30);
		
		// Add event listeners on filter HTML elements
		conDecFiltering.addOnChangeEventToFilterElements("rationale-backlog", conDecRationaleBacklog.updateView, false);

		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);
		
		// Fill view
		this.updateView();
	};

	ConDecRationaleBacklog.prototype.updateView = function () {
		updateView(null);
	};

	function updateView(nodeId) {		
		var filterSettings = conDecFiltering.getFilterSettings("rationale-backlog");
		
		// so that jstree tree viewer only shows a list of elements:
		filterSettings["linkDistance"] = 0; 
		
		conDecTreeViewer.buildTreeViewer(filterSettings, "#rationale-backlog-tree", "#search-input-rationale-backlog", "rationale-backlog-tree");
		if (nodeId === undefined) {
			var rootElement = treant.getCurrentRootElement();
			if (rootElement) {
				conDecTreeViewer.selectNodeInTreeViewer(rootElement.id, "#rationale-backlog-tree");
			}
		} else {
			conDecTreeViewer.selectNodeInTreeViewer(nodeId, "#rationale-backlog-tree");
		}
		
		addSelectNodeEventListenerToTreeViewer();
	}
	
	function addSelectNodeEventListenerToTreeViewer() {
		jQuery("#rationale-backlog-tree").on("select_node.jstree", function(error, tree) {
			var filterSettings = conDecFiltering.getFilterSettings("rationale-backlog");
			var node = tree.node.data;
			filterSettings["knowledgeTypes"] = null;
			filterSettings["status"] = null;
			filterSettings["selectedElement"] = node.key;
			conDecTreant.buildTreant(filterSettings, true, "treant-rationale-backlog");
		});
	}
	
	// export ConDecRationaleBacklog
	global.conDecRationaleBacklog = new ConDecRationaleBacklog();
})(window);
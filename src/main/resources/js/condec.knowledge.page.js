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

	var ConDecKnowledgePage = function() {
	};

	ConDecKnowledgePage.prototype.initView = function() {
		console.log("conDecKnowledgePage initView");

		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);

		// Fill view
		this.fetchAndRender();
	};

	ConDecKnowledgePage.prototype.fetchAndRender = function() {
		var selectedKnowledgeTypes = [];
		const urlParams = new URLSearchParams(window.location.href);
		if (urlParams.has("codeFileName")) {
			selectedKnowledgeTypes.push("Code");
		} else if (urlParams.has("type")) {
			selectedKnowledgeTypes.push(urlParams.get("type"));
		} else {
			selectedKnowledgeTypes.push("Issue");
		}
		conDecFiltering.fillFilterElements("overview", selectedKnowledgeTypes);

		// Add on click listeners to filter button
		conDecFiltering.addOnClickEventToFilterButton("overview", conDecKnowledgePage.updateView);
		conDecFiltering.addOnClickEventToCreateElementButton("overview", conDecKnowledgePage.updateView);
		conDecDecisionTable.addOnClickEventToDecisionTableButtons("overview");

		// Speed up view loading per default
		document.getElementById("is-only-flat-list-input-overview").checked = true;
		
		this.updateView();
	};

	ConDecKnowledgePage.prototype.updateView = function() {
		updateView(null);
	};

	function updateView(nodeId) {
		var filterSettings = conDecFiltering.getFilterSettings("overview");
		var isOnlyFlatListInput = document.getElementById("is-only-flat-list-input-overview");
		if (isOnlyFlatListInput.checked) {
			filterSettings.linkDistance = 0; // to speed-up loading
		}
		filterSettings.isOnlyDecisionKnowledgeShown = false; // since this only applies on right side
		filterSettings.selectedElement = null; // we want to have a list of elements on the left
		conDecTreeViewer.buildTreeViewer(filterSettings, "#jstree", "#search-input-overview", "jstree");
		if (nodeId === undefined) {
			var rootElement = treant.getCurrentRootElement();
			if (rootElement) {
				conDecTreeViewer.selectNodeInTreeViewer(rootElement.id, "#jstree");
			}
		} else {
			conDecTreeViewer.selectNodeInTreeViewer(nodeId, "#jstree");
		}

		addSelectNodeEventListenerToTreeViewer();
	}

	function addSelectNodeEventListenerToTreeViewer() {
		jQuery("#jstree").on("select_node.jstree", function(error, tree) {
			var filterSettings = conDecFiltering.getFilterSettings("overview");
			var node = tree.node.data;
			filterSettings["knowledgeTypes"] = null;
			filterSettings["status"] = null;
			conDecFiltering.setSelectedElement("overview", node.key);
			filterSettings["selectedElement"] = node.key;
			conDecTreant.buildTreant(filterSettings, true, "treant-overview");
			conDecTreeViewer.buildTreeViewer(filterSettings, "#jstree-overview", "#search-input-overview", "jstree-overview");
			jQuery("#jstree-overview").on("loaded.jstree", function() {
				jQuery("#jstree-overview").jstree("open_all");
			});
			conDecVis.buildVis(filterSettings, "graph-overview");
			conDecMatrix.buildMatrix(filterSettings, "overview");
			conDecDecisionTable.build(filterSettings, "overview", node);
			conDecQualityCheck.initView("overview", node);
		});
	}

	global.conDecKnowledgePage = new ConDecKnowledgePage();
})(window);
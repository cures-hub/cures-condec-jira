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
		var knowledgeTypes = conDecAPI.getKnowledgeTypes();
		for (var index = 0; index < knowledgeTypes.length; index++) {
			var isSelected = "";
			if (knowledgeTypes[index] === "Issue") {
				isSelected = "selected ";
			}
			jQuery("select[name='knowledge-type-dropdown-overview']")[0].insertAdjacentHTML("beforeend", "<option "
			        + isSelected + " value='" + knowledgeTypes[index] + "'>" + knowledgeTypes[index] + "</option>");
		}

		var createElementButton = document.getElementById("create-element-button");
		var elementInputField = document.getElementById("element-input-field");
		conDecAPI.isIssueStrategy(function(isEnabled) {
			if (isEnabled) {
				createElementButton.addEventListener("click", function() {
					var summary = elementInputField.value;
					var type = jQuery("select[name='knowledge-type-dropdown-overview']").val();
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
		conDecFiltering.fillDecisionGroupSelect("select2-decision-group-overview");
		
		conDecFiltering.addOnChangeEventToFilterElements("overview", conDecKnowledgePage.updateView, false);

		this.updateView();
	};

	ConDecKnowledgePage.prototype.updateView = function() {
		updateView(null);
	};

	function updateView(nodeId) {
		var filterSettings = conDecFiltering.getFilterSettings("overview");
		var knowledgeType = jQuery("select[name='knowledge-type-dropdown-overview']").val();
		filterSettings["knowledgeTypes"] = [ knowledgeType ];
		filterSettings["linkDistance"] = 0; // so that jstree tree viewer only shows a list of elements
		conDecTreeViewer.buildTreeViewer(filterSettings, "#jstree", "#search-input-overview", "jstree");
		if (nodeId === undefined) {
			var rootElement = treant.getCurrentRootElement();
			if (rootElement) {
				conDecTreeViewer.selectNodeInTreeViewer(rootElement.id, "#jstree");
			}
		} else {
			conDecTreeViewer.selectNodeInTreeViewer(nodeId, "#jstree");
		}
		jQuery("#jstree").on("select_node.jstree", function(error, tree) {
			var node = tree.node.data;
			var linkDistance = document.getElementById("link-distance-input-overview").value;
			filterSettings["linkDistance"] = linkDistance;
    		filterSettings["knowledgeTypes"] = null;
    		filterSettings["selectedElement"] = node.key;
	        conDecTreant.buildTreant(filterSettings, true);
		});
	}

	global.conDecKnowledgePage = new ConDecKnowledgePage();
})(window);
/*
 This tab panel view does:
 * show a tree of decision knowledge elements and other knowledge elements (requirements, tasks, bug reports, ...)
 * enable to filter the tree of knowledge

 Requires
 * conDecAPI
 * conDecObservable
 * conDecTreeViewer
 
 Is referenced in HTML by
 * tabPanel.vm 
 */
(function(global) {
	
	/* private vars */
	var contextMenu = null;
	var conDecAPI = null;
	var conDecObservable = null;
	var treeViewer = null;

	var ConDecIssueTab = function ConDecIssueTab() {
	};

	ConDecIssueTab.prototype.init = function init(_conDecAPI, _conDecObservable, _treeViewer, _contextMenu) {
		console.log("conDecIssueTab init");

		conDecAPI = _conDecAPI;
		conDecObservable = _conDecObservable;
		treeViewer = _treeViewer;
		contextMenu = _contextMenu;

		// Register/subscribe this view as an observer
		// conDecObservable.subscribe(this);

		return true;
	};

	ConDecIssueTab.prototype.fetchAndRender = function () {
		this.updateView();
	};

	/* triggered by onchange event in tabPanel.vm */
	ConDecIssueTab.prototype.updateView = function () {
		console.log("conDecIssueTab updateView");
		var selectedKnowledgeTypes = getSelectedKnowledgeTypes();
		var jiraIssueKey = conDecAPI.getIssueKey();
		var filterSettings = {
				"knowledgeTypes": selectedKnowledgeTypes,
				"selectedElement": jiraIssueKey
		};
		treeViewer.buildTreeViewer(filterSettings, "#jstree", "#jstree-search-input", "issue-container");
		jQuery("#jstree").on("loaded.jstree", function() {
			jQuery("#jstree").jstree("open_all");
		});
	};

	function getSelectedKnowledgeTypes() {
		console.log("conDecIssueTab getSelectedKnowledgeTypes");

		var allKnowledgeTypes = [ "Issue", "Decision", "Alternative", "Argument" ];
		var selectedKnowledgeTypes = [];

		for (var i = 0; i < allKnowledgeTypes.length; i++) {
			var isTypeSelected = document.getElementById(allKnowledgeTypes[i]).checked;
			if (isTypeSelected) {
				selectedKnowledgeTypes.push(allKnowledgeTypes[i]);
			}
		}
		return selectedKnowledgeTypes;
	}

	// export ConDecIssueTab
	global.conDecIssueTab = new ConDecIssueTab();
})(window);
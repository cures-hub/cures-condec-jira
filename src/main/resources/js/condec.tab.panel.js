/*
 This tab panel view does:
 * show a tree of decision knowledge and other knowledge (requirements, tasks, bug reports, ...)
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
		conDecObservable.subscribe(this);

		return true;
	};

	ConDecIssueTab.prototype.fetchAndRender = function() {
		buildTreeViewer([ "Issue", "Decision", "Alternative", "Argument" ]);
	};

	/* triggered by onchange event in tabPanel.vm */
	ConDecIssueTab.prototype.updateView = function updateView() {
		console.log("conDecIssueTab updateView");
		treeViewer.resetTreeViewer();
		var selectedKnowledgeTypes = getSelectedKnowledgeTypes();
		buildTreeViewer(selectedKnowledgeTypes);
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

	function buildTreeViewer(selectedKnowledgeTypes) {
		console.log("conDecIssueTab buildTreeViewer");

		var jiraIssueKey = conDecAPI.getIssueKey();
		conDecAPI.getTreeViewerForSingleElement(jiraIssueKey, selectedKnowledgeTypes, function(core) {
			console.log("conDecTabPanel getTreeViewerWithoutRootElement callback");

			jQueryConDec("#jstree").jstree({
			    "core" : core,
			    "plugins" : [ "dnd", "wholerow", "search", "sort", "state" ],
			    "search" : {
				    "show_only_matches" : true
			    },
			    "sort" : sortfunction
			});
			jQueryConDec("#jstree").on("loaded.jstree", function() {
				jQueryConDec("#jstree").jstree("open_all");
			});
			$("#jstree-search-input").keyup(function() {
				var searchString = $(this).val();
				jQueryConDec("#jstree").jstree(true).search(searchString);
			});
			treeViewer.addDragAndDropSupportForTreeViewer();
			treeViewer.addContextMenuToTreeViewer("issue-container");
		});
	}

	/* used by buildTreeViewer */
	function sortfunction(a, b) {
		a1 = this.get_node(a);
		b1 = this.get_node(b);
		if (a1.id > b1.id) {
			return 1;
		} else {
			return -1;
		}
	}

	// export ConDecIssueTab
	global.conDecIssueTab = new ConDecIssueTab();
})(window);
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
		buildTreeViewer([ true, true, true, true, true ]);
	};
	
	ConDecIssueTab.prototype.updateView = function updateView() {
		console.log("conDecIssueTab updateView");
		treeViewer.resetTreeViewer();
		var knowledgeTypeSelection = toggleSelectedDecisionElements();
		buildTreeViewer(knowledgeTypeSelection);
	};

	/* triggered by onchange event in tabPanel.vm */
	function toggleSelectedDecisionElements() {
		console.log("conDecIssueTab toggleSelectedDecisionElements");

		var decisionElements = [ "Issue", "Decision", "Alternative", "Argument", "Relevant" ];
		var checked = [];

		for (var i = 0; i < decisionElements.length; i++) {
			var check = document.getElementById(decisionElements[i]).checked;
			checked.push(check);
		}
		return checked;
	}

	function buildTreeViewer(knowledgeTypeSelection) {
		console.log("conDecIssueTab buildTreeViewer");

		var jiraIssueKey = conDecAPI.getIssueKey();
		conDecAPI.getTreeViewerForSingleElement(jiraIssueKey, knowledgeTypeSelection, function(core) {
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
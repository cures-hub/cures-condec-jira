/*
 This tab panel view does:
 * toggle sentence types
 * show a tree of relevant decision knowledge

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
		console.log("conDecTabPanel init");

		// TODO add simple type checks
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

	/* triggered by onchange event in tabPanel.vm */
	function toggleSelectedDecisionElements() {
		console.log("view.condec.tab.panel toggleSelectedDecisionElements");

		var decisionElements = [ "Issue", "Decision", "Alternative", "Argument", "Relevant" ];
		var checked = [];

		for (var i = 0; i < decisionElements.length; i++) {
			var check = document.getElementById(decisionElements[i]).checked;
			checked.push(check);
		}
		return checked;
	}

	ConDecIssueTab.prototype.updateView = function updateView() {
		console.log("view.condec.tab.panel updateView");
		treeViewer.resetTreeViewer();
		var checked = toggleSelectedDecisionElements();
		buildTreeViewer(checked);
	};

	/*
	 called by
	 * conDecTabPanel:callDialog
	 * view.context.menu.js
	 */
	function buildTreeViewer(knowledgeTypeSelection) {
		console.log("conDecTabPanel buildTreeViewer");

		conDecAPI.getTreeViewerWithoutRootElement(knowledgeTypeSelection, function(core) {
			console.log("conDecTabPanel getTreeViewerWithoutRootElement callback");

			jQueryConDec("#jstree").jstree({
				"core" : core,
				"plugins" : [ "dnd", "wholerow", "search", "sort", "state" ],
				"search" : {
					"show_only_matches" : true
				},
				"sort" : sortfunction
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

	// Expose methods:
	// for tabPanel.vm
	ConDecIssueTab.prototype.toggleSelectedDecisionElements = toggleSelectedDecisionElements;
	// for view.context.menu.js
	ConDecIssueTab.prototype.buildTreeViewer = buildTreeViewer;

	// export ConDecIssueTab
	global.conDecIssueTab = new ConDecIssueTab();
})(window);
/*
 This tab panel view does:
 * toggle sentence types
 * show a tree of relevant decision knowledge

 Requires
 * condec.api.js
 
 Is required by
 * tabPanel.vm 
 
 Is referenced in HTML by
 * tabPanel.vm 
 */
(function(global) {
	/* private vars */
	var contextMenu = null;
	var conDecAPI = null;
	var treeViewer = null;
	var i18n = null;

	var ConDecIssueTab = function ConDecIssueTab() {
	};

	// TODO Insert TreeViewer directly into Tab panel without dialog
	ConDecIssueTab.prototype.init = function init(_conDecAPI, _treeViewer, _contextMenu, _i18n) {
		console.log("view.tab.panel.js init");

		// TODO add simple type checks
		conDecAPI = _conDecAPI;
		treeViewer = _treeViewer;
		contextMenu = _contextMenu;
		i18n = _i18n;

		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);


		console.log(treeViewer)
		buildTreeViewer(true);


		return true;
	};

	/* triggered by onchange event in tabPanel.vm */
	function toggleSelectedDecisionElements(element) {
		console.log("view.tab.panel.js toggleSelectedDecisionElements");

		var decisionElements = [ "Issue", "Decision", "Alternative", "Pro", "Con" ];
		var sentences = document.getElementsByClassName(element.id);
		if (element.id !== "Relevant") {
			setVisibility(sentences, element.checked);
		} else if (element.id === "Relevant") {
			sentences = document.getElementsByClassName("isNotRelevant");
			setVisibility(sentences, element.checked);
		}
	}

	/* called by toggleSelectedDecisionElements */
	function setVisibility(sentences, checked) {
		for (var i = sentences.length - 1; i >= 0; i--) {
			if (checked) {
				sentences[i].style.visibility = 'visible';
			}
			if (!checked) {
				sentences[i].style.visibility = 'collapse';
			}
		}
	}


	/*

	 called by
	 * view.tab.panel.js:callDialog
	 * view.context.menu.js
	    lines: 414,617
	 */
	function buildTreeViewer(showRelevant) {
		console.log("view.tab.panel.js buildTreeViewer");

		conDecAPI.getTreeViewerWithoutRootElement(showRelevant, function(core) {
			console.log("view.tab.panel.js getTreeViewerWithoutRootElement callback");

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
			console.log(treeViewer)
			treeViewer.addDragAndDropSupportForTreeViewer();
			treeViewer.addContextMenuToTreeViewer();
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

	
	function updateView() {
		console.log("view.tabPanel updateView");
		treeViewer.resetTreeViewer();
		buildTreeViewer(true);
	}



	// Expose methods:
	ConDecIssueTab.prototype.updateView = updateView;
	// for tabPanel.vm
	ConDecIssueTab.prototype.toggleSelectedDecisionElements = toggleSelectedDecisionElements;
	// for view.context.menu.js
	ConDecIssueTab.prototype.buildTreeViewer = buildTreeViewer;

	// export ConDecIssueTab
	global.conDecIssueTab = new ConDecIssueTab();
})(window);
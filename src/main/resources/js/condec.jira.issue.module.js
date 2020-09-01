/*
 This view provides a tree of relevant decision knowledge in the JIRA issue view.

 Requires
 * conDecAPI
 * conDecObservable
 * conDecContextMenu
 * conDecTreant
 * conDecDecisionTable
 * conDecExport
 * conDecVis
 * conDecDialog

 Is referenced in HTML by
 * jiraIssueModule.vm
 */
(function(global) {
	/* private vars */
	var conDecAPI = null;
	var conDecObservable = null;
	var conDecDialog = null;
	var conDecContextMenu = null;
	var treant = null;
	var vis = null;
	var conDecDecisionTable = null;

	var issueKey = "";
	var search = "";

	var ConDecJiraIssueModule = function() {
		console.log("conDecJiraIssueModule constructor");
	};

	ConDecJiraIssueModule.prototype.init = function(_conDecAPI, _conDecObservable, _conDecDialog, _conDecContextMenu,
	        _treant, _vis, _conDecDecisionTable) {

		console.log("ConDecJiraIssueModule init");
		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
		        && isConDecDialogType(_conDecDialog) && isConDecContextMenuType(_conDecContextMenu)
		        && isConDecTreantType(_treant) && isConDecVisType(_vis)
		        && isConDecDecisionTableTyp(_conDecDecisionTable)) {

			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;
			conDecDialog = _conDecDialog;
			conDecContextMenu = _conDecContextMenu;
			treant = _treant;
			vis = _vis;
			conDecDecisionTable = _conDecDecisionTable;

			// Fill HTML elements for filter criteria
			conDecFiltering.fillFilterElements("treant");
			conDecFiltering.fillFilterElements("graph");
			
			// Add event listener on buttons
			conDecFiltering.addOnClickEventToFilterButton("graph", function(filterSettings) {
				issueKey = conDecAPI.getIssueKey();
				filterSettings["selectedElement"] = issueKey;
				vis.buildVis(filterSettings);
			});

			addOnClickEventToExportAsTable();
			conDecDecisionTable.addOnClickEventToDecisionTableButtons();

			// Register/subscribe this view as an observer
			conDecObservable.subscribe(this);
			return true;
		}
		return false;
	};

	ConDecJiraIssueModule.prototype.initView = function() {
		console.log("ConDecJiraIssueModule initView");
		issueKey = conDecAPI.getIssueKey();
		AJS.$("#menu-item-graph").click();
	};

	ConDecJiraIssueModule.prototype.showTreant = function () {
		console.log("ConDecJiraIssueModule showTreant");
		var filterSettings = conDecFiltering.getFilterSettings("treant");
		filterSettings["selectedElement"] = issueKey;
		var isTestCodeShown = document.getElementById("show-test-elements-input").checked;
		filterSettings["isTestCodeShown"] = isTestCodeShown;
		treant.buildTreant(filterSettings, true);
	};

	ConDecJiraIssueModule.prototype.addOnChangeEventToTreantFilters = function () {
		conDecFiltering.addOnChangeEventToFilterElements("treant", conDecJiraIssueModule.showTreant);
		var isTestCodeShownInput = document.getElementById("show-test-elements-input");
		isTestCodeShownInput.addEventListener("change", conDecJiraIssueModule.showTreant);
	};

	function getURLsSearch() {
		// get jql from url
		var search = global.location.search.toString();
		search = search.toString().replace("&", "§");
		// if search query does not exist check
		return search;
	}

	ConDecJiraIssueModule.prototype.updateView = function() {
		console.log("ConDecJiraIssueModule updateView");
		JIRA.trigger(JIRA.Events.REFRESH_ISSUE_PAGE, [ JIRA.Issue.getIssueId() ]);
	};

	function addOnClickEventToExportAsTable() {
		console.log("ConDecJiraIssueModule addOnClickEventToExportAsTable");

		var exportMenuItem = document.getElementById("export-as-table-link");

		exportMenuItem.addEventListener("click", function(event) {
			event.preventDefault();
			event.stopPropagation();
			conDecDialog.showExportDialog(JIRA.Issue.getIssueId(), "i");
		});
	}

	/*
	 * Init Helpers
	 */
	function isConDecAPIType(conDecAPI) {
		if (!(conDecAPI !== undefined && conDecAPI.getDecisionKnowledgeElement !== undefined && typeof conDecAPI.getDecisionKnowledgeElement === 'function')) {
			console.warn("ConDecJiraIssueModule: invalid ConDecAPI object received.");
			return false;
		}
		return true;
	}

	function isConDecObservableType(conDecObservable) {
		if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
			console.warn("ConDecJiraIssueModule: invalid ConDecObservable object received.");
			return false;
		}
		return true;
	}

	function isConDecDialogType(conDecDialog) {
		if (!(conDecDialog !== undefined && conDecDialog.showCreateDialog !== undefined && typeof conDecDialog.showCreateDialog === 'function')) {
			console.warn("ConDecJiraIssueModule: invalid conDecDialog object received.");
			return false;
		}
		return true;
	}

	function isConDecContextMenuType(conDecContextMenu) {
		if (!(conDecContextMenu !== undefined && conDecContextMenu.createContextMenu !== undefined && typeof conDecContextMenu.createContextMenu === 'function')) {
			console.warn("ConDecJiraIssueModule: invalid conDecContextMenu object received.");
			return false;
		}
		return true;
	}

	function isConDecTreantType(conDecTreant) {
		if (!(conDecTreant !== undefined && conDecTreant.buildTreant !== undefined && typeof conDecTreant.buildTreant === 'function')) {
			console.warn("ConDecJiraIssueModule: invalid conDecTreant object received.");
			return false;
		}
		return true;
	}

	function isConDecVisType(conDecVis) {
		if (!(conDecVis !== undefined && conDecVis.buildVis !== undefined && typeof conDecVis.buildVis === 'function')) {
			console.warn("ConDecJiraIssueModule: invalid conDecVis object received.");
			return false;
		}
		return true;
	}

	function isConDecDecisionTableTyp(conDecDecisionTable) {
		if (!(conDecVis !== undefined && conDecDecisionTable.loadDecisionProblems !== undefined && typeof conDecDecisionTable.loadDecisionProblems === 'function')) {
			console.warn("ConDecJiraIssueModule: ivalid conDecDecisionTable object received.");
			return false;
		}
		return true;
	}

	// export ConDecJiraIssueModule
	global.conDecJiraIssueModule = new ConDecJiraIssueModule();
})(window);
/*
 This view provides a tree of relevant decision knowledge in the JIRA issue view.

 Requires
 * conDecAPI
 * conDecObservable
 * conDecContextMenu
 * conDecTreant

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
	var decisionTable = null;

	var issueKey = "";
	var search = "";

	var ConDecJiraIssueModule = function() {
		console.log("conDecJiraIssueModule constructor");
	};

	ConDecJiraIssueModule.prototype.init = function(_conDecAPI, _conDecObservable, _conDecDialog, _conDecContextMenu,
	        _treant, _vis, _decisionTable) {

		console.log("ConDecJiraIssueModule init");
		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
		        && isConDecDialogType(_conDecDialog) && isConDecContextMenuType(_conDecContextMenu)
		        && isConDecTreantType(_treant) && isConDecVisType(_vis) && isConDecDecisionTableTyp(_decisionTable)) {//) {

			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;
			conDecDialog = _conDecDialog;
			conDecContextMenu = _conDecContextMenu;
			treant = _treant;
			vis = _vis;
			decisionTable = _decisionTable;

			// Register/subscribe this view as an observer
			conDecObservable.subscribe(this);

			addOnClickEventToExportAsTable();
			addOnClickEventToTab();
			addOnClickEventToFilterButton();
			addOnClickEventToDecisionTableButtons();
			addOnClickEventToTreantFilters();

			// initial call to api depending on selected tab!
			determineSelectedTab(window.location.href);
			return true;
		}
		return false;
	};

	ConDecJiraIssueModule.prototype.initView = function() {
		console.log("ConDecJiraIssueModule initView");
		issueKey = conDecAPI.getIssueKey();
		search = getURLsSearch();
		initFilter(issueKey, search);
	};

	function addOnClickEventToDecisionTableButtons() {
		document.getElementById("btnAddCriterion").addEventListener("click", function() {
			conDecDecisionTable.showAddCriteriaToDecisionTableDialog();
		});

		document.getElementById("btnAddAlternative").addEventListener("click", function() {
			conDecDecisionTable.showCreateDialogForIssue();
		});
	}

	function addOnClickEventToTab() {
		console.log("ConDecJiraIssueModule addOnClickEventVisualizationSelectionTab");

		AJS.$("#visualization-selection-tabs-menu").on("click", function(event) {
			event.preventDefault();
			event.stopPropagation();
			determineSelectedTab(event.target.href);
		});

		//initial call of active tab
		determineSelectedTab(AJS.$(".active-tab")[0].firstElementChild.href)
	}

	// TODO Only fill tab when clicking menu item for the first time
	function determineSelectedTab(href) {
		if (href === undefined || href.includes("#treant")) {
			AJS.tabs.change(jQuery('a[href="#treant"]'));
			showTreant();
		} else if (href.includes("#vis")) {
			AJS.tabs.change(jQuery('a[href="#vis"]'));
			showGraph();
		} else if (href.includes("#decisionTable")) {
			AJS.tabs.change(jQuery('a[href="#decisionTable"]'));
			showDecisionTable();
		} else if (href.includes("#duplicate-issues-tab")) {
			AJS.tabs.change(jQuery('a[href="#duplicate-issues-tab"]'));
			consistencyTabsModule.loadDuplicateData();
		} else if (href.includes("#related-issues-tab")) {
			AJS.tabs.change(jQuery('a[href="#related-issues-tab"]'));
			consistencyTabsModule.loadData();
		}
	}

	function addOnClickEventToFilterButton() {
		console.log("ConDecJiraIssueModule addOnClickEventToFilterButton");

		var filterButton = document.getElementById("filter-button");
		filterButton.addEventListener("click", function(event) {
			event.preventDefault();
			event.stopPropagation();
			applyFilters();
		});
	}

	function showTreant() {
		console.log("ConDecJiraIssueModule showTreant");
		issueKey = conDecAPI.getIssueKey();
		var isOnlyDecisionKnowledgeShown = document.getElementById("is-decision-knowledge-only-input").checked;
		var linkDistance = document.getElementById("link-distance-input").value;
		var search = document.getElementById("search-treant-input").value;
		var knowledgeTypes = conDecFiltering.getSelectedItems("knowledge-type-dropdown");
		var minLinkNumber = document.getElementById("min-number-linked-issues-input").value;
		var maxLinkNumber = document.getElementById("max-number-linked-issues-input").value;
		var isTestCodeShown = document.getElementById("show-test-elements-input").checked;
		var filterSettings = {
		    "searchTerm" : search,
		    "knowledgeTypes" : knowledgeTypes,
		    "isOnlyDecisionKnowledgeShown" : isOnlyDecisionKnowledgeShown,
		    "linkDistance" : linkDistance,
		    "selectedElement" : issueKey,
		    "isTestCodeShown" : isTestCodeShown,
		    "minDegree" : minLinkNumber,
		    "maxDegree" : maxLinkNumber
		};
		treant.buildTreant(filterSettings, true);
	}

	function showGraph() {
		console.log("ConDecJiraIssueModule showGraph");
		issueKey = conDecAPI.getIssueKey();
		applyFilters();
	}

	function showDecisionTable() {
		console.log("ConDecJiraIssueModule showDecisionTable");
		issueKey = conDecAPI.getIssueKey();
		decisionTable.loadDecisionProblems(issueKey);
	}

	function applyFilters() {
		var issueTypes = conDecFiltering.getSelectedItems("issuetype-dropdown");
		var createdAfter = -1;
		var createdBefore = -1;
		var status = conDecFiltering.getSelectedItems("status-dropdown");
		var documentationLocations = conDecFiltering.getSelectedItems("documentation-dropdown");
		var linkTypes = conDecFiltering.getSelectedItems("linktype-dropdown");

		var nodeDistance = 4;
		if (!isNaN(document.getElementById("created-after-picker").valueAsNumber)) {
			createdAfter = document.getElementById("created-after-picker").valueAsNumber;
		}
		if (!isNaN(document.getElementById("created-before-picker").valueAsNumber)) {
			createdBefore = document.getElementById("created-before-picker").valueAsNumber;
		}
		var nodeDistanceInput = document.getElementById("node-distance-picker");
		if (nodeDistanceInput !== null) {
			nodeDistance = nodeDistanceInput.value;
		}
		var filterSettings = {
				"startDate": createdAfter,
				"endDate": createdBefore,
				"documentationLocations": documentationLocations,
				"knowledgeTypes": issueTypes,
				"status": status,
				"linkTypes": linkTypes,
				"selectedElement": issueKey,
				"linkDistance": nodeDistance
		};
		vis.buildVis(filterSettings);
	}

	function initFilter(issueKey, search) {
		console.log("ConDecJiraIssueModule initFilter");
		
		conDecAPI.getLinkTypes(function (linkTypes) {
			var linkTypeArray = [];
			for (linkType in linkTypes) {
				if (linkType !== undefined) {
					linkTypeArray.push(linkType);
				}				
			}
			conDecFiltering.initDropdown("linktype-dropdown", linkTypeArray);
		});		

		// Graph view
		var firstDatePicker = document.getElementById("created-after-picker");
		var secondDatePicker = document.getElementById("created-before-picker");

		// Parses a Jira query (in JQL) into filter settings, uses the default settings in case no Jira query is provided
		conDecAPI.getFilterSettings(issueKey, search, function(filterData) {
			var allKnowledgeTypes = conDecAPI.getKnowledgeTypes();		
			var selectedKnowledgeTypes = filterData.knowledgeTypes;
			var status = conDecAPI.knowledgeStatus;
			var documentationLocation = filterData.documentationLocations;

			var knowledgeTypeDropdown = conDecFiltering.initDropdown("knowledge-type-dropdown", allKnowledgeTypes,
			        selectedKnowledgeTypes); // Tree view
			knowledgeTypeDropdown.addEventListener("change", showTreant);

			conDecFiltering.initDropdown("issuetype-dropdown", allKnowledgeTypes, selectedKnowledgeTypes); // graph view
			conDecFiltering.initDropdown("status-dropdown", status);
			conDecFiltering.initDropdown("documentation-dropdown", documentationLocation);
			if (filterData.startDate >= 0) {
				firstDatePicker.valueAsDate = new Date(filterData.startDate + 1000);
			}
			if (filterData.endDate >= 0) {
				secondDatePicker.valueAsDate = new Date(filterData.endDate + 1000);
			}
		});		
	}
	
	function addOnClickEventToTreantFilters() {
		conDecFiltering.addEventListenerToLinkDistanceInput("link-distance-input", showTreant);

		var isOnlyDecisionKnowledgeShownInput = document.getElementById("is-decision-knowledge-only-input");
		isOnlyDecisionKnowledgeShownInput.addEventListener("change", showTreant);

		var isTestCodeShownInput = document.getElementById("show-test-elements-input");
		isTestCodeShownInput.addEventListener("change", showTreant);

		var minLinkNumberInput = document.getElementById("min-number-linked-issues-input");
		minLinkNumberInput.addEventListener("change", showTreant);

		var maxLinkNumberInput = document.getElementById("max-number-linked-issues-input");
		minLinkNumberInput.addEventListener("change", showTreant);

		var searchInputTreant = document.getElementById("search-treant-input");
		searchInputTreant.addEventListener("change", showTreant);
	}

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
		;
		return true;
	}
	// export ConDecJiraIssueModule
	global.conDecJiraIssueModule = new ConDecJiraIssueModule();
})(window);
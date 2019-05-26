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
	var condecContextVis = null;

	var ConDecJiraIssueModule = function ConDecJiraIssueModule() {
		console.log("conDecJiraIssueModule constructor");
	};

	ConDecJiraIssueModule.prototype.init = function init(_conDecAPI, _conDecObservable, _conDecDialog,
			_conDecContextMenu, _treant, _vis, _condecContextVis) {

		console.log("ConDecJiraIssueModule init");

		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
				&& isConDecDialogType(_conDecDialog) && isConDecContextMenuType(_conDecContextMenu)
				&& isConDecTreantType(_treant) && isConDecVisType(_vis) && isCondecContextVisType(_condecContextVis)) {

			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;
			conDecDialog = _conDecDialog;
			conDecContextMenu = _conDecContextMenu;
			treant = _treant;
			vis = _vis;
			condecContextVis = _condecContextVis;

			// Register/subscribe this view as an observer
			conDecObservable.subscribe(this);

			addOnClickEventToExportAsTable();

			return true;
		}
		return false;
	};



	ConDecJiraIssueModule.prototype.initView = function initView() {
		console.log("ConDecJiraIssueModule initView");
        var issueKey = conDecAPI.getIssueKey();
        var search = getURLsSearch();
        AJS.$('#visualization-selection-tabs').on('tabSelect', function(e,o){
            console.log("tab switched");
            console.log(window);
            if (o.tab.attr("href")==="#treant") {
                window.conDecJiraIssueModule.initTreant();
            } else if (o.tab.attr("href")==="#vis"){
                window.conDecJiraIssueModule.initVis();
            }
        });
        AJS.$("#filter-button").on('click', function (e) {
            e.preventDefault();
            window.conDecJiraIssueModule.initVisFiltered();
        });
        initFilter(issueKey, search);
	};

    ConDecJiraIssueModule.prototype.initTreant = function initTreant() {
        console.log("ConDecJiraIssueModule initTreant");
        var issueKey = conDecAPI.getIssueKey();
        var search = getURLsSearch();
        treant.buildTreant(issueKey, true, search);
    };

    ConDecJiraIssueModule.prototype.initVis = function initVis() {
        var issueKey = conDecAPI.getIssueKey();
        var search = getURLsSearch();
        console.log("ConDecJiraIssueModule initVis");
        vis.buildVis(issueKey, search);
    };

    ConDecJiraIssueModule.prototype.initVisFiltered = function initVisFiltered() {
        var issueKey = conDecAPI.getIssueKey();
        var search = getURLsSearch();
        var issueTypes = "";
        var createdAfter = -1;
        var createdBefore = -1;
        var documentationLocation = "";
        var nodeDistance = 4;
        if (!isNaN(document.getElementById("created-after-picker").valueAsNumber)) {
            createdAfter = document.getElementById("created-after-picker").valueAsNumber;
        }
        if (!isNaN(document.getElementById("created-before-picker").valueAsNumber)) {
            createdBefore = document.getElementById("created-before-picker").valueAsNumber;
        }
        for (var i=0; i < AJS.$('#issuetype-dropdown').children().size(); i++) {
            if (typeof AJS.$('#issuetype-dropdown').children().eq(i).attr('checked') !== typeof undefined
                && AJS.$('#issuetype-dropdown').children().eq(i).attr('checked') !== false) {
                issueTypes = issueTypes + AJS.$('#issuetype-dropdown').children().eq(i).text();
                issueTypes= issueTypes + ",";
            }
        }
        for (var j=0; j < AJS.$('#documentation-dropdown').children().size(); j++) {
            if (typeof AJS.$('#documentation-dropdown').children().eq(j).attr('checked') !== typeof undefined
                && AJS.$('#documentation-dropdown').children().eq(j).attr('checked') !== false) {
                documentationLocation = documentationLocation + AJS.$('#documentation-dropdown').children().eq(j).text();
            }
        }
        var nodeDistanceInput = document.getElementById("node-distance-picker");
		if (nodeDistanceInput !== null) {
        	nodeDistance = nodeDistanceInput.value;
		}
        vis.buildVisFiltered(issueKey,search,nodeDistance,issueTypes,createdAfter,createdBefore,documentationLocation);
    };

    function initFilter(issueKey, search) {
        console.log("ConDecJiraIssueModule initFilter");
        var checkedItems;
        var issueTypeDropdown = document.getElementById("issuetype-dropdown");
        var firstDatePicker = document.getElementById("created-after-picker");
        var secondDatePicker = document.getElementById("created-before-picker");
        var documentationDropdown = document.getElementById("documentation-dropdown");
        conDecAPI.getFilterData(issueKey, search, function (filterData) {
            var allIssueTypes = filterData.allIssueTypes;
            var selectedIssueTypes = filterData.issueTypesMatchingFilter;
            var documentationLocation = filterData.documentationLocations;
            checkedItems =selectedIssueTypes;
            issueTypeDropdown.innerHTML = "";

            for (var index = 0; index < allIssueTypes.length; index++) {
                var isSelected = "";
                if (selectedIssueTypes.includes(allIssueTypes[index])) {
                    isSelected = "checked";
                }
                issueTypeDropdown.insertAdjacentHTML("beforeend","<aui-item-checkbox interactive " + isSelected + ">" +
                    allIssueTypes[index] + "</aui-item-checkbox>");
            }
            for (var count = 0; count < documentationLocation.length; count++) {

                documentationDropdown.insertAdjacentHTML("beforeend","<aui-item-checkbox interactive checked>" +
                    documentationLocation[count] + "</aui-item-checkbox>");
            }
            if (filterData.startDate >= 0) {
                firstDatePicker.valueAsDate = new Date(filterData.startDate+1000);
            }
            if (filterData.endDate >= 0) {
                secondDatePicker.valueAsDate = new Date(filterData.endDate+1000);
            }
        });
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

		exportMenuItem.addEventListener("click", function (event) {
			event.preventDefault();
			event.stopPropagation();
			AJS.dialog2("#export-dialog").show();
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

	function isCondecContextVisType(conDecContextVis) {
	    if (!(conDecContextVis !== undefined && conDecContextVis.createContextVis !== undefined && typeof conDecContextVis.createContextVis === 'function')) {
	        console.warn("ConDecJiraIssueModule: invalid conDecContextVis object received.");
	        return false;
        }
        return true;
    }

	// export ConDecJiraIssueModule
	global.conDecJiraIssueModule = new ConDecJiraIssueModule();
})(window);
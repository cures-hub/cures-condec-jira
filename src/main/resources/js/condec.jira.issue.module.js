/*
 This view provides a tree of relevant decision knowledge in the JIRA issue view.

 Requires
 * conDecAPI
 * conDecObservable
 * conDecContextMenu
 * conDecTreant
 * conDecDecisionTable
 
 Is referenced in HTML by
 * jiraIssueModule.vm
 */
(function (global) {
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

    var ConDecJiraIssueModule = function ConDecJiraIssueModule() {
        console.log("conDecJiraIssueModule constructor");
    };

    ConDecJiraIssueModule.prototype.init = function init(_conDecAPI, _conDecObservable, _conDecDialog,
                                                         _conDecContextMenu, _treant, _vis, _decisionTable) {
        if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
            && isConDecDialogType(_conDecDialog) && isConDecContextMenuType(_conDecContextMenu)
            && isConDecTreantType(_treant) && isConDecVisType(_vis) && isConDecDecisionTableTyp(_decisionTable)) {

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

            console.log("ConDecJiraIssueModule true");
            return true;
        }
        console.log("ConDecJiraIssueModule false");
        return false;
    };

    ConDecJiraIssueModule.prototype.initView = function initView() {
        console.log("ConDecJiraIssueModule initView");
        issueKey = conDecAPI.getIssueKey();
        search = getURLsSearch();
        initFilter(issueKey, search);
    };

    ConDecJiraIssueModule.prototype.applyClassViewFilters = function applyClassViewFilters() {
        showClassTreant();
    };

    ConDecJiraIssueModule.prototype.applyTreeVisFilters = function applyTreeVisFilters() {
        showTreant();
    };

    function addOnClickEventToTab() {
        console.log("ConDecJiraIssueModule addOnClickEventVisualizationSelectionTab");

        AJS.$("#visualization-selection-tabs").on("tabSelect", function (event) {
            event.preventDefault();
            event.stopPropagation();
            if (event.target.href === undefined) {
                return;
            }
            if (event.target.href.includes("#treant")) {
                showTreant();
            } else if (event.target.href.includes("#vis")) {
                showGraph();
            } else if (event.target.href.includes("#class-treant")) {
                showClassTreant();
            } else if (event.target.href.includes("#decisionTable")) {
                showDecisionTable();
            }
        });
    }

    function addOnClickEventToFilterButton() {
        console.log("ConDecJiraIssueModule addOnClickEventToFilterButton");

        var filterButton = document.getElementById("filter-button");

        filterButton.addEventListener("click", function (event) {
            event.preventDefault();
            event.stopPropagation();
            applyFilters();
        });
    }

    function showTreant() {
        console.log("ConDecJiraIssueModule showTreant");
        var showOtherJiraIssues = document.getElementById("show-elements-input").checked;
        treant.buildTreant(issueKey, true, search, showOtherJiraIssues);
    }

    function showClassTreant() {
        console.log("ConDecJiraIssueModule showTreant");
        treant.buildClassTreant(issueKey, true, search, true);
    }

    function showGraph() {
        console.log("ConDecJiraIssueModule showGraph");
        vis.buildVis(issueKey, search);
    }

    function showDecisionTable() {
        console.log("ConDecJiraIssueModule showDecisionTable");
        decisionTable.buildTable(issueKey);
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
        vis.buildVisFiltered(issueKey, search, nodeDistance, issueTypes, status, createdAfter, createdBefore, linkTypes,
            documentationLocations);
    }

    function initFilter(issueKey, search) {
        console.log("ConDecJiraIssueModule initFilter");
        var issueTypeDropdown = document.getElementById("issuetype-dropdown");
        var firstDatePicker = document.getElementById("created-after-picker");
        var secondDatePicker = document.getElementById("created-before-picker");

        conDecAPI.getFilterSettings(issueKey, search, function (filterData) {
            var allIssueTypes = filterData.jiraIssueTypes;
            var selectedIssueTypes = filterData.jiraIssueTypes;

            var status = conDecAPI.knowledgeStatus;

            var documentationLocation = filterData.documentationLocations;
            issueTypeDropdown.innerHTML = "";

            for (var index = 0; index < allIssueTypes.length; index++) {
                var isSelected = "";
                if (selectedIssueTypes.includes(allIssueTypes[index])) {
                    isSelected = "checked";
                }
                issueTypeDropdown.insertAdjacentHTML("beforeend", "<aui-item-checkbox interactive " + isSelected + ">"
                    + allIssueTypes[index] + "</aui-item-checkbox>");
            }
            conDecFiltering.initDropdown("status-dropdown", status);
            conDecFiltering.initDropdown("documentation-dropdown", documentationLocation);
            if (filterData.startDate >= 0) {
                firstDatePicker.valueAsDate = new Date(filterData.startDate + 1000);
            }
            if (filterData.endDate >= 0) {
                secondDatePicker.valueAsDate = new Date(filterData.endDate + 1000);
            }

            var linkTypes = conDecAPI.linkTypes;
            conDecFiltering.initDropdown("linktype-dropdown", linkTypes);
        });
    }

    function getURLsSearch() {
        // get jql from url
        var search = global.location.search.toString();
        search = search.toString().replace("&", "§");
        // if search query does not exist check
        return search;
    }

    ConDecJiraIssueModule.prototype.updateView = function () {
        console.log("ConDecJiraIssueModule updateView");
        JIRA.trigger(JIRA.Events.REFRESH_ISSUE_PAGE, [JIRA.Issue.getIssueId()]);
    };

    function addOnClickEventToExportAsTable() {
        console.log("ConDecJiraIssueModule addOnClickEventToExportAsTable");

        var exportMenuItem = document.getElementById("export-as-table-link");

        exportMenuItem.addEventListener("click", function (event) {
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
        if(!(conDecVis !== undefined && conDecDecisionTable.buildTable !== undefined && typeof conDecDecisionTable.buildTable === 'function')) {
            console.warn("ConDecJiraIssueModule: ivalid conDecDecisionTable object received.");
            return false;
        };
        return true;
    }
    // export ConDecJiraIssueModule
    global.conDecJiraIssueModule = new ConDecJiraIssueModule();
})(window);
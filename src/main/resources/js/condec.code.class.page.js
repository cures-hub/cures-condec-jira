/*
 This view provides trees of decision knowledge for code classes.
 
 Requires
 * conDecAPI
 * conDecObservable
 * conDecContextMenu
 * conDecTreant
 * conDecTreeViewer

 Is referenced in HTML by
 * decisionKnowledgePage.vm
 */
(function (global) {
    /* private vars */
    var conDecObservable = null;
    var conDecAPI = null;
    var conDecDialog = null;
    var conDecContextMenu = null;
    var treant = null;
    var treeViewer = null;

    var ConDecCodeClassPage = function ConDecCodeClassPage() {
    };

    ConDecCodeClassPage.prototype.init = function (_conDecAPI, _conDecObservable, _conDecDialog, _conDecContextMenu,
                                                   _treant, _treeViewer) {
        console.log("ConDecCodeClassPage init");

        if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
            && isConDecDialogType(_conDecDialog) && isConDecContextMenuType(_conDecContextMenu)
            && isConDecTreantType(_treant) && isConDecTreeViewerType(_treeViewer)) {

            conDecAPI = _conDecAPI;
            conDecObservable = _conDecObservable;
            conDecDialog = _conDecDialog;
            conDecContextMenu = _conDecContextMenu;
            treant = _treant;
            treeViewer = _treeViewer;

            // Register/subscribe this view as an observer
            // conDecObservable.subscribe(this);

            return true;
        }
        return false;
    };

    ConDecCodeClassPage.prototype.fetchAndRender = function () {
        initializeDecisionKnowledgePage(conDecAPI, treant, treeViewer);
    };

    ConDecCodeClassPage.prototype.updateView = function () {
        updateView(null, treant, treeViewer);
    };

    function initializeDecisionKnowledgePage(conDecAPI, treant, treeViewer) {
        console.log("ConDecCodeClassPage initializeDecisionKnowledgePage");

        conDecFiltering.addEventListenerToLinkDistanceInput("link-distance-input-code", function() {
        	conDecCodeClassPage.updateView(null, treant, treeViewer);
        });
        
        var isOnlyDecisionKnowledgeShownInput = document.getElementById("is-decision-knowledge-only-input-code");
		isOnlyDecisionKnowledgeShownInput.addEventListener("change", function(e) {
			conDecCodeClassPage.updateView();
		});

        conDecAPI.fillDecisionGroupSelect("select2-code-decision-group");

        updateView(null, treant, treeViewer);
    }

    function updateView(nodeId, treant, treeViewer) {
        /* get cache or server data? */
		var knowledgeTypes = ["codeClass"];
        var selectedGroups = conDecFiltering.getSelectedGroups("select2-code-decision-group");
        var minLinkNumber = document.getElementById("min-number-linked-issues-input").value;
		var maxLinkNumber = document.getElementById("max-number-linked-issues-input").value;
		var filterSettings = {
			"jiraIssueTypes" : knowledgeTypes,
			"linkDistance" : 0,
			"groups" : selectedGroups,
			"minDegree" : minLinkNumber,
			"maxDegree" : maxLinkNumber
		};
        treeViewer.buildTreeViewer(filterSettings, "#code-class-tree", "#jstree-search-input-code", "code-class-tree");
        if (nodeId === undefined) {
            var rootElement = treant.getCurrentRootElement();
            if (rootElement) {
                treeViewer.selectNodeInTreeViewer(rootElement.id, "#code-class-tree");
            }
        } else {
            treeViewer.selectNodeInTreeViewer(nodeId, "#code-class-tree");
        }
        jQueryConDec("#code-class-tree").on("select_node.jstree", function (error, tree) {
            var node = tree.node.data;
            var linkDistance = document.getElementById("link-distance-input-code").value;
            var isOnlyDecisionKnowledgeShown = document.getElementById("is-decision-knowledge-only-input").checked;
    		filterSettings["linkDistance"] = linkDistance;
    		filterSettings["isOnlyDecisionKnowledgeShown"] = isOnlyDecisionKnowledgeShown;
    		filterSettings["jiraIssueTypes"] = null;
    		filterSettings["selectedElement"] = node.key;
    		treant.buildTreant(filterSettings, true, "treant-container-class");
        });
    }

    /*
     * Init Helpers
     */
    function isConDecAPIType(conDecAPI) {
        if (!(conDecAPI !== undefined && conDecAPI.getDecisionKnowledgeElement !== undefined && typeof conDecAPI.getDecisionKnowledgeElement === 'function')) {
            console.warn("ConDecCodeClassPage: invalid ConDecAPI object received.");
            return false;
        }
        return true;
    }

    function isConDecObservableType(conDecObservable) {
        if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
            console.warn("ConDecCodeClassPage: invalid ConDecObservable object received.");
            return false;
        }
        return true;
    }

    function isConDecDialogType(conDecDialog) {
        if (!(conDecDialog !== undefined && conDecDialog.showCreateDialog !== undefined && typeof conDecDialog.showCreateDialog === 'function')) {
            console.warn("ConDecCodeClassPage: invalid conDecDialog object received.");
            return false;
        }
        return true;
    }

    function isConDecContextMenuType(conDecContextMenu) {
        if (!(conDecContextMenu !== undefined && conDecContextMenu.createContextMenu !== undefined && typeof conDecContextMenu.createContextMenu === 'function')) {
            console.warn("ConDecCodeClassPage: invalid conDecContextMenu object received.");
            return false;
        }
        return true;
    }

    function isConDecTreantType(conDecTreant) {
        if (!(conDecTreant !== undefined && conDecTreant.buildTreant !== undefined && typeof conDecTreant.buildTreant === 'function')) {
            console.warn("ConDecCodeClassPage: invalid conDecTreant object received.");
            return false;
        }
        return true;
    }

    function isConDecTreeViewerType(treeViewer) {
        if (!(treeViewer !== undefined && treeViewer.selectNodeInTreeViewer !== undefined && typeof treeViewer.selectNodeInTreeViewer === 'function')) {
            console.warn("ConDecCodeClassPage: invalid treeViewer object received.");
            return false;
        }
        return true;
    }

    // export ConDecCodeClassPage
    global.conDecCodeClassPage = new ConDecCodeClassPage();
})(window);
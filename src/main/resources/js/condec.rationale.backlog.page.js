/*
 This view provides the Rationale Backlog.
 
 Requires
 * conDecAPI
 * conDecObservable
 * conDecContextMenu
 * conDecTreant
 * conDecTreeViewer

 Is referenced in HTML by
 * rationaleBacklogView.vm
 */
(function (global) {
    /* private vars */
    var conDecObservable = null;
    var conDecAPI = null;
    var conDecDialog = null;
    var conDecContextMenu = null;
    var treant = null;
    var treeViewer = null;

    var RationaleBacklogPage = function () {
    };

    RationaleBacklogPage.prototype.init = function (_conDecAPI, _conDecObservable, _conDecDialog, _conDecContextMenu,
                                                   _treant, _treeViewer) {
        console.log("conRationaleBacklogPage init");

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
            conDecObservable.subscribe(this);

            return true;
        }
        return false;
    };

    RationaleBacklogPage.prototype.fetchAndRender = function () {
        initializeRationaleBacklogPage(conDecAPI, treant, treeViewer);
    };

    RationaleBacklogPage.prototype.updateView = function () {
        updateView(null, treant, treeViewer);
    };

    function initializeRationaleBacklogPage(conDecAPI, treant, treeViewer) {
        console.log("conRationaleBacklogPage initializeRationaleBacklogPage");
        var knowledgeTypes = conDecAPI.getKnowledgeTypes();
        for (var index = 0; index < knowledgeTypes.length; index++) {
            var isSelected = "";
            if (knowledgeTypes[index] === "Issue") {
                isSelected = "selected ";
            }
            jQueryConDec("select[name='select-root-element-type']")[0].insertAdjacentHTML("beforeend", "<option "
                + isSelected + " value='" + knowledgeTypes[index] + "'>" + knowledgeTypes[index] + "</option>");
        }

        var createElementButton = document.getElementById("create-element-button-backlog");
        var elementInputField = document.getElementById("element-input-field-backlog");
        conDecAPI.isIssueStrategy(function (isEnabled) {
            if (isEnabled) {
                createElementButton.addEventListener("click", function () {
                    var summary = elementInputField.value;
                    var type = jQueryConDec("select[name='select-root-element-type']").val();
                    elementInputField.value = "";
                    conDecAPI.createDecisionKnowledgeElement(summary, "", type, "i", 0, null, function (id) {
                        updateView(id, treant, treeViewer);
                    });
                });
            } else {
                createElementButton.style.display = "none";
                elementInputField.style.display = "none";
            }
        });

        conDecFiltering.addEventListenerToLinkDistanceInput("link-distance-input", function() {
        	updateView(null, treant, treeViewer);
        });

        conDecAPI.fillDecisionGroupSelect("select2-decision-group");
        $("#select2-decision-group").on("change.select2", function (e) {
            // @issue Should filters change all views or only the current view?
            // @decision Filters are only applied in the current view using updateView()!
            // @alternative We update all views using conDecObservable.notify()!
            // @pro The user could reuse the filter settings, which is more useable.
            // @con This would need more computation and decreases performance.
            conDecRationaleBacklogPage.updateView();
        });

        conDecFiltering.initDropdown("status-dropdown-rationale-backlog", conDecAPI.knowledgeStatus);
        var statusDropdown = document.getElementById("status-dropdown-rationale-backlog");
        statusDropdown.addEventListener("change", function (e) {
            conDecRationaleBacklogPage.updateView();
        });
        
        var isOnlyDecisionKnowledgeShownInput = document.getElementById("is-decision-knowledge-only-input");
        isOnlyDecisionKnowledgeShownInput.addEventListener("change", function (e) {
            conDecRationaleBacklogPage.updateView();
        });

        updateView(null, treant, treeViewer);
    }

    function updateView(nodeId, treant, treeViewer) {
        treeViewer.buildTreeViewer();
        if (nodeId === undefined) {
            var rootElement = treant.getCurrentRootElement();
            if (rootElement) {
                treeViewer.selectNodeInTreeViewer(rootElement.id);
            }
        } else {
            treeViewer.selectNodeInTreeViewer(nodeId);
        }
        jQueryConDec("#jstree").on("select_node.jstree", function (error, tree) {
            var node = tree.node.data;
            var isOnlyDecisionKnowledgeShown = document.getElementById("is-decision-knowledge-only-input").checked;
            var linkDistance = document.getElementById("link-distance-input").value;
            treant.buildTreant(node.key, true, "", isOnlyDecisionKnowledgeShown, linkDistance);
        });

        var selectedGroupsObj = $('#select2-decision-group').select2('data');
        var selectedGroups = [];
        for (var i = 0; i <= selectedGroupsObj.length; i++) {
            if (selectedGroupsObj[i]) {
                selectedGroups[i] = selectedGroupsObj[i].text;
            }
        }
        if (selectedGroups !== undefined && selectedGroups.length > 0) {
            treeViewer.filterNodesByGroup(selectedGroups, "#jstree");
        }

        var selectedStatus = conDecFiltering.getSelectedItems("status-dropdown-rationale-backlog");
        if (selectedStatus !== undefined && selectedStatus.length < conDecAPI.knowledgeStatus.length) {
            treeViewer.filterNodesByStatus(selectedStatus, "#jstree");
        }
    }

    /*
     * Init Helpers
     */
    function isConDecAPIType(conDecAPI) {
        if (!(conDecAPI !== undefined && conDecAPI.getDecisionKnowledgeElement !== undefined && typeof conDecAPI.getDecisionKnowledgeElement === 'function')) {
            console.warn("ConDecRationaleBacklogPage: invalid ConDecAPI object received.");
            return false;
        }
        return true;
    }

    function isConDecObservableType(conDecObservable) {
        if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
            console.warn("ConDecRationaleBacklogPage: invalid ConDecObservable object received.");
            return false;
        }
        return true;
    }

    function isConDecDialogType(conDecDialog) {
        if (!(conDecDialog !== undefined && conDecDialog.showCreateDialog !== undefined && typeof conDecDialog.showCreateDialog === 'function')) {
            console.warn("ConDecRationaleBacklogPage: invalid conDecDialog object received.");
            return false;
        }
        return true;
    }

    function isConDecContextMenuType(conDecContextMenu) {
        if (!(conDecContextMenu !== undefined && conDecContextMenu.createContextMenu !== undefined && typeof conDecContextMenu.createContextMenu === 'function')) {
            console.warn("ConDecRationaleBacklogPage: invalid conDecContextMenu object received.");
            return false;
        }
        return true;
    }

    function isConDecTreantType(conDecTreant) {
        if (!(conDecTreant !== undefined && conDecTreant.buildTreant !== undefined && typeof conDecTreant.buildTreant === 'function')) {
            console.warn("ConDecRationaleBacklogPage: invalid conDecTreant object received.");
            return false;
        }
        return true;
    }

    function isConDecTreeViewerType(treeViewer) {
        if (!(treeViewer !== undefined && treeViewer.selectNodeInTreeViewer !== undefined && typeof treeViewer.selectNodeInTreeViewer === 'function')) {
            console.warn("ConDecRationaleBacklogPage: invalid treeViewer object received.");
            return false;
        }
        return true;
    }

    // export ConDecRationaleBacklogPage
    global.conDecRationaleBacklogPage = new RationaleBacklogPage();
})(window);
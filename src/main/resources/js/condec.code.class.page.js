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
            conDecObservable.subscribe(this);

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
        var knowledgeTypes = conDecAPI.knowledgeTypes;
        console.log(knowledgeTypes.length);
        for (var index = 0; index < knowledgeTypes.length; index++) {
            var isSelected = "";
            if (knowledgeTypes[index] === "Issue") {
                isSelected = "selected ";
            }
            jQueryConDec("select[name='select-root-element-type-code']")[0].insertAdjacentHTML("beforeend", "<option "
                + isSelected + " value='" + knowledgeTypes[index] + "'>" + knowledgeTypes[index] + "</option>");
        }

        var createElementButton = document.getElementById("create-element-button-code");
        var elementInputField = document.getElementById("element-input-field-code");
        conDecAPI.isIssueStrategy(function (isEnabled) {
            if (isEnabled) {
                createElementButton.addEventListener("click", function () {
                    var summary = elementInputField.value;
                    var type = jQueryConDec("select[name='select-root-element-type-code']").val();
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

        var depthOfTreeInput = document.getElementById("depth-of-tree-input");
        depthOfTreeInput.addEventListener("input", function () {
            var depthOfTreeWarningLabel = document.getElementById("depth-of-tree-warning");
            if (this.value > 0) {
                depthOfTreeWarningLabel.style.visibility = "hidden";
                updateView(null, treant, treeViewer);
            } else {
                depthOfTreeWarningLabel.style.visibility = "visible";
            }
        });

        conDecAPI.fillDecisionGroupSelect("select2-decision-group-code");

        updateView(null, treant, treeViewer);
    }

    function updateView(nodeId, treant, treeViewer) {
        /* get cache or server data? */
        treeViewer.buildClassTreeViewer();
        if (nodeId === undefined) {
            var rootElement = treant.getCurrentRootElement();
            if (rootElement) {
                treeViewer.selectNodeInTreeViewer(rootElement.id);
            }
        } else {
            treeViewer.selectNodeInTreeViewer(nodeId);
        }
        jQueryConDec("#jstree-code").on("select_node.jstree", function (error, tree) {
            var node = tree.node.data;
            treant.buildClassTreant(node.key, true, "", node.description);
        });
        /* Have to change filterNodesByGroup
        var selectedGroupsObj = $('#select2-decision-group-code').select2('data');
        var selectedGroups = [];
        for(var i=0; i<= selectedGroupsObj.length; i++){
            if(selectedGroupsObj[i]){
                selectedGroups[i] = selectedGroupsObj[i].text;
            }
        }
        if(!selectedGroups===undefined || selectedGroups.length>0){
            treeViewer.filterNodesByGroup(selectedGroups);
        } */
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
    global.ConDecCodeClassPage = new ConDecCodeClassPage();
})(window);
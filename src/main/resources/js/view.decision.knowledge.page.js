/*
 This decision knowledge view controller does:
 * render decision tree
 * provide a list of action items for the context menu

 Requires
 * rest.client.js
 * management.js
 * view.treant.js
 * view.tree.viewer.js
 * view.context.menu.js

 Required by
 * view.context.menu.js
 * decisionKnowledgePage.vm

 Referenced in HTML by
 * decisionKnowledgePage.vm
*/
(function (global) {
    /* private vars */
    var contextMenu = null;
    var i18n = null;
    var management = null;
    var restClient = null;
    var treant = null;
    var treeViewer = null;

    var ConDecKnowledgePage = function ConDecKnowledgePage() {
    };

    ConDecKnowledgePage.prototype.init = function(_restClient, _management, _treant, _treeViewer, _contextMenu, _i18n) {
        console.log("view.decision.knowledge.page init");

        if ( isConDecRestClientType(_restClient) &&
             isConDecManagementType(_management) &&
             isConDecTreantType(_treant) &&
             isConDecTreeViewerType(_treeViewer) &&
             isConDecContextType(_contextMenu) // not using and thus not checking i18n yet.
            ) {
            restClient = _restClient;
            management = _management;
            treant = _treant;
            treeViewer = _treeViewer;
            contextMenu = _contextMenu;
            i18n = _i18n;

            return true;
            }
        return false;
    }

    ConDecKnowledgePage.prototype.fetchAndRender = function () {
        initializeDecisionKnowledgePage(restClient, management, treant, treeViewer);
    }

    ConDecKnowledgePage.prototype.setAsRootElement = function setAsRootElement(id) {
        console.log("view.decision.knowledge.page setAsRootElement");
        treeViewer.selectNodeInTreeViewer(nodeId);
    }

    ConDecKnowledgePage.prototype.openIssue = function openIssue(nodeId) {
        console.log("view.decision.knowledge.page openIssue");

        // only allow this in case the selected node is a JIRA issue, for sentences map to JIRA issue
        restClient.getDecisionKnowledgeElement(nodeId, function(decisionKnowledgeElement) {
            var baseUrl = AJS.params.baseURL;
            var key = decisionKnowledgeElement.key;
            window.open(baseUrl + "/browse/" + key, '_blank');
        });
    }

    function initializeDecisionKnowledgePage(restClient, management, treant, treeViewer) {
        console.log("view.decision.knowledge.page initializeDecisionKnowledgePage");

        var knowledgeTypes = management.knowledgeTypes;
        for (var index = 0; index < knowledgeTypes.length; index++) {
            var isSelected = "";
            if (knowledgeTypes[index] === "Decision") {
                isSelected = "selected ";
            }
            jQueryConDec("select[name='select-root-element-type']")[0].insertAdjacentHTML("beforeend", "<option " + isSelected + " value='"
                    + knowledgeTypes[index] + "'>" + knowledgeTypes[index] + "</option>");
        }

        var createElementButton = document.getElementById("create-element-button");
        var elementInputField = document.getElementById("element-input-field");
        createElementButton.addEventListener("click", function() {
            var summary = elementInputField.value;
            var type = jQueryConDec("select[name='select-root-element-type']").val();
            elementInputField.value = "";
            restClient.createDecisionKnowledgeElement(summary, "", type, function(id) {
                updateView(id,treant,treeViewer);
            });
        });

        var depthOfTreeInput = document.getElementById("depth-of-tree-input");
        depthOfTreeInput.addEventListener("input", function() {
            var depthOfTreeWarningLabel = document.getElementById("depth-of-tree-warning");
            if (this.value > 0) {
                depthOfTreeWarningLabel.style.visibility = "hidden";
                updateView(null,treant,treeViewer);
            } else {
                depthOfTreeWarningLabel.style.visibility = "visible";
            }
        });

        updateView(null,treant,treeViewer);
    }

    function updateView(nodeId, treant, treeViewer) {
        console.log("view.decision.knowledge.page updateView");
        treeViewer.buildTreeViewer();
        if (nodeId === undefined) {
            var rootElement = treant.getCurrentRootElement();
            if (rootElement) {
                treeViewer.selectNodeInTreeViewer(rootElement.id);
            }
        } else {
            treeViewer.selectNodeInTreeViewer(nodeId);
        }
        jQueryConDec("#jstree").on("select_node.jstree", function(error, tree) {
            var node = tree.node.data;
            treant.buildTreant(node.key, true,"",getContextMenuActionsForTreant(contextMenu));
        });
    }


    // for view.context.menu
    function getContextMenuActionsForTreant(contextMenu) {
        console.log("view.decision.knowledge.page getContextMenuActionsForTreant");
        var menu =
		{ "asRoot" : contextMenu.contextMenuSetAsRootAction
		, "create" : contextMenu.contextMenuCreateAction
		, "edit" : contextMenu.contextMenuEditAction
		, "link" : contextMenu.contextMenuLinkAction
		, "deleteLink" : contextMenu.contextMenuDeleteLinkAction
		, "delete" : contextMenu.contextMenuDeleteAction
		, "openIssue" : contextMenu.contextMenuOpenJiraIssueAction
        };
        return menu;
	};


    /*
     Init Helpers
    */
    function isConDecRestClientType(restClient) {
        if (!( restClient!=undefined
                && restClient.getDecisionKnowledgeElement!=undefined
                && typeof restClient.getDecisionKnowledgeElement === 'function' ))
            {
                console.warn("ConDecKnowledgePage: invalid restClient object received.")
                return false;
            }
        return true;
    }

    function isConDecManagementType(management) {
        if (!( management!=undefined
            && management.getIssueKey!=undefined
            && typeof management.getIssueKey === 'function' ))
        {
            console.warn("ConDecKnowledgePage: invalid management object received.")
            return false;
        }
        return true;
    }

    function isConDecTreantType(treant) {
        if (!( treant!=undefined
            && treant.buildTreant!=undefined
            && typeof treant.buildTreant === 'function' ))
        {
            console.warn("ConDecKnowledgePage: invalid treant object received.")
            return false;
        }
        return true;
    }

    function isConDecContextType(contextMenu) {
        if (!( contextMenu!=undefined
            && contextMenu.setUpDialog!=undefined
            && typeof contextMenu.setUpDialog === 'function' ))
        {
            console.warn("ConDecKnowledgePage: invalid contextMenu object received.")
            return false;
        }
        return true;
    }

    function isConDecTreeViewerType(treeViewer) {
        if (!( treeViewer!=undefined
            && treeViewer.selectNodeInTreeViewer!=undefined
            && typeof treeViewer.selectNodeInTreeViewer === 'function' ))
        {
            console.warn("ConDecKnowledgePage: invalid treeViewer object received.")
            return false;
        }
        return true;
    }
    // export ConDecKnowledgePage
    global.conDecKnowledgePage = new ConDecKnowledgePage();
})(window);
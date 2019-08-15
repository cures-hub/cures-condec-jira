/*
 This module is responsible for showing a context menu upon right mouse click.

 Requires
 * conDecAPI
 * conDecDialog
 * conDecTreant

 Is required by
 * conDecTreant
 * conDecTreeViewer
 */
(function(global) {

    var isContextVisOpen = null;
    var contextVisNode = null;
    var contextVisForSentencesNode = null;

    var ConDecContextVis = function ConDecContextVis() {
        console.log("conDecContextVis constructor");
        isContextVisOpen = false;
        jQueryConDec(global).blur(hideContextVis);
        jQueryConDec(document).click(hideContextVis);
    };

    function hideContextVis() {
        /*
         * @issue This event gets launched many times at the same time! Check
         * what fires it. Probably more and more onclick event handlers get
         * added instead of just one.
         *
         * @decision On click and on blur event handlers are only set in the
         * constructor (see above).
         */
        if (isContextVisOpen) {
            console.log("contextmenu closed");
            if (contextVisNode) {
                contextVisNode.setAttribute('aria-hidden', 'true');
            }
            if (contextVisForSentencesNode) {
                contextVisForSentencesNode.setAttribute('aria-hidden', 'true');
            }
        }
        isContextVisOpen = false;
    }

    /*
     * external references: condec.treant, condec.tree.viewer
     */
    ConDecContextVis.prototype.createContextVis = function createContextVis(id, documentationLocation, event) {
        console.log("contextmenu opened");
        isContextVisOpen = true;

        contextVisNode = document.getElementById("condec-context-menu");
        if (!contextVisNode) {
            console.error("contextmenu not found");
            return;
        }

        setContextMenuItemsEventHandlers(id, documentationLocation);

        $(contextVisNode).css({
            left : event.layerX + "px",
            top : event.screenY + "px"
        });

        contextVisNode.style.zIndex = 9998; // why this number?
        contextVisNode.setAttribute('aria-hidden', 'false');


        document.getElementById("condec-context-menu-set-root-item").style.display = "none";
        document.getElementById("condec-context-menu-delete-link-item").style.display = "none";
        if (documentationLocation === "s") {
            document.getElementById("condec-context-menu-link-item").style.display = "none";
            document.getElementById("condec-context-menu-sentence-irrelevant-item").style.display = "initial";
            document.getElementById("condec-context-menu-sentence-convert-item").style.display = "initial";
            document.getElementById("condec-context-menu-set-root-item").style.display = "none";
        } else {
            document.getElementById("condec-context-menu-link-item").style.display = "initial";
            document.getElementById("condec-context-menu-sentence-irrelevant-item").style.display = "none";
            document.getElementById("condec-context-menu-sentence-convert-item").style.display = "none";
        }
    };

    function setContextMenuItemsEventHandlers(id, documentationLocation) {
        document.getElementById("condec-context-menu-create-item").onclick = function() {
            conDecDialog.showCreateDialog(id, documentationLocation);
        };

        document.getElementById("condec-context-menu-edit-item").onclick = function() {
            conDecDialog.showEditDialog(id, documentationLocation);
        };

        document.getElementById("condec-context-menu-change-type-item").onclick = function() {
            conDecDialog.showChangeTypeDialog(id, documentationLocation);
        };

        document.getElementById("condec-context-menu-change-status-item").onclick = function () {
            conDecDialog.showChangeStatusDialog(id,documentationLocation);
        };

        document.getElementById("condec-context-menu-issue-item").onclick = function() {
            conDecAPI.changeKnowledgeType(id, "Issue", documentationLocation, function() {
                conDecObservable.notify();
            });
        };

        document.getElementById("condec-context-menu-decision-item").onclick = function() {
            conDecAPI.changeKnowledgeType(id, "Decision", documentationLocation, function() {
                conDecObservable.notify();
            });
        };

        document.getElementById("condec-context-menu-alternative-item").onclick = function() {
            conDecAPI.changeKnowledgeType(id, "Alternative", documentationLocation, function() {
                conDecObservable.notify();
            });
        };

        document.getElementById("condec-context-menu-pro-item").onclick = function() {
            conDecAPI.changeKnowledgeType(id, "Pro", documentationLocation, function() {
                conDecObservable.notify();
            });
        };

        document.getElementById("condec-context-menu-con-item").onclick = function() {
            conDecAPI.changeKnowledgeType(id, "Con", documentationLocation, function() {
                conDecObservable.notify();
            });
        };

        // only default documentation location
        // TODO enable linking of existing elements for every documentation
        // location
        document.getElementById("condec-context-menu-link-item").onclick = function() {
            conDecDialog.showLinkDialog(id, documentationLocation);
        };

        document.getElementById("condec-context-menu-delete-link-item").onclick = function() {
            conDecDialog.showDeleteLinkDialog(id, documentationLocation);
        };

        document.getElementById("condec-context-menu-summarized-code").onclick = function() {
            conDecDialog.showSummarizedDialog(id, documentationLocation);
        };

        document.getElementById("condec-context-menu-delete-item").onclick = function() {
            conDecDialog.showDeleteDialog(id, documentationLocation);
        };

        document.getElementById("condec-context-menu-open-jira-issue-item").onclick = function() {
            conDecAPI.openJiraIssue(id, documentationLocation);
        };

        // only for sentences
        document.getElementById("condec-context-menu-sentence-irrelevant-item").onclick = function() {
            conDecAPI.setSentenceIrrelevant(id, function() {
                conDecObservable.notify();
            });
        };

        document.getElementById("condec-context-menu-sentence-convert-item").onclick = function() {
            conDecAPI.createIssueFromSentence(id, function() {
                conDecObservable.notify();
            });
        };
        document.getElementById("condec-context-menu-export").onclick = function() {
            conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function(decisionKnowledgeElement) {
                conDecDialog.showExportDialog(decisionKnowledgeElement.key);
            });
        };
    }

    // export ConDecContextVis
    global.conDecContextVis = new ConDecContextVis();
})(window);
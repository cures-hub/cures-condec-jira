/*
 This tab view controller does:
 * toggle sentence types
 * open the dialog box

 Requires
 * rest.client.js
 
 Required by
 * view.context.menu.js
 * tabPanel.vm 
 
 Referenced in HTML by
 * tabPanel.vm 
*/
(function (global, _restClient, _i18n) {
    var restClient = _restClient;
    var i18n = _i18n;
    
    var ConDecIssueTab = function ConDecIssueTab() {
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

    /* called by callDialog */
    function callDialogFromView() {
        console.log("view.tab.panel.js callDialogFromView");

        var textheader="Edit and Link Decision Knowledge in Issue Comments";
        var textSaveButton = "Save";
        
        var submitButton = document.getElementById("dialog-submit-button");
        submitButton.textContent = textSaveButton;
        submitButton.onclick = function() {
            console.log("view.tab.panel.js submitButton.onclick");
            AJS.dialog2("#dialog").hide();
        };
        setUpDialog();
        var header = document.getElementById("dialog-header");
        header.textContent = textheader;
    }

    /* Triggered by tabPanel.vm */
    function callDialog() {
        console.log("view.tab.panel.js callDialog");

        callDialogFromView();
        document.getElementById("dialog-content").innerHTML = "<div id =header2> </div> <div id =jstree> </div> ";
        document.getElementById("header2").innerHTML = "<input class=text medium-long-field id=jstree-search-input placeholder=Search decision knowledge />";
        document.getElementById("dialog").classList.remove("aui-dialog2-medium");
        document.getElementById("dialog").classList.add("aui-dialog2-large");
        buildTreeViewer(document.getElementById("Relevant").checked);
    }

    /*

     called by
     * view.tab.panel.js
        lines: 43
     * view.context.menu.js
        lines: 414,617
    */
    function buildTreeViewer(showRelevant) {
        console.log("view.tab.panel.js buildTreeViewer");

        restClient.getTreeViewerWithoutRootElement(showRelevant, function(core) {
            console.log("view.tab.panel.js getTreeViewerWithoutRootElement callback");

            jQueryConDec("#jstree").jstree({
                "core" : core,
                "plugins" : [ "dnd", "contextmenu", "wholerow", "search", "sort", "state" ],
                "search" : {
                    "show_only_matches" : true
                },
                "contextmenu" : {
                    "items" : customContextMenu
                },
                "sort" : sortfunction
            });
            $("#jstree-search-input").keyup(function() {
                var searchString = $(this).val();
                jQueryConDec("#jstree").jstree(true).search(searchString);
            });
            addDragAndDropSupportForTreeViewer();
            document.getElementById("jstree").addEventListener("mousemove", bringContextMenuToFront);
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

    /* used by buildTreeViewer */
    function customContextMenu(node) {
        console.log("view.tab.panel.js customContextMenu");

        if (node.li_attr['class'] === "sentence") {
            return contextMenuActionsForSentences;
        } else {
            return;
        }
    }

    /* used by buildTreeViewer */
    function bringContextMenuToFront() {
        if (document.getElementsByClassName("vakata-context").length > 0) {
            document.getElementsByClassName("vakata-context")[0].style.zIndex = 9999;
        }
    }

    /* TODO: check is it not used ? */
    function createSentenceLinkToExistingElement(idOfExistingElement, idOfNewElement, knowledgeTypeOfChild) {
        console.log("view.tab.panel.js createSentenceLinkToExistingElement");

        switchLinkTypes(knowledgeTypeOfChild, idOfExistingElement, idOfNewElement, function(linkType, idOfExistingElement,
                idOfNewElement) {
            linkSentences(idOfExistingElement, idOfNewElement, linkType, function() {
            });
        });
    }

    // Expose methods:

    // for tabPanel.vm
    ConDecIssueTab.prototype.callDialog = callDialog;
    // for tabPanel.vm
    ConDecIssueTab.prototype.toggleSelectedDecisionElements = toggleSelectedDecisionElements;
    // for view.context.menu.js
    ConDecIssueTab.prototype.buildTreeViewer = buildTreeViewer;
        
    // initialize ConDecIssueTab
    global.conDecIssueTab= new ConDecIssueTab();
})(window,window,null); //TODO: replace 2nd param with rest client object 
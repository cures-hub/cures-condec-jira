/*
 This issue module view controller does:
 * render decision tree
 * provide a list of action items for the context menu
 * OUT OF SCOPE for now set onclick events to button for exporting as table

 Requires
 * rest.client.js
 * management.js
 * view.treant.js
 * view.context.menu.js

 Required by
 * view.context.menu.js
 * tabPanel.vm

 Referenced in HTML by
 * tabPanel.vm
*/
(function (global) {
    /* private vars */
    var contextMenu = null;
    var i18n = null;
    var management = null;
    var restClient = null;
    var treant = null;

    var ConDecIssueModule = function ConDecIssueModule() {
    };

    ConDecIssueModule.prototype.init = function init(_restClient, _management, _treant, _contextMenu, _i18n) {
        console.log("view.issue.module init");

        if ( isConDecRestClientType(_restClient) &&
             isConDecManagementType(_management) &&
             isConDecTreantType(_treant) &&
             isConDecContextType(_contextMenu) // not using and thus not checking i18n yet.
            ) {
            restClient = _restClient;
            management = _management;
            treant = _treant;
            contextMenu = _contextMenu;
            i18n = _i18n;

            addOnClickEventToExportAsTable();

            return true;
        }
        return false;
    };

    ConDecIssueModule.prototype.initView = function initView() {
        console.log("view.issue.module initView");

        updateView(management, treant, contextMenu);
    }

    // for view.context.menu
    ConDecIssueModule.prototype.setAsRootElement = function setAsRootElement(id) {
        console.log("view.issue.module setAsRootElement",id);
        restClient.getDecisionKnowledgeElement(id, function(decisionKnowledgeElement) {
            var baseUrl = AJS.params.baseURL;
            var key = decisionKnowledgeElement.key;
            window.open(baseUrl + "/browse/" + key, '_self'); // TODO: why window open ?
        });
    }

    function addOnClickEventToExportAsTable() {
	    console.log("view.issue.module addOnClickEventToExportAsTable");

        var exportMenuItem = document.getElementById("export-as-table-link");
        if (!exportMenuItem) return;
        exportMenuItem.addEventListener("click", function(e) {
            e.preventDefault();
            e.stopPropagation();
            console.log("view.issue.module exportDecisionKnowledge");
            AJS.dialog2("#export-dialog").show();
        });
    }

    // for view.context.menu
    function getContextMenuActionsForTreant(contextMenu) {
        console.log("view.issue.module getContextMenuActionsForTreant");
        var menu =
        { "asRoot" : contextMenu.contextMenuSetAsRootAction
        , "create" : contextMenu.contextMenuCreateAction
        , "edit" : contextMenu.contextMenuEditAction
        , "link" : contextMenu.contextMenuLinkAction
        , "deleteLink" : contextMenu.contextMenuDeleteLinkAction
        , "delete" : contextMenu.contextMenuDeleteAction
        };
        return menu;
    }

    function updateView(management,treant,contextMenu) {
	    console.log("view.issue.module updateView");
        var issueKey = management.getIssueKey();
        var search = management.getURLsSearch();
        treant.buildTreant(issueKey, true, search, getContextMenuActionsForTreant(contextMenu) );
    }

    /*
     Init Helpers
    */
    function isConDecRestClientType(restClient) {
        if (!( restClient!=undefined
                && restClient.getDecisionKnowledgeElement!=undefined
                && typeof restClient.getDecisionKnowledgeElement === 'function' ))
            {
                console.warn("ConDecIssueModule: invalid restClient object received.")
                return false;
            }
        return true;
    }

    function isConDecManagementType(management) {
        if (!( management!=undefined
            && management.getIssueKey!=undefined
            && typeof management.getIssueKey === 'function' ))
        {
            console.warn("ConDecIssueModule: invalid management object received.")
            return false;
        }
        return true;
    }

    function isConDecTreantType(buildTreant) {
        if (!( buildTreant!=undefined
            && buildTreant.buildTreant!=undefined
            && typeof buildTreant.buildTreant === 'function' ))
        {
            console.warn("ConDecIssueModule: invalid buildTreant object received.")
            return false;
        }
        return true;
    }

    function isConDecContextType(contextMenu) {
        if (!( contextMenu!=undefined
            && contextMenu.setUpDialog!=undefined
            && typeof contextMenu.setUpDialog === 'function' ))
        {
            console.warn("ConDecIssueModule: invalid contextMenu object received.")
            return false;
        }
        return true;
    }

    // export ConDecIssueModule
    global.conDecIssueModule = new ConDecIssueModule();
})(window);

/* OUT of scope for Restructing:
 ExportAsTable functions
*/
function closeExportDialog() {
	AJS.dialog2("#export-dialog").hide();
}

function exportAsMyTable() {
	// get jql from url
	var myJql = getQueryFromUrl();
	console.log("query", myJql);
	var baseLink = window.location.origin + "/browse/";
	callGetElementsByQueryAndDownload(myJql, baseLink);
}

function exportLinkedElements() {
	var myJql = getQueryFromUrl();
	var issueKey = getIssueKey();
	getLinkedElementsByQuery(myJql, issueKey, function(res) {
		console.log("noResult", res);
		if (res) {
			console.log("linked", res);
			if (res.length > 0) {
				download("issueLinkJson", JSON.stringify(res));
			} else {
				showFlag("error", "The Element was not found.");
			}
		}
	});

}

/**
 * returns jql if empty or nonexistent create it returning jql for one issue
 * 
 * @returns {string}
 */
function getQueryFromUrl() {
	var userInputJql = getURLsSearch();
	var baseUrl = AJS.params.baseURL;
	var sPathName = document.location.href;
	var sPathWithoutBaseUrl = sPathName.split(baseUrl)[1];

	// check if jql is empty or non existent
	var myJql = "";
	if (userInputJql && userInputJql.indexOf("?jql=") > -1 && userInputJql.split("?jql=")[1]) {
		myJql = userInputJql;
	} else if (userInputJql && userInputJql.indexOf("?filter=") > -1 && userInputJql.split("?filter=")[1]) {
		myJql = userInputJql;
	} else if (sPathWithoutBaseUrl && sPathWithoutBaseUrl.indexOf("/browse/") > -1) {
		var issueKey = sPathWithoutBaseUrl.split("/browse/")[1];
		if (issueKey.indexOf("?jql=")) {
			issueKey = issueKey.split("?jql=")[0];
		}
		if (issueKey.indexOf("?filter=")) {
			issueKey = issueKey.split("?filter=")[0];
		}
		myJql = "?jql=issue=" + issueKey;
	}
	return myJql;
}

function callGetElementsByQueryAndDownload(jql, baseLink) {
	var elementsWithLinkArray = [];
	getElementsByQuery(jql, function(response) {
		console.log("byQuery", response);
		if (response) {
			response.map(function(el) {
				el["link"] = baseLink + el["key"];
				elementsWithLinkArray.push(el);
			});
			if (elementsWithLinkArray.length > 0) {
				download("issueJson", JSON.stringify(elementsWithLinkArray));
			} else {
				showFlag("error", "No Elements were found.");
			}
		}
	});
}

function download(filename, text) {
	var element = document.createElement('a');
	element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
	element.setAttribute('download', filename);
	element.style.display = 'none';
	document.body.appendChild(element);
	element.click();
	document.body.removeChild(element);
}



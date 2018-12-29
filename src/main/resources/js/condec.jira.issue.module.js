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
	var i18n = null;
	var conDecAPI = null;
	var conDecObservable = null;
	var conDecDialog = null;
	var conDecContextMenu = null;
	var treant = null;

	var ConDecJiraIssueModule = function ConDecJiraIssueModule() {
		console.log("conDecJiraIssueModule constructor");
	};

	ConDecJiraIssueModule.prototype.init = function init(_conDecAPI, _conDecObservable, _conDecDialog,
			_conDecContextMenu, _treant, _i18n) {

		console.log("ConDecJiraIssueModule init");

		// TODO: Add i18n support and check i18n
		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
				&& isConDecDialogType(_conDecDialog) && isConDecContextMenuType(_conDecContextMenu)
				&& isConDecTreantType(_treant)) {
			conDecAPI = _conDecAPI;

			conDecObservable = _conDecObservable;
			conDecDialog = _conDecDialog;
			conDecContextMenu = _conDecContextMenu;
			treant = _treant;
			i18n = _i18n;

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
		treant.buildTreant(issueKey, true, search);
	};

	function getURLsSearch() {
		// get jql from url
		var search = global.location.search.toString();
		search = search.toString().replace("&", "§");
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
			AJS.dialog2("#export-dialog").show();

			document.getElementById("exportAllElementsMatchingQueryJson").onclick = function() {
				exportAllElementsMatchingQuery("json");
			};
			document.getElementById("exportAllElementsMatchingQueryDocument").onclick = function() {
				exportAllElementsMatchingQuery("document");
			};
			document.getElementById("exportLinkedElementsJson").onclick = function() {
				exportLinkedElements("json");
			};
			document.getElementById("exportLinkedElementsDocument").onclick = function() {
				exportLinkedElements("document");
			};
		});
	}

	function exportAllElementsMatchingQuery(exportType) {
		var jql = getURLsSearch();
		var baseLink = global.location.origin + "/browse/";
		var elementsWithLinkArray = [];
		conDecAPI.getElementsByQuery(jql, function(elements) {
			if (elements && elements.length > 0 && elements[0] !== null) {
				download("decisionKnowledge", elements, exportType);
			}
		});
	}

	function exportLinkedElements(exportType) {
		var jql = getURLsSearch();
		var issueKey = conDecAPI.getIssueKey();
		conDecAPI.getLinkedElementsByQuery(jql, issueKey, "i", function(elements) {
			if (elements && elements.length > 0 && elements[0] !== null) {
				download("decisionKnowledgeGraph", elements, exportType);
			}
		});
	}

	function download(filename, elements, exportType) {
		var element = document.createElement('a');
		var dataString = "";
		switch (exportType) {
		case "document":
			filename += ".doc";
			var htmlString = createHtmlStringForWordDocument(elements);
			dataString = "data:text/html," + encodeURIComponent(htmlString);
			break;
		case "json":
			dataString = 'data:text/plain;charset=utf-8,' + encodeURIComponent(JSON.stringify(elements));
			filename += ".json";
			break;
		}
		element.setAttribute('href', dataString);
		element.setAttribute('download', filename);
		element.style.display = 'none';
		document.body.appendChild(element);
		element.click();
		document.body.removeChild(element);
	}

	function createHtmlStringForWordDocument(elements) {
		var contentString = "";

		var sTable = "<table><tr><th>Key</th><th>Summary</th><th>Description</th><th>Type</th></tr>";
		elements.map(function(el) {
			if (el) {
				var sLink = "";
				var sKey = "";
				var sSummary = "";
				var sDescription = "";
				var sType = "";
				if (el["url"]) {
					sLink = el["url"];
				}
				if (el["key"]) {
					sKey = el["key"];
				}
				if (el["summary"]) {
					sSummary = el["summary"];
				}
				if (el["description"]) {
					sDescription = el["description"];
				}
				if (el["type"]) {
					sType = el["type"];
				}
				var row = "<tr>";
				row += "<td><a href='" + sLink + "'>" + sKey + "</a></td>";
				row += "<td>" + sSummary + "</td>";
				row += "<td>" + sDescription + "</td>";
				row += "<td>" + sType + "</td>";
				row += "</tr>";
			}
			sTable += row;
		});

		contentString += sTable + "</table>";
		var styleString = "table{font-family:arial,sans-serif;border-collapse:collapse;width:100%}td,th{border:1px solid #ddd;text-align:left;padding:8px}tr:nth-child(even){background-color:#ddd}";
		var htmlString = $("<html>").html(
				"<head><style>" + styleString + "</style></head><body>" + contentString + "</body>").html();
		return htmlString;
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

	// export ConDecJiraIssueModule
	global.conDecJiraIssueModule = new ConDecJiraIssueModule();
})(window);
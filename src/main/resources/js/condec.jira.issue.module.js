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

	var ConDecJiraIssueModule = function ConDecJiraIssueModule() {
		console.log("conDecJiraIssueModule constructor");
	};

	ConDecJiraIssueModule.prototype.init = function init(_conDecAPI, _conDecObservable, _conDecDialog,
			_conDecContextMenu, _treant) {

		console.log("ConDecJiraIssueModule init");

		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
				&& isConDecDialogType(_conDecDialog) && isConDecContextMenuType(_conDecContextMenu)
				&& isConDecTreantType(_treant)) {

			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;
			conDecDialog = _conDecDialog;
			conDecContextMenu = _conDecContextMenu;
			treant = _treant;

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
		// if search query does not exist check
		return search;
	}

	/**
	 * returns jql if empty or nonexistent create it returning jql for one issue
	 * 
	 * @returns {string}
	 */
	function getQueryFromUrl(bAllIssues) {
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
			// user on url of a single issue
			if (bAllIssues) {
				myJql = "?filter=allissues";
			} else {
				var issueKey = sPathWithoutBaseUrl.split("/browse/")[1];
				if (issueKey.indexOf("?jql=")) {
					issueKey = issueKey.split("?jql=")[0];
				}
				if (issueKey.indexOf("?filter=")) {
					issueKey = issueKey.split("?filter=")[0];
				}
				myJql = "?jql=issue=" + issueKey;
			}
		}
		return myJql;
	}

	ConDecJiraIssueModule.prototype.updateView = function () {
		console.log("ConDecJiraIssueModule updateView");
		JIRA.trigger(JIRA.Events.REFRESH_ISSUE_PAGE, [JIRA.Issue.getIssueId()]);
	};
	var selectedRadioTreeType = "";
	var selectedRadioExportType = "";

	function addOnClickEventToExportAsTable() {
		console.log("ConDecJiraIssueModule addOnClickEventToExportAsTable");

		var exportMenuItem = document.getElementById("export-as-table-link");

		exportMenuItem.addEventListener("click", function (event) {
			event.preventDefault();
			event.stopPropagation();
			AJS.dialog2("#export-dialog").show();

			document.getElementById("export-dialog-confirm-button").onclick = function () {
				getSelectedRadioBoxForExport();
			};
			$('#exportDecisionKnowledgeFieldSet input:radio').on('change', function () {
				selectedRadioTreeType = $(this).context.id;
			});
			$('#exportDecisionKnowledgeFieldSetExportType input:radio').on('change', function () {
				selectedRadioExportType = $(this).context.id;
			});
		});
	}

	function getSelectedRadioBoxForExport() {
		var exportType = "";
		if (selectedRadioExportType) {
			if (selectedRadioExportType === "exportAsJson") {
				exportType = "json";
			}
			if (selectedRadioExportType === "exportAsDocument") {
				exportType = "document";
			}
			if (selectedRadioTreeType === "exportLinked") {
				exportLinkedElements(exportType);
			}
			if (selectedRadioTreeType === "exportLinkedAndQuery") {
				exportAllMatchedAndLinkedElements(exportType);
			}
		}
		// close dialog
		AJS.dialog2('#export-dialog').hide();
	}

	function exportLinkedElements(exportType) {
		var jql = getQueryFromUrl(true);
		var jiraIssueKey = conDecAPI.getIssueKey();
		conDecAPI.getLinkedElementsByQuery(jql, jiraIssueKey, "i", function(elements) {
			if (elements && elements.length > 0 && elements[0] !== null) {
				download(elements, "decisionKnowledgeGraph", exportType);
			}
		});
	}

	function exportAllMatchedAndLinkedElements(exportType) {
		var jql = getQueryFromUrl(false);
		conDecAPI.getAllElementsByQueryAndLinked(jql, function(elements) {
			if (elements && elements.length > 0 && elements[0] !== null) {
				download(elements, "decisionKnowledgeGraphWithLinked", exportType, true);
			}
		});
	}

	function download(elements, filename, exportType, multipleArrays) {
		var dataString = "";
		switch (exportType) {
		case "document":
			filename += ".doc";
			var htmlString = "";
			if (multipleArrays) {
				elements.map(function(aElement) {
					htmlString += createHtmlStringForWordDocument(aElement) + "<hr>";
				});
			} else {
				htmlString = createHtmlStringForWordDocument(elements);
			}
			dataString = "data:text/html," + encodeURIComponent(htmlString);
			break;
		case "json":
			dataString = "data:text/plain;charset=utf-8," + encodeURIComponent(JSON.stringify(elements));
			filename += ".json";
			break;
		}

		var link = document.createElement('a');
		link.style.display = 'none';
		link.setAttribute('href', dataString);
		link.setAttribute('download', filename);
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
	}

	function createHtmlStringForWordDocument(elements) {
		var table = "<table><tr><th>Key</th><th>Summary</th><th>Description</th><th>Type</th></tr>";
		elements.map(function(element) {
			var summary = element["summary"] === undefined ? "" : element["summary"];
			var description = element["description"] === undefined ? "" : element["description"];
			var type = element["type"] === undefined ? "" : element["type"];

			table += "<tr>";
			table += "<td><a href='" + element["url"] + "'>" + element["key"] + "</a></td>";
			table += "<td>" + summary + "</td>";
			table += "<td>" + description + "</td>";
			table += "<td>" + type + "</td>";
			table += "</tr>";
		});
		table += "</table>";

		var styleString = "table{font-family:arial,sans-serif;border-collapse:collapse;width:100%}td,th{border:1px solid #ddd;text-align:left;padding:8px}tr:nth-child(even){background-color:#ddd}";
		var htmlString = $("<html>").html("<head><style>" + styleString + "</style></head><body>" + table + "</body>")
				.html();
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
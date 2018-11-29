/*
 This view provides a tree of relevant decision knowledge in the JIRA issue view.

 Requires
 * condec.api.js
 * condec.observable.js
 * condec.context.menu.js
 * condec.treant.js

 Is required by
 * tabPanel.vm

 Is referenced in HTML by
 * tabPanel.vm
 */
(function (global) {
	/* private vars */
	var i18n = null;
	var conDecAPI = null;
	var conDecObservable = null;
	var conDecDialog = null;
	var conDecContextMenu = null;
	var treant = null;

	var ConDecIssueModule = function ConDecIssueModule() {
	};

	ConDecIssueModule.prototype.init = function init(_conDecAPI, _conDecObservable, _conDecDialog, _conDecContextMenu,
													 _treant, _i18n) {
		console.log("view.issue.module init");

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

	ConDecIssueModule.prototype.initView = function initView() {
		console.log("view.issue.module initView");

		updateView(treant);
	};

	ConDecIssueModule.prototype.updateView = function () {
		updateView(treant);
	};

	// for view.context.menu
	ConDecIssueModule.prototype.setAsRootElement = function setAsRootElement(id) {
		console.log("view.issue.module setAsRootElement", id);
		conDecAPI.getDecisionKnowledgeElement(id, function (decisionKnowledgeElement) {
			var baseUrl = AJS.params.baseURL;
			var key = decisionKnowledgeElement.key;
			global.open(baseUrl + "/browse/" + key, '_self');
		});
	};

	function addOnClickEventToExportAsTable() {
		console.log("view.issue.module addOnClickEventToExportAsTable");


		var exportMenuItem = document.getElementById("export-as-table-link");
		exportMenuItem.addEventListener("click", function (e) {
			e.preventDefault();
			e.stopPropagation();
			console.log("view.issue.module exportDecisionKnowledge");
			AJS.dialog2("#export-dialog").show();
			document.getElementById("exportAllElementsMatchingQueryJson").onclick = function () {
				exportAllElementsMatchingQuery("json");
			};
			document.getElementById("exportAllElementsMatchingQueryDocument").onclick = function () {
				exportAllElementsMatchingQuery("document");
			};
			document.getElementById("exportLinkedElementsJson").onclick = function () {
				exportLinkedElements("json");
			};

			document.getElementById("exportLinkedElementsDocument").onclick = function () {
				exportLinkedElements("document");
			};
		});
	}

	function updateView(treant) {
		console.log("view.issue.module updateView");
		var issueKey = conDecAPI.getIssueKey();
		var search = getURLsSearch();
		treant.buildTreant(issueKey, true, search);
	}

	/*
	 * Init Helpers
	 */
	function isConDecAPIType(conDecAPI) {
		if (!(conDecAPI !== undefined && conDecAPI.getDecisionKnowledgeElement !== undefined && typeof conDecAPI.getDecisionKnowledgeElement === 'function')) {
			console.warn("ConDecIssueModule: invalid ConDecAPI object received.");
			return false;
		}
		return true;
	}

	function isConDecObservableType(conDecObservable) {
		if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
			console.warn("ConDecIssueModule: invalid ConDecObservable object received.");
			return false;
		}
		return true;
	}

	function isConDecDialogType(conDecDialog) {
		if (!(conDecDialog !== undefined && conDecDialog.showCreateDialog !== undefined && typeof conDecDialog.showCreateDialog === 'function')) {
			console.warn("ConDecIssueModule: invalid conDecDialog object received.");
			return false;
		}
		return true;
	}

	function isConDecContextMenuType(conDecContextMenu) {
		if (!(conDecContextMenu !== undefined && conDecContextMenu.createContextMenu !== undefined && typeof conDecContextMenu.createContextMenu === 'function')) {
			console.warn("ConDecIssueModule: invalid conDecContextMenu object received.");
			return false;
		}
		return true;
	}

	function isConDecTreantType(conDecTreant) {
		if (!(conDecTreant !== undefined && conDecTreant.buildTreant !== undefined && typeof conDecTreant.buildTreant === 'function')) {
			console.warn("ConDecIssueModule: invalid conDecTreant object received.");
			return false;
		}
		return true;
	}

	function getURLsSearch() {
		var search = global.location.search.toString();
		search = search.toString().replace("&", "§");
		return search;
	}

	/*
	 * OUT of scope for Restructing: ExportAsTable functions
	 */
	function exportAllElementsMatchingQuery(exportType) {
		// get jql from url
		var myJql = getQueryFromUrl();
		console.log("query", myJql);
		var baseLink = global.location.origin + "/browse/";
		callGetElementsByQueryAndDownload(myJql, baseLink, exportType);
	}

	function exportLinkedElements(exportType) {
		var myJql = getQueryFromUrl();
		var issueKey = conDecAPI.getIssueKey();
		conDecAPI.getLinkedElementsByQuery(myJql, issueKey, function (res) {
			console.log("noResult", res);
			if (res) {
				console.log("linked", res);
				if (res.length > 0) {
					var obj = getArrayAndTransformToConfluenceObject(res);
					download("decisionKnowledgeGraph", obj, exportType);
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

	function callGetElementsByQueryAndDownload(jql, baseLink, exportType) {
		var elementsWithLinkArray = [];
		conDecAPI.getElementsByQuery(jql, function (response) {
			console.log("byQuery", response);
			if (response) {
				response.map(function (el) {
					el["link"] = baseLink + el["key"];
					elementsWithLinkArray.push(el);
				});
				if (elementsWithLinkArray.length > 0) {
					var obj = getArrayAndTransformToConfluenceObject(elementsWithLinkArray);
					download("decisionKnowledge", obj, exportType);
				} else {
					showFlag("error", "No Elements were found.");
				}
			}
		});
	}

	function getArrayAndTransformToConfluenceObject(jsonArray) {
		var baseUrl = AJS.params.baseURL + "/browse/";
		return {
			url: baseUrl,
			data: jsonArray
		};
	}

	function download(filename, jsonObject, exportType) {
		var element = document.createElement('a');
		var dataString = "";
		switch (exportType) {
			case "document":
				filename += ".doc";
				var htmlString = createHtmlStringForWordDocument(jsonObject);
				dataString = "data:text/html," + encodeURIComponent(htmlString);
				break;
			case "json":
				dataString = 'data:text/plain;charset=utf-8,' + encodeURIComponent(JSON.stringify(jsonObject));
				filename += ".json";
				break
		}
		element.setAttribute('href', dataString);
		element.setAttribute('download', filename);
		element.style.display = 'none';
		document.body.appendChild(element);
		element.click();
		document.body.removeChild(element);
	}

	function createHtmlStringForWordDocument(jsonObj) {
		var contentString="";

		var sTable="<table><tr><th>Key</th><th>Summary</th><th>Description</th><th>Type</th></tr>";
		jsonObj.data.map(function(el){
			var row="<tr>";
			row+="<td><a href='"+el["link"]+"'>"+el["key"]+"</a></td>";
			row+="<td>"+el["summary"]+"</td>";
			row+="<td>"+el["description"]+"</td>";
			row+="<td>"+el["type"]+"</td>";
			row+="</tr>";

			sTable+=row;
		});

		contentString+=sTable+"</table>";
		var styleString="table{font-family:arial,sans-serif;border-collapse:collapse;width:100%}td,th{border:1px solid #ddd;text-align:left;padding:8px}tr:nth-child(even){background-color:#ddd}"
		var htmlString=$("<html>").html("<head><style>"+styleString+
			"</style></head><body>"+contentString+"</body>").html();
		return htmlString;
	}

	// export ConDecIssueModule
	global.conDecIssueModule = new ConDecIssueModule();
})(window);
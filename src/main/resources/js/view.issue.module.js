/*
 This issue module view controller does:
 * render tree of decision knowledge
 * provide a list of action items for the context menu

 Requires
 * condec.api.js
 * condec.observable.js
 * view.treant.js
 * view.context.menu.js

 Required by
 * view.context.menu.js
 * tabPanel.vm

 Referenced in HTML by
 * tabPanel.vm
 */
(function(global) {
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

		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable) && isConDecTreantType(_treant)
		//&& isConDecContextType(_contextMenu) // not using and thus not checking i18n yet.
		) {
			conDecAPI = _conDecAPI;

			//TODO: Register/Subscribe as observer
			conDecObservable = _conDecObservable;
			conDecDialog = _conDecDialog;
			conDecContextMenu = _conDecContextMenu;
			treant = _treant;
			i18n = _i18n;

			addOnClickEventToExportAsTable();

			return true;
		}
		return false;
	};

	ConDecIssueModule.prototype.initView = function initView() {
		console.log("view.issue.module initView");

		updateView(treant);
	};

	ConDecIssueModule.prototype.updateView = function() {
		updateView(treant);
	};

	// for view.context.menu
	ConDecIssueModule.prototype.setAsRootElement = function setAsRootElement(id) {
		console.log("view.issue.module setAsRootElement", id);
		conDecAPI.getDecisionKnowledgeElement(id, function(decisionKnowledgeElement) {
			var baseUrl = AJS.params.baseURL;
			var key = decisionKnowledgeElement.key;
			global.open(baseUrl + "/browse/" + key, '_self');
		});
	};

	function addOnClickEventToExportAsTable() {
		console.log("view.issue.module addOnClickEventToExportAsTable");

		var exportMenuItem = document.getElementById("export-as-table-link");
		exportMenuItem.addEventListener("click", function(e) {
			e.preventDefault();
			e.stopPropagation();
			console.log("view.issue.module exportDecisionKnowledge");
			AJS.dialog2("#export-dialog").show();
		});
	}

	function updateView(treant) {
		console.log("view.issue.module updateView");
		var issueKey = conDecAPI.getIssueKey();
		var search = getURLsSearch();
		treant.buildTreant(issueKey, true, search);
	}

	/*
	 Init Helpers
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

	function isConDecTreantType(buildTreant) {
		if (!(buildTreant !== undefined && buildTreant.buildTreant !== undefined && typeof buildTreant.buildTreant === 'function')) {
			console.warn("ConDecIssueModule: invalid buildTreant object received.");
			return false;
		}
		return true;
	}

	//	function isConDecContextType(contextMenu) {
	//		if (!(contextMenu !== undefined && contextMenu.setUpDialog !== undefined && typeof contextMenu.setUpDialog === 'function')) {
	//			console.warn("ConDecIssueModule: invalid contextMenu object received.");
	//			return false;
	//		}
	//		return true;
	//	}

	function getURLsSearch() {
		var search = global.location.search.toString();
		search = search.toString().replace("&", "§");
		return search;
	}

	/*
	 * OUT of scope for Restructing: ExportAsTable functions
	 */
	function exportAllElementsMatchingQuery() {
		// get jql from url
		var myJql = getQueryFromUrl();
		console.log("query", myJql);
		var baseLink = global.location.origin + "/browse/";
		callGetElementsByQueryAndDownload(myJql, baseLink);
	}

	function exportLinkedElements() {
		var myJql = getQueryFromUrl();
		var issueKey = conDecAPI.getIssueKey();
		conDecAPI.getLinkedElementsByQuery(myJql, issueKey, function(res) {
			console.log("noResult", res);
			if (res) {
				console.log("linked", res);
				if (res.length > 0) {
					var obj = getArrayAndTransformToConfluenceObject(res);
					download("decisionKnowledgeGraph", JSON.stringify(obj));
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
		conDecAPI.getElementsByQuery(jql, function(response) {
			console.log("byQuery", response);
			if (response) {
				response.map(function(el) {
					el["link"] = baseLink + el["key"];
					elementsWithLinkArray.push(el);
				});
				if (elementsWithLinkArray.length > 0) {
					var obj = getArrayAndTransformToConfluenceObject(elementsWithLinkArray);
					download("decisionKnowledge", JSON.stringify(obj));
				} else {
					showFlag("error", "No Elements were found.");
				}
			}
		});
	}

	function getArrayAndTransformToConfluenceObject(jsonArray) {
		var baseUrl = AJS.params.baseURL + "/browse/";
		return {
			url : baseUrl,
			data : jsonArray
		};
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

	// export ConDecIssueModule
	global.conDecIssueModule = new ConDecIssueModule();
})(window);
function fillIssueModule() {
	console.log("view.issue.module fillIssueModule");
	
	var exportMenuItem = document.getElementById("export-as-table-link");
	exportMenuItem.addEventListener("click", function(e) {
	    e.preventDefault();
	    e.stopPropagation();
		console.log("view.issue.module exportDecisionKnowledge");
		AJS.dialog2("#export-dialog").show();
	});

	updateView();
}

function updateView() {
	console.log("view.issue.module updateView");
	var issueKey = getIssueKey();
	var search = getURLsSearch();
	buildTreant(issueKey, true, search);
}

function setAsRootElement(id) {
	getDecisionKnowledgeElement(id, function(decisionKnowledgeElement) {
		var baseUrl = AJS.params.baseURL;
		var key = decisionKnowledgeElement.key;
		window.open(baseUrl + "/browse/" + key, '_self');
	});
}

var contextMenuActionsTreant = {
	"asRoot" : contextMenuSetAsRootAction,
	"create" : contextMenuCreateAction,
	"edit" : contextMenuEditAction,
	"link" : contextMenuLinkAction,
	"deleteLink" : contextMenuDeleteLinkAction,
	"delete" : contextMenuDeleteAction
};

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
				var obj=getArrayAndTransformToConfluenceObject(res);
				download("issueLinkJson", JSON.stringify(obj));
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
				var obj=getArrayAndTransformToConfluenceObject(elementsWithLinkArray);
				download("issueJson", JSON.stringify(obj));
			} else {
				showFlag("error", "No Elements were found.");
			}
		}
	});
}
function getArrayAndTransformToConfluenceObject(jsonArray) {
	var baseUrl=AJS.params.baseURL+"/browse/";
	return {url:baseUrl,data:jsonArray};
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

function fillIssueModule() {
	console.log("view.issue.module fillIssueModule");
	
	updateView();
	
	$(document).ready(function() {
        console.log("issueModule init");
        $("#export-as-table-link").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            exportAsTable();
        });
    });	
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

function exportAsTable() {
	//get jql from url
	var userInputJql = getURLsSearch();
	var baseLink = window.location.origin + "/jira/browse/";
	//check if jql is empty or non existent
	var myJql;
	if (userInputJql && userInputJql.indexOf("?jql=") > -1) {
		myJql = userInputJql.split("?jql=")[1];
	}
	if (userInputJql && userInputJql.indexOf("?filter=") > -1) {
		myJql = userInputJql.split("?filter=")[1];
	}
	if (myJql) {
		callGetElementsByQueryAndDownload(userInputJql, baseLink);
	}
	//get selected issue
	else {
		var sPathName = window.location.pathname;
		var issueKey;
		if (sPathName && sPathName.indexOf("/jira/browse/") > -1) {
			issueKey = sPathName.split("/jira/browse/")[1];
			if (issueKey) {
				var issueJql = "?jql=issue=" + issueKey;
				callGetElementsByQueryAndDownload(issueJql, baseLink);
			}
		}
	}
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
			download("issueJson", JSON.stringify(elementsWithLinkArray));
		}
	});
}

function download(filename, text) {
	console.log("filename", filename);

	var element = document.createElement('a');
	element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
	element.setAttribute('download', filename);

	element.style.display = 'none';
	document.body.appendChild(element);

	element.click();

	document.body.removeChild(element);
}

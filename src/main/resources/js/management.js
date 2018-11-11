function notify() {
	if (window.conDecIssueModule !== undefined) {
		window.conDecIssueModule.updateView();
	} else if (window.conDecKnowledgePage !== undefined) {
		window.conDecKnowledgePage.updateView();
	}
}

function getIssueKey() {
	console.log("management.js getIssueKey");
	var issueKey = JIRA.Issue.getIssueKey();
	if (issueKey === null) {
		issueKey = AJS.Meta.get("issue-key");
	}
	return issueKey;
}

function getProjectKey() {
	console.log("management.js getProjectKey");
	var projectKey;
	try {
		projectKey = JIRA.API.Projects.getCurrentProjectKey();
	} catch (error) {
		console.log(error);
	}
	if (projectKey === undefined) {
		try {
			var issueKey = getIssueKey();
			projectKey = issueKey.split("-")[0];
		} catch (error) {
			console.log(error);
		}
	}
	return projectKey;
}

function getProjectId() {
	var projectId;
	try {
		var projectId = JIRA.API.Projects.getCurrentProjectId();
	} catch (error) {
		console.log(error);
	}
	return projectId;
}

function showFlag(type, message) {
	AJS.flag({
		type : type,
		close : "auto",
		title : type.charAt(0).toUpperCase() + type.slice(1),
		body : message
	});
}

function getURLsSearch() {
	var search = window.location.search.toString();
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
	var baseLink = window.location.origin + "/browse/";
	callGetElementsByQueryAndDownload(myJql, baseLink);
}

function exportLinkedElements() {
	var myJql = getQueryFromUrl();
	var issueKey = getIssueKey();
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
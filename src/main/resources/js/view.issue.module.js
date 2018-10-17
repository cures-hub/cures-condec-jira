function fillIssueModule() {
	console.log("view.issue.module fillIssueModule");
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

function downloadMyJsonAsTable() {
	//get jql from url
	var userInputJql = getURLsSearch();
	var baseLink = window.location.origin + "/jira/browse/";
	//check if jql is empty or non existent
	var myJql;
	if (userInputJql && userInputJql.indexOf("?jql=") > -1) {
		myJql = userInputJql.split("?jql=")[1];
	}
	if (userInputJql && userInputJql.indexOf("?filter=") > -1) {
		myJql = userInputJql.split("?filter=")[1]
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

function downloadMyLinkedElements() {
	console.log("downloadClickedElements");
	var baseLink = window.location.origin + "/jira/browse/";
	var sPathName = window.location.pathname;
	var issueKey;
	if (sPathName && sPathName.indexOf("/jira/browse/") > -1) {
		issueKey = sPathName.split("/jira/browse/")[1];
		if (issueKey.indexOf("?") > -1) {
			issueKey = issueKey.split("?")[0];
		}
	} else {
		var projectKey = getProjectKey();
		issueKey = sPathName.split("/jira/projects/" + projectKey + "/issues/")[1];
	}
	if (issueKey) {
		var issueJql = "?jql=issue=" + issueKey

		var elementsWithLinkArray = [];
		getElementsByQuery(issueJql, function(response) {
			if (response) {
				response.map(function(el) {
					el["link"] = baseLink + el["key"];
					elementsWithLinkArray.push(el);
					getLinkedElements(el["id"], function(res) {
						if (res) {
							res.map(function(element) {
								element["link"] = baseLink + element["key"];
								elementsWithLinkArray.push(element);
							});
							download("jsonWithLinked", JSON.stringify(elementsWithLinkArray));
						}
					})
				});
			}
		});
	}

}

function downloadJsonAsTree() {
	//get jql from url
	var userInputJql = window.location.search;
	var baseLink = window.location.origin + "/jira/browse/";
	console.log("projectId", getProjectKey());

	getElementsByQuery(userInputJql, function(response) {
		console.log("byQuery", response);
		var elementsWithLinkArray = [];
		if (response) {
			var myPromise = new Promise(function(resolve, reject) {
				response.map(function(topNode, i, arr) {
					//make new request foreach

					getTreant(topNode["key"], 4, function(myNode) {
						console.log("myNode", myNode);

						// var parentObject = handleParentObject(myNode.nodeStructure);

						// if (myNode.nodeStructure.children.length > 0) {
						// myNode.nodeStructure.children.map(function (child, i, arr) {
						//     console.log("pushingTOCHildren", child);
						// var tree = handleChildrenRecursive(child);
						//
						// console.log("recieve Tree OBject child", tree)
						// //push children to parentObject
						// console.log("parentBefore", parentObject)
						//
						// parentObject.children.push(tree);
						// console.log("parentafter", parentObject)

						// });
						// }
						elementsWithLinkArray.push(myNode);
						if (arr.length - 1 === i) {
							// last one
							resolve();
						}
					});

				})

			});
			console.log("complete Child Tree", elementsWithLinkArray);
			myPromise.then(function() {
				download("jsonAsTree", JSON.stringify(elementsWithLinkArray));
			});
		}

	})
}

function handleParentObject(oParent) {
	parentObject = {};
	parentObject["subType"] = oParent.HTMLclass || "";
	parentObject["id"] = oParent.HTMLid || "";
	parentObject["link"] = oParent.link.href || "";
	parentObject["description"] = oParent.link.title || "";
	parentObject["key"] = oParent.text.desc || "";
	parentObject["type"] = oParent.text.name || "";
	parentObject["title"] = oParent.text.title || "";
	parentObject["children"] = [];
	return parentObject;
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

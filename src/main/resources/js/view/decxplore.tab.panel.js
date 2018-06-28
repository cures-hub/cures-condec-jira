var issueTabTreantTree;

function fillIssueTabPanel() {
	console.log("init");
	var pathname = window.location.pathname;
	var stringArray = pathname.split("/");
	var issueKey = stringArray[stringArray.length - 1];
	if (issueKey.substring(0, issueKey.indexOf('?')) != '') {
		issueKey = issueKey.substring(0, issueKey.indexOf('?'));
	}
	console.log("key:" + issueKey);
	buildTreantIssueTabPanel(issueKey);
}

function buildTreantIssueTabPanel(decisionKnowledgeElementKey) {
	//var depthOfTree = document.getElementById("depthOfTreeInput").value;
	var depthOfTree = 4;
	getTreant(decisionKnowledgeElementKey, depthOfTree, function(treant) {
		document.getElementById("treant-container").innerHTML = "";
		new Treant(treant);

		//createContextMenuForTreantNodes();
		//addDragAndDropSupportForTreant();
	});
}

//function buildTreantIssueTabPanel(issueKey) {
//	console.log(issueKey);
//
//	var treeStructure = [];
//
//	var infoData = getData(AJS.contextPath() + "/rest/api/latest/issue/" + issueKey);
//
//	var thisLevelElement = {
//		text : {
//			title : infoData.fields.issuetype.name,
//			name : infoData.fields.summary
//		}
//	};
//	treeStructure.push(thisLevelElement);
//
//	var linkedIssues = infoData.fields.issuelinks;
//
//	if (linkedIssues) {
//		treeStructure = checkForTopLevelElements(treeStructure, linkedIssues, thisLevelElement);
//	}
//
//	var commitData = getData(AJS.contextPath() + "/rest/gitplugin/1.0/issues/" + issueKey + "/commits");
//	var commits = commitData.commits;
//	if (commits.length > 0) {
//		treeStructure = addCommits(commits, thisLevelElement, treeStructure);
//	}
//	console.log(treeStructure);
//
//	//New Try
//    getTreant(treeStructure ,function(treant) {
//        document.getElementById("treant-container").innerHTML = "";
//        console.log(treant);
//        treantTree = new Treant(treant);
//    });
//}

function getData(url) {
	var request = new XMLHttpRequest(); // a new request
	request.open("GET", url, false);
	request.send(null);

	var data = request.responseText;

	return JSON.parse(data);
}

function checkForTopLevelElements(tree_structure, linkedIssues, thisLevelElement) {
	var element;
	linkedIssues.forEach(function(linkedIssue) {
		if (linkedIssue.inwardIssue) {
			element = {
				parent : thisLevelElement,
				HTMLclass : 'blue',
				text : {
					title : linkedIssue.inwardIssue.fields.issuetype.name,
					name : linkedIssue.inwardIssue.fields.summary
				}
			};
			tree_structure.push(element);
		} else if (linkedIssue.outwardIssue) {
			element = {
				parent : thisLevelElement,
				HTMLclass : 'red',
				text : {
					title : linkedIssue.outwardIssue.fields.issuetype.name,
					name : linkedIssue.outwardIssue.fields.summary
				}
			};
			tree_structure.push(element);
			var commitData = getData(AJS.contextPath() + "/rest/gitplugin/1.0/issues/" + linkedIssue.outwardIssue.key
					+ "/commits");
			var commits = commitData.commits;
			if (commits.length > 0) {
				tree_structure = addCommits(commits, element, tree_structure);
			}
		}
	});
	return tree_structure;
}

function openDetails(commit) {
	url = AJS.contextPath() + "/secure/bbb.gp.gitviewer.Commit.jspa?repoId=" + commit.repository.id + "&commitId="
			+ commit.commitId;
	window.open(url);
}

function addCommits(commits, parentElement, tree_structure) {
	commits.forEach(function(commit) {
		var message = commit.message;

		var splitMessage = message.split("@");

		var decision;
		var element;
		for ( var i in splitMessage) {
			var split = splitMessage[i].split(" ");
			var message = splitMessage[i].substr(splitMessage[i].indexOf(" ") + 1);
			switch (split[0]) {
			case "Decision:":
				decision = {
					parent : parentElement,
					HTMLclass : 'yellow',
					innerHTML : "<p class=\"node-title\">Decision:</p><p class=\"node-name\">" + message
							+ "</p><a href=\"#\" id=\"" + commit.commitId + "\">More Information</a>"
				};
				$(document).on('click', '#' + commit.commitId + '', function() {
					openDetails(commit);
				});
				tree_structure.push(decision);
				break;
			default:
				element = {
					parent : decision,
					HTMLclass : 'yellow',
					text : {
						title : split[0],
						name : message
					}
				};
				tree_structure.push(element);
				break;
			}
		}
	});
	return tree_structure;
}

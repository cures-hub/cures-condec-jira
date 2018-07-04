function fillIssueModule() {
	var issueKey = JIRA.Issue.getIssueKey();
	buildTreantIssueTabPanel(issueKey);
}

function buildTreantIssueTabPanel(elementKey) {
	// var depthOfTree = document.getElementById("depthOfTreeInput").value;
	var depthOfTree = 4;
	getTreant(elementKey, depthOfTree, function(treeStructure) {
		var treantContainer = document.getElementById("treant-container");
		treantContainer.innerHTML = "";
		treantContainer.style.visibility = "visible";

		var commitData = getData(AJS.contextPath() + "/rest/gitplugin/1.0/issues/" + elementKey + "/commits");
		var commits = commitData.commits;
		if (commits.length > 0) {
			treeStructure.nodeStructure.children = addCommits(commits, treeStructure.nodeStructure.children);
		}

		console.log(treeStructure);

		treantTree = new Treant(treeStructure);
		createContextMenuForTreantNodes();
		addDragAndDropSupportForTreant();
	});
}

function getData(url) {
	var request = new XMLHttpRequest(); // a new request
	request.open("GET", url, false);
	request.send(null);

	var data = request.responseText;

	return JSON.parse(data);
}

function addCommits(commits, elementArray) {
	commits.forEach(function(commit) {
		var message = commit.message;

		var splitMessage = message.split("@");
		splitMessage.shift();

		var decision;
		var element;
		for (var i in splitMessage) {
			var split = splitMessage[i].split(" ");
			var message = splitMessage[i].substr(splitMessage[i].indexOf(" ") + 1);
			switch (split[0]) {
			case "Decision:":
				decision = {
					children : [],
					HTMLclass : 'decision',
					innerHTML : "<p class=\"node-title\">Decision:</p><p class=\"node-name\">" + message
							+ "</p><a href=\"#\" id=\"" + commit.commitId + "\">More Information</a>"
				};
				$(document).on('click', '#' + commit.commitId + '', function() {
					openDetails(commit);
				});
				elementArray.push(decision);
				break;
			default:
				element = {
					children : [],
					HTMLclass : 'decision',
					text : {
						title : split[0],
						name : message
					}
				};
				elementArray.push(element);
				break;
			}
		}
	});
	return elementArray;
}

function openDetails(commit) {
	url = AJS.contextPath() + "/secure/bbb.gp.gitviewer.Commit.jspa?repoId=" + commit.repository.id + "&commitId="
			+ commit.commitId;
	window.open(url);
}

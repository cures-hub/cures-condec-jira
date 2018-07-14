function fillIssueModule() {
	updateView();
}

function updateView() {
	var issueKey = getIssueKey();
	buildTreant(issueKey);
}
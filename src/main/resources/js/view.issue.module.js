function fillIssueModule() {
	updateIssueModule();
}

function updateIssueModule() {
	var issueKey = getIssueKey();
	buildTreant(issueKey, false);
}
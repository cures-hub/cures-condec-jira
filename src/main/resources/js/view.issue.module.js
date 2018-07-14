function fillIssueModule() {
	var issueKey = getIssueKey();
	buildTreant(issueKey);
}

function updateView() {
	var rootElement = getCurrentRootElement();
	if (rootElement) {
		buildTreant(rootElement.key);
	}
}
function notify() {
	if (window.conDecIssueModule !== undefined) {
		window.conDecIssueModule.updateView();
	} else if (window.conDecKnowledgePage !== undefined) {
		window.conDecKnowledgePage.updateView();
	}
}
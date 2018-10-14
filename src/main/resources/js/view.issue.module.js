function fillIssueModule() {
    console.log("view.issue.module fillIssueModule");
    updateView();
}

function updateView() {
    console.log("view.issue.module updateView");
    var issueKey = getIssueKey();
    buildTreant(issueKey, true);
}

function setAsRootElement(id) {	
	getDecisionKnowledgeElement(id, function(decisionKnowledgeElement) {
		var baseUrl = AJS.params.baseURL;
		var key = decisionKnowledgeElement.key;
		window.open(baseUrl + "/browse/" + key, '_self');
	});	
}
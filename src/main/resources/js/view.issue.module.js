function fillIssueModule() {
    console.log("view.issue.module fillIssueModule");
    updateView();
}

function updateView() {
    console.log("view.issue.module updateView");
    var search = getURLsSearch();
    var issueKey = getIssueKey();
    buildTreant(issueKey, true);
}
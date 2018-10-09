function fillIssueModule() {
    console.log("view.issue.module fillIssueModule");
    updateIssueModuleView();
}

function updateIssueModuleView() {
    console.log("view.issue.module updateIssueModuleView");
    var issueKey = getIssueKey();
    buildTreant(issueKey, false);
}
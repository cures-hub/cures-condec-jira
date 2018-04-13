function createDecisionComponent(summary, type, callback) {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    if (summary !== "") {
        var jsondata = {
            "projectKey": projectKey,
            "summary": summary,
            "type": type,
            "description": summary
        };
        postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/createDecisionKnowledgeElement.json", jsondata, function (err, data) {
            if (err !== null) {
                AJS.flag({
                    type: 'error',
                    close: 'auto',
                    title: 'Error',
                    body: type + ' has not been created. Error Code: ' + err
                });
            } else {
                callback(data);
            }
        });
    } else {
        //summary is empty
    }
}
function editDecisionComponent(issueId, summary, description, callback) {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    var jsondata = {
        "id": issueId,
        "summary": summary,
        "projectKey": projectKey,
        "description": description
    };
    postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/updateDecisionKnowledgeElement.json", jsondata, function (err, data) {
        if (err !== null) {
            AJS.flag({
                type: 'error',
                close: 'auto',
                title: 'Error',
                body: 'Decision Component has not been updated. Error Code: ' + err
            });
        } else {
            callback(data);
        }
    });
    window.location.reload(true);
}
function deleteDecisionComponent(issueId, callback) {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    var jsondata = {
        "id": issueId,
        "projectKey": projectKey
    };
    deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json", jsondata, function (err, data) {
        if (err !== null) {
            AJS.flag({
                type: 'error',
                close: 'auto',
                title: 'Error',
                body: 'Decision Component has not been deleted. Error Code: ' + err
            });
        } else {
            callback(data);
        }
    });
}
function createLink(parentId, childId, linkType, callback) {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    var jsondata = {
        "linkType": linkType,
        "ingoingId": childId,
        "outgoingId": parentId
    };
    putJSON(AJS.contextPath() + "/rest/decisions/latest/decisions/createLink.json?projectKey=" + projectKey, jsondata, function (err, data) {
        if (err !== null) {
            AJS.flag({
                type: 'error',
                close: 'auto',
                title: 'Error',
                body: 'IssueLink could not be created'
            });
        } else {
            callback(data);
        }
    });
}

function deleteLink(parentId, childId, linkType, callback) {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    var jsondata = {
        "linkType": linkType,
        "ingoingId": childId,
        "outgoingId": parentId
    };
    putJSON(AJS.contextPath() + "/rest/decisions/latest/deleteLink.json?projectKey=" + projectKey, jsondata, function (err, data) {
        if (err !== null) {
            AJS.flag({
                type: 'error',
                close: 'auto',
                title: 'Error',
                body: 'IssueLink could not be deleted'
            });
        } else {
            callback(data);
        }
    });
}
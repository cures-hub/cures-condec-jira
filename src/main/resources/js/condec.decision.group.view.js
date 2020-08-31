(function (global) {

    /* private vars */
    var conDecObservable = null;
    var conDecAPI = null;

    var ConDecDecisionGroupView = function ConDecDecisionGroupView() {
    };

    ConDecDecisionGroupView.prototype.init = function (_conDecAPI, _conDecObservable) {
        console.log("ConDecDecisionGroupView init");
        if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)) {
            conDecAPI = _conDecAPI;
            conDecObservable = _conDecObservable;
            conDecObservable.subscribe(this);
            return true;
        }
        return false;
    };

    ConDecDecisionGroupView.prototype.buildMatrix = function () {
        conDecAPI.getDecisionGroupTable(function (groups, projectKey) {
            const body = document.getElementById("group-table-body");
            for (var i = 0; i < groups.length; i++) {
                var group = groups[i];
                var keys = getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getAllDecisionElementsWithCertainGroup.json?projectKey=" + projectKey
                    + "&group=" + group);
                var classes = getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getAllClassElementsWithCertainGroup.json?projectKey=" + projectKey
                    + "&group=" + group);
                newTableRow(body, group, keys.length, classes.length);
            }
        });
    };

    ConDecDecisionGroupView.prototype.updateView = function () {
        const body = document.getElementById("group-table-body");
        body.innerHTML = "";
        conDecDecisionGroupView.buildMatrix();
    };

    function newTableRow(body, row1, row2, row3) {
    	const row = document.createElement("tr");
        const tableRowElement = document.createElement("td");
        tableRowElement.innerHTML = row1;
        tableRowElement.addEventListener("contextmenu", function (e) {
            e.preventDefault();
            conDecContextMenu.createContextMenu(row1, "groups", e, null);
        }, false);
        row.appendChild(tableRowElement);
        const tableRowElement2 = document.createElement("td");
        tableRowElement2.innerHTML = row2;
        row.appendChild(tableRowElement2);
        const tableRowElement3 = document.createElement("td");
        tableRowElement3.innerHTML = row3;
        row.appendChild(tableRowElement3);
        body.appendChild(row);
    };

    /*
     * Init Helpers
     */
    function isConDecAPIType(conDecAPI) {
        if (!(conDecAPI !== undefined && conDecAPI.getDecisionKnowledgeElement !== undefined && typeof conDecAPI.getDecisionKnowledgeElement === 'function')) {
            console.warn("ConDecKnowledgePage: invalid ConDecAPI object received.");
            return false;
        }
        return true;
    };

    function isConDecObservableType(conDecObservable) {
        if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
            console.warn("ConDecKnowledgePage: invalid ConDecObservable object received.");
            return false;
        }
        return true;
    };

    function getResponseAsReturnValue(url) {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", url, false);
        xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
        xhr.send();
        return JSON.parse(xhr.response);
    }

    global.conDecDecisionGroupView = new ConDecDecisionGroupView();
})(window);
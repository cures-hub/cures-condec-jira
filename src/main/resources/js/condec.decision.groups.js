(function (global) {

    var ConDecDecisionGroups = function ConDecDecisionGroups() {
    };

    ConDecDecisionGroups.prototype.initView = function () {
        console.log("ConDecDecisionGroups initView");
      
        conDecObservable.subscribe(this);         
        this.buildMatrix();
    };

    // TODO Refactor, move API methods to ConDecAPI
    ConDecDecisionGroups.prototype.buildMatrix = function () {
        const groups = conDecAPI.getAllDecisionGroups();
        const projectKey = conDecAPI.getProjectKey();
        const body = document.getElementById("group-table-body");
        for (var i = 0; i < groups.length; i++) {
            var group = groups[i];
            var keys = getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getAllDecisionElementsWithCertainGroup.json?projectKey=" + projectKey
                + "&group=" + group);
            var classes = getResponseAsReturnValue(AJS.contextPath() + "/rest/condec/latest/config/getAllClassElementsWithCertainGroup.json?projectKey=" + projectKey
                + "&group=" + group);
            newTableRow(body, group, keys.length, classes.length);
        }
    };

    ConDecDecisionGroups.prototype.updateView = function () {
        const body = document.getElementById("group-table-body");
        body.innerHTML = "";
        this.buildMatrix();
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
    
    function getResponseAsReturnValue(url) {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", url, false);
        xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
        xhr.send();
        return JSON.parse(xhr.response);
    }

    global.conDecDecisionGroups = new ConDecDecisionGroups();
})(window);
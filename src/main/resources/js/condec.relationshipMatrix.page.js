(function(global) {

    /* private vars */
    var conDecObservable = null;
    var conDecAPI = null;

    var ConDecRelationshipMatrixPage = function ConDecRelationshipMatrixPage() {
    };

    ConDecRelationshipMatrixPage.prototype.init = function(_conDecAPI, _conDecObservable) {
        console.log("ConDecRelationshipMatrixPage init");
        if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)) {
            conDecAPI = _conDecAPI;
            conDecObservable = _conDecObservable;

            conDecObservable.subscribe(this);
            return true;
        }
        return false;
    };

    ConDecRelationshipMatrixPage.prototype.buildMatrix = function() {
        conDecAPI.getDecisionMatrix(function (data) {
            const matrix = document.getElementById("matrix");
            const thead = document.createElement("thead");
            const firstRowHeaderElement = document.createElement("th");
            firstRowHeaderElement.innerText = "";
			firstRowHeaderElement.classList.add("columnHeader");
            thead.appendChild(firstRowHeaderElement);

            for (let d in data.headerSummaries) {
                newTableHeaderElement(thead, data.headerSummaries[d], data.headerElements[d], "columnHeader");
            }

			matrix.appendChild(thead);

            for (let d in data.coloredRows){
            	const row = data.coloredRows[d];
                newTableRow(matrix, row, data.headerElements[d]);
            }
        });
    };

    ConDecRelationshipMatrixPage.prototype.updateView = function() {
        const matrix = document.getElementById("matrix");
        matrix.innerHTML = "";
        conDecRelationshipMatrixPage.buildMatrix();
    };

    function newTableHeaderElement(matrix, text, headerData, styleClass) {
        const element = document.createElement("th");
		element.addEventListener("contextmenu", function(e) {
			e.preventDefault();
			conDecContextMenu.createContextMenu(headerData.id, headerData.documentationLocation, e, null);
		}, false);
        element.classList.add(styleClass);
        const div = document.createElement("div");
        div.innerText = text;
        element.appendChild(div);
        matrix.appendChild(element);
    };

    function newTableRow(matrix, row, header) {
        matrix.appendChild(document.createElement("tr"));
		newTableHeaderElement(matrix, header.summary, header, "rowHeader");
        for (let d in row) {
        	new newTableElement(matrix, row[d]);
        }
    };

    function newTableElement(matrix, color) {
        const tableRowElement = document.createElement("td");
        tableRowElement.style.backgroundColor = color;
        matrix.appendChild(tableRowElement);
    };

    ConDecRelationshipMatrixPage.prototype.buildLegend = function buildLegend() {
        conDecAPI.getLinkTypes(function(linkTypes) {
            const legend = document.getElementById("legendList");
            for (let linkType in linkTypes) {
                if (linkTypes[linkType] != "") {
                    const li = document.createElement("li");
                    li.innerText = linkType;
                    const span = document.createElement("span");
                    span.style.background = linkTypes[linkType];
                    li.appendChild(span);
                    legend.appendChild(li);
                }
            }
        });
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

    global.conDecRelationshipMatrixPage = new ConDecRelationshipMatrixPage();
})(window);
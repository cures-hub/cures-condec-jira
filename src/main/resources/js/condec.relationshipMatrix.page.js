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

    ConDecRelationshipMatrixPage.prototype.buildMatrix = function buildMatrix() {
        conDecAPI.getMatrixData(function (data) {
            console.log(data);
            const matrix = document.getElementById("matrix");
            const firstRowHeaderElement = document.createElement("th");
            firstRowHeaderElement.innerText = "";
            matrix.appendChild(firstRowHeaderElement);

            for (let d in data.matrixHeaderRow) {
                newTableHeaderElement(matrix, data.matrixHeaderRow[d], "columnHeader");
            }

            for (let d in data.matrixData){
                const row = data.matrixData[d];
                newTableRow(matrix, row);
            }
        });


    }

    function newTableHeaderElement(matrix, text, styleClass) {
        const element = document.createElement("th");
        element.classList.add(styleClass);
        const div = document.createElement("div");
        div.innerText = text;
        element.appendChild(div);
        matrix.appendChild(element);
    }

    function newTableRow(matrix, row) {
        matrix.appendChild(document.createElement("tr"));
        for (let d in row) {
            if (d == 0) {
                newTableHeaderElement(matrix, row[d], "rowHeader");
            } else {
                new newTableElement(matrix, row[d]);
            }
        }
    }

    function newTableElement(matrix, color) {
        const tableRowElement = document.createElement("td");
        tableRowElement.style.backgroundColor = color;
        matrix.appendChild(tableRowElement);
    }

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

    }

    /*
     * Init Helpers
     */
    function isConDecAPIType(conDecAPI) {
        if (!(conDecAPI !== undefined && conDecAPI.getDecisionKnowledgeElement !== undefined && typeof conDecAPI.getDecisionKnowledgeElement === 'function')) {
            console.warn("ConDecKnowledgePage: invalid ConDecAPI object received.");
            return false;
        }
        return true;
    }

    function isConDecObservableType(conDecObservable) {
        if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
            console.warn("ConDecKnowledgePage: invalid ConDecObservable object received.");
            return false;
        }
        return true;
    }

    global.conDecRelationshipMatrixPage = new ConDecRelationshipMatrixPage();
})(window);
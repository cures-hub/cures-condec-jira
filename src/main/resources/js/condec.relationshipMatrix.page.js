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
            const div = document.getElementById("matrix");
            var event, index, id;

            var matrix = new Handsontable(div, {
            	data: data.coloredRows,
				renderer: customRenderer,
                rowHeaders: data.headerSummaries,
				width: 1000,
				height: 500,
                rowHeaderWidth: 150,
                colHeaders: data.headerSummaries,
                afterGetColHeader: function(i, TH) {
                    TH.innerHTML = '<div class="head">' + TH.innerHTML + '</div>'
                },
                colWidths: 25,
                selectionMode: "single",
                dropdownMenu: {
                    callback: function (key, selection, clickEvent) {
                        event = clickEvent;
                        index = selection[0].start.col;
                        id = data.headerElements[index].id;
                        conDecContextMenu.createContextMenu(id, data.headerElements[index].documentationLocation, event, null);
                    }, items: {
                        "Context Menu": {
                            name: "Open Context Menu"
                        }
                    }
                },
                licenseKey: "non-commercial-and-evaluation"
            });
        });
    };

    function customRenderer(instance, td) {
        Handsontable.renderers.TextRenderer.apply(this, arguments);
        td.style.backgroundColor = td.innerText;
        td.innerText = "";
    }

    ConDecRelationshipMatrixPage.prototype.updateView = function() {
        const matrix = document.getElementById("matrix");
        matrix.innerHTML = "";
        conDecRelationshipMatrixPage.buildMatrix();
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
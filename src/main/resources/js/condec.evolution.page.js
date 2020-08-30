(function (global) {

    /* private vars */
    var conDecObservable = null;
    var conDecAPI = null;
    var conDecVis = null;
    var networkRight = null;
    var networkLeft = null;
    var timeline = null;

    var ConDecEvolutionPage = function ConDecEvolutionPage() {
    };

    ConDecEvolutionPage.prototype.init = function (_conDecAPI, _conDecObservable, _conDecVis) {
        console.log("ConDecEvolutionPage init");
        if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)) {
            conDecAPI = _conDecAPI;
            conDecObservable = _conDecObservable;
            conDecVis = _conDecVis;
            //conDecObservable.subscribe(this);
            return true;
        }
        return false;
    };

    ConDecEvolutionPage.prototype.buildTimeLine = function buildTimeLine() {
        console.log("ConDecEvolutionPage build chronology");
        conDecFiltering.initDropdown("knowledge-type-dropdown-chronology", conDecAPI.getKnowledgeTypes(), ["Decision"]);
        conDecFiltering.initDropdown("status-dropdown-chronology", conDecAPI.knowledgeStatus);
        conDecAPI.fillDecisionGroupSelect("select2-decision-group-chronology");      
        var startDate = new Date();
        startDate.setDate(startDate.getDate() - 30);
        document.getElementById("start-date-picker-chronology").value = startDate.toISOString().substr(0, 10); 
        document.getElementById("end-date-picker-chronology").value = new Date().toISOString().substr(0, 10);
        
        var container = document.getElementById("evolution-timeline");
        timeline = new vis.Timeline(container, new vis.DataSet(), {});
        timeline.on("contextmenu", function (properties) {
            properties.event.preventDefault();
            var nodeId = properties.item;
            var documentationLocation = timeline.itemsData.get(nodeId).documentationLocation;
            conDecContextMenu.createContextMenu(nodeId, documentationLocation, properties.event, "evolution-timeline");
        });
        
        addOnClickEventToFilterTimeLineButton();
    };

    ConDecEvolutionPage.prototype.buildCompare = function buildCompare() {
        console.log("ConDec build compare view");
        conDecFiltering.initDropdown("knowledge-type-dropdown-comparison", conDecAPI.getKnowledgeTypes());
        conDecFiltering.initDropdown("status-dropdown-comparison", conDecAPI.knowledgeStatus);

        var date = new Date();
        var today = date.getFullYear() + '-' + date.getMonth() + '-' + date.getDay();
        document.getElementById("end-data-picker-compare-left").value = date.toISOString().substr(0, 10);
        document.getElementById("end-data-picker-compare-right").value = date.toISOString().substr(0, 10);
        var endTime = date.getTime();
        date.setDate(date.getDate() - 7);
        var startTime = date.getTime();
        document.getElementById("start-data-picker-compare-left").value = date.toISOString().substr(0, 10);
        conDecAPI.getCompareVis(startTime, endTime, "", conDecAPI.getKnowledgeTypes(), conDecAPI.knowledgeStatus,
            function (visData) {
                var containerLeft = document.getElementById('left-network');
                var dataLeft = {
                    nodes: visData.nodes,
                    edges: visData.edges
                };
                var options = getOptions();
                networkLeft = new vis.Network(containerLeft, dataLeft, options);
                networkLeft.setSize("100%", "500px");
                networkLeft.on("oncontext", function (params) {
                    conDecVis.addContextMenu(params, networkLeft);
                });
                networkLeft.on("selectNode", function (params) {
                    networkRight.focus(params.nodes[0]);
                    networkLeft.focus(params.nodes[0]);
                });

            });
        date.setDate(date.getDate() - 7);
        startTime = date.getTime();
        document.getElementById("start-data-picker-compare-right").value = date.toISOString().substr(0, 10);
        conDecAPI.getCompareVis(startTime, endTime, "", conDecAPI.getKnowledgeTypes(), conDecAPI.knowledgeStatus,
            function (visData) {
                var containerRight = document.getElementById('right-network');
                var dataRight = {
                    nodes: visData.nodes,
                    edges: visData.edges
                };
                var options = getOptions();
                networkRight = new vis.Network(containerRight, dataRight, options);
                networkRight.setSize("100%", "500px");
                networkRight.on("oncontext", function (params) {
                    conDecVis.addContextMenu(params, networkRight);
                });

                networkRight.on("selectNode", function (params) {
                    networkRight.focus(params.nodes[0]);
                    networkLeft.focus(params.nodes[0]);
                });
            });
        addOnClickEventToFilterCompareButton();
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
    }

    function isConDecObservableType(conDecObservable) {
        if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
            console.warn("ConDecKnowledgePage: invalid ConDecObservable object received.");
            return false;
        }
        return true;
    }

    // Compute filter and select new elements
    function addOnClickEventToFilterCompareButton() {
        console.log("ConDecJiraEvolutionPage addOnClickEventToFilterButtonCompare");

        var filterButton = document.getElementById("filter-button-compare");

        filterButton.addEventListener("click", function (event) {
            var firstDateLeft = -1;
            var secondDateLeft = -1;
            var firstDateRight = -1;
            var secondDateRight = -1;
            var knowledgeTypes = conDecFiltering.getSelectedItems("knowledge-type-dropdown-comparison");
            console.log(knowledgeTypes);
            var issueStatus = conDecFiltering.getSelectedItems("status-dropdown-comparison");
            console.log(issueStatus);
            if (!isNaN(document.getElementById("start-data-picker-compare-left").valueAsNumber)) {
                firstDateLeft = document.getElementById("start-data-picker-compare-left").valueAsNumber;
            }
            if (!isNaN(document.getElementById("end-data-picker-compare-left").valueAsNumber)) {
                secondDateLeft = document.getElementById("end-data-picker-compare-left").valueAsNumber;
            }
            if (!isNaN(document.getElementById("start-data-picker-compare-right").valueAsNumber)) {
                firstDateRight = document.getElementById("start-data-picker-compare-right").valueAsNumber;
            }
            if (!isNaN(document.getElementById("end-data-picker-compare-right").valueAsNumber)) {
                secondDateRight = document.getElementById("end-data-picker-compare-right").valueAsNumber;
            }
            var searchString = "";
            searchString = document.getElementById("compare-search-input").value;
            conDecAPI.getCompareVis(firstDateLeft, secondDateLeft, searchString, knowledgeTypes, issueStatus, function (
                visDataLeft) {
                var dateLeft = {
                    nodes: visDataLeft.nodes,
                    edges: visDataLeft.edges
                };
                networkLeft.setData(dateLeft);
            });
            conDecAPI.getCompareVis(firstDateRight, secondDateRight, searchString, knowledgeTypes, issueStatus, function (
                visDataRight) {
                var dateRight = {
                    nodes: visDataRight.nodes,
                    edges: visDataRight.edges
                };
                networkRight.setData(dateRight);
            });
        });
    }

    // Compute filter and select new elements in the TimeLine View
    function addOnClickEventToFilterTimeLineButton() {
        console.log("ConDecEvolutionPage addOnClickEventToFilterButtonTimeLine");
        var filterButton = document.getElementById("filter-button-chronology");

        filterButton.addEventListener("click", function (event) {
        	var filterSettings = conDecFiltering.getFilterSettings("chronology");
            conDecAPI.getEvolutionData(filterSettings, function (visData) {
                var data = visData.dataSet;
                var groups = visData.groupSet;
                var item = new vis.DataSet(data);
                timeline.setItems(item);
                timeline.setGroups(groups);
                timeline.redraw();
            });
        });
        
        filterButton.click();
    }

    function getOptions() {
        return {
            layout: {
                randomSeed: 1,
                hierarchical: {
                    direction: "UD"
                }
            },
            physics: {
                enabled: false
            },
            interaction: {
                keyboard: true
            },
            nodes: {
                shape: "box",
                widthConstraint: 120,
                color: {
                    background: 'rgba(255, 255, 255,1)',
                    border: 'rgba(0,0,0,1)',
                    highlight: {
                        background: 'rgba(255,255,255,1)',
                        border: 'rgba(0,0,0,1)'
                    }
                },
                font: {
                    multi: false
                },
                shapeProperties: {
                    interpolation: false
                }
            },
            edges: {
                arrows: "to"
            },
            physics: {
                enabled: false
            }
        };
    }

    global.conDecEvolutionPage = new ConDecEvolutionPage();
})(window);
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
        conDecFiltering.fillDatePickers("chronology", 30);
        
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

    ConDecEvolutionPage.prototype.buildCompare = function buildCompare() {
        console.log("ConDec build compare view");
        conDecFiltering.initDropdown("knowledge-type-dropdown-comparison", conDecAPI.getKnowledgeTypes());
        conDecFiltering.initDropdown("status-dropdown-comparison", conDecAPI.knowledgeStatus);
        conDecFiltering.fillDatePickers("comparison", 30); // left side
        conDecFiltering.fillDatePickers("comparison-right", 7);
        
        var containerLeft = document.getElementById('left-network');
        networkLeft = new vis.Network(containerLeft, new vis.DataSet(), getOptions());
        networkLeft.setSize("100%", "500px");
        networkLeft.on("oncontext", function (params) {
            conDecVis.addContextMenu(params, networkLeft);
        });
        networkLeft.on("selectNode", function (params) {
            networkRight.focus(params.nodes[0]);
            networkLeft.focus(params.nodes[0]);
        });
        var containerRight = document.getElementById('right-network');
        networkRight = new vis.Network(containerRight, new vis.DataSet(), getOptions());
        networkRight.setSize("100%", "500px");
        networkRight.on("oncontext", function (params) {
            conDecVis.addContextMenu(params, networkRight);
        });

        networkRight.on("selectNode", function (params) {
            networkRight.focus(params.nodes[0]);
            networkLeft.focus(params.nodes[0]);
        });
        addOnClickEventToFilterCompareButton();
    };

    // Compute filter and select new elements
    function addOnClickEventToFilterCompareButton() {
        console.log("ConDecJiraEvolutionPage addOnClickEventToFilterButtonCompare");

        var filterButton = document.getElementById("filter-button-comparison");

        filterButton.addEventListener("click", function (event) {
        	// left side
            var filterSettingsLeft = conDecFiltering.getFilterSettings("comparison");            
            conDecAPI.getVis(filterSettingsLeft, function (visDataLeft) {
            	visDataLeft.nodes.sort(sortVis);
                var dateLeft = {
                    nodes: visDataLeft.nodes,
                    edges: visDataLeft.edges
                };
                networkLeft.setData(dateLeft);
            });
            
            // right side
            var filterSettingsRight = filterSettingsLeft;
            var startDatePicker = document.getElementById("start-date-picker-comparison-right");    		
    		filterSettingsRight["startDate"] = new Date(startDatePicker.value).getTime();  			
    		var endDatePicker = document.getElementById("end-date-picker-comparison-right");
    		filterSettingsRight["endDate"] = new Date(endDatePicker.value).getTime();
            conDecAPI.getVis(filterSettingsRight, function (visDataRight) {
                visDataRight.nodes.sort(sortVis);
                var dateRight = {
                    nodes: visDataRight.nodes,
                    edges: visDataRight.edges
                };
                networkRight.setData(dateRight);
            });
        });
        
        filterButton.click();
    }
    
    function sortVis(a, b) {
        if (a.id > b.id) {
            return 1;
        }
        if (a.id < b.id) {
            return -1;
        }
        return 0;
    }

    function getOptions() {
    	var options = conDecVis.getOptions();
    	options["layout"] = {
                randomSeed: 1,
                hierarchical: {
                    direction: "UD"
                }
    		};
        return options;
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

    global.conDecEvolutionPage = new ConDecEvolutionPage();
})(window);
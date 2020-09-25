(function (global) {

    /* private vars */
    var networkRight = null;
    var networkLeft = null;

    var ConDecComparison = function ConDecComparison() {
    };

    ConDecComparison.prototype.initView = function () {
        console.log("ConDecComparison initView");    
            
        // Fill HTML elements for filter criteria            
        conDecFiltering.fillFilterElements("comparison"); 
        conDecFiltering.fillDatePickers("comparison", 30); // left side
        conDecFiltering.fillDatePickers("comparison-right", 7);
        
        // Register/subscribe this view as an observer
        conDecObservable.subscribe(this);
        
        // Fill view
        this.buildCompare();
    };
    
    ConDecComparison.prototype.updateView = function () {
    	document.getElementById("filter-button-comparison").click();
	};

    ConDecComparison.prototype.buildCompare = function buildCompare() {        
        var containerLeft = document.getElementById('left-network');
        networkLeft = new vis.Network(containerLeft, new vis.DataSet(), conDecVis.getOptionsForHierarchicalGraph());
        networkLeft.setSize("100%", "500px");
        networkLeft.on("oncontext", function (params) {
            conDecVis.addContextMenu(params, networkLeft);
        });
        networkLeft.on("selectNode", function (params) {
            networkRight.focus(params.nodes[0]);
            networkLeft.focus(params.nodes[0]);
        });
        var containerRight = document.getElementById('right-network');
        networkRight = new vis.Network(containerRight, new vis.DataSet(), conDecVis.getOptionsForHierarchicalGraph());
        networkRight.setSize("100%", "500px");
        networkRight.on("oncontext", function (params) {
            conDecVis.addContextMenu(params, networkRight);
        });

        networkRight.on("selectNode", function (params) {
            networkRight.focus(params.nodes[0]);
            networkLeft.focus(params.nodes[0]);
        });
        
        conDecFiltering.addOnClickEventToFilterButton("comparison", function(filterSettings) {
        	// left side
            var filterSettingsLeft = filterSettings;            
            filterSettingsLeft ["isHierarchical"] = true;
            conDecAPI.getVis(filterSettingsLeft, function (visDataLeft) {
            	visDataLeft.nodes.sort(sortVis);
                var dateLeft = {
                    nodes: visDataLeft.nodes,
                    edges: visDataLeft.edges
                };
                networkLeft.setData(dateLeft);
                networkLeft.setOptions(conDecVis.getOptionsForHierarchicalGraph(visDataLeft));
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
                networkRight.setOptions(conDecVis.getOptionsForHierarchicalGraph(visDataRight));
            });
        });
        
        document.getElementById("filter-button-comparison").click();
    };
    
    function sortVis(a, b) {
        if (a.id > b.id) {
            return 1;
        }
        if (a.id < b.id) {
            return -1;
        }
        return 0;
    }

    global.conDecComparison = new ConDecComparison();
})(window);
(function (global) {

    /* private vars */
    var timeline = null;

    var ConDecChronology = function ConDecChronology() {
    };

    ConDecChronology.prototype.initView = function () {
        console.log("ConDecChronology initView");           
        
        // Fill HTML elements for filter criteria
        conDecFiltering.fillFilterElements("chronology", ["Decision"]);      
        conDecFiltering.fillDatePickers("chronology", 30);
        
        // Register/subscribe this view as an observer
        conDecObservable.subscribe(this);
        
        // Fill view
        this.buildTimeLine();
    };
    
    ConDecChronology.prototype.updateView = function () {
    	document.getElementById("filter-button-chronology").click();
	};

	ConDecChronology.prototype.buildTimeLine = function () {
        console.log("ConDecChronology build chronology");
        
        var container = document.getElementById("evolution-timeline");
        timeline = new vis.Timeline(container, new vis.DataSet(), {});
        timeline.on("contextmenu", function (properties) {
            properties.event.preventDefault();
            var nodeId = properties.item;
            var documentationLocation = timeline.itemsData.get(nodeId).documentationLocation;
            conDecContextMenu.createContextMenu(nodeId, documentationLocation, properties.event, "evolution-timeline");
        });
        
        conDecFiltering.addOnClickEventToFilterButton("chronology", function(filterSettings) {
        	conDecAPI.getEvolutionData(filterSettings, function (visData) {
                var data = visData.dataSet;
                var groups = visData.groupSet;
                var item = new vis.DataSet(data);
                timeline.setItems(item);
                timeline.setGroups(groups);
                timeline.redraw();
            });
        });
        
        document.getElementById("filter-button-chronology").click();
    };

    global.conDecChronology = new ConDecChronology();
})(window);
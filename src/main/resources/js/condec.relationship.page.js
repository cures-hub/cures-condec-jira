(function (global) {

    var ConDecRelationshipPage = function ConDecRelationshipPage() {
    };

    ConDecRelationshipPage.prototype.initView = function () {
        console.log("ConDecRelationshipPage initView");       

        // Fill HTML elements for filter criteria
        conDecFiltering.fillFilterElements("graph", ["Decision"]);
        conDecFiltering.addOnClickEventToFilterButton("graph", function(filterSettings) {
        	conDecAPI.getVis(filterSettings, function (knowledgeGraph) {
            	conDecVis.buildGraphNetwork(knowledgeGraph, "graph-container");
            });
        });

        // Register/subscribe this view as an observer
        conDecObservable.subscribe(this);
        
        // Fill view
        this.updateView();
    };

    ConDecRelationshipPage.prototype.updateView = function () {
    	document.getElementById("filter-button-graph").click();
    };

    global.conDecRelationshipPage = new ConDecRelationshipPage();
})(window);
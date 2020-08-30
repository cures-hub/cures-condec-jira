(function (global) {

    /* private vars */
    var conDecObservable = null;
    var conDecAPI = null;
    var conDecVis = null;

    var ConDecRelationshipPage = function ConDecRelationshipPage() {
    };

    ConDecRelationshipPage.prototype.init = function (_conDecAPI, _conDecObservable, _conDecVis) {
        console.log("ConDecRelationshipPage init");
        if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)) {
            conDecAPI = _conDecAPI;
            conDecObservable = _conDecObservable;
            conDecVis = _conDecVis;

            fillFilterElements();
            addOnClickEventToFilterButton();

            // Register/subscribe this view as an observer
            conDecObservable.subscribe(this);
            return true;
        }
        return false;
    };

    ConDecRelationshipPage.prototype.updateView = function () {
    	document.getElementById("filter-button-graph").click();
    };
    
    function fillFilterElements() {
    	conDecAPI.getLinkTypes(function (linkTypes) {
			var linkTypeArray = [];
			for (linkType in linkTypes) {
				if (linkType !== undefined) {
					linkTypeArray.push(linkType);
				}				
			}
			conDecFiltering.initDropdown("link-type-dropdown-graph", linkTypeArray);
		});	

    	conDecAPI.fillDecisionGroupSelect("select2-decision-group-graph");
        conDecFiltering.initDropdown("status-dropdown-graph", conDecAPI.knowledgeStatus);
        conDecFiltering.initDropdown("knowledge-type-dropdown-graph", conDecAPI.getKnowledgeTypes(), ["Decision"]);
    }

    function addOnClickEventToFilterButton() {
        var filterButton = document.getElementById("filter-button-graph");

        filterButton.addEventListener("click", function (event) {
        	var filterSettings = conDecFiltering.getFilterSettings("graph");
        	
        	conDecAPI.getVis(filterSettings, function (knowledgeGraph) {
            	conDecVis.buildGraphNetwork(knowledgeGraph, 'graph-container');
            });
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

    global.conDecRelationshipPage = new ConDecRelationshipPage();
})(window);
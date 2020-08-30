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

            addOnClickEventToDecisionLinkFilterButton();

            conDecObservable.subscribe(this);
            return true;
        }
        return false;
    };

    ConDecRelationshipPage.prototype.buildDecisionGraph = function () {
        console.log("ConDec build Decision Relationship Graph");        
        var filterButton = document.getElementById("filterDecisionLinks-button");
        filterButton.click();       
    };

    ConDecRelationshipPage.prototype.updateView = function () {
        conDecRelationshipPage.buildDecisionGraph();
    }

    function addOnClickEventToDecisionLinkFilterButton() {
    	conDecAPI.getLinkTypes(function (linkTypes) {
			var linkTypeArray = [];
			for (linkType in linkTypes) {
				if (linkType !== undefined) {
					linkTypeArray.push(linkType);
				}				
			}
			conDecFiltering.initDropdown("linktype-dropdown", linkTypeArray);
		});	

    	conDecAPI.fillDecisionGroupSelect("select2-decision-group-graph");
        conDecFiltering.initDropdown("status-dropdown-graph", conDecAPI.knowledgeStatus);
        conDecFiltering.initDropdown("knowledge-type-dropdown-graph", conDecAPI.getKnowledgeTypes(), ["Decision"]);

        var filterButton = document.getElementById("filterDecisionLinks-button");

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
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

        conDecAPI.fillDecisionGroupSelect("select2-decision-group-relationshipView");

        var filterSettings = {
				"knowledgeTypes": ["Decision"]
		};
        conDecAPI.getVis(filterSettings, function (knowledgeGraph) {
            conDecVis.buildGraphNetwork(knowledgeGraph, 'graph-container');
        });
    };

    ConDecRelationshipPage.prototype.buildDecisionGraphFiltered = function (linkTypes, searchString, status, selectedGroups) {
    	var filterSettings = {
				"searchTerm": searchString,
				"knowledgeTypes": ["Decision"],
				"status": status,
				"linkTypes": linkTypes,
				"groups": selectedGroups
		};
        conDecAPI.getVis(filterSettings, function (knowledgeGraph) {
        	conDecVis.buildGraphNetwork(knowledgeGraph, 'graph-container');
        });
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

        conDecFiltering.initDropdown("status-dropdown-graph", conDecAPI.knowledgeStatus);

        var filterButton = document.getElementById("filterDecisionLinks-button");

        filterButton.addEventListener("click", function (event) {
            var linkTypes = conDecFiltering.getSelectedItems("linktype-dropdown");
            var status = conDecFiltering.getSelectedItems("status-dropdown-graph");
            var searchString = document.getElementById("decision-search-input").value;
            var selectedGroups = conDecFiltering.getSelectedGroups("select2-decision-group-relationshipView");

            conDecRelationshipPage.buildDecisionGraphFiltered(linkTypes, searchString, status, selectedGroups);
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
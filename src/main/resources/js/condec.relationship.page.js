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
        conDecAPI.getDecisionGraph(filterSettings, function (knowledgeGraph) {
            buildGraphNetwork(knowledgeGraph);
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
        conDecAPI.getDecisionGraph(filterSettings, function (knowledgeGraph) {
            buildGraphNetwork(knowledgeGraph);
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

    function buildGraphNetwork(data) {
        var coloredEdges = [];
        for (var e in data.edges) {
            data.edges[e].color = {
                color: data.edges[e].color,
                inherit: false
            }
            coloredEdges.push(data.edges[e]);
        }

        var dataset = {
            nodes: data.nodes,
            edges: coloredEdges
        };

        var graphContainer = document.getElementById('graph-container');

        var options = {
            edges: {
                arrows: "to",
                length: 200
            },
            layout: {
                randomSeed: 228332
            },
            manipulation: {
                enabled: true,
                addNode: false,
                deleteNode: function (data, callback) {
                    conDecVis.deleteNode(data);
                },
                addEdge: function (data, callback) {
                    conDecVis.addEdge(data);
                },
                deleteEdge: function (data, callback) {
                    conDecVis.deleteEdge(data, dataset);
                },
                editEdge: false
            },
            physics: {
                enabled: true,
                barnesHut: {
                    avoidOverlap: 0.2
                }
            }
        };

        var graphNetwork = new vis.Network(graphContainer, dataset, options);

        graphNetwork.on("oncontext", function (params) {
            conDecVis.addContextMenu(params, graphNetwork);
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
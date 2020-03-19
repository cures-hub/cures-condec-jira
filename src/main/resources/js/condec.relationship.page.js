(function(global) {

	/* private vars */
	var conDecObservable = null;
	var conDecAPI = null;
	var conDecVis = null;

	var ConDecRelationshipPage = function ConDecRelationshipPage() {
	};

	ConDecRelationshipPage.prototype.init = function(_conDecAPI, _conDecObservable, _conDecVis) {
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

	ConDecRelationshipPage.prototype.buildDecisionGraph = function() {
		console.log("ConDec build Decision Relationship Graph");

		conDecAPI.fillDecisionGroupSelect("select2-decision-group-relationshipView");
		
		conDecAPI.getDecisionGraph(function (knowledgeGraph) {
			buildGraphNetwork(knowledgeGraph);
		});
	};

	ConDecRelationshipPage.prototype.buildDecisionGraphFiltered = function(linkTypes, searchString, selectedGroups) {
		conDecAPI.getDecisionGraphFiltered(linkTypes, searchString, selectedGroups, function (knowledgeGraph) {
			buildGraphNetwork(knowledgeGraph);
		});
	};

	ConDecRelationshipPage.prototype.updateView = function() {
		conDecRelationshipPage.buildDecisionGraph();
	}

	function addOnClickEventToDecisionLinkFilterButton() {
		var linkTypes = conDecAPI.linkTypes;
		conDecFiltering.initDropdown("linktype-dropdown", linkTypes);

		var filterButton = document.getElementById("filterDecisionLinks-button");

		filterButton.addEventListener("click", function(event) {
			var linkTypes = conDecFiltering.getSelectedItems("linktype-dropdown");
			var searchString = document.getElementById("decision-search-input").value;
			
			var selectedGroupsObj = $('#select2-decision-group-relationshipView').select2('data');
			var selectedGroups = [];
			for(var i=0; i<= selectedGroupsObj.length; i++){
				if(selectedGroupsObj[i]){
					selectedGroups[i] = selectedGroupsObj[i].text;				
				}
			}
			
			conDecRelationshipPage.buildDecisionGraphFiltered(linkTypes, searchString, selectedGroups);
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
			layout : {
				randomSeed : 228332
			},
			manipulation: {
				enabled: true,
				addNode: false,
				deleteNode: function(data, callback) {
					conDecVis.deleteNode(data, callback);
				},
				addEdge: function (data, callback) {
					conDecVis.addEdgeWithType(data, callback);
				},
				deleteEdge: function(data, callback) {
					conDecVis.deleteEdge(data, dataset, callback);
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

		graphNetwork.on("oncontext", function(params) {
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
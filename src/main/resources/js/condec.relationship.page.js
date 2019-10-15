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

			initDecisionLinkFilter();
			addOnClickEventToDecisionLinkFilterButton();

			conDecObservable.subscribe(this);
			return true;
		}
		return false;
	};

	ConDecRelationshipPage.prototype.buildDecisionGraph = function() {
		console.log("ConDec build Decision Relationship Graph");

		conDecAPI.getDecisionGraph(function (data) {
			buildGraphNetwork(data);
		});
	};

	ConDecRelationshipPage.prototype.buildDecisionGraphFiltered = function(linkTypes) {
		conDecAPI.getDecisionGraphFiltered(linkTypes, function (data) {
			buildGraphNetwork(data);
		});
	};

	ConDecRelationshipPage.prototype.updateView = function() {
		conDecRelationshipPage.buildDecisionGraph();
	}

	function applyDecisionLinkFilter() {
		var linkTypes = [];

		for (var i = 0; i < AJS.$('#linktype-dropdown').children().size(); i++) {
			if (typeof AJS.$('#linktype-dropdown').children().eq(i).attr('checked') !== typeof undefined
				&& AJS.$('#linktype-dropdown').children().eq(i).attr('checked') !== false) {
				linkTypes.push(AJS.$('#linktype-dropdown').children().eq(i).text());
			}
		}
		conDecRelationshipPage.buildDecisionGraphFiltered(linkTypes);
	}

	function addOnClickEventToDecisionLinkFilterButton() {
		var filterButton = document.getElementById("filterDecisionLinks-button");

		filterButton.addEventListener("click", function(event) {
			event.preventDefault();
			event.stopPropagation();
			applyDecisionLinkFilter();
		});
	}

	function initDecisionLinkFilter() {
		var linkTypeDropdown = document.getElementById("linktype-dropdown");
		conDecAPI.getFilterSettings("projectKey", "", function(filterData) {
			var allLinkTypes = filterData.allLinkTypes;
			var selectedLinkTypes = filterData.selectedLinkTypes;
			linkTypeDropdown.innerHTML = "";

			for (var index in allLinkTypes) {
				var isSelected = "";
				if (selectedLinkTypes.includes(allLinkTypes[index])) {
					isSelected = "checked";
				}
				linkTypeDropdown.insertAdjacentHTML("beforeend", "<aui-item-checkbox interactive " + isSelected + ">"
					+ allLinkTypes[index] + "</aui-item-checkbox>");
			}
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
				addNode: function(data, callback) {
					conDecVis.addNode(data, callback);
				},
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
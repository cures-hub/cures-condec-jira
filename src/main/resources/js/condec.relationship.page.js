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

			conDecObservable.subscribe(this);
			return true;
		}
		return false;
	};

	ConDecRelationshipPage.prototype.buildDecisionGraph = function() {
		console.log("ConDec build Decision Relationship Graph");

		conDecAPI.getCompareVis(-1, -1,"", ["Decision"],
			conDecAPI.knowledgeStatus, function (data) {
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
						arrows: "to"
					},
                    layout : {
                        randomSeed : 228332
                    }
				};

				var graphNetwork = new vis.Network(graphContainer, dataset, options);

				graphNetwork.on("oncontext", function(params) {
					conDecVis.addContextMenu(params, graphNetwork);
				});
			});
	};

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
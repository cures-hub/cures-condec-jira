(function(global) {

	/* private vars */
	var conDecObservable = null;
	var conDecAPI = null;

	var ConDecRelationshipPage = function ConDecRelationshipPage() {
	};

	ConDecRelationshipPage.prototype.init = function(_conDecAPI, _conDecObservable) {
		console.log("ConDecRelationshipPage init");
		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)) {
			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;

			conDecObservable.subscribe(this);
			return true;
		}
		return false;
	};

	ConDecRelationshipPage.prototype.buildDecisionGraph = function() {
		console.log("ConDec build Decision Relationship Graph");

		conDecAPI.getCompareVis(-1, -1,"", ["Decision"],
			conDecAPI.knowledgeStatus, function (data) {
				var dataset = {
					nodes: data.nodes,
					edges: data.edges
				};

				var graphContainer = document.getElementById('graph-container');

				var options = {
					edges: {
						arrows: "to"
					}
				};

				var graphNetwork = new vis.Network(graphContainer, dataset, options);
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
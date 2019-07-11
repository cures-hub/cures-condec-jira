(function(global) {

	/* private vars */
	var conDecObservable = null;
	var conDecAPI = null;

	var ConDecEvolutionPage = function ConDecEvolutionPage() {
	};

	ConDecEvolutionPage.prototype.init = function(_conDecAPI, _conDecObservable) {
		console.log("ConDecEvolutionPage init");
		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)) {
			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;

			conDecObservable.subscribe(this);
			return true;
		}
		return false;
	};
	
	ConDecEvolutionPage.prototype.buildTimeLine = function buildTimeLine(projectKey) {
		console.log("ConDec build timeline");
		conDecAPI.getEvolutionData(function(evolutionData) {
			var container = document.getElementById('evolution-timeline');
			var data = evolutionData;
			var item = new vis.DataSet(data);
			var options = {};
			var timeline = new vis.Timeline(container, item, options);
		});
	};

	ConDecEvolutionPage.prototype.buildCompare = function buildCompare(projectKey, firstDate, secondDate) {
		console.log("ConDec build compare view");
        conDecAPI.getCompareVis("-1", "-1", function (visData) {
            console.log("Test if this is shown we have some other problem");
            var containerleft = document.getElementById('left-network');
            var dataleft = {
                nodes : visData.nodes,
                edges : visData.edges
            };
            var options = {};
            var networkleft = new vis.Network(containerleft, dataleft, options);
        });
        conDecAPI.getCompareVis(firstDate, secondDate,function (visData) {
            var containerright = document.getElementById('right-network');
            var dataright = {
                nodes : visData.nodes,
                edges : visData.edges
            };
            var options = {};
            var networkright = new vis.Network(containerright, dataright, options);
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
	global.conDecEvolutionPage = new ConDecEvolutionPage();
})(window);
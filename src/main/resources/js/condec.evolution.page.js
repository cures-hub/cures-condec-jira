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
		conDecAPI.getEvolutionData(projectKey, function(evolutionData) {
			var container = document.getElementById('evolution-timeline');
			var data = evolutionData;
			var item = new vis.DataSet(data);
			var options = {};
			var timeline = new vis.Timeline(container, item, options);
		});
	};

	ConDecEvolutionPage.prototype.buildCompare = function buildCompare(projectKey, firstDate, scondDate) {
		console.log("ConDec build compare view");

		var containerleft = document.getElementById('left-network');
		var containerright = document.getElementById('right-network');

		var options = {};
		var nodesleft = new vis.DataSet([ {
			id : 1,
			label : 'Node 1'
		}, {
			id : 2,
			label : 'Node 2'
		}, {
			id : 3,
			label : 'Node 3'
		} ]);
		var edgesleft = new vis.DataSet([ {
			from : 1,
			to : 3
		}, {
			from : 1,
			to : 2
		}, {
			from : 3,
			to : 3
		} ]);

		var dataleft = {
			nodes : nodesleft,
			edges : edgesleft
		};
		var networkleft = new vis.Network(containerleft, dataleft, options);

		var nodesright = new vis.DataSet([ {
			id : 1,
			label : 'Node 1'
		}, {
			id : 2,
			label : 'Node 2'
		}, {
			id : 3,
			label : 'Node 3'
		}, {
			id : 4,
			label : 'Node 4'
		}, {
			id : 5,
			label : 'Node 5'
		} ]);

		// create an array with edges
		var edgesright = new vis.DataSet([ {
			from : 1,
			to : 3
		}, {
			from : 1,
			to : 2
		}, {
			from : 2,
			to : 4
		}, {
			from : 2,
			to : 5
		}, {
			from : 3,
			to : 3
		} ]);

		// create a network

		var dataright = {
			nodes : nodesright,
			edges : edgesright
		};
		var networkright = new vis.Network(containerright, dataright, options);
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
(function(global) {

	/* private vars */
	var conDecObservable = null;
	var conDecAPI = null;
	var conDecVis = null;
	var networkRight = null;

	var ConDecEvolutionPage = function ConDecEvolutionPage() {
	};

	ConDecEvolutionPage.prototype.init = function(_conDecAPI, _conDecObservable, _conDecVis) {
		console.log("ConDecEvolutionPage init");
		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)) {
			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;
			conDecVis = _conDecVis;

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
        conDecAPI.getCompareVis("-1", "-1","",function (visData) {
            console.log("Test if this is shown we have some other problem");
            var containerleft = document.getElementById('left-network');
            var dataleft = {
                nodes : visData.nodes,
                edges : visData.edges
            };
            var options = conDecVis.getVisOptions();
            var networkLeft = new vis.Network(containerleft, dataleft, options);

        });
        conDecAPI.getCompareVis(firstDate, secondDate,"",function (visData) {
            var containerright = document.getElementById('right-network');
            var dataright = {
                nodes : visData.nodes,
                edges : visData.edges
            };
            var options = conDecVis.getVisOptions();
            networkRight = new vis.Network(containerright, dataright, options);
        });
        addOnClickEventToFilterButton();
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

	//Compute filter and select new elements
    function addOnClickEventToFilterButton() {
        console.log("ConDecJiraEvolutionPage addOnClickEventToFilterButtonCompare");

        var filterButton = document.getElementById("filter-button-compare");

        filterButton.addEventListener("click", function(event) {
            var firstDate = document.getElementById("start-data-picker").valueAsNumber;
            var secondDate = document.getElementById("end-data-picker").valueAsNumber;
            var searchString = document.getElementById("compare-search-input").value;
            console.log(searchString);
            console.log(firstDate);
            console.log(secondDate);
            conDecAPI.getCompareVis(firstDate, secondDate,searchString, function (visData) {
                var dataRight = {
                    nodes : visData.nodes,
                    edges : visData.edges
                };
                networkRight.setData(dataRight);
            });
        });
    }
	global.conDecEvolutionPage = new ConDecEvolutionPage();
})(window);
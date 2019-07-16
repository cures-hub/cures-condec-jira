(function(global) {

	/* private vars */
	var conDecObservable = null;
	var conDecAPI = null;
	var conDecVis = null;
	var networkRight = null;
	var timeline = null;

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
	
	ConDecEvolutionPage.prototype.buildTimeLine = function buildTimeLine() {
		console.log("ConDec build timeline");
		conDecAPI.getEvolutionData("", -1, -1 ,function(evolutionData) {
			var container = document.getElementById('evolution-timeline');
			var data = evolutionData;
			var item = new vis.DataSet(data);
			var options = {};
			timeline = new vis.Timeline(container, item, options);
		});
        addOnClickEventToFilterTimeLineButton();
	};

	ConDecEvolutionPage.prototype.buildCompare = function buildCompare() {
		console.log("ConDec build compare view");
        conDecAPI.getCompareVis(-1, -1,"",function (visData) {
            var containerLeft = document.getElementById('left-network');
            var dataLeft = {
                nodes : visData.nodes,
                edges : visData.edges
            };
            var options = conDecVis.getVisOptions();
            var networkLeft = new vis.Network(containerLeft, dataLeft, options);

        });
        conDecAPI.getCompareVis(-1, -1,"",function (visData) {
            var containerRight = document.getElementById('right-network');
            var dataRight = {
                nodes : visData.nodes,
                edges : visData.edges
            };
            var options = conDecVis.getVisOptions();
            networkRight = new vis.Network(containerRight, dataRight, options);
        });
        addOnClickEventToFilterCompareButton();
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
    function addOnClickEventToFilterCompareButton() {
        console.log("ConDecJiraEvolutionPage addOnClickEventToFilterButtonCompare");

        var filterButton = document.getElementById("filter-button-compare");

        filterButton.addEventListener("click", function(event) {
            var firstDate = -1;
            var secondDate = -1;
            if (!isNaN(document.getElementById("start-data-picker-compare").valueAsNumber)) {
                firstDate = document.getElementById("start-data-picker-compare").valueAsNumber;
            }
            if (!isNaN(document.getElementById("end-data-picker-compare").valueAsNumber)) {
                secondDate = document.getElementById("end-data-picker-compare").valueAsNumber;
            }
            var searchString = "";
            searchString = document.getElementById("compare-search-input").value;
            conDecAPI.getCompareVis(firstDate, secondDate,searchString, function (visData) {
                var dataRight = {
                    nodes : visData.nodes,
                    edges : visData.edges
                };
                networkRight.setData(dataRight);
            });
        });
    }

    //Compute filter and select new elements in the TimeLine View
    function addOnClickEventToFilterTimeLineButton() {
        console.log("ConDecJiraEvolutionPage addOnClickEventToFilterButtonTimeLine");
        var filterButton = document.getElementById("filter-button-time");

        filterButton.addEventListener("click", function(event) {
            var firstDate = -1;
            var secondDate = -1;
            if (!isNaN(document.getElementById("start-date-picker-time").valueAsNumber)) {
                firstDate = document.getElementById("start-date-picker-time").valueAsNumber;
            }
            if (!isNaN(document.getElementById("end-date-picker-time").valueAsNumber)) {
                secondDate = document.getElementById("end-date-picker-time").valueAsNumber;
            }
            var searchString = document.getElementById("time-search-input").value;
            conDecAPI.getEvolutionData(searchString, firstDate, secondDate,  function (visData) {
                var data = visData;
                var item = new vis.DataSet(data);
                timeline.setItems(item);
                timeline.redraw();
            });
        });
    }

	global.conDecEvolutionPage = new ConDecEvolutionPage();
})(window);
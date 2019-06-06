(function (global) {

    /* private vars */
    var conDecObservable = null;
    var conDecAPI = null;

    var ConDecEvolutionPage = function ConDecEvolutionPage() {
    };

    ConDecEvolutionPage.prototype.init = function (_conDecAPI, _conDecObservable) {
        console.log("ConDecEvolutionPage init");
        if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)){
            conDecAPI = _conDecAPI;
            conDecObservable = _conDecObservable;

            conDecObservable.subscribe(this);
            return true;
        }
        return  false;
    };
    ConDecEvolutionPage.prototype.buildTimeLine = function buildTimeLine(projectKey) {
        console.log("ConDec build timeline");
        console.log(conDecAPI);
        conDecAPI.getEvolutionData(projectKey,function (evolutionData) {
            var container = document.getElementById('evolution-timeline');
            console.log(evolutionData);
            var data = evolutionData;
            var item = new vis.DataSet(data);
            var options = {};
            var timeline = new vis.Timeline(container,item, options);
            timeline.setSize("100%","600px");
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
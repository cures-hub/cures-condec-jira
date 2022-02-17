(function (global) {

    /* private vars */
    var networkRight = null;
    var networkLeft = null;

    var ConDecComparison = function ConDecComparison() {
    };

    ConDecComparison.prototype.initView = function () {
        console.log("ConDecComparison initView");

        // Fill HTML elements for filter criteria            
        conDecFiltering.fillFilterElements("comparison");
        conDecFiltering.fillDatePickers("comparison", 30); // left side
        conDecFiltering.fillDatePickers("comparison-right", 7);
        addOnClickEventToFilterButton();

        // Register/subscribe this view as an observer
        conDecObservable.subscribe(this);       

        // Fill view
        this.updateView();
    };

    ConDecComparison.prototype.updateView = function () {
        document.getElementById("filter-button-comparison").click();
    };

    function addOnClickEventToFilterButton () {
        conDecFiltering.addOnClickEventToFilterButton("comparison", function (filterSettings) {
            // left side
            conDecViewAPI.getVis(filterSettings, function (visDataLeft) {
                visDataLeft.nodes.sort(sortVis);
                var containerLeft = document.getElementById('left-network');
                var optionsLeft = conDecVis.getOptions(visDataLeft, filterSettings["isHierarchical"]);
                networkLeft = new vis.Network(containerLeft, visDataLeft, optionsLeft);
                networkLeft.setSize("100%", "500px");
                networkLeft.on("oncontext", function (params) {
                    conDecVis.addContextMenu(params, networkLeft);
                });
                networkLeft.on("selectNode", function (params) {
                    networkRight.focus(params.nodes[0]);
                    networkLeft.focus(params.nodes[0]);
                });
            });

            // right side
            var filterSettingsRight = filterSettings;
            var startDatePicker = document.getElementById("start-date-picker-comparison-right");
            filterSettingsRight["startDate"] = new Date(startDatePicker.value).getTime();
            var endDatePicker = document.getElementById("end-date-picker-comparison-right");
            filterSettingsRight["endDate"] = new Date(endDatePicker.value).getTime();
            conDecViewAPI.getVis(filterSettingsRight, function (visDataRight) {
                visDataRight.nodes.sort(sortVis);
                var containerRight = document.getElementById('right-network');
                var optionsRight = conDecVis.getOptions(visDataRight, filterSettings["isHierarchical"]);
                networkRight = new vis.Network(containerRight, visDataRight, optionsRight);
                networkRight.setSize("100%", "500px");
                networkRight.on("oncontext", function (params) {
                    conDecVis.addContextMenu(params, networkRight);
                });

                networkRight.on("selectNode", function (params) {
                    networkRight.focus(params.nodes[0]);
                    networkLeft.focus(params.nodes[0]);
                });
            });
        });
    }

    function sortVis(a, b) {
        if (a.id > b.id) {
            return 1;
        }
        if (a.id < b.id) {
            return -1;
        }
        return 0;
    }

    global.conDecComparison = new ConDecComparison();
})(window);
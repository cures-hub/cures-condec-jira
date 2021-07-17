/*
 This module fills the box plots and pie charts used in the requirements dashboard page.

 Requires
 * eChart

 Is referenced in HTML by
 * dashboard/generalMetrics.vm
 * dashboard/rationaleCompleteness.vm
 * dashboard/rationaleCoverage.vm
 * dashboard/featureBranches.vm
 */
(function (global) {

    var detailsOverlayClickHandlersSet = false;

    var issueKeyParser = /([^a-z]*)([a-z]+)-([0-9]+).*/gi

    var isIssueData = true;

    var colorPalette = null;

    const CHART_RICH_PIE = "piechartRich";
    const CHART_SIMPLE_PIE = "piechartInteger";
    const CHART_BOXPLOT = "boxplot";
    const DEC_STRING_SEPARATOR = " ";

    var ConDecReqDash = function ConDecReqDash() {
    };

    ConDecReqDash.prototype.init = function init() {
    }
    
    /** 
     * @issue How to convert a velocity map into a Javascript map?
	 * @decision Do a manual conversion from a velocity map into a Javascript map!
	 * @con Not very elegant at all.
	 * @alternative Directly use velocity maps in Javascript.
	 * @con It is not possible to use velocity maps in Javascript.
	 */	
	function getMap(velocityMap) {
		velocityMap = JSON.stringify(velocityMap);

		// removes brackets
		velocityMap = velocityMap.replaceAll("\{", "")
		velocityMap = velocityMap.replaceAll("\}", "");
		velocityMap = velocityMap.replaceAll("\":", "\" : ");
		velocityMap = velocityMap.replaceAll("\"", "");

		var jsMap = new Map();
		var mapEntries = velocityMap.split(",");
		for (var i = 0; i < mapEntries.length; i++) {
			var mapEntry = mapEntries[i].split(" : ");
			jsMap.set(mapEntry[0], mapEntry[1]);
		}
		return jsMap;
	}

	ConDecReqDash.prototype.initializeChart = function (divId, title, subtitle, dataMap) {
		isIssueData = true;
		colorPalette = null;
		this.initializeChartForSources(divId, title, subtitle, getMap(dataMap));
	}

	/* used by branch dashboard item condec.rationale.coverage.dashboard.js */
	ConDecReqDash.prototype.initializeChartWithColorPalette = function (divId, title, subtitle, dataMap, palette) {
		isIssueData = true;
		colorPalette = palette;
		this.initializeChartForSources(divId, title, subtitle, getMap(dataMap));
	}

    /* used by branch dashboard item condec.git.branches.dashboard.js */
    ConDecReqDash.prototype.initializeChartForBranchSource = function (divId, title, subtitle, dataMap) {
        isIssueData = false;
		colorPalette = null;
        this.initializeChartForSources(divId, title, subtitle, dataMap);
    }

    /* TODO: Below function does not need to be exposed! */
    ConDecReqDash.prototype.initializeChartForSources = function (divId, title, subtitle, dataMap) {
        var domElement = document.getElementById(divId);
        if (!domElement) {
            console.warn("Could not find element with ID: " + divId);
            return;
        }
        var chart = echarts.init(domElement);
        if (!chart) {
            console.warn("Could not init chart for element " + divId);
            return;
        }
        // pick the chart type based on html element's id attribute
        if (divId.startsWith(CHART_RICH_PIE)) {
            chart = this.initializeDivWithPieChartData(chart, title, subtitle, dataMap, true);
        } else if (divId.startsWith(CHART_SIMPLE_PIE)) {
            chart = this.initializeDivWithPieChartData(chart, title, subtitle, dataMap, false);
        } else if (divId.startsWith(CHART_BOXPLOT)) {
            chart = this.initializeDivWithBoxPlotFromMap(chart, title, subtitle, dataMap);
        } else {
            chart = null;
        }

        if (!chart) {
            console.error("could not setup chart for element " + divId);
        }
        // add click handler for chart data (in canvas)
        chart.on('click', echartDataClicked);
        // remove development aids
        domElement.classList.remove("notsetyet");
    }

    ConDecReqDash.prototype.initializeDivWithBoxPlotFromMap = function (boxplot, title, xAxis, dataMap) {
        var values = [];
        var keysAsArray = Array.from(dataMap.keys());
        for (var i = keysAsArray.length - 1; i >= 0; i--) {
            var value = Number(dataMap.get(keysAsArray[i]));
            values.push(value);
        }

        var data = echarts.dataTool.prepareBoxplotData(new Array(values));
        boxplot.setOption(getOptionsForBoxplot(title, xAxis, "", data));
        boxplot.rawConDecData = dataMap;
        return boxplot;
    };

    /* TODO: Function does not need to be exposed */
    ConDecReqDash.prototype.initializeDivWithPieChartData = function (pieChart, title, subtitle,
                                                                      objectsMap, hasRichData) {
        var data = [];
        var source = [];

        var sourceCounter = function (accumulator, currentValue) {
            if (currentValue.trim() === "") {
                return accumulator + 0;
            }
            return accumulator + 1
        }

        var dataAsArray = Array.from(objectsMap.keys());
        for (var i = dataAsArray.length - 1; i >= 0; i--) {
            var key = dataAsArray[i];
            var value = objectsMap.get(key)
            var entry = {};
            if (hasRichData && (typeof value === 'string' || value instanceof String)) {
                entry["value"] = value.split(' ').reduce(sourceCounter, 0);
            } else if (!hasRichData && (typeof value === 'string' || value instanceof String)) {
                entry["value"] = Number(value);
            } else {
                entry["value"] = value;
            }
            entry["name"] = key;
            data.push(entry);
            source.push(value);
        }

		pieChart.setOption(getOptionsForPieChart(title, subtitle, dataAsArray, data));
        if (hasRichData) {
			pieChart.groupedConDecData = source;
        }
        return pieChart;
    };

    ConDecReqDash.prototype.navigateToElement = function (elementName) {
		var targetBaseUrl = AJS.contextPath();
		var issueKey = elementName.replace(issueKeyParser, issueKeyBuilder);
        if (!isIssueData) {
			var issueKeyParts = elementName.split("origin/");
			var branchName = issueKeyParts[0];
			if (issueKeyParts.length > 1) { // not local branch name
				branchName = issueKeyParts[1];
			}
			var branchNameParts = branchName.split(".");
			issueKey = branchNameParts[0];
			var newWindow = window.open(targetBaseUrl + '/browse/' + issueKey + '#menu-item-feature-branches', '_blank');
			var script = document.createElement('script');
			function openTab() {
				AJS.tabs.change(AJS.$("a[href=#feature-branches-tab]"));
			}
			script.innerHTML = '(' + openTab.toString() + '());';
			newWindow.onload = function() {
				this.document.body.appendChild(script);
			};
		} else if (elementName.includes('.')) {
			var projectKey = elementName.substring(0, elementName.indexOf('-'));
			var codeFileName = elementName.substring(elementName.indexOf('-') + 1);
			window.open(targetBaseUrl + '/projects/' + projectKey + '?selectedItem=decision-knowledge-page&codeFileName=' + codeFileName, '_blank');
        } else {
			window.open(targetBaseUrl + '/browse/' + issueKey, '_blank');
		}
    }

    ConDecReqDash.prototype.showClickedSource = function (chart, dataIndexClicked, dataClicked) {
        if (chart.hasOwnProperty("groupedConDecData")) { // CHART_RICH_PIE case
            // extract data keys (dec. knowledge elements)
            return showDecKnowledgeElementsOverlay(chart.groupedConDecData[dataIndexClicked], isIssueData);
        } else if (chart.hasOwnProperty("rawConDecData")) { // CHART_BOXPLOT case
            // match source data map entries with clicked data
            var elementsList = extractBoxplotData(chart, dataClicked);
            return showDecKnowledgeElementsOverlay(elementsList, isIssueData);
        } else { // CHART_SIMPLE_PIE or other case
            console.warn("Not supported chart type.")
        }
    }

    function cleanPreviousChildNodes(parentNode) {
        var nodeList = parentNode.childNodes;
        //remove child elements from back, nodeList.length will shrink
        for (var i = nodeList.length - 1; i >= 0; i--) {
            nodeList[i].parentNode.removeChild(nodeList[i]);
        }
    }

    function extractBoxplotData(boxplot, dataClicked) {
        var completeArray2d = Array.from(boxplot.rawConDecData) // [ [decElem, int], ... ]
        var trimmedData = dataClicked.slice(1);
        var involvedElements = completeArray2d.filter(
            function (elem) {
                return this.includes(Number(elem[1]));
            }, trimmedData);

		return involvedElements.map(
			function (val) { // value is a 2-element array
				return val[0];
			}
		).join(DEC_STRING_SEPARATOR);
    }

    function getOptionsForBoxplot(name, xLabel, yLabel, data) {
		return {
			title: [{
				text: name,
				left: "center",
			},],
			tooltip: {
				trigger: "item",
				axisPointer: {
					type: "shadow"
				}
			},
			grid: {
				left: "15%",
				right: "10%",
				bottom: "15%"
			},
			xAxis: {
				type: "category",
				data: data.axisData,
				boundaryGap: true,
				nameGap: 30,
				splitArea: {
					show: false
				},
				axisLabel: {
					formatter: xLabel
				},
			},
			yAxis: {
				type: "value",
				name: yLabel,
				splitArea: {
					show: true
				}
			},
			series: [{
				name: "boxplot",
				type: "boxplot",
				data: data.boxData,

			}, {
				name: "outlier",
				type: "scatter",
				data: data.outliers
			}]
		};
    }

    function getOptionsForPieChart(title, subtitle, dataKeys, dataMap) {
        var options = {
			title: {
				text: title,
				subtext: subtitle,
				x: "center"
			},
			tooltip: {
				trigger: "item",
				formatter: "{b} : {c} ({d}%)"
			},
			legend: {
				orient: "horizontal",
				bottom: "bottom",
				data: dataKeys
			},
			series: [{
				type: "pie",
				radius: "60%",
				center: ["50%", "50%"],
				data: dataMap,
				itemStyle: {
					emphasis: {
						shadowBlur: 10,
						shadowOffsetX: 0,
						shadowColor: "rgba(0, 0, 0, 0.5)"
					}
				},
				avoidLabelOverlap: true,
				label: {
					normal: {
						show: false,
						position: 'center'
					},
					emphasis: {
						show: false
					}
				},
				labelLine: {
					normal: {
						show: false
					}
				}
			}],
		};

        if (colorPalette) {
			options.color = colorPalette;
		}

		return options;
    }

    function issueKeyBuilder(m, p1, p2, p3) {
        return p2 + "-" + p3;
    }

    function fillChildNodes(node, flatList, isIssueData) {
        var listArray = flatList.split(DEC_STRING_SEPARATOR);
        if (!isIssueData) {
            listArray = listArray.map(function (e) {
                return e.replace("refs/remotes/", "");
            });
        }

        for (var i = 0; i < listArray.length; i++) {
            var span = document.createElement("p");
            span.dataset.isbranch = !isIssueData;
            span.innerText = listArray[i];
            node.appendChild(span);
        }
    }

    function showDecKnowledgeElementsOverlay(flatList, isIssueData) {
        var overlay = document.getElementById('decKnowElementsOverlay');
        overlay.classList.remove("hidden");
        var contents = document.getElementById('extractedDecKnowElements');

        if (!detailsOverlayClickHandlersSet) {
            console.info("attaching overlay handlers")
            overlay.addEventListener('click', clickDecKnowledgeElementsOverlay, {
                capture: true,
                once: false,
                passive: false
            });
            contents.addEventListener('click', clickDecKnowledgeElementsInOverlay, {
                capture: true,
                once: false,
                passive: false
            });
            detailsOverlayClickHandlersSet = true;
        }
        cleanPreviousChildNodes(contents);
        fillChildNodes(contents, flatList, isIssueData);
    }

    global.ConDecReqDash = new ConDecReqDash();
})(window);

function echartDataClicked(param) {
    if (typeof param.seriesIndex != 'undefined') {
        ConDecReqDash.showClickedSource(this, param.dataIndex, param.data)
    }
}

function clickDecKnowledgeElementsInOverlay(event) {
    if (event.target.nodeName.toLowerCase() === "p") {
        ConDecReqDash.navigateToElement(event.target.innerText);
    }
}

function clickDecKnowledgeElementsOverlay(event) {
    if (event.target === event.currentTarget) {
        event.currentTarget.classList.add("hidden");
    }
}
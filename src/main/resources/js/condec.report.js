/*
 This module fills the box plots and pie charts used in the report page.

 Requires
 * echart

 Is referenced in HTML by
 * decisionKnowledgeReport.vm
 */
(function(global) {

	var ConDecReport = function ConDecReport() {
	};

	ConDecReport.prototype.initializeDivWithBoxPlot = function initializeDivWithBoxPlot(divId, data, xAxis, title) {
		var boxplot = echarts.init(document.getElementById(divId));
		var data = echarts.dataTool.prepareBoxplotData(new Array(data));
		boxplot.setOption(getOptionsForBoxplot(title, xAxis, "", data));
		document.getElementById(divId).setAttribute("list", data);
	};

	ConDecReport.prototype.initializeDivWithBoxPlotFromMap = function initializeDivWithBoxPlotFromMap(divId, dataMap,
			xAxis, title) {
		var listToShowUserWithAllValues = "";
		var values = [];
		for (var i = Array.from(dataMap.keys()).length - 1; i >= 0; i--) {
			var key = Array.from(dataMap.keys())[i];
			var value = dataMap.get(key);
			listToShowUserWithAllValues = listToShowUserWithAllValues + key + ": " + value + "; ";
			values.push(value);
		}

		var boxplot = echarts.init(document.getElementById(divId));
		var data = echarts.dataTool.prepareBoxplotData(new Array(values));
		boxplot.setOption(getOptionsForBoxplot(title, xAxis, "", data));
		document.getElementById(divId).setAttribute("list", listToShowUserWithAllValues);
	};

	ConDecReport.prototype.initializeDivWithPieChart = function initializeDivWithPieChart(divId, title, subtitle,
			dataMap) {
		var data = [];
		var list = "";

		for (var i = Array.from(dataMap.keys()).length - 1; i >= 0; i--) {
			var key = Array.from(dataMap.keys())[i];
			var entry = new Object();
			entry["value"] = dataMap.get(key);
			entry["name"] = key;
			data.push(entry);
			list = list + " " + key + ": " + dataMap.get(key) + "; ";
		}

		var piechart = echarts.init(document.getElementById(divId));
		piechart.setOption(getOptionsForPieChart(title, subtitle, Array.from(dataMap.keys()), data));
		document.getElementById(divId).setAttribute("list", list);
	};

	function getOptionsForBoxplot(name, xLabel, ylabel, data) {
		return {
			title : [ {
				text : name,
				left : "center",
			}, ],
			tooltip : {
				trigger : "item",
				axisPointer : {
					type : "shadow"
				}
			},
			grid : {
				left : "15%",
				right : "10%",
				bottom : "15%"
			},
			xAxis : {
				type : "category",
				data : data.axisData,
				boundaryGap : true,
				nameGap : 30,
				splitArea : {
					show : false
				},
				axisLabel : {
					formatter : xLabel
				},
			},
			yAxis : {
				type : "value",
				name : ylabel,
				splitArea : {
					show : true
				}
			},
			series : [ {
				name : "boxplot",
				type : "boxplot",
				data : data.boxData,

			}, {
				name : "outlier",
				type : "scatter",
				data : data.outliers
			} ]
		};
	}

	function getOptionsForPieChart(title, subtitle, dataKeys, dataMap) {
		return option = {
			title : {
				text : title,
				subtext : subtitle,
				x : "center"
			},
			tooltip : {
				trigger : "item",
				formatter : "{b} : {c} ({d}%)"
			},
			legend : {
				orient : "horizontal",
				bottom : "bottom",
				data : dataKeys
			},
			series : [ {
				type : "pie",
				radius : "60%",
				center : [ "50%", "50%" ],
				data : dataMap,
				itemStyle : {
					emphasis : {
						shadowBlur : 10,
						shadowOffsetX : 0,
						shadowColor : "rgba(0, 0, 0, 0.5)"
					}
				},
				avoidLabelOverlap : true,
				label : {
					normal : {
						show : false,
						position : 'center'
					},
					emphasis : {
						show : false
					}
				},
				labelLine : {
					normal : {
						show : false
					}
				}
			} ]
		};
	}

	global.conDecReport = new ConDecReport();
})(window);
function initializeDivWithBoxPlot(id, dataFromServer, xAxis, title) {
	var myChart = echarts.init(document.getElementById(id));
	var data = echarts.dataTool.prepareBoxplotData(new Array(dataFromServer));
	myChart.setOption(getOptionsForBoxplot(title, xAxis, "", data));
	document.getElementById(id).setAttribute("list", dataFromServer);
}

function initializeDivWithPieChart(id, title, subtitle, dataMap) {
	var myChart = echarts.init(document.getElementById(id));
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
	myChart.setOption(getOptionsForPieChart(title, subtitle, Array.from(dataMap.keys()), data));
	document.getElementById(id).setAttribute("list", list);
}

function getData(p1) {
	return echarts.dataTool.prepareBoxplotData([ JSON.parse(p1) ]);
}

function getOptionsForBoxplot(name, xLabel, ylabel, data) {
	return {
		title : [ {
			text : name,
			left : 'center',
		}, ],
		tooltip : {
			trigger : 'item',
			axisPointer : {
				type : 'shadow'
			}
		},
		grid : {
			left : '10%',
			right : '10%',
			bottom : '15%'
		},
		xAxis : {
			type : 'category',
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
			type : 'value',
			name : ylabel,
			splitArea : {
				show : true
			}
		},
		series : [ {
			name : 'boxplot',
			type : 'boxplot',
			data : data.boxData,

		}, {
			name : 'outlier',
			type : 'scatter',
			data : data.outliers
		} ]
	};
}

function getOptionsForPieChart(title, subtitle, dataKeys, dataMap) {
	return option = {
		title : {
			text : title,
			subtext : subtitle,
			x : 'center'
		},
		tooltip : {
			trigger : 'item',
			formatter : "{b} : {c} ({d}%)"
		},
		legend : {
			orient : 'horizontal',
			bottom : 'bottom',
			data : dataKeys
		},
		series : [ {
			type : 'pie',
			radius : '55%',
			center : [ '50%', '60%' ],
			data : dataMap,
			itemStyle : {
				emphasis : {
					shadowBlur : 10,
					shadowOffsetX : 0,
					shadowColor : 'rgba(0, 0, 0, 0.5)'
				}
			}
		} ]
	};
}
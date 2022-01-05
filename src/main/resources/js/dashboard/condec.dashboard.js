/**
 This module contains methods used to render the dashboards and their configuration screens.
 
 Requires echarts library to plot metrics as boxplots and pie charts

 Is referenced by
 * condec.general.metrics.dashboard.js
 * condec.git.branches.dashboard.js
 * condec.rationale.completeness.dashboard.js
 * condec.rationale.coverage.dashboard.js
 */
(function(global) {
	var ConDecDashboard = function ConDecDashboard() {
		console.log("ConDecDashboard constructor");
	};

	/**
	 * Initializes a dashboard with saved filterSettings.
	 * 
	 * external references: condec.general.metrics.dashboard.js
	 * condec.git.branches.dashboard.js, 
	 * condec.rationale.completeness.dashboard.js, 
	 * condec.rationale.coverage.dashboard.js
	 * 
	 * @param dashboard reference to the current dashboard item
	 * @param viewIdentifier identifies the HTML elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard API
	 * @param filterSettings the options set in the dashboard configuration
	 */
	ConDecDashboard.prototype.initDashboard = function(dashboard, viewIdentifier, dashboardAPI, savedFilterSettings) {
		dashboardAPI.once("afterRender",
			function() {
				var filterSettings = toFilterSettings(savedFilterSettings);
				if (filterSettings["projectKey"]) {
					dashboardAPI.showLoadingBar();
					document.getElementById("condec-dashboard-selected-project-" + viewIdentifier).innerText = filterSettings.projectKey;
					dashboard.getData(dashboardAPI, filterSettings);
					dashboardAPI.resize();
				}
			});
	};

	/**
	 * Neccessary because dashboardAPI.savePreferences(filterSettings) saves lists as strings 
	 * and cannot save objects such as definitionOfDone.
	 * 
	 * @param filterSettings with lists as strings and no objects such as definitionOfDone
	 */
	function toFilterSettings(filterSettings) {
		filterSettings.knowledgeTypes = toList(filterSettings["knowledgeTypes"]);
		filterSettings.linkTypes = toList(filterSettings["linkTypes"]);
		filterSettings.status = toList(filterSettings["status"]);
		filterSettings.documentationLocations = toList(filterSettings["documentationLocations"]);
		filterSettings.groups = toList(filterSettings["groups"]);
		filterSettings.knowledgeTypesToBeCoveredWithRationale = toList(filterSettings["knowledgeTypesToBeCoveredWithRationale"]);
		filterSettings.changeImpactAnalysisConfig = {};
		filterSettings.definitionOfDone = {
			"minimumDecisionsWithinLinkDistance": filterSettings.minimumDecisionsWithinLinkDistance,
			"maximumLinkDistanceToDecisions": filterSettings.maximumLinkDistanceToDecisions
		};
		return filterSettings;
	}

	/**
	 * Converts a string to a list.
	 * Necessary because lists persists in the preferences as a string.
	 *
	 * @param string the string to be converted to a list.
	 */
	function toList(string) {
		if (!string || !string.length) {
			return null;
		}

		if (Array.isArray(string)) {
			return string;
		}

		string = string.replace("\[", "").replace("\]", "");
		string = string.replaceAll("\"", "");

		return string.split(",");
	}

	/**
	 * Initializes a dashboard configuration screen. 
	 * Automatically when the dashboard edit function is selected or
	 * when the dashboard loads without set filterSettings.
	 * 
	 * external references: condec.general.metrics.dashboard.js
	 * condec.git.branches.dashboard.js,
	 * condec.rationale.completeness.dashboard.js,
	 * condec.rationale.coverage.dashboard.js
	 * 
	 * @param viewIdentifier identifies the HTML elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard API
	 * @param filterSettings the options set in the dashboard configuration
	 */
	ConDecDashboard.prototype.initConfiguration = function(viewIdentifier, dashboardAPI, filterSettings) {
		dashboardAPI.once("afterRender",
			function() {
				showDashboardSection("condec-dashboard-config-", viewIdentifier);

				createSaveButton(dashboardAPI, viewIdentifier);
				createCancelButton(filterSettings, dashboardAPI, viewIdentifier);
				createListener(viewIdentifier);
				setPreferences(filterSettings, viewIdentifier);
				dashboardAPI.resize();
			});
	};

	/**
	 * Process the data that was returned from a REST-call.
	 *
	 * external references: condec.general.metrics.dashboard.js
	 * condec.git.branches.dashboard.js,
	 * condec.rationale.completeness.dashboard.js,
	 * condec.rationale.coverage.dashboard.js
	 *
	 * @param error the error message returned in the REST-call
	 *              null if no error occurred
	 * @param result the result of the REST-call
	 * @param dashboard reference to the current dashboard
	 * @param viewIdentifier identifies the HTML elements of the dashboard
	 * @param dashboardAPI used to call methods of the Jira dashboard API
	 * @param filterSettings the filterSettings used in the REST-call
	 */
	ConDecDashboard.prototype.processData = function(error, result, dashboard, viewIdentifier, dashboardAPI, filterSettings) {
		dashboardAPI.hideLoadingBar();
		if (error) {
			showDashboardSection("condec-dashboard-contents-data-error-", viewIdentifier);
			console.log(error);
		} else {
			showDashboardSection("condec-dashboard-contents-container-", viewIdentifier);
			dashboard.renderData(result, filterSettings);
		}

		dashboardAPI.resize();
	};

	/**
	 * Creates the save button and adds a listener to it.
	 * When the save button is pressed and the projectKey is set
	 * the dashboard renders its content.
	 *
	 * @param dashboardAPI used to call methods of the Jira dashboard API
	 * @param viewIdentifier identifies the HTML elements of the dashboard
	 */
	function createSaveButton(dashboardAPI, viewIdentifier) {
		function onSaveButton() {
			var filterSettings = conDecFiltering.getFilterSettings(viewIdentifier);

			if (filterSettings["projectKey"]) {
				if (filterSettings["definitionOfDone"]) {
					// necessary since savePreferences cannot store objects
					Object.assign(filterSettings, filterSettings.definitionOfDone);
				}
				dashboardAPI.savePreferences(filterSettings);
			}

			dashboardAPI.resize();
		}

		clearListener("save-button-" + viewIdentifier);

		document.getElementById("save-button-" + viewIdentifier).addEventListener("click", onSaveButton);
	}

	/**
	 * Creates the save button and adds a listener to it.
	 * When the cancel button is pressed and projectKey is set
	 * it opens the already rendered content view without
	 * recalculating it
	 *
	 * @param filterSettings the options set in the dashboard configuration
	 * @param dashboardAPI used to call methods of the Jira dashboard API
	 * @param viewIdentifier identifies the HTML elements of the dashboard
	 */
	function createCancelButton(filterSettings, dashboardAPI, viewIdentifier) {
		function onCancelButton() {
			if (filterSettings["projectKey"]) {
				showDashboardSection("condec-dashboard-contents-container-", viewIdentifier);
			}

			dashboardAPI.resize();
		}

		clearListener("cancel-button-" + viewIdentifier);

		document.getElementById("cancel-button-" + viewIdentifier).addEventListener("click", onCancelButton);
	}

	/**
	 * Creates a listener and adds it to the projectKey-dropdown.
	 * If a project is selected, further filter elements can be filled.
	 *
	 * @param viewIdentifier identifies the HTML elements of the dashboard
	 */
	function createListener(viewIdentifier) {
		function onSelectProject() {
			var projectKey = document.getElementById("project-dropdown-" + viewIdentifier).value;
			if (projectKey) {
				conDecAPI.projectKey = projectKey;
				// reset cashed settings of former project
				conDecAPI.knowledgeTypes = [];
				conDecGroupingAPI.decisionGroups = [];

				conDecFiltering.fillDropdownMenus(viewIdentifier);
				conDecFiltering.fillMinimumCoverageAndMaximumLinkDistance(viewIdentifier, projectKey);
			}
		}

		clearListener("project-dropdown-" + viewIdentifier);

		document.getElementById("project-dropdown-" + viewIdentifier).addEventListener("change", onSelectProject);
	}

	/**
	 * Clears all listeners from a html element.
	 * Used to prevent multiple listeners doing the same thing
	 * being added to an element every time the edit view is opened.
	 *
	 * @param elementId the id of the element from which the listeners 
	 *                  should be cleared
	 */
	function clearListener(elementId) {
		var element = document.getElementById(elementId);
		if (element) {
			element.replaceWith(element.cloneNode(true));
		}
	}

	/**
	 * Shows the specified html element.
	 * All other HTML elements are hidden.
	 *
	 * @param elementId the id of the element (without viewIdentifier) 
	 *                  that should be shown
	 * @param viewIdentifier identifies the HTML elements of the dashboard
	 */
	function showDashboardSection(elementId, viewIdentifier) {
		var hiddenClass = "hidden";
		document.getElementById("condec-dashboard-config-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById("condec-dashboard-contents-container-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById("condec-dashboard-contents-data-error-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById("condec-dashboard-no-project-" + viewIdentifier).classList.add(hiddenClass);
		document.getElementById(elementId + viewIdentifier).classList.remove(hiddenClass);
	}

	/**
	 * Set the options of the html filter elements from the preferences.
	 *
	 * @param preferences stores the filter options
	 * @param viewIdentifier identifies the HTML elements of the dashboard
	 */
	function setPreferences(filterSettings, viewIdentifier) {
		var projectKey = filterSettings["projectKey"];
		if (projectKey) {
			conDecAPI.projectKey = projectKey;
		}
		conDecFiltering.fillFilterElementsFromSettings(viewIdentifier, filterSettings);
	}

	ConDecDashboard.prototype.initializeChart = function(divId, title, subtitle, dataMap) {
		this.initializeChartWithColorPalette(divId, title, subtitle, dataMap, null);
	};

	/* used by branch dashboard item condec.rationale.coverage.dashboard.js */
	ConDecDashboard.prototype.initializeChartWithColorPalette = function(divId, title, subtitle, dataMap, colorPalette) {
		isIssueData = true;
		initializeChartForSources(divId, title, subtitle, new Map(Object.entries(dataMap)), colorPalette);
	};

	/* used by branch dashboard item condec.git.branches.dashboard.js */
	ConDecDashboard.prototype.initializeChartForBranchSource = function(divId, title, subtitle, dataMap) {
		isIssueData = false;
		initializeChartForSources(divId, title, subtitle, dataMap);
	};

	ConDecDashboard.prototype.createPieChartWithListOfElements = function(dataMap, divId, title, viewIdentifier, colorPalette) {
		var pieChart = this.createPieChartWithList(dataMap, divId, title, colorPalette);
		pieChart.on('click', function(param) {
			if (typeof param.seriesIndex != 'undefined' && param.data.list) {
				var dialogContent = conDecDashboard.initDialog(viewIdentifier);
				for (element of param.data.list) {
					var link = createLinkToElement(element);
					dialogContent.appendChild(link);
				}
			}
		});
	};

	ConDecDashboard.prototype.initDialog = function(viewIdentifier) {
		var navigationDialog = document.getElementById("navigate-dialog-" + viewIdentifier);
		AJS.dialog2(navigationDialog).show();
		var dialogContent = document.getElementById("navigate-dialog-content-" + viewIdentifier);
		dialogContent.innerHTML = "";
		return dialogContent;
	}

	function createLinkToElement(element) {
		var link = document.createElement("a");
		link.classList = "navigationLink";
		link.innerText = element.type + ": " + element.summary;
		link.title = element.key;
		link.href = element.url;
		link.target = "_blank";
		return link;
	}

	ConDecDashboard.prototype.createPieChartWithList = function(dataMap, divId, title, colorPalette) {
		var data = [];
		for (const [category, list] of dataMap.entries()) {
			entry = { "name": category, "value": list.length, "list": list }
			data.push(entry);
		}
		return createPieChart(divId, title, Array.from(dataMap.keys()), data, colorPalette);
	};

	ConDecDashboard.prototype.createSimplePieChart = function(dataMap, divId, title, colorPalette) {
		var data = [];
		for (const [category, value] of dataMap.entries()) {
			entry = { "name": category, "value": value }
			data.push(entry);
		}
		return createPieChart(divId, title, Array.from(dataMap.keys()), data, colorPalette);
	};

	function createPieChart(divId, title, keys, data, colorPalette) {
		var domElement = document.getElementById(divId);
		var chart = echarts.init(domElement);
		chart.setOption(getOptionsForPieChart("", title, keys, data, colorPalette));
		return chart;
	}

	ConDecDashboard.prototype.createBoxPlotWithListOfElements = function(divId, title, dataMap, viewIdentifier) {
		var boxplot = this.createBoxPlot(divId, title, dataMap);
		boxplot.on('click', function(param) {
			if (typeof param.seriesIndex != 'undefined') {
				var dialogContent = conDecDashboard.initDialog(viewIdentifier);
				var selectedValue = param.value[1];
				var elementsForValue = dataMap.get(String(selectedValue));
				if (!elementsForValue) {
					return;
				}
				for (element of elementsForValue) {
					var link = createLinkToElement(element);
					dialogContent.appendChild(link);
				}
			}
		});
		return boxplot;
	};

	ConDecDashboard.prototype.createBoxPlot = function(divId, title, dataMap) {
		var domElement = document.getElementById(divId);
		var boxplot = echarts.init(domElement);

		var values = [];
		for (const [number, elements] of dataMap.entries()) {
			for (element of elements) {
				values.push(number);
			}
		}
		values = values.map(Number);

		var data = [];
		for (const [category, list] of dataMap.entries()) {
			entry = { "name": category, "value": list.length, "list": list }
			data.push(entry);
		}

		boxplot.setOption(getOptionsForBoxplot("", title, "", new Array(values)));
		return boxplot;
	};

	function getOptionsForBoxplot(name, xLabel, yLabel, data) {
		console.log(data);
		return {
			title: [{
				text: name,
				left: "center",
			},],
			dataset: [
				{ source: data },
				{
					transform: {
						type: "boxplot"
					}
				},
				{
					fromDatasetIndex: 1,
					fromTransformResult: 1
				}
			],
			grid: {
				left: "15%",
				right: "10%",
				bottom: "15%"
			},
			xAxis: {
				type: "category",
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
			legend: {
				selected: { outlier: true }
			},
			series: [{
				name: "boxplot",
				type: "boxplot",
				datasetIndex: 1
			}, {
				name: "outlier",
				type: "scatter",
				datasetIndex: 2
			}]
		};
	}

	function getOptionsForPieChart(title, subtitle, dataKeys, dataMap, colorPalette) {
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

	global.conDecDashboard = new ConDecDashboard();
})(window);
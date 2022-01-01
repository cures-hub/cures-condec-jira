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
	var detailsOverlayClickHandlersSet = false;

	var issueKeyParser = /([^a-z]*)([a-z]+)-([0-9]+).*/gi

	var isIssueData = true;

	const CHART_RICH_PIE = "piechartRich";
	const CHART_SIMPLE_PIE = "piechartInteger";
	const CHART_BOXPLOT = "boxplot";
	const DEC_STRING_SEPARATOR = " ";

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
		filterSettings.sourceKnowledgeTypes = toList(filterSettings["sourceKnowledgeTypes"]);
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

	ConDecDashboard.prototype.createPieChart = function(divId, title, keys, data, colorPalette) {
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
		chart.setOption(getOptionsForPieChart("", title, keys, data, colorPalette));

		if (!chart) {
			console.error("could not setup chart for element " + divId);
		}
		// add click handler for chart data (in canvas)
		chart.on('click', function(param) {
			if (typeof param.seriesIndex != 'undefined') {
				// param.dataIndex
				// param.data
				console.log(param);
				var navigationDialog = document.getElementById("navigate-dialog");
				AJS.dialog2(navigationDialog).show();

				var dialogContent = document.getElementById("navigate-dialog-content");
				dialogContent.innerHTML = "";
				for (element of param.data.elements) {
					console.log(element);
					var link = document.createElement("a");
					link.classList = "navigationLink";
					link.innerText = element.key;
					link.href = element.url;
					link.target = "_blank";
					dialogContent.appendChild(link);
				}
			}
		});
		return chart;
	};

	function initializeChartForSources(divId, title, subtitle, dataMap, colorPalette) {
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
			chart = initializeDivWithPieChartData(chart, title, subtitle, dataMap, colorPalette, true);
		} else if (divId.startsWith(CHART_SIMPLE_PIE)) {
			chart = initializeDivWithPieChartData(chart, title, subtitle, dataMap, colorPalette, false);
		} else if (divId.startsWith(CHART_BOXPLOT)) {
			chart = initializeDivWithBoxPlotFromMap(chart, title, subtitle, dataMap);
		} else {
			chart = null;
		}

		if (!chart) {
			console.error("could not setup chart for element " + divId);
		}
		// add click handler for chart data (in canvas)
		chart.on('click', echartDataClicked);
	}

	function initializeDivWithBoxPlotFromMap(boxplot, title, xAxis, dataMap) {
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
	}

	function initializeDivWithPieChartData(pieChart, title, subtitle, objectsMap, colorPalette, hasRichData) {
		var data = [];
		var source = [];

		var dataAsArray = Array.from(objectsMap.keys());
		for (var i = dataAsArray.length - 1; i >= 0; i--) {
			var entry = {};
			entry["name"] = dataAsArray[i];
			var value = objectsMap.get(entry["name"]);

			if (hasRichData && (typeof value === 'string' || value instanceof String)) {
				entry["value"] = value.split(' ').reduce(sourceCounter, 0);
			} else if (!hasRichData && (typeof value === 'string' || value instanceof String)) {
				entry["value"] = Number(value);
			} else {
				entry["value"] = value;
			}
			data.push(entry);
			source.push(value);
		}

		pieChart.setOption(getOptionsForPieChart(title, subtitle, dataAsArray, data, colorPalette));
		if (hasRichData) {
			pieChart.groupedConDecData = source;
		}
		return pieChart;
	}

	var sourceCounter = function(accumulator, currentValue) {
		if (currentValue.trim() === "") {
			return accumulator + 0;
		}
		return accumulator + 1;
	}

	function navigateToElement(elementName) {
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
			var newWindow = window.open(targetBaseUrl + '/browse/' + issueKey + '#menu-item-git', '_blank');
			var script = document.createElement('script');
			function openTab() {
				AJS.tabs.change(AJS.$("a[href=#git-tab]"));
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

	function showClickedSource(chart, dataIndexClicked, dataClicked) {
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
			function(elem) {
				return this.includes(Number(elem[1]));
			}, trimmedData);

		return involvedElements.map(
			function(val) { // value is a 2-element array
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
				data: data.boxData
			}, {
				name: "outlier",
				type: "scatter",
				data: data.outliers
			}]
		};
	}

	function getOptionsForPieChart(title, subtitle, dataKeys, dataMap, colorPalette) {
		console.log(dataKeys);
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
			listArray = listArray.map(function(e) {
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

	function clickDecKnowledgeElementsOverlay(event) {
		if (event.target === event.currentTarget) {
			event.currentTarget.classList.add("hidden");
		}
	}

	function echartDataClicked(param) {
		if (typeof param.seriesIndex != 'undefined') {
			showClickedSource(this, param.dataIndex, param.data);
		}
	}

	function clickDecKnowledgeElementsInOverlay(event) {
		if (event.target.nodeName.toLowerCase() === "p") {
			navigateToElement(event.target.innerText);
		}
	}

	global.conDecDashboard = new ConDecDashboard();
})(window);
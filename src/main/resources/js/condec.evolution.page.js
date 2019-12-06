(function(global) {

	/* private vars */
	var conDecObservable = null;
	var conDecAPI = null;
	var conDecVis = null;
	var networkRight = null;
	var networkLeft = null;
	var timeline = null;
	var completeKnowledgeStatus = null;

	var ConDecEvolutionPage = function ConDecEvolutionPage() {
	};

	ConDecEvolutionPage.prototype.init = function(_conDecAPI, _conDecObservable, _conDecVis) {
		console.log("ConDecEvolutionPage init");
		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)) {
			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;
			conDecVis = _conDecVis;
			completeKnowledgeStatus = _conDecAPI.knowledgeStatus;
			completeKnowledgeStatus = completeKnowledgeStatus.concat(_conDecAPI.issueStatus);
			//conDecObservable.subscribe(this);
			return true;
		}
		return false;
	};

	ConDecEvolutionPage.prototype.buildTimeLine = function buildTimeLine() {
		console.log("ConDec build timeline");
		var issueTypeDropdown = document.getElementById("chronologie-dropdown");
		var issueStatusDropdown = document.getElementById("chronologie-status-dropdown");
		initIssueTypeSelect(issueTypeDropdown);
		initIssueStatusSelect(issueStatusDropdown);
		conDecAPI.getEvolutionData("", -1, -1, conDecAPI.knowledgeTypes, completeKnowledgeStatus, function(
				evolutionData) {
			var container = document.getElementById('evolution-timeline');
			var data = evolutionData.dataSet;
			var item = new vis.DataSet(data);
			var groups = evolutionData.groupSet;
			var date = new Date();
			document.getElementById("end-date-picker-time").value = date.toISOString().substr(0, 10);
			var endTime = date.toDateString();
			date.setDate(date.getDate() - 7);
			document.getElementById("start-date-picker-time").value = date.toISOString().substr(0, 10);
			var startTime = date.toDateString();
			var options = {
				locale : 'de',
				start : startTime,
				end : endTime
			};
			timeline = new vis.Timeline(container, item, options);
			timeline.setGroups(groups);
			timeline.on('contextmenu', function(properties) {
				properties.event.preventDefault();
				var nodeId = properties.item;
				var documentationLocation = timeline.itemsData._data[nodeId].documentationLocation;
				conDecContextMenu.createContextMenu(nodeId, documentationLocation, properties.event, "vis");
			});
		});
		addOnClickEventToFilterTimeLineButton();
	};

	ConDecEvolutionPage.prototype.buildCompare = function buildCompare() {
		console.log("ConDec build compare view");
		var issueTypeDropdown = document.getElementById("compare-dropdown");
		var issueStatusDropdown = document.getElementById("compare-status-dropdown");
		initIssueTypeSelect(issueTypeDropdown);
		initIssueStatusSelect(issueStatusDropdown);

		var date = new Date();
		var today = date.getFullYear() + '-' + date.getMonth() + '-' + date.getDay();
		document.getElementById("end-data-picker-compare-left").value = date.toISOString().substr(0, 10);
		document.getElementById("end-data-picker-compare-right").value = date.toISOString().substr(0, 10);
		var endTime = date.getTime();
		date.setDate(date.getDate() - 7);
		var startTime = date.getTime();
		document.getElementById("start-data-picker-compare-left").value = date.toISOString().substr(0, 10);
		conDecAPI.getCompareVis(startTime, endTime, "", conDecAPI.knowledgeTypes, completeKnowledgeStatus,
				function(visData) {
					var containerLeft = document.getElementById('left-network');
					var dataLeft = {
						nodes : visData.nodes,
						edges : visData.edges
					};
					var options = getOptions();
					networkLeft = new vis.Network(containerLeft, dataLeft, options);
					networkLeft.setSize("100%", "500px");
					networkLeft.on("oncontext", function(params) {
						conDecVis.addContextMenu(params, networkLeft);
					});
					networkLeft.on("selectNode", function(params) {
						networkRight.focus(params.nodes[0]);
						networkLeft.focus(params.nodes[0]);
					});

				});
		date.setDate(date.getDate() - 7);
		startTime = date.getTime();
		document.getElementById("start-data-picker-compare-right").value = date.toISOString().substr(0, 10);
		conDecAPI.getCompareVis(startTime, endTime, "", conDecAPI.knowledgeTypes, completeKnowledgeStatus,
				function(visData) {
					var containerRight = document.getElementById('right-network');
					var dataRight = {
						nodes : visData.nodes,
						edges : visData.edges
					};
					var options = getOptions();
					networkRight = new vis.Network(containerRight, dataRight, options);
					networkRight.setSize("100%", "500px");
					networkRight.on("oncontext", function(params) {
						conDecVis.addContextMenu(params, networkRight);
					});

					networkRight.on("selectNode", function(params) {
						networkRight.focus(params.nodes[0]);
						networkLeft.focus(params.nodes[0]);
					});
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

	function initIssueTypeSelect(issueTypeDropdown) {
		initDropdown(issueTypeDropdown, conDecAPI.knowledgeTypes);
	}

	function initIssueStatusSelect(issueStatusDropdown) {
		initDropdown(issueStatusDropdown, completeKnowledgeStatus);
	}
	
	function initDropdown(dropdown, items) {
		for (var index = 0; index < items.length; index++) {
			dropdown.insertAdjacentHTML("beforeend", "<aui-item-checkbox interactive " + "checked" + ">"
					+ items[index] + "</aui-item-checkbox>");
		}
	}

	// Compute filter and select new elements
	function addOnClickEventToFilterCompareButton() {
		console.log("ConDecJiraEvolutionPage addOnClickEventToFilterButtonCompare");

		var filterButton = document.getElementById("filter-button-compare");

		filterButton.addEventListener("click", function(event) {
			var firstDateLeft = -1;
			var secondDateLeft = -1;
			var firstDateRight = -1;
			var secondDateRight = -1;
			var knowledgeTypes = getSelectedItems(AJS.$('#compare-dropdown'));
			var issueStatus = getSelectedItems(AJS.$('#compare-status-dropdown'));
			if (!isNaN(document.getElementById("start-data-picker-compare-left").valueAsNumber)) {
				firstDateLeft = document.getElementById("start-data-picker-compare-left").valueAsNumber;
			}
			if (!isNaN(document.getElementById("end-data-picker-compare-left").valueAsNumber)) {
				secondDateLeft = document.getElementById("end-data-picker-compare-left").valueAsNumber;
			}
			if (!isNaN(document.getElementById("start-data-picker-compare-right").valueAsNumber)) {
				firstDateRight = document.getElementById("start-data-picker-compare-right").valueAsNumber;
			}
			if (!isNaN(document.getElementById("end-data-picker-compare-right").valueAsNumber)) {
				secondDateRight = document.getElementById("end-data-picker-compare-right").valueAsNumber;
			}
			var searchString = "";
			searchString = document.getElementById("compare-search-input").value;
			conDecAPI.getCompareVis(firstDateLeft, secondDateLeft, searchString, knowledgeTypes, issueStatus, function(
					visDataLeft) {
				var dateLeft = {
					nodes : visDataLeft.nodes,
					edges : visDataLeft.edges
				};
				networkLeft.setData(dateLeft);
			});
			conDecAPI.getCompareVis(firstDateRight, secondDateRight, searchString, knowledgeTypes, issueStatus, function(
					visDataRight) {
				var dateRight = {
					nodes : visDataRight.nodes,
					edges : visDataRight.edges
				};
				networkRight.setData(dateRight);
			});
		});
	}

	// Compute filter and select new elements in the TimeLine View
	function addOnClickEventToFilterTimeLineButton() {
		console.log("ConDecJiraEvolutionPage addOnClickEventToFilterButtonTimeLine");
		var filterButton = document.getElementById("filter-button-time");

		filterButton.addEventListener("click", function(event) {
			var firstDate = -1;
			var secondDate = -1;
			var knowledgeTypes = getSelectedItems(AJS.$('#chronologie-dropdown'));
			var issueStatus = getSelectedItems(AJS.$('#chronologie-status-dropdown'));
			if (!isNaN(document.getElementById("start-date-picker-time").valueAsNumber)) {
				firstDate = document.getElementById("start-date-picker-time").valueAsNumber;
			}
			if (!isNaN(document.getElementById("end-date-picker-time").valueAsNumber)) {
				secondDate = document.getElementById("end-date-picker-time").valueAsNumber;
			}
			var searchString = document.getElementById("time-search-input").value;
			
			conDecAPI.getEvolutionData(searchString, firstDate, secondDate, knowledgeTypes, issueStatus, function(visData) {
				var data = visData.dataSet;
				var groups = visData.groupSet;
				var item = new vis.DataSet(data);
				timeline.setItems(item);
				timeline.setGroups(groups);
				timeline.redraw();
			});
		});
	}
	
	function getSelectedItems(dropdown) {
		var selectedItems = [];
		for (var i = 0; i < dropdown.children().size(); i++) {
			if (typeof dropdown.children().eq(i).attr('checked') !== typeof undefined
					&& dropdown.children().eq(i).attr('checked') !== false) {
				selectedItems.push(dropdown.children().eq(i).text());
			}
		}
		return selectedItems;
	}

	function getOptions() {
		return {
			layout : {
				randomSeed : 1,
				hierarchical : {
					direction : "UD"
				}
			},
			physics : {
				enabled : false
			},
			interaction : {
				keyboard : true
			},
			nodes : {
				shape : "box",
				widthConstraint : 120,
				color : {
					background : 'rgba(255, 255, 255,1)',
					border : 'rgba(0,0,0,1)',
					highlight : {
						background : 'rgba(255,255,255,1)',
						border : 'rgba(0,0,0,1)'
					}
				},
				font : {
					multi : false
				},
				shapeProperties : {
					interpolation : false
				}
			},
			edges : {
				arrows : "to"
			},
			groups : {
				// Setting colors for Decision Knowledge Elements
				decision : {
					color : {
						background : 'rgba(252,227,190,1)',
						highlight : {
							background : 'rgba(252,227,190,1)'
						}
					}
				},
				issue : {
					color : {
						background : 'rgba(255, 255, 204,1)',
						highlight : {
							background : 'rgba(255,255,204,1)'
						}
					}
				},
				alternative : {
					color : {
						background : 'rgba(252,227,190,1',
						highlight : {
							background : 'rgba(252,227,190,1)'
						}
					}
				},
				pro : {
					color : {
						background : 'rgba(222, 250, 222,1)',
						highlight : {
							background : 'rgba(222,250,222,1)'
						}
					}
				},
				con : {
					color : {
						background : 'rgba(255, 231, 231,1)',
						highlight : {
							background : 'rgba(255,231,231,1)'
						}
					}
				},
				argument : {
					color : {
						background : 'rgba(255, 255, 255,1)',
						highlight : {
							background : 'rgba(255,255,255,1)'
						}
					}
				},
				constraint : {
					color : {
						background : 'rgba(255, 255, 255,1)',
						highlight : {
							background : 'rgba(255,255,255,1)'
						}
					}
				},
				assumption : {
					color : {
						background : 'rgba(255, 255, 255,1)',
						highlight : {
							background : 'rgba(255,255,255,1)'
						}
					}
				},
				implication : {
					color : {
						background : 'rgba(255, 255, 255,1)',
						highlight : {
							background : 'rgba(255,255,255,1)'
						}
					}
				},
				context : {
					color : {
						background : 'rgba(255, 255, 221,1)',
						highlight : {
							background : 'rgba(255,255,221,1)'
						}
					}
				},
				problem : {
					color : {
						background : 'rgba(255, 255, 204,1)',
						highlight : {
							background : 'rgba(255,255,204,1)'
						}
					}
				},
				goal : {
					color : {
						background : 'rgba(255, 255, 255,1)',
						highlight : {
							background : 'rgba(255,255,255,1)'
						}
					}
				},
				solution : {
					color : {
						background : 'rgba(255, 246, 232,1)',
						highlight : {
							background : 'rgba(255,246,232,1)'
						}
					}
				},
				claim : {
					color : {
						background : 'rgba(255, 255, 255,1)',
						highlight : {
							background : 'rgba(255,255,255,1)'
						}
					}
				},
				rationale : {
					color : {
						background : 'rgba(255, 255, 221,1)',
						highlight : {
							background : 'rgba(255,255,221,1)'
						}
					}
				},
				question : {
					color : {
						background : 'rgba(255, 255, 255,1)',
						highlight : {
							background : 'rgba(255,255,255,1)'
						}
					}
				},
				assessment : {
					color : {
						background : 'rgba(255, 255, 255,1)',
						highlight : {
							background : 'rgba(255,255,255,1)'
						}
					}
				},
				collapsed : {
					shape : "dot",
					size : 5,
					color : {
						background : 'rgba(0,0,0,1)'
					}
				}
			},
			physics : {
				enabled : false
			}
		};
	}

	global.conDecEvolutionPage = new ConDecEvolutionPage();
})(window);
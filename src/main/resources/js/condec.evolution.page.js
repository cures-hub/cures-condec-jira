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
        var issueTypeDropdown = document.getElementById("chronologie-dropdown");
        var issueStatusDropdown = document.getElementById("chronologie-status-dropdown");
        initIssueTypeSelectCompare(issueTypeDropdown);
        initIssueStatusSelect(issueStatusDropdown);
		conDecAPI.getEvolutionData("", -1, -1 ,conDecAPI.extendedKnowledgeTypes,
            conDecAPI.knowledgeStatus, function(evolutionData) {
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
        var issueTypeDropdown = document.getElementById("compare-dropdown");
        var issueStatusDropdown = document.getElementById("compare-status-dropdown");
        initIssueTypeSelectCompare(issueTypeDropdown);
        initIssueStatusSelect(issueStatusDropdown);
        conDecAPI.getCompareVis(-1, -1,"",conDecAPI.extendedKnowledgeTypes,
            conDecAPI.knowledgeStatus, function (visData) {
            var containerLeft = document.getElementById('left-network');
            var dataLeft = {
                nodes : visData.nodes,
                edges : visData.edges
            };
            var options = getOptions();
            var networkLeft = new vis.Network(containerLeft, dataLeft, options);

        });
        conDecAPI.getCompareVis(-1, -1,"",conDecAPI.extendedKnowledgeTypes,
            conDecAPI.knowledgeStatus, function (visData) {
            var containerRight = document.getElementById('right-network');
            var dataRight = {
                nodes : visData.nodes,
                edges : visData.edges
            };
            var options = getOptions();
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

    function initIssueTypeSelectCompare(issueTypeDropdown) {
        var issueType = conDecAPI.extendedKnowledgeTypes;
        for (var index = 0; index < issueType.length; index++) {
            issueTypeDropdown.insertAdjacentHTML("beforeend", "<aui-item-checkbox interactive " + "checked" + ">"
                + issueType[index] + "</aui-item-checkbox>");
        }
    }

    function initIssueStatusSelect(issueStatusDropdown) {
	    var issueStatus= conDecAPI.knowledgeStatus;
        for (var index = 0; index < issueStatus.length; index++) {
            issueStatusDropdown.insertAdjacentHTML("beforeend", "<aui-item-checkbox interactive " + "checked" + ">"
                + issueStatus[index] + "</aui-item-checkbox>");
        }
    }

	//Compute filter and select new elements
    function addOnClickEventToFilterCompareButton() {
        console.log("ConDecJiraEvolutionPage addOnClickEventToFilterButtonCompare");

        var filterButton = document.getElementById("filter-button-compare");

        filterButton.addEventListener("click", function(event) {
            var firstDate = -1;
            var secondDate = -1;
            var issueTypes = [];
            var issueStatus = [];
            if (!isNaN(document.getElementById("start-data-picker-compare").valueAsNumber)) {
                firstDate = document.getElementById("start-data-picker-compare").valueAsNumber;
            }
            if (!isNaN(document.getElementById("end-data-picker-compare").valueAsNumber)) {
                secondDate = document.getElementById("end-data-picker-compare").valueAsNumber;
            }
            var searchString = "";
            searchString = document.getElementById("compare-search-input").value;
            for (var i = 0; i < AJS.$('#compare-dropdown').children().size(); i++) {
                if (typeof AJS.$('#compare-dropdown').children().eq(i).attr('checked') !== typeof undefined
                    && AJS.$('#compare-dropdown').children().eq(i).attr('checked') !== false) {
                    issueTypes.push(AJS.$('#compare-dropdown').children().eq(i).text());
                }
            }
            for (var j = 0; j< AJS.$('#compare-status-dropdown').children().size(); j++) {
                if (typeof AJS.$('#compare-status-dropdown').children().eq(j).attr('checked') !== typeof undefined
                    && AJS.$('#compare-status-dropdown').children().eq(j).attr('checked') !== false) {
                    issueStatus.push(AJS.$('#compare-status-dropdown').children().eq(j).text());
                }
            }
            conDecAPI.getCompareVis(firstDate, secondDate,searchString, issueTypes, issueStatus, function(visData) {
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
            var issueTypes = [];
            var issueStatus = [];
            if (!isNaN(document.getElementById("start-date-picker-time").valueAsNumber)) {
                firstDate = document.getElementById("start-date-picker-time").valueAsNumber;
            }
            if (!isNaN(document.getElementById("end-date-picker-time").valueAsNumber)) {
                secondDate = document.getElementById("end-date-picker-time").valueAsNumber;
            }
            var searchString = document.getElementById("time-search-input").value;
            for (var i = 0; i < AJS.$('#chronologie-dropdown').children().size(); i++) {
                if (typeof AJS.$('#chronologie-dropdown').children().eq(i).attr('checked') !== typeof undefined
                    && AJS.$('#chronologie-dropdown').children().eq(i).attr('checked') !== false) {
                    issueTypes.push(AJS.$('#chronologie-dropdown').children().eq(i).text());
                }
            }
            for (var j = 0; j< AJS.$('#chronologie-status-dropdown').children().size(); j++) {
                if (typeof AJS.$('#chronologie-status-dropdown').children().eq(j).attr('checked') !== typeof undefined
                    && AJS.$('#chronologie-status-dropdown').children().eq(j).attr('checked') !== false) {
                    issueStatus.push(AJS.$('#chronologie-status-dropdown').children().eq(j).text());
                }
            }
            conDecAPI.getEvolutionData(searchString, firstDate, secondDate,  issueTypes,issueStatus, function (visData) {
                var data = visData;
                var item = new vis.DataSet(data);
                timeline.setItems(item);
                timeline.redraw();
            });
        });
    }

    function getOptions(){
	    return {
            layout: {
                randomSeed: 2,
                improvedLayout: true,
                hierarchical: {
                    enabled: false,
                    levelSeparation: 150,
                    nodeSpacing: 100,
                    treeSpacing: 200
                }
            },
            nodes: {
                shape: "box",
                widthConstraint: 120,
                color: {
                    background: 'rgba(255, 255, 255,1)',
                    border: 'rgba(0,0,0,1)',
                    highlight: {
                        background: 'rgba(255,255,255,1)',
                        border: 'rgba(0,0,0,1)'
                    }
                },
                font: {
                    multi: false
                },
                shapeProperties: {
                    interpolation: false
                }
            },
            edges: {
                arrows: "to"
            },
	        groups: {
                // Setting colors for Decision Knowledge Elements
                decision: {
                    color: {
                        background: 'rgba(252,227,190,1)',
                        highlight: {
                            background: 'rgba(252,227,190,1)'
                        }
                    }
                },
                issue: {
                    color: {
                        background: 'rgba(255, 255, 204,1)',
                        highlight: {
                            background: 'rgba(255,255,204,1)'
                        }
                    }
                },
                alternative: {
                    color: {
                        background: 'rgba(252,227,190,1',
                        highlight: {
                            background: 'rgba(252,227,190,1)'
                        }
                    }
                },
                pro: {
                    color: {
                        background: 'rgba(222, 250, 222,1)',
                        highlight: {
                            background: 'rgba(222,250,222,1)'
                        }
                    }
                },
                con: {
                    color: {
                        background: 'rgba(255, 231, 231,1)',
                        highlight: {
                            background: 'rgba(255,231,231,1)'
                        }
                    }
                },
                argument: {
                    color: {
                        background: 'rgba(255, 255, 255,1)',
                        highlight: {
                            background: 'rgba(255,255,255,1)'
                        }
                    }
                },
                constraint: {
                    color: {
                        background: 'rgba(255, 255, 255,1)',
                        highlight: {
                            background: 'rgba(255,255,255,1)'
                        }
                    }
                },
                assumption: {
                    color: {
                        background: 'rgba(255, 255, 255,1)',
                        highlight: {
                            background: 'rgba(255,255,255,1)'
                        }
                    }
                },
                implication: {
                    color: {
                        background: 'rgba(255, 255, 255,1)',
                        highlight: {
                            background: 'rgba(255,255,255,1)'
                        }
                    }
                },
                context: {
                    color: {
                        background: 'rgba(255, 255, 221,1)',
                        highlight: {
                            background: 'rgba(255,255,221,1)'
                        }
                    }
                },
                problem: {
                    color: {
                        background: 'rgba(255, 255, 204,1)',
                        highlight: {
                            background: 'rgba(255,255,204,1)'
                        }
                    }
                },
                goal: {
                    color: {
                        background: 'rgba(255, 255, 255,1)',
                        highlight: {
                            background: 'rgba(255,255,255,1)'
                        }
                    }
                },
                solution: {
                    color: {
                        background: 'rgba(255, 246, 232,1)',
                        highlight: {
                            background: 'rgba(255,246,232,1)'
                        }
                    }
                },
                claim: {
                    color: {
                        background: 'rgba(255, 255, 255,1)',
                        highlight: {
                            background: 'rgba(255,255,255,1)'
                        }
                    }
                },
                rationale: {
                    color: {
                        background: 'rgba(255, 255, 221,1)',
                        highlight: {
                            background: 'rgba(255,255,221,1)'
                        }
                    }
                },
                question: {
                    color: {
                        background: 'rgba(255, 255, 255,1)',
                        highlight: {
                            background: 'rgba(255,255,255,1)'
                        }
                    }
                },
                assessment: {
                    color: {
                        background: 'rgba(255, 255, 255,1)',
                        highlight: {
                            background: 'rgba(255,255,255,1)'
                        }
                    }
                },
                collapsed: {
                    shape: "dot",
                    size: 5,
                    color: {
                        background: 'rgba(0,0,0,1)'
                    }
                }
            },
            physics: {
                enabled: false
            }
        };
    }

	global.conDecEvolutionPage = new ConDecEvolutionPage();
})(window);
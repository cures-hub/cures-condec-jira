(function(global) {

	var ConDecVis = function() {
	};
	
	/*
	 * external references: condec.jira.issue.module
	 */
	ConDecVis.prototype.buildVis = function (filterSettings) {
		console.log("conDecVis buildVis");
		conDecAPI.getVis(filterSettings, function(visData) {
			build(visData.nodes, visData.edges, visData.rootElementId);
		});
	};

	function build (theNodes, theEdges, rootElementId) {
		console.log("ConDecVis build");
		var nodes = new vis.DataSet(theNodes);
		var edges = new vis.DataSet(theEdges);
		var container = document.getElementById('vis-container');
		nodes.update([ {
		    id : rootElementId,
		    borderWidth : 3
		} ]);
		nodes.update([ {
		    id : rootElementId,
		    shadow : {
			    enabled : true
		    }
		} ]);

		var data = {
		    nodes : nodes,
		    edges : edges
		};
		var options = conDecVis.getVisOptions(data);

		var network = new vis.Network(container, data, options);
		network.setSize("100%", "500px");

		network.on("oncontext", function(params) {
			conDecVis.addContextMenu(params, network);
		});

		network.on("hold", function(params) {
			conDecVis.holdFunction(params, network);
		});
		network.on("selectNode", function(params) {
			conDecVis.selectNode(params, network);
		});
		return network;
	}

	ConDecVis.prototype.addContextMenu = function (params, network) {
		params.event.preventDefault();
		var nodeIndices = network.body.nodeIndices;
		var clickedNodeId;
		for (var i = 0; i < nodeIndices.length; i++) {
			var nodeId = nodeIndices[i];
			var boundingBox = network.getBoundingBox(nodeId);
			if (boundingBox.left <= params.pointer.canvas.x && params.pointer.canvas.x <= boundingBox.right
			        && boundingBox.top <= params.pointer.canvas.y && params.pointer.canvas.y <= boundingBox.bottom) {
				clickedNodeId = nodeId;
			}
		}
		if (clickedNodeId === undefined) {
			return;
		}
		conDecContextMenu.createContextMenu(clickedNodeId.toString().slice(0, -2),
		        getDocumentationLocationFromId(clickedNodeId), params.event, "vis-container");
	};

	ConDecVis.prototype.getVisOptions = function(visData) {
        var dataset = {
                nodes: data.nodes,
                edges: data.edges
            };
        
		return {
		    clickToUse : false,
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

		    autoResize : false,

		    layout : {
		        improvedLayout : true,
		        hierarchical : {
		            enabled : true,
		            levelSeparation : 140,
		            nodeSpacing : 250,
		            treeSpacing : 0,
		            blockShifting : false,
		            edgeMinimization : false,
		            parentCentralization : true,
		            direction : 'UD', // UD, DU, LR, RL
		            sortMethod : 'directed' // hubsize, directed
		        }

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

		    manipulation : {
		        enabled : true,
		        editEdge : false,
		        addNode : false,
		        addEdge : function(data, callback) {
			        conDecVis.addEdge(data);
		        },
		        deleteNode : function(data, callback) {
			        conDecVis.deleteNode(data);
		        },
		        deleteEdge : function(data, callback) {
			        conDecVis.deleteEdge(data, dataset);
		        }
		    },
		    physics : {
			    enabled : false
		    },
		    interaction : {
			    tooltipDelay : 600
		    }
		}
	};

	ConDecVis.prototype.holdFunction = function(params, network) {
		var nodeIndices = network.body.nodeIndices;
		var clickedNodeId;
		for (var i = 0; i < nodeIndices.length; i++) {
			var nodeId = nodeIndices[i];
			var boundingBox = network.getBoundingBox(nodeId);
			if (boundingBox.left <= params.pointer.canvas.x && params.pointer.canvas.x <= boundingBox.right
			        && boundingBox.top <= params.pointer.canvas.y && params.pointer.canvas.y <= boundingBox.bottom) {
				clickedNodeId = nodeId;
			}
		}
		if (clickedNodeId === undefined) {
			return;
		}
		params.event.preventDefault();
		conDecDialog.showEditDialog(clickedNodeId.toString().slice(0, -2),
		        getDocumentationLocationFromId(clickedNodeId));
	};

	ConDecVis.prototype.selectNode = function(params, network) {
		if (params.nodes.length === 1) {
			if (network.isCluster(params.nodes[0]) === true) {
				network.openCluster(params.nodes[0]);
			}
		}
	};

	// TODO Avoid data slicing, this is very hard to understand!
	ConDecVis.prototype.deleteNode = function(data) {
		conDecDialog.showDeleteDialog(data.nodes[0].slice(0, -2), data.nodes[0].substr(-1));
	};

	// TODO Avoid data slicing, this is very hard to understand!
	ConDecVis.prototype.addEdge = function(data) {
		if (data.from === data.to) {
			return;
		}
		conDecDialog.showLinkDialog(data.from.slice(0, -2),  data.from.substr(-1), data.to.slice(0, -2),
				data.to.substr(-1));
	};

	// TODO Avoid data slicing, this is very hard to understand!
	// TODO Deleting edges does not work in Jira issue view
	ConDecVis.prototype.deleteEdge = function(data, visData) {
		console.log(data);
		console.log(visData.edges);
		var allEdges = new vis.DataSet(visData.edges);
		console.log(allEdges);
		var edgeToBeDeleted = allEdges.get(data.edges[0]);
		console.log(edgeToBeDeleted);
		var idOfChild = edgeToBeDeleted.to.slice(0, -2);
		var idOfParent = edgeToBeDeleted.from.slice(0, -2);
		var documentationLocationOfChild = edgeToBeDeleted.to.substr(-1);
		var documentationLocationOfParent = edgeToBeDeleted.from.substr(-1);
		conDecDialog.showDeleteLinkDialog(idOfChild, documentationLocationOfChild, idOfParent, documentationLocationOfParent);
	};

	function getDocumentationLocationFromId(nodeId) {
		return nodeId.toString().substr(-1);
	}

	global.conDecVis = new ConDecVis();
})(window);
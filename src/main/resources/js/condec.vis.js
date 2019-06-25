(function(global) {

	var ConDecVis = function ConDecVis() {
	};

	function build(theNodes, theEdges, rootElementKey, nodeDistance) {
		var nodes = new vis.DataSet(theNodes);
		var edges = new vis.DataSet(theEdges);
		var container = document.getElementById('vis-container');
		nodes.update([ {
			id : rootElementKey,
			borderWidth : 3
		} ]);
		nodes.update([ {
			id : rootElementKey,
			shadow : {
				enabled : true
			}
		} ]);

		var data = {
			nodes : nodes,
			edges : edges
		};
		var options = {
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
					console.log('add edge', data);
					if (data.from !== data.to) {
						callback(data);

						conDecAPI.createLink(null, data.from.slice(0, -2), data.to.slice(0, -2), data.to.substr(-1),
								data.from.substr(-1), function() {
									conDecObservable.notify();
								});
					}
				},
				deleteNode : function(data, callback) {
					AJS.dialog2("#vis-delete-warning-dialog").show();
					AJS.$(document).on(
							"click",
							"#vis-delete-dialog-confirm",
							function(e) {
								e.preventDefault();
								console.log('delete node:', data.nodes[0].slice(0, -2));
								conDecAPI.deleteDecisionKnowledgeElement(data.nodes[0].slice(0, -2), data.nodes[0]
										.substr(-1), function() {
									conDecObservable.notify();
								});
								AJS.dialog2("#vis-delete-warning-dialog").hide();
							});
					AJS.dialog2("#vis-delete-warning-dialog").on("hide", function() {
						network.disableEditMode();
						network.enableEditMode();
					});
				},
				deleteEdge : function(data, callback) {
					AJS.dialog2("#vis-delete-warning-dialog").show();
					AJS.$(document).on(
							"click",
							"#vis-delete-dialog-confirm",
							function(e) {
								e.preventDefault();
								edgeToBedeleted = edges.get(data.edges[0]);
								console.log('delete link:', edgeToBedeleted.from, "->", edgeToBedeleted.to);
								conDecAPI.deleteLink(edgeToBedeleted.to.slice(0, -2),
										edgeToBedeleted.from.slice(0, -2), edgeToBedeleted.to.substr(-1),
										edgeToBedeleted.from.substr(-1), function() {
											conDecObservable.notify();
										});
								AJS.dialog2("#vis-delete-warning-dialog").hide();
							});
					AJS.dialog2("#vis-delete-warning-dialog").on("hide", function() {
						network.disableEditMode();
						network.enableEditMode();
					});
				}

			},
			physics : {
				enabled : false
			},
			interaction : {
				tooltipDelay : 600
			}
		};

		var network = new vis.Network(container, data, options);
		network.setSize("100%", "500px");

		network.on("oncontext",
				function(params) {
					console.log(params);
					params.event.preventDefault();
					var nodeIndices = network.body.nodeIndices;
					var clickedNodeId;
					console.log(network);
					for (var i = 0; i < nodeIndices.length; i++) {
						var nodeId = nodeIndices[i];
						var boundingBox = network.getBoundingBox(nodeId);
						if (boundingBox.left <= params.pointer.canvas.x && params.pointer.canvas.x <= boundingBox.right
								&& boundingBox.top <= params.pointer.canvas.y
								&& params.pointer.canvas.y <= boundingBox.bottom) {
							clickedNodeId = nodeId;
						}
					}
					if (clickedNodeId !== undefined && clickedNodeId !== 'distanceCluster') {
						console.log("ContextMenu for ID: " + clickedNodeId.toString().slice(0, -2) + " and location: "
								+ clickedNodeId.toString().substr(-1));
						conDecContextVis.createContextVis(clickedNodeId.toString().slice(0, -2),
								getDocumentationLocationFromId(clickedNodeId), params.event);
					}
				});

		network.on("hold",
				function(params) {
					var nodeIndices = network.body.nodeIndices;
					var clickedNodeId;
					for (var i = 0; i < nodeIndices.length; i++) {
						var nodeId = nodeIndices[i];
						var boundingBox = network.getBoundingBox(nodeId);
						if (boundingBox.left <= params.pointer.canvas.x && params.pointer.canvas.x <= boundingBox.right
								&& boundingBox.top <= params.pointer.canvas.y
								&& params.pointer.canvas.y <= boundingBox.bottom) {
							clickedNodeId = nodeId;
						}
					}
					if (clickedNodeId !== undefined && clickedNodeId !== 'distanceCluster') {
						params.event.preventDefault();
						conDecDialog.showEditDialog(clickedNodeId.toString().slice(0, -2),
								getDocumentationLocationFromId(clickedNodeId));
					}
				});
		network.on("selectNode", function(params) {
			if (params.nodes.length === 1) {
				if (network.isCluster(params.nodes[0]) === true) {
					network.openCluster(params.nodes[0]);
				}
			}
		});
		var clusterOptionsByData = {
			joinCondition : function(childOptions) {
				return ((childOptions.level <= 50 - nodeDistance) || (childOptions.level >= 50 + nodeDistance) || (childOptions.cid >= nodeDistance));
			},
			clusterNodeProperties : {
				allowSingleNodeCluster : false,
				id : 'distanceCluster',
				shape : 'ellipse',
				label : 'clusteredNodes',
				level : ((50 * 1) + (nodeDistance * 1))
			}

		};
		network.cluster(clusterOptionsByData);
		return network;
	}

	/*
	 * external references: condec.vis
	 */
	ConDecVis.prototype.buildVisFiltered = function buildVisFiltered(issueKey, search, nodeDistance, issueTypes,
			createdAfter, createdBefore, documentationLocation) {
		console.log("conDecVis buildVisFiltered");
		conDecAPI.getVisFiltered(issueKey, search, issueTypes, createdAfter, createdBefore, documentationLocation,
				function(visData) {
					build(visData.nodes, visData.edges, visData.rootElementKey, nodeDistance);
				});
	};
	
	/*
	 * external references: condec.vis
	 */
	ConDecVis.prototype.buildVis = function buildVis(elementKey, searchTerm) {
		console.log("conDecVis buildVis");
		conDecAPI.getVis(elementKey, searchTerm, function(visData) {
			var network = build(visData.nodes, visData.edges, visData.rootElementKey, 10);
			network.focus(visData.rootElementKey, {
				scale : 0.9
			});
		});
	};

	function getDocumentationLocationFromId(nodeId) {
		return nodeId.toString().substr(-1);
	}

	global.conDecVis = new ConDecVis();
})(window);
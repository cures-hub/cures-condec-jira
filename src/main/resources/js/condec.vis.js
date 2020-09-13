(function(global) {

	var ConDecVis = function() {
	};
	
	ConDecVis.prototype.initView = function (isJiraIssueView = false) {
		console.log("ConDecVis initView");       
		
		// Fill HTML elements for filter criteria and add on click listener
		if (isJiraIssueView) {
			conDecFiltering.fillFilterElements("graph");
			conDecFiltering.addOnClickEventToFilterButton("graph", function(filterSettings) {
				var issueKey = conDecAPI.getIssueKey();
				filterSettings["selectedElement"] = issueKey;
				conDecVis.buildVis(filterSettings, "vis-graph-container");
			});
		} else {
			conDecFiltering.fillFilterElements("graph", ["Decision"]);
			conDecFiltering.addOnClickEventToFilterButton("graph", function(filterSettings) {
				conDecVis.buildVis(filterSettings, "vis-graph-container");
			});
			document.getElementById("link-distance-input-label-graph").remove();
			document.getElementById("link-distance-input-graph").remove();
		}	

		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);
	        
		// Fill view
		this.updateView();
	};

	ConDecVis.prototype.updateView = function () {
		document.getElementById("filter-button-graph").click();
	};

	/*
	 * external references: none, used in initView function
	 */
	ConDecVis.prototype.buildVis = function(filterSettings, container) {
		console.log("conDecVis buildVis");
		conDecAPI.getVis(filterSettings, function(visData) {
			conDecVis.buildGraphNetwork(visData, container);
		});
	};

	ConDecVis.prototype.buildGraphNetwork = function(data, container) {
		var options = conDecVis.getOptions(data);

		var graphContainer = document.getElementById(container);
		var graphNetwork = new vis.Network(graphContainer, data, options);
		graphNetwork.setSize("100%", "500px");

		graphNetwork.on("oncontext", function(params) {
			conDecVis.addContextMenu(params, graphNetwork);
		});
		if (data.selectedVisNodeId !== undefined && data.selectedVisNodeId !== "") {
			graphNetwork.selectNodes([ data.selectedVisNodeId ]);
		}
	};
	
	ConDecVis.prototype.getOptions = function (visData) {		
		var options = {
		    edges : {
		        arrows : "to",
		        length : 200
		    },
		    layout : {
			    randomSeed : 1,
			    improvedLayout : false
		    },
		    manipulation : {
		        enabled : true,
		        addNode : function(newNode, callback) {
		        	conDecVis.addNode(newNode);
		        },
		        deleteNode : function(selectedNode, callback) {
			        conDecVis.deleteNode(selectedNode, callback);
		        },
		        addEdge : function(selectedNodes, callback) {
			        conDecVis.addEdge(selectedNodes);
		        },
		        deleteEdge : function(selectedEdge, callback) {
			        conDecVis.deleteEdge(selectedEdge, visData, callback);
		        },
		        editNode : function(selectedNode, callback) {
			        conDecVis.editNode(selectedNode, callback);
		        },
		        editEdge : function(selectedEdge, callback) {
			        conDecVis.editEdge(selectedEdge, visData);
		        }
		    },
		    physics : {
		        enabled : true,
		        stabilization : true // if false, there is a lot of movement
										// at the beginning
		    },
		    nodes : {
		    	  shapeProperties: {
		    	    interpolation: false    // 'true' for intensive zooming
		    	}
		    }
		};
		return options;
	};

	ConDecVis.prototype.addContextMenu = function(params, network) {
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
		const elementId = getElementId(clickedNodeId);
		const documentationLocation = getDocumentationLocation(clickedNodeId);
		conDecContextMenu.createContextMenu(elementId, documentationLocation, params.event, "vis-container");
	};
	
	ConDecVis.prototype.addNode = function (newNode) {
		conDecDialog.showCreateDialog(-1, null, "Decision");
	};

	ConDecVis.prototype.deleteNode = function (selectedNodes, callback) {
		conDecDialog.showDeleteDialog(getElementId(selectedNodes.nodes[0]), getDocumentationLocation(selectedNodes.nodes[0]), callback);
	};
	
	ConDecVis.prototype.editNode = function (selectedNode, callback) {
		conDecDialog.showEditDialog(selectedNode.elementId, selectedNode.documentationLocation, callback);
	};

	ConDecVis.prototype.addEdge = function (newEdge) {
		if (newEdge.from === newEdge.to) {
			return;
		}
		conDecDialog.showLinkDialog(getElementId(newEdge.from), getDocumentationLocation(newEdge.from), 
				getElementId(newEdge.to), getDocumentationLocation(newEdge.to));
	};

	ConDecVis.prototype.deleteEdge = function (selectedEdges, visData, callback) {
		var allEdges = new vis.DataSet(visData.edges);
		var edgeToBeDeleted = allEdges.get(selectedEdges.edges[0]);
		showDeleteLinkDialogForVisEdge(edgeToBeDeleted, callback);
	};
	
	ConDecVis.prototype.editEdge = function (newEdge, visData) {		
		var allEdges = new vis.DataSet(visData.edges);
		var edgeToBeDeleted = allEdges.get(newEdge.id);
		if (edgeToBeDeleted.from !== newEdge.from || edgeToBeDeleted.to !== newEdge.to) {
			showDeleteLinkDialogForVisEdge(edgeToBeDeleted);
		}		
		conDecDialog.showLinkDialog(getElementId(newEdge.from), getDocumentationLocation(newEdge.from), 
				getElementId(newEdge.to), getDocumentationLocation(newEdge.to), newEdge.label);
	};
	
	function showDeleteLinkDialogForVisEdge(edge, callback) {
		var idOfChild = getElementId(edge.to);
		var idOfParent = getElementId(edge.from);
		var documentationLocationOfChild = getDocumentationLocation(edge.to);
		var documentationLocationOfParent = getDocumentationLocation(edge.from);
		conDecDialog.showDeleteLinkDialog(idOfChild, documentationLocationOfChild, idOfParent,
		        documentationLocationOfParent, callback);	
	}

	function getDocumentationLocation(node) {
		return node.substr(-1);
	}
	
	function getElementId(node) {
		return node.slice(0, -2);
	}
	
	global.conDecVis = new ConDecVis();
})(window);
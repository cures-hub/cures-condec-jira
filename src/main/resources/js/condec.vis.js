(function(global) {

	var ConDecVis = function() {
	};
	
	ConDecVis.prototype.initView = function (isJiraIssueView = false) {
		console.log("ConDecVis initView");       
		
		// Fill HTML elements for filter criteria and add on click listener
		if (isJiraIssueView) {
			conDecFiltering.fillFilterElements("graph");
			conDecFiltering.addOnClickEventToFilterButton("graph", function(filterSettings) {
				issueKey = conDecAPI.getIssueKey();
				filterSettings["selectedElement"] = issueKey;
				conDecVis.buildVis(filterSettings);
			});
		} else {
			conDecFiltering.fillFilterElements("graph", ["Decision"]);
			conDecFiltering.addOnClickEventToFilterButton("graph", function(filterSettings) {
				conDecVis.buildVis(filterSettings);
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
	ConDecVis.prototype.buildVis = function(filterSettings) {
		console.log("conDecVis buildVis");
		conDecAPI.getVis(filterSettings, function(visData) {
			conDecVis.buildGraphNetwork(visData, "vis-graph-container");
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
		if (data.rootElementId !== undefined && data.rootElementId !== "") {
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
		        addNode : function(data, callback) {
		        	conDecDialog.showCreateDialog(-1, null, "Decision");
		        },
		        deleteNode : function(selectedNode, callback) {
			        conDecVis.deleteNode(selectedNode);
		        },
		        addEdge : function(selectedNodes, callback) {
			        conDecVis.addEdge(selectedNodes);
		        },
		        deleteEdge : function(selectedEdge, callback) {
			        conDecVis.deleteEdge(selectedEdge, visData);
		        },
		        editNode : function(selectedNode, callback) {
			        conDecVis.editNode(selectedNode);
		        },
		        editEdge : function(selectedNode, callback) {
			        conDecVis.editEdge(selectedNode);
		        }
		    },
		    physics : {
		        enabled : true,
		        stabilization : true // if false, there is a lot of movement at the beginning
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

	ConDecVis.prototype.deleteNode = function (data) {
		conDecDialog.showDeleteDialog(getElementId(data.nodes[0]), getDocumentationLocation(data.nodes[0]));
	};
	
	ConDecVis.prototype.editNode = function (data) {
		conDecDialog.showEditDialog(data.elementId, data.documentationLocation);
	};

	ConDecVis.prototype.addEdge = function (data) {
		if (data.from === data.to) {
			return;
		}
		conDecDialog.showLinkDialog(getElementId(data.from), getDocumentationLocation(data.from), 
				getElementId(data.to), getDocumentationLocation(data.to));
	};

	ConDecVis.prototype.deleteEdge = function (data, visData) {
		var allEdges = new vis.DataSet(visData.edges);
		var edgeToBeDeleted = allEdges.get(data.edges[0]);
		var idOfChild = getElementId(edgeToBeDeleted.to);
		var idOfParent = getElementId(edgeToBeDeleted.from);
		var documentationLocationOfChild = getDocumentationLocation(edgeToBeDeleted.to);
		var documentationLocationOfParent = getDocumentationLocation(edgeToBeDeleted.from);
		conDecDialog.showDeleteLinkDialog(idOfChild, documentationLocationOfChild, idOfParent,
		        documentationLocationOfParent);
	};
	
	ConDecVis.prototype.editEdge = function (data) {
		console.log(data);
	};

	function getDocumentationLocation(node) {
		return node.substr(-1);
	}
	
	function getElementId(node) {
		return node.slice(0, -2);
	}
	
	global.conDecVis = new ConDecVis();
})(window);
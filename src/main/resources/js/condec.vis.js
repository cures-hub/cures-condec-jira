(function(global) {

	var ConDecVis = function() {
	};

	/*
	 * external references: condec.jira.issue.module
	 */
	ConDecVis.prototype.buildVis = function(filterSettings) {
		console.log("conDecVis buildVis");
		conDecAPI.getVis(filterSettings, function(visData) {
			conDecVis.buildGraphNetwork(visData, "vis-container");
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
			graphNetwork.selectNodes([ data.rootElementId ]);
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
		        editEdge : false
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
		conDecContextMenu.createContextMenu(clickedNodeId.toString().slice(0, -2),
		        getDocumentationLocationFromId(clickedNodeId), params.event, "vis-container");
	};

	// TODO Avoid data slicing, this is very hard to understand!
	ConDecVis.prototype.deleteNode = function (data) {
		conDecDialog.showDeleteDialog(data.nodes[0].slice(0, -2), data.nodes[0].substr(-1));
	};

	// TODO Avoid data slicing, this is very hard to understand!
	ConDecVis.prototype.addEdge = function (data) {
		if (data.from === data.to) {
			return;
		}
		conDecDialog.showLinkDialog(data.from.slice(0, -2), data.from.substr(-1), data.to.slice(0, -2), data.to
		        .substr(-1));
	};

	// TODO Avoid data slicing, this is very hard to understand!
	ConDecVis.prototype.deleteEdge = function (data, visData) {
		var allEdges = new vis.DataSet(visData.edges);
		var edgeToBeDeleted = allEdges.get(data.edges[0]);
		var idOfChild = edgeToBeDeleted.to.slice(0, -2);
		var idOfParent = edgeToBeDeleted.from.slice(0, -2);
		var documentationLocationOfChild = edgeToBeDeleted.to.substr(-1);
		var documentationLocationOfParent = edgeToBeDeleted.from.substr(-1);
		conDecDialog.showDeleteLinkDialog(idOfChild, documentationLocationOfChild, idOfParent,
		        documentationLocationOfParent);
	};

	function getDocumentationLocationFromId(nodeId) {
		return nodeId.toString().substr(-1);
	}

	global.conDecVis = new ConDecVis();
})(window);
(function (global) {

    var ConDecVis = function ConDecVis() {
    };

    function build(theNodes, theEdges, rootElementKey, nodeDistance) {
        console.log("ConDecVis build");
        var nodes = new vis.DataSet(theNodes);
        var edges = new vis.DataSet(theEdges);
        var container = document.getElementById('vis-container');
        nodes.update([{
            id: rootElementKey,
            borderWidth: 3
        }]);
        nodes.update([{
            id: rootElementKey,
            shadow: {
                enabled: true
            }
        }]);

        var data = {
            nodes: nodes,
            edges: edges
        };
        var options = conDecVis.getVisOptions(data);

        var network = new vis.Network(container, data, options);
        network.setSize("100%", "500px");

        network.on("oncontext", function (params) {
                conDecVis.addContextMenu(params, network);
        });

        network.on("hold", function (params) {
            conDecVis.holdFunction(params, network);
        });
        network.on("selectNode", function (params) {
            conDecVis.selectNode(params, network);
        });
        var clusterOptionsByData = {
            joinCondition: function (childOptions) {
                return ((childOptions.level <= 50 - nodeDistance) || (childOptions.level >= 50 + nodeDistance) || (childOptions.cid >= nodeDistance));
            },
            clusterNodeProperties: {
                allowSingleNodeCluster: false,
                id: 'distanceCluster',
                shape: 'ellipse',
                label: 'clusteredNodes',
                level: ((50 * 1) + (nodeDistance * 1))
            }

        };
        network.cluster(clusterOptionsByData);
        return network;
    }

    ConDecVis.prototype.addContextMenu = function addContextMenu(params, network) {
        params.event.preventDefault();
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
            conDecContextMenu.createContextMenu(clickedNodeId.toString().slice(0, -2),
                getDocumentationLocationFromId(clickedNodeId), params.event, "vis-container");
        }
    };

    /*
     * external references: condec.jira.issue.module
     */
    ConDecVis.prototype.buildVisFiltered = function buildVisFiltered(issueKey, search, nodeDistance, issueTypes,
                                                                     createdAfter, createdBefore, linkTypes, documentationLocation) {
        console.log("conDecVis buildVisFiltered");
        conDecAPI.getVisFiltered(issueKey, search, issueTypes, createdAfter, createdBefore, linkTypes, documentationLocation,
            function (visData) {
                build(visData.nodes, visData.edges, visData.rootElementKey, nodeDistance);
            });
    };

    /*
     * external references: condec.jira.issue.module
     */
    ConDecVis.prototype.buildVis = function buildVis(elementKey, searchTerm) {
        console.log("conDecVis buildVis");
        conDecAPI.getVis(elementKey, searchTerm, function (visData) {
            var network = build(visData.nodes, visData.edges, visData.rootElementKey, 10);
            network.focus(visData.rootElementKey, {
                scale: 0.9
            });
        });
    };

    ConDecVis.prototype.getVisOptions = function getVisOptions(visData) {
        return {
            clickToUse: false,
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

            autoResize: false,

            layout: {
                improvedLayout: true,
                hierarchical: {
                    enabled: true,
                    levelSeparation: 140,
                    nodeSpacing: 250,
                    treeSpacing: 0,
                    blockShifting: false,
                    edgeMinimization: false,
                    parentCentralization: true,
                    direction: 'UD', // UD, DU, LR, RL
                    sortMethod: 'directed' // hubsize, directed
                }

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

            manipulation: {
                enabled: true,
                editEdge: false,
                addNode: false,
                addEdge: function (data, callback) {
                    conDecVis.addEdge(data, callback);
                },
                deleteNode: function (data, callback) {
                    conDecVis.deleteNode(data, callback);
                },
                deleteEdge: function (data, callback) {
                    conDecVis.deleteEdge(data, visData, callback);
                }

            },
            physics: {
                enabled: false
            },
            interaction: {
                tooltipDelay: 600
            }
        }
    };

    ConDecVis.prototype.holdFunction = function  holdFunction(params, network) {
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
    };

    ConDecVis.prototype.selectNode =  function selectNode(params, network) {
        if (params.nodes.length === 1) {
            if (network.isCluster(params.nodes[0]) === true) {
                network.openCluster(params.nodes[0]);
            }
        }
    };

    ConDecVis.prototype.deleteNode =  function deleteNode(data, callback) {
        conDecDialog.showDeleteDialog(data.nodes[0].slice(0, -2), data.nodes[0]
            .substr(-1), function() {
            callback(data);
        });
    };

    ConDecVis.prototype.addEdge = function addEdge(data, callback) {
        if (data.from !== data.to) {
            conDecAPI.createLink(null, data.from.slice(0, -2), data.to.slice(0, -2), data.to.substr(-1),
                data.from.substr(-1), null, function () {
                    conDecObservable.notify();
                    callback(data);
                });
        }
    };

    ConDecVis.prototype.addEdgeWithType = function addEdge(data, callback) {
        if (data.from !== data.to) {
            conDecDialog.showDecisionLinkDialog(data.from.slice(0, -2), data.to.slice(0, -2), data.from.substr(-1), data.to.substr(-1), function() {
                callback(data);
            });
        }
    };

    ConDecVis.prototype.deleteEdge = function deleteEdge(data, visData, callback) {
        var allEdges = new vis.DataSet(visData.edges);
        var edgeToBeDeleted = allEdges.get(data.edges[0]);
        var idOfChild = edgeToBeDeleted.to.slice(0, -2);
        var idOfParent = edgeToBeDeleted.from.slice(0, -2);
        var documentationLocationOfChild = edgeToBeDeleted.to.substr(-1);
        var documentationLocationOfParent = edgeToBeDeleted.from.substr(-1);
        conDecDialog.showDeleteLinkDialog(idOfChild, documentationLocationOfChild, 
        		idOfParent, documentationLocationOfParent, function() {
            callback(data);
        });
    };

    function getDocumentationLocationFromId(nodeId) {
        return nodeId.toString().substr(-1);
    }

    global.conDecVis = new ConDecVis();
})(window);
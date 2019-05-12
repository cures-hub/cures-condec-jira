(function(global) {

    var ConDecVis = function ConDecVis() {
    };

    ConDecVis.prototype.buildVis = function buildVis(elementKey,  searchTerm) {
        console.log("conDecVis buildVis");

        conDecAPI.getVis(elementKey,searchTerm,function (visData) {
            var container = document.getElementById('vis-container');
            console.log(visData.nodes);
            var data = {
                nodes: visData.nodes,
                edges: visData.edges
            };
            var options = {
                clickToUse: false,
                interaction:{ keyboard: {enabled: true}},
                nodes: {
                    shape: "box",
                    widthConstraint:120,
                    color:{ background: 'rgba(255, 255, 255,1)',
                            border: 'rgba(0,0,0,1)',
                            highlight: {border: 'rgba(0,0,0,1)'
                            }},
                    font: {multi: true}
                },
                edges:{
                    arrows: "to"
                },

                autoResize: false,

                layout: {
                    improvedLayout: false,
                    hierarchical: {
                        enabled: true,
                        levelSeparation: 140,
                        nodeSpacing: 300,
                        treeSpacing: 200,
                        blockShifting: true,
                        edgeMinimization: false,
                        parentCentralization: true,
                        direction: 'UD', // UD, DU, LR, RL
                        sortMethod: 'directed' // hubsize, directed
                    }

                },
                groups:{
                    // Setting colors and Levels for Decision Knowledge Elements stored in Jira Issues
                    decision: {color:{background: 'rgba(252,227,190,1)'}},
                    issue: {color:{background: 'rgba(255, 255, 204,1)'}},
                    alternative: {color:{background: 'rgba(252,227,190,1'}},
                    pro: {color:{background: 'rgba(222, 250, 222,1)'}},
                    con: {color:{background: 'rgba(255, 231, 231,1)'}},
                    argument: {color:{background: 'rgba(255, 255, 255,1)'}},
                    constraint: {color:{background: 'rgba(255, 255, 255,1)'}},
                    assumption: {color:{background: 'rgba(255, 255, 255,1)'}},
                    implication: {color:{background: 'rgba(255, 255, 255,1)'}},
                    context: {color:{background: 'rgba(255, 255, 255,1)'}},
                    problem: {color:{background: 'rgba(255, 255, 204,1)'}},
                    goal: {color:{background: 'rgba(255, 255, 255,1)'}},
                    solution: {color:{background: 'rgba(255, 255, 255,1)'}},
                    claim: {color:{background: 'rgba(255, 255, 255,1)'}},
                    rationale: {color:{background: 'rgba(255, 255, 255,1)'}},
                    question: {color:{background: 'rgba(255, 255, 255,1)'}},
                    assessment: {color:{background: 'rgba(255, 255, 255,1)'}},
                    collapsed: {shape: "dot", size: 5, color: {background: 'rgba(0,0,0,1)'}}
                },

                manipulation: {
                    enabled: true,
                    editEdge: false,
                    addNode: false,
                    addEdge: function(data, callback) {
                        console.log('add edge', data);
                        if (data.from !== data.to) {
                            callback(data);

                            conDecAPI.createLink(null,data.from.slice(0, -2),data.to.slice(0,-2),data.to.substr(-1),
                                data.from.substr(-1),function () {
                                    //network.destroy();
                                    //buildVis(elementKey,searchTerm);
                                    conDecObservable.notify();
                                })
                        }
                    },
                    deleteNode: function(data, callback) {
                        console.log('deleteNode', data);
                        console.log('delete node:', data.nodes[0].slice(0,-2));
                        var conf = confirm("You want to delete this element?");
                            if (conf) {
                                conDecAPI.deleteDecisionKnowledgeElement(data.nodes[0].slice(0,-2),data.nodes[0].substr(-1),function() {
                                     conDecObservable.notify();
                                });
                        }
                    },
                    deleteEdge: function (data,callback) {
                        console.log('deleteEdge', data);
                    }

                },
                physics: {enabled:false}
                };
            var network = new vis.Network(container, data, options);
            network.setSize("100%","400px");
            //network.addEdgeMode();
            network.stabilize();

            network.focus();
            network.on("oncontext", function(params) {
                console.log(params);
                params.event.preventDefault();
                var nodeIndices = network.body.nodeIndices;
                var clickedNodeId;
                console.log(network);
                for (var i = 0; i < nodeIndices.length; i++) {
                    var nodeId = nodeIndices[i];
                    var boundingBox = network.getBoundingBox(nodeId);
                    if (boundingBox.left <= params.pointer.canvas.x &&
                        params.pointer.canvas.x <= boundingBox.right &&
                        boundingBox.top <= params.pointer.canvas.y &&
                        params.pointer.canvas.y <= boundingBox.bottom) {
                        clickedNodeId = nodeId;
                    }
                }
                console.log("ContextMenu for ID: " + clickedNodeId.toString().slice(0, -2) +
                    " and location: " + clickedNodeId.toString().substr(-1));
                conDecContextVis.createContextVis(clickedNodeId.toString().slice(0, -2),
                    getDocumentationLocationFromId(clickedNodeId), params.event, "vis-container");
            });

            network.on("hold", function(params) {
                var nodeIndices = network.body.nodeIndices;
                var clickedNodeId;
                for (var i = 0; i < nodeIndices.length; i++) {
                    var nodeId = nodeIndices[i];
                    var boundingBox = network.getBoundingBox(nodeId);
                    if (boundingBox.left <= params.pointer.canvas.x &&
                        params.pointer.canvas.x <= boundingBox.right &&
                        boundingBox.top <= params.pointer.canvas.y &&
                        params.pointer.canvas.y <= boundingBox.bottom) {
                        clickedNodeId = nodeId;
                    }
                }
                if (clickedNodeId !== undefined) {
                    params.event.preventDefault();
                    conDecDialog.showEditDialog(clickedNodeId.toString().slice(0, -2),
                        getDocumentationLocationFromId(clickedNodeId));
                }
            })
        });


    };
    function getDocumentationLocationFromId(nodeId) {
        return nodeId.toString().substr(-1);
    };

    global.conDecVis = new ConDecVis();
})(window);
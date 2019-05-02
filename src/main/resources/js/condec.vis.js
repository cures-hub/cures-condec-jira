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
                            highlight: {background: 'rgba(255,255,255,1)',
                                        border: 'rgba(0,0,0,1)'
                            }},
                    font: {multi: true},
                    shadow: true,
                    scaling: {label: {enabled: true}}
                },
                edges:{
                    arrows: "to"
                },

                layout: {
                    improvedLayout: false,
                    hierarchical: {
                        enabled: true,
                        levelSeparation: 100,
                        nodeSpacing: 250,
                        treeSpacing: 200,
                        blockShifting: true,
                        edgeMinimization: true,
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

                            conDecAPI.createLink(null,data.to.slice(0, -2),data.from.slice(0,-2),data.to.substr(-1),
                                data.from.substr(-1),function () {
                                    //network.destroy();
                                    //buildVis(elementKey,searchTerm);
                                    conDecObservable.notify();
                                })
                        }
                    },
                    deleteEdge: function(data, callback) {
                        console.log(data);

                    },
                    deleteNode: function(data, callback) {
                        console.log(data);
                    }

                },
                physics: {
                    stabilization: false,
                    barnesHut: {
                        gravitationalConstant: -80000,
                        springConstant: 0.001,
                        springLength: 200
                    }
                }
                };
            var network = new vis.Network(container, data, options);
            network.setSize("100%","600px");


            var hasDrawnOnce = false;
            network.on("startStabilizing", function() {
                if(hasDrawnOnce!=false){
                    network.stopSimulation();
                }
                hasDrawnOnce=true;
            });

            network.focus(visData.rootNodeId,{scale: 0.2});
            console.log(network.body.nodes[visData.rootNodeId]);
            var node = network.body.nodes[visData.rootNodeId];
            node.setOptions({
                color:{ borderWidth: 3,
                        color: {border: 'rgba(255,0,0,1)'}
                }
            });

            network.on("hold", function(params){
               params.event.preventDefault;
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
                console.log(clickedNodeId.toString(),getDocumentationLocationFromId(clickedNodeId),network.body.nodes[clickedNodeId].options.groups);
                conDecDialog.showEditDialog(clickedNodeId.toString(),getDocumentationLocationFromId(clickedNodeId),network.body.nodes[clickedNodeId].options.groups);
            });

            network.on("oncontext", function(params) {
                params.event.preventDefault();
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
                console.log("ContextMenu for ID: " + clickedNodeId.toString().slice(0, -2) +
                    " and location: " + clickedNodeId.toString().substr(-1));
                conDecContextMenu.createContextMenu(clickedNodeId.toString().slice(0, -2),
                    getDocumentationLocationFromId(clickedNodeId), params.event, "vis-container");
            });

            var keys = vis.keycharm({
                container: container,
                preventDefault: true
            });

            keys.bind("delete", function(event) {
                var selection = network.getSelection();
                console.log(selection);
                console.log(event);
            });
        });


    };
    function getDocumentationLocationFromId(nodeId) {
        return nodeId.toString().substr(-1);
    };

    global.conDecVis = new ConDecVis();
})(window);
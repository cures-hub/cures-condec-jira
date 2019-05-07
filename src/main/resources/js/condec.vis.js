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
                    Decision_i: {color:{background: 'rgba(252,227,190,1)'}},
                    Issue_i: {color:{background: 'rgba(255, 255, 204,1)'}},
                    Alternative_i: {color:{background: 'rgba(252,227,190,1'}},
                    Pro_i: {color:{background: 'rgba(222, 250, 222,1)'}},
                    Con_i: {color:{background: 'rgba(255, 231, 231,1)'}},
                    Argument_i: {color:{background: 'rgba(255, 255, 255,1)'}},
                    Constraint_i: {color:{background: 'rgba(255, 255, 255,1)'}},
                    Assumption_i: {color:{background: 'rgba(255, 255, 255,1)'}},
                    Implication_i: {color:{background: 'rgba(255, 255, 255,1)'}},
                    Context_i: {color:{background: 'rgba(255, 255, 255,1)'}},
                    Problem_i: {color:{background: 'rgba(255, 255, 204,1)'}},
                    Goal_i: {color:{background: 'rgba(255, 255, 255,1)'}},
                    Solution_i: {color:{background: 'rgba(255, 255, 255,1)'}},
                    Claim_i: {color:{background: 'rgba(255, 255, 255,1)'}},
                    Rationale_i: {color:{background: 'rgba(255, 255, 255,1)'}},
                    Question_i: {color:{background: 'rgba(255, 255, 255,1)'}},
                    Assessment_i: {color:{background: 'rgba(255, 255, 255,1)'}},
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
                console.log(params.event);
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
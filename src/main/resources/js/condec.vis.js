(function(global) {

    var ConDecVis = function ConDecVis() {
    };

    ConDecVis.prototype.buildVis = function buildVis(elementKey,  searchTerm) {
        console.log("conDecVis buildVis");

        conDecAPI.getVis(elementKey,searchTerm,function (visData) {
            var container = document.getElementById('treant-container');
            console.log(visData.nodes);
            var data = {
                nodes: visData.nodes,
                edges: visData.edges
            };
            var options = {
                nodes: {
                    shape: "box",
                    level:8,
                    widthConstraint:120,
                    color:{background: 'rgba(255, 255, 255,1)'}
                },
                edges:{
                    arrows: "to"
                },

                autoResize: false,

                layout: {
                    improvedLayout: true,
                    hierarchical: {
                        enabled: true,
                        levelSeparation: 100,
                        nodeSpacing: 150,
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
                    Decision_i: {color:{background: 'rgba(252,227,190,1)'}, level: 11},
                    Issue_i: {color:{background: 'rgba(255, 255, 204,1)'}, level: 10},
                    Alternative_i: {color:{background: 'rgba(252,227,190,1)'}, level: 11},
                    Pro_i: {color:{background: 'rgba(222, 250, 222,1)'}, level: 12},
                    Con_i: {color:{background: 'rgba(255, 231, 231,1)'}, level: 12},
                    Argument_i: {color:{background: 'rgba(255, 255, 255,1)'}, level: 12},
                    Constraint_i: {color:{background: 'rgba(255, 255, 255,1)'}, level: 12},
                    Assumption_i: {color:{background: 'rgba(255, 255, 255,1)'}, level: 12},
                    Implication_i: {color:{background: 'rgba(255, 255, 255,1)'}, level: 12},
                    Context_i: {color:{background: 'rgba(255, 255, 255,1)'}, level: 12},
                    Problem_i: {color:{background: 'rgba(255, 255, 204,1)'}, level: 9},
                    Goal_i: {color:{background: 'rgba(255, 255, 255,1)'}, level: 12},
                    Solution_i: {color:{background: 'rgba(255, 255, 255,1)'}, level: 12},
                    Claim_i: {color:{background: 'rgba(255, 255, 255,1)'}, level: 12},
                    Rationale_i: {color:{background: 'rgba(255, 255, 255,1)'}, level: 12},
                    Question_i: {color:{background: 'rgba(255, 255, 255,1)'}, level: 12},
                    Assessment_i: {color:{background: 'rgba(255, 255, 255,1)'}, level: 12}
                },

                manipulation: {
                    enabled: false,
                    addNode: function(data, callback) {
                        // filling in the popup DOM elements
                        console.log('add', data);
                    },
                    editNode: function(data, callback) {
                        // filling in the popup DOM elements
                        console.log('edit', data);
                    },
                    addEdge: function(data, callback) {
                        console.log('add edge', data);
                        if (data.from !== data.to) {
                            callback(data);

                            conDecAPI.createLink(null,data.to.slice(0, -2),data.from.slice(0,-2),data.to.substr(-1),
                                 data.from.substr(-1),function () {
                                network.destroy();
                                buildVis(elementKey,searchTerm);
                                conDecObservable.notify();
                            })
                        }
                        // after each adding you will be back to addEdge mode

                        network.addEdgeMode();
                    }
                }
                };
            var network = new vis.Network(container, data, options);
            network.setSize("100%","400px");
            network.addEdgeMode();
            network.on("oncontext", function(params) {
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
                conDecContextMenu.createContextMenu(clickedNodeId.toString().slice(0, -2), clickedNodeId.toString().substr(-1),
                    params.event, "treant-container");
            })
        });

    };
    function getDocumentationLocationFromId(nodeId) {

    };
    global.conDecVis = new ConDecVis();
})(window);
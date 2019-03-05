(function(global) {

    var ConDecVis = function ConDecVis() {
    };

    ConDecVis.prototype.buildVis = function buildVis(elementKey,  searchTerm) {
        console.log("conDecVis buildVis");

        conDecAPI.getVis(elementKey,searchTerm,function (visData) {
            var container = document.getElementById('treant-container');
            console.log(visData.nodes)
            var data = {
                nodes: visData.nodes,
                edges: visData.edges
            }
            var options = {
                nodes: {
                    shape: "box",

                    font: {
                        size:18
                    },
                    scaling: {
                        min: 10,
                        max: 30
                    }
                },
                edges:{
                    arrows: "to"
                },

                autoResize: false,

                layout: {
                    improvedLayout: true,
                    hierarchical: {
                        enabled: true,
                        levelSeparation: 150,
                        nodeSpacing: 100,
                        treeSpacing: 200,
                        blockShifting: true,
                        edgeMinimization: true,
                        parentCentralization: true,
                        direction: 'UD', // UD, DU, LR, RL
                        sortMethod: 'hubsize' // hubsize, directed
                    }
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
                            console.log("something happened");
                        }
                        // after each adding you will be back to addEdge mode
                        network.addEdgeMode();
                    }
                }
                };
            var network = new vis.Network(container, data, options)
            network.setSize("100%","400px");
            network.addEdgeMode();
            network.on("oncontext", function(params) {
                params.event.preventDefault();
                params.event.
                console.log("right-click: ",params);
                console.log("node-position 1: ",network.getPositions("ASDF-1"));
                console.log("node-position 2: ",network.getPositions("ASDF-2"));
                console.log("node-position 3: ",network.getPositions("ASDF-3"));
                console.log("node-position 4: ",network.getPositions("ASDF-4"));
                console.log("node: ",network.getNodeAt(params.pointer.canvas.x,params.pointer.canvas.y))

                //var node = network.getNodeAt(params.event.x,params.event.y);
                //conDecContextMenu.createContextMenu(node.id,"",params.event,"treant-container");
            })
        });
    }
    global.conDecVis = new ConDecVis();
})(window);
function updateView(nodeId){
    $('#evts').on("select_node.jstree", function(error, data) {
        var node = data.node.data;
        fillAccordion(node);
        buildTreant(node);
    });
    buildTreeViewer(nodeId);
}

function initView() {
   updateView(getProjectKey());
}
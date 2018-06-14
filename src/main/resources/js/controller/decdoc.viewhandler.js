function updateView(nodeId){
    if ($('#evts').jstree(true)) {
        var tree = $('#evts').jstree(true);
        tree.destroy();
    }
    $('#evts').on("select_node.jstree", function(error, data) {
        var node = data.node.data;
        console.log(node);
        fillAccordion(node);
        buildTreant(node);
    });
    buildTreeViewer(nodeId);
}

function initView() {
   updateView(getProjectKey());
}
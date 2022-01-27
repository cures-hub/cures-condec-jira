/**
 * This module implements the tooltip that appears when hovering over
 * an element inside a JSTreeViewer.
 *
 * Requires: Nothing
 *
 * Is required by: conDecTreeViewer
 */
(function(global) {

    var ConDecCIATooltip = function() {
	};

	ConDecCIATooltip.prototype.addJSTreeTooltip = function() {
        jQuery("#jstree").on("hover_node.jstree", function(error, tree) {
            $("#" + tree.node.id).prop("title", $("#" + tree.node.id).attr("cia_tooltip"));
        });
	};

    global.conDecCIATooltip = new ConDecCIATooltip();
})(window);

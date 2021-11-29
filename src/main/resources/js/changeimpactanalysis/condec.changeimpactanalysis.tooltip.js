(function(global) {

    var ConDecCIATooltip = function() {
	};

	ConDecCIATooltip.prototype.addJSTreeTooltip = function() {
        jQuery("#jstree").on("hover_node.jstree", function(error, tree) {
            let propagationRuleExplanation = ($("#" + tree.node.id).attr("cia_propagationRuleSummary") !== "") ?
                ("\nPropagation Rule Value: " + $("#" + tree.node.id).attr("cia_ruleBasedValue") +
                "; Utilized Rules:" + 
                "\n" + $("#" + tree.node.id).attr("cia_propagationRuleSummary")) :
                ""
            // Check if node is root
            let ciaExplanation = ($("#" + tree.node.id).attr("id").substring(0,2) === "tv") ?
            "This is the source node from which the Change Impact Analysis was calculated" :
            "Overall CIA Impact Factor: " + $("#" + tree.node.id).attr("cia_impactFactor") +
                "\n--- --- --- --- --- --- --- --- ---" +
                "\nParent Node Impact: " + $("#" + tree.node.id).attr("cia_parentImpact") +
                "\nLink Type Decay Weight: " + $("#" + tree.node.id).attr("cia_linkTypeWeight") +
                propagationRuleExplanation +
                "\n--- --- --- --- --- --- --- --- ---" +
                "\n" + $("#" + tree.node.id).attr("cia_valueExplanation");
            $("#" + tree.node.id).prop("title", ciaExplanation);
        });
	}

    ConDecCIATooltip.prototype.addMatrixTooltip = function(headerCell) {
        let propagationRuleExplanation = "\nPropagation Rule Value: " + 0.0 +
        "; Utilized Rules:" + 
        "\n" + ""
        let ciaExplanation = "Overall CIA Impact Factor: " + 0.0 +
            "\n--- --- --- --- --- --- --- --- ---" +
            "\nParent Node Impact: " + 0.0 +
            "\nLink Type Decay Weight: " + 0.0 +
            propagationRuleExplanation +
            "\n--- --- --- --- --- --- --- --- ---" +
            "\n" + "";
    headerCell.title = ciaExplanation;
    }

    global.conDecCIATooltip = new ConDecCIATooltip();
})(window);

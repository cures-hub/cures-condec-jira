/*
 This module is responsible for filtering functionality and view elements.
 
 Requires

 Is required by
 * conDecJiraIssueModule
 * conDecEvolutionPage
 * ConDecRelationshipPage
 */
(function(global) {

	var ConDecFiltering = function () {
		console.log("conDecFiltering constructor");
	};

	/*
	 * external references: condec.jira.issue.module, condec.evolution.page, condec.relationship.page
	 */
	ConDecFiltering.prototype.initDropdown = function (dropdownId, items) {
		var dropdown = document.getElementById(dropdownId);
		for (var index = 0; index < items.length; index++) {
			dropdown.insertAdjacentHTML("beforeend", "<aui-item-checkbox interactive " + "checked" + ">" + items[index]
			        + "</aui-item-checkbox>");
		}
	};
	
	/*
	 * external references: condec.jira.issue.module, condec.evolution.page, condec.relationship.page
	 */
	ConDecFiltering.prototype.getSelectedItems = function (dropdownId) {
		var dropdown = AJS.$("#" + dropdownId);
		var selectedItems = [];
		for (var i = 0; i < dropdown.children().size(); i++) {
			if (typeof dropdown.children().eq(i).attr("checked") !== typeof undefined
					&& dropdown.children().eq(i).attr("checked") !== false) {
				selectedItems.push(dropdown.children().eq(i).text());
			}
		}
		return selectedItems;
	};
	
	/*
	 * external references: condec.jira.issue.module, condec.knowledge.page
	 */
	ConDecFiltering.prototype.addEventListenerToLinkDistanceInput = function (inputId, callback) {
		var depthOfTreeInput = document.getElementById(inputId);
        depthOfTreeInput.addEventListener("input", function () {
            var depthOfTreeWarningLabel = document.getElementById("depth-of-tree-warning");
            if (this.value > 0) {
                depthOfTreeWarningLabel.style.display = "none";
                callback();
            } else {
                depthOfTreeWarningLabel.style.display = "inline";
                depthOfTreeWarningLabel.style.visibility = "visible";
            }
        });
	};

	// export ConDecFiltering
	global.conDecFiltering = new ConDecFiltering();
})(window);
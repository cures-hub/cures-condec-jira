/*
 This module is responsible for filtering functionality and view elements.
 
 Requires

 Is required by
 * conDecJiraIssueModule
 * conDecEvolutionPage
 * ConDecRelationshipPage
 */
(function(global) {

	var ConDecFiltering = function ConDecFiltering() {
		console.log("conDecFiltering constructor");
	};

	/*
	 * external references: condec.jira.issue.module, condec.evolution.page, condec.relationship.page
	 */
	ConDecFiltering.prototype.initDropdown = function initDropdown(dropdownId, items) {
		var dropdown = document.getElementById(dropdownId);
		for (var index = 0; index < items.length; index++) {
			dropdown.insertAdjacentHTML("beforeend", "<aui-item-checkbox interactive " + "checked" + ">" + items[index]
			        + "</aui-item-checkbox>");
		}
	};
	
	/*
	 * external references: condec.jira.issue.module, condec.evolution.page, condec.relationship.page
	 */
	ConDecFiltering.prototype.getSelectedItems = function getSelectedItems(dropdownId) {
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

	// export ConDecFiltering
	global.conDecFiltering = new ConDecFiltering();
})(window);
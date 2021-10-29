/**
 This module provides the dialogs for managing decision groups/levels.

 Requires
 * conDecGroupingAPI

 Is required by
 * conDecContextMenu
 */
(function(global) {
	var ConDecGroupingDialog = function() {
	};

	ConDecGroupingDialog.prototype.showAssignDialog = function(sourceId, documentationLocation) {
		console.log("ConDecGroupingDialog showAssignDialog");

		// HTML elements
		var assignDialog = document.getElementById("assign-dialog");
		var selectLevelField = document.getElementById("assign-form-select-level");
		var inputExistingGroupsField = document.getElementById("assign-form-input-existing");
		var inputAddGroupField = document.getElementById("assign-form-input-add");
		var submitButton = document.getElementById("assign-dialog-submit-button");
		var cancelButton = document.getElementById("assign-dialog-cancel-button");

		// Fill HTML elements
		inputAddGroupField.value = "";

		conDecGroupingAPI.getDecisionGroups(sourceId, documentationLocation, function(groups) {
			if (groups.length > 0) {
				var level = groups[0];
				selectLevelField.value = level;
			}
			if (groups.length > 1) {
				groups.shift();
				inputExistingGroupsField.value = groups;
			} else {
				inputExistingGroupsField.value = "";
			}
		});

		// Set onclick listener on buttons
		submitButton.onclick = function() {
			var level = selectLevelField.value;
			var existingGroups = inputExistingGroupsField.value;
			var addgroup = inputAddGroupField.value;
			conDecGroupingAPI.assignDecisionGroup(level, existingGroups, addgroup,
				sourceId, documentationLocation, function(id) {
					conDecObservable.notify();
				});
			AJS.dialog2(assignDialog).hide();
		};

		cancelButton.onclick = function() {
			AJS.dialog2(assignDialog).hide();
		};

		// Show dialog
		AJS.dialog2(assignDialog).show();
	};

	ConDecGroupingDialog.prototype.showRenameGroupDialog = function(groupName) {
		console.log("ConDecGroupingDialog showRenameGroupDialog");
		// HTML elements
		var renameGroupDialog = document.getElementById("rename-group-dialog");
		if (groupName === "High_Level" || groupName === "Medium_Level" || groupName === "Realization_Level") {
			alert("You cannot rename decision levels.");
		} else {
			var inputGroupName = document.getElementById("rename-group-input");
			var submitButton = document.getElementById("rename-group-dialog-submit-button");
			var cancelButton = document.getElementById("rename-group-dialog-cancel-button");

			inputGroupName.value = groupName;

			// Set onclick listener on buttons
			submitButton.onclick = function() {
				var newName = inputGroupName.value;
				conDecGroupingAPI.renameDecisionGroup(groupName, newName, function() {
					conDecObservable.notify();
				});
				AJS.dialog2(renameGroupDialog).hide();
			};

			cancelButton.onclick = function() {
				AJS.dialog2(renameGroupDialog).hide();
			};

			// Show dialog
			AJS.dialog2(renameGroupDialog).show();
		}
	};

	ConDecGroupingDialog.prototype.showDeleteGroupDialog = function(groupName) {
		console.log("ConDecGroupingDialog showDeleteGroupDialog");
		// HTML elements
		var deleteGroupDialog = document.getElementById("delete-group-dialog");
		if (groupName === "High_Level" || groupName === "Medium_Level" || groupName === "Realization_Level") {
			alert("You cannot delete decision levels.");
		} else {
			var deleteMessageLabel = document.getElementById("delete-group-label");
			var submitButton = document.getElementById("delete-group-dialog-submit-button");
			var cancelButton = document.getElementById("delete-group-dialog-cancel-button");

			deleteMessageLabel.innerHTML = "Are you sure that you want to remove the decision group <b>" + groupName + "</b>?";

			// Set onclick listener on buttons
			submitButton.onclick = function() {
				conDecGroupingAPI.deleteDecisionGroup(groupName, function() {
					conDecObservable.notify();
				});
				AJS.dialog2(deleteGroupDialog).hide();
			};

			cancelButton.onclick = function() {
				AJS.dialog2(deleteGroupDialog).hide();
			};

			// Show dialog
			AJS.dialog2(deleteGroupDialog).show();
		}
	};

	global.conDecGroupingDialog = new ConDecGroupingDialog();
})(window);
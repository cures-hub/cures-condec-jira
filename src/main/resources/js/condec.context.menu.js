/*
 This module is responsible for showing a context menu upon right mouse click.
 
 Requires
 * conDecAPI
 * conDecDialog

 Is required by
 * conDecTreant
 * conDecTreeViewer
 */
(function(global) {

	var isContextMenuOpen;

	var ConDecContextMenu = function ConDecContextMenu() {
		jQueryConDec(global).blur(hideContextMenu);
		jQueryConDec(document).click(hideContextMenu);
	};

	function hideContextMenu() {
		if (isContextMenuOpen) {
			console.log("contextmenu closed");
			document.querySelector("#condec-context-menu").setAttribute('aria-hidden', 'true');
			document.querySelector("#condec-context-menu-sentence").setAttribute('aria-hidden', 'true');
		}
		isContextMenuOpen = false;
	}

	function createContextMenu(posX, posY, id) {
		isContextMenuOpen = true;
		console.log("contextmenu opened");

		$("#condec-context-menu").css({
			left : posX,
			top : posY
		});
		document.getElementById("condec-context-menu").style.zIndex = 9998;
		document.querySelector("#condec-context-menu").setAttribute('aria-hidden', 'false');

		document.getElementById("condec-context-menu-create-item").onclick = function() {
			conDecDialog.showCreateDialog(id);
		};

		document.getElementById("condec-context-menu-edit-item").onclick = function() {
			conDecDialog.showEditDialog(id);
		};

		document.getElementById("condec-context-menu-change-type-item").onclick = function() {
			conDecDialog.showChangeTypeDialog(id);
		};

		document.getElementById("condec-context-menu-link-item").onclick = function() {
			conDecDialog.showLinkDialog(id);
		};

		document.getElementById("condec-context-menu-delete-link-item").onclick = function() {
			var parentId = conDecTreant.findParentId(id);
			conDecDialog.showDeleteLinkDialog(id, parentId);
		};

		document.getElementById("condec-context-menu-delete-item").onclick = function() {
			conDecDialog.showDeleteDialog(id);
		};

		document.getElementById("condec-context-menu-set-root-item").onclick = function() {
			if (window.conDecIssueModule !== undefined) {
				window.conDecIssueModule.setAsRootElement(id);
			} else if (window.conDecKnowledgePage !== undefined) {
				window.conDecKnowledgePage.setAsRootElement(id);
			}
		};

		document.getElementById("condec-context-menu-open-jira-issue-item").onclick = function() {
			if (window.conDecKnowledgePage !== undefined) {
				window.conDecKnowledgePage.openJiraIssue(id);
			}
		};
	}

	ConDecContextMenu.prototype.createContextMenu = createContextMenu;

	function createContextMenuForSentences(posX, posY, id) {
		isContextMenuOpen = true;
		console.log("contextmenu opened");

		$("#condec-context-menu-sentence").css({
			left : posX,
			top : posY
		});
		document.getElementById("condec-context-menu-sentence").style.zIndex = 9998;
		document.querySelector("#condec-context-menu-sentence").setAttribute('aria-hidden', 'false');

		document.getElementById("condec-context-menu-sentence-edit-item").onclick = function() {
			conDecDialog.setUpDialogForEditSentenceAction(id);
		};

		document.getElementById("condec-context-menu-sentence-delete-link-item").onclick = function() {
			var parentId = conDecTreant.findParentId(id);
			conDecAPI.deleteGenericLink(parentId, id, "i", "s", conDecAPI.setSentenceIrrelevant(id,
					conDecObservable.notify), false);
			conDecAPI.deleteGenericLink(parentId, id, "s", "s", conDecAPI.setSentenceIrrelevant(id,
					conDecObservable.notify), false);
		};

		document.getElementById("condec-context-menu-sentence-convert-item").onclick = function() {
			conDecAPI.createIssueFromSentence(id, conDecObservable.notify);
		};

		document.getElementById("condec-context-menu-sentence-irrelevant-item").onclick = function() {
			conDecAPI.setSentenceIrrelevant(id, conDecObservable.notify);
		};
	}

	ConDecContextMenu.prototype.createContextMenuForSentences = createContextMenuForSentences;

	// export ConDecContext
	global.conDecContextMenu = new ConDecContextMenu();
})(window);
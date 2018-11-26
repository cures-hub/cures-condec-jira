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

	var isContextMenuOpen = null;

	var ConDecContextMenu = function ConDecContextMenu() {
		isContextMenuOpen = false;
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
		id = id.replace("tv", "");

		var view = null;
		if(document.getElementsByClassName("aui-item detail-panel")[0] != undefined){//filtered View
 			view = document.getElementsByClassName("aui-item detail-panel")[0];
		} else if (document.getElementsByClassName("issue-view")[0] != undefined){//Issue view
			view = document.getElementsByClassName("issue-view")[0];
		}
		console.log(view)
		if(view != null){
			posY = ($("#"+id).offset().top + view.scrollTop); 
		}

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
		
		var view = null;
		if(document.getElementsByClassName("aui-item detail-panel")[0] != undefined){//filtered View
			view = document.getElementsByClassName("aui-item detail-panel")[0];
		} else if (document.getElementsByClassName("issue-view")[0] != undefined){//Issue view
			view = document.getElementsByClassName("issue-view")[0];
		}
		if(view != null){
			posY = ($("#"+id).offset().top + view.scrollTop); 
		}
		


		console.log("contextmenu opened");

		$("#condec-context-menu-sentence").css({
			left : posX,
			top : posY
		});
		id = id.replace("tv", "");
		document.getElementById("condec-context-menu-sentence").style.zIndex = 9998;
		document.querySelector("#condec-context-menu-sentence").setAttribute('aria-hidden', 'false');

		document.getElementById("condec-context-menu-sentence-edit-item").onclick = function() {
			conDecDialog.setUpDialogForEditSentenceAction(id);
		};

		document.getElementById("condec-context-menu-sentence-delete-link-item").onclick = function() {
			var parentId = conDecTreant.findParentId(id);
			conDecAPI.deleteGenericLink(parentId, id, "i", "s", conDecAPI.setSentenceIrrelevant(id, function() {
				conDecObservable.notify();
			}), false);
			conDecAPI.deleteGenericLink(parentId, id, "s", "s", conDecAPI.setSentenceIrrelevant(id, function() {
				conDecObservable.notify();
			}), false);
		};

		document.getElementById("condec-context-menu-sentence-convert-item").onclick = function() {
			conDecAPI.createIssueFromSentence(id, function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-irrelevant-item").onclick = function() {
			conDecAPI.setSentenceIrrelevant(id, function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-issue-item").onclick = function() {
			conDecAPI.changeKnowledgeTypeOfSentence(id, "Issue", function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-decision-item").onclick = function() {
			conDecAPI.changeKnowledgeTypeOfSentence(id, "Decision", function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-alternative-item").onclick = function() {
			conDecAPI.changeKnowledgeTypeOfSentence(id, "Alternative", function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-pro-item").onclick = function() {
			conDecAPI.changeKnowledgeTypeOfSentence(id, "Pro", function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-con-item").onclick = function() {
			conDecAPI.changeKnowledgeTypeOfSentence(id, "Con", function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-delete-item").onclick = function() {
			conDecAPI.deleteSentenceObject2(id, function() {
				conDecObservable.notify();
			});
		};
	}

	ConDecContextMenu.prototype.createContextMenuForSentences = createContextMenuForSentences;

	// export ConDecContextMenu
	global.conDecContextMenu = new ConDecContextMenu();
})(window);
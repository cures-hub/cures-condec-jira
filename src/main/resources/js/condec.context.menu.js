/*
 This module is responsible for showing a context menu upon right mouse click.
 
 Requires
 * conDecAPI
 * conDecDialog
 * conDecTreant

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

	ConDecContextMenu.prototype.createContextMenu = function createContextMenu(htmlElement, id, container, event) {
		isContextMenuOpen = true;
		console.log("contextmenu opened");
		// console.log(htmlElement)

		var position = getPosition(htmlElement, container, event);
		var posX = position["x"];
		var posY = position["y"];

		$("#condec-context-menu").css({
			left : posX,
			top : posY
		});
		document.getElementById("condec-context-menu").style.zIndex = 9998;
		document.querySelector("#condec-context-menu").setAttribute('aria-hidden', 'false');

		document.getElementById("condec-context-menu-create-item").onclick = function() {
			conDecDialog.showCreateDialog(id, "i");
		};

		document.getElementById("condec-context-menu-edit-item").onclick = function() {
			conDecDialog.showEditDialog(id);
		};

		document.getElementById("condec-context-menu-change-type-item").onclick = function() {
			conDecDialog.showChangeTypeDialog(id, "");
		};

		document.getElementById("condec-context-menu-link-item").onclick = function() {
			conDecDialog.showLinkDialog(id, "");
		};

		document.getElementById("condec-context-menu-delete-link-item").onclick = function() {
			var parentId = conDecTreant.findParentId(id);
			conDecDialog.showDeleteLinkDialog(id, parentId);
		};

		document.getElementById("condec-context-menu-delete-item").onclick = function() {
			conDecDialog.showDeleteDialog(id, "");
		};

		document.getElementById("condec-context-menu-set-root-item").onclick = function() {
			conDecAPI.getDecisionKnowledgeElement(id, "i", function(decisionKnowledgeElement) {
				conDecTreant.buildTreant(decisionKnowledgeElement.key, true, "");
			});
		};

		document.getElementById("condec-context-menu-open-jira-issue-item").onclick = function() {
			conDecAPI.openJiraIssue(id);
		};
	};

	ConDecContextMenu.prototype.createContextMenuForSentences = function createContextMenuForSentences(htmlElement, id,
			container, event) {
		isContextMenuOpen = true;
		console.log("contextmenu opened");
		// console.log(htmlElement)

		var position = getPosition(htmlElement, container, event);
		var posX = position["x"];
		var posY = position["y"];

		$("#condec-context-menu-sentence").css({
			left : posX,
			top : posY
		});

		document.getElementById("condec-context-menu-sentence").style.zIndex = 9998;
		document.querySelector("#condec-context-menu-sentence").setAttribute('aria-hidden', 'false');

		document.getElementById("condec-context-menu-sentence-create-item").onclick = function() {
			conDecDialog.showCreateDialog(id, "s");
		};

		document.getElementById("condec-context-menu-sentence-edit-item").onclick = function() {
			conDecDialog.setUpDialogForEditSentenceAction(id);
		};

		document.getElementById("condec-context-menu-sentence-delete-link-item").onclick = function() {
			var parentId = conDecTreant.findParentId(id);
			conDecAPI.deleteLink(parentId, id, "i", "s", conDecAPI.setSentenceIrrelevant(id, function() {
				conDecObservable.notify();
			}), false);
			conDecAPI.deleteLink(parentId, id, "s", "s", conDecAPI.setSentenceIrrelevant(id, function() {
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
			conDecAPI.changeKnowledgeType(id, "Issue", "s", function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-decision-item").onclick = function() {
			conDecAPI.changeKnowledgeType(id, "Decision", "s", function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-alternative-item").onclick = function() {
			conDecAPI.changeKnowledgeType(id, "Alternative", "s", function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-pro-item").onclick = function() {
			conDecAPI.changeKnowledgeType(id, "Pro", "s", function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-con-item").onclick = function() {
			conDecAPI.changeKnowledgeType(id, "Con", "s", function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-delete-item").onclick = function() {
			conDecDialog.showDeleteDialog(id, "s");
		};
	};

	getPosition = function getPosition(el, container, event) {
		console.log(el);
		if (container === null && event !== null) {
			return {
				x : event.pageX,
				y : event.pageY
			};
		}
		var xPosition = 0;
		var yPosition = 0;

		while (el) {
			if (el.tagName == "BODY") {
				// deal with browser quirks with body/window/document and page
				// scroll
				var xScrollPos = el.scrollLeft || document.documentElement.scrollLeft;
				var yScrollPos = el.scrollTop || document.documentElement.scrollTop;

				xPosition += (el.offsetLeft - xScrollPos + el.clientLeft);
				yPosition += (el.offsetTop - yScrollPos + el.clientTop);
			} else {
				xPosition += (el.offsetLeft - el.scrollLeft + el.clientLeft);
				yPosition += (el.offsetTop - el.scrollTop + el.clientTop);
			}

			if (container !== null && (el.id == container || el.className == container)) {
				console.log(container);
				break;
			}

			el = el.offsetParent;
		}
		return {
			x : xPosition,
			y : yPosition
		};
	}

	// export ConDecContextMenu
	global.conDecContextMenu = new ConDecContextMenu();
})(window);
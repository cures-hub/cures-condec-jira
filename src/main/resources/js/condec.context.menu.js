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
	var contextMenuNode = null;
	var contextMenuForSentencesNode = null;

	var ConDecContextMenu = function ConDecContextMenu() {
		console.log("conDecContextMenu constructor");
		isContextMenuOpen = false;
		jQueryConDec(global).blur(hideContextMenu);
		jQueryConDec(document).click(hideContextMenu);
	};

	function hideContextMenu() {
		/*
		 * @issue This event gets launched many times at the same time! Check
		 * what fires it Probably more and more onclick event handlers ges added
		 * instead of just one
		 * 
		 * @decision On click and on blur event handlers are only set in the
		 * constructor (see above).
		 */
		if (isContextMenuOpen) {
			console.log("contextmenu closed");
			if (contextMenuNode) {
				contextMenuNode.setAttribute('aria-hidden', 'true');
			}
			if (contextMenuForSentencesNode) {
				contextMenuForSentencesNode.setAttribute('aria-hidden', 'true');
			}
		}
		isContextMenuOpen = false;
	}

	/*
	 * external references: condec.treant, condec.tree.viewer
	 */
	ConDecContextMenu.prototype.createContextMenu = function createContextMenu(id, documentationLocation, event,
			container) {
		console.log("contextmenu opened");
		isContextMenuOpen = true;

		contextMenuNode = document.getElementById("condec-context-menu");
		if (!contextMenuNode) {
			console.error("contextmenu not found");
			return;
		}

		setContextMenuItemsEventHandlers(id, documentationLocation);

		var position = getPosition(event, container);
		var posX = position["x"];
		var posY = position["y"];

		$(contextMenuNode).css({
			left : posX,
			top : posY
		});

		contextMenuNode.style.zIndex = 9998; // why this number?
		contextMenuNode.setAttribute('aria-hidden', 'false');
	};

	function setContextMenuItemsEventHandlers(id, documentationLocation) {
		document.getElementById("condec-context-menu-create-item").onclick = function() {
			conDecDialog.showCreateDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-edit-item").onclick = function() {
			conDecDialog.showEditDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-change-type-item").onclick = function() {
			conDecDialog.showChangeTypeDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-link-item").onclick = function() {
			conDecDialog.showLinkDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-delete-link-item").onclick = function() {
			var parentId = conDecTreant.findParentId(id);
			conDecDialog.showDeleteLinkDialog(id, parentId);
		};

		document.getElementById("condec-context-menu-delete-item").onclick = function() {
			conDecDialog.showDeleteDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-set-root-item").onclick = function() {
			conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function(decisionKnowledgeElement) {
				conDecTreant.buildTreant(decisionKnowledgeElement.key, true, "");
			});
		};

		document.getElementById("condec-context-menu-open-jira-issue-item").onclick = function() {
			conDecAPI.openJiraIssue(id);
		};
	}

	ConDecContextMenu.prototype.createContextMenuForSentences = function createContextMenuForSentences(id,
			documentationLocation, event, container) {
		isContextMenuOpen = true;
		console.log("contextmenu opened");

		contextMenuForSentencesNode = document.getElementById("condec-context-menu-sentence");
		if (!contextMenuForSentencesNode) {
			console.error("contextmenu for sentences not found");
			return;
		}

		setContextMenuItemsSentencesEventHandlers(id, documentationLocation);

		var position = getPosition(event, container);
		var posX = position["x"];
		var posY = position["y"];

		$(contextMenuForSentencesNode).css({
			left : posX,
			top : posY
		});

		contextMenuForSentencesNode.style.zIndex = 9998;
		contextMenuForSentencesNode.setAttribute('aria-hidden', 'false');
	};

	function setContextMenuItemsSentencesEventHandlers(id, documentationLocation) {
		document.getElementById("condec-context-menu-sentence-create-item").onclick = function() {
			conDecDialog.showCreateDialog(id, documentationLocation);
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
	}

	function getPosition(event, container) {
		var element = event.target;
		if (container === null && event !== null) {
			return {
				x : event.pageX,
				y : event.pageY
			};
		}
		var xPosition = 0;
		var yPosition = 0;

		while (element) {
			if (element.tagName === "BODY") {
				// deal with browser quirks with body/window/document and page
				// scroll
				var xScrollPos = element.scrollLeft || document.documentElement.scrollLeft;
				var yScrollPos = element.scrollTop || document.documentElement.scrollTop;

				xPosition += (element.offsetLeft - xScrollPos + element.clientLeft);
				yPosition += (element.offsetTop - yScrollPos + element.clientTop);
			} else {
				xPosition += (element.offsetLeft - element.scrollLeft + element.clientLeft);
				yPosition += (element.offsetTop - element.scrollTop + element.clientTop);
			}

			if (container !== null && (element.id === container || element.className === container)) {
				break;
			}

			element = element.offsetParent;
		}
		return {
			x : xPosition,
			y : yPosition
		};
	}

	// export ConDecContextMenu
	global.conDecContextMenu = new ConDecContextMenu();
})(window);
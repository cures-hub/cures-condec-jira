/*
 This module is responsible for showing a context menu upon right mouse click.
 
 Requires
 * conDecAPI
 * conDecDialog
 * conDecTreant

 Is required by
 * conDecTreant
 * conDecTreeViewer
 * conDecVis
 * conDecEvolutionPage
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
		 * what fires it. Probably more and more onclick event handlers get
		 * added instead of just one. How can we set the event listener only
		 * once?
		 * 
		 * @decision On click and on blur event handlers are only set in the
		 * constructor (see above)!
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
	 * external references: condec.treant, condec.tree.viewer, condec.vis,
	 * condec.evolution.page
	 */
	ConDecContextMenu.prototype.createContextMenu = function createContextMenu(id, documentationLocation, event,
	        container) {
		console.log("contextmenu opened");
		console.log("element id: " + id + " element documentation location: " + documentationLocation);
		
		isContextMenuOpen = true;

		contextMenuNode = document.getElementById("condec-context-menu");
		if (!contextMenuNode) {
			console.error("contextmenu not found");
			return;
		}

		showOrHideContextMenuItems(id, documentationLocation, container);
		setContextMenuItemsEventHandlers(id, documentationLocation);

		var position = getPosition(event, container);
		$(contextMenuNode).css({
		    left : position["x"],
		    top : position["y"]
		});

		contextMenuNode.style.zIndex = 9998; // why this number?
		contextMenuNode.setAttribute('aria-hidden', 'false');
	};

	function setContextMenuItemsEventHandlers(id, documentationLocation) {
		document.getElementById("condec-context-menu-group-rename").onclick = function() {
			conDecDialog.showRenameGroupDialog(id);
		};

		document.getElementById("condec-context-menu-group-delete").onclick = function() {
			conDecDialog.showDeleteGroupDialog(id);
		};

		document.getElementById("condec-context-menu-create-item").onclick = function() {
			conDecDialog.showCreateDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-edit-item").onclick = function() {
			conDecDialog.showEditDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-change-type-item").onclick = function() {
			conDecDialog.showChangeTypeDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-change-status-item").onclick = function() {
			conDecDialog.showChangeStatusDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-issue-item").onclick = function() {
			conDecAPI.changeKnowledgeType(id, "Issue", documentationLocation, function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-assign-decision-group-item").onclick = function() {
			conDecDialog.showAssignDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-decision-item").onclick = function() {
			conDecAPI.changeKnowledgeType(id, "Decision", documentationLocation, function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-alternative-item").onclick = function() {
			conDecAPI.changeKnowledgeType(id, "Alternative", documentationLocation, function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-pro-item").onclick = function() {
			conDecAPI.changeKnowledgeType(id, "Pro", documentationLocation, function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-con-item").onclick = function() {
			conDecAPI.changeKnowledgeType(id, "Con", documentationLocation, function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-link-item").onclick = function() {
			conDecDialog.showLinkDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-delete-link-item").onclick = function() {
			conDecDialog.showDeleteLinkDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-summarized-code").onclick = function() {
			conDecDialog.showSummarizedDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-delete-item").onclick = function() {
			conDecDialog.showDeleteDialog(id, documentationLocation);
		};

		// only default documentation location
		// TODO set as root for sentences
		document.getElementById("condec-context-menu-set-root-item").onclick = function() {
			conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function(knowledgeElement) {
				conDecTreant.buildTreant(knowledgeElement.key, true, "");
			});
		};

		document.getElementById("condec-context-menu-open-jira-issue-item").onclick = function() {
			conDecAPI.openJiraIssue(id, documentationLocation);
		};

		// only for sentences
		document.getElementById("condec-context-menu-sentence-irrelevant-item").onclick = function() {
			conDecAPI.setSentenceIrrelevant(id, function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-sentence-convert-item").onclick = function() {
			conDecAPI.createIssueFromSentence(id, function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-export").onclick = function() {
			conDecDialog.showExportDialog(id, documentationLocation);
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

		if (container.includes("vis")) {
			return {
			    x : event.layerX + "px",
			    y : event.screenY + "px"
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

	// TODO Simplify, this is too complicated!
	function showOrHideContextMenuItems(id, documentationLocation, container) {
		document.getElementById("fifth-context-section").style.display = "none";
		document.getElementById("first-context-section").style.display = "block";
		if (documentationLocation === "c") {
			document.getElementById("condec-context-menu-create-item").style.display = "none";
			document.getElementById("condec-context-menu-edit-item").style.display = "none";
			document.getElementById("condec-context-menu-link-item").style.display = "none";
			document.getElementById("condec-context-menu-delete-link-item").style.display = "none";
			document.getElementById("second-context-section").style.display = "none";
			document.getElementById("third-context-section").style.display = "none";
			document.getElementById("fourth-context-section").style.display = "none";
		} else if (container.includes("tbldecisionTable")) {
			document.getElementById("condec-context-menu-create-item").style.display = "none";
			document.getElementById("condec-context-menu-link-item").style.display = "none";
			document.getElementById("condec-context-menu-delete-link-item").style.display = "none";
			document.getElementById("third-context-section").style.display = "none";
			document.getElementById("condec-context-menu-export").style.display = "none";
			document.getElementById("condec-context-menu-assign-decision-group-item").style.display = "none";
			document.getElementById("condec-context-menu-sentence-irrelevant-item").style.display = "none";
			document.getElementById("condec-context-menu-set-root-item").style.display = "none";
		} else {
			document.getElementById("condec-context-menu-create-item").style.display = "initial";
			document.getElementById("condec-context-menu-edit-item").style.display = "initial";
			document.getElementById("condec-context-menu-link-item").style.display = "initial";
			document.getElementById("condec-context-menu-delete-link-item").style.display = "initial";
			document.getElementById("second-context-section").style.display = "block";
			document.getElementById("third-context-section").style.display = "block";
			document.getElementById("fourth-context-section").style.display = "block";
		}
		if (documentationLocation !== "groups") {
			conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function(decisionKnowledgeElement) {
				var typesForStatus = new Array("issue", "alternative", "decision");
				if (!typesForStatus.includes(decisionKnowledgeElement.type.toLowerCase())) {
					document.getElementById("condec-context-menu-change-status-item").style.display = "none";
				} else {
					document.getElementById("condec-context-menu-change-status-item").style.display = "initial";
				}
			});
		}
		if (container == null || container.includes("vis")) {
			document.getElementById("condec-context-menu-set-root-item").style.display = "none";
			document.getElementById("condec-context-menu-delete-link-item").style.display = "none";
		} else if (documentationLocation !== "c" && container !== "tbldecisionTable") {
			document.getElementById("condec-context-menu-set-root-item").style.display = "initial";
			document.getElementById("condec-context-menu-delete-link-item").style.display = "initial";
		}

		if (documentationLocation === "s" && !container.includes("tbldecisionTable")) {
			document.getElementById("condec-context-menu-sentence-irrelevant-item").style.display = "initial";
			conDecAPI.isIssueStrategy(function(isEnabled) {
				if (isEnabled) {
					document.getElementById("condec-context-menu-sentence-convert-item").style.display = "initial";
				} else {
					document.getElementById("condec-context-menu-sentence-convert-item").style.display = "none";
				}
			});
			document.getElementById("condec-context-menu-set-root-item").style.display = "none";
		} else {
			document.getElementById("condec-context-menu-sentence-irrelevant-item").style.display = "none";
			document.getElementById("condec-context-menu-sentence-convert-item").style.display = "none";
			if (container !== "tbldecisionTable") {
				document.getElementById("condec-context-menu-set-root-item").style.display = "initial";
			}
		}
		/*
		 * if (documentationLocation === "i"){
		 * document.getElementById("condec-context-menu-assign-decision-group-item").style.display =
		 * "none"; }else{
		 * document.getElementById("condec-context-menu-assign-decision-group-item").style.display =
		 * "initial"; }
		 */
		if (documentationLocation === "groups") {
			document.getElementById("first-context-section").style.display = "none";
			document.getElementById("second-context-section").style.display = "none";
			document.getElementById("third-context-section").style.display = "none";
			document.getElementById("fourth-context-section").style.display = "none";
			document.getElementById("fifth-context-section").style.display = "initial";
		}
	}

	// export ConDecContextMenu
	global.conDecContextMenu = new ConDecContextMenu();
})(window);
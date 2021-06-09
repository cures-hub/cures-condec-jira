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

	var ConDecContextMenu = function () {
		console.log("conDecContextMenu constructor");
		isContextMenuOpen = false;
		jQuery(global).blur(hideContextMenu);
		jQuery(document).click(hideContextMenu);
	};

	function hideContextMenu() {
		/*
		 * @issue This event gets launched many times at the same time! Check
		 * what fires it. Probably more and more onclick event handlers get
		 * added instead of just one. How can we set the event listener only
		 * once?
		 * 
		 * @decision On click and on blur event handlers are only set in the
		 * constructor to avoid that the event listeners are added more than once!
		 */
		if (isContextMenuOpen) {
			console.log("contextmenu closed");
			if (contextMenuNode) {
				contextMenuNode.setAttribute('aria-hidden', 'true');
				contextMenuNode.removeAttribute('open');
			}
			if (contextMenuForSentencesNode) {
				contextMenuForSentencesNode.setAttribute('aria-hidden', 'true');
				contextMenuNode.removeAttribute('open');
			}
		}
		isContextMenuOpen = false;
	}

	/*
	 * external references: condec.treant, condec.tree.viewer, condec.vis,
	 * condec.evolution.page
	 */
	ConDecContextMenu.prototype.createContextMenu = function (id, documentationLocation, event, container,
			idOfTarget, documentationLocationOfTarget, linkType) {
		console.log("contextmenu opened");
		console.log("element id: " + id + " element documentation location: " + documentationLocation);
		
		isContextMenuOpen = true;

		contextMenuNode = document.getElementById("condec-context-menu");
		if (!contextMenuNode) {
			console.error("contextmenu not found");
			return;
		}

		showOrHideContextMenuItems(documentationLocation, container);
		setContextMenuItemsEventHandlers(id, documentationLocation, container, idOfTarget, documentationLocationOfTarget, linkType);

		$(contextMenuNode).css({
		    left : event.clientX,
		    top : event.clientY
		});

		contextMenuNode.style.zIndex = 9998; // why this number?
		contextMenuNode.setAttribute('aria-hidden', 'false');
		contextMenuNode.setAttribute('open', '');
	};

	function setContextMenuItemsEventHandlers(id, documentationLocation, container, idOfTarget, documentationLocationOfTarget, linkType) {
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
			conDecDialog.showLinkDialog(id, documentationLocation, idOfTarget, documentationLocationOfTarget, linkType);
		};

		document.getElementById("condec-context-menu-delete-link-item").onclick = function() {
			conDecDialog.showDeleteLinkDialog(id, documentationLocation, idOfTarget, documentationLocationOfTarget);
		};

		document.getElementById("condec-context-menu-summarized-code").onclick = function() {
			conDecDialog.showSummarizedDialog(id, documentationLocation);
		};

		document.getElementById("condec-context-menu-delete-item").onclick = function() {
			conDecDialog.showDeleteDialog(id, documentationLocation);
		};

		// set root only works in Treant currently
		document.getElementById("condec-context-menu-set-root-item").onclick = function() {
			conDecAPI.getDecisionKnowledgeElement(id, documentationLocation, function(knowledgeElement) {
				var filterSettings = conDecFiltering.getFilterSettings("treant");
				filterSettings["selectedElement"] = knowledgeElement.key;
				conDecTreant.buildTreant(filterSettings, true);
			});
		};

		document.getElementById("condec-context-menu-open-jira-issue-item").onclick = function() {
			conDecAPI.openJiraIssue(id, documentationLocation);
		};

		// only for decision knowledge in the description or comments of Jira issues
		document.getElementById("condec-context-menu-sentence-irrelevant-item").onclick = function() {
			conDecAPI.setSentenceIrrelevant(id, function() {
				conDecObservable.notify();
			});
		};

		// only for decision knowledge in the description or comments of Jira issues
		document.getElementById("condec-context-menu-sentence-convert-item").onclick = function() {
			conDecAPI.createJiraIssueFromSentence(id, function() {
				conDecObservable.notify();
			});
		};

		document.getElementById("condec-context-menu-export").onclick = function() {
			conDecDialog.showExportDialog(id, documentationLocation);
		};
	}

	/*
	 * @issue Should it be possible to change code classes using the context menu?
	 */
	function showOrHideContextMenuItems(documentationLocation, container) {
		// initial layout
		document.getElementById("first-context-section").style.display = "block";
		document.getElementById("second-context-section").style.display = "block";
		document.getElementById("third-context-section").style.display = "block";
		document.getElementById("fourth-context-section").style.display = "block";
		document.getElementById("fifth-context-section").style.display = "none";

		document.getElementById("condec-context-menu-create-item").style.display = "initial";
		document.getElementById("condec-context-menu-edit-item").style.display = "initial";
		document.getElementById("condec-context-menu-link-item").style.display = "initial";
		document.getElementById("condec-context-menu-delete-link-item").style.display = "initial";
		document.getElementById("condec-context-menu-set-root-item").style.display = "none";

		// customize context menu for documentation locations
		if (documentationLocation === "s") {
			document.getElementById("condec-context-menu-sentence-irrelevant-item").style.display = "initial";
			conDecAPI.isJiraIssueDocumentationLocationActivated(function(isEnabled) {
				if (isEnabled) {
					document.getElementById("condec-context-menu-sentence-convert-item").style.display = "initial";
				} else {
					document.getElementById("condec-context-menu-sentence-convert-item").style.display = "none";
				}
			});
		} else {
			document.getElementById("condec-context-menu-sentence-irrelevant-item").style.display = "none";
			document.getElementById("condec-context-menu-sentence-convert-item").style.display = "none";
		}
		if (documentationLocation === "groups") {
			document.getElementById("first-context-section").style.display = "none";
			document.getElementById("second-context-section").style.display = "none";
			document.getElementById("third-context-section").style.display = "none";
			document.getElementById("fourth-context-section").style.display = "none";
			document.getElementById("fifth-context-section").style.display = "initial";
		}
		
		if (documentationLocation === "c") {
			document.getElementById("condec-context-menu-create-item").style.display = "none";
			document.getElementById("condec-context-menu-edit-item").style.display = "none";
			document.getElementById("condec-context-menu-link-item").style.display = "none";
			document.getElementById("condec-context-menu-delete-link-item").style.display = "none";
			document.getElementById("second-context-section").style.display = "none";
			document.getElementById("third-context-section").style.display = "none";
			document.getElementById("fourth-context-section").style.display = "none";
			return;
		}

		// customize context menu for containers
		if (container === undefined || container === null) {
			return;
		}
		if (container.includes("tbldecisionTable")) {
			document.getElementById("condec-context-menu-create-item").style.display = "none";
			document.getElementById("condec-context-menu-link-item").style.display = "none";
			document.getElementById("condec-context-menu-delete-link-item").style.display = "none";
			document.getElementById("third-context-section").style.display = "none";
			document.getElementById("condec-context-menu-export").style.display = "none";
			document.getElementById("condec-context-menu-assign-decision-group-item").style.display = "none";
			document.getElementById("condec-context-menu-sentence-irrelevant-item").style.display = "none";
			document.getElementById("condec-context-menu-set-root-item").style.display = "none";
		}
		if (container.includes("vis")) {
			document.getElementById("condec-context-menu-set-root-item").style.display = "none";
			document.getElementById("condec-context-menu-delete-link-item").style.display = "none";
		}
		if (container.includes("treant-container")) {
			document.getElementById("condec-context-menu-set-root-item").style.display = "initial";
		}
	}

	ConDecContextMenu.prototype.addContextMenuToCommentTabPanel = function () {
        console.log("conDecContextMenu addContextMenuToCommentTabPanel");
        // ids are set in AbstractKnowledgeClassificationMacro Java class
        var comments = document.querySelectorAll('[id^="commentnode-"]');
        if (comments) {
            for (i = 0; i < comments.length; i++) {
                comments[i].addEventListener('contextmenu', function(event) {
					event.preventDefault();
                    conDecContextMenu.createContextMenu(this.id.split("-")[1], "s", event, null, JIRA.Issue.getIssueId(), "i");
                });
            }
        }
    };

	global.conDecContextMenu = new ConDecContextMenu();
})(window);
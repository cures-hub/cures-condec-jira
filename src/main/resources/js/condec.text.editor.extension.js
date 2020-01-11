/*
 This file manages the extension of the rich text editor for manual classification of text as decision knowledge.
 It adds five menu items to the editor to insert tags of the following pattern:
 {knowledge type} text {knowledge type}.
 
 Requires
 * jquery
 * jira/editor/registry
 * jira/util/formatter (currently not used)
 */
require([ "jquery", "jira/util/formatter", "jira/editor/registry" ], function($, formatter, editorRegistry) {

	var DEFAULT_PLACEHOLDER = "knowledge element";
	var KNOWLEDGE_TYPES = [ "Issue", "Alternative", "Decision", "Pro", "Con" ];

	editorRegistry.on("register", function(entry) {
		var $otherDropdown = $(entry.toolbar).find(".wiki-edit-other-picker-trigger");

		$otherDropdown.one("click", function(dropdownClickEvent) {

			conDecAPI.isActivated(function(isEnabled) {
				if (!isEnabled) {
					return;
				}
				var speechItem = getDropDownContent(dropdownClickEvent).querySelector(".wiki-edit-speech-item");

				var menuItems = [ speechItem ];

				for (var i = 0; i < KNOWLEDGE_TYPES.length; i++) {
					menuItems.push($(getHTMLListItem(KNOWLEDGE_TYPES[i])));
					menuItems[i + 1].insertAfter(menuItems[i]);
					menuItems[i + 1].on("click", {
						type : KNOWLEDGE_TYPES[i].toLowerCase()
					}, function(event) {
						entry.applyIfTextMode(function() {
							addWikiMarkup(entry, event.data.type);
						});
						entry.applyIfTextMode(function() {
							addRenderedContent(entry, event.data.type);
						});
					});
				}

				entry.onUnregister(function cleanup() {
					for (var i = 0; i < KNOWLEDGE_TYPES.length; i++) {
						menuItems[i + 1].remove();
					}
				});
			});
		});
	});

	function getHTMLListItem(knowledgeType) {
		return "<li><a href='#' class='li-" + knowledgeType + "-dropdown-entry' data-operation='" + knowledgeType
		        + "'>" + knowledgeType + "</a></li>";
	}

	function addWikiMarkup(entry, knowledgeType) {
		var wikiEditor = $(entry.textArea).data("wikiEditor");
		var content = wikiEditor.manipulationEngine.getSelection().text || DEFAULT_PLACEHOLDER;
		wikiEditor.manipulationEngine.replaceSelectionWith("{" + knowledgeType + "}" + content + "{" + knowledgeType
		        + "}");
	}

	function addRenderedContent(entry, knowledgeType) {
		entry.rteInstance.then(function(rteInstance) {
			var tinyMCE = rteInstance.editor;
			if (tinyMCE && !tinyMCE.isHidden()) {
				var content = tinyMCE.selection.getContent() || DEFAULT_PLACEHOLDER;
				tinyMCE.selection.setContent("{" + knowledgeType + "}" + content + "{" + knowledgeType + "}");
			}
		});
	}

	function getDropDownContent(dropdownClickEvent) {
		var dropdownContentId = dropdownClickEvent.currentTarget.getAttribute("aria-owns");
		return document.getElementById(dropdownContentId);
	}
});
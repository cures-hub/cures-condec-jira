/*
 *This class manages the insertion of knowledge types into the rich text exitor. Also the transition between stlyed (Rich text) and plan text with taggs.
 */
require([ "jquery", "jira/util/formatter", "jira/editor/registry" ], function($, formatter, editorRegistry) {
	var DEFAULT_PLACEHOLDER = "knowledge element";
	
	editorRegistry.on('register', function(entry) {
		var $otherDropdown = $(entry.toolbar).find('.wiki-edit-other-picker-trigger');

		$otherDropdown.one('click', function(dropdownClickEvent) {

			var speechItem = getDropDownContent(dropdownClickEvent).querySelector('.wiki-edit-speech-item');
			
			var issueItem = $(getHTMLListItem("Issue"));
			issueItem.insertAfter(speechItem);
			issueItem.on('click', function() {
				entry.applyIfTextMode(addWikiMarkup(entry, "issue"));
				entry.applyIfTextMode(addRenderedContent(entry, "F2F5A9", "issue"));
			});

			var alternativeItem = $(getHTMLListItem("Alternative")).insertAfter(issueItem).on('click', function() {
				entry.applyIfTextMode(addWikiMarkup(entry, "alternative"));
				entry.applyIfVisualMode(addRenderedContent(entry, "f1ccf9", "alternative"));
			});

			var decisionItem = $(getHTMLListItem("Decision")).insertAfter(alternativeItem).on('click', function() {
				entry.applyIfTextMode(addWikiMarkup(entry, "decision"));
				entry.applyIfVisualMode(addRenderedContent(entry, "c5f2f9", "decision"));
			});

			var proItem = $(getHTMLListItem("Pro")).insertAfter(decisionItem).on('click', function() {
				entry.applyIfTextMode(addWikiMarkup(entry, "pro"));
				entry.applyIfVisualMode(addRenderedContent(entry, "b9f7c0", "pro"));
			});

			var conItem = $(getHTMLListItem("Con")).insertAfter(proItem).on('click', function() {
				entry.applyIfTextMode(addWikiMarkup(entry, "con"));
				entry.applyIfVisualMode(addRenderedContent(entry, "ffdeb5", "con"));
			});

			entry.onUnregister(function cleanup() {
				issueItem.remove();
				alternativeItem.remove();
				decisionItem.remove();
				proItem.remove();
				conItem.remove();
			});
		});
	});

	function getHTMLListItem(knowledgeType) {
		return "<li><a href='#' class='li-" + knowledgeType + "-dropdown-entry' data-operation='" + knowledgeType
				+ "'>" + knowledgeType + "</a></li>";
	}

	function addWikiMarkup(entry, knowledgeType) {
		var wikiEditor = $(entry.textArea).data('wikiEditor');
		var content = wikiEditor.manipulationEngine.getSelection().text || DEFAULT_PLACEHOLDER;
		wikiEditor.manipulationEngine.replaceSelectionWith("{" + knowledgeType + "}" + content + "{" + knowledgeType
				+ "}");
	}

	function addRenderedContent(entry, knowledgeTypeColor, knowledgeType) {
		entry.rteInstance.then(function(rteInstance) {
			var tinyMCE = rteInstance.editor;
			if (tinyMCE && !tinyMCE.isHidden()) {
				var content = tinyMCE.selection.getContent() || DEFAULT_PLACEHOLDER;
				tinyMCE.selection.setContent('{' + knowledgeType + '}' + content + '{' + knowledgeType + '}');
			}
		});
	}

	function getDropDownContent(dropdownClickEvent) {
		var dropdownContentId = dropdownClickEvent.currentTarget.getAttribute('aria-owns');
		return document.getElementById(dropdownContentId);
	}
});
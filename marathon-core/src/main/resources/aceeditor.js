function ACEEditor() {
	this.ace = ace.edit('editor');
	this.themelist = ace.require("ace/ext/themelist");
	this.modelist = ace.require("ace/ext/modelist")
	this.addKeyboardMenu();
	var self = this ;
	this.ace.getSession().selection.on('changeCursor', function(evt) {
		var cursor = self.ace.selection.getCursor();
		java.onCursorChange(cursor.row + 1, cursor.column + 1);
		var selection = self.ace.selection;
		var dot, mark ;
		if(selection.isEmpty()) {
			var cursor = self.ace.selection.getCursor();
			dot = mark = self.ace.getSession().getDocument().positionToIndex(cursor);
		} else {
			var range = selection.getRange();
			if(selection.isBackwards()) {
				dot = self.ace.getSession().getDocument().positionToIndex(range.start);
				mark = self.ace.getSession().getDocument().positionToIndex(range.end);
			} else {
				dot = self.ace.getSession().getDocument().positionToIndex(range.end);
				mark = self.ace.getSession().getDocument().positionToIndex(range.start);
			}
		}
		java.fireCaretUpdate(dot, mark);
	});
	this.ace.on('change', function(e) {
		if(self.fireChangeEvent)
			java.fireContentChangeEvent();
	});
	this.ace.on('input', function (e) {
	  java.setUndoManagerStatus();
	});
	this.ace.setDefaultHandler('guttermousedown', function(e) {});
	this.ace.on('gutterdblclick', function(e) {
        var gutterRegion = self.ace.renderer.$gutterLayer.getRegion(e);
        if(gutterRegion === 'markers') {
			var row = e.getDocumentPosition().row;
			java.gutterDblClicked(row);
        }
	});
	this.ace.setScrollSpeed(0.05);
	this.ace.setShowPrintMargin(false);
	this.ace.on('copy', function(text) { java._copy(text); });
	this.ace.on('cut', function(text) { java._cut(text); });
	this.ace.on('paste', function(text) { java._paste(JSON.stringify(text)); });
}

ACEEditor.prototype.toString = function() {
	return "ACE Editor";
}

ACEEditor.prototype.addKeyboardMenu = function() {
	var editor = this.ace;
    editor.commands.addCommand({
        name: "showKeyboardShortcutsX",
        bindKey: {win: "Ctrl-Shift-h", mac: "Command-Shift-h"},
        exec: function(editor) {
            ace.config.loadModule("ace/ext/keybinding_menu", function(module) {
                module.init(editor);
                editor.showKeyboardShortcuts()
            })
        }
    })
}

ACEEditor.prototype.setMode = function(json) {
	this.ace.getSession().setMode('ace/mode/' + json.mode);
}

ACEEditor.prototype.getContent = function(json) {
	return { content: this.ace.getValue() };
}

ACEEditor.prototype.setContent = function(json) {
	var line = json.line || 1 ;
	var content = json.content ;
	this.fireChangeEvent = false
	this.ace.setValue(content, line);
	this.fireChangeEvent = true
	this.ace.gotoLine(0, 0);
	return {};
}

ACEEditor.prototype.setOptions = function(json) {
	for(var key in json) {
		if(key === 'showLineNumbers') {
			this.ace.renderer.setOption('showLineNumbers', json.showLineNumbers);
		} else if(key === 'showInvisibles') {
			this.ace.setShowInvisibles(json.showInvisibles);
		} else if(key === 'firstLineNumber') {
			this.ace.setOption('firstLineNumber', json.firstLineNumber);
		} else if(key === 'tabSize') {
			this.ace.getSession().setTabSize(json.tabSize);
		} else if(key === 'tabConversion') {
			this.ace.getSession().setUseSoftTabs(json.tabConversion);
		} else if(key === 'overwrite') {
			this.ace.getSession().setOverwrite(json.overwrite);
		} else if(key === 'theme') {
			this.ace.setTheme(json.theme);
		} else if(key === 'keyboardHandler') {
			this.setKeyboardHandler(json.keyboardHandler);
		} else if(key === 'fontSize') {
			this.ace.setFontSize(json.fontSize);
		} else {
			console.log('Warning: setOptions - Unknown option ' + key);
		}
	}
}

ACEEditor.prototype.getOptions = function(json) {
	return {
		showLineNumbers: this.ace.renderer.getOption('showLineNumbers'),
		showInvisibles: this.ace.getShowInvisibles(),
		tabSize: this.ace.getSession().getTabSize(),
		tabConversion: this.ace.getSession().getUseSoftTabs(),
		overwrite: this.ace.getSession().getOverwrite(),
		theme: this.getTheme().theme,
		keyboardHandler: this.getKeyboardHandler().handler,
		fontSize: this.getFontSize().fontSize
	}
}

ACEEditor.prototype.getLineCount = function(json) {
	return { count: this.ace.getSession().getDocument().getLength() };
}

ACEEditor.prototype.setBreakPoint = function(json) {
	this.ace.getSession().setBreakpoint(json.row, 'breakpoint');	
}

ACEEditor.prototype.removeAllBreakPoints = function(json) {
	this.ace.getSession().clearBreakpoints();	
}

ACEEditor.prototype.setCaretPosition = function(json) {
	if(json.position === 0) {
		this.ace.gotoLine(0, 0);
	} else {
		var position = this.ace.getSession().getDocument().indexToPosition(json.position);
		// Looks like a bug with ACE.
		this.ace.gotoLine(position.row + 1, position.column);
	}
}

ACEEditor.prototype.setCaretLine = function(json) {
	this.ace.gotoLine(json.row + 1, 0);
}

ACEEditor.prototype.getCaretPosition = function(json) {
	var cursor = this.ace.selection.getCursor();
	var position = this.ace.getSession().getDocument().positionToIndex(cursor);
	return { position: position }
}

ACEEditor.prototype.getSelectionStart = function(json) {
	var selection = this.ace.selection;
	if(selection.isEmpty()) {
		return this.getCaretPosition(json);
	}
	var range = selection.getRange();
	var position = this.ace.getSession().getDocument().positionToIndex({ row: range.start.row, column: range.start.column });
	return { position: position };
}

ACEEditor.prototype.getSelectionEnd = function(json) {
	var selection = this.ace.selection;
	if(selection.isEmpty()) {
		return this.getCaretPosition(json);
	}
	var range = selection.getRange();
	var position = this.ace.getSession().getDocument().positionToIndex({ row: range.end.row, column: range.end.column });
	return { position: position };
}

ACEEditor.prototype.getSelection = function(json) {
	var selection = this.ace.selection;
	if(selection.isEmpty()) {
		var cursor = this.ace.selection.getCursor();
		var position = this.ace.getSession().getDocument().positionToIndex(cursor);
		return { start: position, end: position };
	}
	var range = selection.getRange();
	var start = this.ace.getSession().getDocument().positionToIndex({ row: range.start.row, column: range.start.column });
	var end = this.ace.getSession().getDocument().positionToIndex({ row: range.end.row, column: range.end.column });
	return { start: start, end: end };
}

ACEEditor.prototype.getCaretLine = function(json) {
	var cursor = this.ace.selection.getCursor();
	return { row: cursor.row, column: cursor.column };
}

ACEEditor.prototype.getLineOfOffset = function(json) {
	var position = this.ace.getSession().getDocument().indexToPosition(json.offset);
	return { line: position.row  };
}

ACEEditor.prototype.getLineStartOffset = function(json) {
	var index = this.ace.getSession().getDocument().positionToIndex({ row: json.line, column: 0 });
	return { offset: index};
}

ACEEditor.prototype.getLineEndOffset = function(json) {
	var index = this.ace.getSession().getDocument().positionToIndex({ row: json.line + 1, column: 0 });
	return { offset: index};
}

ACEEditor.prototype.isOverwriteEnabled = function(json) {
	var overwrite = this.ace.getSession().getOverwrite();
	return { overwrite:  overwrite } ;
}

ACEEditor.prototype.highlightLine = function(json) {
    this.ace.getSession().removeGutterDecoration(this.currentLine || 0, 'current_line');
    this.ace.getSession().addGutterDecoration(json.row, 'current_line');
    this.currentLine = json.row ;
}

ACEEditor.prototype.startInserting = function(json) {
	var cursor = this.ace.selection.getCursor();
	this.insertion = new Range(cursor.row, cursor.column, cursor.row, cursor.column);
}

ACEEditor.prototype.stopInserting = function(json) {
	this.insertion = null ;
}

ACEEditor.prototype.isEditable = function(json) {
	return { editable: !this.ace.getReadOnly() };
}

ACEEditor.prototype.setEditable = function(json) {
	this.ace.setReadOnly(!json.editable);
}

ACEEditor.prototype.insertScript = function(json) {
	if(this.insertion === null)
		return;
	console.log(this.insertion.toString());
	this.ace.getSession().replace(this.insertion, json.script);
	var cursor = this.ace.selection.getCursor();
	this.insertion.setEnd(cursor.row, cursor.column);
}

ACEEditor.prototype.cut = function(json) {
	var text = this.ace.getSelectedText();
	this.ace.execCommand("cut");
	return { text: text } ; 
}

ACEEditor.prototype.copy = function(json) {
	var text = this.ace.getSelectedText();
	this.ace.execCommand("copy");
	return { text: text } ; 
}

ACEEditor.prototype.paste = function(json) {
	this.ace.execCommand("paste", json.text);
}

ACEEditor.prototype.find = function(json) {
	this.ace.execCommand("find");
}

ACEEditor.prototype.replace = function(json) {
	this.ace.execCommand("replace");
}

ACEEditor.prototype.undo = function(json) {
	this.ace.execCommand("undo");
}

ACEEditor.prototype.redo = function(json) {
	this.ace.execCommand("redo");
}

ACEEditor.prototype.canundo = function(json) {
	var um = this.ace.getSession().getUndoManager();
	return { value: um.hasUndo() }
}

ACEEditor.prototype.canredo = function(json) {
	var um = this.ace.getSession().getUndoManager();
	return { value: um.hasRedo() }
}

ACEEditor.prototype.getThemes = function(json) {
	return this.themelist;
}

ACEEditor.prototype.getTheme = function(json) {
	var theme = this.ace.getTheme();
	if(!theme)
		theme = 'ace/theme/chrome';
	return { theme: theme };
}

ACEEditor.prototype.setTheme = function(json) {
	this.ace.setTheme(json.theme);
}

ACEEditor.prototype.getKeyboardHandler = function(json) {
	var handler = this.ace.$keybindingId;
	if(handler === 'ace/keyboard/vim')
		handler = 'vim' ;
	else if(handler === 'ace/keyboard/emacs')
		handler = 'emacs';
	else
		handler = 'ace' ;
	return { handler: handler };
}

ACEEditor.prototype.setKeyboardHandler = function(json) {
	var handler = json.handler ;
	if(handler === 'emacs')
		handler = 'ace/keyboard/emacs';
	else if(handler === 'vim')
		handler = 'ace/keyboard/vim';
	else
		handler = null ;
	this.ace.setKeyboardHandler(handler);
}

ACEEditor.prototype.setFontSize = function(json) {
	this.ace.setFontSize(parseInt(json.fontSize));
}

ACEEditor.prototype.requestFocus = function(json) {
    this.ace.focus();
}

ACEEditor.prototype.getFontSize = function(json) {
	var fontSize = parseInt(this.ace.getFontSize());
	if(!fontSize)
		fontSize = '13px';
	else
		fontSize = '' + fontSize + 'px';
	return { fontSize: fontSize };
}

ACEEditor.prototype.hookKeyBindings = function(json) {
	var editor = this.ace;
	json.keys.forEach(function(command) {
		editor.commands.addCommand({
			name: command.name,
			bindKey: {
				win: command.key.replace('^', 'Ctrl'),
				mac: command.key.replace('^', 'Command'),
				sender: 'editor|cli'
			},
			exec: function(env, args, request) {
				java.onCommand(command.key);
			}
		});
	});
}

ACEEditor.prototype.executeScript = function(json) {
	var o = JSON.parse(json);
	var method = o.method ;
	var params = JSON.parse(o.data) ;
	var ret = this[method].call(this, params);
	if(typeof ret === 'object')
		return JSON.stringify(ret);
	return '{}';
}

ACEEditor.prototype.echo = function(json) {
	console.log("Got: `" + json + "`");
}

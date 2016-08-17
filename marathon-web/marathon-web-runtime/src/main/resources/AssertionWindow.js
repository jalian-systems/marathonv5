Marathon.AssertionWindow = function(assertCallback, waitCallback) {
	this.assertCallback = assertCallback;
	this.waitCallback = waitCallback;
	var _this = this;
	document.addEventListener('click', function(evt) {
		_this.handleClickEvent(evt);
	}, true);
}

Marathon.AssertionWindow.prototype.handleClickEvent = function(evt) {
	if (!evt.target) {
		console.log("A click event generated without target!!!", evt);
		return;
	}
	if (evt.which === 1 && evt.altKey && (evt.metaKey || evt.ctrlKey)) {
		if (evt.target.matches('#popUpWindow-top *')) {
			return;
		}
		if (!this.popup || !this.popup.isOpen) {
			this.showWindow(evt.target, evt.offsetX, evt.offsetY);
		}
		this.set(evt.target);
		evt.preventDefault();
		evt.stopPropagation();
		return;
	}
	if (!evt.target.matches('#popUpWindow-top *')) {
		this.hideWindow();
		return;
	}
	if (evt.target.matches('#popUpWindow-top table td')) {
		var prop = evt.target.innerText;
		var value = this.properties[prop] || 'ERROR';
		document.querySelector('.value-p').innerHTML = value;
		this.currentSelection = {
			property : prop,
			value : value
		};
		this.valueSelection = false;
	} else if (evt.target.matches('#popUpWindow-top .parent-b')) {
		var tagName = this.currentElement.tagName.toLowerCase();
		if (tagName === 'body' || tagName === 'html')
			return;
		this.set(this.currentElement.parentElement);
	} else if (evt.target.matches('#popUpWindow-top .next-b')) {
		var allElements = document
				.getElementsByTagName(this.currentElement.tagName);
		var nextElement = null;
		for (var i = 0; i < allElements.length; i++) {
			if (allElements[i] === this.currentElement
					&& i < allElements.length - 1
					&& !allElements[i + 1].matches('#popUpWindow-top *')
					&& !allElements[i + 1].matches('#popUpWindow-top'))
				nextElement = allElements[i + 1];
		}
		if (nextElement === null)
			return;
		this.set(nextElement);
	} else if (evt.target.matches('#popUpWindow-top .prev-b')) {
		var allElements = document
				.getElementsByTagName(this.currentElement.tagName);
		var prevElement = null;
		for (var i = 0; i < allElements.length; i++) {
			if (allElements[i] === this.currentElement && i > 0
					&& !allElements[i - 1].matches('#popUpWindow-top *')
					&& !allElements[i - 1].matches('#popUpWindow-top'))
				prevElement = allElements[i - 1];
		}
		if (prevElement === null)
			return;
		this.set(prevElement);
	} else if (evt.target.matches('#popUpWindow-top .assert-b')) {
		if (this.valueSelection)
			this.currentSelection.selection = this.valueSelection;
		this.assertCallback && this.currentSelection
				&& this.assertCallback(this.currentElement, this.currentSelection);
	} else if (evt.target.matches('#popUpWindow-top .wait-b')) {
		if (this.valueSelection)
			this.currentSelection.selection = this.valueSelection;
		this.waitCallback && this.currentSelection
				&& this.waitCallback(this.currentElement, this.currentSelection);
	}
}

Marathon.AssertionWindow.prototype.getProperties = function(target) {
	var propmap = {};
	if (target.attributes) {
		for (var i = 0, atts = target.attributes, n = atts.length; i < n; i++) {
			propmap[atts[i].nodeName] = atts[i].nodeValue;
		}
	}
	propmap['tag_name'] = target.tagName.toLowerCase();
	var text = target.innerText.trim();
	if (text)
		propmap['text'] = text;
	return propmap;
}

Marathon.AssertionWindow.prototype.set = function(target) {
	this.currentSelection = false;
	this.valueSelection = false;
	this.removeOutline();
	this.prevOutline = target.style.outline;
	target.style.outline = '2px solid #f00';
	var props = this.getProperties(target);
	var buttonStyle = ' style="height: 30px;'
			+ 'line-height: 28px;'
			+ 'padding: 0 12px 2px;'
			+ 'margin: 3px 3px 3px 3px;'
			+ 'vertical-align: baseline;'
			+ 'background: #0085ba;'
			+ 'border-color: #0073aa #006799 #006799;'
			+ '-webkit-box-shadow: 0 1px 0 #006799;'
			+ 'box-shadow: 0 1px 0 #006799;'
			+ 'color: #fff;'
			+ 'text-decoration: none;'
			+ 'text-shadow: 0 -1px 1px #006799,1px 0 1px #006799,0 1px 1px #006799,-1px 0 1px #006799;'
			+ 'display: inline-block;' + 'font-size: 13px;'
			+ 'cursor: pointer;' + 'border-width: 1px;'
			+ 'border-style: solid;' + '-webkit-appearance: none;'
			+ '-webkit-border-radius: 3px;' + 'border-radius: 3px;'
			+ 'white-space: nowrap;' + '-webkit-box-sizing: border-box;'
			+ '-moz-box-sizing: border-box;' + 'box-sizing: border-box;'
			+ 'font-weight: inherit;' + 'text-align: left;" ';
	var html = '<div style="padding-bottom:5px; margin:0;text-align:center;">';
	html += '<input ' + buttonStyle
			+ 'type="button" class="parent-b" value="Parent"/>';
	html += '<input ' + buttonStyle
			+ 'type="button"  class="next-b" value="Next ' + target.tagName
			+ '"/>';
	html += '<input ' + buttonStyle
			+ 'type="button"  class="prev-b" value="Previous ' + target.tagName
			+ '"/>';
	html += '</div>';
	html += '<table style="width:100%;min-height:80px;">';
	for ( var prop in props) {
		if (props.hasOwnProperty(prop))
			html += '<tr style="background:#dee8eb"><td style="cursor:pointer;">'
					+ prop + '</td></tr>';
	}
	html += '</table>';
	html += '<div style="width:100%;min-height:80px;">'
			+ '<p style="background:white;min-height:80px;" class="value-p"></p>'
			+ '<div style="text-align:right"><input '
			+ buttonStyle
			+ 'class="assert-b" type="button" value="Insert Assertion"/><input '
			+ buttonStyle
			+ 'type="button" class="wait-b" value="Insert Wait"/><div>'
			+ '</div>';
	this.popup.setContentHTML(html);
	this.popup.setTitle($marathon.getSuggestedName(target));
	var self = this;
	document.querySelector('#popUpWindow-top .value-p').addEventListener(
			'mouseup',
			function() {
				var text = "";
				if (window.getSelection) {
					text = window.getSelection().toString();
				} else if (document.selection
						&& document.selection.type != "Control") {
					text = document.selection.createRange().text;
				}
				self.valueSelection = text.trim();
				console.log("Selection Length ", self.valueSelection.length);
			});
	this.properties = props;
	this.currentElement = target;
}

Marathon.AssertionWindow.prototype.showWindow = function(target, offsetX, offsetY) {
	var _this = this;
	if (!this.popup)
		this.popup = new Marathon.PopUpWindow('CSS Styling', {
			id : 'popUpWindow-top',
			isResizable : true,
			onClose : function() {
				_this.removeOutline();
			}
		});
	this.popup.positionTo(target, offsetX, offsetY);
	this.popup.open();
}

Marathon.AssertionWindow.prototype.removeOutline = function() {
	if (this.currentElement) {
		this.currentElement.style.outline = this.prevOutline;
	}
}

Marathon.AssertionWindow.prototype.hideWindow = function() {
	this.removeOutline();
	if (this.popup && this.popup.isOpen) {
		this.popup.close();
	}
}

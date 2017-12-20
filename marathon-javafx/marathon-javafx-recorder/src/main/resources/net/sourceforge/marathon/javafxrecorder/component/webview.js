Element.prototype.matches = Element.prototype.matchesSelector
		|| Element.prototype.mozMatchesSelector
		|| Element.prototype.msMatchesSelector
		|| Element.prototype.oMatchesSelector
		|| Element.prototype.webkitMatchesSelector ;

if (!Element.prototype.matches) {
	Element.prototype.matches = function(s) {
				var matches = (this.document || this.ownerDocument)
						.querySelectorAll(s), i = matches.length;
				while (--i >= 0 && matches.item(i) !== this) {
				}
				return i > -1;
			};
}

if (!Element.prototype.css) {
	Element.prototype.css = function() {
		var path = WebView.cssPath(this, true);
		var count = document.querySelectorAll(path).length;
		if(count > 1)
			return WebView.cssPath(this, false);
		return path;
	}
}

function WebView() {
	var _this = this;
	this.rawRecording = 'false' ;
	document.addEventListener('change', function(evt) {
		if(_this.paused)
			return;
		_this.handleChangeEvent(evt);
	}, true);
	document.addEventListener('click', function(evt) {
		if(_this.paused)
			return;
		_this.handleClickEvent(evt);
	}, true);
	document.addEventListener('keypress', function(evt) {
		if(_this.paused)
			return;
		_this.handleKeyPressEvent(evt);
	}, true);

	document.addEventListener('mousemove', function(evt) {
		if(_this.paused)
			return;
		marathon_recorder.record_assertion_selector(evt.target.css());
	}, true);
}


WebView.prototype.handleChangeEvent = function(evt) {
	if(!evt.isTrusted)
		return;
	if(this.rawRecording === "true")
		return;
	var target = evt.target;
	var v = this.value(target);
	if(v != null)
		marathon_recorder.record_select(target.css(), v);
}

WebView.prototype.shouldIgnoreClick = function(target) {
	var values = [ 'input[type="text"]', 'input[type="password"]', 'input[type="color"]',
	               'input[type="date"]', 'input[type="datetime"]', 'input[type="datetime-local"]',
	               'input[type="number"]', 'input[type="range"]', 'input[type="search"]',
	               'input[type="tel"]', 'input[type="time"]', 'input[type="url"]',
	               'input[type="week"]', 'input[type="email"]', 'input[type="file"]',
	               'input[type="checkbox"]', 'input[type="radio"]',
	               'input[type="month"]', 'select', 'option', 'textarea',
					function() { return this.matches('label') && this.getAttribute('for') !== null ;},
					function() { return this.matches('input') && this.getAttribute('type') === null ;}];
	
	var matched = values.find(function(v) { return typeof v === 'function' ? v.call(target) : target.matches(v); });
	if(matched)
		return true;
	return false;
}

WebView.prototype.handleKeyPressEvent = function(evt) {
	if(!evt.isTrusted || this.rawRecording === "false")
		return;
	var modifier = "";
	if(evt.altKey)
		modifier = modifier + "Alt+" ;
	if(evt.ctrlKey)
		modifier = modifier + "Ctrl+" ;
	if(evt.shiftKey)
		modifier = modifier + "Shift+" ;
	if(evt.metaKey)
		modifier = modifier + "Meta+" ;
	if(modifier.length > 0)
		modifier = modifier.slice(0, modifier.length - 1)
	if(modifier.length > 0)
		$WebView.postEvent(evt.target, { type: 'key_raw', keyCode: evt.key.toUpperCase(), modifiersEx: modifier});
	else
		$WebView.postEvent(evt.target, { type: 'key_raw', keyChar: evt.key});
}


WebView.prototype.handleClickEvent = function(evt) {
	if(!evt.isTrusted)
		return;
	var target = evt.target;
	if(this.shouldIgnoreClick(target) === true && this.rawRecording === "false")
		return;
	marathon_recorder.record_click(target.css())
}

WebView.prototype.value = function(target) {
	var values = [ 'input[type="text"]', 'input[type="password"]', 'input[type="color"]',
	               'input[type="date"]', 'input[type="datetime"]', 'input[type="datetime-local"]',
	               'input[type="number"]', 'input[type="range"]', 'input[type="search"]',
	               'input[type="tel"]', 'input[type="time"]', 'input[type="url"]',
	               'input[type="week"]', 'input[type="email"]', 'input[type="file"]',
	               'input[type="month"]',
	               function() { return this.matches('input') && this.getAttribute('type') === null ;}];
		
	var matched = values.find(function(v) { return typeof v === 'function' ? v.call(target) : target.matches(v); });
	if(matched)
		return target.value;

	if(target.matches('input[type="checkbox"]') || target.matches('input[type="radio"]'))
		return "" + target.checked;
	
	if(target.matches('select')) {
	 var result = [];	   
	   for(var i = 0; i < target.options.length; i++) {
	      if(target.options[i].selected)
	      	result.push(target.options[i].text);
	   }
	   return JSON.stringify(result);
	}
	
	if(target.matches('textarea'))		
		return target.value;	
	
	console.log('Unhandled getValue', target);
	return null;
}

WebView.prototype.setRawRecording = function(obj) {
	this.rawRecording = obj.value ;
}

WebView.prototype.setRecordingPause = function(obj) {
	this.paused = obj.value ;
}

$webview = new WebView();

/*
 * Copyright (C) 2011 Google Inc.  All rights reserved.
 * Copyright (C) 2007, 2008 Apple Inc.  All rights reserved.
 * Copyright (C) 2008 Matt Lilek <webkit@mattlilek.com>
 * Copyright (C) 2009 Joseph Pecoraro
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 3.  Neither the name of Apple Computer, Inc. ("Apple") nor the names of
 *     its contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY APPLE AND ITS CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL APPLE OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @param {!WebInspector.DOMNode} node
 * @param {boolean=} optimized
 * @return {string}
 */
WebView.cssPath = function(node, optimized)
{
    if (node.nodeType !== Node.ELEMENT_NODE)
        return "";

    var steps = [];
    var contextNode = node;
    while (contextNode) {
        var step = WebView._cssPathStep(contextNode, !!optimized, contextNode === node);
        if (!step)
            break; // Error - bail out early.
        steps.push(step);
        if (optimized && step.optimized)
            break;
        contextNode = contextNode.parentNode;
    }

    steps.reverse();
    return steps.join(" > ");
}

/**
 * @param {!WebInspector.DOMNode} node
 * @param {boolean} optimized
 * @param {boolean} isTargetNode
 * @return {?WebView.DOMNodePathStep}
 */
WebView._cssPathStep = function(node, optimized, isTargetNode)
{
    if (node.nodeType !== Node.ELEMENT_NODE)
        return null;

    var id = node.getAttribute("id");
    if (optimized) {
        if (id)
            return new WebView.DOMNodePathStep(idSelector(id), true);
        var nodeNameLower = node.tagName.toLowerCase().toLowerCase();
        if (nodeNameLower === "body" || nodeNameLower === "head" || nodeNameLower === "html")
            return new WebView.DOMNodePathStep(node.tagName.toLowerCase(), true);
    }
    var nodeName = node.tagName.toLowerCase();

    if (id)
        return new WebView.DOMNodePathStep(nodeName + idSelector(id), true);
    var parent = node.parentNode;
    if (!parent || parent.nodeType === Node.DOCUMENT_NODE)
        return new WebView.DOMNodePathStep(nodeName, true);

    /**
     * @param {!WebInspector.DOMNode} node
     * @return {!Array.<string>}
     */
    function prefixedElementClassNames(node)
    {
        var classAttribute = node.getAttribute("class");
        if (!classAttribute)
            return [];

        return classAttribute.split(/\s+/g).filter(Boolean).map(function(name) {
            // The prefix is required to store "__proto__" in a object-based map.
            return "$" + name;
        });
    }

    /**
     * @param {string} id
     * @return {string}
     */
    function idSelector(id)
    {
        return "#" + escapeIdentifierIfNeeded(id);
    }

    /**
     * @param {string} ident
     * @return {string}
     */
    function escapeIdentifierIfNeeded(ident)
    {
        if (isCSSIdentifier(ident))
            return ident;
        var shouldEscapeFirst = /^(?:[0-9]|-[0-9-]?)/.test(ident);
        var lastIndex = ident.length - 1;
        return ident.replace(/./g, function(c, i) {
            return ((shouldEscapeFirst && i === 0) || !isCSSIdentChar(c)) ? escapeAsciiChar(c, i === lastIndex) : c;
        });
    }

    /**
     * @param {string} c
     * @param {boolean} isLast
     * @return {string}
     */
    function escapeAsciiChar(c, isLast)
    {
        return "\\" + toHexByte(c) + (isLast ? "" : " ");
    }

    /**
     * @param {string} c
     */
    function toHexByte(c)
    {
        var hexByte = c.charCodeAt(0).toString(16);
        if (hexByte.length === 1)
            hexByte = "0" + hexByte;
        return hexByte;
    }

    /**
     * @param {string} c
     * @return {boolean}
     */
    function isCSSIdentChar(c)
    {
        if (/[a-zA-Z0-9_-]/.test(c))
            return true;
        return c.charCodeAt(0) >= 0xA0;
    }

    /**
     * @param {string} value
     * @return {boolean}
     */
    function isCSSIdentifier(value)
    {
        return /^-?[a-zA-Z_][a-zA-Z0-9_-]*$/.test(value);
    }

    var prefixedOwnClassNamesArray = prefixedElementClassNames(node);
    var needsClassNames = false;
    var needsNthChild = false;
    var ownIndex = -1;
    var elementIndex = -1;
    var siblings = parent.children;
    for (var i = 0; (ownIndex === -1 || !needsNthChild) && i < siblings.length; ++i) {
        var sibling = siblings[i];
        if (sibling.nodeType !== Node.ELEMENT_NODE)
            continue;
        elementIndex += 1;
        if (sibling === node) {
            ownIndex = elementIndex;
            continue;
        }
        if (needsNthChild)
            continue;
        if (sibling.tagName.toLowerCase() !== nodeName)
            continue;

        needsClassNames = true;
        var ownClassNames = prefixedOwnClassNamesArray.keys();
        var ownClassNameCount = 0;
        for (var name in ownClassNames)
            ++ownClassNameCount;
        if (ownClassNameCount === 0) {
            needsNthChild = true;
            continue;
        }
        var siblingClassNamesArray = prefixedElementClassNames(sibling);
        for (var j = 0; j < siblingClassNamesArray.length; ++j) {
            var siblingClass = siblingClassNamesArray[j];
            if (!ownClassNames.hasOwnProperty(siblingClass))
                continue;
            delete ownClassNames[siblingClass];
            if (!--ownClassNameCount) {
                needsNthChild = true;
                break;
            }
        }
    }

    var result = nodeName;
    if (isTargetNode && nodeName.toLowerCase() === "input" && node.getAttribute("type") && !node.getAttribute("id") && !node.getAttribute("class"))
        result += "[type=\"" + node.getAttribute("type") + "\"]";
    if (needsNthChild) {
        result += ":nth-child(" + (ownIndex + 1) + ")";
    } else if (needsClassNames) {
        for (var prefixedName in prefixedOwnClassNamesArray.keys())
            result += "." + escapeIdentifierIfNeeded(prefixedName.substr(1));
    }

    return new WebView.DOMNodePathStep(result, false);
}

/**
 * @constructor
 * @param {string} value
 * @param {boolean} optimized
 */
WebView.DOMNodePathStep = function(value, optimized)
{
    this.value = value;
    this.optimized = optimized || false;
}

WebView.DOMNodePathStep.prototype.toString = function()
{
  return this.value;
}

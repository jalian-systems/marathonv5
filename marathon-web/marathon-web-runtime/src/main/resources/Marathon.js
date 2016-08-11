if (!Element.prototype.matches) {
	Element.prototype.matches = Element.prototype.matchesSelector
			|| Element.prototype.mozMatchesSelector
			|| Element.prototype.msMatchesSelector
			|| Element.prototype.oMatchesSelector
			|| Element.prototype.webkitMatchesSelector
			|| function(s) {
				var matches = (this.document || this.ownerDocument)
						.querySelectorAll(s), i = matches.length;
				while (--i >= 0 && matches.item(i) !== this) {
				}
				return i > -1;
			};
}

if (typeof Object.assign != 'function') {
  Object.assign = function(target) {
    'use strict';
    if (target == null) {
      throw new TypeError('Cannot convert undefined or null to object');
    }

    target = Object(target);
    for (var index = 1; index < arguments.length; index++) {
      var source = arguments[index];
      if (source != null) {
        for (var key in source) {
          if (Object.prototype.hasOwnProperty.call(source, key)) {
            target[key] = source[key];
          }
        }
      }
    }
    return target;
  };
}

if (!Element.prototype.label) {
	Element.prototype.label = function() {
		var id = this.getAttribute('id');
		if(id != null) {
			var labels = document.querySelectorAll('label');
			for(var i = 0; i < labels.length; i++) {
				var forId = labels[i].getAttribute('for');
				if(forId === id)
					return labels[i].innerText.trim();
			}
		}
		return null;
	}
}

if (!Element.prototype.css) {
	Element.prototype.css = function() {
		var path = Marathon.cssPath(this, true);
		var count = document.querySelectorAll(path).length;
		if(count > 1)
			return Marathon.cssPath(this, false);
		return path;
	}
}

if (!Element.prototype.xpath) {
	Element.prototype.xpath = function() {
		var path = Marathon.xPath(this, true);
		var count = document.evaluate('count(' + path + ')', document, null, XPathResult.NUMBER_TYPE, null ).numberValue;
		if(count > 1)
			return Marathon.xPath(this, false);
		return path;
	}
}

if (!Element.prototype.link_text) {
	Element.prototype.link_text = function() {
		if(this.tagName.toLowerCase() === 'a')
			return this.innerText.trim();
		return null;
	}
}

window.matches = function(s) {
	return true;
}

function Marathon(port) {
	this.url = "ws://localhost:" + port + "/";
	this.parent_container = null;
	this.identity = null;
	this.omapLoaded = false;
	console.log("Connecting to server @ " + this.url);

	this.createSocket(this.url);
}

Marathon.prototype.setParentContainer = function(jsonStr) {
	this.parent_container = JSON.parse(jsonStr);	
}

Marathon.prototype.createSocket = function(url) {
	var _this = this;
	
	var ws = new WebSocket(url);
	ws.onopen = function() {
		console.log("Connected to server @ ", ws);
		ws.onmessage = _this.onMessage;
		_this.post = function(method, data) {
			var message = { method: method, data: JSON.stringify(data) };
			ws.send(JSON.stringify(message));
		}
		ws.onclose = function(evt) {
			console.log("Closed connection to " + url);
		}
		_this.addEventHandlers();
		_this.assertionWindow = new Marathon.AssertionWindow(function(target, selection){ $marathon.recordAssertion(target, selection); }, function(target, selection){ $marathon.recordWait(target, selection); }); 
		_this.assertionWindow.getProperties = function(target) { return $marathon.findAssertionProperties(target); }
	}

	ws.onclose = function(evt) {
		console.log("Trying reconnect again to " + url);
		_this.createSocket(url);
	};
}

Marathon.prototype.recordAssertion = function(target, selection) {
	if(selection.selection)
		this.postEvent(target, { type: 'assert', method: 'assert_contains', property: selection.property, value: selection.selection });
	else
		this.postEvent(target, { type: 'assert', property: selection.property, value: selection.value });
}

Marathon.prototype.recordWait = function(target, selection) {
	if(selection.selection)
		this.postEvent(target, { type: 'wait', method: 'wait_contains', property: selection.property, value: selection.selection });
	else
		this.postEvent(target, { type: 'wait', property: selection.property, value: selection.value });
}

Marathon.prototype.reloadScript = function() {
	this.post('reloadScript', {});	
}

Marathon.prototype.addEventHandlers = function() {
	var _this = this;
	document.addEventListener('change', function(evt) {
		_this.handleChangeEvent(evt.target);
	}, true);
	document.addEventListener('mousedown', function(evt) {
		_this.handleMouseDownEvent(evt);
	}, true);
	document.addEventListener('click', function(evt) {
		_this.handleClickEvent(evt);
	}, true);
	var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
	var list = document.querySelector('body');
	
	var observer = new MutationObserver(function(mutations) {  
      var reloadScript = false;
	  mutations.forEach(function(mutation) {
	    if (mutation.type === 'childList' && mutation.addedNodes) {
	      var addedNodes = mutation.addedNodes;
	      for(var i = 0; i < addedNodes.length; i++) {
	        if(addedNodes[i].tagName && addedNodes[i].tagName.toLowerCase() === 'iframe')
	          reloadScript = true;
	      }
	    }
	  });
	  if(reloadScript)
	  	_this.reloadScript();
	});
	
	observer.observe(list, {
	  attributes: true, 
	  childList: true, 
	  subtree: true,
	  characterData: true
	});
}

Marathon.prototype.getName = function(props) {
	var parts = [];
	for(var prop in props) {
		if(props.hasOwnProperty(prop)) {
			parts.push(props[prop]);
		}
	}
	return parts.join(':');
}

Marathon.prototype.getContainer = function(target) {
	var container = {};

	var headContainer = container ;
	var current = target.parentElement;
	while(current != null) {
		if(this.isContainer(current)) {
			container.container = this.getContainerDetails(current);
			container = container.container;
		}
		current = current.parentElement;
	}
	
	container.container = this.getContainerDetails(window);
	return headContainer.container;
}

Marathon.prototype.isContainer = function(target) {
	return this.findMatchingProperties('containerRecognitionProperties', target);
}

Marathon.prototype.getContainerDetails = function(target) {
	var container = {};
	if(this.identity !== null && target === window) {
		container.attributes = Object.assign({}, this.identity.attributes);
		Object.assign(container.attributes, this.findGeneralProperties(target));
		container.urp = Object.assign({}, this.identity.urp);
		container.attributes.title = this.identity.attributes.suggestedName;
	} else {
		var title = this.getName(this.findMatchingProperties('containerNamingProperties', target, false));
		container.attributes = this.findGeneralProperties(target);
		container.attributes.title = title ;
		container.urp = this.findMatchingProperties('containerRecognitionProperties', target);
	}
	container.containerURP = this.findMatchingProperties('containerRecognitionProperties', target);
	if(target === window)
		container.urp.title = title;
	if(target === window) {
		if(this.parent_container === null) {
			container.container_type = "window" ;	
		} else {
			container.container_type = "frame" ;
		}
	} else {
		container.container_type = target.tagName.toLowerCase() ;
	}
	if(this.parent_container !== null && target === window)
	  container.container = this.parent_container;
	return container;
}

Marathon.prototype.getSuffix = function(target) {
	if(target.tagName.toLowerCase() === "input") {
		return (target.getAttribute('type') === null ? "text" : target.getAttribute('type'));
	} else {
		return target.tagName.toLowerCase();
	}
}

Marathon.prototype.handleChangeEvent = function(target) {
	if(target.matches('#popUpWindow-top, #popUpWindow-top *'))
		return;
	var v = this.value(target);
	if(v != null)
		this.postEvent(target, { type: 'select', value: v, suffix: this.getSuffix(target)});
}

Marathon.prototype.shouldIgnoreClick = function(target) {
	var values = [ 'input[type="text"]', 'input[type="password"]', 'input[type="color"]',
	               'input[type="date"]', 'input[type="datetime"]', 'input[type="datetime-local"]',
	               'input[type="number"]', 'input[type="range"]', 'input[type="search"]',
	               'input[type="tel"]', 'input[type="time"]', 'input[type="url"]',
	               'input[type="week"]', 'input[type="email"]', 'input[type="file"]',
	               'input[type="checkbox"]', 'input[type="radio"]',
	               'input[type="month"]', 'select', 'option', 'textarea',
					function() { return this.matches('label') && this.getAttribute('for') !== null ;},
					function() { return this.matches('input') && this.getAttribute('type') === null ;}];
	
	if(target.matches('#popUpWindow-top, #popUpWindow-top *'))
		return true;
	var matched = values.find(function(v) { return typeof v === 'function' ? v.call(target) : target.matches(v); });
	if(matched)
		return true;
	return false;
}

Marathon.prototype.handleMouseDownEvent = function(evt) {
	this.currentMouseDownTarget = evt.target ;
	if(document.activeElement !== evt.target) {
		document.activeElement.blur();
	}
	this.postMouseEvent(evt);
}

Marathon.prototype.handleClickEvent = function(evt) {
	if(this.currentMouseDownTarget && this.currentMouseDownTarget === evt.target)
		return;
	this.postMouseEvent(evt);
}

Marathon.prototype.postMouseEvent = function(evt) {
	if (evt.which === 1 && evt.altKey && (evt.metaKey || evt.ctrlKey))
		return;
	var target = evt.target;
	if(this.shouldIgnoreClick(target) === true)
		return;
	$marathon.postEvent(target, { type: 'click', clickCount: evt.detail == 0 ? 1 : evt.detail, button: evt.button + 1, modifiersEx: "", x: 0, y: 0, suffix: this.getSuffix(target) });
}

Marathon.prototype.postEvent = function(target, event) {
    var record = this.getObjectIdentity(target);
	record.event = event ;
	record.request = 'record-action';
	record.container = this.getContainer(target);
	this.post('record', record);
}

Marathon.prototype.getSuggestedName = function(target) {
	return this.getName(this.findMatchingProperties('namingProperties', target, false));	
}

Marathon.prototype.getObjectIdentity = function(target) {
	var identity = {};	
	identity.attributes = this.findGeneralProperties(target);
	identity.urp = this.findMatchingProperties('recognitionProperties', target);
	identity.urp['tag_name'] = target.tagName.toLowerCase();
	identity.attributes.suggestedName = this.getSuggestedName(target);
	return identity;
}

Marathon.prototype.setContainerIdentity = function(jsonStr) {
	this.identity = JSON.parse(jsonStr);
}

Marathon.prototype.value = function(target) {
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
	
	console.log('Unhandled getValue');
	console.log(target);
	return null;
}

Marathon.prototype.getProperty = function(target, props) {
	if(props === 'tag_name') {
		if(target.tagName)
			return ['tag_name', target.tagName.toLowerCase()];
		return null;
	}
	else if (props === 'text') {
		var text = target.innerText;
		if(text && text.length < 30)
			return ['text', text.trim()];
		return null;
	}
	var user_given_values = props.match(/([^:]*):(.*)/)
	if(user_given_values !== null) {
		user_given_values.shift();
		return user_given_values;
	}
	var aProps = props.split('.');
	var ret = target;
	aProps.forEach(function(prop) {
		if (ret != null)
			ret = $marathon.getImmediateProperty(ret, prop);
	});
	if(ret === null)
		return ret;
	return [props, ret];
}

Marathon.prototype.getImmediateProperty = function(target, prop) {
	var ret = null;
	if (target.getAttribute && (ret = target.getAttribute(prop)) != null)
		return ret;
	if (target[prop] && !this.isFunction(target[prop]))
		return target[prop];
	if (target[prop] && this.isFunction(target[prop])
			&& (ret = target[prop].call(target)) != null)
		return ret;
	return ret;
}

Marathon.prototype.findGeneralProperties = function(target) {
	var propmap = {};
	this.generalProperties.forEach(function(prop) {
		var val = $marathon.getProperty(target, prop);
		if (val != null)
			propmap[val[0]] = val[1];
	});
	if(target.attributes)
		for (var i = 0, atts = target.attributes, n = atts.length; i < n; i++){
	    	propmap[atts[i].nodeName] = atts[i].nodeValue;
		}
	return propmap;
}

Marathon.prototype.findAssertionProperties = function(target) {
	var propmap = {};
	if(target.attributes)
		for (var i = 0, atts = target.attributes, n = atts.length; i < n; i++){
	    	propmap[atts[i].nodeName] = atts[i].nodeValue;
		}
	propmap['tag_name'] = target.tagName.toLowerCase();
	var text = target.innerText;
	if(text)
		propmap['text'] = text.trim();
	if(target.value)
		propmap['value'] = target.value.trim();
	if(target.matches('input[type="checkbox"]') || target.matches('input[type="radio"]')) {
		propmap['selected?'] = '' + target.checked;
	}
	propmap['enabled?'] = target.disabled ? '' + false : '' + true ;
	return propmap;
}

Marathon.prototype.isUnique = function(target, props) {
	var others = document.getElementsByTagName(target.tagName);
	for(var i = 0; i < others.length; i++) {
		if(target == others[i])
			continue;
		var other = others[i];
		var matched = true;
		for(var prop in props) {
			if(matched && props.hasOwnProperty(prop)) {
				var otherValue = this.getProperty(other, prop);
				if(otherValue === null || otherValue[1] !== props[prop])
					matched = false;
			}
		}
		if(matched)
			return false;
	}
	return true;
}

Marathon.prototype.findMatchingProperties = function(properties, target, unique) {
	unique = typeof unique !== 'undefined' ? unique : true;
    for(var i = 0; i < this[properties].length; i++) {
    	var np = this[properties][i];
    	if(target.matches(np.className)) {
    		var arrName = {} ;
    		var rb = true ;
    		np.properties.forEach(function(prop) {
				if (rb) {
					var val = $marathon.getProperty(target, prop);
					if (val == null) {
						rb = false;
					}
					else
						arrName[val[0]] = val[1];
				}
    		});
    		if(rb && (!unique || this.isUnique(target, arrName)))
    			return arrName;
    	}
    }
    return null;
}

Marathon.prototype.isFunction = function(obj) {
	return !!(obj && obj.constructor && obj.call && obj.apply);
};

Marathon.prototype.onMessage = function(evt) {
	var jsonEvt = JSON.parse(evt.data);
	$marathon[jsonEvt.method].call($marathon, JSON.parse(jsonEvt.data));
}

Marathon.prototype.toString = function() {
	return "Marathon[" + this.url + "]";
}

Marathon.prototype.setContextMenuTriggers = function(jsonTriggers) {
}

Marathon.prototype.setObjectMapConfig = function(jsonObjectMap) {
	var lists = [ 'namingProperties', 'recognitionProperties',
			'containerNamingProperties', 'containerRecognitionProperties' ];

	var generalProperties = new Set(jsonObjectMap['generalProperties']);
	var _this = this;
	lists.forEach(function(list) {
		var nplist = [];
		jsonObjectMap[list].forEach(function(np) {
			np.propertyLists.map(function(propertyList) {
				propertyList.className = np.className;
				nplist.push(propertyList);
				propertyList.properties.forEach(function(p) {
					if(p.match(/([^:]*):(.*)/) === null)
						generalProperties.add(p);
				});
			});
		});
		nplist = nplist.sort(function(a, b) {
		    if(a.className.toLowerCase() === b.className.toLowerCase())
				return b.priority - a.priority;
			if(a.className === '*')
			    return 1 ;
			if(b.className === '*')
			    return -1 ;
			return a.className.toLowerCase().localeCompare(b.className.toLowerCase());
		});
		_this[list] = nplist;
	});
	generalProperties.add('tag_name');
	generalProperties.add('link_text');
	_this['generalProperties'] = Array.from(generalProperties);
	_this.omapLoaded = true;
}

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
Marathon.cssPath = function(node, optimized)
{
    if (node.nodeType !== Node.ELEMENT_NODE)
        return "";

    var steps = [];
    var contextNode = node;
    while (contextNode) {
        var step = Marathon._cssPathStep(contextNode, !!optimized, contextNode === node);
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
 * @return {?Marathon.DOMNodePathStep}
 */
Marathon._cssPathStep = function(node, optimized, isTargetNode)
{
    if (node.nodeType !== Node.ELEMENT_NODE)
        return null;

    var id = node.getAttribute("id");
    if (optimized) {
        if (id)
            return new Marathon.DOMNodePathStep(idSelector(id), true);
        var nodeNameLower = node.tagName.toLowerCase().toLowerCase();
        if (nodeNameLower === "body" || nodeNameLower === "head" || nodeNameLower === "html")
            return new Marathon.DOMNodePathStep(node.tagName.toLowerCase(), true);
    }
    var nodeName = node.tagName.toLowerCase();

    if (id)
        return new Marathon.DOMNodePathStep(nodeName + idSelector(id), true);
    var parent = node.parentNode;
    if (!parent || parent.nodeType === Node.DOCUMENT_NODE)
        return new Marathon.DOMNodePathStep(nodeName, true);

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

    return new Marathon.DOMNodePathStep(result, false);
}

/**
 * @param {!WebInspector.DOMNode} node
 * @param {boolean=} optimized
 * @return {string}
 */
Marathon.xPath = function(node, optimized)
{
    if (node.nodeType === Node.DOCUMENT_NODE)
        return "/";

    var steps = [];
    var contextNode = node;
    while (contextNode) {
        var step = Marathon._xPathValue(contextNode, optimized);
        if (!step)
            break; // Error - bail out early.
        steps.push(step);
        if (optimized && step.optimized)
            break;
        contextNode = contextNode.parentNode;
    }

    steps.reverse();
    return (steps.length && steps[0].optimized ? "" : "/") + steps.join("/");
}

/**
 * @param {!WebInspector.DOMNode} node
 * @param {boolean=} optimized
 * @return {?Marathon.DOMNodePathStep}
 */
Marathon._xPathValue = function(node, optimized)
{
    var ownValue;
    var ownIndex = Marathon._xPathIndex(node);
    if (ownIndex === -1)
        return null; // Error.

    switch (node.nodeType) {
    case Node.ELEMENT_NODE:
        if (optimized && node.getAttribute("id"))
            return new Marathon.DOMNodePathStep("//*[@id=\"" + node.getAttribute("id") + "\"]", true);
        ownValue = node.localName;
        break;
    case Node.ATTRIBUTE_NODE:
        ownValue = "@" + node.tagName.toLowerCase();
        break;
    case Node.TEXT_NODE:
    case Node.CDATA_SECTION_NODE:
        ownValue = "text()";
        break;
    case Node.PROCESSING_INSTRUCTION_NODE:
        ownValue = "processing-instruction()";
        break;
    case Node.COMMENT_NODE:
        ownValue = "comment()";
        break;
    case Node.DOCUMENT_NODE:
        ownValue = "";
        break;
    default:
        ownValue = "";
        break;
    }

    if (ownIndex > 0)
        ownValue += "[" + ownIndex + "]";

    return new Marathon.DOMNodePathStep(ownValue, node.nodeType === Node.DOCUMENT_NODE);
}

/**
 * @param {!WebInspector.DOMNode} node
 * @return {number}
 */
Marathon._xPathIndex = function(node)
{
    // Returns -1 in case of error, 0 if no siblings matching the same expression, <XPath index among the same expression-matching sibling nodes> otherwise.
    function areNodesSimilar(left, right)
    {
        if (left === right)
            return true;

        if (left.nodeType === Node.ELEMENT_NODE && right.nodeType === Node.ELEMENT_NODE)
            return left.localName === right.localName;

        if (left.nodeType === right.nodeType)
            return true;

        // XPath treats CDATA as text nodes.
        var leftType = left.nodeType === Node.CDATA_SECTION_NODE ? Node.TEXT_NODE : left.nodeType;
        var rightType = right.nodeType === Node.CDATA_SECTION_NODE ? Node.TEXT_NODE : right.nodeType;
        return leftType === rightType;
    }

    var siblings = node.parentNode ? node.parentNode.children : null;
    if (!siblings)
        return 0; // Root node - no siblings.
    var hasSameNamedElements;
    for (var i = 0; i < siblings.length; ++i) {
        if (areNodesSimilar(node, siblings[i]) && siblings[i] !== node) {
            hasSameNamedElements = true;
            break;
        }
    }
    if (!hasSameNamedElements)
        return 0;
    var ownIndex = 1; // XPath indices start with 1.
    for (var i = 0; i < siblings.length; ++i) {
        if (areNodesSimilar(node, siblings[i])) {
            if (siblings[i] === node)
                return ownIndex;
            ++ownIndex;
        }
    }
    return -1; // An error occurred: |node| not found in parent's children.
}

/**
 * @constructor
 * @param {string} value
 * @param {boolean} optimized
 */
Marathon.DOMNodePathStep = function(value, optimized)
{
    this.value = value;
    this.optimized = optimized || false;
}

Marathon.DOMNodePathStep.prototype.toString = function()
{
  return this.value;
}

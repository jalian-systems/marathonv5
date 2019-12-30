Marathon.dragDrop = {
  initialMouseX: undefined,
  initialMouseY: undefined,
  startX: undefined,
  startY: undefined,
  draggedObject: undefined,
  initElement: function (element, dragHandle, resizeHandle) {
    if (typeof element == 'string')
      element = document.getElementById(element);
    element.addEventListener('mousedown', function(event) {
      Marathon.dragDrop.startDragMouse(element, event, dragHandle, false);
    });
    if(resizeHandle)
	    resizeHandle.addEventListener('mousedown', function(event) {
	      Marathon.dragDrop.startDragMouse(element, event, dragHandle, resizeHandle);
	    });
  },
  startDragMouse: function (element, e, dragHandle, resizeHandle) {
    if(dragHandle && e.target.matches(dragHandle)) {
      Marathon.dragDrop.op = Marathon.dragDrop.setPosition;
    } else if(resizeHandle) {
      Marathon.dragDrop.op = Marathon.dragDrop.setSize;
    } else
      return false;
    Marathon.dragDrop.startDrag(element);
    var evt = e || window.event;
    Marathon.dragDrop.initialMouseX = evt.clientX;
    Marathon.dragDrop.initialMouseY = evt.clientY;
    document.addEventListener('mousemove',Marathon.dragDrop.dragMouse);
    document.addEventListener('mouseup',Marathon.dragDrop.releaseElement);
    return false;
  },
  startDrag: function (obj) {
    if (Marathon.dragDrop.draggedObject)
      Marathon.dragDrop.releaseElement();
    if(Marathon.dragDrop.op === Marathon.dragDrop.setPosition) {
      Marathon.dragDrop.startX = obj.offsetLeft;
      Marathon.dragDrop.startY = obj.offsetTop;
    } else {
      var style = window.getComputedStyle(obj);
      Marathon.dragDrop.startX = parseFloat(style.getPropertyValue('width'));
      Marathon.dragDrop.startY = parseFloat(style.getPropertyValue('height'));
      console.log('object', Marathon.dragDrop.startX, Marathon.dragDrop.startY);
    }
    Marathon.dragDrop.draggedObject = obj;
    obj.className += ' dragged';
  },
  dragMouse: function (e) {
    var evt = e || window.event;
    var dX = evt.clientX - Marathon.dragDrop.initialMouseX;
    var dY = evt.clientY - Marathon.dragDrop.initialMouseY;
    Marathon.dragDrop.op(dX,dY);
    return false;
  },
  setSize: function (dx,dy) {
    Marathon.dragDrop.draggedObject.style.width = Marathon.dragDrop.startX + dx + 'px';
    Marathon.dragDrop.draggedObject.style.height = Marathon.dragDrop.startY + dy + 'px';
  },
  setPosition: function (dx,dy) {
    Marathon.dragDrop.draggedObject.style.left = Marathon.dragDrop.startX + dx + 'px';
    Marathon.dragDrop.draggedObject.style.top = Marathon.dragDrop.startY + dy + 'px';
  },
  releaseElement: function() {
    document.removeEventListener('mousemove',Marathon.dragDrop.dragMouse);
    document.removeEventListener('mouseup',Marathon.dragDrop.releaseElement);
    Marathon.dragDrop.draggedObject.className = Marathon.dragDrop.draggedObject.className.replace(/dragged/,'');
    Marathon.dragDrop.draggedObject = null;
  }
}

Marathon.PopUpWindow = function(title, options) {
  this.options = {
    id:             false,
    onOpen:         false,
    onClose:        false,
    isDraggable:    true,
    isClosable:     true,
    isResizable:    false,
    resizeLimits:   false,
    className:      'popUpWindow',
    contentDiv:     false,
    injectLocation: null,
    width:          400,
    zIndex:         2147483647,
    top:            0,
    left:           0,
    fadeAnimation:  false,
    stylePopUpWindow: "margin-bottom:0px;border:1px solid #b9b3af;background:#eef8fb;box-shadow:4px 4px 3px rgba(180,180,250,0.7);text-align:left;",
    styleTitleBar: "position:relative;font-size:16px;padding:5px 0 0 9px;cursor:move;color:#3c6b98;",
    styleCloseIcon: "position:absolute;top:5px;right:7px;height:18px;width:18px;cursor:pointer;" ,
    styleContent: "position:relative;margin:0;padding:8px 10px 7px 10px;font-size:14px;color:#278622;height:400px;width:300px;overflow:auto;",
    styleResizeIcon: "color:#278622;position:absolute;right:1px;bottom:1px;height:16px;width:16px;text-align:right;cursor:se-resize;" ,
    URL:            null
  };

  this.initialize(title, options);
}

Marathon.PopUpWindow.prototype.initialize = function(title, options) {
  this.setOptions(options);
  this.title = title;

  Marathon.PopUpWindow.topZIndex = Math.max(this.options.zIndex, Marathon.PopUpWindow.topZIndex);

  var windowDiv = document.createElement('div');
  windowDiv.style.visibility = 'hidden' ;
  windowDiv.style.position = 'absolute' ;

  this.isOpen = false;
  if(this.options.id)
    windowDiv.setAttribute('id', this.options.id);
  windowDiv.style.left = this.options.left;
  windowDiv.style.top = this.options.top;
  
  var closeIconHTML  = this.options.isClosable  ? '<span style="' + this.options.styleCloseIcon + '" class="closeIcon">&#10006;</span>'  : '';
  var resizeIconHTML = this.options.isResizable ? '</div><span style="' + this.options.styleResizeIcon + '" class="resizeIcon resizeHandle">&#9698;</span><div>' : '';

  windowDiv.innerHTML = '<div class="' + this.options.className + '" style="' + this.options.stylePopUpWindow + '">' +
                          ' <div class="titleBar" style="' + this.options.styleTitleBar + '"><span>' + title + '</span>' + closeIconHTML + '</div>' +
                          ' <div class="content" style="' + this.options.styleContent + '"><div class="contentHolder"></div>' + resizeIconHTML + '</div>' +
                          '</div>';

  (this.options.injectLocation || document.body).appendChild(windowDiv);
  windowDiv.titleBar  = windowDiv.getElementsByClassName('titleBar')[0];
  windowDiv.titleSpan = windowDiv.getElementsByTagName('span')[0];
  windowDiv.closeIcon = windowDiv.getElementsByClassName('closeIcon')[0];
  windowDiv.contentDivHolder = windowDiv.getElementsByClassName('contentHolder')[0];
  windowDiv.resizeHandle = windowDiv.getElementsByClassName('resizeHandle')[0];

  windowDiv.contentDiv = (document.getElementById(this.options.contentDiv) || document.createElement('div'));
  if(this.options.width)
    windowDiv.contentDiv.style.width = '' + this.options.width + 'px';
  windowDiv.contentDivHolder.appendChild(windowDiv.contentDiv);
  if(windowDiv.contentDiv.style.display == 'none')
    windowDiv.contentDiv.style.display = 'block';

  this.windowDiv = windowDiv;
  var self = this;
  this.windowDiv.addEventListener('mousedown', function() { self.windowDiv.style.zIndex = Marathon.PopUpWindow.topZIndex++; });

  this.initializeDrag();

  if (this.options.isClosable)
    this.windowDiv.closeIcon.addEventListener('click', function(evt) { self.close(evt) });

  if(this.options.URL)
    this.openURL(this.options.URL);
};

Marathon.PopUpWindow.prototype.initializeDrag = function() {
    if(this.options.isResizable)
      Marathon.dragDrop.initElement(this.windowDiv.getElementsByClassName('content')[0], false, this.windowDiv.resizeHandle);
    if(this.options.isDraggable) {
      this.windowDiv.titleBar.style.cursor = 'move';
      Marathon.dragDrop.initElement(this.windowDiv, '.titleBar, .titleBar *', false);
    }
};

Marathon.PopUpWindow.prototype.handleDragOverEvent = function(event) {
  event.preventDefault();
  event.dataTransfer.dropEffect = 'move' ;
};

Marathon.PopUpWindow.prototype.handleDragStartEvent = function(event) {
  event.preventDefault();
  event.dataTransfer.setData('text/plain', "Moving Around");
};

Marathon.PopUpWindow.prototype.handleDropEvent = function(event) {
  event.preventDefault();
};

Marathon.PopUpWindow.prototype.getWindowDiv = function() { return this.windowDiv; };
Marathon.PopUpWindow.prototype.getContentDiv =  function() { return this.windowDiv.contentDivHolder; };

Marathon.PopUpWindow.prototype.setTitle = function(newTitle) { this.windowDiv.titleSpan.innerHTML = newTitle; };
Marathon.PopUpWindow.prototype.setContent =  function(contentDiv) { this.windowDiv.contentDivHolder.empty().adopt(contentDiv); };
Marathon.PopUpWindow.prototype.setContentHTML = function(contentHTML) { this.windowDiv.contentDivHolder.innerHTML = contentHTML; };
Marathon.PopUpWindow.prototype.setWidth = function(newWidth) { this.windowDiv.style.width = newWidth + 'px'; };

Marathon.PopUpWindow.prototype.close = function(event) {
  if(!this.isOpen)
    return;
  this.isOpen = false;
  if(this.options['onClose']) this.options['onClose'].call(this);
  this.fadeOut(this.windowDiv);
};

Marathon.PopUpWindow.prototype.open = function() {
  this.windowDiv.style.zIndex = Marathon.PopUpWindow.topZIndex++;
  if(this.isOpen)
    return;
  this.fadeIn(this.windowDiv);
  if(this.options['onOpen']) this.options['onOpen'].call(this);
  this.isOpen = true;
};

Marathon.PopUpWindow.prototype.toggle = function() { this.isOpen ? this.close() : this.open(); };

Marathon.PopUpWindow.prototype.setPosition = function(options) {
  var posRelative = options.relativeTo.getBoundingClientRect();
  var x = posRelative.left + options.offset.x;
  var y = posRelative.top + options.offset.y;
  this.windowDiv.style.left = '' + x + 'px';
  this.windowDiv.style.top = '' + y + 'px';
};

Marathon.PopUpWindow.prototype.positionTo = function(relativeTo, xOffset, yOffset) {
  this.setPosition( { relativeTo: relativeTo,
                      offset: { x: xOffset, y: yOffset},
                      position: 'top left',
                      edge: 'top left' });
};

Marathon.PopUpWindow.prototype.openURL = function(URL, newTitle, onComplete, method) {
  var self = this;
  new Request.HTML({ url: URL,
    method: method || 'post',
    update: this.windowDiv.contentDivHolder,
    evalScripts: true,
    onComplete: function() {
    self.open();
    if(newTitle)
    self.setTitle(newTitle);
    if(onComplete)
    onComplete(this.response.text);
    } }).send();
}

Marathon.PopUpWindow.prototype.setOptions = function(options) {
  for(var key in options) {
    if(this.options.hasOwnProperty(key))
      this.options[key] = options[key];
    else
      console.warn("Unknown property given: " + key);
  }
  return this;
}

Marathon.PopUpWindow.prototype.fadeOut = function(element)
{
  if(this.options.fadeAnimation) {
  var op = 1;  // initial opacity
  var timer = setInterval(function () {
      if (op <= 0.1){
          clearInterval(timer);
          element.style.visibility = 'hidden';
      }
      element.style.opacity = op;
      element.style.filter = 'alpha(opacity=' + op * 100 + ")";
      op -= op * 0.1;
  }, 50);
  } else {
    element.style.visibility = 'hidden';
  }
}

Marathon.PopUpWindow.prototype.fadeIn = function(element)
{
  if(this.options.fadeAnimation) {
    var op = 0.1;  // initial opacity
    element.style.visibility = 'visible';
    var timer = setInterval(function () {
        if (op >= 1){
            clearInterval(timer);
        }
        element.style.opacity = op;
        element.style.filter = 'alpha(opacity=' + op * 100 + ")";
        op += op * 0.1;
    }, 10);
  } else {
    element.style.visibility = 'visible';
  }
}

Marathon.PopUpWindow.topZIndex = 1;

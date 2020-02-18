puts("Loading gwt.rb...")

gwt_can_handle = Proc.new { |e|
    e.get_attribute('role') === 'tree'
}

gwt_select = Proc.new { |e|
}

def click_gwt_tree(tree, treenode)
    path = JSONObject.new(treenode).get('path')
    elements = path.split('/')
    node = get_component(tree)
    bmark = MyBenchMark.new
    treepath = ""
    elements.each { |element|
        treepath = treepath + '/' + element ;
        bmark.report("Finding node: " + treepath) {
            wait = Wait.new(:timeout => 10)
            wait.until {
                findNode(node, element)
            }
            node = findNode(node, element)
        }
    }
    print 'clicking on node ', node.text, "\n"
    findTextNode(node).click
end

def findTextNode(node)
    divs = node.find_elements(:css, 'div');
    text = node.text
    textDiv = node ;
    divs.each{ |div|
        textDiv = div if div.text === node.text
    }
    return textDiv;
end

def findNode(node, text)
    xpath = './/div[@role="treeitem"';
    parts = text.split(":")
    parts.each{ |part|
        xpath += ' and .//*[normalize-space(text())="' + part + '"]' ;
    }
    xpath += ']';
    puts "Using xpath: " + xpath
    items = node.find_elements(:xpath, xpath)
    if items.length === 0
        img = node.find_element(:css, 'img')
        img.click
        items = node.find_elements(:xpath, './/div[@role="treeitem" and .//div[text()=" + text + "]]')
    end

    return items[0] if items.length == 1
end

def click_gwt_grid(table, cellinfo)
  $marathon._click(findCell(table, cellinfo))
end

def findCell(table, cellinfo)
  e = get_component(table)
  info = JSONObject.new(cellinfo)
  row = info.get("row")
  column = nil
  cellIndex = -1
  column = info.get("column") if info.has("column")
  cellIndex = info.get("cellIndex") if info.has("cellIndex")
  subrow = info.get("subrow")
  cellIndex = findCellIndex(e, column) if cellIndex == -1
  cellIndex += 1
  cell = e.find_element(:xpath, './/tr[@__gwt_row=' + row + ' and @__gwt_subrow=' + subrow + ']/td[' + cellIndex.to_s + ']')
end

def select_gwt_grid(table, value, cellinfo)
  cell = findCell(table, cellinfo).find_element(:xpath, './/input|.//select')
  $marathon._select(cell, value)
end

def findCellIndex(table, column)
    th = table.find_element(:xpath, './/th[normalize-space(text()) = normalize-space("' + column + '")]')
    return th.attribute('cellIndex').to_i
end

{ :can_handle => gwt_can_handle, :select => gwt_select }

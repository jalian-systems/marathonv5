LOGGER.info("Loading... JQuery.rb")

jquery_can_handle = Proc.new { |e|
  if e.attribute('class')
	classes = e.attribute('class').split(/\s/)
	classes.include?('ui-checkboxradio') || classes.include?('hasDatepicker')
  else
  	false
  end
}

jquery_select = Proc.new { |e, text|
  if e.attribute('class') && e.attribute('class').split(/\s/).include?('ui-checkboxradio')
      selected = e.selected?.to_s
      LOGGER.warning("Selected = " + selected + " text = " + text)
      if(text.downcase != selected.downcase)
          id = e.attribute('id')
          label = driver.find_element(:css, 'label[for="' + id + '"]')
          LOGGER.warning('Clicking label ' + label.text)
          label.click
      else
          LOGGER.warning('Skipped click')
      end
  end
  if e.attribute('class') && e.attribute('class').split(/\s/).include?('hasDatepicker')
    e.click if e.tag_name.downcase === 'input'
    driver.execute_script('jQuery(arguments[0]).datepicker("setDate", arguments[1])', e, text);
    driver.execute_script('jQuery(arguments[0]).datepicker("hide")', e) if e.tag_name.downcase === 'input'
  end    
}

{ :can_handle => jquery_can_handle, :select => jquery_select }

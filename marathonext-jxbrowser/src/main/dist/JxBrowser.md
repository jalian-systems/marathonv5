# Using JxBrowser with Marathon

Marathon includes a component resolver extension that can be
used to test JxBrowser components in the application. For
enabling the extension copy the
marathonext-jxbrowser-<version>.jar into the UserLibs folder.

## Notes

* When you record an application the following actions are
recorded:    
    1. Clicks are recorded on all HTML elements except for those that support recording a select call.
    2. Select calls are recorded on all `input`, `textarea` and `select` elements.
* The record statements take a CSS selector as the final argument:    
for example:
```
    click('browser', '#submit')
    select('browser', 'joe', '#user_log')
```
* If your application use frames, the selector contains a frameid and the CSS selector.
```
    click('browser', '2:#submit')
```
* You can use other selectors with the calls by manually modifying the recorded script. The supported types are: id, name, classname and xpath.
```
    click('browser', 'id:submit') # Uses ID selector
    select('browser', 'joe', 'name:log') # Uses name selector
    select('browser', 'joe', '3:name:log') # Uses name selector (frameid = 3)
```
* When the application is launched with Marathon, the `--remote-debugging-port` option is added to the JxBrowser instance. If your application already sets the option Marathon accesses the same port number.
* You can use standarad selenium calls with the JxBrowser instance using `with_jxbrowser` command.
```
    with_jxbrowser { |webdriver|
        # Use the webdriver here
        puts "**** The title of the page is: " + webdriver.title
    }
````

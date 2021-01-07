/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javaagent.css;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.json.JSONException;
import net.sourceforge.marathon.javaagent.NoSuchWindowException;
import net.sourceforge.marathon.javaagent.UnsupportedCommandException;

public class SimpleSelector implements Selector {

    public static final Logger LOGGER = Logger.getLogger(SimpleSelector.class.getName());

    private String tag;

    private List<SelectorFilter> filters = new ArrayList<SelectorFilter>();

    private PseudoClassFilter nthFilter = null;

    public SimpleSelector(String tag) {
        this.tag = tag;
    }

    public void addFilter(SelectorFilter filter) {
        if (nthFilter != null) {
            throw new ParserException("Only a single nth psuedoclass may be specified and it should be the last one", null);
        }
        if (filter instanceof PseudoClassFilter && ((PseudoClassFilter) filter).isNth()) {
            nthFilter = (PseudoClassFilter) filter;
        } else {
            filters.add(filter);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        for (SelectorFilter selectorFilter : filters) {
            sb.append(selectorFilter.toString());
        }
        if (nthFilter != null) {
            sb.append(nthFilter.toString());
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IJavaElement> findElements(final IJavaAgent driver, final IJavaElement container, long implicitWait) {
        final Object[] r = new Object[] { null };
        if (implicitWait == 0) {
            EventQueueWait.exec(new Runnable() {
                @Override
                public void run() {
                    try {
                        r[0] = found(container, driver);
                    } catch (NoSuchWindowException e) {
                        r[0] = e;
                    } catch (UnsupportedCommandException e) {
                        r[0] = e;
                    } catch (JSONException e) {
                        r[0] = e;
                    } catch (Exception e) {
                        r[0] = e;
                    }
                }
            });
        } else {
            new EventQueueWait() {
                @Override
                public boolean till() {
                    List<IJavaElement> list;
                    try {
                        list = found(container, driver);
                        r[0] = list;
                        return list.size() > 0;
                    } catch (NoSuchWindowException e) {
                        r[0] = e;
                        return true;
                    } catch (UnsupportedCommandException e) {
                        r[0] = e;
                        return true;
                    } catch (JSONException e) {
                        r[0] = e;
                        return true;
                    } catch (Exception e) {
                        r[0] = e;
                        return true;
                    }
                }
            }.wait_noexc("Unable to find component", implicitWait, 50);
        }
        if (r[0] instanceof NoSuchWindowException) {
            throw (NoSuchWindowException) r[0];
        }
        if (r[0] instanceof UnsupportedCommandException) {
            throw (UnsupportedCommandException) r[0];
        }
        if (r[0] instanceof JSONException) {
            throw (JSONException) r[0];
        }
        if (r[0] instanceof RuntimeException) {
            throw (RuntimeException) r[0];
        }
        if (r[0] instanceof Exception) {
            throw new RuntimeException(((Exception) r[0]).getMessage(), (Exception) r[0]);
        }
        List<IJavaElement> list = (List<IJavaElement>) r[0];
        return list;
    }

    public List<IJavaElement> found(IJavaElement self, IJavaAgent driver) {
        List<IJavaElement> cs = new ArrayList<IJavaElement>();
        if (tag.equals(".")) {
            cs.addAll(filterMatches(self));
        } else {
            findByTagName(self, cs, driver, false);
        }
        JWindow topContainer = driver.switchTo().getCurrentWindow();
        List<IJavaElement> r = new ArrayList<IJavaElement>();
        for (IJavaElement component : cs) {
            r.add(topContainer.addElement(component));
        }
        if (nthFilter != null) {
            if (nthFilter.getNthIndex() - 1 < r.size()) {
                return Arrays.asList(r.get(nthFilter.getNthIndex() - 1));
            } else {
                return new ArrayList<IJavaElement>();
            }
        }
        return r;
    }

    private void findByTagName(IJavaElement je, List<IJavaElement> cs, IJavaAgent driver, boolean addThis) {
        if (addThis) {
            cs.addAll(matchesSelector(je));
        }
        IJavaElement[] components = je.getComponents();
        for (IJavaElement javaElement : components) {
            findByTagName(javaElement, cs, driver, true);
        }
        return;
    }

    public List<IJavaElement> matchesSelector(IJavaElement je) {
        if ("*".equals(tag) || tag.equals(je.getTagName())) {
            return filterMatches(je);
        }
        return new ArrayList<IJavaElement>();
    }

    private List<IJavaElement> filterMatches(IJavaElement je) {
        List<IJavaElement> elements = Arrays.asList(je);
        for (SelectorFilter f : filters) {
            List<IJavaElement> toProcess = new ArrayList<IJavaElement>();
            for (IJavaElement javaElement : elements) {
                toProcess.addAll(f.match(javaElement));
            }
            elements = toProcess;
        }
        return elements;
    }

}

package net.sourceforge.marathon.javaagent.css;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.javaagent.NoSuchWindowException;
import net.sourceforge.marathon.javaagent.UnsupportedCommandException;

import org.json.JSONException;

public class SimpleSelector implements Selector {

    private String tag;

    private List<SelectorFilter> filters = new ArrayList<SelectorFilter>();

    private PseudoClassFilter nthFilter = null;

    public SimpleSelector(String tag) {
        this.tag = tag;
    }

    public void addFilter(SelectorFilter filter) {
        if (nthFilter != null)
            throw new ParserException("Only a single nth psuedoclass may be specified and it should be the last one", null);
        if (filter instanceof PseudoClassFilter && ((PseudoClassFilter) filter).isNth())
            nthFilter = (PseudoClassFilter) filter;
        else
            filters.add(filter);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        for (SelectorFilter selectorFilter : filters) {
            sb.append(selectorFilter.toString());
        }
        if (nthFilter != null)
            sb.append(nthFilter.toString());
        return sb.toString();
    }

    @SuppressWarnings("unchecked") @Override public List<IJavaElement> findElements(final JavaAgent driver,
            final IJavaElement container, long implicitWait) {
        final Object[] r = new Object[] { null };
        if (implicitWait == 0) {
            EventQueueWait.exec(new Runnable() {
                @Override public void run() {
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
                @Override public boolean till() {
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
        if (r[0] instanceof NoSuchWindowException)
            throw (NoSuchWindowException) r[0];
        if (r[0] instanceof UnsupportedCommandException)
            throw (UnsupportedCommandException) r[0];
        if (r[0] instanceof JSONException)
            throw (JSONException) r[0];
        if (r[0] instanceof RuntimeException)
            throw (RuntimeException) r[0];
        if (r[0] instanceof Exception)
            throw new RuntimeException(((Exception) r[0]).getMessage(), (Exception) r[0]);
        List<IJavaElement> list = (List<IJavaElement>) r[0];
        return list;
    }

    public List<IJavaElement> found(IJavaElement self, JavaAgent driver) {
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
            if (nthFilter.getNthIndex() - 1 < r.size())
                return Arrays.asList(r.get(nthFilter.getNthIndex() - 1));
            else
                return new ArrayList<IJavaElement>();
        }
        return r;
    }

    private void findByTagName(IJavaElement je, List<IJavaElement> cs, JavaAgent driver, boolean addThis) {
        if (addThis)
            cs.addAll(matchesSelector(je));
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

package net.sourceforge.marathon.runtime.api;

import org.apache.commons.lang3.StringEscapeUtils;

public class UsedAssertion {

    private ComponentId id;
    private String property;
    private String expected;
    private String actual;
    private boolean success;

    public UsedAssertion(ComponentId id, String property, String expected, String actual, boolean success) {
        this.id = id;
        this.property = property;
        this.expected = expected;
        this.actual = actual;
        this.success = success;
    }

    public void makeTableEntry(StringBuffer sb) {
        String name = id.getName();
        String info = id.getComponentInfo();
        if(name == null) {
            name = id.getNameProps().toString();
        }
        if(info == null) {
            info = id.getComponentInfoProps() == null ? null : id.getComponentInfoProps().toString();
        }
        sb.append("<tr>\n");
        sb.append("<td>").append(StringEscapeUtils.escapeHtml4(id.toString())).append("</td>\n");
        sb.append("<td>").append(StringEscapeUtils.escapeHtml4(property)).append("</td>\n");
        sb.append("<td>").append(StringEscapeUtils.escapeHtml4(expected)).append("</td>\n");
        sb.append("<td>").append(StringEscapeUtils.escapeHtml4(actual)).append("</td>\n");
        sb.append("<td>").append(StringEscapeUtils.escapeHtml4(success ? "Success" : "Fail")).append("</td>\n");
        sb.append("</tr>\n");
    }

}

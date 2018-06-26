package net.sourceforge.marathon.runtime.api;

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
        sb.append("<td>").append(escapeHtml4(id.toString())).append("</td>\n");
        sb.append("<td>").append(escapeHtml4(property)).append("</td>\n");
        sb.append("<td>").append(escapeHtml4(expected)).append("</td>\n");
        sb.append("<td>").append(escapeHtml4(actual)).append("</td>\n");
        sb.append("<td>").append(escapeHtml4(success ? "Success" : "Fail")).append("</td>\n");
        sb.append("</tr>\n");
    }

    private String escapeHtml4(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}

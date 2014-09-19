package mirana.common.http.restful;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

public class AntPathStringMatcher {

    private static final Pattern GLOB_PATTERN             = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");

    private static final String  DEFAULT_VARIABLE_PATTERN = "(.*)";

    private final Pattern        pattern;

    private final List<String>   variableNames            = new LinkedList<String>();

    public AntPathStringMatcher(String pattern) {
        StringBuilder patternBuilder = new StringBuilder();
        Matcher m = GLOB_PATTERN.matcher(pattern);
        int end = 0;
        while (m.find()) {
            patternBuilder.append(quote(pattern, end, m.start()));
            String match = m.group();
            if ("?".equals(match)) {
                patternBuilder.append('.');
            } else if ("*".equals(match)) {
                patternBuilder.append(".*");
            } else if (match.startsWith("{") && match.endsWith("}")) {
                int colonIdx = match.indexOf(':');
                if (colonIdx == -1) {
                    patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
                    this.variableNames.add(m.group(1));
                } else {
                    String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                    patternBuilder.append('(');
                    patternBuilder.append(variablePattern);
                    patternBuilder.append(')');
                    String variableName = match.substring(1, colonIdx);
                    this.variableNames.add(variableName);
                }
            }
            end = m.end();
        }
        patternBuilder.append(quote(pattern, end, pattern.length()));
        this.pattern = Pattern.compile(patternBuilder.toString());
    }

    private String quote(String s, int start, int end) {
        if (start == end) {
            return "";
        }
        return Pattern.quote(s.substring(start, end));
    }

    public boolean matchStrings(String str, Map<String, String> uriTemplateVariables) {
        Matcher matcher = this.pattern.matcher(str);
        if (matcher.matches()) {
            if (uriTemplateVariables != null) {
                // SPR-8455
                Assert.assertTrue("The number of capturing groups in the pattern segment " + this.pattern
                        + " does not match the number of URI template variables it defines, which can occur if "
                        + " capturing groups are used in a URI template regex. Use non-capturing groups instead.", this.variableNames.size() == matcher.groupCount());
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String name = this.variableNames.get(i - 1);
                    String value = matcher.group(i);
                    uriTemplateVariables.put(name, value);
                }
            }
            return true;
        } else {
            return false;
        }
    }

}

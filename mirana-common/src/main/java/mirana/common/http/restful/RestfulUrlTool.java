package mirana.common.http.restful;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RestfulUrlTool {

    public static final String  DEF_PATHSEPARATOR = "/";

    public static final boolean DEF_TRIMTOKENS    = true;

    public static String handle(String path, Set<String> rules) {
        return handle(path, DEF_PATHSEPARATOR, DEF_TRIMTOKENS, rules);
    }

    public static String handle(String path, String pathSeparator, boolean trimTokens, Set<String> rules) {
        if (StringUtils.hasText(path)) {
            List<String> re = new ArrayList<String>();
            for (String p : rules) {
                if (doMatch(p, path, true, pathSeparator, trimTokens, null)) {
                    re.add(p);
                }
            }
            if (!re.isEmpty()) {
                AntPathMatcher matcher = new AntPathMatcher();
                matcher.setPathSeparator(pathSeparator);
                matcher.setTrimTokens(trimTokens);
                Collections.sort(re, matcher.getPatternComparator(path));
                return re.get(0);
            } else {
                return path;
            }
        }
        return "/";
    }


    private static boolean doMatch(String pattern, String path, boolean fullMatch, String pathSeparator, boolean trimTokens, Map<String, String> uriTemplateVariables) {
        if (path.startsWith(pathSeparator) != pattern.startsWith(pathSeparator)) {
            return false;
        }

        String[] pattDirs = tokenizePath(pattern, pathSeparator, trimTokens);
        String[] pathDirs = tokenizePath(path, pathSeparator, trimTokens);

        int pattIdxStart = 0;
        int pattIdxEnd = pattDirs.length - 1;
        int pathIdxStart = 0;
        int pathIdxEnd = pathDirs.length - 1;

        // Match all elements up to the first **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String pattDir = pattDirs[pattIdxStart];
            if ("**".equals(pattDir)) {
                break;
            }
            if (!matchStrings(pattDir, pathDirs[pathIdxStart], uriTemplateVariables)) {
                return false;
            }
            pattIdxStart++;
            pathIdxStart++;
        }

        if (pathIdxStart > pathIdxEnd) {
            // Path is exhausted, only match if rest of pattern is * or **'s
            if (pattIdxStart > pattIdxEnd) {
                return (pattern.endsWith(pathSeparator) ? path.endsWith(pathSeparator) : !path.endsWith(pathSeparator));
            }
            if (!fullMatch) {
                return true;
            }
            if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") && path.endsWith(pathSeparator)) {
                return true;
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        } else if (pattIdxStart > pattIdxEnd) {
            // String not exhausted, but pattern is. Failure.
            return false;
        } else if (!fullMatch && "**".equals(pattDirs[pattIdxStart])) {
            // Path start definitely matches due to "**" part in pattern.
            return true;
        }

        // up to last '**'
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String pattDir = pattDirs[pattIdxEnd];
            if (pattDir.equals("**")) {
                break;
            }
            if (!matchStrings(pattDir, pathDirs[pathIdxEnd], uriTemplateVariables)) {
                return false;
            }
            pattIdxEnd--;
            pathIdxEnd--;
        }
        if (pathIdxStart > pathIdxEnd) {
            // String is exhausted
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }

        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            int patIdxTmp = -1;
            for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
                if (pattDirs[i].equals("**")) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == pattIdxStart + 1) {
                // '**/**' situation, so skip one
                pattIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - pattIdxStart - 1);
            int strLength = (pathIdxEnd - pathIdxStart + 1);
            int foundIdx = -1;

            strLoop: for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = pattDirs[pattIdxStart + j + 1];
                    String subStr = pathDirs[pathIdxStart + i + j];
                    if (!matchStrings(subPat, subStr, uriTemplateVariables)) {
                        continue strLoop;
                    }
                }
                foundIdx = pathIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            pattIdxStart = patIdxTmp;
            pathIdxStart = foundIdx + patLength;
        }

        for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
            if (!pattDirs[i].equals("**")) {
                return false;
            }
        }

        return true;
    }

    private static boolean matchStrings(String pattern, String str, Map<String, String> uriTemplateVariables) {
        AntPathStringMatcher matcher = new AntPathStringMatcher(pattern);
        return matcher.matchStrings(str, uriTemplateVariables);
    }

    private static String[] tokenizePath(String path, String pathSeparator, boolean trimTokens) {
        return StringUtils.tokenizeToStringArray(path, pathSeparator, trimTokens, true);
    }
}

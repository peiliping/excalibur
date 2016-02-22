package augur;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.enforcer.AbstractBanDependencies;
import org.apache.maven.plugins.enforcer.utils.ArtifactMatcher;
import org.apache.maven.plugins.enforcer.utils.ArtifactMatcher.Pattern;

import augur.HttpUtil.HttpResult;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import edu.emory.mathcs.backport.java.util.Arrays;

public class BannedDependenciesEX extends AbstractBanDependencies {

    private String      configUrl = null;

    private Set<String> excludes  = new HashSet<String>();
    private Set<String> includes  = new HashSet<String>();
    private Set<String> warnings  = new HashSet<String>();

    private boolean     init      = false;

    private synchronized void initConfig(Log log) {
        if (init) {
            return;
        }
        if (StringUtils.isBlank(configUrl)) {
            Validate.isTrue(false, "BannedDependenciesEX ConfigUrl Is Blank!");
        }
        HttpResult r = HttpUtil.httpGet(configUrl);
        Validate.isTrue(r.success, "BannedDependenciesEX Get Config Failed!");
        JSONObject result = JSON.parseObject(r.content);
        JSONObject rules = result.getJSONObject("rules");
        setExcludes(convertRules(rules, "excludes"));
        setIncludes(convertRules(rules, "includes"));
        setWarnings(convertRules(rules, "warnings"));
        init = true;
    }

    private List<String> convertRules(JSONObject rules, String key) {
        List<String> result = new ArrayList<String>();
        JSONArray ja = rules.getJSONArray(key);
        if (ja != null) {
            for (Object o : ja)
                result.add(String.valueOf(o));
        }
        return result;
    }

    protected Set<Artifact> checkDependencies(Set<Artifact> theDependencies, Log log) throws EnforcerRuleException {
        initConfig(log);
        Set<Artifact> excluded = checkDependencies(theDependencies, excludes);
        if (excluded != null) {
            Set<Artifact> included = checkDependencies(theDependencies, includes);
            if (included != null) {
                excluded.removeAll(included);
            }
        }
        if (!warnings.isEmpty()) {
            Set<Artifact> warningList = checkDependencies(theDependencies, warnings);
            if (warningList != null) {
                log.warn("========== BannedDependenciesEX  Warning Start ==========");
                for (Artifact aft : warningList)
                    log.warn("BannedDependenciesEX : " + aft.toString());
                log.warn("========== BannedDependenciesEX  Warning END ==========");
                // Post Data to Server
            }
        }
        return excluded;
    }

    private Set<Artifact> checkDependencies(Set<Artifact> dependencies, Set<String> thePatterns) throws EnforcerRuleException {
        Set<Artifact> foundMatches = null;
        if (thePatterns != null && thePatterns.size() > 0) {
            for (String pattern : thePatterns) {
                String[] subStrings = pattern.split(":");
                subStrings = StringUtils.stripAll(subStrings);
                String resultPattern = StringUtils.join(subStrings, ":");
                for (Artifact artifact : dependencies) {
                    if (compareDependency(resultPattern, artifact)) {
                        if (foundMatches == null) {
                            foundMatches = new HashSet<Artifact>();
                        }
                        foundMatches.add(artifact);
                    }
                }
            }
        }
        return foundMatches;
    }

    protected boolean compareDependency(String pattern, Artifact artifact) throws EnforcerRuleException {
        ArtifactMatcher.Pattern am = new Pattern(pattern);
        boolean result;
        try {
            result = am.match(artifact);
        } catch (InvalidVersionSpecificationException e) {
            throw new EnforcerRuleException("Invalid Version Range: ", e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<String> getExcludes() {
        return Arrays.asList(this.excludes.toArray());
    }

    public void setExcludes(List<String> theExcludes) {
        this.excludes.addAll(theExcludes);
    }

    @SuppressWarnings("unchecked")
    public List<String> getIncludes() {
        return Arrays.asList(this.includes.toArray());
    }

    public void setIncludes(List<String> theIncludes) {
        this.includes.addAll(theIncludes);
    }

    @SuppressWarnings("unchecked")
    public List<String> getWarnings() {
        return Arrays.asList(this.warnings.toArray());
    }

    public void setWarnings(List<String> warnings) {
        this.warnings.addAll(warnings);
    }

    public String getConfigUrl() {
        return configUrl;
    }

    public void setConfigUrl(String configUrl) {
        this.configUrl = configUrl;
    }

}

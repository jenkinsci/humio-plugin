package org.jenkinsci.plugins.humio;

import hudson.model.Run;

import java.io.IOException;
import java.util.Map;

public class Util {
    public static void addRunMetaData(Run build, Map<String, String> extraFields) {

        // Get info from the build environment and create fields for them.
        // Currently only Git.

        String gitBranch = null;
        try {
            gitBranch = build.getEnvironment().get("GIT_BRANCH");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        if (gitBranch != null && !"".equals(gitBranch)) {
            extraFields.put("gitBranch", gitBranch);
        }
    }
}

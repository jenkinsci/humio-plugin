package org.jenkinsci.plugins.humio;

import hudson.EnvVars;
import hudson.model.Run;

import java.io.IOException;
import java.util.Map;

public class Util {
    public static void addRunMetaData(Run build, Map<String, String> extraFields) {

        // Get info from the build environment and create fields for them.
        // Currently only Git.

        try {
            // TODO: This is deprecated, I just don't know the proper way to get the env.
            EnvVars e = build.getEnvironment();

            String gitBranch = e.get("GIT_BRANCH");
            if (gitBranch != null && !"".equals(gitBranch)) {
                extraFields.put("git.branch", gitBranch);
            }

            String gitAuthorName = e.get("GIT_AUTHOR_NAME");
            if (gitAuthorName != null && !"".equals(gitAuthorName)) {
                extraFields.put("git.author.name", gitAuthorName);
            }

            String gitAuthorEmail = e.get("GIT_AUTHOR_EMAIL");
            if (gitAuthorEmail != null && !"".equals(gitAuthorEmail)) {
                extraFields.put("git.author.email", gitAuthorEmail);
            }

            String gitCommitterName = e.get("GIT_COMMITTER_NAME");
            if (gitCommitterName != null && !"".equals(gitCommitterName)) {
                extraFields.put("git.committer.name", gitCommitterName);
            }

            String gitCommitterEmail = e.get("GIT_COMMITTER_EMAIL");
            if (gitCommitterEmail  != null && !"".equals(gitCommitterEmail )) {
                extraFields.put("git.committer.email", gitCommitterEmail );
            }

            String gitCommit = e.get("GIT_COMMIT");
            if (gitCommit  != null && !"".equals(gitCommit )) {
                extraFields.put("git.commit", gitCommit );
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

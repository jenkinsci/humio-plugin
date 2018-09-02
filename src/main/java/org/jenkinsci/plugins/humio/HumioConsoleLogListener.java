package org.jenkinsci.plugins.humio;

import hudson.Extension;
import hudson.console.ConsoleLogFilter;
import hudson.model.Run;
import org.apache.commons.io.output.TeeOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

@Extension
@SuppressWarnings("unused")
public class HumioConsoleLogListener extends ConsoleLogFilter {
    @Override
    public OutputStream decorateLogger(Run build, OutputStream logger) throws IOException, InterruptedException {
        if (HumioConfig.getInstance().getEnabled()) {
            // Get info from the build environment and create fields for them.
            // Currently only Git.
            Map<String, String> extraFields = new TreeMap<>();

            String gitBranch = build.getEnvironment().get("GIT_BRANCH");
            if (gitBranch != null && !"".equals(gitBranch)) {
                extraFields.put("gitBranch", gitBranch);
            }

            return new TeeOutputStream(logger, new HumioOutputStream(build.getParent().getName(), build.getNumber(), extraFields));
        } else {
            return logger;
        }
    }
}

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
    public OutputStream decorateLogger(Run build, OutputStream logger) {
        if (HumioConfig.getInstance().getEnabled()) {

            Map<String, String> extraFields = new TreeMap<>();

            Util.addRunMetaData(build, extraFields);

            return new TeeOutputStream(logger, new HumioOutputStream(build.getParent().getName(), build.getNumber(), extraFields));
        } else {
            return logger;
        }
    }
}

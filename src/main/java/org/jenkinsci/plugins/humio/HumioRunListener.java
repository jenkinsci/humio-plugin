package org.jenkinsci.plugins.humio;

import hudson.Extension;
import hudson.Launcher;
import hudson.console.ConsoleLogFilter;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.tasks.BuildWrapper;
import org.apache.commons.io.output.TeeOutputStream;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

@Extension
@SuppressWarnings("unused")
public class HumioRunListener extends RunListener<Run> {

    @Override
    public void onStarted(Run build, TaskListener listener) {
        if (HumioConfig.getInstance().getEnabled()) {
            Map<String, String> extraFields = new TreeMap<>();
            extraFields.put("build.duration", Long.toString(build.getDuration()));
            extraFields.put("build.start", java.time.Instant.ofEpochMilli(build.getStartTimeInMillis()).toString());

            Util.addRunMetaData(build, extraFields);

            String logLine = String.format("%s (#%d) - STARTED", build.getParent().getName(), build.getNumber());

            HumioLogShipper.send(logLine, build.getNumber(), build.getParent().getName(), "start", extraFields);
        }
    }

    @Override
    public void onCompleted(Run build, @Nonnull TaskListener listener) {
        Result result = build.getResult();
        if (HumioConfig.getInstance().getEnabled() && result != null) {
            Map<String, String> extraFields = new TreeMap<>();
            extraFields.put("build.duration", Long.toString(build.getDuration()));
            extraFields.put("build.start", java.time.Instant.ofEpochMilli(build.getStartTimeInMillis()).toString());
            extraFields.put("build.end", java.time.Instant.ofEpochMilli(build.getStartTimeInMillis() + build.getDuration()).toString());
            extraFields.put("build.result", result.toString());

            Util.addRunMetaData(build, extraFields);

            String logLine = String.format("%s (#%d) - %s", build.getParent().getName(), build.getNumber(), result);

            HumioLogShipper.send(logLine, build.getNumber(), build.getParent().getName(), "end", extraFields);
        }
    }
}

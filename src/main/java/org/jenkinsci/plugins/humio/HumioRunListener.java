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
    public void onCompleted(Run run, @Nonnull TaskListener listener) {
        if (HumioConfig.getInstance().getEnabled() && run.getResult() != null) {
            Map<String, String> attributes = new TreeMap<>();
            attributes.put("duration", Long.toString(run.getDuration()));
            attributes.put("start", java.time.Instant.ofEpochMilli(run.getStartTimeInMillis()).toString());
            attributes.put("end", java.time.Instant.ofEpochMilli(run.getStartTimeInMillis() + run.getDuration()).toString());
            attributes.put("result", run.getResult().toString());

            HumioLogShipper.send("Generated Build Statistics", run.getNumber(), run.getParent().getName(), attributes);
        }
    }
}

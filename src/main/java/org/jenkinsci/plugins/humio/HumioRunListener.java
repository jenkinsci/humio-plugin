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
    public void onCompleted(Run build, @Nonnull TaskListener listener) {
        Result result = build.getResult();
        if (HumioConfig.getInstance().getEnabled() && result != null) {
            Map<String, String> extraFields = new TreeMap<>();
            extraFields.put("duration", Long.toString(build.getDuration()));
            extraFields.put("start", java.time.Instant.ofEpochMilli(build.getStartTimeInMillis()).toString());
            extraFields.put("end", java.time.Instant.ofEpochMilli(build.getStartTimeInMillis() + build.getDuration()).toString());
            extraFields.put("result", result.toString());

            Util.addRunMetaData(build, extraFields);

            HumioLogShipper.send("Generated Build Statistics", build.getNumber(), build.getParent().getName(), extraFields);
        }
    }
}

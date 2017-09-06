package com.humio.jenkinshumio;

import hudson.Extension;
import hudson.console.ConsoleLogFilter;
import hudson.model.*;
import org.apache.commons.io.output.TeeOutputStream;

import java.io.*;

@Extension
@SuppressWarnings("unused")
public class HumioJobListener extends ConsoleLogFilter {
    @Override
    public OutputStream decorateLogger(Run build, OutputStream logger) throws IOException, InterruptedException {
        if (HumioConfig.getInstance().enabled) {
            return new TeeOutputStream(logger, new HumioOutputStream(build.getParent().getName(), build.getNumber()));
        } else {
            return logger;
        }
    }
}

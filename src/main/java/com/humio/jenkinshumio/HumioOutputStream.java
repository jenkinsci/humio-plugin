package com.humio.jenkinshumio;

import hudson.console.LineTransformationOutputStream;

import java.io.IOException;

public class HumioOutputStream extends LineTransformationOutputStream {
    private final String jobName;
    private final int buildNumber;

    private boolean isBlankLine(String line) {
        return line.trim().isEmpty();
    }

    HumioOutputStream(String jobName, int buildNumber) {
        this.jobName = jobName;
        this.buildNumber = buildNumber;
    }

    @Override
    protected void eol(byte[] b, int len) throws IOException {
        String line = new String(b, 0, len);

        if (!isBlankLine(line)) {
            // HACK: For some reason, the build result is duplicated in the last line of output.
            // Only in code, in the Jenkins console it looks right. So we just fix it. *Yuck*
            String trimmedLine = correctBuildStatus(trimEOL(line));
            HumioLogShipper.send(trimmedLine, buildNumber, jobName);
        }
    }

    private String correctBuildStatus(String line) {
        switch (line) {
            case "Finished: SUCCESSFinished: SUCCESS":
                return "Finished: SUCCESS";
            case "Finished: FAILUREFinished: FAILURE":
                return "Finished: FAILURE";
            default:
                return line;
        }
    }
}

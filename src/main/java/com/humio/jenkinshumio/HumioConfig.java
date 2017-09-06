package com.humio.jenkinshumio;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;

import javax.annotation.Nonnull;

@Extension
public class HumioConfig extends GlobalConfiguration {

    String serverURL = "https://cloud.humio.com/";
    String authToken = "";
    String dataspaceId = "";
    Boolean enabled = true;

    @Nonnull
    @Override
    public String getDisplayName() {
        return "Humio";
    }

    public static HumioConfig getInstance() {
        return all().get(HumioConfig.class);
    }

    @SuppressWarnings("all")
    public static boolean isValid() {
        HumioConfig i = getInstance();
        return !(  "".equals(i.serverURL)
                || "".equals(i.authToken)
                || "".equals(i.dataspaceId));
    }
}

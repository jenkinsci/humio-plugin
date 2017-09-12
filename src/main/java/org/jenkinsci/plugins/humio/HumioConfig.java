package org.jenkinsci.plugins.humio;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

@Extension
public class HumioConfig extends GlobalConfiguration {

    private static final String HUMIO_CREDENTIAL_KEY = "HumioPluginIngestToken";
    private static final Logger LOGGER = LoggerFactory.getLogger(HumioConfig.class);

    private String serverURL = "https://cloud.humio.com/";
    private String dataspaceId = "";
    private Boolean enabled = true;

    public HumioConfig() {
        super();
        load();
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return "Humio";
    }

    public static synchronized HumioConfig getInstance() {

        return all().get(HumioConfig.class);
    }

    @SuppressWarnings("all")
    public static boolean isValid() {
        HumioConfig i = getInstance();
        return !(  "".equals(i.serverURL)
                || i.serverURL == null
                || "".equals(i.getIngestToken())
                || i.getIngestToken() == null
                || "".equals(i.dataspaceId)
                || i.dataspaceId == null);
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        super.configure(req, json);
        save();
        return true;
    }

    @SuppressWarnings("all")
    public String getServerURL() {
        return serverURL;
    }

    @SuppressWarnings("unused")
    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    @SuppressWarnings("all")
    public String getIngestToken() {
        // TODO: There must be a more elegant way of getting credentials?

        List<Credentials> credentialsList =
                SystemCredentialsProvider
                        .getInstance()
                        .getCredentials();

        for (Credentials c : credentialsList) {
            if (c instanceof StandardUsernamePasswordCredentials) {
                StandardUsernamePasswordCredentials cc = (StandardUsernamePasswordCredentials)c;

                if (HUMIO_CREDENTIAL_KEY.equals(cc.getId())) {
                    return cc.getPassword().getPlainText();
                }
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
    public void setIngestToken(String ingestToken) {
        try {
            updateIngestToken(ingestToken);
        } catch (IOException e) {
            LOGGER.error("Failed to update Humio Intest Token", e);
        }
    }

    @SuppressWarnings("unused")
    public String getDataspaceId() {
        return dataspaceId;
    }

    @SuppressWarnings("unused")
    public void setDataspaceId(String dataspaceId) {
        this.dataspaceId = dataspaceId;
    }

    @SuppressWarnings("unused")
    public Boolean getEnabled() {
        return enabled;
    }

    @SuppressWarnings("unused")
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    private void updateIngestToken(String ingestToken) throws IOException {
        SystemCredentialsProvider provider = SystemCredentialsProvider.getInstance();

        // TODO: There might be a smarter way of updating.
        // Now I am just removing the old and adding the new.

        provider.getCredentials().removeIf(credentials -> {
            if (credentials instanceof StandardUsernamePasswordCredentials) {
                StandardUsernamePasswordCredentials cc = (StandardUsernamePasswordCredentials)credentials;
                return HUMIO_CREDENTIAL_KEY.equals(cc.getId());
            }
            return false;
        });

        provider.getCredentials()
                .add(new UsernamePasswordCredentialsImpl(
                        CredentialsScope.GLOBAL,
                        HUMIO_CREDENTIAL_KEY,
                        "Humio Ingest Token",
                        "HumioPlugin", ingestToken
                ));

        provider.save();
    }
}

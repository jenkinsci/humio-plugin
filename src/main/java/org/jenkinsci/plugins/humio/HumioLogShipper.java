package org.jenkinsci.plugins.humio;

import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// TODO: find an appropriate extension point for this so we can do init and destroy.
public class HumioLogShipper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HumioLogShipper.class);

    private static final HttpClient httpClient = new HttpClient();
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final BlockingQueue<Event> queue = new ArrayBlockingQueue<>(10000);

    private static final String apiToken = "SomeToken"

    static {{
      executorService.scheduleAtFixedRate(HumioLogShipper::ship, 0, 500, TimeUnit.MILLISECONDS);
    }}

    public static void send(String line, int buildNumber, String jobName) {
        // It is important to do the timestamp BEFORE we queue the event.
        queue.add(new Event(line, java.time.Instant.now(), buildNumber, jobName));
    }

    private static void ship() {
        if (queue.isEmpty()) return;

        // TODO: Warn if the queue is full. Either to Jenkins Admin or Log it to Humio?

        List<Event> events = new ArrayList<>(queue.size());
        queue.drainTo(events);

        // We want events in the order they were added, this is for
        // events that share the same timestamp so they appear in the
        // right order in Humio.
        events = Lists.reverse(events);

        JSONObject json = new JSONObject();
        json.put("events", events.parallelStream().map(HumioLogShipper::toEventJson).collect(ArrayList::new, ArrayList::add, ArrayList::addAll));

        JSONObject tags = new JSONObject();
        tags.put("host", "jenkins");
        json.put("tags", tags);

        JSONArray requestData = new JSONArray();
        requestData.add(json);

        try {
            HumioConfig config = HumioConfig.getInstance();

            if (!HumioConfig.isValid()) {
                LOGGER.warn("Trying to send logs. But Humio Plugin is not configured yet.");
                return;
            }

            PostMethod post = new PostMethod(ingestURL(config.getDataspaceId()));
            post.setRequestHeader("Authorization", "Bearer " + config.getAuthToken());
            post.setRequestEntity(new StringRequestEntity(requestData.toString(),  "application/json", "utf-8"));
            int statusCode = httpClient.executeMethod(post);

            if (statusCode >= 400) {
                LOGGER.error("Failed to send logs to Humio. Got bad response code. code={} response={}", statusCode, post.getResponseBodyAsString());
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Failed to send logs to Humio. serverURL={}", HumioConfig.getInstance().getServerURL(), e);
        }
    }


    private static String ingestURL(String dataspaceId) throws URISyntaxException {
        HumioConfig config = HumioConfig.getInstance();

        // Remove any double '/' and so on, since the user may have included them in the serverURL.
        URI uri = new URI(config.getServerURL() + "/api/v1/dataspaces/" + dataspaceId + "/ingest");
        uri = uri.normalize();

        return uri.toString();
    }

    private static JSONObject toEventJson(Event event) {
        JSONObject json = new JSONObject();
        json.put("timestamp", event.timestamp.toString());
        json.put("rawstring", event.data);

        JSONObject attributes = new JSONObject();
        attributes.put("buildNumber", event.buildNumber);
        attributes.put("jobName", event.jobName);
        json.put("attributes", attributes);

        return json;
    }

    private static class Event {
        Event(String data, Instant timestamp, int buildNumber, String jobName) {
            this.timestamp = timestamp;
            this.data = data;
            this.buildNumber = buildNumber;
            this.jobName = jobName;
        }
        Instant timestamp;
        String data;
        int buildNumber;
        String jobName;
    }
}

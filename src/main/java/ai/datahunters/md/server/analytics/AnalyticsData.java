package ai.datahunters.md.server.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class AnalyticsData {

    @JsonProperty("images_number")
    private long photoCount;

}

package ai.datahunters.md.server.analytics;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalyticsData {

    @JsonProperty("PhotoCount")
    private long photoCount;

}

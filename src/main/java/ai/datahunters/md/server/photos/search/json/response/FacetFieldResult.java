package ai.datahunters.md.server.photos.search.json.response;

import lombok.Value;

import java.util.Map;


@Value
public class FacetFieldResult {
    private Map<String, Long> results;
}

package uk.co.autotrader.traverson.conversion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HalResource<E> {

    @JsonProperty("_embedded")
    private Map<String, List<E>> embedded = Collections.emptyMap();

    @JsonProperty("_links")
    private Map<String, Link> links = Collections.emptyMap();

    public Map<String, List<E>> getEmbedded() {
        return embedded;
    }

    public void setEmbedded(Map<String, List<E>> embedded) {
        this.embedded = embedded;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }
}

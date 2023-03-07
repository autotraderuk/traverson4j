package uk.co.autotrader.traverson.link;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.autotrader.traverson.exception.UnknownRelException;
import uk.co.autotrader.traverson.link.hal.HalLinkDiscoverer;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HalLinkDiscovererTest {
    private LinkDiscoverer linkDiscoverer;

    @BeforeEach
    void setUp() {
        this.linkDiscoverer = new HalLinkDiscoverer();
    }

    @Test
    void findHref_GivenHalResource_ReturnsSelfHref() throws Exception {
        JSONObject json = getJsonResource("hal-simple.json");

        String url = this.linkDiscoverer.findHref(json, "self");

        assertThat(url).isEqualTo("http://localhost:8080/");
    }

    @Test
    void findHref_GivenRelDoesntExist_ThrowsUnknownRelException() throws Exception {
        JSONObject json = getJsonResource("hal-simple.json");

        assertThatThrownBy(() -> this.linkDiscoverer.findHref(json, "doesNotExist"))
                .isInstanceOf(UnknownRelException.class)
                .hasMessage("Rel doesNotExist not in the following [domains, self]");
    }

    @Test
    void findHref_GivenHalResourceAndDomainsRel_ReturnsDomainsHref() throws Exception {
        JSONObject json = getJsonResource("hal-simple.json");

        String url = this.linkDiscoverer.findHref(json, "domains");

        assertThat(url).isEqualTo("http://localhost:8080/domains");
    }

    @Test
    void findHref_GivenHalResourceWithEmbedded_ReturnsSelfHrefForFirstItemMatchedByName() throws Exception {
        JSONObject json = getJsonResource("hal-embedded.json");

        String url = this.linkDiscoverer.findHref(json, "AutoTrader");

        assertThat(url).isEqualTo("http://localhost:8080/domains/autotrader");
    }

    @Test
    void findHref_GivenHalResourceWithEmbeddedAndRelDoesNotMatchNamedItem_ReturnsSelfHrefFromRootLinks() throws Exception {
        JSONObject json = getJsonResource("hal-embedded.json");

        String url = this.linkDiscoverer.findHref(json, "self");

        assertThat(url).isEqualTo("http://localhost:8080/domains");
    }

    @Test
    void findHref_GivenHalResourceWithEmptyEmbedded_ReturnsSelfHrefFromRootLinks() throws Exception {
        JSONObject json = getJsonResource("hal-empty-embedded.json");

        String url = this.linkDiscoverer.findHref(json, "self");

        assertThat(url).isEqualTo("http://localhost:8080/vehicles");
    }

    @Test
    void findHref_GivenHalResourceWithLinkArray_ReturnsMatchingHrefFromArray() throws Exception {
        JSONObject json = getJsonResource("hal-keyed-link-rels.json");

        String url = this.linkDiscoverer.findHref(json, "section[name:turnip]");

        assertThat(url).isEqualTo("http://turnip-link");
    }

    @Test
    void findHref_GivenPropertyBasedRelDoesntExist_throwsException() throws Exception {
        JSONObject json = getJsonResource("hal-traverson-builder-data.json");

        assertThatThrownBy(() -> this.linkDiscoverer.findHref(json, "non-existent[key:value]"))
                .isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'non-existent' with an item with property 'key: value' not found in {")
                .hasMessageContaining("'_links'=[makes, self, vegetables]")
                .hasMessageContaining("'_embedded'=[ships, vegetables]");
    }

    @Test
    void findHref_GivenHalResourceWithLinkArrayIndex_ReturnsMatchingHrefFromArray() throws Exception {
        JSONObject json = getJsonResource("hal-keyed-link-rels.json");

        String url = this.linkDiscoverer.findHref(json, "section[1]");

        assertThat(url).isEqualTo("http://spatula-link");
    }

    @Test
    void findHref_GivenNonExistentArrayIndex_throwsException() throws Exception {
        JSONObject json = getJsonResource("hal-traverson-builder-data.json");

        assertThatThrownBy(() -> this.linkDiscoverer.findHref(json, "non-existent[0]"))
                .isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'non-existent' with an item at index '0' not found in {")
                .hasMessageContaining("'_links'=[makes, self, vegetables]")
                .hasMessageContaining("'_embedded'=[ships, vegetables]");
    }

    private JSONObject getJsonResource(String resourceName) throws IOException {
        return JSON.parseObject(Resources.toString(getResource(resourceName), Charset.defaultCharset()));
    }
}

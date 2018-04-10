package uk.co.autotrader.traverson.link;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.autotrader.traverson.exception.UnknownRelException;
import uk.co.autotrader.traverson.link.hal.HalLinkDiscoverer;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class HalLinkDiscovererTest {
    private LinkDiscoverer linkDiscoverer;

    @Before
    public void setUp() throws Exception {
        this.linkDiscoverer = new HalLinkDiscoverer();
    }

    @Test
    public void findHref_GivenHalResource_ReturnsSelfHref() throws Exception {
        JSONObject json = getJsonResource("hal-simple.json");

        String url = this.linkDiscoverer.findHref(json, "self");

        assertThat(url).isEqualTo("http://localhost:8080/");
    }

    @Test
    public void findHref_GivenRelDoesntExist_ThrowsUnknownRelException() throws Exception {
        JSONObject json = getJsonResource("hal-simple.json");

        try {
            this.linkDiscoverer.findHref(json, "doesNotExist");
            fail("Test should throw exception for missing link");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessage("Rel doesNotExist not in the following [domains, self]");
        }

    }

    @Test
    public void findHref_GivenHalResourceAndDomainsRel_ReturnsDomainsHref() throws Exception {
        JSONObject json = getJsonResource("hal-simple.json");

        String url = this.linkDiscoverer.findHref(json, "domains");

        assertThat(url).isEqualTo("http://localhost:8080/domains");
    }

    @Test
    public void findHref_GivenHalResourceWithEmbedded_ReturnsSelfHrefForFirstItemMatchedByName() throws Exception {
        JSONObject json = getJsonResource("hal-embedded.json");

        String url = this.linkDiscoverer.findHref(json, "AutoTrader");

        assertThat(url).isEqualTo("http://localhost:8080/domains/autotrader");
    }

    @Test
    public void findHref_GivenHalResourceWithEmbeddedAndRelDoesNotMatchNamedItem_ReturnsSelfHrefFromRootLinks() throws Exception {
        JSONObject json = getJsonResource("hal-embedded.json");

        String url = this.linkDiscoverer.findHref(json, "self");

        assertThat(url).isEqualTo("http://localhost:8080/domains");
    }

    @Test
    public void findHref_GivenHalResourceWithEmptyEmbedded_ReturnsSelfHrefFromRootLinks() throws Exception {
        JSONObject json = getJsonResource("hal-empty-embedded.json");

        String url = this.linkDiscoverer.findHref(json, "self");

        assertThat(url).isEqualTo("http://localhost:8080/vehicles");
    }

    @Test
    public void findHref_GivenHalResourceWithLinkArray_ReturnsMatchingHrefFromArray() throws Exception {
        JSONObject json = getJsonResource("hal-keyed-link-rels.json");

        String url = this.linkDiscoverer.findHref(json, "section[name:turnip]");

        assertThat(url).isEqualTo("http://turnip-link");
    }

    @Test
    public void findHref_GivenPropertyBasedRelDoesntExist_throwsException() throws Exception {
        JSONObject json = getJsonResource("hal-traverson-builder-data.json");

        try {
            this.linkDiscoverer.findHref(json, "non-existent[key:value]");
            fail("Should throw exception when rel not found");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'non-existent' with an item with property 'key: value' not found in {")
                    .hasMessageContaining("'_links'=[makes, self, vegetables]")
                    .hasMessageContaining("'_embedded'=[ships, vegetables]");
        }
    }

    @Test
    public void findHref_GivenHalResourceWithLinkArrayIndex_ReturnsMatchingHrefFromArray() throws Exception {
        JSONObject json = getJsonResource("hal-keyed-link-rels.json");

        String url = this.linkDiscoverer.findHref(json, "section[1]");

        assertThat(url).isEqualTo("http://spatula-link");
    }

    @Test
    public void findHref_GivenNonExistentArrayIndex_throwsException() throws Exception {
        JSONObject json = getJsonResource("hal-traverson-builder-data.json");

        try {
            this.linkDiscoverer.findHref(json, "non-existent[0]");
            fail("Should throw exception when rel not found");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'non-existent' with an item at index '0' not found in {")
                    .hasMessageContaining("'_links'=[makes, self, vegetables]")
                    .hasMessageContaining("'_embedded'=[ships, vegetables]");
        }
    }

    private JSONObject getJsonResource(String resourceName) throws IOException {
        return JSON.parseObject(Resources.toString(getResource(resourceName), Charset.defaultCharset()));
    }
}
package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import uk.co.autotrader.traverson.exception.UnknownRelException;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.*;

class RelByArrayIndexDiscovererTest {

    private static final LinksResolver LINKS_RESOLVER = new LinksResolver();
    private static final EmbeddedResolver EMBEDDED_RESOLVER = new EmbeddedResolver();
    private final RelByArrayIndexDiscoverer testHandler = new RelByArrayIndexDiscoverer(LINKS_RESOLVER, EMBEDDED_RESOLVER);

    @Test
    void findHref_GivenNonMatchingSyntax_ReturnsNull() throws Exception {
        assertThat(this.testHandler.findHref(testJson(), "self")).isNull();
    }

    @Test
    void findHref_GivenNonNumber_ReturnsNull() throws Exception {
        assertThat(this.testHandler.findHref(testJson(), "array[first]")).isNull();
    }

    @Test
    void findHref_GivenNegativeNumber_ReturnsNull() throws Exception {
        assertThat(this.testHandler.findHref(testJson(), "array[-1]")).isNull();
    }

    @Test
    void findHref_GivenLinksResolverAndRelDoesntExist_throwsException() {
        RelByArrayIndexDiscoverer linksResolverHandler = new RelByArrayIndexDiscoverer(LINKS_RESOLVER);

        assertThatThrownBy(() -> {
            linksResolverHandler.findHref(testJson(), "doesnt-exist[0]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessage("Rel 'doesnt-exist' with an item at index '0' not found in {'_links'=[array, not-array, self]}")
                .hasMessageNotContaining("_embedded");
    }

    @Test
    void findHref_GivenEmbeddedResolverAndRelDoesntExist_throwsException() {
        RelByArrayIndexDiscoverer embeddedResolverHandler = new RelByArrayIndexDiscoverer(EMBEDDED_RESOLVER);

        assertThatThrownBy(() -> {
            embeddedResolverHandler.findHref(testJson(), "doesnt-exist[0]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessage("Rel 'doesnt-exist' with an item at index '0' not found in {'_embedded'=[array, not-array]}")
                .hasMessageNotContaining("_links");
    }

    @Test
    void findHref_GivenBothResolversAndRelDoesntExist_throwsException() {
        assertThatThrownBy(() -> {
            this.testHandler.findHref(testJson(), "doesnt-exist[0]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'doesnt-exist' with an item at index '0' not found in")
                .hasMessageContaining("'_embedded'=[array, not-array]")
                .hasMessageContaining("'_links'=[array, not-array, self]");
    }

    @Test
    void findHref_GivenLinksResolverAndNonArrayRel_throwsException() {
        RelByArrayIndexDiscoverer linksResolverHandler = new RelByArrayIndexDiscoverer(LINKS_RESOLVER);

        assertThatThrownBy(() -> {
            linksResolverHandler.findHref(testJson(), "not-array[0]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessage("Rel 'not-array' with an item at index '0' not found in {'_links'=[array, not-array, self]}");
    }

    @Test
    void findHref_GivenEmbeddedResolverAndNonArrayRel_throwsException() {
        RelByArrayIndexDiscoverer embeddedResolverHandler = new RelByArrayIndexDiscoverer(EMBEDDED_RESOLVER);

        assertThatThrownBy(() -> {
            embeddedResolverHandler.findHref(testJson(), "not-array[0]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessage("Rel 'not-array' with an item at index '0' not found in {'_embedded'=[array, not-array]}")
                .hasMessageNotContaining("_links");
    }

    @Test
    void findHref_GivenBothResolversAndNonArrayRel_throwsException() {
        assertThatThrownBy(() -> {
            this.testHandler.findHref(testJson(), "not-array[0]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'not-array' with an item at index '0' not found in")
                .hasMessageContaining("'_embedded'=[array, not-array]")
                .hasMessageContaining("'_links'=[array, not-array, self]");
    }

    @Test
    void findHref_GivenResourceWithNoEmbeddedAndUnknownRel_throwsException() throws Exception {
        JSONObject resource = JSON.parseObject(Resources.toString(getResource("hal-simple.json"), Charset.defaultCharset()));

        assertThatThrownBy(() -> {
            this.testHandler.findHref(resource, "not-exists[0]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'not-exists' with an item at index '0' not found in")
                .hasMessageNotContaining("_embedded");
    }

    @Test
    void findHref_GivenArrayPosition_ReturnsHref() throws Exception {
        assertThat(this.testHandler.findHref(testJson(), "array[0]")).isEqualTo("http://first");
    }

    @Test
    void findHref_GivenSecondArrayPosition_ReturnsHref() throws Exception {
        assertThat(this.testHandler.findHref(testJson(), "array[1]")).isEqualTo("http://second");
    }

    @Test
    void findHref_GivenLinksResolverAndNonExistentArrayPosition_throwsException() {
        RelByArrayIndexDiscoverer linksResolverHandler = new RelByArrayIndexDiscoverer(LINKS_RESOLVER);

        assertThatThrownBy(() -> {
            linksResolverHandler.findHref(testJson(), "array[2]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessage("Rel 'array' with an item at index '2' not found in {'_links'=[array, not-array, self]}")
                .hasMessageNotContaining("_embedded");
    }

    @Test
    void findHref_GivenEmbeddedResolverAndNonExistentArrayPosition_throwsException() {
        RelByArrayIndexDiscoverer embeddedResolverHandler = new RelByArrayIndexDiscoverer(EMBEDDED_RESOLVER);

        assertThatThrownBy(() -> {
            embeddedResolverHandler.findHref(testJson(), "array[2]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessage("Rel 'array' with an item at index '2' not found in {'_embedded'=[array, not-array]}")
                .hasMessageNotContaining("_links");
    }

    @Test
    void findHref_GivenBothResolverAndNonExistentArrayPosition_throwsException() {
        assertThatThrownBy(() -> {
            this.testHandler.findHref(testJson(), "array[2]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'array' with an item at index '2' not found in")
                .hasMessageContaining("'_embedded'=[array, not-array]")
                .hasMessageContaining("'_links'=[array, not-array, self]");
    }

    private JSONObject testJson() throws IOException {
        return JSON.parseObject(Resources.toString(getResource("hal-array-rels-test-data.json"), Charset.defaultCharset()));
    }
}

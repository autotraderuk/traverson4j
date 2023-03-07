package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import uk.co.autotrader.traverson.exception.UnknownRelException;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.*;

class RelByArrayPropertyDiscovererTest {

    private static final LinksResolver LINKS_RESOLVER = new LinksResolver();
    private static final EmbeddedResolver EMBEDDED_RESOLVER = new EmbeddedResolver();

    private final RelByArrayPropertyDiscoverer testSubject = new RelByArrayPropertyDiscoverer(LINKS_RESOLVER, EMBEDDED_RESOLVER);

    @Test
    void findHref_GivenRelWithoutProperty_ReturnsNull() throws Exception {
        assertThat(this.testSubject.findHref(testJson(), "self")).isNull();
    }

    @Test
    void findHref_GivenLinksResolverAndNonExistentRel_throwsException() {
        RelByArrayPropertyDiscoverer discoverer = new RelByArrayPropertyDiscoverer(LINKS_RESOLVER);

        assertThatThrownBy(() -> {
            discoverer.findHref(testJson(), "non-existent[key:value]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'non-existent' with an item with property 'key: value' not found in {'_links'=[duplicates, not-array, section, self]")
                .hasMessageNotContaining("_embedded");
    }

    @Test
    void findHref_GivenEmbeddedResolverAndNonExistentRel_throwsException() {
        RelByArrayPropertyDiscoverer discoverer = new RelByArrayPropertyDiscoverer(EMBEDDED_RESOLVER);

        assertThatThrownBy(() -> {
            discoverer.findHref(testJson(), "non-existent[key:value]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'non-existent' with an item with property 'key: value' not found in {'_embedded'=[duplicates, not-array, section]")
                .hasMessageNotContaining("_links");
    }

    @Test
    void findHref_GivenRelWithPropertyThatExists_ReturnsRelatedHref() throws Exception {
        assertThat(this.testSubject.findHref(testJson(), "section[name:turnip]")).isEqualTo("http://turnip-link");
    }

    @Test
    void findHref_GivenLinksResolverAndRelWithPropertyNotExisting_throwsException() {
        RelByArrayPropertyDiscoverer discoverer = new RelByArrayPropertyDiscoverer(LINKS_RESOLVER);

        assertThatThrownBy(() -> {
            discoverer.findHref(testJson(), "section[name:notexist]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'section' with an item with property 'name: notexist' not found in {'_links'=[duplicates, not-array, section, self]")
                .hasMessageNotContaining("_embedded");
    }

    @Test
    void findHref_GivenEmbeddedResolverAndRelWithPropertyNotExisting_throwsException() {
        RelByArrayPropertyDiscoverer discoverer = new RelByArrayPropertyDiscoverer(EMBEDDED_RESOLVER);

        assertThatThrownBy(() -> {
            discoverer.findHref(testJson(), "section[name:notexist]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'section' with an item with property 'name: notexist' not found in {'_embedded'=[duplicates, not-array, section]")
                .hasMessageNotContaining("_links");
    }

    @Test
    void findHref_GivenBothResolversAndRelWithPropertyNotExisting_throwsException() {
        assertThatThrownBy(() -> {
            this.testSubject.findHref(testJson(), "section[name:notexist]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'section' with an item with property 'name: notexist' not found in")
                .hasMessageContaining("'_embedded'=[duplicates, not-array, section]")
                .hasMessageContaining("'_links'=[duplicates, not-array, section, self]");
    }

    @Test
    void findHref_GivenLinksResolverAndRelThatIsntArray_throwsException() {
        RelByArrayPropertyDiscoverer discoverer = new RelByArrayPropertyDiscoverer(LINKS_RESOLVER);

        assertThatThrownBy(() -> {
            discoverer.findHref(testJson(), "self[prop:value]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'self' with an item with property 'prop: value' not found in {'_links'=[duplicates, not-array, section, self]")
                .hasMessageNotContaining("_embedded");
    }

    @Test
    void findHref_GivenEmbeddedResolverAndRelThatIsntArray_throwsException() {
        RelByArrayPropertyDiscoverer discoverer = new RelByArrayPropertyDiscoverer(EMBEDDED_RESOLVER);

        assertThatThrownBy(() -> {
            discoverer.findHref(testJson(), "self[prop:value]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'self' with an item with property 'prop: value' not found in {'_embedded'=[duplicates, not-array, section]")
                .hasMessageNotContaining("_links");
    }

    @Test
    void findHref_GivenBothResolversAndRelThatIsntArray_throwsException() {
        assertThatThrownBy(() -> {
            this.testSubject.findHref(testJson(), "self[prop:value]");
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'self' with an item with property 'prop: value' not found in")
                .hasMessageContaining("'_embedded'=[duplicates, not-array, section]")
                .hasMessageContaining("'_links'=[duplicates, not-array, section, self]");
    }

    @Test
    void findHref_GivenRelWithDuplicateMatchingProperties_ReturnsFirstMatch() throws Exception {
        assertThat(this.testSubject.findHref(testJson(), "duplicates[name:dupe]")).isEqualTo("http://duplicate-link-1");
    }

    private JSONObject testJson() throws Exception {
        return JSON.parseObject(Resources.toString(getResource("hal-keyed-link-rels.json"), Charsets.UTF_8));
    }
}

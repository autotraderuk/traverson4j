package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.assertj.core.api.Condition;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.autotrader.traverson.exception.UnknownRelException;

import java.util.stream.Stream;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.condition.AnyOf.anyOf;

class RelByArrayPropertyDiscovererTest {

    private static Stream<Arguments> handlers() {
        return Stream.of(
                Arguments.of(new LinksResolver()),
                Arguments.of(new EmbeddedResolver())
        );
    }

    private RelByArrayPropertyDiscoverer testSubject;

    @ParameterizedTest
    @MethodSource("handlers")
    void findHref_GivenRelWithoutProperty_ReturnsNull(HalEntityResolver halEntityResolver) throws Exception {
        testSubject = new RelByArrayPropertyDiscoverer(halEntityResolver);
        assertThat(this.testSubject.findHref(testJson(), "self")).isNull();
    }

    @ParameterizedTest
    @MethodSource("handlers")
    void findHref_GivenNonExistentRel_throwsException(HalEntityResolver halEntityResolver) throws Exception {
        try {
            testSubject = new RelByArrayPropertyDiscoverer(halEntityResolver);
            this.testSubject.findHref(testJson(), "non-existent[key:value]");
            fail("Should throw exception");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'non-existent' with an item with property 'key: value' not found in")
                    .has(anyOf(
                            messageContaining("{'_links'=[duplicates, not-array, section, self]"),
                            messageContaining("{'_embedded'=[duplicates, not-array, section]")));
        }
    }

    @ParameterizedTest
    @MethodSource("handlers")
    void findHref_GivenRelWithPropertyThatExists_ReturnsRelatedHref(HalEntityResolver halEntityResolver) throws Exception {
        testSubject = new RelByArrayPropertyDiscoverer(halEntityResolver);
        assertThat(this.testSubject.findHref(testJson(), "section[name:turnip]")).isEqualTo("http://turnip-link");
    }

    @ParameterizedTest
    @MethodSource("handlers")
    void findHref_GivenRelWithPropertyNotExisting_throwsException(HalEntityResolver halEntityResolver) throws Exception {
        try {
            testSubject = new RelByArrayPropertyDiscoverer(halEntityResolver);
            this.testSubject.findHref(testJson(), "section[name:notexist]");
            fail("Should throw exception");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'section' with an item with property 'name: notexist' not found in")
                    .has(anyOf(
                            messageContaining("{'_links'=[duplicates, not-array, section, self]"),
                            messageContaining("{'_embedded'=[duplicates, not-array, section]")));
        }
    }

    @ParameterizedTest
    @MethodSource("handlers")
    void findHref_GivenRelThatIsntArray_throwsException(HalEntityResolver halEntityResolver) throws Exception {
        try {
            testSubject = new RelByArrayPropertyDiscoverer(halEntityResolver);
            this.testSubject.findHref(testJson(), "self[prop:value]");
            fail("Should throw exception");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'self' with an item with property 'prop: value' not found in")
                    .has(anyOf(
                            messageContaining("{'_links'=[duplicates, not-array, section, self]"),
                            messageContaining("{'_embedded'=[duplicates, not-array, section]")));
        }
    }

    @ParameterizedTest
    @MethodSource("handlers")
    void findHref_GivenRelWithDuplicateMatchingProperties_ReturnsFirstMatch(HalEntityResolver halEntityResolver) throws Exception {
        testSubject = new RelByArrayPropertyDiscoverer(halEntityResolver);
        assertThat(this.testSubject.findHref(testJson(), "duplicates[name:dupe]")).isEqualTo("http://duplicate-link-1");
    }

    private JSONObject testJson() throws Exception {
        return JSON.parseObject(Resources.toString(getResource("hal-keyed-link-rels.json"), Charsets.UTF_8));
    }

    private Condition<Throwable> messageContaining(final String expected) {
        return new Condition<Throwable>() {
            @Override
            public boolean matches(Throwable thrown) {
                return thrown.getMessage().contains(expected);
            }
        }.as("message contains %s", expected);
    }
}

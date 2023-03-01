package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.io.Resources;
import org.assertj.core.api.Condition;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.autotrader.traverson.exception.UnknownRelException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Stream;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class RelByArrayIndexDiscovererTest {

    private static Stream<Arguments> arrayIndexHandlers() {
        return Stream.of(
                Arguments.of(new LinksResolver()),
                Arguments.of(new EmbeddedResolver())
        );
    }

    private RelByArrayIndexDiscoverer testHandler;

    @ParameterizedTest
    @MethodSource("arrayIndexHandlers")
    void findHref_GivenNonMatchingSyntax_ReturnsNull(HalEntityResolver testEntityResolver) throws Exception {
        testHandler = new RelByArrayIndexDiscoverer(testEntityResolver);
        assertThat(this.testHandler.findHref(testJson(), "self")).isNull();
    }

    @ParameterizedTest
    @MethodSource("arrayIndexHandlers")
    void findHref_GivenNonNumber_ReturnsNull(HalEntityResolver testEntityResolver) throws Exception {
        testHandler = new RelByArrayIndexDiscoverer(testEntityResolver);
        assertThat(this.testHandler.findHref(testJson(), "array[first]")).isNull();
    }

    @ParameterizedTest
    @MethodSource("arrayIndexHandlers")
    void findHref_GivenNegativeNumber_ReturnsNull(HalEntityResolver testEntityResolver) throws Exception {
        testHandler = new RelByArrayIndexDiscoverer(testEntityResolver);
        assertThat(this.testHandler.findHref(testJson(), "array[-1]")).isNull();
    }

    @ParameterizedTest
    @MethodSource("arrayIndexHandlers")
    void findHref_GivenRelDoesntExist_throwsException(HalEntityResolver testEntityResolver) throws Exception {
        try {
            testHandler = new RelByArrayIndexDiscoverer(testEntityResolver);
            this.testHandler.findHref(testJson(), "doesnt-exist[0]");
            fail("Should throw exception");
        } catch (UnknownRelException e) {
            assertThat(e)
                    .hasMessageContaining("Rel 'doesnt-exist' with an item at index '0' not found in")
                    .has(anyOf(
                            messageContaining("{'_links'=[array, not-array, self]}"),
                            messageContaining("{'_embedded'=[array, not-array]}")));
        }
    }

    @ParameterizedTest
    @MethodSource("arrayIndexHandlers")
    void findHref_GivenNonArrayRel_throwsException(HalEntityResolver testEntityResolver) throws Exception {
        try {
            testHandler = new RelByArrayIndexDiscoverer(testEntityResolver);
            this.testHandler.findHref(testJson(), "not-array[0]");
            fail("Should throw exception");
        } catch (UnknownRelException e) {
            assertThat(e)
                    .hasMessageContaining("Rel 'not-array' with an item at index '0' not found in")
                    .has(anyOf(
                            messageContaining("{'_links'=[array, not-array, self]}"),
                            messageContaining("{'_embedded'=[array, not-array]}")));
        }
    }

    @ParameterizedTest
    @MethodSource("arrayIndexHandlers")
    void findHref_GivenResourceWithNoEmbeddedAndUnknownRel_throwsException(HalEntityResolver testEntityResolver) throws Exception {
        try {
            JSONObject resource = JSON.parseObject(Resources.toString(getResource("hal-simple.json"), Charset.defaultCharset()));
            testHandler = new RelByArrayIndexDiscoverer(testEntityResolver);
            this.testHandler.findHref(resource, "not-exists[0]");
            fail("Should throw exception");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'not-exists' with an item at index '0' not found in");
            assertThat(e.getMessage()).doesNotContain("_embedded");
        }
    }

    @ParameterizedTest
    @MethodSource("arrayIndexHandlers")
    void findHref_GivenArrayPosition_ReturnsHref(HalEntityResolver testEntityResolver) throws Exception {
        testHandler = new RelByArrayIndexDiscoverer(testEntityResolver);
        assertThat(this.testHandler.findHref(testJson(), "array[0]")).isEqualTo("http://first");
    }

    @ParameterizedTest
    @MethodSource("arrayIndexHandlers")
    void findHref_GivenSecondArrayPosition_ReturnsHref(HalEntityResolver testEntityResolver) throws Exception {
        testHandler = new RelByArrayIndexDiscoverer(testEntityResolver);
        assertThat(this.testHandler.findHref(testJson(), "array[1]")).isEqualTo("http://second");
    }

    @ParameterizedTest
    @MethodSource("arrayIndexHandlers")
    void findHref_GivenNonExistentArrayPosition_throwsException(HalEntityResolver testEntityResolver) throws Exception {
        try {
            testHandler = new RelByArrayIndexDiscoverer(testEntityResolver);
            this.testHandler.findHref(testJson(), "array[2]");
            fail("Should throw exception");
        } catch (UnknownRelException e) {
            assertThat(e)
                    .hasMessageContaining("Rel 'array' with an item at index '2' not found in")
                    .has(anyOf(
                            messageContaining("{'_links'=[array, not-array, self]}"),
                            messageContaining("{'_embedded'=[array, not-array]}")));
        }
    }

    private JSONObject testJson() throws IOException {
        return JSON.parseObject(Resources.toString(getResource("hal-array-rels-test-data.json"), Charset.defaultCharset()));
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

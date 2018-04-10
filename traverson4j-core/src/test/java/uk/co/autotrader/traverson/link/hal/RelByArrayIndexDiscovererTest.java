package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import uk.co.autotrader.traverson.exception.UnknownRelException;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class RelByArrayIndexDiscovererTest {

    @Parameters(name = "{0}")
    public static Object[][] arrayIndexHandlers() {
        return new Object[][]{
                {new LinksResolver()},
                {new EmbeddedResolver()}
        };
    }

    public RelByArrayIndexDiscovererTest(HalEntityResolver testEntityResolver) {
        this.testHandler = new RelByArrayIndexDiscoverer(testEntityResolver);
    }

    private final RelByArrayIndexDiscoverer testHandler;

    @Test
    public void findHref_GivenNonMatchingSyntax_ReturnsNull() throws Exception {
        assertThat(this.testHandler.findHref(testJson(), "self")).isNull();
    }

    @Test
    public void findHref_GivenNonNumber_ReturnsNull() throws Exception {
        assertThat(this.testHandler.findHref(testJson(), "array[first]")).isNull();
    }

    @Test
    public void findHref_GivenNegativeNumber_ReturnsNull() throws Exception {
        assertThat(this.testHandler.findHref(testJson(), "array[-1]")).isNull();
    }

    @Test
    public void findHref_GivenRelDoesntExist_throwsException() throws Exception {
        try {
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

    @Test
    public void findHref_GivenNonArrayRel_throwsException() throws Exception {
        try {
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

    @Test
    public void findHref_GivenResourceWithNoEmbeddedAndUnknownRel_throwsException() throws Exception {
        try {
            JSONObject resource = JSON.parseObject(Resources.toString(getResource("hal-simple.json"), Charset.defaultCharset()));
            this.testHandler.findHref(resource, "not-exists[0]");
            fail("Should throw exception");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'not-exists' with an item at index '0' not found in");
            assertThat(e.getMessage()).doesNotContain("_embedded");
        }
    }

    @Test
    public void findHref_GivenArrayPosition_ReturnsHref() throws Exception {
        assertThat(this.testHandler.findHref(testJson(), "array[0]")).isEqualTo("http://first");
    }

    @Test
    public void findHref_GivenSecondArrayPosition_ReturnsHref() throws Exception {
        assertThat(this.testHandler.findHref(testJson(), "array[1]")).isEqualTo("http://second");
    }

    @Test
    public void findHref_GivenNonExistentArrayPosition_throwsException() throws Exception {
        try {
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

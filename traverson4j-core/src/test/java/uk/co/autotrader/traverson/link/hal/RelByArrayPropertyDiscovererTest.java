package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import uk.co.autotrader.traverson.exception.UnknownRelException;
import uk.co.autotrader.traverson.link.hal.entity.EmbeddedResolver;
import uk.co.autotrader.traverson.link.hal.entity.LinksResolver;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.condition.AnyOf.anyOf;

@RunWith(Parameterized.class)
public class RelByArrayPropertyDiscovererTest {

    @Parameters
    public static Object[][] handlers() {
        return new Object[][]{
                {new LinksResolver()},
                {new EmbeddedResolver()}
        };
    }

    private final RelByArrayPropertyDiscoverer testSubject;

    public RelByArrayPropertyDiscovererTest(HalEntityResolver halEntityResolver) {
        this.testSubject = new RelByArrayPropertyDiscoverer(halEntityResolver);
    }

    @Test
    public void findHref_GivenRelWithoutProperty_ReturnsNull() throws Exception {
        assertThat(this.testSubject.findHref(testJson(), "self")).isNull();
    }

    @Test
    public void findHref_GivenNonExistentRel_throwsException() throws Exception {
        try {
            this.testSubject.findHref(testJson(), "non-existent[key:value]");
            fail("Should throw exception");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'non-existent' with an item with property 'key: value' not found in")
                    .has(anyOf(
                            messageContaining("{'_links'=[duplicates, not-array, section, self]"),
                            messageContaining("{'_embedded'=[duplicates, not-array, section]")));
        }
    }

    @Test
    public void findHref_GivenRelWithPropertyThatExists_ReturnsRelatedHref() throws Exception {
        assertThat(this.testSubject.findHref(testJson(), "section[name:turnip]")).isEqualTo("http://turnip-link");
    }

    @Test
    public void findHref_GivenRelWithPropertyNotExisting_throwsException() throws Exception {
        try {
            this.testSubject.findHref(testJson(), "section[name:notexist]");
            fail("Should throw exception");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'section' with an item with property 'name: notexist' not found in")
                    .has(anyOf(
                            messageContaining("{'_links'=[duplicates, not-array, section, self]"),
                            messageContaining("{'_embedded'=[duplicates, not-array, section]")));
        }
    }

    @Test
    public void findHref_GivenRelThatIsntArray_throwsException() throws Exception {
        try {
            this.testSubject.findHref(testJson(), "self[prop:value]");
            fail("Should throw exception");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'self' with an item with property 'prop: value' not found in")
                    .has(anyOf(
                            messageContaining("{'_links'=[duplicates, not-array, section, self]"),
                            messageContaining("{'_embedded'=[duplicates, not-array, section]")));
        }
    }

    @Test
    public void findHref_GivenRelWithDuplicateMatchingProperties_ReturnsFirstMatch() throws Exception {
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

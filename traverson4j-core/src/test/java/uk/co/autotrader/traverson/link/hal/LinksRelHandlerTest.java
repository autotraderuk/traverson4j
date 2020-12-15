package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.autotrader.traverson.exception.UnknownRelException;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class LinksRelHandlerTest {

    private LinksRelHandler handler;

    @Before
    public void setUp() throws Exception {
        this.handler = new LinksRelHandler();
    }

    @Test
    public void findHref_GivenJsonContainsExpectedRel_ReturnsHref() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("hal-simple.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        String url = this.handler.findHref(json, "self");

        assertThat(url).isEqualTo("http://localhost:8080/");
    }

    @Test
    public void findHref_GivenJsonDoesNotContainExpectedRel_ThrowsException() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("hal-simple.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        try {
            this.handler.findHref(json, "doesNotExist");
            fail("Test should throw exception for missing link");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessage("Rel doesNotExist not in the following [domains, self]");
        }
    }
}

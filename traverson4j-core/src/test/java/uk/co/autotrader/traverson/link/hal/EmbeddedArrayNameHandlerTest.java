package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class EmbeddedArrayNameHandlerTest {

    private EmbeddedArrayNameHandler handler;

    @Before
    public void setUp() throws Exception {
        this.handler = new EmbeddedArrayNameHandler();
    }

    @Test
    public void findRef_GivenNamedEntityExists_ReturnsSelfLinkOfUrl() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("hal-embedded.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        String url = this.handler.findHref(json, "AutoTrader");

        assertThat(url).isEqualTo("http://localhost:8080/domains/autotrader");
    }

    @Test
    public void findRef_GivenNameOfEntityThatDoesNotExist_ReturnsNull() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("hal-embedded.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        String url = this.handler.findHref(json, "NotExisting");

        assertThat(url).isNull();
    }

    @Test
    public void findRef_GivenResourceWithNoEmbedded_ReturnsNull() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("hal-simple.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        String url = this.handler.findHref(json, "self");

        assertThat(url).isNull();
    }
}

package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EmbeddedArrayNameHandlerTest {

    private EmbeddedArrayNameHandler handler;

    @BeforeEach
    void setUp() {
        this.handler = new EmbeddedArrayNameHandler();
    }

    @Test
    void findRef_GivenNamedEntityExists_ReturnsSelfLinkOfUrl() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("hal-embedded.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        String url = this.handler.findHref(json, "AutoTrader");

        assertThat(url).isEqualTo("http://localhost:8080/domains/autotrader");
    }

    @Test
    void findRef_GivenNameOfEntityThatDoesNotExist_ReturnsNull() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("hal-embedded.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        String url = this.handler.findHref(json, "NotExisting");

        assertThat(url).isNull();
    }

    @Test
    void findRef_GivenResourceWithNoEmbedded_ReturnsNull() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("hal-simple.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        String url = this.handler.findHref(json, "self");

        assertThat(url).isNull();
    }
}

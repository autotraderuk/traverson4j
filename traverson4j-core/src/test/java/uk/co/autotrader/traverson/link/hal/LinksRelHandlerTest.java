package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.autotrader.traverson.exception.UnknownRelException;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class LinksRelHandlerTest {

    private LinksRelHandler handler;

    @BeforeEach
    void setUp() {
        this.handler = new LinksRelHandler();
    }

    @Test
    void findHref_GivenJsonContainsExpectedRel_ReturnsHref() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("hal-simple.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        String url = this.handler.findHref(json, "self");

        assertThat(url).isEqualTo("http://localhost:8080/");
    }

    @Test
    void findHref_GivenJsonDoesNotContainExpectedRel_ThrowsException() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("hal-simple.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        assertThatThrownBy(() -> this.handler.findHref(json, "doesNotExist"))
                .isInstanceOf(UnknownRelException.class)
                .hasMessage("Rel doesNotExist not in the following [domains, self]");
    }
}

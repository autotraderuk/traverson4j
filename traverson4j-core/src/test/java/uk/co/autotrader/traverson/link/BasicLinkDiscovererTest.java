package uk.co.autotrader.traverson.link;

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
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class BasicLinkDiscovererTest {
    private LinkDiscoverer linkDiscoverer;

    @BeforeEach
    void setUp() {
        linkDiscoverer = new BasicLinkDiscoverer();
    }

    @Test
    void findHref_GivenBasicHypermediaResource_ReturnsHref() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("basic-hypermedia-simple.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        String uri = linkDiscoverer.findHref(json, "link_to");

        assertThat(uri).isEqualTo("http://api.example.com/follow/me");
    }

    @Test
    void findHref_GivenBasicHypermediaResourceAndRelNotExisting_ThrowsException() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("basic-hypermedia-simple.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        try {
            linkDiscoverer.findHref(json, "NotExistingRel");
            fail("Test should throw exception for missing link");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessage("Rel NotExistingRel not found");
        }
    }
}

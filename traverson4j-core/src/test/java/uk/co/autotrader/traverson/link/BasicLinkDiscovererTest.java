package uk.co.autotrader.traverson.link;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.autotrader.traverson.exception.UnknownRelException;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class BasicLinkDiscovererTest {
    private LinkDiscoverer linkDiscoverer;

    @Before
    public void setUp() throws Exception {
        linkDiscoverer = new BasicLinkDiscoverer();
    }

    @Test
    public void findHref_GivenBasicHypermediaResource_ReturnsHref() throws Exception {
        String fileContents = Resources.toString(Resources.getResource("basic-hypermedia-simple.json"), Charset.defaultCharset());
        JSONObject json = JSON.parseObject(fileContents);

        String uri = linkDiscoverer.findHref(json, "link_to");

        assertThat(uri).isEqualTo("http://api.example.com/follow/me");
    }

    @Test
    public void findHref_GivenBasicHypermediaResourceAndRelNotExisting_ThrowsException() throws Exception {
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
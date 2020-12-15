package uk.co.autotrader.traverson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import uk.co.autotrader.traverson.exception.UnknownRelException;
import uk.co.autotrader.traverson.http.Request;
import uk.co.autotrader.traverson.http.Response;
import uk.co.autotrader.traverson.http.TraversonClient;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TraversonBuilderFollowsTest {
    @Mock
    private TraversonClient client;

    private TraversonBuilder testSubject;

    @Before
    public void mockClientToResolveUrlsFromClasspath() {
        when(this.client.execute(any(Request.class), any(Class.class))).thenAnswer(
                new Answer<Response<JSONObject>>() {
                    @Override
                    public Response<JSONObject> answer(InvocationOnMock invocation) throws Throwable {
                        Request request = invocation.getArgument(0, Request.class);
                        JSONObject resource = getJsonResource(request.getUrl());

                        Response<JSONObject> response = new Response<JSONObject>();
                        if (resource != null) {
                            response.setStatusCode(200);
                            response.setResource(resource);
                        } else {
                            response.setStatusCode(404);
                        }

                        return response;
                    }
                }
        );

    }

    @Before
    public void instantiateNewBuilder() {
        this.testSubject = new TraversonBuilder(this.client);
    }

    @Test
    public void follows_GivenSimpleRelation_FollowsLinkInLinksBlock() throws Exception {
        JSONObject result = this.testSubject.jsonHal()
                .from("hal-traverson-builder-data.json")
                .follow("makes")
                .get()
                .getResource();

        assertThat(result.toJSONString()).isEqualTo("{\"makes\":\"a bunch of makes.\"}");
    }

    @Test
    public void follows_GivenRelByPropertyMatchingBothLinksAndEmbedded_FollowsLinkInLinksBlock() {
        JSONObject result = this.testSubject.jsonHal()
                .from("hal-traverson-builder-data.json")
                .follow("vegetables[name:turnip]")
                .get()
                .getResource();

        assertThat(result.toJSONString()).isEqualTo("{\"i-am\":\"a turnip.\"}");
    }

    @Test
    public void follows_GivenRelByProperty_FollowsLinkInEmbeddedBlock() {
        JSONObject result = this.testSubject.jsonHal()
                .from("hal-traverson-builder-data.json")
                .follow("ships[name:tug-boat]")
                .get()
                .getResource();

        assertThat(result.toJSONString()).isEqualTo("{\"tug-boat\":\"true\"}");
    }

    @Test
    public void follows_GivenRelByPropertyThatIsNotAnArray_throwsException() throws Exception {
        try {
            this.testSubject.jsonHal()
                    .from("hal-traverson-builder-data.json")
                    .follow("makes[invalid:property]")
                    .get();
            fail("Test should throw an exception for an unknown rel");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'makes' with an item with property 'invalid: property' not found in {")
                    .hasMessageContaining("'_links'=[makes, self, vegetables]")
                    .hasMessageContaining("'_embedded'=[ships, vegetables]");
        }
    }

    @Test
    public void follows_GivenRelByArrayMatchingBothLinksAndEmbedded_FollowsLinkInEmbeddedBlock() throws Exception {
        JSONObject result = this.testSubject.jsonHal()
                .from("hal-traverson-builder-data.json")
                .follow("vegetables[1]")
                .get()
                .getResource();

        assertThat(result.toJSONString()).isEqualTo("{\"i-am\":\"a parsnip.\"}");
    }

    @Test
    public void follows_GivenRelByArrayThatIsNotAnArray_throwsException() throws Exception {
        try {
            this.testSubject.jsonHal()
                    .from("hal-traverson-builder-data.json")
                    .follow("makes[0]")
                    .get();
            fail("Test should throw an exception for an unknown rel");
        } catch (UnknownRelException e) {
            assertThat(e).hasMessageContaining("Rel 'makes' with an item at index '0' not found in {")
                    .hasMessageContaining("'_links'=[makes, self, vegetables]")
                    .hasMessageContaining("'_embedded'=[ships, vegetables]");
        }
    }

    @Test
    public void follow_GivenSimpleRelThatMatchesNamePropertyOfSomethingInEmbeddedBlock_followsLink() throws Exception {
        JSONObject result = this.testSubject.jsonHal()
                .from("hal-traverson-builder-data.json")
                .follow("tug-boat")
                .get()
                .getResource();

        assertThat(result.toJSONString()).isEqualTo("{\"tug-boat\":\"true\"}");
    }

    private JSONObject getJsonResource(String resource) {
        try {
            return JSON.parseObject(Resources.toString(getResource(resource), Charsets.UTF_8));
        } catch (IOException e) {
            System.out.println(format("Failed to locate/read test resource '%s'", resource));
            return null;
        }
    }
}

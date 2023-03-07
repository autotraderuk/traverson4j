package uk.co.autotrader.traverson;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import uk.co.autotrader.traverson.exception.UnknownRelException;
import uk.co.autotrader.traverson.http.Request;
import uk.co.autotrader.traverson.http.Response;
import uk.co.autotrader.traverson.http.TraversonClient;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraversonBuilderFollowsTest {
    @Mock
    private TraversonClient client;

    private TraversonBuilder testSubject;

    @BeforeEach
    void mockClientToResolveUrlsFromClasspath() {
        when(this.client.execute(any(Request.class), any(Class.class))).thenAnswer(
                (Answer<Response<JSONObject>>) invocation -> {
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
        );

    }

    @BeforeEach
    void instantiateNewBuilder() {
        this.testSubject = new TraversonBuilder(this.client);
    }

    @Test
    void follows_GivenSimpleRelation_FollowsLinkInLinksBlock() {
        JSONObject result = this.testSubject.jsonHal()
                .from("hal-traverson-builder-data.json")
                .follow("makes")
                .get()
                .getResource();

        assertThat(result.toJSONString()).isEqualTo("{\"makes\":\"a bunch of makes.\"}");
    }

    @Test
    void follows_GivenRelByPropertyMatchingBothLinksAndEmbedded_FollowsLinkInLinksBlock() {
        JSONObject result = this.testSubject.jsonHal()
                .from("hal-traverson-builder-data.json")
                .follow("vegetables[name:turnip]")
                .get()
                .getResource();

        assertThat(result.toJSONString()).isEqualTo("{\"i-am\":\"a turnip.\"}");
    }

    @Test
    void follows_GivenRelByProperty_FollowsLinkInEmbeddedBlock() {
        JSONObject result = this.testSubject.jsonHal()
                .from("hal-traverson-builder-data.json")
                .follow("ships[name:tug-boat]")
                .get()
                .getResource();

        assertThat(result.toJSONString()).isEqualTo("{\"tug-boat\":\"true\"}");
    }

    @Test
    void follows_GivenRelByPropertyThatIsNotAnArray_throwsException() {
        assertThatThrownBy(() -> {
            this.testSubject.jsonHal()
                    .from("hal-traverson-builder-data.json")
                    .follow("makes[invalid:property]")
                    .get();
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'makes' with an item with property 'invalid: property' not found in {")
                .hasMessageContaining("'_links'=[makes, self, vegetables]")
                .hasMessageContaining("'_embedded'=[ships, vegetables]");
    }

    @Test
    void follows_GivenRelByArrayMatchingBothLinksAndEmbedded_FollowsLinkInEmbeddedBlock() {
        JSONObject result = this.testSubject.jsonHal()
                .from("hal-traverson-builder-data.json")
                .follow("vegetables[1]")
                .get()
                .getResource();

        assertThat(result.toJSONString()).isEqualTo("{\"i-am\":\"a parsnip.\"}");
    }

    @Test
    void follows_GivenRelByArrayThatIsNotAnArray_throwsException() {
        assertThatThrownBy(() -> {
            this.testSubject.jsonHal()
                    .from("hal-traverson-builder-data.json")
                    .follow("makes[0]")
                    .get();
        }).isInstanceOf(UnknownRelException.class)
                .hasMessageContaining("Rel 'makes' with an item at index '0' not found in {")
                .hasMessageContaining("'_links'=[makes, self, vegetables]")
                .hasMessageContaining("'_embedded'=[ships, vegetables]");
    }

    @Test
    void follow_GivenSimpleRelThatMatchesNamePropertyOfSomethingInEmbeddedBlock_followsLink() {
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
            System.out.printf("Failed to locate/read test resource '%s'%n", resource);
            return null;
        }
    }
}

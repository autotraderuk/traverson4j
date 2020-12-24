package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson.JSONObject;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Rule;
import org.junit.Test;
import uk.co.autotrader.traverson.Traverson;

import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {
    @Rule
    @SuppressFBWarnings
    public WireMockRule wireMockRule = new WireMockRule(8089);

    private final Traverson traverson = new Traverson(new ApacheHttpTraversonClientAdapter());

    @Test
    public void requestBody_SimpleTextBodyIsSerializedAndPostedCorrectly() {
        stubFor(patch(urlEqualTo("/records/1"))
                .willReturn(WireMock.status(202)));
        Response<JSONObject> response = traverson.from("http://localhost:8089/records/1")
                .patch(new TextBody("{\"key\":123}", "application/json", StandardCharsets.UTF_8));

        verify(1, patchRequestedFor(urlEqualTo("/records/1")).withRequestBody(equalToJson("{\"key\":123}")));
        assertThat(response.getStatusCode()).isEqualTo(202);
    }

    @Test
    public void requestBody_MultipartBodyIsSerializedAndPostedCorrectly() {
        byte[] data = new byte[]{0x00, 0x01, 0x02};
        stubFor(post("/records")
                .withMultipartRequestBody(aMultipart()
                        .withName("my-body-part")
                        .withHeader("Content-Type", equalTo("application/octet-stream"))
                        .withBody(binaryEqualTo(data))
                )
                .willReturn(WireMock.status(202)));
        SimpleMultipartBody.BodyPart bodyPart = new SimpleMultipartBody.BodyPart("my-body-part", data, "application/octet-stream", "my-file");
        SimpleMultipartBody multipartBody = new SimpleMultipartBody(bodyPart);
        Response<JSONObject> response = traverson.from("http://localhost:8089/records")
                .post(multipartBody);

        verify(1, postRequestedFor(urlEqualTo("/records")));
        assertThat(response.getStatusCode()).isEqualTo(202);
    }

    @Test
    public void basicAuthentication_ReactsToUnauthorizedStatusAndAuthenticateHeader() {
        stubFor(get("/restricted-area")
                .inScenario("Restricted access").whenScenarioStateIs(STARTED)
                .willSetStateTo("First request made")
                .willReturn(unauthorized().withHeader("WWW-Authenticate", "Basic realm=\"User Visible Realm\"")));

        stubFor(get("/restricted-area")
                .inScenario("Restricted access").whenScenarioStateIs("First request made")
                .withBasicAuth("MyUsername", "MyPassword")
                .willReturn(ok()));

        Response<String> response = traverson.from("http://localhost:8089/restricted-area")
                                            .withAuth("MyUsername", "MyPassword", "http://localhost:8089")
                                            .get(String.class);

        assertThat(response.getStatusCode()).isEqualTo(200);
        verify(2, getRequestedFor(urlEqualTo("/restricted-area")));
    }

    @Test
    public void basicAuthentication_GivenPreemptiveAuthenticationSetToTrue_SendsUsernameAndPasswordWithoutNeedingAnUnauthorizedResponse() {
        stubFor(get("/restricted-area")
                .withBasicAuth("MyUsername", "MyPassword")
                .willReturn(ok()));

        Response<String> response = traverson.from("http://localhost:8089/restricted-area")
                                            .withAuth("MyUsername", "MyPassword", "http://localhost:8089", true)
                                            .get(String.class);

        assertThat(response.getStatusCode()).isEqualTo(200);
        verify(1, getRequestedFor(urlEqualTo("/restricted-area")));
    }
}

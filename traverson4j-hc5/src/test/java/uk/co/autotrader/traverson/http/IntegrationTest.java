package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson2.JSONObject;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.autotrader.traverson.Traverson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {
    private static WireMockServer wireMockServer;

    private final Traverson traverson = new Traverson(new ApacheHttpTraversonClientAdapter());

    @BeforeAll
    public static void beforeClass() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
    }

    @AfterAll
    public static void afterClass() {
        wireMockServer.stop();
        wireMockServer.resetAll();
    }

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
    }

    @Test
    void inputStream_allowsClientsToRequestTheBodyAsInputStreamWithoutClosingTheHttpConnection() throws IOException {
        String htmlBody = "<html></html>";
        wireMockServer.stubFor(get(urlEqualTo("/"))
                .willReturn(WireMock.status(200).withBody(htmlBody)));

        Response<String> response = traverson.from("http://localhost:8089")
                .withHeader("Accept", "content-type")
                .get(String.class);

        assertThat(response.getResource()).isEqualTo(htmlBody);
        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/"))
                .withHeader("Accept", equalTo("content-type")));
    }

    @Test
    void acceptHeaderCanBeSet() throws IOException {
        String htmlBody = "<html></html>";
        wireMockServer.stubFor(get(urlEqualTo("/")).willReturn(WireMock.status(200).withBody(htmlBody)));

        Response<InputStream> response = traverson.from("http://localhost:8089").get(InputStream.class);
        try (InputStream inputStream = response.getResource()) {
            assertThat(inputStream.readAllBytes()).isEqualTo(htmlBody.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Test
    void requestBody_SimpleTextBodyIsSerializedAndPostedCorrectly() {
        wireMockServer.stubFor(patch(urlEqualTo("/records/1"))
                .willReturn(WireMock.status(202)));
        Response<JSONObject> response = traverson.from("http://localhost:8089/records/1")
                .patch(new TextBody("{\"key\":123}", "application/json", StandardCharsets.UTF_8));

        wireMockServer.verify(1, patchRequestedFor(urlEqualTo("/records/1")).withRequestBody(equalToJson("{\"key\":123}")));
        assertThat(response.getStatusCode()).isEqualTo(202);
    }

    @Test
    void requestBody_MultipartBodyIsSerializedAndPostedCorrectly() {
        byte[] data = new byte[]{0x00, 0x01, 0x02};
        wireMockServer.stubFor(post("/records")
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

        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/records")));
        assertThat(response.getStatusCode()).isEqualTo(202);
    }

    @Test
    void basicAuthentication_ReactsToUnauthorizedStatusAndAuthenticateHeader() {
        wireMockServer.stubFor(get("/restricted-area")
                .inScenario("Restricted access").whenScenarioStateIs(STARTED)
                .willSetStateTo("First request made")
                .willReturn(unauthorized().withHeader("WWW-Authenticate", "Basic realm=\"User Visible Realm\"")));

        wireMockServer.stubFor(get("/restricted-area")
                .inScenario("Restricted access").whenScenarioStateIs("First request made")
                .withBasicAuth("MyUsername", "MyPassword")
                .willReturn(ok()));

        Response<String> response = traverson.from("http://localhost:8089/restricted-area")
                                            .withAuth("MyUsername", "MyPassword", "http://localhost:8089")
                                            .get(String.class);

        assertThat(response.getStatusCode()).isEqualTo(200);
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/restricted-area")));
    }

    @Test
    void basicAuthentication_GivenPreemptiveAuthenticationSetToTrue_SendsUsernameAndPasswordWithoutNeedingAnUnauthorizedResponse() {
        wireMockServer.stubFor(get("/restricted-area")
                .withBasicAuth("MyUsername", "MyPassword")
                .willReturn(ok()));

        Response<String> response = traverson.from("http://localhost:8089/restricted-area")
                                            .withAuth("MyUsername", "MyPassword", "http://localhost:8089", true)
                                            .get(String.class);

        assertThat(response.getStatusCode()).isEqualTo(200);
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/restricted-area")));
    }
}

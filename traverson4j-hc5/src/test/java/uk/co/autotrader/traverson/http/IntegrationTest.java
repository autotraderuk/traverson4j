package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson.JSONObject;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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

    @BeforeClass
    public static void beforeClass() throws Exception {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        wireMockServer.stop();
        wireMockServer.resetAll();
    }

    @Before
    public void setUp() throws Exception {
        wireMockServer.resetAll();
    }

    @Test
    public void inputStream_allowsClientsToRequestTheBodyAsInputStreamWithoutClosingTheHttpConnection() throws IOException {
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
    public void acceptHeaderCanBeSet() throws IOException {
        String htmlBody = "<html></html>";
        wireMockServer.stubFor(get(urlEqualTo("/")).willReturn(WireMock.status(200).withBody(htmlBody)));

        Response<InputStream> response = traverson.from("http://localhost:8089").get(InputStream.class);
        try (InputStream inputStream = response.getResource()) {
            assertThat(inputStream.readAllBytes()).isEqualTo(htmlBody.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Test
    public void requestBody_SimpleTextBodyIsSerializedAndPostedCorrectly() {
        wireMockServer.stubFor(patch(urlEqualTo("/records/1"))
                .willReturn(WireMock.status(202)));
        Response<JSONObject> response = traverson.from("http://localhost:8089/records/1")
                .patch(new TextBody("{\"key\":123}", "application/json", StandardCharsets.UTF_8));

        wireMockServer.verify(1, patchRequestedFor(urlEqualTo("/records/1")).withRequestBody(equalToJson("{\"key\":123}")));
        assertThat(response.getStatusCode()).isEqualTo(202);
    }

    @Test
    public void requestBody_MultipartBodyIsSerializedAndPostedCorrectly() {
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
    public void basicAuthentication_ReactsToUnauthorizedStatusAndAuthenticateHeader() {
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
    public void basicAuthentication_GivenPreemptiveAuthenticationSetToTrue_SendsUsernameAndPasswordWithoutNeedingAnUnauthorizedResponse() {
        wireMockServer.stubFor(get("/restricted-area")
                .withBasicAuth("MyUsername", "MyPassword")
                .willReturn(ok()));

        Response<String> response = traverson.from("http://localhost:8089/restricted-area")
                                            .withAuth("MyUsername", "MyPassword", "http://localhost:8089", true)
                                            .get(String.class);

        assertThat(response.getStatusCode()).isEqualTo(200);
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/restricted-area")));
    }

    @Test
    public void requestBody_nonSuccessStatus_providesError() {
        wireMockServer.stubFor(get(urlEqualTo("/path"))
                .willReturn(WireMock.badRequest().withBody("error message")));
        Response<Integer> response = traverson.from("http://localhost:8089/path")
                .get(Integer.class);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/path")));
        assertThat(response.getResource()).isNull();
        assertThat(response.getError()).isEqualTo("error message");
    }
}

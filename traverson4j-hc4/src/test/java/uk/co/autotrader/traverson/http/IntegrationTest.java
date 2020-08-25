package uk.co.autotrader.traverson.http;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Rule;
import org.junit.Test;
import uk.co.autotrader.traverson.Traverson;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {
    @Rule
    @SuppressFBWarnings
    public WireMockRule wireMockRule = new WireMockRule(8089);

    private Traverson traverson = new Traverson(new ApacheHttpTraversonClientAdapter());


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

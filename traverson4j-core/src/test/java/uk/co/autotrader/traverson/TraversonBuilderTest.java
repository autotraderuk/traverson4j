package uk.co.autotrader.traverson;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.autotrader.traverson.http.AuthCredential;
import uk.co.autotrader.traverson.exception.IncompleteTraversalException;
import uk.co.autotrader.traverson.http.*;
import uk.co.autotrader.traverson.link.BasicLinkDiscoverer;
import uk.co.autotrader.traverson.link.hal.HalLinkDiscoverer;
import uk.co.autotrader.traverson.link.LinkDiscoverer;

import java.net.URI;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TraversonBuilderTest {
    private TraversonBuilder builder;
    @Mock
    private TraversonClient client;
    @Mock
    private Body body;
    @Mock
    private Response response;
    @Mock
    private JSONObject resource;
    @Mock
    private Deque<String> relsToFollow;
    @Captor
    private ArgumentCaptor<Deque<String>> relsCaptor;
    @Mock
    private LinkDiscoverer linkDiscoverer;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Mock
    public Response<JSONObject> firstResponse;
    @Mock
    public Response<JSONObject> secondResponse;
    @Mock
    public Response<String> stringResponse;

    @Before
    public void setUp() throws Exception {
        builder = new TraversonBuilder(client);
        FieldUtils.writeDeclaredField(builder, "linkDiscoverer", linkDiscoverer, true);
    }

    private Request reflectionGetRequest() throws IllegalAccessException {
        return (Request) FieldUtils.readDeclaredField(builder, "request", true);
    }

    @Test
    public void from_SetsUrl() throws Exception {
        assertThat(builder.from("http://localhost:8080")).isEqualTo(builder);
        assertThat(reflectionGetRequest().getUrl()).isEqualTo("http://localhost:8080");
    }

    @Test
    public void json_SetsAcceptTypeAndLinkDiscoverer() throws Exception {
        assertThat(builder.json()).isEqualTo(builder);
        assertThat(reflectionGetRequest().getAcceptMimeType()).isEqualTo("application/json");
        assertThat(FieldUtils.readDeclaredField(builder, "linkDiscoverer", true)).isInstanceOf(BasicLinkDiscoverer.class);
    }

    @Test
    public void jsoHal_SetsAcceptTypeAndLinkDiscoverer() throws Exception {
        assertThat(builder.jsonHal()).isEqualTo(builder);
        assertThat(reflectionGetRequest().getAcceptMimeType()).isEqualTo("application/hal+json");
        assertThat(FieldUtils.readDeclaredField(builder, "linkDiscoverer", true)).isInstanceOf(HalLinkDiscoverer.class);
    }

    @Test
    public void follow_ReplacesRels() throws Exception {
        FieldUtils.writeField(builder, "relsToFollow", relsToFollow, true);

        builder.follow("A", "B", "C");

        verify(relsToFollow).clear();
        verify(relsToFollow).addAll(relsCaptor.capture());
        assertThat(relsCaptor.getValue()).containsExactly("A", "B", "C");
    }

    @Test
    public void withQueryParam_IncrementallyAddsQueryParams() throws Exception {
        assertThat(builder.withQueryParam("key1", "value1")).isEqualTo(builder);
        assertThat(builder.withQueryParam("key2", "value2")).isEqualTo(builder);

        Map<String, List<String>> queryParams = reflectionGetRequest().getQueryParameters();
        assertThat(queryParams).hasSize(2);
        assertThat(queryParams).containsOnly(entry("key1", newArrayList("value1")), entry("key2", newArrayList("value2")));
    }

    @Test
    public void withQueryParam_SetsQueryParams() throws Exception {
        assertThat(builder.withQueryParam("key1", "value1")).isEqualTo(builder);

        List<String> value = newArrayList("value1");
        Map<String, List<String>> queryParams = ImmutableMap.of("key1", value);
        assertThat(reflectionGetRequest().getQueryParameters()).isEqualTo(queryParams);
    }

    @Test
    public void withQueryParam_AddsSingleQueryParamWithMultipleValues() throws Exception {
        assertThat(builder.withQueryParam("key", "value1", "value2", "value3")).isEqualTo(builder);
        Map<String, List<String>> queryParams = reflectionGetRequest().getQueryParameters();
        assertThat(queryParams).hasSize(1);
        assertThat(queryParams.get("key").get(0)).isEqualTo("value1");
        assertThat(queryParams.get("key").get(1)).isEqualTo("value2");
        assertThat(queryParams.get("key").get(2)).isEqualTo("value3");
    }

    @Test
    public void withQueryParam_AddsQueryParamsForExistingKeysAndDoesNotOverwrite() throws Exception {
        builder.withQueryParam("key", "value1");
        Map<String, List<String>> queryParams = reflectionGetRequest().getQueryParameters();

        assertThat(queryParams).hasSize(1);
        assertThat(queryParams.get("key")).containsExactly("value1");

        builder.withQueryParam("key", "value2");
        Map<String, List<String>> additionalQueryParams = reflectionGetRequest().getQueryParameters();
        assertThat(additionalQueryParams).hasSize(1);
        assertThat(additionalQueryParams.get("key")).containsOnly("value1", "value2");
    }

    @Test
    public void withTemplateParam_IncrementallyAddsTemplateParams() throws Exception {
        assertThat(builder.withTemplateParam("key1", "value1")).isEqualTo(builder);
        assertThat(builder.withTemplateParam("key2", "value2")).isEqualTo(builder);
        Map<String, List<String>> templateParams = reflectionGetRequest().getTemplateParams();
        assertThat(templateParams).hasSize(2);
        assertThat(templateParams.get("key1").get(0)).isEqualTo("value1");
        assertThat(templateParams.get("key2").get(0)).isEqualTo("value2");
    }

    @Test
    public void withTemplateParam_AddsSingleTemplateParamWithMultipleValues() throws Exception {
        assertThat(builder.withTemplateParam("key", "value1", "value2", "value3")).isEqualTo(builder);
        Map<String, List<String>> templateParams = reflectionGetRequest().getTemplateParams();
        assertThat(templateParams).hasSize(1);
        assertThat(templateParams.get("key").get(0)).isEqualTo("value1");
        assertThat(templateParams.get("key").get(1)).isEqualTo("value2");
        assertThat(templateParams.get("key").get(2)).isEqualTo("value3");
    }

    @Test
    public void withTemplateParam_SetsQueryParams() throws Exception {
        assertThat(builder.withTemplateParam("key1", "value1")).isEqualTo(builder);

        Map<String, List<String>> templateParams = Collections.singletonMap("key1", Arrays.asList("value1"));
        assertThat(reflectionGetRequest().getTemplateParams()).isEqualTo(templateParams);
    }

    @Test
    public void withTemplateParam_AddsTemplateParamsForExistingKeysAndDoesNotOverwrite() throws Exception {
        builder.withTemplateParam("key", "value1");
        Map<String, List<String>> templateParams = reflectionGetRequest().getTemplateParams();

        assertThat(templateParams).hasSize(1);
        assertThat(templateParams.get("key")).containsExactly("value1");

        builder.withTemplateParam("key", "value2");
        Map<String, List<String>> additionalTemplateParams = reflectionGetRequest().getTemplateParams();
        assertThat(additionalTemplateParams).hasSize(1);
        assertThat(additionalTemplateParams.get("key")).containsOnly("value1", "value2");
    }

    @Test
    public void addHeader_IncrementallyAddsHeaders() throws Exception {
        assertThat(builder.withHeader("key1", "value1")).isEqualTo(builder);
        assertThat(builder.withHeader("key2", "value2")).isEqualTo(builder);
        Map<String, String> headers = reflectionGetRequest().getHeaders();
        assertThat(headers).hasSize(2).containsEntry("key1", "value1").containsEntry("key2", "value2");
    }

    @Test
    public void withHeaders_SetsHeaders() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("key1", "value1");
        assertThat(builder.withHeader("key1", "value1")).isEqualTo(builder);
        assertThat(reflectionGetRequest().getHeaders()).isEqualTo(headers);
    }

    @Test
    public void withAuth_SetsCredentials() throws Exception {
        assertThat(builder.withAuth("user", "password")).isEqualTo(builder);
        List<AuthCredential> authCredentials = reflectionGetRequest().getAuthCredentials();

        assertThat(authCredentials).hasSize(1);
        AuthCredential credential = authCredentials.get(0);
        assertThat(credential.getUsername()).isEqualTo("user");
        assertThat(credential.getPassword()).isEqualTo("password");
        assertThat(credential.getHostname()).isNull();
    }

    @Test
    public void withAuth_SetsCredentialsIncludingHostname() throws Exception {
        assertThat(builder.withAuth("user", "password", "autotrader.co.uk")).isEqualTo(builder);
        List<AuthCredential> authCredentials = reflectionGetRequest().getAuthCredentials();

        assertThat(authCredentials).hasSize(1);
        AuthCredential credential = authCredentials.get(0);
        assertThat(credential.getUsername()).isEqualTo("user");
        assertThat(credential.getPassword()).isEqualTo("password");
        assertThat(credential.getHostname()).isEqualTo("autotrader.co.uk");
    }

    @Test
    public void withAuth_AppendsCredentials() throws Exception {
        assertThat(builder.withAuth("user", "password", "autotrader.co.uk").withAuth("user2", "password2")).isEqualTo(builder);
        List<AuthCredential> authCredentials = reflectionGetRequest().getAuthCredentials();

        assertThat(authCredentials).hasSize(2);
        AuthCredential credential = authCredentials.get(0);
        assertThat(credential.getUsername()).isEqualTo("user");
        assertThat(credential.getPassword()).isEqualTo("password");
        assertThat(credential.getHostname()).isEqualTo("autotrader.co.uk");
        credential = authCredentials.get(1);
        assertThat(credential.getUsername()).isEqualTo("user2");
        assertThat(credential.getHostname()).isNull();
    }

    @Test
    public void get_GivenInputs_BuildsRequestAndExecutes() throws Exception {
        when(client.execute(any(Request.class), eq(JSONObject.class))).thenReturn(firstResponse);

        Response response = builder.get();

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(firstResponse);
        assertThat(request.getBody()).isNull();
        assertThat(request.getMethod()).isEqualTo(Method.GET);
    }

    @Test
    public void post_GivenInputs_BuildsRequestAndExecutes() throws Exception {
        when(client.execute(any(Request.class), eq(JSONObject.class))).thenReturn(firstResponse);

        Response response = builder.post(body);

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(firstResponse);
        assertThat(request.getBody()).isEqualTo(body);
        assertThat(request.getMethod()).isEqualTo(Method.POST);
    }

    @Test
    public void post_GivenInputsAndReturnType_BuildsRequestAndExecutes() throws Exception {
        when(client.execute(any(Request.class), eq(String.class))).thenReturn(stringResponse);

        Response<String> response = builder.post(body, String.class);

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(stringResponse);
        assertThat(request.getBody()).isEqualTo(body);
        assertThat(request.getMethod()).isEqualTo(Method.POST);
    }

    @Test
    public void put_GivenInputs_BuildsRequestAndExecutes() throws Exception {
        when(client.execute(any(Request.class), eq(JSONObject.class))).thenReturn(firstResponse);

        Response response = builder.put(body);

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(firstResponse);
        assertThat(request.getBody()).isEqualTo(body);
        assertThat(request.getMethod()).isEqualTo(Method.PUT);
    }

    @Test
    public void put_GivenInputsAndReturnType_BuildsRequestAndExecutes() throws Exception {
        when(client.execute(any(Request.class), eq(String.class))).thenReturn(stringResponse);

        Response response = builder.put(body, String.class);

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(stringResponse);
        assertThat(request.getBody()).isEqualTo(body);
        assertThat(request.getMethod()).isEqualTo(Method.PUT);
    }

    @Test
    public void patch_GivenInputs_BuildsRequestAndExecutes() throws Exception {
        when(client.execute(any(Request.class), eq(JSONObject.class))).thenReturn(firstResponse);

        Response response = builder.patch(body);

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(firstResponse);
        assertThat(request.getBody()).isEqualTo(body);
        assertThat(request.getMethod()).isEqualTo(Method.PATCH);
    }


    @Test
    public void patch_GivenInputsAndReturnType_BuildsRequestAndExecutes() throws Exception {
        when(client.execute(any(Request.class), eq(String.class))).thenReturn(stringResponse);

        Response response = builder.patch(body, String.class);

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(stringResponse);
        assertThat(request.getBody()).isEqualTo(body);
        assertThat(request.getMethod()).isEqualTo(Method.PATCH);
    }

    @Test
    public void delete_GivenInputs_BuildsRequestAndExecutes() throws Exception {
        when(client.execute(any(Request.class), eq(JSONObject.class))).thenReturn(firstResponse);

        Response response = builder.delete();

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(firstResponse);
        assertThat(request.getBody()).isNull();
        assertThat(request.getMethod()).isEqualTo(Method.DELETE);
    }

    @Test
    public void delete_GivenInputsAndReturnType_BuildsRequestAndExecutes() throws Exception {
        when(client.execute(any(Request.class), eq(String.class))).thenReturn(stringResponse);

        Response response = builder.delete(String.class);

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(stringResponse);
        assertThat(request.getBody()).isNull();
        assertThat(request.getMethod()).isEqualTo(Method.DELETE);
    }

    @Test
    public void get_GivenRelToFollow_NavigatesToLastRelThenPerformsMethod() throws Exception {
        when(firstResponse.isSuccessful()).thenReturn(true);
        when(firstResponse.getResource()).thenReturn(resource);
        when(client.execute(reflectionGetRequest(), JSONObject.class)).thenReturn(firstResponse).thenReturn(secondResponse);
        when(linkDiscoverer.findHref(resource, "rel")).thenReturn("http://localhost/next");

        Response response = builder.from("http://localhost/").follow("rel").get();

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(secondResponse);
        assertThat(request.getUrl()).isEqualTo("http://localhost/next");
        assertThat(request.getMethod()).isEqualTo(Method.GET);
    }

    @Test
    public void getResource_GivenRelToFollow_NavigatesToLastRelThenPerformsMethod() throws Exception {
        JSONObject secondResource = mock(JSONObject.class);
        when(firstResponse.isSuccessful()).thenReturn(true);
        when(firstResponse.getResource()).thenReturn(resource);
        when(secondResponse.getResource()).thenReturn(secondResource);
        when(client.execute(reflectionGetRequest(), JSONObject.class)).thenReturn(firstResponse).thenReturn(secondResponse);
        when(linkDiscoverer.findHref(resource, "rel")).thenReturn("http://localhost/next");

        JSONObject resource = builder.from("http://localhost/").follow("rel").getResource();

        Request request = reflectionGetRequest();
        assertThat(resource).isEqualTo(secondResource);
        assertThat(request.getUrl()).isEqualTo("http://localhost/next");
        assertThat(request.getMethod()).isEqualTo(Method.GET);
    }

    @Test
    public void post_GivenRelToFollow_NavigatesToLastRelThenPerformsMethod() throws Exception {
        when(firstResponse.isSuccessful()).thenReturn(true);
        when(firstResponse.getResource()).thenReturn(resource);
        when(client.execute(reflectionGetRequest(), JSONObject.class)).thenReturn(firstResponse).thenReturn(secondResponse);
        when(linkDiscoverer.findHref(resource, "rel")).thenReturn("http://localhost/next");

        Response response = builder.from("http://localhost/").follow("rel").post(body);

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(secondResponse);
        assertThat(request.getUrl()).isEqualTo("http://localhost/next");
        assertThat(request.getMethod()).isEqualTo(Method.POST);
    }

    @Test
    public void put_GivenRelToFollow_NavigatesToLastRelThenPerformsMethod() throws Exception {
        when(firstResponse.isSuccessful()).thenReturn(true);
        when(firstResponse.getResource()).thenReturn(resource);
        when(client.execute(reflectionGetRequest(), JSONObject.class)).thenReturn(firstResponse).thenReturn(secondResponse);
        when(linkDiscoverer.findHref(resource, "rel")).thenReturn("http://localhost/next");

        Response response = builder.from("http://localhost/").follow("rel").put(body);

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(secondResponse);
        assertThat(request.getUrl()).isEqualTo("http://localhost/next");
        assertThat(request.getMethod()).isEqualTo(Method.PUT);
    }

    @Test
    public void patch_GivenRelToFollow_NavigatesToLastRelThenPerformsMethod() throws Exception {
        when(firstResponse.isSuccessful()).thenReturn(true);
        when(firstResponse.getResource()).thenReturn(resource);
        when(client.execute(reflectionGetRequest(), JSONObject.class)).thenReturn(firstResponse).thenReturn(secondResponse);
        when(linkDiscoverer.findHref(resource, "rel")).thenReturn("http://localhost/next");

        Response response = builder.from("http://localhost/").follow("rel").patch(body);

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(secondResponse);
        assertThat(request.getUrl()).isEqualTo("http://localhost/next");
        assertThat(request.getMethod()).isEqualTo(Method.PATCH);
    }

    @Test
    public void delete_GivenRelToFollow_NavigatesToLastRelThenPerformsMethod() throws Exception {
        when(firstResponse.isSuccessful()).thenReturn(true);
        when(firstResponse.getResource()).thenReturn(resource);
        when(client.execute(reflectionGetRequest(), JSONObject.class)).thenReturn(firstResponse).thenReturn(secondResponse);
        when(linkDiscoverer.findHref(resource, "rel")).thenReturn("http://localhost/next");

        Response response = builder.from("http://localhost/").follow("rel").delete();

        Request request = reflectionGetRequest();
        assertThat(response).isEqualTo(secondResponse);
        assertThat(request.getUrl()).isEqualTo("http://localhost/next");
        assertThat(request.getMethod()).isEqualTo(Method.DELETE);
    }

    @Test
    public void get_GivenRelToFollow_ThrowsExceptionWhenANonSuccessfulResponseIsReturnedMidwayThroughTraversing() throws Exception {
        when(firstResponse.getUri()).thenReturn(URI.create("http://brokenurl.com/not_found"));
        when(firstResponse.isSuccessful()).thenReturn(false);
        when(firstResponse.getStatusCode()).thenReturn(404);
        when(firstResponse.getResource()).thenReturn(resource);
        when(client.execute(reflectionGetRequest(), JSONObject.class)).thenReturn(firstResponse);
        exception.expect(IncompleteTraversalException.class);
        exception.expectMessage("Received status code 404 from url http://brokenurl.com/not_found");

        Response response = builder.from("http://localhost/").follow("rel").get();

        Request request = reflectionGetRequest();
        verify(client, times(1)).execute(request, JSONObject.class);
        assertThat(response).isEqualTo(firstResponse);
        assertThat(request.getUrl()).isEqualTo("http://localhost/");
        assertThat(request.getMethod()).isEqualTo(Method.GET);
        verifyZeroInteractions(linkDiscoverer);
    }
}

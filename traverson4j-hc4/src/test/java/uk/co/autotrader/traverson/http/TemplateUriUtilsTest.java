package uk.co.autotrader.traverson.http;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TemplateUriUtilsTest {

    private TemplateUriUtils templateUriUtils;

    @Before
    public void setUp() throws Exception {
        templateUriUtils = new TemplateUriUtils();
    }

    @Test
    public void expandTemplateUri_GivenTemplateUriAndTemplateParams_ExpandsTemplate() throws Exception {
        String input = "http://example.autotrader.co.uk/dealers{?dealerId*,otherParam}";
        Map<String, List<String>> templateParams = new HashMap<String, List<String>>();
        templateParams.put("dealerId", Arrays.asList("123"));
        templateParams.put("otherParam", Arrays.asList("456"));

        String uri = templateUriUtils.expandTemplateUri(input, templateParams);

        assertThat(uri).isEqualTo("http://example.autotrader.co.uk/dealers?dealerId=123&otherParam=456");
    }

    @Test
    public void expandTemplateUri_GivenTemplateUriAndUnusedTemplateParams_ExpandsTemplate() throws Exception {
        String input = "http://example.autotrader.co.uk/dealers{?dealerId,otherParam}";
        Map<String, List<String>> templateParams = new HashMap<String, List<String>>();
        templateParams.put("notFound", Arrays.asList("dummy"));

        String uri = templateUriUtils.expandTemplateUri(input, templateParams);

        assertThat(uri).isEqualTo("http://example.autotrader.co.uk/dealers");
    }

    @Test
    public void expandTemplateUri_GivenTemplateUriAndNoTemplateParams_ExpandsTemplate() throws Exception {
        String input = "http://example.autotrader.co.uk/dealers{?dealerId,otherParam}";
        Map<String, List<String>> templateParams = new HashMap<String, List<String>>();

        String uri = templateUriUtils.expandTemplateUri(input, templateParams);

        assertThat(uri).isEqualTo("http://example.autotrader.co.uk/dealers");
    }


    @Test
    public void expandTemplateUri_GivenTemplateUriAndMultipleOfTheSameTemplateParam_ExpandsTemplate() throws Exception {
        String input = "http://example.autotrader.co.uk/dealers{?dealerId*,otherParam}";
        Map<String, List<String>> templateParams = new HashMap<String, List<String>>();
        templateParams.put("dealerId", Arrays.asList("1234", "4567", "78910"));

        String uri = templateUriUtils.expandTemplateUri(input, templateParams);

        assertThat(uri).isEqualTo("http://example.autotrader.co.uk/dealers?dealerId=1234&dealerId=4567&dealerId=78910");
    }

}
package uk.co.autotrader.traverson.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TemplateUriUtilsTest {

    private TemplateUriUtils templateUriUtils;

    @BeforeEach
    void setUp() {
        templateUriUtils = new TemplateUriUtils();
    }

    @Test
    void expandTemplateUri_GivenTemplateUriAndTemplateParams_ExpandsTemplate() {
        String input = "http://example.autotrader.co.uk/dealers{?dealerId*,otherParam}";
        Map<String, List<String>> templateParams = new HashMap<String, List<String>>();
        templateParams.put("dealerId", Arrays.asList("123"));
        templateParams.put("otherParam", Arrays.asList("456"));

        String uri = templateUriUtils.expandTemplateUri(input, templateParams);

        assertThat(uri).isEqualTo("http://example.autotrader.co.uk/dealers?dealerId=123&otherParam=456");
    }

    @Test
    void expandTemplateUri_GivenTemplateUriAndUnusedTemplateParams_ExpandsTemplate() {
        String input = "http://example.autotrader.co.uk/dealers{?dealerId,otherParam}";
        Map<String, List<String>> templateParams = new HashMap<String, List<String>>();
        templateParams.put("notFound", Arrays.asList("dummy"));

        String uri = templateUriUtils.expandTemplateUri(input, templateParams);

        assertThat(uri).isEqualTo("http://example.autotrader.co.uk/dealers");
    }

    @Test
    void expandTemplateUri_GivenTemplateUriAndNoTemplateParams_ExpandsTemplate() {
        String input = "http://example.autotrader.co.uk/dealers{?dealerId,otherParam}";
        Map<String, List<String>> templateParams = new HashMap<String, List<String>>();

        String uri = templateUriUtils.expandTemplateUri(input, templateParams);

        assertThat(uri).isEqualTo("http://example.autotrader.co.uk/dealers");
    }


    @Test
    void expandTemplateUri_GivenTemplateUriAndMultipleOfTheSameTemplateParam_ExpandsTemplate() {
        String input = "http://example.autotrader.co.uk/dealers{?dealerId*,otherParam}";
        Map<String, List<String>> templateParams = new HashMap<String, List<String>>();
        templateParams.put("dealerId", Arrays.asList("1234", "4567", "78910"));

        String uri = templateUriUtils.expandTemplateUri(input, templateParams);

        assertThat(uri).isEqualTo("http://example.autotrader.co.uk/dealers?dealerId=1234&dealerId=4567&dealerId=78910");
    }

    @Test
    void expandTemplateUri_GivenTemplateUriAndEmptyValueForTemplateParam_ExpandsTemplate() {
        String input = "http://example.autotrader.co.uk/dealers{?dealerId,param}";
        Map<String, List<String>> templateParams = new HashMap<>();
        templateParams.put("dealerId", Arrays.asList("1234"));
        templateParams.put("param", new ArrayList<>());

        String uri = templateUriUtils.expandTemplateUri(input, templateParams);

        assertThat(uri).isEqualTo("http://example.autotrader.co.uk/dealers?dealerId=1234");
    }
}

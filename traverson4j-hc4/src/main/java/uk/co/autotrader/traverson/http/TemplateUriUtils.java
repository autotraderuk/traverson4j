package uk.co.autotrader.traverson.http;

import com.damnhandy.uri.template.UriTemplate;

import java.util.List;
import java.util.Map;

public class TemplateUriUtils {

    String expandTemplateUri(String templateUri, Map<String, List<String>> templateParams) {
        UriTemplate uriTemplate = UriTemplate.fromTemplate(templateUri);
        for (Map.Entry<String, List<String>> templateParam : templateParams.entrySet()) {
            uriTemplate.set(templateParam.getKey(), templateParam.getValue());
        }
        return uriTemplate.expand();
    }

}

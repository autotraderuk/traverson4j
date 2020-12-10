package uk.co.autotrader.traverson.http.entity;

import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import uk.co.autotrader.traverson.http.Body;
import uk.co.autotrader.traverson.http.FormDataBody;

import java.util.Arrays;
import java.util.stream.Collectors;

class FormDataEntityConverter implements HttpEntityConverter {

    @Override
    public HttpEntity toEntity(Body body) {
        FormDataBody formDataBody = (FormDataBody) body;

        return new UrlEncodedFormEntity(
                Arrays.stream(formDataBody.getContent()).map(pair -> new BasicNameValuePair(pair.getName(), pair.getValue())).collect(Collectors.toList()),
                formDataBody.getCharset());
    }
}

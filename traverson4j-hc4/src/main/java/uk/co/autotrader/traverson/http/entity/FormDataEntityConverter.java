package uk.co.autotrader.traverson.http.entity;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import uk.co.autotrader.traverson.http.Body;
import uk.co.autotrader.traverson.http.FormDataBody;
import uk.co.autotrader.traverson.http.TextBody;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;

class FormDataEntityConverter implements HttpEntityConverter {
    @Override
    public HttpEntity toEntity(Body body) {
        FormDataBody formDataBodyBody = (FormDataBody) body;

        // TODO how is the charset handled / assumed ?
        return new UrlEncodedFormEntity(
                Arrays.stream(formDataBodyBody.getContent()).map(pair -> new BasicNameValuePair(pair.getName(), pair.getValue())).collect(Collectors.toList()),
                Charset.defaultCharset());
    }
}

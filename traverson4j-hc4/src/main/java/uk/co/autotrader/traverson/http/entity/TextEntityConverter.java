package uk.co.autotrader.traverson.http.entity;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import uk.co.autotrader.traverson.http.Body;
import uk.co.autotrader.traverson.http.TextBody;

public class TextEntityConverter implements HttpEntityConverter {
    @Override
    public HttpEntity toEntity(Body body) {
        TextBody textBody = (TextBody) body;

        return new StringEntity(textBody.getContent(), ContentType.create(body.getContentType(), textBody.getCharset()));
    }
}

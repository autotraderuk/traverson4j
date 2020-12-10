package uk.co.autotrader.traverson.http.entity;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import uk.co.autotrader.traverson.http.Body;
import uk.co.autotrader.traverson.http.TextBody;

class TextEntityConverter implements HttpEntityConverter {
    @Override
    public HttpEntity toEntity(Body body) {
        TextBody textBody = (TextBody) body;

        return new StringEntity(textBody.getContent(), ContentType.create(body.getContentType(), textBody.getCharset()));
    }
}

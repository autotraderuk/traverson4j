package uk.co.autotrader.traverson.http.entity;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import uk.co.autotrader.traverson.http.Body;
import uk.co.autotrader.traverson.http.SimpleMultipartBody;

public class MultipartEntityConverter implements HttpEntityConverter {

    @Override
    public HttpEntity toEntity(Body body) {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        for (SimpleMultipartBody.BodyPart bodyPart : ((SimpleMultipartBody) body).getContent()) {
            multipartEntityBuilder.addBinaryBody(bodyPart.getName(), bodyPart.getData(), ContentType.create(bodyPart.getContentType()), bodyPart.getFilename());
        }
        return multipartEntityBuilder.build();
    }
}

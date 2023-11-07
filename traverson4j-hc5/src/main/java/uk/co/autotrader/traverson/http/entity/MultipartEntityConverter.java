package uk.co.autotrader.traverson.http.entity;

import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import uk.co.autotrader.traverson.http.Body;
import uk.co.autotrader.traverson.http.SimpleMultipartBody;

class MultipartEntityConverter implements HttpEntityConverter {

    @Override
    public HttpEntity toEntity(Body body) {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        for (SimpleMultipartBody.BodyPart bodyPart : ((SimpleMultipartBody) body).getContent()) {

            if (bodyPart.getData() != null) {
                multipartEntityBuilder.addBinaryBody(bodyPart.getName(), bodyPart.getData(), ContentType.create(bodyPart.getContentType()), bodyPart.getFilename());
            } else {
                multipartEntityBuilder.addBinaryBody(bodyPart.getName(), bodyPart.getInputStream(), ContentType.create(bodyPart.getContentType()), bodyPart.getFilename());
            }
        }
        return multipartEntityBuilder.build();
    }
}

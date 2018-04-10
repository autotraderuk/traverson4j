package uk.co.autotrader.traverson.http.entity;

import org.apache.http.HttpEntity;
import uk.co.autotrader.traverson.http.Body;

public interface HttpEntityConverter {
    HttpEntity toEntity(Body body);
}

package uk.co.autotrader.traverson.http.entity;

import org.apache.hc.core5.http.HttpEntity;
import uk.co.autotrader.traverson.http.Body;
import uk.co.autotrader.traverson.http.FormDataBody;
import uk.co.autotrader.traverson.http.SimpleMultipartBody;
import uk.co.autotrader.traverson.http.TextBody;

import java.util.HashMap;
import java.util.Map;

public class BodyFactory {
    private static Map<Class<? extends Body>, HttpEntityConverter> converters = new HashMap<Class<? extends Body>, HttpEntityConverter>();

    //TODO: Look at a nicer way to register converters, maybe Reflections? Or ServiceLoaders
    static {
        register(SimpleMultipartBody.class, new MultipartEntityConverter());
        register(TextBody.class, new TextEntityConverter());
        register(FormDataBody.class, new FormDataEntityConverter());
    }

    public static void register(Class<? extends Body> type, HttpEntityConverter httpEntityConverter) {
        converters.put(type, httpEntityConverter);
    }

    public HttpEntity toEntity(Body body) {
        HttpEntityConverter converter = converters.get(body.getClass());
        if (converter == null) {
            throw new UnsupportedOperationException("Not supported Request body, the supported types are " + converters.keySet());
        }
        return converter.toEntity(body);
    }
}

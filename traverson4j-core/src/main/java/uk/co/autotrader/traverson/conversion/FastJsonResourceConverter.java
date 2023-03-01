package uk.co.autotrader.traverson.conversion;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.InputStream;

class FastJsonResourceConverter implements ResourceConverter<JSONObject> {

    @Override
    public Class<JSONObject> getDestinationType() {
        return JSONObject.class;
    }

    @Override
    public JSONObject convert(InputStream resource, Class<? extends JSONObject> returnType) {
        String resourceAsString = null;
        try {
            resourceAsString = new StringResourceConverter().convert(resource, String.class);

            if (resourceAsString.isEmpty()) {
                return null;
            }

            return JSON.parseObject(resourceAsString);
        } catch (JSONException ex) {
            throw new ConversionException("Failed to parse to JSONObject", resourceAsString, ex);
        }
    }
}

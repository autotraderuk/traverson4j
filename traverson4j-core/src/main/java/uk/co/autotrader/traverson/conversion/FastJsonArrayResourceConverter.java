package uk.co.autotrader.traverson.conversion;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.InputStream;

public class FastJsonArrayResourceConverter implements ResourceConverter<JSONArray> {

    @Override
    public Class<JSONArray> getDestinationType() {
        return JSONArray.class;
    }

    @Override
    public JSONArray convert(InputStream resource, Class<? extends JSONArray> returnType) {
        String resourceAsString = null;
        try {
            resourceAsString = new StringResourceConverter().convert(resource, String.class);
            return JSONArray.parseArray(resourceAsString);
        } catch (JSONException ex) {
            throw new ConversionException("Failed to parse to JSONObject", resourceAsString, ex);
        }
    }
}


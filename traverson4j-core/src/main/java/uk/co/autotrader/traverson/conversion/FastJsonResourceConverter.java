package uk.co.autotrader.traverson.conversion;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import uk.co.autotrader.traverson.exception.ConversionException;

class FastJsonResourceConverter implements ResourceConverter<JSONObject> {

    @Override
    public Class<JSONObject> getDestinationType() {
        return JSONObject.class;
    }

    @Override
    public JSONObject convert(String resourceAsString, Class<? extends JSONObject> returnType) {
        try {
            return JSONObject.parseObject(resourceAsString, Feature.OrderedField);
        } catch (JSONException ex) {
            throw new ConversionException("Failed to parse to JSONObject", resourceAsString, ex);
        }
    }
}

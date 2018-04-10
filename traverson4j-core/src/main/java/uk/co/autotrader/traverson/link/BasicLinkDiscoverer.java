package uk.co.autotrader.traverson.link;

import com.alibaba.fastjson.JSONObject;
import uk.co.autotrader.traverson.exception.UnknownRelException;

public class BasicLinkDiscoverer implements LinkDiscoverer {
    @Override
    public String findHref(JSONObject responseEntity, String rel) {
        if (responseEntity.containsKey(rel)) {
            return responseEntity.getString(rel);
        }
        throw new UnknownRelException(rel);
    }
}

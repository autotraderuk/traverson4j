package uk.co.autotrader.traverson.link;

import com.alibaba.fastjson.JSONObject;

public interface LinkDiscoverer {
    String findHref(JSONObject responseEntity, String rel);
}

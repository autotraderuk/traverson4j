package uk.co.autotrader.traverson.link;

import com.alibaba.fastjson2.JSONObject;

public interface LinkDiscoverer {
    String findHref(JSONObject responseEntity, String rel);
}

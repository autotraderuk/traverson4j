package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import uk.co.autotrader.traverson.link.LinkDiscoverer;

import static java.util.Collections.emptyList;

public class EmbeddedArrayNameHandler implements LinkDiscoverer {

    private final LinksRelHandler fromLinks;

    public EmbeddedArrayNameHandler() {
        this.fromLinks = new LinksRelHandler();
    }

    @Override
    public String findHref(JSONObject resource, String rel) {
        JSONObject embedded = resource.getJSONObject("_embedded");
        if (embedded != null) {
            for (Object value : embedded.values()) {
                JSONArray embeddedCollection = safeCastToJsonArray(value);
                for (int i = 0; i < embeddedCollection.size(); i++) {
                    JSONObject item = embeddedCollection.getJSONObject(i);
                    if (rel.equals(item.getString("name"))) {
                        return this.fromLinks.findHref(item, "self");
                    }
                }
            }
        }
        return null;
    }

    private JSONArray safeCastToJsonArray(Object object) {
        if (object instanceof JSONArray) {
            return (JSONArray) object;
        }

        return new JSONArray(emptyList());
    }
}

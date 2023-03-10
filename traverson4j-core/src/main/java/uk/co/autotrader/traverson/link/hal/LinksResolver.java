package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Collections.singletonMap;

class LinksResolver implements HalEntityResolver {

    @Override
    public JSONArray findJSONArrayRelation(JSONObject halResource, String relationType) {
        Object maybeJsonArray = getLinksSection(halResource).get(relationType);
        if (!(maybeJsonArray instanceof JSONArray)) {
            return new JSONArray();
        }

        return (JSONArray) maybeJsonArray;
    }

    @Override
    public String resolveLink(JSONObject jsonObject) {
        return jsonObject.getString("href");
    }

    @Override
    public Map<String, SortedSet<String>> describeRelations(JSONObject resource) {
        SortedSet<String> sortedRels = new TreeSet<>();
        sortedRels.addAll(getLinksSection(resource).keySet());
        return singletonMap("'_links'", sortedRels);
    }

    private JSONObject getLinksSection(JSONObject halResource) {
        return halResource.getJSONObject("_links");
    }
}

package uk.co.autotrader.traverson.link.hal.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import uk.co.autotrader.traverson.link.hal.HalEntityResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class EmbeddedResolver implements HalEntityResolver {
    @Override
    public JSONArray findJSONArrayRelation(JSONObject halResource, String relationType) {
        JSONObject embeddedResources = getEmbeddedSection(halResource);
        if (embeddedResources == null) {
            return new JSONArray();
        }

        Object maybeJsonArray = embeddedResources.get(relationType);
        if (maybeJsonArray == null || !(maybeJsonArray instanceof JSONArray)) {
            return new JSONArray();
        }

        return (JSONArray) maybeJsonArray;
    }

    @Override
    public String resolveLink(JSONObject jsonObject) {
        return jsonObject.getJSONObject("_links").getJSONObject("self").getString("href");
    }

    @Override
    public Map<String, SortedSet<String>> describeRelations(JSONObject resource) {
        JSONObject embeddedResources = getEmbeddedSection(resource);

        Map<String, SortedSet<String>> relations = new HashMap<String, SortedSet<String>>();
        if (embeddedResources != null) {
            SortedSet<String> sortedRels = new TreeSet<String>();
            sortedRels.addAll(embeddedResources.keySet());
            relations.put("'_embedded'", sortedRels);
        }
        return relations;
    }

    private JSONObject getEmbeddedSection(JSONObject halResource) {
        return halResource.getJSONObject("_embedded");
    }
}

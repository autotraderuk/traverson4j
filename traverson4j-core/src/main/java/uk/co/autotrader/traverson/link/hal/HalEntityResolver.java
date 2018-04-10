package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.SortedSet;

public interface HalEntityResolver {

    JSONArray findJSONArrayRelation(JSONObject halResource, String relationType);

    String resolveLink(JSONObject jsonObject);

    Map<String, SortedSet<String>> describeRelations(JSONObject resource);
}

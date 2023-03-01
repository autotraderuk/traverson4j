package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.Map;
import java.util.SortedSet;

interface HalEntityResolver {

    JSONArray findJSONArrayRelation(JSONObject halResource, String relationType);

    String resolveLink(JSONObject jsonObject);

    Map<String, SortedSet<String>> describeRelations(JSONObject resource);
}

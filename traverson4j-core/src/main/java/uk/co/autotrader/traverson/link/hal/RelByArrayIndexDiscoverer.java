package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import uk.co.autotrader.traverson.exception.UnknownRelException;
import uk.co.autotrader.traverson.link.LinkDiscoverer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class RelByArrayIndexDiscoverer implements LinkDiscoverer {
    private static final Pattern REL_AND_ARRAY_INDEX = Pattern.compile("(.*)\\[(\\d+)\\]");

    private final List<HalEntityResolver> halEntityResolvers;

    public RelByArrayIndexDiscoverer(HalEntityResolver... halEntityResolvers) {
        this.halEntityResolvers = asList(halEntityResolvers);
    }

    @Override
    public String findHref(JSONObject responseEntity, String rel) {
        Matcher matcher = REL_AND_ARRAY_INDEX.matcher(rel);

        if (matcher.matches()) {
            String relName = matcher.group(1);
            int arrayIndex = Integer.parseInt(matcher.group(2));

            return this.findLink(responseEntity, relName, arrayIndex);
        }

        return null;
    }

    private String findLink(JSONObject resource, String relName, int arrayIndex) {
        for (HalEntityResolver resolver : this.halEntityResolvers) {
            JSONArray entities = resolver.findJSONArrayRelation(resource, relName);

            if (arrayIndex < entities.size()) {
                JSONObject foundEntity = entities.getJSONObject(arrayIndex);
                return resolver.resolveLink(foundEntity);
            }
        }

        throw createUnknownRelException(resource, relName, arrayIndex);
    }

    private UnknownRelException createUnknownRelException(JSONObject resource, String relName, int arrayIndex) {
        Map<String, SortedSet<String>> rels = new HashMap<String, SortedSet<String>>();
        for (HalEntityResolver resolver : this.halEntityResolvers) {
            rels.putAll(resolver.describeRelations(resource));
        }

        return new UnknownRelException(format("'%s' with an item at index '%d'", relName, arrayIndex), rels);
    }
}

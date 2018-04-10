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

public class RelByArrayPropertyDiscoverer implements LinkDiscoverer {
    private static final Pattern RELATION_WITH_PROPERTY_AND_VALUE = Pattern.compile("(.*)\\[(.*):(.*)\\]");

    private final List<HalEntityResolver> halEntityResolvers;

    public RelByArrayPropertyDiscoverer(HalEntityResolver... halEntityResolvers) {
        this.halEntityResolvers = asList(halEntityResolvers);
    }

    @Override
    public String findHref(JSONObject responseEntity, String rel) {
        Matcher matcher = RELATION_WITH_PROPERTY_AND_VALUE.matcher(rel);

        if (matcher.matches()) {
            String relName = matcher.group(1);
            String propertyName = matcher.group(2);
            String propertyValue = matcher.group(3);

            return this.findLink(responseEntity, relName, propertyName, propertyValue);
        }

        return null;
    }

    private String findLink(JSONObject resource, String relName, String propertyName, String propertyValue) {
        for (HalEntityResolver resolver : this.halEntityResolvers) {
            JSONArray entities = resolver.findJSONArrayRelation(resource, relName);

            for (int i = 0; i < entities.size(); i++) {
                JSONObject potentialLink = entities.getJSONObject(i);

                if (propertyValue.equals(potentialLink.getString(propertyName))) {
                    return resolver.resolveLink(potentialLink);
                }
            }
        }

        throw createUnknownRelException(resource, relName, propertyName, propertyValue);
    }

    private UnknownRelException createUnknownRelException(JSONObject resource, String relName, String propertyName, String propertyValue) {
        Map<String, SortedSet<String>> rels = new HashMap<String, SortedSet<String>>();
        for (HalEntityResolver resolver : this.halEntityResolvers) {
            rels.putAll(resolver.describeRelations(resource));
        }

        return new UnknownRelException(format("'%s' with an item with property '%s: %s'", relName, propertyName, propertyValue), rels);
    }

}

package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson.JSONObject;
import uk.co.autotrader.traverson.exception.UnknownRelException;
import uk.co.autotrader.traverson.link.LinkDiscoverer;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

class LinksRelHandler implements LinkDiscoverer {

    @Override
    public String findHref(JSONObject resource, String rel) {
        JSONObject links = resource.getJSONObject("_links");

        if (links.containsKey(rel)) {
            return links.getJSONObject(rel).getString("href");
        } else {
            throw new UnknownRelException(rel, sort(links.keySet()));
        }
    }

    private SortedSet<String> sort(Set<String> set) {
        SortedSet<String> sorted = new TreeSet<String>();
        sorted.addAll(set);
        return sorted;
    }
}

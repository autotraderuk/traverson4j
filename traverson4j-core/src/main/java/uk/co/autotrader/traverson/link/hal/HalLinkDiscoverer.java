package uk.co.autotrader.traverson.link.hal;

import com.alibaba.fastjson2.JSONObject;
import uk.co.autotrader.traverson.link.LinkDiscoverer;

import java.util.Arrays;
import java.util.List;

public class HalLinkDiscoverer implements LinkDiscoverer {

    private final List<? extends LinkDiscoverer> linkHandlers;

    public HalLinkDiscoverer() {
        this.linkHandlers = Arrays.asList(
                new EmbeddedArrayNameHandler(),
                new RelByArrayPropertyDiscoverer(
                        new LinksResolver(),
                        new EmbeddedResolver()),
                new RelByArrayIndexDiscoverer(
                        new EmbeddedResolver(),
                        new LinksResolver()),
                new LinksRelHandler());
    }

    @Override
    public String findHref(JSONObject responseEntity, String rel) {
        for (int i = 0; i < linkHandlers.size() - 1; i++) {
            LinkDiscoverer linkHandler = linkHandlers.get(i);
            String url = linkHandler.findHref(responseEntity, rel);

            if (url != null) {
                return url;
            }
        }
        LinkDiscoverer lastLinkHandler = linkHandlers.get(linkHandlers.size() - 1);
        return lastLinkHandler.findHref(responseEntity, rel);
    }
}

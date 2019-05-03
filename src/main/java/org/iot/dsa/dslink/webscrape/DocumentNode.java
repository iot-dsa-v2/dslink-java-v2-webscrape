package org.iot.dsa.dslink.webscrape;

import java.io.IOException;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSMap.Entry;
import org.iot.dsa.node.DSString;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DocumentNode extends ElementNode {
    
    private String url;
    private Document document;
    
    public DocumentNode(String url) {
        this.url = url;
    }
    
    public DocumentNode() {
        
    }
    
    @Override
    protected void onStarted() {
        super.onStarted();
        DSIObject urlobj = get("URL");
        if (urlobj instanceof DSString) {
            this.url = ((DSString) urlobj).toString();
        }
    }

    @Override
    protected void init() {
        if (url != null) {
            put("URL", url);
            try {
                Connection conn = Jsoup.connect(url);
                for (Entry entry: MainNode.getCookies()) {
                    conn.cookie(entry.getKey(), entry.getValue().toString());
                }
                document = conn.get();
            } catch (IOException e) {
                warn("", e);
            }
        }
    }

    @Override
    public Element getElement() {
        return document;
    }

}

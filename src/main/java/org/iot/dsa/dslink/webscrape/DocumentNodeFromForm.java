package org.iot.dsa.dslink.webscrape;

import java.io.IOException;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSMap.Entry;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;

public class DocumentNodeFromForm extends DocumentNode {
    
    private DSMap inputs;
    
    public DocumentNodeFromForm(DocumentFetcher parent, String url, DSMap inputs) {
        super(parent, url);
        this.inputs = inputs;
    }
    
    public DocumentNodeFromForm() {
        super();
    }
    
    @Override
    protected void onStarted() {
        super.onStarted();
        DSIObject inputsobj = get("inputs");
        if (inputsobj instanceof DSMap) {
            inputs = (DSMap) inputsobj;
        }
    }

    @Override
    protected void init() {
        if (url != null) {
            put("URL", url);
            put("inputs", inputs.copy());
            try {
                Connection conn = Jsoup.connect(url).cookies(parent.getCookies());
                for (Entry entry: inputs) {
                    conn.data(entry.getKey(), entry.getValue().toString());
                }
                response = conn.method(Method.POST).execute();
                document = response.parse();
            } catch (IOException e) {
                warn("", e);
            }
        }
    }

}

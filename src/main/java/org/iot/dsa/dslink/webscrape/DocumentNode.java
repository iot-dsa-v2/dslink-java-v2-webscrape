package org.iot.dsa.dslink.webscrape;

import java.io.IOException;
import java.util.Map;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DocumentNode extends ElementNode implements DocumentFetcher {
    
    protected String url;
    protected Response response;
    protected Document document;
    protected DocumentFetcher parent;
    
    public DocumentNode(DocumentFetcher parent, String url) {
        this.url = url;
        this.parent = parent;
    }
    
    public DocumentNode() {
        
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Fetch Document", DocumentFetcher.makeFetchDocAction());
    }
    
    @Override
    protected void onStarted() {
        super.onStarted();
        DSIObject urlobj = get("URL");
        if (urlobj instanceof DSString) {
            this.url = ((DSString) urlobj).toString();
        }
        parent = (DocumentFetcher) getAncestor(DocumentFetcher.class);
    }

    @Override
    protected void init() {
        if (url != null) {
            put("URL", url);
            try {
                Connection conn = Jsoup.connect(url).cookies(parent.getCookies());
                response = conn.method(Method.GET).execute();
                document = response.parse();
//                String tmpFileName = "temp/" + System.currentTimeMillis() + ".html";
//                File tmpFile = new File(tmpFileName);
//                BufferedWriter w = new BufferedWriter(new FileWriter(tmpFile));
//                w.write(documentPreJS.toString());
//                w.close();
//                System.setProperty("phantomjs.binary.path", "libs/phantomjs.exe");
//                PhantomJSDriver ghostDriver = new PhantomJSDriver();
//                try {
//                    ghostDriver.get(tmpFileName);
//                    document = Jsoup.parse(ghostDriver.getPageSource());
//                } finally {
//                    ghostDriver.quit();
//                }                
            } catch (IOException e) {
                warn("", e);
            }
        }
    }

    @Override
    public Element getElement() {
        return document;
    }

    @Override
    public void fetchDocument(DSMap parameters) {
        String name = parameters.getString("Name");
        String url = parameters.getString("URL");
        put(name, new DocumentNode(this, url));
    }

    @Override
    public Map<String, String> getCookies() {
        return response.cookies();
    }

}

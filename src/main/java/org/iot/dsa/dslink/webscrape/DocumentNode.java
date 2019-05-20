package org.iot.dsa.dslink.webscrape;

import java.io.IOException;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class DocumentNode extends ElementNode implements DocumentFetcher {
    
    protected String url;
    protected HtmlPage page;
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
    protected void onStable() {
        super.onStable();
        init();
    }

    
    protected void init() {
        if (url != null) {
            put("URL", url);
            try {
                page = getWebClient().getPage(url);
            } catch (FailingHttpStatusCodeException | IOException e) {
                warn("", e);
            }
        }
    }


    @Override
    public void fetchDocument(DSMap parameters) {
        String name = parameters.getString("Name");
        String url = parameters.getString("URL");
        put(name, new DocumentNode(this, url));
    }

    @Override
    public WebClient getWebClient() {
        return parent.getWebClient();
    }

    @Override
    public HtmlElement getElement() {
        if (page != null) {
            return page.getDocumentElement();
        } else {
            return null;
        }
    }
}
